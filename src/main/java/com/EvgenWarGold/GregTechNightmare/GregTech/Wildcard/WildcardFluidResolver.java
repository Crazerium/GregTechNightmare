package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import java.lang.reflect.Method;

import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Materials;

public final class WildcardFluidResolver {

    private WildcardFluidResolver() {}

    public static FluidStack resolve(Materials material, WildcardPrefix.FluidMode mode, long amount) {
        if (material == null || mode == null || mode == WildcardPrefix.FluidMode.NONE || amount <= 0) return null;

        int safeAmount = amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
        if (mode == WildcardPrefix.FluidMode.MOLTEN) {
            FluidStack molten = invoke(material, "getMolten", safeAmount);
            return molten != null ? molten : invoke(material, "getFluid", safeAmount);
        }

        FluidStack fluid = invoke(material, "getFluid", safeAmount);
        if (fluid != null) return fluid;

        fluid = invoke(material, "getGas", safeAmount);
        return fluid != null ? fluid : invoke(material, "getMolten", safeAmount);
    }

    private static FluidStack invoke(Materials material, String methodName, int amount) {
        for (Class<?> type = material.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            for (Method method : type.getDeclaredMethods()) {
                if (!methodName.equals(method.getName()) || method.getParameterTypes().length != 1) continue;

                Object argument = toSupportedAmount(method.getParameterTypes()[0], amount);
                if (argument == null) continue;

                try {
                    method.setAccessible(true);
                    Object result = method.invoke(material, argument);
                    if (result instanceof FluidStack) return ((FluidStack) result).copy();
                } catch (ReflectiveOperationException | RuntimeException ignored) {}
            }
        }
        return null;
    }

    private static Object toSupportedAmount(Class<?> parameter, int amount) {
        if (parameter == long.class || parameter == Long.class) return Long.valueOf(amount);
        if (parameter == int.class || parameter == Integer.class) return Integer.valueOf(amount);
        return null;
    }
}
