package com.EvgenWarGold.GregTechNightmare.GregTech.Render.BBModel;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.EvgenWarGold.GregTechNightmare.GTN_Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Minimal runtime loader for Blockbench projects (.bbmodel, "GeckoLib"/"Generic" formats with per-face UV).
 * <p>
 * Coordinates are Blockbench pixels (16 = one block) exactly as they appear in the editor, textures are read from the
 * images embedded in the project, so an artist can simply re-save the project into resources.
 * Rendering is done in the current GL matrix: the caller is responsible for placing the model in the world.
 */
@SideOnly(Side.CLIENT)
public final class GTN_BBModel {

    private static final String[] FACES = { "north", "south", "east", "west", "up", "down" };
    /** Floats per quad: normal (3) + 4 vertices * (x, y, z, u, v). */
    private static final int QUAD_STRIDE = 3 + 4 * 5;

    private static final class Texture {

        final String name;
        final String source;
        final float uvWidth;
        final float uvHeight;
        ResourceLocation location;
        BufferedImage image;
        boolean decoded;

        Texture(String name, String source, float uvWidth, float uvHeight) {
            this.name = name;
            this.source = source;
            this.uvWidth = uvWidth;
            this.uvHeight = uvHeight;
        }
    }

    private static final class Bone {

        final String uuid;
        final String name;
        final float[] pivot;
        final float[] rotation;
        final List<Bone> children = new ArrayList<>();
        /** texture index -> opaque quads */
        final Map<Integer, float[]> quads = new HashMap<>();
        /** texture index -> quads with semi-transparent pixels, drawn after the opaque ones */
        final Map<Integer, float[]> translucentQuads = new HashMap<>();

        Bone(String uuid, String name, float[] pivot, float[] rotation) {
            this.uuid = uuid;
            this.name = name;
            this.pivot = pivot;
            this.rotation = rotation;
        }
    }

    private final ResourceLocation location;
    private final List<Texture> textures = new ArrayList<>();
    private final Bone root = new Bone("", "root", new float[3], new float[3]);
    private final Map<String, GTN_BBAnimation> animations = new HashMap<>();
    private boolean hasTranslucentQuads;

    private final float[] tmpPosition = new float[3];
    private final float[] tmpRotation = new float[3];
    private final float[] tmpScale = new float[3];

    private GTN_BBModel(ResourceLocation location) {
        this.location = location;
    }

