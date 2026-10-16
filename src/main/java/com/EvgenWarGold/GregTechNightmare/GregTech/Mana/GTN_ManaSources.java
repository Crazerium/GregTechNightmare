package com.EvgenWarGold.GregTechNightmare.GregTech.Mana;

import java.util.ArrayList;
import java.util.List;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_MagicCraftingInputHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_ManaHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;

import appeng.api.storage.IMEMonitor;
import gregtech.common.tileentities.machines.IDualInputHatch;

/**
 * Mana of a machine from its Magic Crafting Input Hatches: first the mana pushed with the patterns (hatch buffer),
 * then the mana of the ME networks of the hatches.
 */
public final class GTN_ManaSources {

    private GTN_ManaSources() {}

    private static final class Sources {

        final List<GTN_MagicCraftingInputHatch> hatches = new ArrayList<>();
        /** One entry per ME network, so several hatches on the same network do not count its mana twice. */
        final List<GTN_MagicCraftingInputHatch> networkHatches = new ArrayList<>();
        final List<IMEMonitor<AEManaStack>> monitors = new ArrayList<>();
    }

    public static long getAvailable(GTN_MultiBlockBase<?> machine) {
        Sources sources = findSources(machine);
        long total = 0;
        for (GTN_MagicCraftingInputHatch hatch : sources.hatches) total += hatch.getStoredMana();
        for (IMEMonitor<AEManaStack> monitor : sources.monitors) {
            total += GTN_MagicCraftingInputHatch.getManaAmount(monitor);
        }
        return total;
    }

    /** Takes up to {@code amount} mana, returns how much was taken. */
    public static long consume(GTN_MultiBlockBase<?> machine, long amount) {
        Sources sources = findSources(machine);
        long missing = amount;
        for (GTN_MagicCraftingInputHatch hatch : sources.hatches) {
            if (missing <= 0) break;
            missing -= hatch.takeStoredMana(missing);
        }
        for (int i = 0; i < sources.monitors.size() && missing > 0; i++) {
            missing -= sources.networkHatches.get(i)
                .extractMana(sources.monitors.get(i), missing);
        }
        return amount - missing;
    }

    /** Mana of the Mana Hatches and the Magic Crafting Input Hatches of the machine. */
    public static long getTotalAvailable(GTN_MultiBlockBase<?> machine) {
        long total = getAvailable(machine);
        for (GTN_ManaHatch hatch : machine.manaHatches) total += hatch.getCurrentMana();
        return total;
    }

    /** Takes {@code amount} mana from the Mana Hatches first, nothing is taken if there is not enough. */
    public static boolean consumeTotal(GTN_MultiBlockBase<?> machine, long amount) {
        if (amount <= 0) return true;
        if (getTotalAvailable(machine) < amount) return false;
        long missing = amount;
        for (GTN_ManaHatch hatch : machine.manaHatches) {
            if (missing <= 0) break;
            int take = (int) Math.min(hatch.getCurrentMana(), missing);
            if (take <= 0) continue;
            hatch.extractMana(take, false);
            missing -= take;
        }
        if (missing > 0) missing -= consume(machine, missing);
        return missing <= 0;
    }

    private static Sources findSources(GTN_MultiBlockBase<?> machine) {
        Sources sources = new Sources();
        for (IDualInputHatch dualInput : machine.mDualInputHatches) {
            if (!(dualInput instanceof GTN_MagicCraftingInputHatch hatch)) continue;
            if (hatch.getBaseMetaTileEntity() == null) continue;
            sources.hatches.add(hatch);
            IMEMonitor<AEManaStack> monitor = hatch.getManaMonitor();
            if (monitor != null && !sources.monitors.contains(monitor)) {
                sources.monitors.add(monitor);
                sources.networkHatches.add(hatch);
            }
        }
        return sources;
    }
}
