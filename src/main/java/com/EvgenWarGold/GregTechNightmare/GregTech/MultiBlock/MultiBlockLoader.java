package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTNItemList;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Example.GTN_TestMultiBlock;

public final class MultiBlockLoader {

    public static void init() {
        GTNItemList.TestMultiBlock.set(
            new GTN_TestMultiBlock(20_000, "Test"));
    }
}