    public static GTN_BBModel load(ResourceLocation location) {
        GTN_BBModel model = new GTN_BBModel(location);
        try (InputStream stream = Minecraft.getMinecraft()
            .getResourceManager()
            .getResource(location)
            .getInputStream()) {
            JsonObject json = new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8))
                .getAsJsonObject();
            model.parse(json);
        } catch (Exception e) {
            GTN_Logger.error(e, "Failed to load Blockbench model " + location);
        }
        return model;
    }

    public GTN_BBAnimation getAnimation(String name) {
        return animations.get(name);
    }

    /** Renders the model. {@code animation} may be null for the rest pose. */
    public void render(GTN_BBAnimation animation, float time) {
        renderBone(root, animation, time, false);
        if (!hasTranslucentQuads) return;
        // semi-transparent faces (lenses, glass) must not hide what is drawn after them
        GL11.glDepthMask(false);
        renderBone(root, animation, time, true);
        GL11.glDepthMask(true);
    }

    // region Parsing
    private void parse(JsonObject json) {
        float projectWidth = 16;
        float projectHeight = 16;
        if (json.has("resolution")) {
            JsonObject resolution = json.getAsJsonObject("resolution");
            projectWidth = resolution.get("width")
                .getAsFloat();
            projectHeight = resolution.get("height")
                .getAsFloat();
        }

        Map<String, Integer> textureIds = new HashMap<>();
        if (json.has("textures")) {
            JsonArray array = json.getAsJsonArray("textures");
            for (int i = 0; i < array.size(); i++) {
                JsonObject texture = array.get(i)
                    .getAsJsonObject();
                textures.add(
                    new Texture(
                        getString(texture, "name", "texture" + i),
                        getString(texture, "source", ""),
                        texture.has("uv_width") ? texture.get("uv_width")
                            .getAsFloat() : projectWidth,
                        texture.has("uv_height") ? texture.get("uv_height")
                            .getAsFloat() : projectHeight));
                if (texture.has("id")) textureIds.put(
                    texture.get("id")
                        .getAsString(),
                    i);
            }
        }

        Map<String, JsonObject> elements = new HashMap<>();
        for (JsonElement element : json.getAsJsonArray("elements")) {
            JsonObject cube = element.getAsJsonObject();
            elements.put(
                cube.get("uuid")
                    .getAsString(),
                cube);
        }

        Map<String, JsonObject> groups = new HashMap<>();
        if (json.has("groups")) {
            for (JsonElement element : json.getAsJsonArray("groups")) {
                JsonObject group = element.getAsJsonObject();
                groups.put(
                    group.get("uuid")
                        .getAsString(),
                    group);
            }
        }

        parseOutliner(root, json.getAsJsonArray("outliner"), elements, groups, textureIds);

        if (json.has("animations")) {
            for (JsonElement element : json.getAsJsonArray("animations")) {
                GTN_BBAnimation animation = GTN_BBAnimation.parse(element.getAsJsonObject());
                animations.put(animation.name, animation);
            }
        }
    }

    private void parseOutliner(Bone parent, JsonArray children, Map<String, JsonObject> elements,
        Map<String, JsonObject> groups, Map<String, Integer> textureIds) {
        for (JsonElement child : children) {
            if (child.isJsonPrimitive()) {
                JsonObject cube = elements.get(child.getAsString());
                if (cube != null) addCube(parent, cube, textureIds);
                continue;
            }

            JsonObject node = child.getAsJsonObject();
            // Project format 5+ keeps group data in "groups", older versions inline it into the outliner node
            JsonObject group = groups.getOrDefault(
                node.get("uuid")
                    .getAsString(),
                node);
            if (!getBoolean(group, "visibility", true) || !getBoolean(group, "export", true)) continue;

            Bone bone = new Bone(
                node.get("uuid")
                    .getAsString(),
                getString(group, "name", ""),
                getVector(group, "origin", 0),
                getVector(group, "rotation", 0));
            parent.children.add(bone);
            if (node.has("children"))
                parseOutliner(bone, node.getAsJsonArray("children"), elements, groups, textureIds);
        }
    }

    private void addCube(Bone bone, JsonObject cube, Map<String, Integer> textureIds) {
        if (!getBoolean(cube, "visibility", true) || !getBoolean(cube, "export", true)) return;
        String type = getString(cube, "type", "cube");
        if (!"cube".equals(type)) return; // meshes, locators, etc. are not supported

        float inflate = cube.has("inflate") ? cube.get("inflate")
            .getAsFloat() : 0;
        float[] from = getVector(cube, "from", 0);
        float[] to = getVector(cube, "to", 0);
        for (int i = 0; i < 3; i++) {
            from[i] -= inflate;
            to[i] += inflate;
        }
        float[] origin = getVector(cube, "origin", 0);
        float[] rotation = getVector(cube, "rotation", 0);

        JsonObject faces = cube.getAsJsonObject("faces");
        if (faces == null) return;
        for (String direction : FACES) {
            if (!faces.has(direction)) continue;
            JsonObject face = faces.getAsJsonObject(direction);
            if (!face.has("texture") || face.get("texture")
                .isJsonNull()) continue;

            String textureKey = face.get("texture")
                .getAsString();
            Integer textureIndex = textureIds.get(textureKey);
            if (textureIndex == null) {
                try {
                    textureIndex = Integer.parseInt(textureKey);
                } catch (NumberFormatException e) {
                    continue;
                }
            }
            if (textureIndex < 0 || textureIndex >= textures.size()) continue;

            float[] uv = getVector(face, "uv", 0, 4);
            if (uv[0] == uv[2] || uv[1] == uv[3]) continue; // degenerate face
            int uvRotation = face.has("rotation") ? face.get("rotation")
                .getAsInt() : 0;

            Texture texture = textures.get(textureIndex);
            float[] quad = buildQuad(direction, from, to, origin, rotation, uv, uvRotation, texture);
            boolean translucent = hasSemiTransparentPixels(texture, uv);
            hasTranslucentQuads |= translucent;
            Map<Integer, float[]> target = translucent ? bone.translucentQuads : bone.quads;
            float[] old = target.get(textureIndex);
            if (old == null) {
                target.put(textureIndex, quad);
            } else {
                float[] merged = new float[old.length + quad.length];
                System.arraycopy(old, 0, merged, 0, old.length);
                System.arraycopy(quad, 0, merged, old.length, quad.length);
                target.put(textureIndex, merged);
            }
        }
    }

    /**
     * Corners are listed top-left, bottom-left, bottom-right, top-right as seen from outside the face
     * (same convention as Java block models / Blockbench).
     */
    private static float[] buildQuad(String direction, float[] f, float[] t, float[] origin, float[] rotation,
        float[] uv, int uvRotation, Texture texture) {
        float[][] corners;
        float[] normal;
        switch (direction) {
            case "north":
                corners = new float[][] { { t[0], t[1], f[2] }, { t[0], f[1], f[2] }, { f[0], f[1], f[2] },
                    { f[0], t[1], f[2] } };
                normal = new float[] { 0, 0, -1 };
                break;
            case "south":
                corners = new float[][] { { f[0], t[1], t[2] }, { f[0], f[1], t[2] }, { t[0], f[1], t[2] },
                    { t[0], t[1], t[2] } };
                normal = new float[] { 0, 0, 1 };
                break;
            case "east":
                corners = new float[][] { { t[0], t[1], t[2] }, { t[0], f[1], t[2] }, { t[0], f[1], f[2] },
                    { t[0], t[1], f[2] } };
                normal = new float[] { 1, 0, 0 };
                break;
            case "west":
                corners = new float[][] { { f[0], t[1], f[2] }, { f[0], f[1], f[2] }, { f[0], f[1], t[2] },
                    { f[0], t[1], t[2] } };
                normal = new float[] { -1, 0, 0 };
                break;
            case "up":
                corners = new float[][] { { f[0], t[1], f[2] }, { f[0], t[1], t[2] }, { t[0], t[1], t[2] },
                    { t[0], t[1], f[2] } };
                normal = new float[] { 0, 1, 0 };
                break;
            default: // down
                corners = new float[][] { { f[0], f[1], t[2] }, { f[0], f[1], f[2] }, { t[0], f[1], f[2] },
                    { t[0], f[1], t[2] } };
                normal = new float[] { 0, -1, 0 };
                break;
        }

        for (float[] corner : corners) rotateAround(corner, origin, rotation);
        rotateAround(normal, null, rotation);

        float u1 = uv[0] / texture.uvWidth;
        float v1 = uv[1] / texture.uvHeight;
        float u2 = uv[2] / texture.uvWidth;
        float v2 = uv[3] / texture.uvHeight;
        float[][] uvs = { { u1, v1 }, { u1, v2 }, { u2, v2 }, { u2, v1 } };
        int shift = ((uvRotation / 90) % 4 + 4) % 4;

        float[] quad = new float[QUAD_STRIDE];
        quad[0] = normal[0];
        quad[1] = normal[1];
        quad[2] = normal[2];
        for (int i = 0; i < 4; i++) {
            int offset = 3 + i * 5;
            float[] cornerUv = uvs[(i + shift) % 4];
            quad[offset] = corners[i][0];
            quad[offset + 1] = corners[i][1];
            quad[offset + 2] = corners[i][2];
            quad[offset + 3] = cornerUv[0];
            quad[offset + 4] = cornerUv[1];
        }
        return quad;
    }

    /** Blockbench Euler order is ZYX: X is applied first, then Y, then Z. */
    private static void rotateAround(float[] point, float[] origin, float[] rotationDegrees) {
        if (rotationDegrees[0] == 0 && rotationDegrees[1] == 0 && rotationDegrees[2] == 0) return;
        float x = point[0], y = point[1], z = point[2];
        if (origin != null) {
            x -= origin[0];
            y -= origin[1];
            z -= origin[2];
        }

        double a = Math.toRadians(rotationDegrees[0]);
        float cos = (float) Math.cos(a), sin = (float) Math.sin(a);
        float ny = y * cos - z * sin;
        float nz = y * sin + z * cos;
        y = ny;
        z = nz;

        a = Math.toRadians(rotationDegrees[1]);
        cos = (float) Math.cos(a);
        sin = (float) Math.sin(a);
        float nx = x * cos + z * sin;
        nz = -x * sin + z * cos;
        x = nx;
        z = nz;

        a = Math.toRadians(rotationDegrees[2]);
        cos = (float) Math.cos(a);
        sin = (float) Math.sin(a);
        nx = x * cos - y * sin;
        ny = x * sin + y * cos;
        x = nx;
        y = ny;

        if (origin != null) {
            x += origin[0];
            y += origin[1];
            z += origin[2];
        }
        point[0] = x;
        point[1] = y;
        point[2] = z;
    }
    // endregion

    // region Rendering
    private void renderBone(Bone bone, GTN_BBAnimation animation, float time, boolean translucentPass) {
        float[] position = tmpPosition;
        float[] rotation = tmpRotation;
        float[] scale = tmpScale;
        position[0] = position[1] = position[2] = 0;
        rotation[0] = rotation[1] = rotation[2] = 0;
        scale[0] = scale[1] = scale[2] = 1;
        if (animation != null && !bone.uuid.isEmpty()) {
            animation.sample(bone.uuid, GTN_BBAnimation.Channel.POSITION, time, position);
            animation.sample(bone.uuid, GTN_BBAnimation.Channel.ROTATION, time, rotation);
            animation.sample(bone.uuid, GTN_BBAnimation.Channel.SCALE, time, scale);
        }

        float rx = bone.rotation[0] + rotation[0];
        float ry = bone.rotation[1] + rotation[1];
        float rz = bone.rotation[2] + rotation[2];
        boolean rotated = rx != 0 || ry != 0 || rz != 0;
        boolean scaled = scale[0] != 1 || scale[1] != 1 || scale[2] != 1;
        boolean moved = position[0] != 0 || position[1] != 0 || position[2] != 0;
        boolean transformed = rotated || scaled || moved;

        if (transformed) {
            GL11.glPushMatrix();
            GL11.glTranslatef(bone.pivot[0] + position[0], bone.pivot[1] + position[1], bone.pivot[2] + position[2]);
            if (rz != 0) GL11.glRotatef(rz, 0, 0, 1);
            if (ry != 0) GL11.glRotatef(ry, 0, 1, 0);
            if (rx != 0) GL11.glRotatef(rx, 1, 0, 0);
            if (scaled) GL11.glScalef(scale[0], scale[1], scale[2]);
            GL11.glTranslatef(-bone.pivot[0], -bone.pivot[1], -bone.pivot[2]);
        }

        for (Map.Entry<Integer, float[]> entry : (translucentPass ? bone.translucentQuads : bone.quads).entrySet()) {
            bindTexture(entry.getKey());
            drawQuads(entry.getValue());
        }
        for (Bone child : bone.children) renderBone(child, animation, time, translucentPass);

        if (transformed) GL11.glPopMatrix();
    }

    private static void drawQuads(float[] data) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        for (int quad = 0; quad < data.length; quad += QUAD_STRIDE) {
            tessellator.setNormal(data[quad], data[quad + 1], data[quad + 2]);
            for (int i = 0; i < 4; i++) {
                int offset = quad + 3 + i * 5;
                tessellator.addVertexWithUV(
                    data[offset],
                    data[offset + 1],
                    data[offset + 2],
                    data[offset + 3],
                    data[offset + 4]);
            }
        }
        tessellator.draw();
    }

    private void bindTexture(int index) {
        Texture texture = textures.get(index);
        if (texture.location == null) {
            texture.location = uploadTexture(texture);
        }
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(texture.location);
    }

    private ResourceLocation uploadTexture(Texture texture) {
        BufferedImage image = getImage(texture);
        if (image == null) return new ResourceLocation("textures/misc/unknown_pack.png");
        String name = "gtn_bbmodel/" + location.getResourcePath() + "/" + texture.name;
        return Minecraft.getMinecraft()
            .getTextureManager()
            .getDynamicTextureLocation(name, new DynamicTexture(image));
    }

    /** Decodes the image embedded into the project once; null if it cannot be read. */
    private BufferedImage getImage(Texture texture) {
        if (texture.decoded) return texture.image;
        texture.decoded = true;
        try {
            int comma = texture.source.indexOf(',');
            if (!texture.source.startsWith("data:") || comma < 0) {
                throw new IllegalStateException("texture is not embedded into the project");
            }
            byte[] bytes = Base64.getDecoder()
                .decode(texture.source.substring(comma + 1));
            texture.image = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            GTN_Logger.error(e, "Failed to load texture " + texture.name + " from " + location);
        }
        return texture.image;
    }

    /** True if the UV rectangle of a face contains pixels that are neither fully opaque nor fully transparent. */
    private boolean hasSemiTransparentPixels(Texture texture, float[] uv) {
        BufferedImage image = getImage(texture);
        if (image == null) return false;
        float scaleX = image.getWidth() / texture.uvWidth;
        float scaleY = image.getHeight() / texture.uvHeight;
        int x0 = Math.max(0, (int) Math.floor(Math.min(uv[0], uv[2]) * scaleX));
        int x1 = Math.min(image.getWidth(), (int) Math.ceil(Math.max(uv[0], uv[2]) * scaleX));
        int y0 = Math.max(0, (int) Math.floor(Math.min(uv[1], uv[3]) * scaleY));
        int y1 = Math.min(image.getHeight(), (int) Math.ceil(Math.max(uv[1], uv[3]) * scaleY));
        for (int y = y0; y < y1; y++) {
            for (int x = x0; x < x1; x++) {
                int alpha = image.getRGB(x, y) >>> 24;
                if (alpha > 0 && alpha < 255) return true;
            }
        }
        return false;
    }
    // endregion

    // region Json helpers
    private static String getString(JsonObject json, String key, String fallback) {
        return json.has(key) && !json.get(key)
            .isJsonNull() ? json.get(key)
                .getAsString() : fallback;
    }

    private static boolean getBoolean(JsonObject json, String key, boolean fallback) {
        return json.has(key) && json.get(key)
            .isJsonPrimitive() ? json.get(key)
                .getAsBoolean() : fallback;
    }

    private static float[] getVector(JsonObject json, String key, float fallback) {
        return getVector(json, key, fallback, 3);
    }

    private static float[] getVector(JsonObject json, String key, float fallback, int size) {
        float[] result = new float[size];
        java.util.Arrays.fill(result, fallback);
        if (!json.has(key) || !json.get(key)
            .isJsonArray()) return result;
        JsonArray array = json.getAsJsonArray(key);
        for (int i = 0; i < size && i < array.size(); i++) result[i] = array.get(i)
            .getAsFloat();
        return result;
    }
    // endregion
}
