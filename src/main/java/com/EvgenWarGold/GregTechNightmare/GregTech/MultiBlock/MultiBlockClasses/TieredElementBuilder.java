package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureUtility;

import gregtech.api.interfaces.IHatchElement;
import gregtech.api.util.GTStructureUtility;

public class TieredElementBuilder<T> {

    private final TierData tierData;
    private final Class<T> tileClass;

    private GTN_Casings[] casings = new GTN_Casings[0];
    private IHatchElement<? super T>[] hatches;
    private int dot = 1;

    public TieredElementBuilder(TierData tierData, Class<T> tileClass) {
        this.tierData = tierData;
        this.tileClass = tileClass;
    }

    public static <T> TieredElementBuilder<T> create(TierData tierData, Class<T> tileClass) {
        return new TieredElementBuilder<>(tierData, tileClass);
    }

    public TieredElementBuilder<T> casings(GTN_Casings... casings) {
        this.casings = casings;
        return this;
    }

    @SafeVarargs
    public final TieredElementBuilder<T> hatches(IHatchElement<? super T>... hatches) {
        this.hatches = hatches;
        this.dot = 1;
        return this;
    }

    @SafeVarargs
    public final TieredElementBuilder<T> hatches(int dot, IHatchElement<? super T>... hatches) {
        this.hatches = hatches;
        this.dot = dot;
        return this;
    }

    public IStructureElement<T> build() {
        IStructureElement<T> blocks = GTN_StructureUtility.createTierBlocks(tierData, casings);

        if (hatches == null) {
            return StructureUtility.withChannel(
                tierData.getChannelName(),
                blocks);
        }

        return StructureUtility.withChannel(
            tierData.getChannelName(),
            StructureUtility.ofChain(
                GTStructureUtility.buildHatchAdder(tileClass)
                    .atLeast(hatches)
                    .casingIndex(tierData.getCasingTextureId())
                    .dot(dot)
                    .buildAndChain(GTN_StructureUtility.createTierBlocks(tierData, casings))));
    }
}
