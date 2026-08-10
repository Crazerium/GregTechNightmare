package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.EvgenWarGold.GregTechNightmare.GregTech.Wireless.GTN_IWirelessMEEndpoint;

import appeng.me.helpers.AENetworkProxy;
import gregtech.common.tileentities.machines.outputme.base.MTEHatchOutputMEBase;

/**
 * Redirects only the runtime network access of the native GT5U ME-output provider to the selected wireless grid.
 *
 * The provider's own proxy is deliberately left untouched for NBT and BaseMetaTileEntity lifecycle handling. That
 * keeps the native GridNode responsible for its normal save/load/invalidate/chunk-unload lifecycle while the actual
 * item transfer, active-state checks and cell-array notifications use the remote Hub network.
 */
@Mixin(targets = "gregtech.common.tileentities.machines.outputme.base.MTEHatchOutputMEBase", remap = false)
public abstract class MTEHatchOutputMEBaseWirelessMixin {

    @Shadow
    @Final
    private MTEHatchOutputMEBase.Environment<?> env;

    @Redirect(
        method = {
            "updateCell",
            "getCellArray",
            "onContentsChanged",
            "onPostTick",
            "flushCachedStack",
            "updateCellArray",
            "getInfoData" },
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/common/tileentities/machines/outputme/base/MTEHatchOutputMEBase;getProxy()Lappeng/me/helpers/AENetworkProxy;"),
        remap = false,
        require = 0)
    private AENetworkProxy gtn$useWirelessProxyForRuntime(MTEHatchOutputMEBase<?> provider) {
        if (env instanceof GTN_IWirelessMEEndpoint endpoint && endpoint.gtn$isWirelessLinked()) {
            return endpoint.gtn$getWirelessProxy();
        }
        return provider.getProxy();
    }
}
