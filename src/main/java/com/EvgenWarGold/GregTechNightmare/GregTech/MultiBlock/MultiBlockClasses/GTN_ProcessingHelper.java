package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;
import gregtech.api.util.GTUtility;
import gregtech.api.util.ItemEjectionHelper;
import gregtech.api.util.VoidProtectionHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_AspectHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_ManaHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_MeAspectHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeResult.ResultMessage;

import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import thaumcraft.api.aspects.Aspect;

public class GTN_ProcessingHelper<T extends GTN_MultiBlockBase<T>> {

    // region Variables
    private final T multiblock;
    // endregion

    // region Constructor
    public GTN_ProcessingHelper(T multiblock) {
        this.multiblock = multiblock;
    }
    // endregion

    // region Items
    public boolean consumeItem(ItemStack item, int amount, boolean simulate) {
        ArrayList<ItemStack> storedItems = multiblock.getAllStoredInputs();
        int total = 0;

        for (ItemStack stored : storedItems) {
            if (stored.isItemEqual(item)) {
                total += stored.stackSize;
            }

            if (total >= amount) {
                break;
            }
        }

        if (total < amount) {
            return false;
        }

        if (simulate) {
            return true;
        }

        int remaining = amount;

        for (ItemStack stored : storedItems) {
            if (!stored.isItemEqual(item)) {
                continue;
            }

            int available = stored.stackSize;

            if (available <= 0) {
                continue;
            }

            int take = Math.min(available, remaining);

            stored.stackSize -= take;

            remaining -= take;

            if (remaining == 0) {
                break;
            }
        }

        return true;
    }

    public boolean consumeItem(ItemStack item, int amount) {
        return consumeItem(item, amount, false);
    }

    public boolean consumeItem(Map<ItemStack, Integer> itemsMap, boolean simulate) {
        for (Map.Entry<ItemStack, Integer> entry : itemsMap.entrySet()) {
            ItemStack item = entry.getKey();
            int amount = entry.getValue();

            if (!consumeItem(item, amount, true)) {
                return false;
            }
        }

        if (simulate) {
            return true;
        }

        for (Map.Entry<ItemStack, Integer> entry : itemsMap.entrySet()) {
            ItemStack item = entry.getKey();
            int amount = entry.getValue();
            consumeItem(item, amount, false);
        }

        return true;
    }

    public boolean consumeItem(Map<ItemStack, Integer> itemsMap) {
        return consumeItem(itemsMap, false);
    }

    public boolean outputItem(ItemStack item, int amount, boolean simulate) {
        ItemStack outputStack = item.copy();
        outputStack.stackSize = amount;

        VoidProtectionHelper voidProtectionHelper = new VoidProtectionHelper();
        voidProtectionHelper.setMachine(multiblock)
            .setItemOutputs(new ItemStack[]{outputStack})
            .build();

        boolean hasSpace = !voidProtectionHelper.isItemFull();

        if (!hasSpace) {
            return false;
        }

        if (simulate) {
            return true;
        }

        multiblock.mOutputItems = GTN_Utils.toArray(outputStack.copy());

        return true;
    }

    public boolean outputItem(ItemStack item, int amount) {
        return outputItem(item, amount, false);
    }

    public boolean outputItem(Map<ItemStack, Integer> itemsMap, boolean simulate) {
        VoidProtectionHelper voidProtectionHelper = new VoidProtectionHelper();
        voidProtectionHelper.setMachine(multiblock);

        List<ItemStack> allOutputs = new ArrayList<>();
        for (Map.Entry<ItemStack, Integer> entry : itemsMap.entrySet()) {
            ItemStack item = entry.getKey();
            int amount = entry.getValue();

            ItemStack outputStack = item.copy();
            outputStack.stackSize = amount;
            allOutputs.add(outputStack);
        }

        voidProtectionHelper.setItemOutputs(allOutputs.toArray(new ItemStack[0]));
        voidProtectionHelper.build();

        boolean hasSpace = !voidProtectionHelper.isItemFull();

        if (!hasSpace) {
            return false;
        }

        if (simulate) {
            return true;
        }

        multiblock.mOutputItems = allOutputs.toArray(new ItemStack[0]);

        return true;
    }

