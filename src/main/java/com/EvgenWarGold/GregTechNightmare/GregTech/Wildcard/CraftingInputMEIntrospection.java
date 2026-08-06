package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

public final class CraftingInputMEIntrospection {

    private static final int MAX_DEPTH = 4;
    private static final String[] PATTERN_METHOD_NAMES = { "getPatterns", "getPatternDetails", "getAvailablePatterns",
        "readPatterns", "getPatternInventory", "getPatternItems", "getPatternsForCrafting" };
    private static final Map<Class<?>, List<Field>> MAP_FIELDS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Field> PATTERN_SLOT_MAP_FIELDS = new ConcurrentHashMap<>();

    private CraftingInputMEIntrospection() {}

    public static List<ICraftingPatternDetails> readPatterns(Object host) {
        Map<ICraftingPatternDetails, Boolean> unique = new IdentityHashMap<>();
        Map<Object, Boolean> visited = new IdentityHashMap<>();
        List<ICraftingPatternDetails> result = new ArrayList<>();
        World world = findWorld(host);

        Class<?> type = host.getClass();
        while (type != null && type != Object.class) {
            readMethods(host, type, world, result, unique, visited);
            readFields(host, type, world, result, unique, visited);
            type = type.getSuperclass();
        }
        return result;
    }

    public static boolean hasLivePatternSlot(Object host, ICraftingPatternDetails source) {
        if (host == null || source == null) return false;

        PatternSlotLookup lookup = findPatternSlot(host, source);
        return lookup.slot != null || !lookup.foundCandidateMap;
    }

    public static Object findLivePatternSlot(Object host, ICraftingPatternDetails source) {
        if (host == null || source == null) return null;
        return findPatternSlot(host, source).slot;
    }

    private static PatternSlotLookup findPatternSlot(Object host, ICraftingPatternDetails source) {
        Class<?> hostType = host.getClass();
        Field cachedField = PATTERN_SLOT_MAP_FIELDS.get(hostType);
        if (cachedField != null) return readPatternSlotMap(host, source, cachedField);

        for (Field field : getMapFields(hostType)) {
            try {
                Object value = field.get(host);
                if (!(value instanceof Map<?, ?>)) continue;

                Map<?, ?> map = (Map<?, ?>) value;
                if (!isPatternSlotMap(field, map)) continue;
                PATTERN_SLOT_MAP_FIELDS.putIfAbsent(hostType, field);
                return new PatternSlotLookup(true, map.get(source));
            } catch (ReflectiveOperationException | RuntimeException ignored) {}
        }
        return new PatternSlotLookup(false, null);
    }

    private static PatternSlotLookup readPatternSlotMap(Object host, ICraftingPatternDetails source, Field field) {
        try {
            Object value = field.get(host);
            if (!(value instanceof Map<?, ?>)) return new PatternSlotLookup(false, null);
            Map<?, ?> map = (Map<?, ?>) value;
            return new PatternSlotLookup(true, map.get(source));
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return new PatternSlotLookup(false, null);
        }
    }

