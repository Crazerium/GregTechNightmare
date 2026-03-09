package com.EvgenWarGold.GregTechNightmare.Utils;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

public class BlockHighlighter {

    private static final HashMap<GTN_MultiBlockBase<?>, BlockHighlighter> highlighters = new HashMap<>();

    private int x, y, z;
    private int dim;
    private boolean showHighlight = true;
    private boolean isActive = false;

    public BlockHighlighter() {}

    public void updatePosition(int x, int y, int z, int dimension, boolean show) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dimension;
        this.showHighlight = show;
        this.isActive = true;
    }

    public void disable() {
        this.isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    @SideOnly(Side.CLIENT)
    private void renderBlockHighlight(RenderWorldLastEvent event) {
        if (!isActive || !showHighlight) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        int currentDim = mc.theWorld.provider.dimensionId;

        if (currentDim != dim) {
            return;
        }

        EntityPlayerSP player = mc.thePlayer;
        double doubleX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
        double doubleY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks;
        double doubleZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glLineWidth(3);
        GL11.glTranslated(-doubleX, -doubleY, -doubleZ);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        renderHighlightedBlock(x, y, z);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    @SideOnly(Side.CLIENT)
    private void renderHighlightedBlock(int x, int y, int z) {
        Tessellator tess = Tessellator.instance;

        GL11.glColor4f(1.0f, 0.2f, 0.2f, 1.0f);
        renderBlockOutline(x, y, z);

        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(-1.0f, -1.0f);

        GL11.glColor4f(1.0f, 0.2f, 0.2f, 0.25f);
        renderBlockFaces(x, y, z);

        GL11.glPolygonOffset(0.0f, 0.0f);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glDepthMask(true);
    }

    @SideOnly(Side.CLIENT)
    private void renderBlockOutline(int x, int y, int z) {
        Tessellator tess = Tessellator.instance;
        tess.startDrawing(GL11.GL_LINE_STRIP);

        tess.addVertex(x, y, z);
        tess.addVertex(x + 1, y, z);
        tess.addVertex(x + 1, y, z + 1);
        tess.addVertex(x, y, z + 1);
        tess.addVertex(x, y, z);

        tess.addVertex(x, y + 1, z);
        tess.addVertex(x + 1, y + 1, z);
        tess.addVertex(x + 1, y + 1, z + 1);
        tess.addVertex(x, y + 1, z + 1);
        tess.addVertex(x, y + 1, z);

        tess.addVertex(x, y, z);
        tess.addVertex(x, y + 1, z);

        tess.addVertex(x + 1, y, z);
        tess.addVertex(x + 1, y + 1, z);

        tess.addVertex(x + 1, y, z + 1);
        tess.addVertex(x + 1, y + 1, z + 1);

        tess.addVertex(x, y, z + 1);
        tess.addVertex(x, y + 1, z + 1);

        tess.draw();
    }

    @SideOnly(Side.CLIENT)
    private void renderBlockFaces(int x, int y, int z) {
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();

        tess.addVertex(x, y, z);
        tess.addVertex(x + 1, y, z);
        tess.addVertex(x + 1, y, z + 1);
        tess.addVertex(x, y, z + 1);

        tess.addVertex(x, y + 1, z);
        tess.addVertex(x, y + 1, z + 1);
        tess.addVertex(x + 1, y + 1, z + 1);
        tess.addVertex(x + 1, y + 1, z);

        tess.addVertex(x, y, z);
        tess.addVertex(x, y + 1, z);
        tess.addVertex(x + 1, y + 1, z);
        tess.addVertex(x + 1, y, z);

        tess.addVertex(x, y, z + 1);
        tess.addVertex(x + 1, y, z + 1);
        tess.addVertex(x + 1, y + 1, z + 1);
        tess.addVertex(x, y + 1, z + 1);

        tess.addVertex(x, y, z);
        tess.addVertex(x, y, z + 1);
        tess.addVertex(x, y + 1, z + 1);
        tess.addVertex(x, y + 1, z);

        tess.addVertex(x + 1, y, z);
        tess.addVertex(x + 1, y + 1, z);
        tess.addVertex(x + 1, y + 1, z + 1);
        tess.addVertex(x + 1, y, z + 1);

        tess.draw();
    }

    public static class EventHandler {

        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public void onRenderWorldLast(RenderWorldLastEvent event) {
            for (BlockHighlighter highlighter : highlighters.values()) {
                highlighter.renderBlockHighlight(event);
            }
        }
    }

    public static void registerHighlighter(GTN_MultiBlockBase<?> machine, BlockHighlighter highlighter) {
        highlighters.put(machine, highlighter);
    }

    public static void removeHighlighter(GTN_MultiBlockBase<?> machine) {
        highlighters.remove(machine);
    }
}
