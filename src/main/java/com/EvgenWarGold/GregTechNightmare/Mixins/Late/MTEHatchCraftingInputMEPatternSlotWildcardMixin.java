package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard.WildcardPatternDetails;
import com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard.WildcardPatternRuntime;

import gregtech.api.objects.GTDualInputPattern;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;

@Mixin(value = MTEHatchCraftingInputME.PatternSlot.class, remap = false)
public abstract class MTEHatchCraftingInputMEPatternSlotWildcardMixin {

    @Inject(method = "getPatternInputs", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void gtn$useResolvedWildcardRecipeHint(CallbackInfoReturnable<GTDualInputPattern> callback) {
        WildcardPatternDetails details = WildcardPatternRuntime.getActiveDetails(this);
        if (details == null) return;

        GTDualInputPattern resolved = WildcardPatternRuntime.buildResolvedPatternInputs(this, details);
        if (resolved != null) callback.setReturnValue(resolved);
    }
}
