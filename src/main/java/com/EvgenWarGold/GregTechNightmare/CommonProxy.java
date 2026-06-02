package com.EvgenWarGold.GregTechNightmare;

import com.EvgenWarGold.GregTechNightmare.GregTech.Blocks.GTN_BlocksRegister;
import com.EvgenWarGold.GregTechNightmare.GregTech.Items.GTN_ItemsRegister;
import com.EvgenWarGold.GregTechNightmare.GregTech.MachineLoader;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeLoader;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeResult.RecipeResultRegisters;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        GTN_ItemsRegister.init();
        GTN_BlocksRegister.init();
    }

    public void init(FMLInitializationEvent event) {
        MachineLoader.init();
        RecipeResultRegisters.init();
    }

    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}

    public void complete(FMLLoadCompleteEvent event) {
        RecipeLoader.init();
    }
}
