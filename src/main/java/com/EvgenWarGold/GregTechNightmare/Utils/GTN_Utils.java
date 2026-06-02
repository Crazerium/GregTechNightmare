package com.EvgenWarGold.GregTechNightmare.Utils;

import net.minecraft.util.StatCollector;

import cpw.mods.fml.common.FMLCommonHandler;

public class GTN_Utils {

    public static String tr(String key) {
        return StatCollector.translateToLocal(key);
    }

    public static String tr(String key, Object... formatted) {
        return StatCollector.translateToLocalFormatted(key, formatted);
    }

    public static boolean isServer() {
        return FMLCommonHandler.instance()
            .getEffectiveSide()
            .isServer();
    }

    public static boolean isClient() {
        return FMLCommonHandler.instance()
            .getEffectiveSide()
            .isClient();
    }
}
