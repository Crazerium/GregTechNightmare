package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureUtility;

import gregtech.api.interfaces.IHatchElement;
import gregtech.api.util.GTStructureUtility;

public class TieredElementBuilder<T> {

    private final TierData tierData;
    private final Class<T> tileClass;

    private GTN_Casings[] casings = new GTN_Casings[0];
    private IHatchElement<? super T>[] hatches;
    private int dot = 1;

    public TieredElementBuilder(TierData tierData, Class<T> tileClass) {
        this.tierData = tierData;
        this.tileClass = tileClass;
    }

    public static <T> TieredElementBuilder<T> create(TierData tierData, Class<T> tileClass) {
        return new TieredElementBuilder<>(tierData, tileClass);
    }

    public TieredElementBuilder<T> casings(GTN_Casings... casings) {
        this.casings = casings;
        return this;
    }

    @SafeVarargs
    public final TieredElementBuilder<T> hatches(IHatchElement<? super T>... hatches) {
        this.hatches = hatches;
        this.dot = 1;
        return this;
    }

    @SafeVarargs
    public final TieredElementBuilder<T> hatches(int dot, IHatchElement<? super T>... hatches) {
        this.hatches = hatches;
        this.dot = dot;
        return this;
    }

    public static <T> IStructureElement<T> createTierBlocks(TierData tierData, ItemStack... itemStacks) {

        List<Pair<Block, Integer>> blocks = new ArrayList<>();
        for (ItemStack stack : itemStacks) {
            if (stack != null && stack.getItem() instanceof ItemBlock itemBlock) {
                blocks.add(Pair.of(itemBlock.field_150939_a, stack.getItemDamage()));
            }
        }

        Consumer<T> countIncrementer = (t) -> tierData.countCasing++;

        return StructureUtility.onElementPass(countIncrementer, StructureUtility.ofBlocksTiered((block, meta) -> {
            for (int i = 0; i < itemStacks.length; i++) {
                ItemStack itemStack = itemStacks[i];
                if (itemStack != null && itemStack.getItem() instanceof ItemBlock itemBlock) {
                    if (block == itemBlock.field_150939_a && meta == itemStack.getItemDamage()) {
                        for (GTN_Casings casing : GTN_Casings.values()) {
                            if (casing.getItemStack()
                                .isItemEqual(itemStack)) {
                                tierData.setCasing(casing);
                                tierData.setCasingTextureId(casing.textureId);
                                return i + 1;
                            }
                        }

                        return i + 1;
                    }
                }
            }
            return null;
        }, blocks, -1, (t, tierIndex) -> {
            if (tierIndex != null) {
                tierData.setCasingTier(tierIndex);
            }
        }, t -> tierData.casingTier));
    }

    public static <T> IStructureElement<T> createTierBlocks(TierData tierData, GTN_Casings... casings) {
        ItemStack[] itemStacks = Arrays.stream(casings)
            .filter(Objects::nonNull)
            .map(GTN_Casings::getItemStack)
            .toArray(ItemStack[]::new);
        return createTierBlocks(tierData, itemStacks);
    }

    public IStructureElement<T> build() {
        return StructureUtility.withChannel(
            tierData.getChannelName(),
            StructureUtility.ofChain(
                GTStructureUtility.buildHatchAdder(tileClass)
                    .atLeast(hatches)
                    .casingIndex(tierData.getCasingTextureId())
                    .dot(dot)
                    .buildAndChain(createTierBlocks(tierData, casings))));
    }
}
