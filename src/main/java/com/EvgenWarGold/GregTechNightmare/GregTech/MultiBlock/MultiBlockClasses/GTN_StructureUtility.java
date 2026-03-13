package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureUtility;

public class GTN_StructureUtility {

    public static class TierData {

        private int casingTier = -1;
        private int countCasing = 0;

        public int getCasingTier() {
            return casingTier;
        }

        public int getCountCasing() {
            return countCasing;
        }

        public void setCasingTier(int casingTier) {
            this.casingTier = casingTier;
        }

        public void reset() {
            casingTier = -1;
            countCasing = 0;
        }
    }

    public static <T> IStructureElement<T> createTierBlocks(TierData tierData, ItemStack... itemStacks) {

        List<Pair<Block, Integer>> blocks = new ArrayList<>();
        for (ItemStack stack : itemStacks) {
            if (stack != null && stack.getItem() instanceof ItemBlock ib) {
                blocks.add(Pair.of(ib.field_150939_a, stack.getItemDamage()));
            }
        }

        Consumer<T> countIncrementer = (t) -> tierData.countCasing++;

        return StructureUtility.onElementPass(countIncrementer, StructureUtility.ofBlocksTiered((block, meta) -> {
            for (int i = 0; i < itemStacks.length; i++) {
                ItemStack itemStack = itemStacks[i];
                if (itemStack != null && itemStack.getItem() instanceof ItemBlock itemBlock) {
                    if (block == itemBlock.field_150939_a && meta == itemStack.getItemDamage()) {
                        return i + 1;
                    }
                }
            }
            return null;
        }, blocks, -1, (t, tierIndex) -> {
            if (tierIndex != null) {
                tierData.setCasingTier(tierIndex);
            }
        }, t -> tierData.getCasingTier()));
    }

    public static <T> IStructureElement<T> createTierBlocks(TierData tierData, GTN_Casings... casings) {
        ItemStack[] itemStacks = Arrays.stream(casings)
            .filter(Objects::nonNull)
            .map(GTN_Casings::getItemStack)
            .toArray(ItemStack[]::new);
        return createTierBlocks(tierData, itemStacks);
    }
}
