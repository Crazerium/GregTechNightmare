package com.EvgenWarGold.GregTechNightmare.Tooltips;

import net.minecraftforge.common.MinecraftForge;

import com.slprime.chromatictooltips.TooltipRegistry;

public class TooltipsLoader {

    public static void init() {
        DebugEnricher debugEnricher = new DebugEnricher();

        TooltipRegistry.addEnricher(debugEnricher);
        MinecraftForge.EVENT_BUS.register(debugEnricher);
    }
}
