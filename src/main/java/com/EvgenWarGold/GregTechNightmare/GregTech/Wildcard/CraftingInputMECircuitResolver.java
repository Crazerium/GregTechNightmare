package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import gregtech.api.util.GTUtility;

public final class CraftingInputMECircuitResolver {

    private static final int MAX_DEPTH = 5;
    private static final int MAX_SLOTS = 256;
    private static final int MAX_CIRCUIT_CONFIG = 32;

    private CraftingInputMECircuitResolver() {}

    public static ItemStack find(Object host, Object patternSlot) {
        ItemStack circuit = findNamedStack(host);
        if (circuit != null) return circuit;

        circuit = findNamedStack(patternSlot);
        if (circuit != null) return circuit;

        circuit = findInventoryStack(host);
        if (circuit != null) return circuit;

        circuit = findGenericStack(host);
        if (circuit != null) return circuit;

        circuit = findGenericStack(patternSlot);
        if (circuit != null) return circuit;

        Integer config = findNamedConfig(host);
        if (config == null) config = findNamedConfig(patternSlot);
        return config == null ? null : one(GTUtility.getIntegratedCircuit(config.intValue()));
    }

    public static boolean isIntegratedCircuit(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;

        ItemStack exemplar = GTUtility.getIntegratedCircuit(0);
        if (exemplar != null && exemplar.getItem() == stack.getItem()) return true;

        String name = stack.getUnlocalizedName();
        if (name == null) return false;

        String normalized = name.toLowerCase(Locale.ROOT).replace("_", "").replace(".", "");
        return normalized.contains("integratedcircuit") || normalized.contains("programmedcircuit");
    }

