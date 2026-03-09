package com.EvgenWarGold.GregTechNightmare;

import com.EvgenWarGold.GregTechNightmare.Utils.BlockHighlighter;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.

    @SideOnly(Side.CLIENT)
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        MinecraftForge.EVENT_BUS.register(new BlockHighlighter.EventHandler());
    }
}
