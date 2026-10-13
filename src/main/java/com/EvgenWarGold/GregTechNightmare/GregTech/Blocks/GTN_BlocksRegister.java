package com.EvgenWarGold.GregTechNightmare.GregTech.Blocks;

import static com.EvgenWarGold.GregTechNightmare.GregTech.Blocks.GTN_Blocks.MATRIX_CASING;
import static com.EvgenWarGold.GregTechNightmare.GregTech.Blocks.GTN_Blocks.META_BLOCK_CASING_01;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;
import com.EvgenWarGold.GregTechNightmare.GregTech.Items.MetaBlockItem;

import cpw.mods.fml.common.registry.GameRegistry;

public class GTN_BlocksRegister {

    public static void init() {
        registryBlocks();
        registryBlockContainers();
    }

    private static void registryBlocks() {
        GameRegistry.registerBlock(META_BLOCK_CASING_01, MetaBlockItem.class, META_BLOCK_CASING_01.unlocalizedName);
        GameRegistry.registerBlock(MATRIX_CASING, MetaBlockItem.class, MATRIX_CASING.unlocalizedName);
    }

    private static void registryBlockContainers() {
        GTN_ItemList.TestCasing.set(META_BLOCK_CASING_01.registerVariant(0));
        GTN_ItemList.CokeOvenCasing.set(META_BLOCK_CASING_01.registerVariant(1));
        GTN_ItemList.ArborealCasing.set(META_BLOCK_CASING_01.registerVariant(2));
        GTN_ItemList.SoulCasing.set(META_BLOCK_CASING_01.registerVariant(3));
        GTN_ItemList.RunicMatrixCasing.set(MATRIX_CASING.registerVariant(0));
    }
}
