package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import gregtech.api.interfaces.tileentity.IVoidable;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.ParallelHelper;
import gregtech.api.util.VoidProtectionHelper;

public class GTN_ProcessingLogic extends ProcessingLogic {

    @Nonnull
    @Override
    protected ParallelHelper createParallelHelper(@Nonnull GTRecipe recipe) {
        return new GTN_ParallelHelper().setRecipe(recipe)
            .setItemInputs(inputItems)
            .setFluidInputs(inputFluids)
            .setAvailableEUt(availableVoltage * availableAmperage)
            .setMachine(machine, protectItems, protectFluids)
            .setRecipeLocked(recipeLockableMachine, isRecipeLocked)
            .setMaxParallel(maxParallel)
            .setEUtModifier(euModifier)
            .enableBatchMode(batchSize)
            .setConsumption(true)
            .setOutputCalculation(true);
    }

    public GTN_ProcessingLogic setOverclockType(OverclockType type) {
        setOverclock(type.timeReduction, type.powerIncrease);
        return this;
    }

    public boolean isOutputItemsFull(ItemStack[] outputItems, IVoidable machine) {
        VoidProtectionHelper voidProtection = new VoidProtectionHelper().setMachine(machine)
            .setItemOutputs(outputItems)
            .build();

        return voidProtection.isItemFull();
    }

    public void setDurationInTicks(int ticks) {
        duration = ticks;
    }

    public void setDurationInSeconds(int seconds) {
        setDurationInTicks(seconds * 20);
    }

    public void setDurationInMinutes(int minutes) {
        setDurationInSeconds(minutes * 60);
    }

    public void setDurationInHours(int hours) {
        setDurationInMinutes(hours * 60);
    }

    public void setDurationInDays(int days) {
        setDurationInHours(days * 24);
    }
}
