package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.EvgenWarGold.GregTechNightmare.GregTech.Mana.GTN_ManaBuses;
import com.glodblock.github.common.parts.PartFluidImportBus;

import appeng.api.networking.ticking.TickRateModulation;
import appeng.parts.automation.PartBaseImportBus;

/** A fluid import bus facing a Botania mana pool imports mana. */
@Mixin(value = PartBaseImportBus.class, remap = false)
public abstract class PartBaseImportBusManaMixin {

    /**
     * @param callback tick rate of the mana import
     * @author Crazerium
     * @reason Fluid buses are reused for mana instead of adding separate mana buses
     */
    @Inject(method = "doBusWork", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void gtn$importMana(CallbackInfoReturnable<TickRateModulation> callback) {
        if (!((Object) this instanceof PartFluidImportBus bus)) return;
        TickRateModulation result = GTN_ManaBuses.importMana(bus);
        if (result != null) callback.setReturnValue(result);
    }
}
