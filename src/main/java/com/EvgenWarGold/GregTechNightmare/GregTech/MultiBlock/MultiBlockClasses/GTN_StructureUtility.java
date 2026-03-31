package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import static gregtech.api.util.GTStructureUtility.activeCoils;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;
import static gregtech.api.util.GTStructureUtility.ofCoil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureUtility;

import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gtPlusPlus.core.block.base.BasicBlock;
import gtPlusPlus.core.block.base.BlockBaseModular;
import gtPlusPlus.core.material.Material;

public class GTN_StructureUtility {

    public static <T> IStructureElement<T> ofFrame(Material material) {
        return StructureUtility.ofBlock(BlockBaseModular.getMaterialBlock(material, BasicBlock.BlockTypes.FRAME), 0);
    }

    public static <T> IStructureElement<T> createTierBlocks(CasingData casingData, ItemStack... itemStacks) {

        List<Pair<Block, Integer>> blocks = new ArrayList<>();
        for (ItemStack stack : itemStacks) {
            if (stack != null && stack.getItem() instanceof ItemBlock itemBlock) {
                blocks.add(Pair.of(itemBlock.field_150939_a, stack.getItemDamage()));
            }
        }

        Consumer<T> countIncrementer = (t) -> casingData.countCasing++;

        return StructureUtility.onElementPass(countIncrementer, StructureUtility.ofBlocksTiered((block, meta) -> {
            for (int i = 0; i < itemStacks.length; i++) {
                ItemStack itemStack = itemStacks[i];
                if (itemStack != null && itemStack.getItem() instanceof ItemBlock itemBlock) {
                    if (block == itemBlock.field_150939_a && meta == itemStack.getItemDamage()) {
                        for (GTN_Casings casing : GTN_Casings.values()) {
                            if (casing.getItemStack()
                                .isItemEqual(itemStack)) {
                                casingData.setCasing(casing);
                                casingData.setCasingTextureId(casing.textureId);
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
                casingData.setCasingTier(tierIndex);
            }
        }, t -> casingData.getCasingTier()));
    }

    public static <T> IStructureElement<T> createTierBlocks(CasingData casingData, GTN_Casings... casings) {
        ItemStack[] itemStacks = Arrays.stream(casings)
            .filter(Objects::nonNull)
            .map(GTN_Casings::getItemStack)
            .toArray(ItemStack[]::new);
        return GTN_StructureUtility.createTierBlocks(casingData, itemStacks);
    }

    public static <T> IStructureElement<T> createTierBlocks(CasingData casingData, Block... blocks) {
        ItemStack[] itemStacks = Arrays.stream(blocks)
            .filter(Objects::nonNull)
            .map(ItemStack::new)
            .toArray(ItemStack[]::new);
        return GTN_StructureUtility.createTierBlocks(casingData, itemStacks);
    }

    public static <T> IStructureElement<T> createAllTieredGlass(CasingData casingData) {
        return StructureUtility.withChannel(
            casingData.getChannelName(),
            chainAllGlasses(-1, (te, t) -> casingData.setCasingTier(t), te -> casingData.getCasingTier()));
    }

    public static <T extends MTEMultiBlockBase> IStructureElement<T> createAllTierCoilBlock(CasingData casingData) {
        return StructureUtility.withChannel(
            casingData.getChannelName(),
            activeCoils(
                ofCoil(
                    (BiConsumer<T, HeatingCoilLevel>) (te, level) -> casingData.setCoilLevel(level),
                    te -> casingData.getCoilLevel())));
    }
}
