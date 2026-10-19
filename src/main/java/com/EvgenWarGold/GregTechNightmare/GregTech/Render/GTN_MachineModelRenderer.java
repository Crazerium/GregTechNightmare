package com.EvgenWarGold.GregTechNightmare.GregTech.Render;

import java.nio.FloatBuffer;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.EvgenWarGold.GregTechNightmare.GregTech.Render.BBModel.GTN_BBAnimation;
import com.EvgenWarGold.GregTechNightmare.GregTech.Render.BBModel.GTN_BBModel;
import com.EvgenWarGold.GregTechNightmare.GregTechNightmare;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Draws the animated Blockbench model of a multiblock inside its structure, in place of its hidden casings.
 */
@SideOnly(Side.CLIENT)
public final class GTN_MachineModelRenderer {

    public static final GTN_MachineModelRenderer AUTOMATED_INFUSION_MATRIX = new GTN_MachineModelRenderer(
        "models/auto_runic_matrix.bbmodel",
        0,
        1,
        1,
        1);
    /** Twice the size of the Blockbench model: 6x4x2 blocks, centered between two columns and two rows. */
    public static final GTN_MachineModelRenderer INDUSTRIAL_CRUCIBLE = new GTN_MachineModelRenderer(
        "models/auto_alchemy_construct.bbmodel",
        -0.5,
        1,
        1.5,
        2);

    private static final String ANIMATION_NAME = "animation";

    /** Minimal block light of the model while idle / at full speed (0..240 lightmap units). */
    private static final int IDLE_GLOW = 96;
    private static final int ACTIVE_GLOW = 240;

    private static final FloatBuffer BASIS = BufferUtils.createFloatBuffer(16);

    private final ResourceLocation modelLocation;
    /** Where the bottom-center of the model sits, relative to the controller center: blocks right, up and back. */
    private final double offsetRight;
    private final double offsetUp;
    private final double offsetBack;
    /** Size of the model; the animation is played as much slower, so the big parts do not move faster. */
    private final float scale;
    private GTN_BBModel model;
    private GTN_BBAnimation animation;

    private GTN_MachineModelRenderer(String modelPath, double offsetRight, double offsetUp, double offsetBack,
        float scale) {
        this.modelLocation = new ResourceLocation(GregTechNightmare.RESOURCE_ROOT_ID, modelPath);
        this.offsetRight = offsetRight;
        this.offsetUp = offsetUp;
        this.offsetBack = offsetBack;
        this.scale = scale;
    }

    public float getAnimationLength() {
        ensureLoaded();
        return animation == null ? 0 : animation.length * scale;
    }

    /**
     * @param x,          y, z TESR offset of the controller block
     * @param controllerX world coordinates of the controller, used to sample light inside the structure
     * @param time        animation time in seconds, up to {@link #getAnimationLength()}
     * @param glow        0..1, how bright the model glows
     */
    public void render(double x, double y, double z, ExtendedFacing facing, World world, int controllerX,
        int controllerY, int controllerZ, float time, float glow) {
        ensureLoaded();
        if (model == null || facing == null) return;

        ForgeDirection right = facing.getRelativeRightInWorld();
        ForgeDirection up = facing.getRelativeUpInWorld();
        ForgeDirection back = facing.getRelativeBackInWorld();
        // right-handed basis: model +Y = structure up, model +Z = into the structure, so model north faces the
        // controller
        int rightX = up.offsetY * back.offsetZ - up.offsetZ * back.offsetY;
        int rightY = up.offsetZ * back.offsetX - up.offsetX * back.offsetZ;
        int rightZ = up.offsetX * back.offsetY - up.offsetY * back.offsetX;

        GL11.glPushMatrix();
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_CURRENT_BIT
                | GL11.GL_TRANSFORM_BIT
                | GL11.GL_TEXTURE_BIT
                | GL11.GL_LIGHTING_BIT);

        GL11.glTranslated(
            x + 0.5 + right.offsetX * offsetRight + up.offsetX * (offsetUp - 0.5) + back.offsetX * offsetBack,
            y + 0.5 + right.offsetY * offsetRight + up.offsetY * (offsetUp - 0.5) + back.offsetY * offsetBack,
            z + 0.5 + right.offsetZ * offsetRight + up.offsetZ * (offsetUp - 0.5) + back.offsetZ * offsetBack);

        BASIS.clear();
        BASIS.put(
            new float[] { rightX, rightY, rightZ, 0, up.offsetX, up.offsetY, up.offsetZ, 0, back.offsetX, back.offsetY,
                back.offsetZ, 0, 0, 0, 0, 1 });
        BASIS.flip();
        GL11.glMultMatrix(BASIS);

        GL11.glScalef(scale / 16F, scale / 16F, scale / 16F);
        GL11.glTranslatef(-8, 0, -8);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glColor4f(1, 1, 1, 1);
        applyLight(
            world,
            controllerX + (int) (up.offsetX * offsetUp + back.offsetX * offsetBack),
            controllerY + (int) (up.offsetY * offsetUp + back.offsetY * offsetBack),
            controllerZ + (int) (up.offsetZ * offsetUp + back.offsetZ * offsetBack),
            glow);

        model.render(animation, time / scale);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private static void applyLight(World world, int x, int y, int z, float glow) {
        int brightness = world == null ? 0 : world.getLightBrightnessForSkyBlocks(x, y, z, 0);
        int blockLight = brightness & 0xFFFF;
        int skyLight = brightness >>> 16;
        int minGlow = (int) (IDLE_GLOW + (ACTIVE_GLOW - IDLE_GLOW) * glow);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, Math.max(blockLight, minGlow), skyLight);
    }

    private void ensureLoaded() {
        if (model != null) return;
        model = GTN_BBModel.load(modelLocation);
        animation = model.getAnimation(ANIMATION_NAME);
    }
}
