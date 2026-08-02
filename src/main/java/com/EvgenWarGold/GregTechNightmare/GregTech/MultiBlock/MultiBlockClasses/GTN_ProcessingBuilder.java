package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import static gregtech.api.recipe.check.CheckRecipeResultRegistry.FLUID_OUTPUT_FULL;
import static gregtech.api.recipe.check.CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
import static gregtech.api.recipe.check.CheckRecipeResultRegistry.SUCCESSFUL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.VoidProtectionHelper;
import thaumcraft.api.aspects.Aspect;

public class GTN_ProcessingBuilder<T extends GTN_MultiBlockBase<T>> {

    // region Variables
    private final GTN_ProcessingHelper<T> helper;
    private final Map<ItemStack, Integer> itemsToConsume = new HashMap<>();
    private final Map<ItemStack, Integer> itemsToOutput = new HashMap<>();
    private final Map<FluidStack, Integer> fluidsToConsume = new HashMap<>();
    private final Map<FluidStack, Integer> fluidsToOutput = new HashMap<>();
    private final Map<Aspect, Integer> aspectsToConsume = new HashMap<>();
    private final Map<Aspect, Integer> meAspectsToConsume = new HashMap<>();
    private Integer manaToConsume = null;
    private Integer durationTicks = null;
    // endregion

    // region Constructor
    public GTN_ProcessingBuilder(GTN_ProcessingHelper<T> helper) {
        this.helper = helper;
    }
    // endregion

    // region Consume Methods
    public GTN_ProcessingBuilder<T> consumeItem(ItemStack item, int amount) {
        if (item != null && amount > 0) {
            itemsToConsume.merge(item.copy(), amount, Integer::sum);
        }
        return this;
    }

