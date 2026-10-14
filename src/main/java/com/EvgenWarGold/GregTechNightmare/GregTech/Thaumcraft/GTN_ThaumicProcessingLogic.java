package com.EvgenWarGold.GregTechNightmare.GregTech.Thaumcraft;

import javax.annotation.Nonnull;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_ProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData.SimpleMetaData;

import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.ParallelHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.research.ResearchManager;

/**
 * Processing of the Thaumcraft machines:
 * <ul>
 * <li>max parallel doubles with every energy tier above the machine base tier;</li>
 * <li>every parallel costs the full recipe essentia, so parallels are also limited by the available essentia;</li>
 * <li>energy left after parallels goes into normal overclocks.</li>
 * </ul>
 */
public class GTN_ThaumicProcessingLogic extends GTN_ProcessingLogic {

    private static final int MAX_TIER_BONUS = 10;

    private final GTN_MultiBlockBase<?> machine;
    private String startSound;

    public GTN_ThaumicProcessingLogic(GTN_MultiBlockBase<?> machine, int baseTier) {
        this.machine = machine;
        setMaxParallelSupplier(() -> getTierParallel(availableVoltage, baseTier));
    }

    /** Sound played for players nearby when a recipe starts. */
    public GTN_ThaumicProcessingLogic setStartSound(String sound) {
        this.startSound = sound;
        return this;
    }

    /** 1 parallel at the base tier, x2 for every tier above it. */
    public static int getTierParallel(long voltage, int baseTier) {
        int bonus = Math.max(0, Math.min(MAX_TIER_BONUS, GTUtility.getTier(voltage) - baseTier));
        return 1 << bonus;
    }

    @Nonnull
    @Override
    public CheckRecipeResult process() {
        setOverclockType(OverclockType.NormalOverclock);
        return super.process();
    }

    @Nonnull
    @Override
    protected CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
        String research = recipe.getMetadata(SimpleMetaData.RESEARCH_KEY);
        if (research != null && !research.isEmpty()) {
            String owner = machine.getBaseMetaTileEntity()
                .getOwnerName();
            if (owner == null || !ResearchManager.isResearchComplete(owner, research)) {
                return SimpleCheckRecipeResult.ofFailure("research_not_completed");
            }
        }
        AspectList cost = recipe.getMetadata(SimpleMetaData.ASPECT_COST);
        if (!GTN_EssentiaRecipes.hasEnough(machine, cost)) {
            return SimpleCheckRecipeResult.ofFailure("not_enough_aspects");
        }
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Nonnull
    @Override
    protected ParallelHelper createParallelHelper(@Nonnull GTRecipe recipe) {
        AspectList cost = recipe.getMetadata(SimpleMetaData.ASPECT_COST);
        int essentiaParallel = GTN_EssentiaRecipes.getMaxParallel(machine, cost);
        return super.createParallelHelper(recipe).setMachine(machine, true, true)
            .setMaxParallel(Math.max(1, Math.min(maxParallel, essentiaParallel)));
    }

    @Nonnull
    @Override
    protected CheckRecipeResult onRecipeStart(@Nonnull GTRecipe recipe) {
        AspectList cost = recipe.getMetadata(SimpleMetaData.ASPECT_COST);
        if (!GTN_EssentiaRecipes.consume(machine, cost, Math.max(1, calculatedParallels))) {
            return SimpleCheckRecipeResult.ofFailure("not_enough_aspects");
        }
        GTN_ThaumicSounds.playAt(machine.getBaseMetaTileEntity(), startSound, 1.0F, 1.0F);
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }
}
