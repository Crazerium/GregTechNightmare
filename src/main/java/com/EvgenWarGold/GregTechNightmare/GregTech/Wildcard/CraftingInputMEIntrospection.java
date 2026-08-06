package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.Map;

import appeng.api.networking.crafting.ICraftingPatternDetails;

public final class CraftingInputMEIntrospection {

    private CraftingInputMEIntrospection() {}

    public static boolean hasLivePatternSlot(Object host, ICraftingPatternDetails source) {
        if (host == null || source == null) return false;

        PatternSlotLookup lookup = findPatternSlot(host, source);
        // Unknown layouts fail open; a recognized map without the source means the pattern was removed.
        return lookup.slot != null || !lookup.foundCandidateMap;
    }

    public static Object findLivePatternSlot(Object host, ICraftingPatternDetails source) {
        if (host == null || source == null) return null;
        return findPatternSlot(host, source).slot;
    }

    private static PatternSlotLookup findPatternSlot(Object host, ICraftingPatternDetails source) {
        boolean foundCandidateMap = false;

        for (Class<?> type = host.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || !Map.class.isAssignableFrom(field.getType())) continue;

                try {
                    field.setAccessible(true);
                    Object value = field.get(host);
                    if (!(value instanceof Map<?, ?>)) continue;

                    Map<?, ?> map = (Map<?, ?>) value;
                    if (!isPatternSlotMap(field, map)) continue;

                    foundCandidateMap = true;
                    if (map.containsKey(source)) return new PatternSlotLookup(true, map.get(source));
                } catch (ReflectiveOperationException | RuntimeException ignored) {}
            }
        }
        return new PatternSlotLookup(foundCandidateMap, null);
    }

    private static boolean isPatternSlotMap(Field field, Map<?, ?> map) {
        String fieldName = field.getName().toLowerCase(Locale.ROOT);
        if (fieldName.contains("pattern") && (fieldName.contains("slot") || fieldName.contains("map"))) return true;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() instanceof ICraftingPatternDetails) return true;

            Object value = entry.getValue();
            if (value != null && value.getClass().getSimpleName().toLowerCase(Locale.ROOT).contains("patternslot")) {
                return true;
            }
        }
        return false;
    }

    private static final class PatternSlotLookup {

        private final boolean foundCandidateMap;
        private final Object slot;

        private PatternSlotLookup(boolean foundCandidateMap, Object slot) {
            this.foundCandidateMap = foundCandidateMap;
            this.slot = slot;
        }
    }
}