    public boolean outputItem(Map<ItemStack, Integer> itemsMap) {
        return outputItem(itemsMap, false);
    }
    // endregion

    // region Fluids
    public boolean consumeFluid(FluidStack fluid, int amount, boolean simulate) {
        ArrayList<FluidStack> storedFluids = multiblock.getStoredFluids();
        int total = 0;

        for (FluidStack stored : storedFluids) {
            if (stored.isFluidEqual(fluid)) {
                total += stored.amount;
            }

            if (total >= amount) {
                break;
            }
        }

        if (total < amount) {
            return false;
        }

        if (simulate) {
            return true;
        }

        int remaining = amount;

        for (FluidStack stored : storedFluids) {
            if (!stored.isFluidEqual(fluid)) {
                continue;
            }

            int available = stored.amount;

            if (available <= 0) {
                continue;
            }

            int take = Math.min(available, remaining);

            stored.amount -= take;

            remaining -= take;

            if (remaining == 0) {
                break;
            }
        }

        return true;
    }

    public boolean consumeFluid(FluidStack fluid, int amount) {
        return consumeFluid(fluid, amount, false);
    }

    public boolean consumeFluid(Map<FluidStack, Integer> fluidsMap, boolean simulate) {
        for (Map.Entry<FluidStack, Integer> entry : fluidsMap.entrySet()) {
            FluidStack fluid = entry.getKey();
            int amount = entry.getValue();

            if (!consumeFluid(fluid, amount, true)) {
                return false;
            }
        }

        if (simulate) {
            return true;
        }

        for (Map.Entry<FluidStack, Integer> entry : fluidsMap.entrySet()) {
            FluidStack fluid = entry.getKey();
            int amount = entry.getValue();
            consumeFluid(fluid, amount, false);
        }

        return true;
    }

    public boolean consumeFluid(Map<FluidStack, Integer> fluidsMap) {
        return consumeFluid(fluidsMap, false);
    }
    // endregion

    // region Aspects
    public boolean consumeAspect(Aspect aspect, int amount, boolean simulate) {
        int total = 0;

        for (GTN_AspectHatch hatch : multiblock.mAspectHatch) {
            total += hatch.containerContains(aspect);

            if (total >= amount) {
                break;
            }
        }

        if (total < amount) {
            return false;
        }

        if (simulate) {
            return true;
        }

        int remaining = amount;

        for (GTN_AspectHatch hatch : multiblock.mAspectHatch) {
            int available = hatch.containerContains(aspect);

            if (available <= 0) {
                continue;
            }

            int take = Math.min(available, remaining);

            hatch.consumeAspect(aspect, take, false);

            remaining -= take;

            if (remaining == 0) {
                break;
            }
        }

        return true;
    }

    public boolean consumeAspect(Aspect aspect, int amount) {
        return consumeAspect(aspect, amount, false);
    }

    public boolean consumeAspect(Map<Aspect, Integer> aspectMap, boolean simulate) {
        for (Map.Entry<Aspect, Integer> entry : aspectMap.entrySet()) {
            Aspect aspect = entry.getKey();
            int amount = entry.getValue();
            int total = 0;

            for (GTN_AspectHatch hatch : multiblock.mAspectHatch) {
                total += hatch.containerContains(aspect);

                if (total >= amount) {
                    break;
                }
            }

            if (total < amount) {
                return false;
            }
        }

        if (simulate) {
            return true;
        }

        for (Map.Entry<Aspect, Integer> entry : aspectMap.entrySet()) {
            Aspect aspect = entry.getKey();
            int remaining = entry.getValue();

            for (GTN_AspectHatch hatch : multiblock.mAspectHatch) {
                int available = hatch.containerContains(aspect);

                if (available <= 0) {
                    continue;
                }

                int take = Math.min(available, remaining);

                hatch.consumeAspect(aspect, take, false);

                remaining -= take;

                if (remaining == 0) {
                    break;
                }
            }
        }

        return true;
    }