    private static List<Field> getMapFields(Class<?> hostType) {
        List<Field> fields = MAP_FIELDS.get(hostType);
        if (fields != null) return fields;

        List<Field> resolved = new ArrayList<>();
        Class<?> type = hostType;
        while (type != null && type != Object.class) {
            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || !Map.class.isAssignableFrom(field.getType())) continue;
                try {
                    field.setAccessible(true);
                    resolved.add(field);
                } catch (RuntimeException ignored) {}
            }
            type = type.getSuperclass();
        }

        List<Field> immutable = Collections.unmodifiableList(resolved);
        List<Field> existing = MAP_FIELDS.putIfAbsent(hostType, immutable);
        return existing == null ? immutable : existing;
    }

    private static final class PatternSlotLookup {

        private final boolean foundCandidateMap;
        private final Object slot;

        private PatternSlotLookup(boolean foundCandidateMap, Object slot) {
            this.foundCandidateMap = foundCandidateMap;
            this.slot = slot;
        }
    }

    private static boolean isPatternSlotMap(Field field, Map<?, ?> map) {
        String fieldName = field.getName()
            .toLowerCase(Locale.ROOT);
        if (fieldName.contains("pattern") && (fieldName.contains("slot") || fieldName.contains("map"))) return true;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() instanceof ICraftingPatternDetails) return true;
            Object value = entry.getValue();
            if (value != null && value.getClass()
                .getSimpleName()
                .toLowerCase(Locale.ROOT)
                .contains("patternslot")) {
                return true;
            }
        }
        return false;
    }

    private static void readMethods(Object host, Class<?> type, World world, List<ICraftingPatternDetails> result,
        Map<ICraftingPatternDetails, Boolean> unique, Map<Object, Boolean> visited) {
        for (Method method : type.getDeclaredMethods()) {
            if (method.getParameterTypes().length != 0 || !isPatternMethodName(method.getName())) continue;
            try {
                method.setAccessible(true);
                collect(method.invoke(host), world, result, unique, visited, 0);
            } catch (ReflectiveOperationException | RuntimeException ignored) {

            }
        }
    }

    private static void readFields(Object host, Class<?> type, World world, List<ICraftingPatternDetails> result,
        Map<ICraftingPatternDetails, Boolean> unique, Map<Object, Boolean> visited) {
        for (Field field : type.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || !isInterestingField(field)) continue;
            try {
                field.setAccessible(true);
                collect(field.get(host), world, result, unique, visited, 0);
            } catch (ReflectiveOperationException | RuntimeException ignored) {

            }
        }
    }

    private static void collect(Object value, World world, List<ICraftingPatternDetails> result,
        Map<ICraftingPatternDetails, Boolean> unique, Map<Object, Boolean> visited, int depth) {
        if (value == null || depth > MAX_DEPTH) return;

        if (value instanceof ICraftingPatternDetails) {
            add((ICraftingPatternDetails) value, result, unique);
            return;
        }
        if (value instanceof ItemStack) {
            decode((ItemStack) value, world, result, unique);
            return;
        }
        if (value instanceof IInventory) {
            if (visited.put(value, Boolean.TRUE) != null) return;
            IInventory inventory = (IInventory) value;
            for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
                collect(inventory.getStackInSlot(slot), world, result, unique, visited, depth + 1);
            }
            return;
        }
        if (value instanceof Map<?, ?>) {
            if (visited.put(value, Boolean.TRUE) != null) return;
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                collect(entry.getKey(), world, result, unique, visited, depth + 1);
                collect(entry.getValue(), world, result, unique, visited, depth + 1);
            }
            return;
        }
        if (value instanceof Collection<?>) {
            if (visited.put(value, Boolean.TRUE) != null) return;
            for (Object element : (Collection<?>) value) {
                collect(element, world, result, unique, visited, depth + 1);
            }
            return;
        }
        if (value instanceof Optional<?>) {
            collect(((Optional<?>) value).orElse(null), world, result, unique, visited, depth + 1);
            return;
        }
        if (value.getClass()
            .isArray()) {
            if (visited.put(value, Boolean.TRUE) != null) return;
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                collect(Array.get(value, i), world, result, unique, visited, depth + 1);
            }
            return;
        }

        if (!shouldInspectObject(value.getClass()) || visited.put(value, Boolean.TRUE) != null) return;
        Class<?> type = value.getClass();
        while (type != null && type != Object.class) {
            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || !isInterestingField(field)) continue;
                try {
                    field.setAccessible(true);
                    collect(field.get(value), world, result, unique, visited, depth + 1);
                } catch (ReflectiveOperationException | RuntimeException ignored) {}
            }
            type = type.getSuperclass();
        }
    }

    private static boolean isInterestingField(Field field) {
        String name = field.getName()
            .toLowerCase(Locale.ROOT);
        Class<?> fieldType = field.getType();
        return name.contains("pattern") || name.contains("inventory")
            || name.contains("slot")
            || name.contains("detail")
            || ICraftingPatternDetails.class.isAssignableFrom(fieldType)
            || ItemStack.class.isAssignableFrom(fieldType)
            || IInventory.class.isAssignableFrom(fieldType)
            || Map.class.isAssignableFrom(fieldType)
            || Collection.class.isAssignableFrom(fieldType)
            || Optional.class.isAssignableFrom(fieldType)
            || fieldType.isArray();
    }

    private static boolean shouldInspectObject(Class<?> type) {
        String name = type.getName();
        String simple = type.getSimpleName()
            .toLowerCase(Locale.ROOT);
        return name.startsWith("gregtech.") || name.startsWith("appeng.")
            || simple.contains("pattern")
            || simple.contains("slot")
            || simple.contains("inventory");
    }

    private static void decode(ItemStack stack, World world, List<ICraftingPatternDetails> result,
        Map<ICraftingPatternDetails, Boolean> unique) {
        if (stack == null || world == null || !(stack.getItem() instanceof ICraftingPatternItem)) return;
        ICraftingPatternDetails details = ((ICraftingPatternItem) stack.getItem()).getPatternForItem(stack, world);
        if (details != null) add(details, result, unique);
    }

    private static void add(ICraftingPatternDetails details, List<ICraftingPatternDetails> result,
        Map<ICraftingPatternDetails, Boolean> unique) {
        if (!unique.containsKey(details)) {
            unique.put(details, Boolean.TRUE);
            result.add(details);
        }
    }

    private static World findWorld(Object host) {
        try {
            Method getter = host.getClass()
                .getMethod("getBaseMetaTileEntity");
            Object base = getter.invoke(host);
            if (base instanceof IGregTechTileEntity) return ((IGregTechTileEntity) base).getWorld();
        } catch (ReflectiveOperationException | RuntimeException ignored) {}
        return null;
    }

    private static boolean isPatternMethodName(String name) {
        for (String candidate : PATTERN_METHOD_NAMES) {
            if (candidate.equals(name)) return true;
        }
        return false;
    }
}
