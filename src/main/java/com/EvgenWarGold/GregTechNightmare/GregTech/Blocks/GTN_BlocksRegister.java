package com.EvgenWarGold.GregTechNightmare.GregTech.Blocks;

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
    }

    private static void registryBlockContainers() {
        GTN_ItemList.TestCasing.set(META_BLOCK_CASING_01.registerVariant(0));
    }
}
