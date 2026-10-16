package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.EvgenWarGold.GregTechNightmare.GregTech.Mana.GTN_ManaBuses;
import com.glodblock.github.common.parts.PartFluidExportBus;

import appeng.api.networking.ticking.TickRateModulation;
import appeng.parts.automation.PartBaseExportBus;

/** A fluid export bus facing a Botania mana receiver exports mana. */
@Mixin(value = PartBaseExportBus.class, remap = false)
public abstract class PartBaseExportBusManaMixin {

    /**
     * @param callback tick rate of the mana export
     * @author Crazerium
     * @reason Fluid buses are reused for mana instead of adding separate mana buses
     */
    @Inject(method = "doBusWork", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void gtn$exportMana(CallbackInfoReturnable<TickRateModulation> callback) {
        if (!((Object) this instanceof PartFluidExportBus bus)) return;
        TickRateModulation result = GTN_ManaBuses.exportMana(bus);
        if (result != null) callback.setReturnValue(result);
    }
}