    public boolean consumeAspect(Map<Aspect, Integer> aspectMap) {
        return consumeAspect(aspectMap, false);
    }
    // endregion

    // region MeAspects
    public boolean consumeMeAspect(Aspect aspect, int amount, boolean simulate) {
        long total = 0;

        for (GTN_MeAspectHatch hatch : multiblock.mMeAspectHatch) {
            total += hatch.getAspectAmountInNetwork(aspect);

            if (total >= amount) {
                break;
            }
        }

        if (total < amount) {
            return false;
        }

        if (simulate) {
            return true;
        }

        long remaining = amount;

        for (GTN_MeAspectHatch hatch : multiblock.mMeAspectHatch) {
            long available = hatch.getAspectAmountInNetwork(aspect);

            if (available <= 0) {
                continue;
            }

            long take = Math.min(available, remaining);

            hatch.extractEssentia(aspect, take, false);

            remaining -= take;

            if (remaining <= 0) {
                break;
            }
        }

        return true;
    }

    public boolean consumeMeAspect(Aspect aspect, int amount) {
        return consumeMeAspect(aspect, amount, false);
    }

    public boolean consumeMeAspect(Map<Aspect, Integer> aspectMap, boolean simulate) {
        for (Map.Entry<Aspect, Integer> entry : aspectMap.entrySet()) {
            Aspect aspect = entry.getKey();
            int amount = entry.getValue();
            long total = 0;

            for (GTN_MeAspectHatch hatch : multiblock.mMeAspectHatch) {
                total += hatch.getAspectAmountInNetwork(aspect);

                if (total >= amount) {
                    break;
                }
            }

            if (total < amount) {
                return false;
            }
        }

        if (simulate) {
            return true;
        }

        for (Map.Entry<Aspect, Integer> entry : aspectMap.entrySet()) {
            Aspect aspect = entry.getKey();
            long remaining = entry.getValue();

            for (GTN_MeAspectHatch hatch : multiblock.mMeAspectHatch) {
                long available = hatch.getAspectAmountInNetwork(aspect);

                if (available <= 0) {
                    continue;
                }

                long take = Math.min(available, remaining);

                hatch.extractEssentia(aspect, take, false);

                remaining -= take;

                if (remaining <= 0) {
                    break;
                }
            }
        }

        return true;
    }

    public boolean consumeMeAspect(Map<Aspect, Integer> aspectMap) {
        return consumeMeAspect(aspectMap, false);
    }
    // endregion

    // region Mana
    public boolean consumeMana(int amount, boolean simulate) {
        int total = 0;

        for (GTN_ManaHatch hatch : multiblock.mManaHatch) {
            total += hatch.getCurrentMana();

            if (total >= amount) {
                break;
            }
        }

        if (total < amount) {
            return false;
        }

        if (simulate) {
            return true;
        }

        int remaining = amount;

        for (GTN_ManaHatch hatch : multiblock.mManaHatch) {
            int available = hatch.getCurrentMana();

            if (available <= 0) {
                continue;
            }

            int take = Math.min(available, remaining);

            hatch.extractMana(take, false);

            remaining -= take;

            if (remaining == 0) {
                break;
            }
        }

        return true;
    }

    public boolean consumeMana(int amount) {
        return consumeMana(amount, false);
    }
    // endregion

    // region Durations
    public void setDurationInTicks(int ticks) {
        multiblock.mMaxProgresstime = ticks;
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
    // endregion

    // region Result
    public CheckRecipeResult resultSuccess() {
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public CheckRecipeResult resultNoRecipe() {
        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    public CheckRecipeResult resultGenerating() {
        return CheckRecipeResultRegistry.GENERATING;
    }

    public CheckRecipeResult resultFailureMessage(String message) {
        multiblock.stopMachine(ShutDownReasonRegistry.NONE);
        return ResultMessage.of(message);
    }

    public CheckRecipeResult resultSuccessMessage(String message) {
        return ResultMessage.of(message);
    }
    // endregion
}
