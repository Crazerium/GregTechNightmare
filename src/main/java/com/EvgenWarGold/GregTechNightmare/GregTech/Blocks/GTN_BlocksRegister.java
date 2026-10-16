package com.EvgenWarGold.GregTechNightmare.GregTech.Blocks;

import static com.EvgenWarGold.GregTechNightmare.GregTech.Blocks.GTN_Blocks.FLUX_MANA_POOL;
import static com.EvgenWarGold.GregTechNightmare.GregTech.Blocks.GTN_Blocks.MATRIX_CASING;
import static com.EvgenWarGold.GregTechNightmare.GregTech.Blocks.GTN_Blocks.META_BLOCK_CASING_01;

import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;
import com.EvgenWarGold.GregTechNightmare.GregTech.Items.MetaBlockItem;
import com.EvgenWarGold.GregTechNightmare.GregTech.Mana.TileFluxManaPool;

import cpw.mods.fml.common.registry.GameRegistry;

public class GTN_BlocksRegister {

    public static void init() {
        registryBlocks();
        registryBlockContainers();
    }

    private static void registryBlocks() {
        GameRegistry.registerBlock(META_BLOCK_CASING_01, MetaBlockItem.class, META_BLOCK_CASING_01.unlocalizedName);
        GameRegistry.registerBlock(MATRIX_CASING, MetaBlockItem.class, MATRIX_CASING.unlocalizedName);
        GameRegistry.registerBlock(FLUX_MANA_POOL, "GTN_FluxManaPool");
        GameRegistry.registerTileEntity(TileFluxManaPool.class, "GTN_FluxManaPool");
    }

    private static void registryBlockContainers() {
        GTN_ItemList.TestCasing.set(META_BLOCK_CASING_01.registerVariant(0));
        GTN_ItemList.CokeOvenCasing.set(META_BLOCK_CASING_01.registerVariant(1));
        GTN_ItemList.ArborealCasing.set(META_BLOCK_CASING_01.registerVariant(2));
        GTN_ItemList.SoulCasing.set(META_BLOCK_CASING_01.registerVariant(3));
        GTN_ItemList.RunicMatrixCasing.set(MATRIX_CASING.registerVariant(0));
        GTN_ItemList.FluxManaPool.set(new ItemStack(FLUX_MANA_POOL));
    }
}
