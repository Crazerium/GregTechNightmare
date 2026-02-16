package com.EvgenWarGold.GregTechNightmare.GregTech.Items;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTNItemList;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

import static com.EvgenWarGold.GregTechNightmare.GregTech.Items.GTNItems.META_ITEM_01;

public class GTNItemsRegister {

    public static void init() {
        registryItems();
        registryItemContainers();
    }

    private static void registryItems() {
        Item[] itemsToReg = {
            META_ITEM_01
        };

        for (Item item : itemsToReg) {
            GameRegistry.registerItem(item, item.unlocalizedName);
        }
    }

    private static void registryItemContainers() {
        // spotless:off
        GTNItemList.TestItem.set(META_ITEM_01.registerVariantWithTooltips(
            0,
            new String[]{
                "Test"
            }));
        // spotless:on
    }
}
