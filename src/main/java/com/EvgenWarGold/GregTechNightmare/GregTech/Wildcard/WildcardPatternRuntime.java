package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import gregtech.api.enums.GTValues;
import gregtech.api.objects.GTDualInputPattern;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;

public final class WildcardPatternRuntime {

    private static final Object ACTIVE_LOCK = new Object();
    private static final Map<Object, ActivePattern> ACTIVE_PATTERNS = new WeakHashMap<>();

    private WildcardPatternRuntime() {}

    public static boolean preparePush(Object host, WildcardPatternDetails details) {
        if (host == null || details == null) return false;

        Object patternSlot = CraftingInputMEIntrospection.findLivePatternSlot(host, details.getDelegate());
        if (patternSlot == null) return false;

        MTEHatchCraftingInputME craftingHost = host instanceof MTEHatchCraftingInputME
            ? (MTEHatchCraftingInputME) host
            : null;

        synchronized (ACTIVE_LOCK) {
            ActivePattern active = ACTIVE_PATTERNS.get(patternSlot);
            if (active == null || isPatternSlotEmpty(patternSlot)) {
                ACTIVE_PATTERNS.put(patternSlot, new ActivePattern(details, craftingHost));
                return true;
            }

            active.setHost(craftingHost);
            return sameResolvedPattern(active.details, details);
        }
    }

    public static WildcardPatternDetails getActiveDetails(Object patternSlot) {
        if (patternSlot == null) return null;

        synchronized (ACTIVE_LOCK) {
            ActivePattern active = ACTIVE_PATTERNS.get(patternSlot);
            return active == null ? null : active.details;
        }
    }

    public static GTDualInputPattern buildResolvedPatternInputs(Object patternSlot, WildcardPatternDetails details) {
        if (patternSlot == null || details == null) return null;

        ItemStack[] sharedItems = null;
        MTEHatchCraftingInputME host;
        synchronized (ACTIVE_LOCK) {
            ActivePattern active = ACTIVE_PATTERNS.get(patternSlot);
            host = active == null ? null : active.getHost();
        }

        if (host != null) {
            try {
                sharedItems = host.getSharedItems();
            } catch (RuntimeException ignored) {}
        }

        List<ItemStack> items = new ArrayList<>();
        appendSharedItems(sharedItems, items);

        if (!containsIntegratedCircuit(items)) {
            ItemStack circuit = CraftingInputMECircuitResolver.find(host, patternSlot);
            if (circuit != null) items.add(circuit);
        }

        List<FluidStack> fluids = new ArrayList<>();
        appendResolvedInputs(details.getAEInputs(), items, fluids);

        GTDualInputPattern result = new GTDualInputPattern();
        result.inputItems = items.toArray(new ItemStack[items.size()]);
        result.inputFluid = fluids.isEmpty() ? GTValues.emptyFluidStackArray
            : fluids.toArray(new FluidStack[fluids.size()]);
        return result;
    }

    private static void appendSharedItems(ItemStack[] sharedItems, List<ItemStack> output) {
        if (sharedItems == null) return;

        for (ItemStack stack : sharedItems) {
            if (stack != null && stack.stackSize > 0) output.add(stack.copy());
        }
    }

    private static void appendResolvedInputs(IAEStack[] inputs, List<ItemStack> items, List<FluidStack> fluids) {
        for (IAEStack aeStack : inputs) {
            if (aeStack == null || aeStack.getStackSize() <= 0) continue;

            if (aeStack instanceof IAEItemStack) {
                ItemStack stack = ((IAEItemStack) aeStack).getItemStack();
                if (stack == null) continue;

                ItemStack copy = stack.copy();
                copy.stackSize = clampAmount(aeStack.getStackSize());
                items.add(copy);
                continue;
            }

            if (aeStack instanceof IAEFluidStack) {
                FluidStack stack = ((IAEFluidStack) aeStack).getFluidStack();
                if (stack == null) continue;

                FluidStack copy = stack.copy();
                copy.amount = clampAmount(aeStack.getStackSize());
                fluids.add(copy);
            }
        }
    }

    private static int clampAmount(long amount) {
        return amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
    }

    private static boolean isPatternSlotEmpty(Object patternSlot) {
        for (Class<?> type = patternSlot.getClass(); type != null && type != Object.class;
            type = type.getSuperclass()) {
            try {
                Method method = type.getDeclaredMethod("isEmpty");
                method.setAccessible(true);
                Object result = method.invoke(patternSlot);
                if (result instanceof Boolean) return ((Boolean) result).booleanValue();
            } catch (NoSuchMethodException ignored) {
                continue;
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                break;
            }
        }

        // Fail closed when an occupied slot cannot be inspected.
        return false;
    }

    private static boolean containsIntegratedCircuit(List<ItemStack> items) {
        for (ItemStack stack : items) {
            if (CraftingInputMECircuitResolver.isIntegratedCircuit(stack)) return true;
        }
        return false;
    }

    private static boolean sameResolvedPattern(WildcardPatternDetails first, WildcardPatternDetails second) {
        return sameStacks(first.getCondensedAEInputs(), second.getCondensedAEInputs())
            && sameStacks(first.getCondensedAEOutputs(), second.getCondensedAEOutputs());
    }

    private static boolean sameStacks(IAEStack[] first, IAEStack[] second) {
        if (first == null || second == null) return first == second;
        if (first.length != second.length) return false;

        boolean[] used = new boolean[second.length];
        for (IAEStack wanted : first) {
            boolean found = false;
            for (int i = 0; i < second.length; i++) {
                if (used[i] || !sameStack(wanted, second[i])) continue;

                used[i] = true;
                found = true;
                break;
            }
            if (!found) return false;
        }
        return true;
    }

    private static boolean sameStack(IAEStack first, IAEStack second) {
        if (first == null || second == null) return first == second;
        if (first.getStackSize() != second.getStackSize()) return false;

        if (first instanceof IAEItemStack && second instanceof IAEItemStack) {
            ItemStack firstItem = ((IAEItemStack) first).getItemStack();
            ItemStack secondItem = ((IAEItemStack) second).getItemStack();
            return firstItem != null && secondItem != null && firstItem.isItemEqual(secondItem)
                && ItemStack.areItemStackTagsEqual(firstItem, secondItem);
        }

        if (first instanceof IAEFluidStack && second instanceof IAEFluidStack) {
            FluidStack firstFluid = ((IAEFluidStack) first).getFluidStack();
            FluidStack secondFluid = ((IAEFluidStack) second).getFluidStack();
            return firstFluid != null && secondFluid != null && firstFluid.isFluidEqual(secondFluid);
        }
        return false;
    }

    private static final class ActivePattern {

        private final WildcardPatternDetails details;
        private WeakReference<MTEHatchCraftingInputME> host;

        private ActivePattern(WildcardPatternDetails details, MTEHatchCraftingInputME host) {
            this.details = details;
            setHost(host);
        }

        private MTEHatchCraftingInputME getHost() {
            return host == null ? null : host.get();
        }

        private void setHost(MTEHatchCraftingInputME host) {
            this.host = host == null ? null : new WeakReference<>(host);
        }
    }
}
