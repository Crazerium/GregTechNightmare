package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import net.minecraft.inventory.InventoryCrafting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard.WildcardPatternProviderHook;
import com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard.WildcardPatternPushHook;

import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProviderHelper;

/** Hooks the GT5U Crafting Input Bus backend without inheriting its GUI. */
@Mixin(targets = "gregtech.common.tileentities.machines.MTEHatchCraftingInputME", remap = false)
public abstract class MTEHatchCraftingInputMEMixin {

    /**
     * Routes GT5U pattern publication through the wildcard expansion provider.
     *
     * @param craftingTracker AE crafting registry receiving the published pattern
     * @param medium          crafting medium that owns the pattern
     * @param details         source encoded pattern
     * @author Crazerium
     * @reason AE must receive one concrete processing pattern for every compatible GregTech material
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

    /**
     * Validates and reserves the resolved wildcard variant before GT5U accepts the crafting request.
     *
     * @param details           pattern selected by AE
     * @param craftingInventory AE crafting inventory supplied to GT5U
     * @param callback          callback used to reject an invalid or blocked request
     * @author Crazerium
     * @reason Cached plans may become invalid after the source pattern or blacklist changes
     */
    @Inject(method = "pushPattern", at = @At("HEAD"), cancellable = true, order = 850, remap = false, require = 0)
    private void gtn$prepareWildcardPattern(ICraftingPatternDetails details, InventoryCrafting craftingInventory,
        CallbackInfoReturnable<Boolean> callback) {
        if (!WildcardPatternPushHook.canPush(this, details)) {
            callback.setReturnValue(false);
            return;
        }
    }

    /**
     * Replaces the synthetic wildcard pattern with the original encoded pattern expected by GT5U.
     *
     * @param details pattern passed by AE
     * @return original encoded pattern for wildcard variants, otherwise the supplied pattern
     * @author Crazerium
     * @reason GT5U tracks its live pattern slots by the original pattern object
     */
    @ModifyVariable(
        method = "pushPattern",
        at = @At("HEAD"),
        argsOnly = true,
        index = 1,
        order = 1100,
        remap = false,
        require = 0)
    private ICraftingPatternDetails gtn$unwrapWildcardPattern(ICraftingPatternDetails details) {
        return WildcardPatternPushHook.unwrap(this, details);
    }
}
