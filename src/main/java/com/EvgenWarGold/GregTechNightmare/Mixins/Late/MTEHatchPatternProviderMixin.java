package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard.WildcardPatternProviderHook;

import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProviderHelper;

/** Redirects GT5U pattern publication through wildcard expansion. */
@Mixin(targets = "gregtech.common.tileentities.machines.MTEHatchPatternProvider", remap = false)
public abstract class MTEHatchPatternProviderMixin {

    /**
     * Routes the pattern-provider publication path through wildcard expansion.
     *
     * @param craftingTracker AE crafting registry receiving the published pattern
     * @param medium          crafting medium that owns the pattern
     * @param details         source encoded pattern
     * @author Crazerium
     * @reason Some GT5U versions publish Crafting Input ME patterns through the provider base class
     */
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
