package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard.WildcardPatternDetails;
import com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard.WildcardPatternRuntime;

import gregtech.api.objects.GTDualInputPattern;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;

/** Makes GT5U recipe lookup use material-resolved wildcard inputs and shared bus slots. */
@Mixin(value = MTEHatchCraftingInputME.PatternSlot.class, remap = false)
public abstract class MTEHatchCraftingInputMEPatternSlotWildcardMixin {

    /**
     * Supplies the concrete item and fluid inputs selected for the active wildcard material.
     *
     * @param callback callback used to return the resolved GT dual-input pattern
     * @author Crazerium
     * @reason GT5U only sees the encoded wildcard tokens and cannot use them for recipe lookup
     */
    @Inject(method = "getPatternInputs", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void gtn$useResolvedWildcardRecipeHint(CallbackInfoReturnable<GTDualInputPattern> callback) {
        WildcardPatternDetails details = WildcardPatternRuntime.getActiveDetails(this);
        if (details == null) return;

        GTDualInputPattern resolved = WildcardPatternRuntime.buildResolvedPatternInputs(this, details);
        if (resolved != null) callback.setReturnValue(resolved);
    }
}
