package com.EvgenWarGold.GregTechNightmare.GregTech.Thaumcraft;

import java.util.ArrayList;
import java.util.List;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_ThaumicCraftingInputHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;

import appeng.api.storage.IMEMonitor;
import gregtech.common.tileentities.machines.IDualInputHatch;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumicenergistics.common.storage.AEEssentiaStack;

/**
 * Essentia for Thaumcraft machines comes from their Thaumic Crafting Input Hatches: first from the essentia pushed by
 * AE2 with the patterns (hatch buffer), then directly from the ME networks of the hatches.
 */
public final class GTN_EssentiaRecipes {

    private GTN_EssentiaRecipes() {}

    private static final class Sources {

        final List<GTN_ThaumicCraftingInputHatch> hatches = new ArrayList<>();
        /** One entry per ME network, so several hatches on the same network do not count its essentia twice. */
        final List<GTN_ThaumicCraftingInputHatch> networkHatches = new ArrayList<>();
        final List<IMEMonitor<AEEssentiaStack>> monitors = new ArrayList<>();
    }

    public static boolean hasEnough(GTN_MultiBlockBase<?> machine, AspectList cost) {
        if (cost == null || cost.size() == 0) return true;
        return hasEnough(findSources(machine), cost, 1);
    }

    /** How many times the cost can be paid with the essentia available right now. */
    public static int getMaxParallel(GTN_MultiBlockBase<?> machine, AspectList cost) {
        if (cost == null || cost.size() == 0) return Integer.MAX_VALUE;
        Sources sources = findSources(machine);
        long parallel = Integer.MAX_VALUE;
        for (Aspect aspect : cost.getAspects()) {
            int amount = cost.getAmount(aspect);
            if (aspect == null || amount <= 0) continue;
            parallel = Math.min(parallel, getAvailable(sources, aspect) / amount);
        }
        return (int) parallel;
    }

    public static boolean consume(GTN_MultiBlockBase<?> machine, AspectList cost) {
        return consume(machine, cost, 1);
    }

    /** Consumes {@code cost * multiplier} essentia, nothing is taken if there is not enough. */
    public static boolean consume(GTN_MultiBlockBase<?> machine, AspectList cost, int multiplier) {
        if (cost == null || cost.size() == 0) return true;
        Sources sources = findSources(machine);
        if (!hasEnough(sources, cost, multiplier)) return false;
        for (Aspect aspect : cost.getAspects()) {
            long missing = (long) cost.getAmount(aspect) * multiplier;
            for (GTN_ThaumicCraftingInputHatch hatch : sources.hatches) {
                if (missing <= 0) break;
                missing -= hatch.takeStoredEssentia(aspect, (int) Math.min(Integer.MAX_VALUE, missing));
            }
            for (int i = 0; i < sources.monitors.size() && missing > 0; i++) {
                missing -= sources.networkHatches.get(i)
                    .extractEssentia(sources.monitors.get(i), aspect, missing);
            }
            if (missing > 0) return false;
        }
        return true;
    }

    private static boolean hasEnough(Sources sources, AspectList cost, int multiplier) {
        for (Aspect aspect : cost.getAspects()) {
            if (aspect == null) return false;
            if (getAvailable(sources, aspect) < (long) cost.getAmount(aspect) * multiplier) return false;
        }
        return true;
    }

    private static long getAvailable(Sources sources, Aspect aspect) {
        long total = 0;
        for (GTN_ThaumicCraftingInputHatch hatch : sources.hatches) total += hatch.getStoredEssentia(aspect);
        for (IMEMonitor<AEEssentiaStack> monitor : sources.monitors) {
            total += GTN_ThaumicCraftingInputHatch.getEssentiaAmount(monitor, aspect);
        }
        return total;
    }

    private static Sources findSources(GTN_MultiBlockBase<?> machine) {
        Sources sources = new Sources();
        for (IDualInputHatch dualInput : machine.mDualInputHatches) {
            if (!(dualInput instanceof GTN_ThaumicCraftingInputHatch hatch)) continue;
            if (hatch.getBaseMetaTileEntity() == null) continue;
            sources.hatches.add(hatch);
            IMEMonitor<AEEssentiaStack> monitor = hatch.getEssentiaMonitor();
            if (monitor != null && !sources.monitors.contains(monitor)) {
                sources.monitors.add(monitor);
                sources.networkHatches.add(hatch);
            }
        }
        return sources;
    }
}
