package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import gregtech.api.enums.OrePrefixes;

public enum WildcardPrefix {

    // Metadata is persisted in worlds and encoded patterns. Existing entries must never be reordered.
    INGOT("ingot", "ingot"),
    PLATE("plate", "plate"),
    DUST("dust", "dust"),
    STICK("stick", "stick"),
    SCREW("screw", "screw"),
    BOLT("bolt", "bolt"),
    RING("ring", "ring"),
    FOIL("foil", "foil"),
    GEAR("gear", "gearGt"),
    WIRE_1X("wire1x", "wireGt01"),
    CABLE_1X("cable1x", "cableGt01"),
    FRAME("frame", "frameGt"),
    GEM("gem", "gem"),
    BLOCK("block", "block"),
    NUGGET("nugget", "nugget"),
    DENSE_PLATE("dense_plate", "plateDense"),
    HOT_INGOT("hot_ingot", "ingotHot"),
    TINY_DUST("tiny_dust", "dustTiny"),
    SMALL_DUST("small_dust", "dustSmall"),
    CHIPPED_GEM("chipped_gem", "gemChipped"),
    FLAWED_GEM("flawed_gem", "gemFlawed"),
    FLAWLESS_GEM("flawless_gem", "gemFlawless"),
    EXQUISITE_GEM("exquisite_gem", "gemExquisite"),
    DOUBLE_PLATE("double_plate", "plateDouble"),
    TRIPLE_PLATE("triple_plate", "plateTriple"),
    QUADRUPLE_PLATE("quadruple_plate", "plateQuadruple"),
    QUINTUPLE_PLATE("quintuple_plate", "plateQuintuple"),
    ITEM_CASING("item_casing", "itemCasing"),
    LONG_STICK("long_stick", "stickLong"),
    ROUND("round", "round"),
    SMALL_SPRING("small_spring", "springSmall"),
    SPRING("spring", "spring"),
    FINE_WIRE("fine_wire", "wireFine"),
    ROTOR("rotor", "rotor"),
    SMALL_GEAR("small_gear", "gearGtSmall"),
    WIRE_2X("wire2x", "wireGt02"),
    WIRE_4X("wire4x", "wireGt04"),
    WIRE_8X("wire8x", "wireGt08"),
    WIRE_12X("wire12x", "wireGt12"),
    WIRE_16X("wire16x", "wireGt16"),
    CABLE_2X("cable2x", "cableGt02"),
    CABLE_4X("cable4x", "cableGt04"),
    CABLE_8X("cable8x", "cableGt08"),
    CABLE_12X("cable12x", "cableGt12"),
    CABLE_16X("cable16x", "cableGt16"),
    LENS("lens", "lens"),
    TURBINE_BLADE("turbine_blade", "turbineBlade"),
    CELL("cell", "cell"),
    MOLTEN_CELL("molten_cell", "cellMolten"),
    TINY_PIPE("tiny_pipe", "pipeTiny"),
    SMALL_PIPE("small_pipe", "pipeSmall"),
    MEDIUM_PIPE("medium_pipe", "pipeMedium"),
    LARGE_PIPE("large_pipe", "pipeLarge"),
    HUGE_PIPE("huge_pipe", "pipeHuge"),
    QUADRUPLE_PIPE("quadruple_pipe", "pipeQuadruple"),
    NONUPLE_PIPE("nonuple_pipe", "pipeNonuple"),
    TINY_RESTRICTIVE_PIPE("tiny_restrictive_pipe", "pipeRestrictiveTiny"),
    SMALL_RESTRICTIVE_PIPE("small_restrictive_pipe", "pipeRestrictiveSmall"),
    MEDIUM_RESTRICTIVE_PIPE("medium_restrictive_pipe", "pipeRestrictiveMedium"),
    LARGE_RESTRICTIVE_PIPE("large_restrictive_pipe", "pipeRestrictiveLarge"),
    HUGE_RESTRICTIVE_PIPE("huge_restrictive_pipe", "pipeRestrictiveHuge"),
    FLUID("fluid", null, FluidMode.FLUID),
    MOLTEN_FLUID("molten_fluid", null, FluidMode.MOLTEN);

    private static final WildcardPrefix[] VALUES = values();

    private final String serializedName;
    private final String orePrefixName;
    private final FluidMode fluidMode;
    private transient volatile OrePrefixes cachedOrePrefix;
    private transient volatile boolean orePrefixResolved;

    WildcardPrefix(String serializedName, String orePrefixName) {
        this(serializedName, orePrefixName, FluidMode.NONE);
    }

    WildcardPrefix(String serializedName, String orePrefixName, FluidMode fluidMode) {
        this.serializedName = serializedName;
        this.orePrefixName = orePrefixName;
        this.fluidMode = fluidMode;
    }

    public String getSerializedName() {
        return serializedName;
    }

    public String getOrePrefixName() {
        return orePrefixName;
    }

    public OrePrefixes getOrePrefix() {
        if (orePrefixName == null) return null;

        if (!orePrefixResolved) {
            synchronized (this) {
                if (!orePrefixResolved) {
                    cachedOrePrefix = resolveOrePrefix(orePrefixName);
                    orePrefixResolved = true;
                }
            }
        }
        return cachedOrePrefix;
    }

    public boolean isFluid() {
        return fluidMode != FluidMode.NONE;
    }

    public FluidMode getFluidMode() {
        return fluidMode;
    }

    public boolean usesGregTechBlockModel() {
        return getOrePrefix() != null
            && (this == FRAME || serializedName.startsWith("wire") || isCable() || serializedName.contains("pipe"));
    }

    public boolean isCable() {
        return serializedName.startsWith("cable");
    }

    public int getMeta() {
        return ordinal();
    }

    public static WildcardPrefix byMeta(int meta) {
        return meta >= 0 && meta < VALUES.length ? VALUES[meta] : null;
    }

    // OrePrefixes is extensible in GT5U and cannot be resolved with Enum.valueOf.
    private static OrePrefixes resolveOrePrefix(String name) {
        try {
            Field field = OrePrefixes.class.getField(name);
            if (!Modifier.isStatic(field.getModifiers()) || !OrePrefixes.class.isAssignableFrom(field.getType())) {
                return null;
            }

            Object value = field.get(null);
            return value instanceof OrePrefixes ? (OrePrefixes) value : null;
        } catch (ReflectiveOperationException | SecurityException ignored) {
            return null;
        }
    }

    public enum FluidMode {
        NONE,
        FLUID,
        MOLTEN
    }
}
