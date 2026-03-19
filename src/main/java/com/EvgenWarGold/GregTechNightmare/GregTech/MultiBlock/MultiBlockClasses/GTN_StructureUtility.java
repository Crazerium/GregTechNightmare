package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.TierData;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureUtility;

import gtPlusPlus.core.block.base.BasicBlock;
import gtPlusPlus.core.block.base.BlockBaseModular;
import gtPlusPlus.core.material.Material;

public class GTN_StructureUtility {

    public static <T> IStructureElement<T> ofFrame(Material material) {
        return StructureUtility.ofBlock(BlockBaseModular.getMaterialBlock(material, BasicBlock.BlockTypes.FRAME), 0);
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
}
