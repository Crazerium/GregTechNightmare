package com.EvgenWarGold.GregTechNightmare.Utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

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

    @SafeVarargs
    public static <T> T[] toArray(T... values) {
        return values;
    }

    public static FluidStack copyAmount(int amount, FluidStack fluid) {
        if (fluid == null) {
            return null;
        }

        FluidStack copyFluid = fluid.copy();
        copyFluid.amount = amount;
        return copyFluid;
    }

    public static ItemStack copyAmount(int count, ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        ItemStack copyItemStack = itemStack.copy();
        copyItemStack.stackSize = count;
        return copyItemStack;
    }
}
