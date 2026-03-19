package com.EvgenWarGold.GregTechNightmare.GregTech.Items;

import net.minecraft.item.Item;

import com.EvgenWarGold.GregTechNightmare.GregTechNightmare;

public class GTN_Items {

    public static final MetaItem01 META_ITEM_01 = new MetaItem01();
    public static final Item LINK_TOOL = new ItemStructuresLinkTool()
        .setTextureName(GregTechNightmare.RESOURCE_ROOT_ID + ":StructuresLinkTool");
}
