package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Example.GTN_TestMultiBlock;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.STEAM.GTN_AdvancedBBF;

public final class MultiBlockLoader {

    public static void init() {
        GTN_ItemList.TestMultiBlock.set(new GTN_TestMultiBlock(20_000, "Test"));
        GTN_ItemList.AdvancedBBF.set(new GTN_AdvancedBBF(20_001, "AdvancedBBF"));
    }
}
