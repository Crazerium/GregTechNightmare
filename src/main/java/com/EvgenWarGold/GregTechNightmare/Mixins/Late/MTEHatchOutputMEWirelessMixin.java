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

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_WirelessMEIOHub;
import com.EvgenWarGold.GregTechNightmare.GregTech.Wireless.GTN_IWirelessMEEndpoint;
import com.EvgenWarGold.GregTechNightmare.GregTech.Wireless.GTN_WirelessAENetworkProxy;
import com.EvgenWarGold.GregTechNightmare.GregTech.Wireless.GTN_WirelessMEDataStick;
import com.EvgenWarGold.GregTechNightmare.GregTech.Wireless.GTN_WirelessMELink;

import appeng.api.networking.IGridNode;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IMEInventory;
import appeng.api.util.AEColor;
import appeng.api.storage.data.IAEFluidStack;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import gregtech.api.enums.Dyes;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.tileentities.machines.outputme.MTEHatchOutputME;

/** Adds optional wireless access to the existing GT5U ME Output Hatch while keeping its native fluid provider. */
@Mixin(targets = "gregtech.common.tileentities.machines.outputme.MTEHatchOutputME", remap = false)
public abstract class MTEHatchOutputMEWirelessMixin implements GTN_IWirelessMEEndpoint {

    @Unique
    private final GTN_WirelessMELink gtn$wirelessLink = new GTN_WirelessMELink();
    @Unique
    private GTN_WirelessAENetworkProxy gtn$wirelessProxy;
    @Unique
    private BaseActionSource gtn$wirelessActionSource;
    @Unique
    private GTN_WirelessMEIOHub gtn$wirelessActionSourceHub;

    @Override
    public GTN_WirelessMELink gtn$getWirelessLink() {
        return gtn$wirelessLink;
    }

    @Override
    public AENetworkProxy gtn$getWirelessProxy() {
        if (gtn$wirelessProxy == null) {
            MTEHatchOutputME self = (MTEHatchOutputME) (Object) this;
            gtn$wirelessProxy = new GTN_WirelessAENetworkProxy(
                (IGridProxyable) (Object) this,
                "gtnWirelessProxy",
                self.getStackForm(1),
                gtn$wirelessLink);
            gtn$configureWirelessProxy(gtn$wirelessProxy);
        }
        return gtn$wirelessProxy;
    }

    @Inject(method = "loadNBTData", at = @At("HEAD"), remap = false, require = 0)
    private void gtn$loadWirelessLinkBeforeProviderProxy(NBTTagCompound tag, CallbackInfo callback) {
        gtn$wirelessLink.load(tag);
    }

    @Inject(method = "saveNBTData", at = @At("TAIL"), remap = false, require = 0)
    private void gtn$saveWirelessLink(NBTTagCompound tag, CallbackInfo callback) {
        gtn$wirelessLink.save(tag);
    }

    @Inject(method = "inValidate", at = @At("HEAD"), remap = false, require = 0)
    private void gtn$unloadWirelessProxy(CallbackInfo callback) {
        if (gtn$wirelessProxy != null) gtn$wirelessProxy.onChunkUnload();
    }

    @Inject(method = "onRemoval", at = @At("HEAD"), remap = false, require = 0)
    private void gtn$invalidateWirelessProxy(CallbackInfo callback) {
        if (gtn$wirelessProxy != null) gtn$wirelessProxy.invalidate();
    }

