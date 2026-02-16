package com.EvgenWarGold.GregTechNightmare.GregTech.Blocks;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTNItemList;
import com.EvgenWarGold.GregTechNightmare.GregTech.Items.GtnMetaBlockItem;
import cpw.mods.fml.common.registry.GameRegistry;

import static com.EvgenWarGold.GregTechNightmare.GregTech.Blocks.GTNBlocks.META_BLOCK_CASING_01;

public class GTNBlocksRegister {

    public static void init() {
        registryBlocks();
        registryBlockContainers();
    }

    private static void registryBlocks() {
        GameRegistry.registerBlock(META_BLOCK_CASING_01, GtnMetaBlockItem.class, META_BLOCK_CASING_01.unlocalizedName);
    }

    private static void registryBlockContainers() {
        GTNItemList.TestCasing.set(META_BLOCK_CASING_01.registerVariant(0));
    }
}
