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
 * Draws the animated runic matrix (Blockbench model) inside the Automated Infusion Matrix structure.
 */
@SideOnly(Side.CLIENT)
public final class GTN_AutomatedInfusionMatrixRenderer {

    private static final ResourceLocation MODEL_LOCATION = new ResourceLocation(
        GregTechNightmare.RESOURCE_ROOT_ID,
        "models/auto_runic_matrix.bbmodel");
    private static final String ANIMATION_NAME = "animation";

    /** Where the bottom-center of the model sits, relative to the controller: blocks up / blocks back. */
    public static final int MODEL_OFFSET_UP = 1;
    public static final int MODEL_OFFSET_BACK = 1;

    /** Minimal block light of the model while idle / at full speed (0..240 lightmap units). */
    private static final int IDLE_GLOW = 96;
    private static final int ACTIVE_GLOW = 240;

    private static GTN_BBModel model;
    private static GTN_BBAnimation animation;
    private static final FloatBuffer BASIS = BufferUtils.createFloatBuffer(16);

    private GTN_AutomatedInfusionMatrixRenderer() {}

    public static float getAnimationLength() {
        ensureLoaded();
        return animation == null ? 0 : animation.length;
    }

    /**
     * @param x,          y, z TESR offset of the controller block
     * @param controllerX world coordinates of the controller, used to sample light inside the structure
     * @param time        animation time in seconds
     * @param glow        0..1, how bright the model glows
     */
    public static void render(double x, double y, double z, ExtendedFacing facing, World world, int controllerX,
        int controllerY, int controllerZ, float time, float glow) {
        ensureLoaded();
        if (model == null || facing == null) return;

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
            x + 0.5 + up.offsetX * (MODEL_OFFSET_UP - 0.5) + back.offsetX * MODEL_OFFSET_BACK,
            y + 0.5 + up.offsetY * (MODEL_OFFSET_UP - 0.5) + back.offsetY * MODEL_OFFSET_BACK,
            z + 0.5 + up.offsetZ * (MODEL_OFFSET_UP - 0.5) + back.offsetZ * MODEL_OFFSET_BACK);

        BASIS.clear();
        BASIS.put(
            new float[] { rightX, rightY, rightZ, 0, up.offsetX, up.offsetY, up.offsetZ, 0, back.offsetX, back.offsetY,
                back.offsetZ, 0, 0, 0, 0, 1 });
        BASIS.flip();
        GL11.glMultMatrix(BASIS);

        GL11.glScalef(1 / 16F, 1 / 16F, 1 / 16F);
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
            controllerX + up.offsetX * MODEL_OFFSET_UP + back.offsetX * MODEL_OFFSET_BACK,
            controllerY + up.offsetY * MODEL_OFFSET_UP + back.offsetY * MODEL_OFFSET_BACK,
            controllerZ + up.offsetZ * MODEL_OFFSET_UP + back.offsetZ * MODEL_OFFSET_BACK,
            glow);

        model.render(animation, time);

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

    private static void ensureLoaded() {
        if (model != null) return;
        model = GTN_BBModel.load(MODEL_LOCATION);
        animation = model.getAnimation(ANIMATION_NAME);
    }
}
