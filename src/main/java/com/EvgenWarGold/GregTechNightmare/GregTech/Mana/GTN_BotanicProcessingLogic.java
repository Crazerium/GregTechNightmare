package com.EvgenWarGold.GregTechNightmare.GregTech.Mana;

import javax.annotation.Nonnull;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_ProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData.SimpleMetaData;
import com.EvgenWarGold.GregTechNightmare.GregTech.Thaumcraft.GTN_ThaumicProcessingLogic;

import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.ParallelHelper;

/**
 * Processing of the Botania machines:
 * <ul>
 * <li>every craft costs the fixed recipe mana, taken from Mana Hatches and Magic Crafting Input Hatches;</li>
 * <li>max parallel doubles with every energy tier above the machine base tier, limited by the available mana;</li>
 * <li>energy left after parallels goes into normal overclocks.</li>
 * </ul>
 */
public class GTN_BotanicProcessingLogic extends GTN_ProcessingLogic {

    private final GTN_MultiBlockBase<?> machine;

    public GTN_BotanicProcessingLogic(GTN_MultiBlockBase<?> machine, int baseTier) {
        this.machine = machine;
        setMaxParallelSupplier(() -> GTN_ThaumicProcessingLogic.getTierParallel(availableVoltage, baseTier));
    }

    @Nonnull
    @Override
    public CheckRecipeResult process() {
        setOverclockType(OverclockType.NormalOverclock);
        return super.process();
    }

    private static int getManaCost(GTRecipe recipe) {
        return recipe.getMetadataOrDefault(SimpleMetaData.MANA_COST, 0);
    }

    @Nonnull
    @Override
    protected CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
        if (GTN_ManaSources.getTotalAvailable(machine) < getManaCost(recipe)) {
            return SimpleCheckRecipeResult.ofFailure("not_enough_mana");
        }
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Nonnull
    @Override
    protected ParallelHelper createParallelHelper(@Nonnull GTRecipe recipe) {
        int cost = getManaCost(recipe);
        long manaParallel = cost <= 0 ? Integer.MAX_VALUE : GTN_ManaSources.getTotalAvailable(machine) / cost;
        return super.createParallelHelper(recipe).setMachine(machine, true, true)
            .setMaxParallel((int) Math.max(1, Math.min(maxParallel, manaParallel)));
    }

    @Nonnull
    @Override
    protected CheckRecipeResult onRecipeStart(@Nonnull GTRecipe recipe) {
        long cost = (long) getManaCost(recipe) * Math.max(1, calculatedParallels);
        if (!GTN_ManaSources.consumeTotal(machine, cost)) {
            return SimpleCheckRecipeResult.ofFailure("not_enough_mana");
        }
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }
}