    @Inject(method = "onRightclick", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtn$handleWirelessBinding(IGregTechTileEntity tile, EntityPlayer player,
        CallbackInfoReturnable<Boolean> callback) {
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


    @Inject(method = "getGridNode", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtn$exposeWirelessGridNode(ForgeDirection side, CallbackInfoReturnable<IGridNode> callback) {
        if (gtn$wirelessLink.isLinked()) callback.setReturnValue(gtn$getWirelessProxy().getNode());
    }

    @Inject(method = "onFacingChange", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtn$useWirelessCableSides(CallbackInfo callback) {
        if (!gtn$wirelessLink.isLinked()) return;
        gtn$disconnectNativeProxyFromCable();
        gtn$configureWirelessProxy((GTN_WirelessAENetworkProxy) gtn$getWirelessProxy());
        callback.cancel();
    }

    @Inject(method = "onWireCutterRightClick", at = @At("RETURN"), remap = false, require = 0)
    private void gtn$syncWirelessSidesAfterWireCutter(ForgeDirection side, ForgeDirection wrenchingSide,
        EntityPlayer player, float x, float y, float z, ItemStack tool, CallbackInfoReturnable<Boolean> callback) {
        if (!gtn$wirelessLink.isLinked()) return;
        gtn$disconnectNativeProxyFromCable();
        gtn$configureWirelessProxy((GTN_WirelessAENetworkProxy) gtn$getWirelessProxy());
    }

    @Inject(method = "setConnectsToAllSides", at = @At("RETURN"), remap = false, require = 0)
    private void gtn$syncWirelessSidesAfterConnectionChange(boolean connects, CallbackInfo callback) {
        if (!gtn$wirelessLink.isLinked()) return;
        gtn$disconnectNativeProxyFromCable();
        gtn$configureWirelessProxy((GTN_WirelessAENetworkProxy) gtn$getWirelessProxy());
    }

    @Inject(method = "onColorChangeServer", at = @At("RETURN"), remap = false, require = 0)
    private void gtn$syncWirelessColorAfterColorChange(byte color, CallbackInfo callback) {
        if (!gtn$wirelessLink.isLinked()) return;
        gtn$configureWirelessProxy((GTN_WirelessAENetworkProxy) gtn$getWirelessProxy());
    }

    @Inject(method = "onPostTick", at = @At("TAIL"), remap = false, require = 0)
    private void gtn$tickWirelessLink(IGregTechTileEntity tile, long tick, CallbackInfo callback) {
        if (!tile.isServerSide() || !gtn$wirelessLink.isLinked()) return;

        GTN_WirelessAENetworkProxy wirelessProxy = (GTN_WirelessAENetworkProxy) gtn$getWirelessProxy();
        wirelessProxy.updateWirelessConnection();
        boolean changed = gtn$wirelessLink.tick(wirelessProxy);
        tile.setActive(gtn$wirelessLink.isReady());
        if (!changed) return;

        MTEHatchOutputME self = (MTEHatchOutputME) (Object) this;
        if (gtn$wirelessLink.isReady()) self.getProvider().flushCachedStack();
        tile.markDirty();
    }

    @Inject(method = "isPowered", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtn$useWirelessPoweredState(CallbackInfoReturnable<Boolean> callback) {
        if (gtn$wirelessLink.isLinked()) callback.setReturnValue(gtn$getWirelessProxy().isPowered());
    }

    @Inject(method = "isActive", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtn$useWirelessActiveState(CallbackInfoReturnable<Boolean> callback) {
        if (gtn$wirelessLink.isLinked()) callback.setReturnValue(gtn$getWirelessProxy().isActive());
    }

    @Inject(method = "getNetworkInvtory", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtn$useWirelessNetworkInventory(CallbackInfoReturnable<IMEInventory<IAEFluidStack>> callback)
        throws GridAccessException {
        if (!gtn$wirelessLink.isLinked()) return;
        callback.setReturnValue(gtn$getWirelessProxy().getStorage().getFluidInventory());
    }

    @Inject(method = "getActionSource", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtn$useHubAsActionSource(CallbackInfoReturnable<BaseActionSource> callback) {
        if (!gtn$wirelessLink.isLinked()) return;

        GTN_WirelessMEIOHub hub = gtn$wirelessLink.getHub();
        if (hub == null) return;
        if (gtn$wirelessActionSource == null || gtn$wirelessActionSourceHub != hub) {
            gtn$wirelessActionSourceHub = hub;
            gtn$wirelessActionSource = new MachineSource(hub);
        }
        callback.setReturnValue(gtn$wirelessActionSource);
    }

    @Inject(method = "getActionableNode", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtn$useHubActionableNode(CallbackInfoReturnable<IGridNode> callback) {
        if (!gtn$wirelessLink.isLinked() || gtn$wirelessLink.getProxy() == null) return;
        callback.setReturnValue(gtn$wirelessLink.getProxy().getNode());
    }

    @Unique
    private boolean gtn$bindWireless(ItemStack dataStick) {
        MTEHatchOutputME self = (MTEHatchOutputME) (Object) this;
        gtn$disconnectNativeProxyFromCable();
        if (!gtn$wirelessLink.bind(dataStick)) return false;

        gtn$wirelessActionSource = null;
        gtn$wirelessActionSourceHub = null;
        gtn$disconnectNativeProxyFromCable();
        AENetworkProxy wirelessProxy = gtn$getWirelessProxy();
        if (!wirelessProxy.isReady()) wirelessProxy.onReady();
        self.getBaseMetaTileEntity().enableTicking();
        self.markDirty();
        return true;
    }

    @Unique
    private void gtn$disconnectNativeProxyFromCable() {
        MTEHatchOutputME self = (MTEHatchOutputME) (Object) this;
        AENetworkProxy localProxy = self.getProxy();
        if (localProxy == null) return;
        localProxy.setValidSides(EnumSet.noneOf(ForgeDirection.class));
        if (localProxy.getNode() != null) localProxy.getNode().updateState();
    }

    @Unique
    private void gtn$configureWirelessProxy(GTN_WirelessAENetworkProxy proxy) {
        MTEHatchOutputME self = (MTEHatchOutputME) (Object) this;
        if (self.getBaseMetaTileEntity() == null) return;
        proxy.configureCableSides(self.connectsToAllSides(), self.getBaseMetaTileEntity().getFrontFacing());
        byte color = self.getColor();
        proxy.setColor(color == -1 ? AEColor.Transparent : AEColor.values()[Dyes.transformDyeIndex(color)]);
        if (proxy.getNode() != null) proxy.getNode().updateState();
    }

    @Unique
    private void gtn$disconnectWireless() {
        MTEHatchOutputME self = (MTEHatchOutputME) (Object) this;
        gtn$wirelessLink.disconnect();
        gtn$wirelessActionSource = null;
        gtn$wirelessActionSourceHub = null;

        if (gtn$wirelessProxy != null) {
            gtn$wirelessProxy.invalidate();
            gtn$wirelessProxy = null;
        }

        AENetworkProxy localProxy = self.getProxy();
        if (localProxy != null && !localProxy.isReady()) localProxy.onReady();
        self.onFacingChange();
        self.onColorChangeServer(self.getColor());
        self.markDirty();
    }
}
