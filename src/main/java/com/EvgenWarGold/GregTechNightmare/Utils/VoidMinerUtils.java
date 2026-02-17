package com.EvgenWarGold.GregTechNightmare.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.ChunkProviderServer;

import bwcrossmod.galacticgreg.VoidMinerUtility;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.objects.XSTR;
import gregtech.api.util.GTUtility;

public class VoidMinerUtils {

    protected VoidMinerUtility.DropMap dropMap = null;
    protected VoidMinerUtility.DropMap extraDropMap = null;
    protected float totalWeight = 0;
    protected List<Integer> allowDimension = new ArrayList<>();

    public VoidMinerUtils(List<Integer> allowDimension) {
        this.allowDimension = allowDimension;
    }

    public void initDropMap(IGregTechTileEntity te) {
        dropMap = new VoidMinerUtility.DropMap();
        extraDropMap = new VoidMinerUtility.DropMap();
        WorldProvider worldProvider = te.getWorld().provider;
        int dimensionId = worldProvider.dimensionId;
        handleModDimDef(dimensionId, te);
        handleExtraDrops(dimensionId);
        totalWeight = dropMap.getTotalWeight() + extraDropMap.getTotalWeight();

        if (totalWeight <= 0) {
            dropMap = null;
            extraDropMap = null;
        }
    }

    private void handleModDimDef(int id, IGregTechTileEntity te) {
        if (VoidMinerUtility.dropMapsByDimId.containsKey(id)) {
            dropMap = VoidMinerUtility.dropMapsByDimId.get(id);
        } else {
            String chunkProviderName = ((ChunkProviderServer) te.getWorld()
                .getChunkProvider()).currentChunkProvider.getClass()
                    .getName();

            if (VoidMinerUtility.dropMapsByChunkProviderName.containsKey(chunkProviderName)) {
                dropMap = VoidMinerUtility.dropMapsByChunkProviderName.get(chunkProviderName);
            }
        }
    }

    private void handleExtraDrops(int id) {
        if (VoidMinerUtility.extraDropsDimMap.containsKey(id)) {
            extraDropMap = VoidMinerUtility.extraDropsDimMap.get(id);
        }
    }

    public ItemStack generateOneStackOre() {
        float currentWeight = 0.f;
        while (true) {
            float randomNumber = XSTR.XSTR_INSTANCE.nextFloat() * totalWeight;
            for (Map.Entry<GTUtility.ItemId, Float> entry : dropMap.getInternalMap()
                .entrySet()) {
                currentWeight += entry.getValue();
                if (randomNumber < currentWeight) return entry.getKey()
                    .getItemStack();
            }
            for (Map.Entry<GTUtility.ItemId, Float> entry : extraDropMap.getInternalMap()
                .entrySet()) {
                currentWeight += entry.getValue();
                if (randomNumber < currentWeight) return entry.getKey()
                    .getItemStack();
            }
        }
    }

    public List<ItemStack> generateStackOre(int amountStacks) {
        List<ItemStack> result = new ArrayList<>();
        boolean isItemAdded = false;
        for (int i = 0; i < amountStacks; i++) {
            float currentWeight = 0.f;
            float randomNumber = XSTR.XSTR_INSTANCE.nextFloat() * totalWeight;
            for (Map.Entry<GTUtility.ItemId, Float> entry : dropMap.getInternalMap()
                .entrySet()) {
                currentWeight += entry.getValue();
                if (randomNumber < currentWeight) {
                    isItemAdded = true;
                    result.add(
                        entry.getKey()
                            .getItemStack());
                    break;
                }
            }
            if (!isItemAdded) {
                for (Map.Entry<GTUtility.ItemId, Float> entry : extraDropMap.getInternalMap()
                    .entrySet()) {
                    currentWeight += entry.getValue();
                    if (randomNumber < currentWeight) {
                        result.add(
                            entry.getKey()
                                .getItemStack());
                        break;
                    }
                }
            }
        }
        return result;
    }
}
