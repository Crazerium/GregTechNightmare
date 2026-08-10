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
import org.spongepowered.asm.mixin.gen.Invoker;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_WirelessMEIOHub;
import com.EvgenWarGold.GregTechNightmare.GregTech.Wireless.GTN_IWirelessMEEndpoint;
import com.EvgenWarGold.GregTechNightmare.GregTech.Wireless.GTN_WirelessAENetworkProxy;
import com.EvgenWarGold.GregTechNightmare.GregTech.Wireless.GTN_WirelessMEDataStick;
import com.EvgenWarGold.GregTechNightmare.GregTech.Wireless.GTN_WirelessMELink;

import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import gregtech.api.enums.ItemList;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import gregtech.common.tileentities.machines.RecipeCheckReason;

/** Adds optional wireless access to the existing GT5U ME Stocking/Advanced Stocking Input Bus. */
@Mixin(targets = "gregtech.common.tileentities.machines.MTEHatchInputBusME", remap = false)
public abstract class MTEHatchInputBusMEWirelessMixin implements GTN_IWirelessMEEndpoint {

    @Shadow
    protected boolean processingRecipe;

    @Shadow
    protected abstract void updateAllInformationSlots();

    @Shadow
    protected abstract void clearExtractedStacks();

    @Invoker("scheduleRecipeCheck")
    protected abstract void gtn$invokeScheduleRecipeCheck(RecipeCheckReason reason);

    @Unique
    private final GTN_WirelessMELink gtn$wirelessLink = new GTN_WirelessMELink();
    @Unique
    private GTN_WirelessAENetworkProxy gtn$wirelessProxy;
    @Unique
    private BaseActionSource gtn$wirelessRequestSource;
    @Unique
    private GTN_WirelessMEIOHub gtn$wirelessRequestSourceHub;
    @Unique
    private boolean gtn$wirelessAvailableAtRecipeStart;
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
            MTEHatchInputBusME self = (MTEHatchInputBusME) (Object) this;
            ItemStack visual = self.autoPullAvailable ? ItemList.Hatch_Input_Bus_ME_Advanced.get(1)
                : ItemList.Hatch_Input_Bus_ME.get(1);
            gtn$wirelessProxy = new GTN_WirelessAENetworkProxy(
                (IGridProxyable) (Object) this,
                "gtnWirelessProxy",
                visual,
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
        MTEHatchInputBusME self = (MTEHatchInputBusME) (Object) this;
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



    @Redirect(
        method = "onPostTick",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/api/interfaces/tileentity/IGregTechTileEntity;tryDisableTicking()V"),
        remap = false,
        require = 0)
    private void gtn$keepWirelessBusTicking(IGregTechTileEntity tile) {
        if (!gtn$wirelessLink.isLinked()) tile.tryDisableTicking();
    }

    @Inject(method = "onPostTick", at = @At("TAIL"), remap = false, require = 0)
    private void gtn$tickWirelessLink(IGregTechTileEntity tile, long timer, CallbackInfo callback) {
        if (!tile.isServerSide() || !gtn$wirelessLink.isLinked()) return;

        GTN_WirelessAENetworkProxy wirelessProxy = (GTN_WirelessAENetworkProxy) gtn$getWirelessProxy();
        wirelessProxy.updateWirelessConnection();
        boolean changed = gtn$wirelessLink.tick(wirelessProxy);
        if (changed) {
            if (gtn$wirelessLink.isReady()) {
                updateAllInformationSlots();
                gtn$invokeScheduleRecipeCheck(RecipeCheckReason.IMMEDIATE);
            } else {
                clearExtractedStacks();
            }
            tile.setActive(gtn$wirelessLink.isReady());
            tile.markDirty();
        }

        // Keep the existing periodic fallback as a safety net even though the wireless proxy is now a real grid node.
        if (gtn$wirelessLink.isReady() && timer % 20 == 0) {
            gtn$invokeScheduleRecipeCheck(RecipeCheckReason.THROTTLED);
        }
    }

    @Inject(method = "needsPeriodicChecks", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtn$forcePeriodicChecksForWireless(CallbackInfoReturnable<Boolean> callback) {
        if (gtn$wirelessLink.isLinked()) callback.setReturnValue(true);
    }

    @Inject(method = "startRecipeProcessing", at = @At("HEAD"), remap = false, require = 0)
    private void gtn$rememberWirelessAvailability(CallbackInfo callback) {
        if (!gtn$wirelessLink.isLinked()) {
            gtn$wirelessAvailableAtRecipeStart = true;
            return;
        }
        gtn$wirelessAvailableAtRecipeStart = gtn$wirelessLink.isReady();
    }

    @Inject(method = "endRecipeProcessing", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtn$avoidOfflineStartupShutdown(MTEMultiBlockBase controller,
        CallbackInfoReturnable<CheckRecipeResult> callback) {
        if (!gtn$wirelessLink.isLinked() || gtn$wirelessAvailableAtRecipeStart) return;

        // checkRecipe() always calls endRecipeProcessing(), even when this stocking bus was unavailable and supplied
        // no virtual items. Native GT interprets that temporary AE startup state as a critical extraction failure.
        processingRecipe = false;
        clearExtractedStacks();
        callback.setReturnValue(CheckRecipeResultRegistry.SUCCESSFUL);
    }

    @Inject(method = "getRequestSource", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtn$useHubAsActionSource(CallbackInfoReturnable<BaseActionSource> callback) {
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
        MTEHatchInputBusME self = (MTEHatchInputBusME) (Object) this;
        if (!gtn$wirelessLink.isLinked()) {
            gtn$nativeProxy = gtn$getNativeProxy(self);
        }

        if (!gtn$wirelessLink.bind(dataStick)) return false;
        gtn$disconnectNativeProxyFromCable();
        self.getBaseMetaTileEntity().enableTicking();
        gtn$wirelessRequestSource = null;
        gtn$wirelessRequestSourceHub = null;
        AENetworkProxy wirelessProxy = gtn$getWirelessProxy();
        if (!wirelessProxy.isReady()) wirelessProxy.onReady();
        self.onFacingChange();
        clearExtractedStacks();
        gtn$invokeScheduleRecipeCheck(RecipeCheckReason.IMMEDIATE);
        self.markDirty();
        return true;
    }

    @Unique
    private AENetworkProxy gtn$getNativeProxy(MTEHatchInputBusME self) {
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
        MTEHatchInputBusME self = (MTEHatchInputBusME) (Object) this;
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
        gtn$invokeScheduleRecipeCheck(RecipeCheckReason.IMMEDIATE);
        self.markDirty();
    }
}
