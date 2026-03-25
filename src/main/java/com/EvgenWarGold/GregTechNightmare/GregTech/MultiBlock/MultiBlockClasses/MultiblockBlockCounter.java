package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.IStructureElementChain;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import blockrenderer6343.client.utils.ConstructableData;
import blockrenderer6343.client.world.DummyWorld;
import blockrenderer6343.integration.nei.StructureHacks;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;

public class MultiblockBlockCounter {

    private static final int MAX_TIERS = 50;

    private final Map<String, Integer> blockCounts = new HashMap<>();
    private final Set<ItemStack> uniqueBlocks = new HashSet<>();
    private int totalBlocks = 0;

    public int getTotalBlockCount(IConstructable multiblock) {
        analyzeMultiblock(multiblock);
        return totalBlocks;
    }

    public Map<String, Integer> getBlockCounts(IConstructable multiblock) {
        analyzeMultiblock(multiblock);
        return new HashMap<>(blockCounts);
    }

    public Set<ItemStack> getUniqueBlocks(IConstructable multiblock) {
        analyzeMultiblock(multiblock);
        return new HashSet<>(uniqueBlocks);
    }

    public Map<String, ElementBlockInfo> getDetailedInfo(IConstructable multiblock) {
        Map<String, ElementBlockInfo> result = new HashMap<>();
        analyzeMultiblockDetailed(multiblock, result);
        return result;
    }

