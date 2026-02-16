package com.EvgenWarGold.GregTechNightmare.GregTech.Api;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Unmodifiable;

public interface IHasVariant {

    ItemStack getVariant(int meta) throws IllegalArgumentException;

    ItemStack[] getVariants();

    ItemStack registerVariant(int meta) throws IllegalArgumentException;

    @Unmodifiable
    Set<Integer> getVariantIds();

    default boolean isValidVariant(int meta) {
        return getVariantIds().contains(meta);
    }

    default Map<Integer, IIcon> registerAllVariantIcons(IIconRegister register, Function<Integer, String> iconPath) {
        return getVariantIds().stream()
            .map(meta -> Pair.of(meta, register.registerIcon(iconPath.apply(meta))))
            .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }
}
