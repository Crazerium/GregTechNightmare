package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.EvgenWarGold.GregTechNightmare.GregTech.Mana.GTN_ManaBuses;
import com.glodblock.github.common.parts.PartFluidImportBus;

/** Keeps a fluid import bus awake when it faces a Botania mana pool instead of a fluid handler. */
@Mixin(value = PartFluidImportBus.class, remap = false)
public abstract class PartFluidImportBusManaMixin {

    /**
     * @param callback returns the facing mana pool when there is no fluid target
     * @author Crazerium
     * @reason The bus sleeps while it has no target, so the mana pool has to count as one
     */
    @Inject(method = "getTarget", at = @At("RETURN"), cancellable = true, remap = false, require = 1)
    private void gtn$targetManaPool(CallbackInfoReturnable<Object> callback) {
        if (callback.getReturnValue() != null) return;
        TileEntity tile = GTN_ManaBuses.getFacingTile((PartFluidImportBus) (Object) this);
        if (GTN_ManaBuses.isManaStorage(tile)) callback.setReturnValue(tile);
    }
}
