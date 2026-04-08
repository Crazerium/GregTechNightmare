package com.EvgenWarGold.GregTechNightmare;

import net.minecraftforge.common.MinecraftForge;

import com.EvgenWarGold.GregTechNightmare.Event.WelcomeMessageEvent;
import com.EvgenWarGold.GregTechNightmare.GregTech.Items.ItemStructuresLinkTool;
import com.EvgenWarGold.GregTechNightmare.Utils.BlockHighlighter;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy {

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.

    @SideOnly(Side.CLIENT)
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        MinecraftForge.EVENT_BUS.register(new BlockHighlighter.EventHandler());
        MinecraftForge.EVENT_BUS.register(new ItemStructuresLinkTool());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        FMLCommonHandler.instance()
            .bus()
            .register(new WelcomeMessageEvent());
    }
}
