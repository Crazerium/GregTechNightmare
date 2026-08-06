package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Materials;

public final class WildcardFluidResolver {

    private static final Map<Materials, EnumMap<WildcardPrefix.FluidMode, FluidStack>> RESOLVED_FLUIDS = new IdentityHashMap<>();
    private static final Map<Materials, EnumSet<WildcardPrefix.FluidMode>> MISSING_FLUIDS = new IdentityHashMap<>();
    private static final Map<Class<?>, Map<String, MethodInvoker>> INVOKERS = new HashMap<>();
    private static final MethodInvoker MISSING_INVOKER = new MethodInvoker(null, false);

    private WildcardFluidResolver() {}

    public static FluidStack resolve(Materials material, WildcardPrefix.FluidMode mode, long amount) {
        if (material == null || mode == null || mode == WildcardPrefix.FluidMode.NONE || amount <= 0) return null;

        FluidStack template;
        synchronized (RESOLVED_FLUIDS) {
            EnumMap<WildcardPrefix.FluidMode, FluidStack> byMode = RESOLVED_FLUIDS.get(material);
            template = byMode == null ? null : byMode.get(mode);
            if (template == null) {
                EnumSet<WildcardPrefix.FluidMode> missing = MISSING_FLUIDS.get(material);
                if (missing != null && missing.contains(mode)) return null;
            }
        }

        if (template == null) {
            template = resolveTemplate(material, mode);
            synchronized (RESOLVED_FLUIDS) {
                if (template == null || template.getFluid() == null) {
                    EnumSet<WildcardPrefix.FluidMode> missing = MISSING_FLUIDS.get(material);
                    if (missing == null) {
                        missing = EnumSet.noneOf(WildcardPrefix.FluidMode.class);
                        MISSING_FLUIDS.put(material, missing);
                    }
                    missing.add(mode);
                    return null;
                }

                EnumMap<WildcardPrefix.FluidMode, FluidStack> byMode = RESOLVED_FLUIDS.get(material);
                if (byMode == null) {
                    byMode = new EnumMap<>(WildcardPrefix.FluidMode.class);
                    RESOLVED_FLUIDS.put(material, byMode);
                }
                byMode.put(mode, template.copy());
            }
        }

        FluidStack result = template.copy();
        result.amount = amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
        return result;
    }

    private static FluidStack resolveTemplate(Materials material, WildcardPrefix.FluidMode mode) {
        if (mode == WildcardPrefix.FluidMode.MOLTEN) {
            FluidStack molten = invoke(material, "getMolten");
            return molten != null ? molten : invoke(material, "getFluid");
        }

        FluidStack fluid = invoke(material, "getFluid");
        if (fluid != null) return fluid;
        fluid = invoke(material, "getGas");
        return fluid != null ? fluid : invoke(material, "getMolten");
    }

    private static FluidStack invoke(Materials material, String methodName) {
        MethodInvoker invoker = getInvoker(material.getClass(), methodName);
        if (invoker == MISSING_INVOKER) return null;

        try {
            Object amount = invoker.usesLong ? Long.valueOf(1L) : Integer.valueOf(1);
            Object result = invoker.method.invoke(material, amount);
            return result instanceof FluidStack ? ((FluidStack) result).copy() : null;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    private static MethodInvoker getInvoker(Class<?> type, String methodName) {
        synchronized (INVOKERS) {
            Map<String, MethodInvoker> byName = INVOKERS.get(type);
            if (byName != null && byName.containsKey(methodName)) return byName.get(methodName);
        }

        MethodInvoker resolved = findInvoker(type, methodName);
        synchronized (INVOKERS) {
            Map<String, MethodInvoker> byName = INVOKERS.get(type);
            if (byName == null) {
                byName = new HashMap<>();
                INVOKERS.put(type, byName);
            }
            byName.put(methodName, resolved);
        }
        return resolved;
    }

    private static MethodInvoker findInvoker(Class<?> type, String methodName) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            for (Method method : current.getDeclaredMethods()) {
                if (!methodName.equals(method.getName()) || method.getParameterTypes().length != 1) continue;
                Class<?> parameter = method.getParameterTypes()[0];
                boolean usesLong = parameter == long.class || parameter == Long.class;
                if (!usesLong && parameter != int.class && parameter != Integer.class) continue;
                try {
                    method.setAccessible(true);
                    return new MethodInvoker(method, usesLong);
                } catch (RuntimeException ignored) {}
            }
            current = current.getSuperclass();
        }
        return MISSING_INVOKER;
    }

    private static final class MethodInvoker {

        private final Method method;
        private final boolean usesLong;

        private MethodInvoker(Method method, boolean usesLong) {
            this.method = method;
            this.usesLong = usesLong;
        }
    }
}
