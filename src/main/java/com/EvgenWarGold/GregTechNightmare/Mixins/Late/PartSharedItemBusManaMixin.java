package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.EvgenWarGold.GregTechNightmare.GregTech.Mana.GTN_ManaBuses;
import com.glodblock.github.common.parts.PartFluidExportBus;

import appeng.parts.automation.PartSharedItemBus;

/** Keeps a fluid export bus awake when it faces a Botania mana receiver instead of a fluid handler. */
@Mixin(value = PartSharedItemBus.class, remap = false)
public abstract class PartSharedItemBusManaMixin {

    /**
     * @param callback returns the facing mana receiver when there is no fluid target
     * @author Crazerium
     * @reason The bus sleeps while it has no target, so the mana receiver has to count as one
     */
    @Inject(method = "getTarget", at = @At("RETURN"), cancellable = true, remap = false, require = 1)
    private void gtn$targetManaReceiver(CallbackInfoReturnable<Object> callback) {
        if (callback.getReturnValue() != null || !((Object) this instanceof PartFluidExportBus bus)) return;
        TileEntity tile = GTN_ManaBuses.getFacingTile(bus);
        if (GTN_ManaBuses.isManaTarget(tile)) callback.setReturnValue(tile);
    }
}
