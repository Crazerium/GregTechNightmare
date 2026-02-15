package com.EvgenWarGold.GregTechNightmare.GregTech.Api;

import net.minecraft.item.ItemStack;

public interface IHasVariantAndTooltips extends IHasTooltips, IHasVariant {

    default ItemStack registerVariantWithTooltips(int meta, String[] tooltips) {
        ItemStack stack = registerVariant(meta);
        setTooltips(meta, tooltips, false);
        return stack;
    }
}
