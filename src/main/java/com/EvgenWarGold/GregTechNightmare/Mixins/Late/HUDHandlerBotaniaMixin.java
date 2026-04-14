package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_ManaHatch;

import gregtech.api.metatileentity.BaseMetaTileEntity;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.item.ModItems;

@Mixin(value = HUDHandler.class, remap = false)
public abstract class HUDHandlerBotaniaMixin {

    @Inject(
        method = "onDrawScreenPost",
        at = @At(
            value = "INVOKE",
            target = "Lvazkii/botania/client/core/handler/HUDHandler;renderWandModeDisplay(Lnet/minecraft/client/gui/ScaledResolution;)V"),
        locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectGregTechManaHatchHUD(RenderGameOverlayEvent.Post event, CallbackInfo ci, Minecraft mc,
        Profiler profiler, ItemStack equippedStack, MovingObjectPosition pos, Block block, TileEntity tile) {
        if (pos != null && equippedStack != null && equippedStack.getItem() == ModItems.twigWand) {
            if (tile instanceof BaseMetaTileEntity baseTE) {
                if (baseTE.getMetaTileEntity() instanceof GTN_ManaHatch hatch) {
                    hatch.renderHUD(mc, event.resolution, mc.theWorld, pos.blockX, pos.blockY, pos.blockZ);
                }
            }
        }
    }
}
