package com.EvgenWarGold.GregTechNightmare.Event;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.EvgenWarGold.GregTechNightmare.GregTech.Items.ItemManaProspector;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.common.lib.LibObfuscation;

public class TooltipAdditionDisplayEvent {

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            render();
        }
    }

    public static void render() {
        Minecraft mc = Minecraft.getMinecraft();
        GuiScreen gui = mc.currentScreen;
        if (gui instanceof GuiContainer container && mc.thePlayer != null
            && mc.thePlayer.inventory.getItemStack() == null) {
            Slot slot = ReflectionHelper.getPrivateValue(GuiContainer.class, container, LibObfuscation.THE_SLOT);
            if (slot != null && slot.getHasStack()) {
                ItemStack stack = slot.getStack();
                if (stack != null) {
                    ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                    FontRenderer font = mc.fontRenderer;
                    int mouseX = Mouse.getX() * res.getScaledWidth() / mc.displayWidth;
                    int mouseY = res.getScaledHeight() - Mouse.getY() * res.getScaledHeight() / mc.displayHeight;

                    List<String> tooltip;
                    try {
                        tooltip = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
                    } catch (Exception e) {
                        tooltip = new ArrayList<>();
                    }
                    int width = 0;
                    for (String s : tooltip) width = Math.max(width, font.getStringWidth(s) + 2);
                    int tooltipHeight = (tooltip.size() - 1) * 10 + 5;

                    int height = 3;
                    int offx = 11;
                    int offy = 17;

                    boolean offscreen = mouseX + width + 19 >= res.getScaledWidth();

                    int fixY = res.getScaledHeight() - (mouseY + tooltipHeight);
                    if (fixY < 0) offy -= fixY;
                    if (offscreen) offx = -13 - width;

                    if (stack.getItem() instanceof ItemManaProspector) {
                        drawHUD(stack, mouseX, mouseY, offx, offy, width, height, font);
                    }

                }
            }
        }
    }

    private static void drawHUD(ItemStack stack, int mouseX, int mouseY, int offx, int offy, int width, int height,
        FontRenderer font) {
        int level = ItemManaProspector.getLevel(stack);
        int max = ItemManaProspector.LEVELS[Math.min(ItemManaProspector.LEVELS.length - 1, level + 1)];
        boolean ss = level >= ItemManaProspector.LEVELS.length - 1;
        int curr = ItemManaProspector.getMana_(stack);
        float percent = level == 0 ? 0F : (float) curr / (float) max;
        int rainbowWidth = Math.min(width - (ss ? 0 : 1), (int) (width * percent));
        float huePer = width == 0 ? 0F : 1F / width;
        float hueOff = (ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks) * 0.01F;

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        Gui.drawRect(
            mouseX + offx - 1,
            mouseY - offy - height - 1,
            mouseX + offx + width + 1,
            mouseY - offy,
            0xFF000000);
        for (int i = 0; i < rainbowWidth; i++) Gui.drawRect(
            mouseX + offx + i,
            mouseY - offy - height,
            mouseX + offx + i + 1,
            mouseY - offy,
            Color.HSBtoRGB(hueOff + huePer * i, 1F, 1F));
        Gui.drawRect(
            mouseX + offx + rainbowWidth,
            mouseY - offy - height,
            mouseX + offx + width,
            mouseY - offy,
            0xFF555555);

        String rank = StatCollector.translateToLocal("GTN.Tooltip.mana_hud.tier." + level)
            .replaceAll("&", "\u00a7");
        GL11.glPushAttrib(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LIGHTING);
        font.drawStringWithShadow(rank, mouseX + offx, mouseY - offy - 12, 0xFFFFFF);
        if (!ss) {
            rank = StatCollector.translateToLocal("GTN.Tooltip.mana_hud.tier." + (level + 1))
                .replaceAll("&", "\u00a7");
            font.drawStringWithShadow(
                rank,
                mouseX + offx + width - font.getStringWidth(rank),
                mouseY - offy - 12,
                0xFFFFFF);
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopAttrib();
    }
}
