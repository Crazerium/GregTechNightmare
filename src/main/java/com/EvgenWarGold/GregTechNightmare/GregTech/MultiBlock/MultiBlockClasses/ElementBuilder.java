package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import java.util.ArrayList;
import java.util.List;

import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnewhorizon.structurelib.structure.adders.ITileAdder;

import gregtech.api.interfaces.IHatchElement;
import gregtech.api.util.GTStructureUtility;

@SuppressWarnings("unchecked")
public class ElementBuilder<T> {

    private final Class<T> tileClass;

    private GTN_Casings casing;
    private IHatchElement<? super T>[] hatches;
    private int dot = 1;
    private final GTN_MultiBlockBase<?> multiblock;
    private ITileAdder<T> tileAdder;
    private boolean hasTileAdder = false;
    private boolean hasSteamHatch;

    public ElementBuilder(Class<T> tileClass, GTN_MultiBlockBase<?> multiblock) {
        this.multiblock = multiblock;
        this.tileClass = tileClass;
    }

    public static <T> ElementBuilder<T> create(Class<T> tileClass, GTN_MultiBlockBase<?> multiblock) {
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

    public ElementBuilder<T> tileAdder(ITileAdder<T> tileAdder) {
        this.tileAdder = tileAdder;
        this.hasTileAdder = true;
        return this;
    }

    public final ElementBuilder<T> hatches(GTN_HatchElement... hatches) {
        List<IHatchElement<? super T>> filtered = new ArrayList<>();

        for (GTN_HatchElement hatchElement : hatches) {
            if (hatchElement != GTN_HatchElement.SteamInputHatch) {
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
                    .atLeast((IHatchElement<? super T>) GTN_HatchElement.SteamInputHatch)
                    .casingIndex(casing.textureId)
                    .hatchIds(31_040)
                    .dot(dot)
                    .build(),
                element);
        }

        if (hasTileAdder) {
            return StructureUtility
                .ofChain(element, StructureUtility.ofTileAdder(tileAdder, casing.getBlock(), casing.meta));
        }

        return element;
    }
}
