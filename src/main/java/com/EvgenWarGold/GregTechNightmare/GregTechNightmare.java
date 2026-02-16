package com.EvgenWarGold.GregTechNightmare;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(
    modid = GregTechNightmare.MOD_ID,
    version = Tags.VERSION,
    name = GregTechNightmare.MOD_NAME,
    dependencies = "required-after:gregtech;" + "required-after:bartworks;" + "required-after:gtnhintergalactic;",
    acceptedMinecraftVersions = "[1.7.10]")
public class GregTechNightmare {

    public static final String MOD_ID = "GregTechNightmare";
    public static final String MOD_NAME = "GregTechNightmare";
    public static final String RESOURCE_ROOT_ID = "gregtechnightmare";
    public static final Logger LOG = LogManager.getLogger(MOD_ID);

    @SidedProxy(
        clientSide = "com.EvgenWarGold.GregTechNightmare.ClientProxy",
        serverSide = "com.EvgenWarGold.GregTechNightmare.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