    @SuppressWarnings("unchecked")
    private void analyzeMultiblock(IConstructable multiblock) {
        blockCounts.clear();
        uniqueBlocks.clear();
        totalBlocks = 0;

        try {
            IStructureDefinition<?> structure = multiblock.getStructureDefinition();

            if (structure instanceof StructureDefinition) {
                StructureDefinition<IConstructable> structDef =
                    (StructureDefinition<IConstructable>) structure;

                Collection<IStructureElement<IConstructable>[]> structures =
                    structDef.getStructures().values();

                ConstructableData data = new ConstructableData();

                for (IStructureElement<IConstructable>[] elementArray : structures) {
                    if (elementArray == null) continue;

                    for (IStructureElement<IConstructable> element : elementArray) {
                        if (element == null) continue;

                        processElement(multiblock, element, data);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error analyzing multiblock: " + e.getMessage());
        }
    }

    private void analyzeMultiblockDetailed(IConstructable multiblock,
                                           Map<String, ElementBlockInfo> result) {
        try {
            IStructureDefinition<?> structure = multiblock.getStructureDefinition();

            if (structure instanceof StructureDefinition) {
                @SuppressWarnings("unchecked")
                StructureDefinition<IConstructable> structDef =
                    (StructureDefinition<IConstructable>) structure;

                Collection<IStructureElement<IConstructable>[]> structures =
                    structDef.getStructures().values();

                ConstructableData data = new ConstructableData();
                int elementIndex = 0;

                for (IStructureElement<IConstructable>[] elementArray : structures) {
                    if (elementArray == null) continue;

                    for (IStructureElement<IConstructable> element : elementArray) {
                        if (element == null) continue;

                        String elementName = element.getClass().getSimpleName();
                        ElementBlockInfo info = new ElementBlockInfo();
                        info.elementType = elementName;

                        processElementWithInfo(multiblock, element, data, info);

                        result.put(elementName + "_" + elementIndex++, info);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error in detailed analysis: " + e.getMessage());
        }
    }

    private void processElement(IConstructable multiblock,
                                IStructureElement<IConstructable> element,
                                ConstructableData data) {

        String elementName = element.getClass().getName();

        if (StructureHacks.SKIP_ELEMENTS.contains(elementName)) {
            return;
        }

        if (element instanceof IStructureElementChain) {
            IStructureElement<IConstructable>[] elements =
                ((IStructureElementChain<IConstructable>) element).fallbacks();

            if (elements != null) {
                for (IStructureElement<IConstructable> e : elements) {
                    processElement(multiblock, e, data);
                }
            }
            return;
        }

        if (elementName.equals(StructureHacks.LAZY_ELEMENT)) {
            IStructureElement<IConstructable> realElement =
                StructureHacks.getUnderlyingElement(multiblock, element);

            if (realElement != null) {
                processElement(multiblock, realElement, data);
            }
            return;
        }

        IStructureElement<IConstructable> realElement =
            StructureHacks.getUnderlyingElement(multiblock, element);

        if (realElement != null && realElement != element) {
            processElement(multiblock, realElement, data);
            return;
        }

        processRegularElement(multiblock, element, data);
    }

    private void processElementWithInfo(IConstructable multiblock,
                                        IStructureElement<IConstructable> element,
                                        ConstructableData data,
                                        ElementBlockInfo info) {

        String elementName = element.getClass().getName();

        if (StructureHacks.SKIP_ELEMENTS.contains(elementName)) {
            info.isSkipped = true;
            return;
        }

        if (element instanceof IStructureElementChain) {
            info.isChain = true;
            IStructureElement<IConstructable>[] elements =
                ((IStructureElementChain<IConstructable>) element).fallbacks();

            if (elements != null) {
                for (IStructureElement<IConstructable> e : elements) {
                    processElementWithInfo(multiblock, e, data, info);
                }
            }
            return;
        }

        if (elementName.equals(StructureHacks.LAZY_ELEMENT)) {
            info.isLazy = true;
            IStructureElement<IConstructable> realElement =
                StructureHacks.getUnderlyingElement(multiblock, element);

            if (realElement != null) {
                processElementWithInfo(multiblock, realElement, data, info);
            }
            return;
        }

        IStructureElement<IConstructable> realElement =
            StructureHacks.getUnderlyingElement(multiblock, element);

        if (realElement != null && realElement != element) {
            processElementWithInfo(multiblock, realElement, data, info);
            return;
        }

        processRegularElementWithInfo(multiblock, element, data, info);
    }

    private void processRegularElement(IConstructable multiblock,
                                       IStructureElement<IConstructable> element,
                                       ConstructableData data) {

        Iterable<ItemStack> stacks = StructureHacks.getStacksForElement(multiblock, element, data);

        if (stacks != null) {
            for (ItemStack stack : stacks) {
                if (stack != null && stack.getItem() != null) {
                    totalBlocks += stack.stackSize;

                    uniqueBlocks.add(stack.copy());

                    String blockKey = stack.getDisplayName();
                    blockCounts.put(blockKey, blockCounts.getOrDefault(blockKey, 0) + stack.stackSize);
                }
            }
        }
    }

    private void processRegularElementWithInfo(IConstructable multiblock,
                                               IStructureElement<IConstructable> element,
                                               ConstructableData data,
                                               ElementBlockInfo info) {

        Iterable<ItemStack> stacks = StructureHacks.getStacksForElement(multiblock, element, data);

        if (stacks != null) {
            int blockCount = 0;

            for (ItemStack stack : stacks) {
                if (stack != null && stack.getItem() != null) {
                    blockCount += stack.stackSize;
                    info.blocks.add(stack.copy());
                    info.blockCounts.put(stack.getDisplayName(),
                        info.blockCounts.getOrDefault(stack.getDisplayName(), 0) + stack.stackSize);
                }
            }

            info.totalBlocks = blockCount;
            info.processed = true;
        }
    }

    public TierBlockInfo analyzeTieredElement(IConstructable multiblock,
                                              IStructureElement<IConstructable> element) {
        TierBlockInfo result = new TierBlockInfo();

        try {
            ItemStack holo = StructureHacks.HOLO_STACK.copy();
            ConstructableData data = new ConstructableData();

            for (int tier = 1; tier <= MAX_TIERS; tier++) {
                holo.stackSize = tier;

                IStructureElement.BlocksToPlace toPlace = element.getBlocksToPlace(
                    multiblock,
                    DummyWorld.INSTANCE,
                    0, 0, 0,
                    holo,
                    null
                );

                if (toPlace != null && toPlace.getStacks() != null) {
                    TierBlocks tierBlocks = new TierBlocks();
                    tierBlocks.tier = tier;

                    for (ItemStack stack : toPlace.getStacks()) {
                        if (stack != null && stack.getItem() != null) {
                            tierBlocks.blocks.add(stack.copy());
                            tierBlocks.totalBlocks += stack.stackSize;
                        }
                    }

                    result.tiers.put(tier, tierBlocks);
                    result.maxTier = tier;
                } else {
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("Error analyzing tiered element: " + e.getMessage());
        }

        return result;
    }

    public static class ElementBlockInfo {
        public String elementType;
        public boolean processed = false;
        public boolean isSkipped = false;
        public boolean isChain = false;
        public boolean isLazy = false;
        public int totalBlocks = 0;
        public Set<ItemStack> blocks = new HashSet<>();
        public Map<String, Integer> blockCounts = new HashMap<>();

        @Override
        public String toString() {
            return String.format("Element[%s]: %d blocks, %d unique types",
                elementType, totalBlocks, blocks.size());
        }
    }

    public static class TierBlockInfo {
        public Map<Integer, TierBlocks> tiers = new HashMap<>();
        public int maxTier = 0;

        public int getTotalBlocks() {
            return tiers.values().stream().mapToInt(t -> t.totalBlocks).sum();
        }

        public Set<ItemStack> getAllBlocks() {
            Set<ItemStack> all = new HashSet<>();
            tiers.values().forEach(t -> all.addAll(t.blocks));
            return all;
        }
    }

    public static class TierBlocks {
        public int tier;
        public int totalBlocks = 0;
        public Set<ItemStack> blocks = new HashSet<>();
    }

    public static void printReport(IConstructable multiblock) {
        MultiblockBlockCounter counter = new MultiblockBlockCounter();

        System.out.println("=== Multiblock Block Analysis ===");
        System.out.println("Multiblock: " + multiblock.getClass().getSimpleName());

        if (multiblock instanceof IMetaTileEntity) {
            System.out.println("Name: " + ((IMetaTileEntity) multiblock).getMetaName());
        }

        int total = counter.getTotalBlockCount(multiblock);
        System.out.println("Total blocks: " + total);

        Map<String, Integer> counts = counter.getBlockCounts(multiblock);
        System.out.println("\nBlock types:");
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("Unique blocks: " + counter.getUniqueBlocks(multiblock).size());

        Map<String, ElementBlockInfo> detailed = counter.getDetailedInfo(multiblock);
        System.out.println("\nElements analyzed: " + detailed.size());

        System.out.println("================================");
    }
}
