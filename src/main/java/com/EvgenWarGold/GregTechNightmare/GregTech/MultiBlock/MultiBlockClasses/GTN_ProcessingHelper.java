package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_AspectHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_ManaHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_MeAspectHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeResult.ResultMessage;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;

import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTUtility;
import gregtech.api.util.ItemEjectionHelper;
import gregtech.api.util.VoidProtectionHelper;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.tileentities.machines.outputme.MTEHatchOutputME;
import thaumcraft.api.aspects.Aspect;

public class GTN_ProcessingHelper<T extends GTN_MultiBlockBase<T>> {

    // region Variables
    public final T multiblock;
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
            .setItemOutputs(new ItemStack[] { outputStack })
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

    public boolean outputItemToHatches(ItemStack item, int amount, boolean simulate) {
        if (GTUtility.isStackInvalid(item) || amount <= 0) {
            return false;
        }

        ItemStack checkStack = item.copy();
        checkStack.stackSize = amount;
        int initialCount = checkStack.stackSize;

        ItemEjectionHelper checkHelper = new ItemEjectionHelper(multiblock);
        checkHelper.ejectStack(checkStack);

        if (checkStack.stackSize > 0) {
            return false;
        }

        if (simulate) {
            return true;
        }

        ItemStack fillStack = item.copy();
        fillStack.stackSize = amount;
        ItemEjectionHelper fillHelper = new ItemEjectionHelper(multiblock);
        fillHelper.ejectStack(fillStack);

        if (fillStack.stackSize == 0) {
            fillHelper.commit();
            return true;
        } else {
            fillStack.stackSize = initialCount;
            return false;
        }
    }

    public boolean outputItemToHatches(ItemStack item, int amount) {
        return outputItemToHatches(item, amount, false);
    }

    public boolean outputItemToHatches(Map<ItemStack, Integer> itemsMap, boolean simulate) {
        if (itemsMap == null || itemsMap.isEmpty()) {
            return false;
        }

        Map<ItemStack, Integer> remainingItems = new HashMap<>();
        for (Map.Entry<ItemStack, Integer> entry : itemsMap.entrySet()) {
            ItemStack stack = entry.getKey();
            int amount = entry.getValue();
            if (!GTUtility.isStackInvalid(stack) && amount > 0) {
                remainingItems.put(stack.copy(), amount);
            }
        }

        if (remainingItems.isEmpty()) {
            return false;
        }

        ItemEjectionHelper checkHelper = new ItemEjectionHelper(multiblock);
        for (Map.Entry<ItemStack, Integer> entry : remainingItems.entrySet()) {
            ItemStack checkStack = entry.getKey()
                .copy();
            checkStack.stackSize = entry.getValue();
            checkHelper.ejectStack(checkStack);
            if (checkStack.stackSize > 0) {
                return false;
            }
        }

        if (simulate) {
            return true;
        }

        ItemEjectionHelper fillHelper = new ItemEjectionHelper(multiblock);
        boolean allSuccess = true;

        for (Map.Entry<ItemStack, Integer> entry : remainingItems.entrySet()) {
            ItemStack fillStack = entry.getKey()
                .copy();
            fillStack.stackSize = entry.getValue();
            fillHelper.ejectStack(fillStack);
            if (fillStack.stackSize > 0) {
                allSuccess = false;
                break;
            }
        }

        if (allSuccess) {
            fillHelper.commit();
            return true;
        } else {
            return false;
        }
    }

