package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.EvgenWarGold.GregTechNightmare.GregTech.Wireless.GTN_IWirelessMEEndpoint;
import com.EvgenWarGold.GregTechNightmare.GregTech.Wireless.GTN_WirelessAENetworkProxy;
import com.EvgenWarGold.GregTechNightmare.GregTech.Wireless.GTN_WirelessMEDataStick;
import com.EvgenWarGold.GregTechNightmare.GregTech.Wireless.GTN_WirelessMELink;

import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;

/**
 * Adds optional wireless ME access to GT5U Crafting Input Bus/Buffer.
 *
 * <p>The Wildcard Pattern Buffer inherits {@link MTEHatchCraftingInputME}, so it receives the same wireless backend
 * without duplicating any of its wildcard-specific GUI/pattern logic.</p>
 */
@Mixin(targets = "gregtech.common.tileentities.machines.MTEHatchCraftingInputME", remap = false)
public abstract class MTEHatchCraftingInputMEWirelessMixin implements GTN_IWirelessMEEndpoint {

    @Unique
    private final GTN_WirelessMELink gtn$wirelessLink = new GTN_WirelessMELink();
    @Unique
    private GTN_WirelessAENetworkProxy gtn$wirelessProxy;
    @Unique
    private AENetworkProxy gtn$nativeProxy;
    @Unique
    private boolean gtn$forceNativeProxy;

    @Override
    public GTN_WirelessMELink gtn$getWirelessLink() {
        return gtn$wirelessLink;
    }

    @Override
    public AENetworkProxy gtn$getWirelessProxy() {
        if (gtn$wirelessProxy == null) {
            MTEHatchCraftingInputME self = (MTEHatchCraftingInputME) (Object) this;
            gtn$wirelessProxy = new GTN_WirelessAENetworkProxy(
                (IGridProxyable) (Object) this,
                "gtnWirelessProxy",
                self.getStackForm(1),
                gtn$wirelessLink);
            gtn$wirelessProxy.configureCableSides(
                self.connectsToAllSides(),
                self.getBaseMetaTileEntity().getFrontFacing());
        }
        return gtn$wirelessProxy;
    }

