package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.utils.item.IItemHandler;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import gregtech.api.enums.Materials;
import gregtech.api.objects.ItemData;
import gregtech.api.util.GTOreDictUnificator;

public final class WildcardPatternBlacklist {

    private final WildcardBlacklistMode mode;
    private final Set<Materials> materials;
    private final Map<OutputKey, List<ItemStack>> outputs;

    private WildcardPatternBlacklist(WildcardBlacklistMode mode, Set<Materials> materials,
        Map<OutputKey, List<ItemStack>> outputs) {
        this.mode = mode;
        this.materials = materials;
        this.outputs = outputs;
    }

    public static WildcardPatternBlacklist create(WildcardBlacklistMode mode, IItemHandler inventory) {
        WildcardBlacklistMode effectiveMode = mode == null ? WildcardBlacklistMode.OUTPUT : mode;
        Set<Materials> materials = Collections.newSetFromMap(new IdentityHashMap<Materials, Boolean>());
        Map<OutputKey, List<ItemStack>> outputs = new HashMap<>();

        if (inventory != null) {
            for (int slot = 0; slot < inventory.getSlots(); slot++) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (stack == null) continue;

                if (effectiveMode == WildcardBlacklistMode.INPUT) {
                    Materials material = getMaterial(stack);
                    if (material != null && material != Materials._NULL) materials.add(material);
                } else {
                    addOutput(outputs, stack);
                }
            }
        }

        return new WildcardPatternBlacklist(effectiveMode, Collections.unmodifiableSet(materials), outputs);
    }

    public boolean blocksMaterial(Materials material) {
        return mode == WildcardBlacklistMode.INPUT && material != null && materials.contains(material);
    }

    public boolean blocksOutputs(IAEStack[] stacks) {
        if (mode != WildcardBlacklistMode.OUTPUT || stacks == null || outputs.isEmpty()) return false;

        for (IAEStack stack : stacks) {
            if (!(stack instanceof IAEItemStack)) continue;
            ItemStack output = ((IAEItemStack) stack).getItemStack();
            if (output == null) continue;

            List<ItemStack> filters = outputs.get(new OutputKey(output));
            if (filters == null) continue;
            for (ItemStack filter : filters) {
                if (ItemStack.areItemStackTagsEqual(filter, output)) return true;
            }
        }
        return false;
    }

    private static void addOutput(Map<OutputKey, List<ItemStack>> outputs, ItemStack stack) {
        OutputKey key = new OutputKey(stack);
        List<ItemStack> filters = outputs.get(key);
        if (filters == null) {
            filters = new ArrayList<>();
            outputs.put(key, filters);
        }
        ItemStack copy = stack.copy();
        copy.stackSize = 1;
        filters.add(copy);
    }

    private static Materials getMaterial(ItemStack stack) {
        ItemData data = GTOreDictUnificator.getAssociation(stack);
        if (data == null || !data.hasValidMaterialData()) {
            data = GTOreDictUnificator.getItemData(stack);
        }
        return data != null && data.hasValidMaterialData() ? data.mMaterial.mMaterial : null;
    }

    private static final class OutputKey {

        private final Item item;
        private final int damage;
        private final int hashCode;

        private OutputKey(ItemStack stack) {
            item = stack.getItem();
            damage = stack.getItemDamage();
            hashCode = 31 * System.identityHashCode(item) + damage;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (!(object instanceof OutputKey)) return false;
            OutputKey other = (OutputKey) object;
            return item == other.item && damage == other.damage;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }
}
