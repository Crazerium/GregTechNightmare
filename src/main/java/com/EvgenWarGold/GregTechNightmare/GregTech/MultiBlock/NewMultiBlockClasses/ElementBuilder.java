package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses;

import java.util.ArrayList;
import java.util.List;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureUtility;

import gregtech.api.interfaces.IHatchElement;
import gregtech.api.util.GTStructureUtility;

@SuppressWarnings("unchecked")
public class ElementBuilder<T> {

    private final Class<T> tileClass;

    private GTN_Casings casing;
    private IHatchElement<? super T>[] hatches;
    private int dot = 1;
    private final GTN_NewMultiBlockBase<?> multiblock;
    private boolean hasSteamHatch;

    public ElementBuilder(Class<T> tileClass, GTN_NewMultiBlockBase<?> multiblock) {
        this.multiblock = multiblock;
        this.tileClass = tileClass;
    }

    public static <T> ElementBuilder<T> create(Class<T> tileClass, GTN_NewMultiBlockBase<?> multiblock) {
        return new ElementBuilder<>(tileClass, multiblock);
    }

    public ElementBuilder<T> casing(GTN_Casings casing) {
        this.casing = casing;
        return this;
    }

    @SafeVarargs
    public final ElementBuilder<T> hatches(IHatchElement<? super T>... hatches) {
        this.hatches = hatches;
        this.dot = 1;
        return this;
    }

    @SafeVarargs
    public final ElementBuilder<T> hatches(int dot, IHatchElement<? super T>... hatches) {
        this.hatches = hatches;
        this.dot = dot;
        return this;
    }

    public final ElementBuilder<T> hatches(GTN_NewHatchElement... hatches) {
        List<IHatchElement<? super T>> filtered = new ArrayList<>();

        for (GTN_NewHatchElement hatchElement : hatches) {
            if (hatchElement != GTN_NewHatchElement.SteamInputHatch) {
                filtered.add((IHatchElement<? super T>) hatchElement);
            } else hasSteamHatch = true;
        }

        this.hatches = filtered.toArray(new IHatchElement[0]);
        this.dot = 1;
        return this;
    }

    public IStructureElement<T> build() {
        IStructureElement<T> element = GTStructureUtility.buildHatchAdder(tileClass)
            .atLeast(hatches)
            .casingIndex(casing.textureId)
            .dot(dot)
            .buildAndChain(
                StructureUtility.onElementPass(
                    x -> multiblock.mainCasingCount++,
                    StructureUtility.ofBlock(casing.getBlock(), casing.meta)));

        if (hasSteamHatch) {
            return StructureUtility.ofChain(
                GTStructureUtility.buildHatchAdder(tileClass)
                    .atLeast((IHatchElement<? super T>) GTN_NewHatchElement.SteamInputHatch)
                    .casingIndex(casing.textureId)
                    .hatchIds(31_040)
                    .dot(dot)
                    .build(),
                element);
        }
        return element;
    }
}
