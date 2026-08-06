package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import java.lang.reflect.Method;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;

public final class AEPatternStackAccess {

    private AEPatternStackAccess() {}

    public static IAEStack[] getInputs(ICraftingPatternDetails details) {
        return read(details, "getAEInputs", details == null ? null : details.getInputs());
    }

    public static IAEStack[] getOutputs(ICraftingPatternDetails details) {
        return read(details, "getAEOutputs", details == null ? null : details.getOutputs());
    }

    public static IAEStack[] copy(IAEStack[] source) {
        if (source == null) return new IAEStack[0];

        IAEStack[] copy = new IAEStack[source.length];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i] == null ? null : source[i].copy();
        }
        return copy;
    }

    private static IAEStack[] read(ICraftingPatternDetails details, String methodName, IAEItemStack[] fallback) {
        if (details == null) return new IAEStack[0];

        try {
            Method method = findMethod(details.getClass(), methodName);
            if (method != null) {
                method.setAccessible(true);
                Object value = method.invoke(details);
                if (value instanceof IAEStack[]) return copy((IAEStack[]) value);
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {}

        return copy(fallback);
    }

    private static Method findMethod(Class<?> type, String name) {
        try {
            return type.getMethod(name);
        } catch (NoSuchMethodException ignored) {
            // AE2FC implementations can be package-private, so declared methods must also be checked.
        }

        for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
            try {
                return current.getDeclaredMethod(name);
            } catch (NoSuchMethodException ignored) {}
        }
        return null;
    }
}
