package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.IStructureElementChain;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import blockrenderer6343.client.utils.ConstructableData;
import blockrenderer6343.integration.nei.StructureHacks;

public class MultiblockBlockCounter {

    private final Map<String, Integer> blockCounts = new HashMap<>();

    public Map<String, Integer> getBlockCounts(IConstructable multiblock) {
        analyzeMultiblock(multiblock);
        return new HashMap<>(blockCounts);
    }

    @SuppressWarnings("unchecked")
    private void analyzeMultiblock(IConstructable multiblock) {
        blockCounts.clear();

        IStructureDefinition<?> structure = multiblock.getStructureDefinition();

        if (structure instanceof StructureDefinition) {
            StructureDefinition<IConstructable> structDef = (StructureDefinition<IConstructable>) structure;

            Collection<IStructureElement<IConstructable>[]> structures = structDef.getStructures()
                .values();

            ConstructableData data = new ConstructableData();

            for (IStructureElement<IConstructable>[] elementArray : structures) {
                if (elementArray == null) continue;

                for (IStructureElement<IConstructable> element : elementArray) {
                    if (element == null) continue;

                    processElement(multiblock, element, data);
                }
            }
        }
    }

    private void processElement(IConstructable multiblock, IStructureElement<IConstructable> element,
        ConstructableData data) {

        String elementName = element.getClass()
            .getName();

        if (StructureHacks.SKIP_ELEMENTS.contains(elementName)) {
            return;
        }

        if (element instanceof IStructureElementChain) {
            IStructureElement<IConstructable>[] elements = ((IStructureElementChain<IConstructable>) element)
                .fallbacks();

            if (elements != null) {
                for (IStructureElement<IConstructable> e : elements) {
                    processElement(multiblock, e, data);
                }
            }
            return;
        }

        if (elementName.equals(StructureHacks.LAZY_ELEMENT)) {
            IStructureElement<IConstructable> realElement = StructureHacks.getUnderlyingElement(multiblock, element);

            if (realElement != null) {
                processElement(multiblock, realElement, data);
            }
            return;
        }

        IStructureElement<IConstructable> realElement = StructureHacks.getUnderlyingElement(multiblock, element);

        if (realElement != null && realElement != element) {
            processElement(multiblock, realElement, data);
            return;
        }

        processRegularElement(multiblock, element, data);
    }

    private void processRegularElement(IConstructable multiblock, IStructureElement<IConstructable> element,
        ConstructableData data) {

        Iterable<ItemStack> stacks = StructureHacks.getStacksForElement(multiblock, element, data);

        if (stacks != null) {
            for (ItemStack stack : stacks) {
                if (stack != null && stack.getItem() != null) {
                    String blockKey = stack.getDisplayName();
                    blockCounts.put(blockKey, blockCounts.getOrDefault(blockKey, 0) + stack.stackSize);
                }
            }
        }
    }
}