    public GTN_ProcessingBuilder<T> consumeItem(Map<ItemStack, Integer> items) {
        if (items != null) {
            for (Map.Entry<ItemStack, Integer> entry : items.entrySet()) {
                consumeItem(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public GTN_ProcessingBuilder<T> consumeFluid(FluidStack fluid, int amount) {
        if (fluid != null && amount > 0) {
            fluidsToConsume.merge(fluid.copy(), amount, Integer::sum);
        }
        return this;
    }

    public GTN_ProcessingBuilder<T> consumeFluid(Map<FluidStack, Integer> fluids) {
        if (fluids != null) {
            for (Map.Entry<FluidStack, Integer> entry : fluids.entrySet()) {
                consumeFluid(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public GTN_ProcessingBuilder<T> consumeAspect(Aspect aspect, int amount) {
        if (aspect != null && amount > 0) {
            aspectsToConsume.merge(aspect, amount, Integer::sum);
        }
        return this;
    }

    public GTN_ProcessingBuilder<T> consumeAspect(Map<Aspect, Integer> aspects) {
        if (aspects != null) {
            for (Map.Entry<Aspect, Integer> entry : aspects.entrySet()) {
                consumeAspect(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public GTN_ProcessingBuilder<T> consumeMeAspect(Aspect aspect, int amount) {
        if (aspect != null && amount > 0) {
            meAspectsToConsume.merge(aspect, amount, Integer::sum);
        }
        return this;
    }

    public GTN_ProcessingBuilder<T> consumeMeAspect(Map<Aspect, Integer> aspects) {
        if (aspects != null) {
            for (Map.Entry<Aspect, Integer> entry : aspects.entrySet()) {
                consumeMeAspect(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public GTN_ProcessingBuilder<T> consumeMana(int amount) {
        if (amount > 0) {
            manaToConsume = manaToConsume == null ? amount : manaToConsume + amount;
        }
        return this;
    }
    // endregion

    // region Output Methods
    public GTN_ProcessingBuilder<T> outputItem(ItemStack item, int amount) {
        if (item != null && amount > 0) {
            itemsToOutput.merge(item.copy(), amount, Integer::sum);
        }
        return this;
    }

    public GTN_ProcessingBuilder<T> outputItem(Map<ItemStack, Integer> items) {
        if (items != null) {
            for (Map.Entry<ItemStack, Integer> entry : items.entrySet()) {
                outputItem(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public GTN_ProcessingBuilder<T> outputFluid(FluidStack fluid, int amount) {
        if (fluid != null && amount > 0) {
            fluidsToOutput.merge(fluid.copy(), amount, Integer::sum);
        }
        return this;
    }

    public GTN_ProcessingBuilder<T> outputFluid(Map<FluidStack, Integer> fluids) {
        if (fluids != null) {
            for (Map.Entry<FluidStack, Integer> entry : fluids.entrySet()) {
                outputFluid(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }
    // endregion

    // region Duration
    public GTN_ProcessingBuilder<T> setDurationTicks(int ticks) {
        this.durationTicks = ticks;
        return this;
    }

    public GTN_ProcessingBuilder<T> setDurationSeconds(int seconds) {
        return setDurationTicks(seconds * 20);
    }

    public GTN_ProcessingBuilder<T> setDurationMinutes(int minutes) {
        return setDurationSeconds(minutes * 60);
    }

    public GTN_ProcessingBuilder<T> setDurationHours(int hours) {
        return setDurationMinutes(hours * 60);
    }

    public GTN_ProcessingBuilder<T> setDurationDays(int days) {
        return setDurationHours(days * 24);
    }
    // endregion

    // region Execute
    public CheckRecipeResult executeResult(boolean simulate) {
        if (!itemsToConsume.isEmpty()) {
            for (Map.Entry<ItemStack, Integer> entry : itemsToConsume.entrySet()) {
                if (!helper.consumeItem(entry.getKey(), entry.getValue(), true)) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }
            }
        }

        if (!fluidsToConsume.isEmpty()) {
            for (Map.Entry<FluidStack, Integer> entry : fluidsToConsume.entrySet()) {
                if (!helper.consumeFluid(entry.getKey(), entry.getValue(), true)) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }
            }
        }

        if (!aspectsToConsume.isEmpty()) {
            for (Map.Entry<Aspect, Integer> entry : aspectsToConsume.entrySet()) {
                if (!helper.consumeAspect(entry.getKey(), entry.getValue(), true)) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }
            }
        }

        if (!meAspectsToConsume.isEmpty()) {
            for (Map.Entry<Aspect, Integer> entry : meAspectsToConsume.entrySet()) {
                if (!helper.consumeMeAspect(entry.getKey(), entry.getValue(), true)) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }
            }
        }

        if (manaToConsume != null) {
            if (!helper.consumeMana(manaToConsume, true)) {
                return CheckRecipeResultRegistry.NO_RECIPE;
            }
        }

        if (!itemsToOutput.isEmpty()) {
            VoidProtectionHelper voidProtectionHelper = new VoidProtectionHelper();
            voidProtectionHelper.setMachine(helper.multiblock);

            List<ItemStack> allOutputs = new ArrayList<>();
            for (Map.Entry<ItemStack, Integer> entry : itemsToOutput.entrySet()) {
                ItemStack outputStack = entry.getKey()
                    .copy();
                outputStack.stackSize = entry.getValue();
                allOutputs.add(outputStack);
            }

            voidProtectionHelper.setItemOutputs(allOutputs.toArray(new ItemStack[0]));
            voidProtectionHelper.build();

            if (voidProtectionHelper.isItemFull()) {
                return ITEM_OUTPUT_FULL;
            }
        }

        if (!fluidsToOutput.isEmpty()) {
            VoidProtectionHelper voidProtectionHelper = new VoidProtectionHelper();
            voidProtectionHelper.setMachine(helper.multiblock);

            List<FluidStack> allOutputs = new ArrayList<>();
            for (Map.Entry<FluidStack, Integer> entry : fluidsToOutput.entrySet()) {
                FluidStack outputFluid = entry.getKey()
                    .copy();
                outputFluid.amount = entry.getValue();
                allOutputs.add(outputFluid);
            }

            voidProtectionHelper.setFluidOutputs(allOutputs.toArray(new FluidStack[0]));
            voidProtectionHelper.build();

            if (voidProtectionHelper.isFluidFull()) {
                return FLUID_OUTPUT_FULL;
            }
        }

        if (simulate) {
            return SUCCESSFUL;
        }

        if (!itemsToConsume.isEmpty()) {
            for (Map.Entry<ItemStack, Integer> entry : itemsToConsume.entrySet()) {
                if (!helper.consumeItem(entry.getKey(), entry.getValue(), false)) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }
            }
        }

        if (!fluidsToConsume.isEmpty()) {
            for (Map.Entry<FluidStack, Integer> entry : fluidsToConsume.entrySet()) {
                if (!helper.consumeFluid(entry.getKey(), entry.getValue(), false)) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }
            }
        }

        if (!aspectsToConsume.isEmpty()) {
            for (Map.Entry<Aspect, Integer> entry : aspectsToConsume.entrySet()) {
                if (!helper.consumeAspect(entry.getKey(), entry.getValue(), false)) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }
            }
        }

        if (!meAspectsToConsume.isEmpty()) {
            for (Map.Entry<Aspect, Integer> entry : meAspectsToConsume.entrySet()) {
                if (!helper.consumeMeAspect(entry.getKey(), entry.getValue(), false)) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }
            }
        }

        if (manaToConsume != null) {
            if (!helper.consumeMana(manaToConsume, false)) {
                return CheckRecipeResultRegistry.NO_RECIPE;
            }
        }

        if (!itemsToOutput.isEmpty()) {
            helper.outputItem(itemsToOutput, false);
        }

        if (!fluidsToOutput.isEmpty()) {
            helper.outputFluid(fluidsToOutput, false);
        }

        if (durationTicks != null && durationTicks > 0) {
            helper.setDurationInTicks(durationTicks);
        }

        return SUCCESSFUL;
    }

    public boolean executeCheck() {
        return executeResult(true).wasSuccessful();
    }

    public CheckRecipeResult execute() {
        CheckRecipeResult result = executeResult(false);
        if (result.wasSuccessful() && durationTicks != null && durationTicks > 0) {
            helper.multiblock.mMaxProgresstime = durationTicks;
        }
        return result;
    }
    // endregion
}
