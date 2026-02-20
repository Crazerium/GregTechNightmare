package com.EvgenWarGold.GregTechNightmare.Utils;

import net.minecraft.util.StatCollector;

public class GTN_Utils {

    public static String tr(String key) {
        return StatCollector.translateToLocal(key);
    }

    public static String tr(String key, Object... formatted) {
        return StatCollector.translateToLocalFormatted(key, formatted);
    }
}