    @Inject(method = "getProxy", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtn$useWirelessProxy(CallbackInfoReturnable<AENetworkProxy> callback) {
        if (!gtn$forceNativeProxy && gtn$wirelessLink.isLinked()) callback.setReturnValue(gtn$getWirelessProxy());
    }

    @Inject(method = "loadNBTData", at = @At("TAIL"), remap = false, require = 0)
    private void gtn$loadWirelessLinkAfterNativeProxy(NBTTagCompound tag, CallbackInfo callback) {
        MTEHatchCraftingInputME self = (MTEHatchCraftingInputME) (Object) this;
        gtn$nativeProxy = gtn$getNativeProxy(self);
        gtn$wirelessLink.load(tag);
        gtn$disconnectNativeProxyFromCable();
    }

    @Inject(method = "saveNBTData", at = @At("HEAD"), remap = false, require = 0)
    private void gtn$useNativeProxyWhileSaving(NBTTagCompound tag, CallbackInfo callback) {
        gtn$forceNativeProxy = true;
    }

    @Inject(method = "saveNBTData", at = @At("TAIL"), remap = false, require = 0)
    private void gtn$saveWirelessLink(NBTTagCompound tag, CallbackInfo callback) {
        gtn$forceNativeProxy = false;
        gtn$wirelessLink.save(tag);
    }

    @Inject(method = "inValidate", at = @At("HEAD"), remap = false, require = 0)
    private void gtn$useNativeProxyWhileInvalidating(CallbackInfo callback) {
        if (gtn$wirelessProxy != null) gtn$wirelessProxy.onChunkUnload();
        gtn$forceNativeProxy = true;
    }

    @Inject(method = "inValidate", at = @At("TAIL"), remap = false, require = 0)
    private void gtn$finishInvalidating(CallbackInfo callback) {
        gtn$forceNativeProxy = false;
    }

    @Inject(method = "onRemoval", at = @At("HEAD"), remap = false, require = 0)
    private void gtn$useNativeProxyWhileRemoving(CallbackInfo callback) {
        if (gtn$wirelessProxy != null) gtn$wirelessProxy.invalidate();
        gtn$forceNativeProxy = true;
    }

    @Inject(method = "onRemoval", at = @At("TAIL"), remap = false, require = 0)
    private void gtn$finishRemoving(CallbackInfo callback) {
        gtn$forceNativeProxy = false;
    }

    @Inject(method = "onRightclick", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtn$handleWirelessBinding(IGregTechTileEntity tile, EntityPlayer player, ForgeDirection side, float x,
        float y, float z, CallbackInfoReturnable<Boolean> callback) {
        ItemStack held = player.inventory.getCurrentItem();
        if (GTN_WirelessMEDataStick.isWirelessLinkStick(held)) {
            if (tile.isServerSide() && gtn$bindWireless(held)) {
                player.addChatMessage(
                    new ChatComponentText("Wireless ME connected: " + gtn$wirelessLink.getNetworkName()));
            }
            callback.setReturnValue(true);
            return;
        }

        if (player.isSneaking() && held == null && gtn$wirelessLink.isLinked()) {
            if (tile.isServerSide()) {
                String oldName = gtn$wirelessLink.getNetworkName();
                gtn$disconnectWireless();
                player.addChatMessage(new ChatComponentText("Wireless ME disconnected: " + oldName));
            }
            callback.setReturnValue(true);
        }
    }



    @Inject(method = "onPostTick", at = @At("TAIL"), remap = false, require = 0)
    private void gtn$tickWirelessLink(IGregTechTileEntity tile, long tick, CallbackInfo callback) {
        if (!tile.isServerSide() || !gtn$wirelessLink.isLinked()) return;

        GTN_WirelessAENetworkProxy wirelessProxy = (GTN_WirelessAENetworkProxy) gtn$getWirelessProxy();
        wirelessProxy.updateWirelessConnection();
        boolean changed = gtn$wirelessLink.tick(wirelessProxy);
        tile.setActive(gtn$wirelessLink.isReady());

        if (changed) {
            MTEHatchCraftingInputME self = (MTEHatchCraftingInputME) (Object) this;
            self.gridChanged();
            tile.markDirty();
        }
    }

    @Unique
    private boolean gtn$bindWireless(ItemStack dataStick) {
        MTEHatchCraftingInputME self = (MTEHatchCraftingInputME) (Object) this;
        if (!gtn$wirelessLink.isLinked()) {
            gtn$nativeProxy = gtn$getNativeProxy(self);
        }

        if (!gtn$wirelessLink.bind(dataStick)) return false;
        gtn$disconnectNativeProxyFromCable();
        self.getBaseMetaTileEntity().enableTicking();

        AENetworkProxy wirelessProxy = gtn$getWirelessProxy();
        if (!wirelessProxy.isReady()) wirelessProxy.onReady();
        self.onFacingChange();
        self.gridChanged();
        self.markDirty();
        return true;
    }

    @Unique
    private AENetworkProxy gtn$getNativeProxy(MTEHatchCraftingInputME self) {
        if (gtn$nativeProxy != null) return gtn$nativeProxy;

        boolean oldForceNativeProxy = gtn$forceNativeProxy;
        gtn$forceNativeProxy = true;
        try {
            gtn$nativeProxy = self.getProxy();
            return gtn$nativeProxy;
        } finally {
            gtn$forceNativeProxy = oldForceNativeProxy;
        }
    }

    @Unique
    private void gtn$disconnectNativeProxyFromCable() {
        if (!gtn$wirelessLink.isLinked() || gtn$nativeProxy == null) return;
        gtn$nativeProxy.setValidSides(EnumSet.noneOf(ForgeDirection.class));
    }

    @Unique
    private void gtn$disconnectWireless() {
        MTEHatchCraftingInputME self = (MTEHatchCraftingInputME) (Object) this;
        gtn$wirelessLink.disconnect();

        if (gtn$wirelessProxy != null) {
            gtn$wirelessProxy.invalidate();
            gtn$wirelessProxy = null;
        }

        AENetworkProxy localProxy = gtn$getNativeProxy(self);
        if (localProxy != null && !localProxy.isReady()) localProxy.onReady();
        self.onFacingChange();
        self.updateAE2ProxyColor();
        self.gridChanged();
        self.markDirty();
    }
}