    public boolean outputItemToHatches(Map<ItemStack, Integer> itemsMap) {
        return outputItemToHatches(itemsMap, false);
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

    public boolean outputFluid(FluidStack fluid, int amount, boolean simulate) {
        FluidStack outputFluid = fluid.copy();
        outputFluid.amount = amount;

        VoidProtectionHelper voidProtectionHelper = new VoidProtectionHelper();
        voidProtectionHelper.setMachine(multiblock)
            .setFluidOutputs(new FluidStack[] { outputFluid })
            .build();

        boolean hasSpace = !voidProtectionHelper.isFluidFull();

        if (!hasSpace) {
            return false;
        }

        if (simulate) {
            return true;
        }

        multiblock.mOutputFluids = new FluidStack[] { outputFluid.copy() };

        return true;
    }

    public boolean outputFluid(FluidStack fluid, int amount) {
        return outputFluid(fluid, amount, false);
    }

    public boolean outputFluid(Map<FluidStack, Integer> fluidsMap, boolean simulate) {
        VoidProtectionHelper voidProtectionHelper = new VoidProtectionHelper();
        voidProtectionHelper.setMachine(multiblock);

        List<FluidStack> allOutputs = new ArrayList<>();
        for (Map.Entry<FluidStack, Integer> entry : fluidsMap.entrySet()) {
            FluidStack fluid = entry.getKey();
            int amount = entry.getValue();

            FluidStack outputFluid = fluid.copy();
            outputFluid.amount = amount;
            allOutputs.add(outputFluid);
        }

        voidProtectionHelper.setFluidOutputs(allOutputs.toArray(new FluidStack[0]));
        voidProtectionHelper.build();

        boolean hasSpace = !voidProtectionHelper.isFluidFull();

        if (!hasSpace) {
            return false;
        }

        if (simulate) {
            return true;
        }

        multiblock.mOutputFluids = allOutputs.toArray(new FluidStack[0]);

        return true;
    }

    public boolean outputFluid(Map<FluidStack, Integer> fluidsMap) {
        return outputFluid(fluidsMap, false);
    }

    public boolean outputFluidToHatches(FluidStack fluid, int amount, boolean simulate) {
        FluidStack copiedFluidStack = fluid.copy();
        copiedFluidStack.amount = amount;

        int totalCapacity = 0;
        FluidStack checkFluid = fluid.copy();
        checkFluid.amount = amount;

        for (MTEHatchOutput tHatch : GTUtility.validMTEList(multiblock.mOutputHatches)) {
            if (tHatch.canStoreFluid(checkFluid)) {
                if (tHatch instanceof MTEHatchOutputME tMEHatch) {
                    if (!tMEHatch.canFillFluid()) {
                        continue;
                    }
                }

                int tAmount = tHatch.fill(checkFluid, false);
                if (tAmount > 0) {
                    totalCapacity += tAmount;
                    checkFluid.amount -= tAmount;
                }
            }
        }

        if (totalCapacity < amount) {
            return false;
        }

        if (simulate) {
            return true;
        }

        FluidStack fillFluid = fluid.copy();
        fillFluid.amount = amount;
        int remainingAmount = amount;

        for (MTEHatchOutput tHatch : GTUtility.validMTEList(multiblock.mOutputHatches)) {
            if (tHatch.canStoreFluid(fillFluid)) {
                if (tHatch instanceof MTEHatchOutputME tMEHatch) {
                    if (!tMEHatch.canFillFluid()) {
                        continue;
                    }
                }

                int tAmount = tHatch.fill(fillFluid, false);
                if (tAmount > 0) {
                    int actualFill = tHatch.fill(fillFluid, true);
                    remainingAmount -= actualFill;

                    if (remainingAmount <= 0) {
                        return true;
                    }
                    fillFluid.amount = remainingAmount;
                }
            }
        }

        return remainingAmount <= 0;
    }

    public boolean outputFluidToHatches(FluidStack fluid, int amount) {
        return outputFluidToHatches(fluid, amount, false);
    }

    public boolean outputFluidToHatches(Map<FluidStack, Integer> fluidsMap, boolean simulate) {
        Map<FluidStack, Integer> remainingFluids = new HashMap<>();
        for (Map.Entry<FluidStack, Integer> entry : fluidsMap.entrySet()) {
            remainingFluids.put(
                entry.getKey()
                    .copy(),
                entry.getValue());
        }

        for (MTEHatchOutput tHatch : GTUtility.validMTEList(multiblock.mOutputHatches)) {
            if (tHatch instanceof MTEHatchOutputME tMEHatch) {
                if (!tMEHatch.canFillFluid()) {
                    continue;
                }
            }

            Iterator<Map.Entry<FluidStack, Integer>> iterator = remainingFluids.entrySet()
                .iterator();
            while (iterator.hasNext()) {
                Map.Entry<FluidStack, Integer> entry = iterator.next();
                FluidStack fluid = entry.getKey();
                int amount = entry.getValue();

                if (amount <= 0) {
                    iterator.remove();
                    continue;
                }

                FluidStack checkFluid = fluid.copy();
                checkFluid.amount = amount;

                if (!tHatch.canStoreFluid(checkFluid)) {
                    continue;
                }

                int tAmount = tHatch.fill(checkFluid, false);
                if (tAmount > 0) {
                    int newAmount = amount - tAmount;
                    if (newAmount <= 0) {
                        iterator.remove();
                    } else {
                        entry.setValue(newAmount);
                    }
                }
            }

            if (remainingFluids.isEmpty()) {
                break;
            }
        }

        if (!remainingFluids.isEmpty()) {
            return false;
        }

        if (simulate) {
            return true;
        }

        Map<FluidStack, Integer> fillFluids = new HashMap<>();
        for (Map.Entry<FluidStack, Integer> entry : fluidsMap.entrySet()) {
            fillFluids.put(
                entry.getKey()
                    .copy(),
                entry.getValue());
        }

        for (MTEHatchOutput tHatch : GTUtility.validMTEList(multiblock.mOutputHatches)) {
            if (tHatch instanceof MTEHatchOutputME tMEHatch) {
                if (!tMEHatch.canFillFluid()) {
                    continue;
                }
            }

            Iterator<Map.Entry<FluidStack, Integer>> iterator = fillFluids.entrySet()
                .iterator();
            while (iterator.hasNext()) {
                Map.Entry<FluidStack, Integer> entry = iterator.next();
                FluidStack fluid = entry.getKey();
                int amount = entry.getValue();

                if (amount <= 0) {
                    iterator.remove();
                    continue;
                }

                FluidStack fillFluid = fluid.copy();
                fillFluid.amount = amount;

                if (!tHatch.canStoreFluid(fillFluid)) {
                    continue;
                }

                int tAmount = tHatch.fill(fillFluid, false);
                if (tAmount > 0) {
                    int actualFill = tHatch.fill(fillFluid, true);
                    int newAmount = amount - actualFill;
                    if (newAmount <= 0) {
                        iterator.remove();
                    } else {
                        entry.setValue(newAmount);
                    }
                }
            }

            if (fillFluids.isEmpty()) {
                return true;
            }
        }

        return fillFluids.isEmpty();
    }

    public boolean outputFluidToHatches(Map<FluidStack, Integer> fluidsMap) {
        return outputFluidToHatches(fluidsMap, false);
    }
    // endregion

    // region Aspects
    public boolean consumeAspect(Aspect aspect, int amount, boolean simulate) {
        int total = 0;

        for (GTN_AspectHatch hatch : multiblock.aspectHatches) {
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

        for (GTN_AspectHatch hatch : multiblock.aspectHatches) {
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

            for (GTN_AspectHatch hatch : multiblock.aspectHatches) {
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

            for (GTN_AspectHatch hatch : multiblock.aspectHatches) {
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

        for (GTN_MeAspectHatch hatch : multiblock.meAspectHatches) {
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

        for (GTN_MeAspectHatch hatch : multiblock.meAspectHatches) {
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

            for (GTN_MeAspectHatch hatch : multiblock.meAspectHatches) {
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

            for (GTN_MeAspectHatch hatch : multiblock.meAspectHatches) {
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

        for (GTN_ManaHatch hatch : multiblock.manaHatches) {
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

        for (GTN_ManaHatch hatch : multiblock.manaHatches) {
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

    public CheckRecipeResult resultShutDown() {
        multiblock.stopMachine(ShutDownReasonRegistry.NONE);
        return CheckRecipeResultRegistry.NONE;
    }

    public CheckRecipeResult resultGenerating() {
        return CheckRecipeResultRegistry.GENERATING;
    }

    public CheckRecipeResult resultFailureMessage(String message) {
        return ResultMessage.of(message);
    }

    public CheckRecipeResult resultSuccessMessage(String message) {
        return ResultMessage.of(message);
    }
    // endregion

    // region Energy
    public void setEnergyGenerate(long eu, int efficiency) {
        multiblock.mEfficiency = efficiency;
        multiblock.lEUt = eu;
    }

    public void setEnergyGenerate(long eu) {
        setEnergyGenerate(eu, 10_000);
    }

    public void setEnergyUsage(long eu) {
        multiblock.lEUt = -eu;
    }

    public void setEnergyUsageWithoutLoss(long eu) {
        multiblock.lEUt = (long) (-eu * 0.95);
    }
    // endregion
}
