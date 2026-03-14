package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;

import blockrenderer6343.client.renderer.WorldSceneRenderer;
import blockrenderer6343.client.utils.BRUtil;
import codechicken.nei.NEIClientUtils;

@Mixin(BRUtil.class)
public class FixTreeSprouterCrushMixins {

    /**
     * Retrieves the list of items (ingredients) from rendered blocks in the scene.
     * Fixes TreeSprouter crash issue caused by incorrect drop handling.
     *
     * @param renderer The world scene renderer containing the rendered blocks
     * @return List of item stacks representing the blocks in the scene
     * @author EvgenWarGold
     * @reason Fixes TreeSprouter crash when handling blocks with incorrect drop quantities
     *         that would otherwise cause NullPointerException or ClassCastException
     */
    @Overwrite(remap = false)
    public static List<ItemStack> getIngredients(WorldSceneRenderer renderer) {
        List<ItemStack> ingredients = new ArrayList<>();
        for (long renderedBlock : renderer.renderedBlocks) {
            int x = CoordinatePacker.unpackX(renderedBlock);
            int y = CoordinatePacker.unpackY(renderedBlock);
            int z = CoordinatePacker.unpackZ(renderedBlock);
            Block block = renderer.world.getBlock(x, y, z);
            if (block.equals(Blocks.air)) continue;
            int meta = renderer.world.getBlockMetadata(x, y, z);
            int qty = block.quantityDropped(renderer.world.rand);
            ArrayList<ItemStack> itemStacks = new ArrayList<>();
            if (qty != 1) {
                itemStacks.add(new ItemStack(block));
            } else {
                itemStacks = block.getDrops(renderer.world, x, y, z, meta, 0);
            }

            if (itemStacks.isEmpty()) {
                itemStacks.add(new ItemStack(block));
            }

            boolean added = false;
            for (ItemStack ingredient : ingredients) {
                if (NEIClientUtils.areStacksSameTypeWithNBT(ingredient, itemStacks.get(0))) {
                    ingredient.stackSize++;
                    added = true;
                    break;
                }
            }
            if (!added) ingredients.add(itemStacks.get(0));
        }

        return ingredients;
    }
}
