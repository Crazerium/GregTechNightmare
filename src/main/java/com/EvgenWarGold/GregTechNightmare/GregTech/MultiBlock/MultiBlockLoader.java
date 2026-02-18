package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Example.GTN_TestMultiBlock;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.MV.GTN_MediumPowerBender;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.STEAM.GTN_AdvancedBBF;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.STEAM.GTN_BronzeVoidMiner;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.LV.GTN_LowPowerVoidMiner;

public final class MultiBlockLoader {

    public static void init() {
        //spotless:off
        GTN_ItemList.TestMultiBlock.set(new GTN_TestMultiBlock(20_000, "Test"));
        GTN_ItemList.AdvancedBBF.set(new GTN_AdvancedBBF(20_001, "AdvancedBBF"));
        GTN_ItemList.BronzeVoidMiner.set(new GTN_BronzeVoidMiner(20_002, "BronzeVoidMiner"));
        GTN_ItemList.LowPowerVoidMiner.set(new GTN_LowPowerVoidMiner(20_003, "LowPowerVoidMiner"));
        GTN_ItemList.MediumPowerBender.set(new GTN_MediumPowerBender(20_004, "MediumPowerBender"));
        //spotless:on
    }
}
