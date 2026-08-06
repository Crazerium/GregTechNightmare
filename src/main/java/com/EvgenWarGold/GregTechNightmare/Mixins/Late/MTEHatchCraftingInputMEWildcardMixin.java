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

@Mixin(targets = "gregtech.common.tileentities.machines.MTEHatchCraftingInputME", remap = false)
public abstract class MTEHatchCraftingInputMEWildcardMixin {

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

    @Inject(method = "pushPattern", at = @At("HEAD"), cancellable = true, order = 850, remap = false, require = 0)
    private void gtn$prepareWildcardPattern(ICraftingPatternDetails details, InventoryCrafting craftingInventory,
        CallbackInfoReturnable<Boolean> callback) {
        if (!WildcardPatternPushHook.canPush(this, details)) {
            callback.setReturnValue(false);
            return;
        }
    }

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