    private static ItemStack findNamedStack(Object root) {
        if (root == null) return null;

        IdentityHashMap<Object, Boolean> visited = new IdentityHashMap<>();
        for (Class<?> type = root.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            for (Method method : type.getDeclaredMethods()) {
                if (Modifier.isStatic(method.getModifiers()) || method.getParameterTypes().length != 0
                    || method.getReturnType() == Void.TYPE || !containsCircuitWord(method.getName())) {
                    continue;
                }

                try {
                    method.setAccessible(true);
                    ItemStack result = findCircuit(method.invoke(root), visited, 0, true);
                    if (result != null) return one(result);
                } catch (ReflectiveOperationException | RuntimeException ignored) {}
            }
        }

        for (Class<?> type = root.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || !containsCircuitWord(field.getName())) continue;

                try {
                    field.setAccessible(true);
                    ItemStack result = findCircuit(field.get(root), visited, 0, true);
                    if (result != null) return one(result);
                } catch (ReflectiveOperationException | RuntimeException ignored) {}
            }
        }
        return null;
    }

    private static ItemStack findInventoryStack(Object root) {
        if (!(root instanceof IInventory)) return null;

        IInventory inventory = (IInventory) root;
        int count;
        try {
            count = Math.min(inventory.getSizeInventory(), MAX_SLOTS);
        } catch (RuntimeException ignored) {
            return null;
        }

        for (int slot = 0; slot < count; slot++) {
            try {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (isIntegratedCircuit(stack)) return one(stack);
            } catch (RuntimeException ignored) {}
        }
        return null;
    }

    private static ItemStack findGenericStack(Object root) {
        if (root == null) return null;

        IdentityHashMap<Object, Boolean> visited = new IdentityHashMap<>();
        for (Class<?> type = root.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;

                String name = field.getName().toLowerCase(Locale.ROOT);
                if (name.contains("pattern") && Map.class.isAssignableFrom(field.getType())) continue;
                if (!isInterestingStorageField(field, name)) continue;

                try {
                    field.setAccessible(true);
                    ItemStack result = findCircuit(field.get(root), visited, 0, false);
                    if (result != null) return one(result);
                } catch (ReflectiveOperationException | RuntimeException ignored) {}
            }
        }
        return null;
    }

    private static boolean isInterestingStorageField(Field field, String name) {
        Class<?> type = field.getType();
        return name.contains("circuit") || name.contains("inventory") || name.contains("slot")
            || name.contains("input") || name.contains("shared") || ItemStack.class.isAssignableFrom(type)
            || type.isArray() || IInventory.class.isAssignableFrom(type) || Collection.class.isAssignableFrom(type);
    }

    private static ItemStack findCircuit(Object value, IdentityHashMap<Object, Boolean> visited, int depth,
        boolean inspectNamedContainers) {
        if (value == null || depth > MAX_DEPTH) return null;
        if (value instanceof ItemStack) return isIntegratedCircuit((ItemStack) value) ? (ItemStack) value : null;
        if (visited.put(value, Boolean.TRUE) != null) return null;

        if (value instanceof IInventory) {
            IInventory inventory = (IInventory) value;
            int count;
            try {
                count = Math.min(inventory.getSizeInventory(), MAX_SLOTS);
            } catch (RuntimeException ignored) {
                return null;
            }

            for (int slot = 0; slot < count; slot++) {
                try {
                    ItemStack result = findCircuit(
                        inventory.getStackInSlot(slot),
                        visited,
                        depth + 1,
                        inspectNamedContainers);
                    if (result != null) return result;
                } catch (RuntimeException ignored) {}
            }
            return null;
        }

        Class<?> valueType = value.getClass();
        if (valueType.isArray()) {
            int length = Math.min(Array.getLength(value), MAX_SLOTS);
            for (int i = 0; i < length; i++) {
                ItemStack result = findCircuit(Array.get(value, i), visited, depth + 1, inspectNamedContainers);
                if (result != null) return result;
            }
            return null;
        }

        if (value instanceof Collection<?>) {
            int visitedElements = 0;
            for (Object element : (Collection<?>) value) {
                if (visitedElements++ >= MAX_SLOTS) break;
                ItemStack result = findCircuit(element, visited, depth + 1, inspectNamedContainers);
                if (result != null) return result;
            }
            return null;
        }

        ItemStack fromHandler = readStackHandler(value, visited, depth, inspectNamedContainers);
        if (fromHandler != null) return fromHandler;

        if (!inspectNamedContainers || !isInspectableContainer(valueType)) return null;

        for (Class<?> type = valueType; type != null && type != Object.class; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;

                String name = field.getName().toLowerCase(Locale.ROOT);
                if (!isInterestingStorageField(field, name)) continue;

                try {
                    field.setAccessible(true);
                    ItemStack result = findCircuit(field.get(value), visited, depth + 1, true);
                    if (result != null) return result;
                } catch (ReflectiveOperationException | RuntimeException ignored) {}
            }
        }
        return null;
    }

    private static ItemStack readStackHandler(Object value, IdentityHashMap<Object, Boolean> visited, int depth,
        boolean inspectNamedContainers) {
        try {
            Method getSlots = value.getClass().getMethod("getSlots");
            Method getStackInSlot = value.getClass().getMethod("getStackInSlot", int.class);
            Object slotCount = getSlots.invoke(value);
            if (!(slotCount instanceof Number)) return null;

            int count = Math.min(((Number) slotCount).intValue(), MAX_SLOTS);
            for (int slot = 0; slot < count; slot++) {
                ItemStack result = findCircuit(
                    getStackInSlot.invoke(value, slot),
                    visited,
                    depth + 1,
                    inspectNamedContainers);
                if (result != null) return result;
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {}
        return null;
    }

    private static Integer findNamedConfig(Object root) {
        if (root == null) return null;

        for (Class<?> type = root.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            for (Method method : type.getDeclaredMethods()) {
                if (Modifier.isStatic(method.getModifiers()) || method.getParameterTypes().length != 0
                    || !containsCircuitWord(method.getName())) {
                    continue;
                }

                try {
                    method.setAccessible(true);
                    Integer result = asConfig(method.invoke(root));
                    if (result != null) return result;
                } catch (ReflectiveOperationException | RuntimeException ignored) {}
            }

            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || !containsCircuitWord(field.getName())) continue;

                try {
                    field.setAccessible(true);
                    Integer result = asConfig(field.get(root));
                    if (result != null) return result;
                } catch (ReflectiveOperationException | RuntimeException ignored) {}
            }
        }
        return null;
    }

    private static Integer asConfig(Object value) {
        if (!(value instanceof Number)) return null;

        int config = ((Number) value).intValue();
        return config >= 0 && config <= MAX_CIRCUIT_CONFIG ? Integer.valueOf(config) : null;
    }

    private static boolean containsCircuitWord(String value) {
        return value != null && value.toLowerCase(Locale.ROOT).contains("circuit");
    }

    private static boolean isInspectableContainer(Class<?> type) {
        String name = type.getName();
        String simpleName = type.getSimpleName().toLowerCase(Locale.ROOT);
        return name.startsWith("gregtech.") || name.startsWith("appeng.") || simpleName.contains("circuit")
            || simpleName.contains("inventory") || simpleName.contains("slot") || simpleName.contains("handler");
    }

    private static ItemStack one(ItemStack stack) {
        if (stack == null) return null;

        ItemStack copy = stack.copy();
        copy.stackSize = 1;
        return copy;
    }
}
