package com.EvgenWarGold.GregTechNightmare.GregTech.Items;

import static com.EvgenWarGold.GregTechNightmare.GregTech.Items.GTN_Items.LINK_TOOL;
import static com.EvgenWarGold.GregTechNightmare.GregTech.Items.GTN_Items.META_ITEM_01;

import net.minecraft.item.Item;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;

import cpw.mods.fml.common.registry.GameRegistry;

public class GTN_ItemsRegister {

    public static void init() {
        registryItems();
        registryItemContainers();
    }

    private static void registryItems() {
        Item[] itemsToReg = { META_ITEM_01, LINK_TOOL };

        for (Item item : itemsToReg) {
            GameRegistry.registerItem(item, item.unlocalizedName);
        }
    }

    private static void registryItemContainers() {
        // spotless:off

        // Test Item
        GTN_ItemList.TestItem.set(META_ITEM_01.registerVariantWithTooltips(
            0,
            new String[]{
                "Test"
            }));

        // Advanced Clay
        GTN_ItemList.AdvancedClay.set(META_ITEM_01.registerVariant(1));

        // MeteorMinerSchematic1
        GTN_ItemList.MeteorMinerSchematic1.set(META_ITEM_01.registerVariant(2));

        // MeteorMinerSchematic2
        GTN_ItemList.MeteorMinerSchematic2.set(META_ITEM_01.registerVariant(3));

        // spotless:on
    }
}
