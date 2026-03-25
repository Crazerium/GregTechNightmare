package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.gtnewhorizon.structurelib.structure.StructureUtility;
import cpw.mods.fml.relauncher.ReflectionHelper;
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
    private static final ConstructableData data = new ConstructableData();
    private final Map<IStructureElement<IConstructable>, Integer> elementCountMap = new HashMap<>();
    public Map<String, Integer> getBlockCounts(IConstructable multiblock) {
        analyzeMultiblock(multiblock);
        return new HashMap<>(blockCounts);
    }

    @SuppressWarnings("unchecked")
    private void analyzeMultiblock(IConstructable multiblock) {
        blockCounts.clear();
        elementCountMap.clear();

        IStructureDefinition<?> structure = multiblock.getStructureDefinition();

        if (structure instanceof StructureDefinition) {
            StructureDefinition<IConstructable> structDef = (StructureDefinition<IConstructable>) structure;

            Collection<IStructureElement<IConstructable>[]> structures = structDef.getStructures()
                .values();

            for (IStructureElement<IConstructable>[] elementArray : structures) {
                if (elementArray == null) continue;

                for (IStructureElement<IConstructable> element : elementArray) {
                    if (element == null) continue;

                    elementCountMap.put(element, elementCountMap.getOrDefault(element, 0) + 1);
                }
            }

            for (Map.Entry<IStructureElement<IConstructable>, Integer> entry : elementCountMap.entrySet()) {
                IStructureElement<IConstructable> element = entry.getKey();
                Integer count = entry.getValue();

                String channel = getChannelFromElement(element);

                if (channel != null && !channel.isEmpty()) {
                    blockCounts.put(channel, count);
                    continue;
                }

                newPrecessElement(multiblock, element, count);
            }
        }
    }

    private void newPrecessElement(IConstructable multiblock, IStructureElement<IConstructable> element, Integer count) {
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
                    newPrecessElement(multiblock, e, count);
                }
            }
            return;
        }

        if (elementName.equals(StructureHacks.LAZY_ELEMENT)) {
            IStructureElement<IConstructable> realElement = StructureHacks.getUnderlyingElement(multiblock, element);

            if (realElement != null) {
                newPrecessElement(multiblock, realElement, count);
            }
            return;
        }

        IStructureElement<IConstructable> realElement = StructureHacks.getUnderlyingElement(multiblock, element);

        if (realElement != null && realElement != element) {
            newPrecessElement(multiblock, realElement, count);
            return;
        }

        Iterable<ItemStack> stacks = StructureHacks.getStacksForElement(multiblock, element, data);

        if (stacks != null) {
            for (ItemStack stack : stacks) {
                if (stack != null && stack.getItem() != null) {
                    String blockKey = stack.getDisplayName();
                    blockCounts.put(blockKey, count);
                    return;
                }
            }
        }
    }

    private String getChannelFromElement(IStructureElement<IConstructable> element) {
        try {
            Class<?> elementClass = element.getClass();
            Field channelField;
            try {
                channelField = elementClass.getDeclaredField("val$channel");
            } catch (NoSuchFieldException e1) {
                try {
                    channelField = elementClass.getDeclaredField("channel");
                } catch (NoSuchFieldException e2) {
                    return null;
                }
            }

            channelField.setAccessible(true);
            return (String) channelField.get(element);

        } catch (Exception e) {
            return null;
        }
    }
}
