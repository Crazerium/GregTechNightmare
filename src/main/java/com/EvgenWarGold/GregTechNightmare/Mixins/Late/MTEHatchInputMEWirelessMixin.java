package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.common.tileentities.machines.MTEHatchInputME;

/** Adds optional wireless access to the existing GT5U ME Stocking/Advanced Stocking Input Hatch. */
@Mixin(targets = "gregtech.common.tileentities.machines.MTEHatchInputME", remap = false)
public abstract class MTEHatchInputMEWirelessMixin implements GTN_IWirelessMEEndpoint {

    @Shadow
    protected boolean processingRecipe;

    @Shadow
    protected abstract void updateAllInformationSlots();

    @Shadow
    protected abstract void clearExtractedStacks();

    @Unique
    private final GTN_WirelessMELink gtn$wirelessLink = new GTN_WirelessMELink();
    @Unique
    private GTN_WirelessAENetworkProxy gtn$wirelessProxy;
    @Unique
    private AENetworkProxy gtn$nativeProxy;
    @Unique
    private boolean gtn$forceNativeProxy;
    @Unique
    private BaseActionSource gtn$wirelessRequestSource;
    @Unique
    private GTN_WirelessMEIOHub gtn$wirelessRequestSourceHub;
    @Unique
    private boolean gtn$wirelessAvailableAtRecipeStart;

    @Override
    public GTN_WirelessMELink gtn$getWirelessLink() {
        return gtn$wirelessLink;
    }

    @Override
    public AENetworkProxy gtn$getWirelessProxy() {
        if (gtn$wirelessProxy == null) {
            MTEHatchInputME self = (MTEHatchInputME) (Object) this;
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
        MTEHatchInputME self = (MTEHatchInputME) (Object) this;
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



    @Inject(method = "getGridNode", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtn$exposeWirelessGridNode(ForgeDirection side, CallbackInfoReturnable<IGridNode> callback) {
        if (gtn$wirelessLink.isLinked()) callback.setReturnValue(gtn$getWirelessProxy().getNode());
    }

    @Redirect(
        method = "onPostTick",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/api/interfaces/tileentity/IGregTechTileEntity;tryDisableTicking()V"),
        remap = false,
        require = 0)
    private void gtn$keepWirelessHatchTicking(IGregTechTileEntity tile) {
        if (!gtn$wirelessLink.isLinked()) tile.tryDisableTicking();
    }

    @Inject(method = "onPostTick", at = @At("TAIL"), remap = false, require = 0)
    private void gtn$tickWirelessLink(IGregTechTileEntity tile, long tick, CallbackInfo callback) {
        if (!tile.isServerSide() || !gtn$wirelessLink.isLinked()) return;

        GTN_WirelessAENetworkProxy wirelessProxy = (GTN_WirelessAENetworkProxy) gtn$getWirelessProxy();
        wirelessProxy.updateWirelessConnection();
        boolean changed = gtn$wirelessLink.tick(wirelessProxy);
        tile.setActive(gtn$wirelessLink.isReady());
        if (!changed) return;

        if (gtn$wirelessLink.isReady()) {
            updateAllInformationSlots();
            gtn$notifyMachineUpdate(tile);
        } else {
            clearExtractedStacks();
        }
        tile.markDirty();
    }

    @Inject(method = "startRecipeProcessing", at = @At("HEAD"), remap = false, require = 0)
    private void gtn$rememberWirelessAvailability(CallbackInfo callback) {
        gtn$wirelessAvailableAtRecipeStart = !gtn$wirelessLink.isLinked() || gtn$wirelessLink.isReady();
    }

    @Inject(method = "endRecipeProcessing", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtn$avoidOfflineStartupShutdown(MTEMultiBlockBase controller,
        CallbackInfoReturnable<CheckRecipeResult> callback) {
        if (!gtn$wirelessLink.isLinked() || gtn$wirelessAvailableAtRecipeStart) return;

        processingRecipe = false;
        clearExtractedStacks();
        callback.setReturnValue(CheckRecipeResultRegistry.SUCCESSFUL);
    }

    @Inject(method = "getRequestSource", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtn$useHubAsRequestSource(CallbackInfoReturnable<BaseActionSource> callback) {
        if (!gtn$wirelessLink.isLinked()) return;

        GTN_WirelessMEIOHub hub = gtn$wirelessLink.getHub();
        if (hub == null) return;
        if (gtn$wirelessRequestSource == null || gtn$wirelessRequestSourceHub != hub) {
            gtn$wirelessRequestSourceHub = hub;
            gtn$wirelessRequestSource = new MachineSource(hub);
        }
        callback.setReturnValue(gtn$wirelessRequestSource);
    }

    @Unique
    private boolean gtn$bindWireless(ItemStack dataStick) {
        MTEHatchInputME self = (MTEHatchInputME) (Object) this;
        if (!gtn$wirelessLink.isLinked()) gtn$nativeProxy = gtn$getNativeProxy(self);
        if (!gtn$wirelessLink.bind(dataStick)) return false;

        gtn$wirelessRequestSource = null;
        gtn$wirelessRequestSourceHub = null;
        gtn$disconnectNativeProxyFromCable();
        self.getBaseMetaTileEntity().enableTicking();

        AENetworkProxy wirelessProxy = gtn$getWirelessProxy();
        if (!wirelessProxy.isReady()) wirelessProxy.onReady();
        self.onFacingChange();
        clearExtractedStacks();
        gtn$notifyMachineUpdate(self.getBaseMetaTileEntity());
        self.markDirty();
        return true;
    }

    @Unique
    private AENetworkProxy gtn$getNativeProxy(MTEHatchInputME self) {
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
        if (gtn$nativeProxy.getNode() != null) gtn$nativeProxy.getNode().updateState();
    }

    @Unique
    private void gtn$disconnectWireless() {
        MTEHatchInputME self = (MTEHatchInputME) (Object) this;
        gtn$wirelessLink.disconnect();
        gtn$wirelessRequestSource = null;
        gtn$wirelessRequestSourceHub = null;
        clearExtractedStacks();

        if (gtn$wirelessProxy != null) {
            gtn$wirelessProxy.invalidate();
            gtn$wirelessProxy = null;
        }

        AENetworkProxy localProxy = gtn$getNativeProxy(self);
        if (localProxy != null && !localProxy.isReady()) localProxy.onReady();
        self.onFacingChange();
        self.updateAE2ProxyColor();
        gtn$notifyMachineUpdate(self.getBaseMetaTileEntity());
        self.markDirty();
    }

    @Unique
    private static void gtn$notifyMachineUpdate(IGregTechTileEntity tile) {
        if (tile == null || !tile.isServerSide()) return;
        GregTechAPI.causeMachineUpdate(tile.getWorld(), tile.getXCoord(), tile.getYCoord(), tile.getZCoord());
    }
}
