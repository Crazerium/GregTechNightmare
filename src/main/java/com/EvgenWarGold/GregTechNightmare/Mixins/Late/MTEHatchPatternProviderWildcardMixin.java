package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard.WildcardPatternProviderHook;

import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProviderHelper;

@Mixin(targets = "gregtech.common.tileentities.machines.MTEHatchPatternProvider", remap = false)
public abstract class MTEHatchPatternProviderWildcardMixin {

    @Redirect(
        method = "provideCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/networking/crafting/ICraftingProviderHelper;addCraftingOption(Lappeng/api/networking/crafting/ICraftingMedium;Lappeng/api/networking/crafting/ICraftingPatternDetails;)V"),
        remap = false,
        require = 0)
    private void gtn$publishResolvedWildcardPatterns(ICraftingProviderHelper craftingTracker, ICraftingMedium medium,
        ICraftingPatternDetails details) {
        WildcardPatternProviderHook.addCraftingOption(craftingTracker, medium, details);
    }
}
