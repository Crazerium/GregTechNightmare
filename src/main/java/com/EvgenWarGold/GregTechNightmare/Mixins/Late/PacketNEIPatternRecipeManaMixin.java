package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import static com.EvgenWarGold.GregTechNightmare.GregTech.Mana.AEManaStackType.MANA_STACK_TYPE;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.EvgenWarGold.GregTechNightmare.GregTech.Mana.AEManaStack;
import com.github.vfyjxf.nee.network.packet.PacketNEIPatternRecipe;

import appeng.api.storage.data.IAEStack;

/** The mana input of a Botania machine recipe in NEI becomes mana in the pattern. */
@Mixin(value = PacketNEIPatternRecipe.Handler.class, remap = false)
public abstract class PacketNEIPatternRecipeManaMixin {

    /**
     * @param stack    ingredient of the NEI recipe
     * @param callback the mana stack for the mana display item
     * @author Crazerium
     * @reason Not Enough Energistics only converts fluids and essentia into their AE stacks
     */
    @Inject(method = "getAEStack", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void gtn$convertMana(ItemStack stack, CallbackInfoReturnable<IAEStack<?>> callback) {
        AEManaStack mana = MANA_STACK_TYPE.convertStackFromItem(stack);
        if (mana != null) callback.setReturnValue(mana);
    }
}
