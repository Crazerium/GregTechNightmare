package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import java.util.ArrayList;
import java.util.List;

import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureUtility;

import gregtech.api.interfaces.IHatchElement;
import gregtech.api.util.GTStructureUtility;

@SuppressWarnings("unchecked")
public class TieredElementBuilder<T> {

    private final TierData tierData;
    private final Class<T> tileClass;

    private GTN_Casings[] casings = new GTN_Casings[0];
    private IHatchElement<? super T>[] hatches;
    private IHatchElement<? super T>[] customHatches;
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

    public final TieredElementBuilder<T> customHatches(GTN_HatchElement... hatches) {
        this.customHatches = (IHatchElement<? super T>[]) hatches;
        this.dot = 1;
        return this;
    }

    private IHatchElement<? super T>[] safeHatches(IHatchElement<? super T>[] array) {
        return array != null && array.length > 0 ? array : null;
    }

    public IStructureElement<T> build() {
        IStructureElement<T> blocks = GTN_StructureUtility.createTierBlocks(tierData, casings);

        if (hatches == null && customHatches == null) {
            return StructureUtility.withChannel(tierData.getChannelName(), blocks);
        }

        IHatchElement<? super T>[] hatchesToUse = safeHatches(this.hatches);
        IHatchElement<? super T>[] customToUse = safeHatches(this.customHatches);

        IStructureElement<T> customHatches = null;
        if (customToUse != null) {
            customHatches = GTStructureUtility.buildHatchAdder(tileClass)
                .atLeast(customToUse)
                .casingIndex(tierData.getCasingTextureId())
                .dot(dot)
                .build();
        }

        IStructureElement<T> hatches = null;
        if (hatchesToUse != null) {
            hatches = GTStructureUtility.buildHatchAdder(tileClass)
                .atLeast(hatchesToUse)
                .casingIndex(tierData.getCasingTextureId())
                .dot(dot)
                .build();
        }

        List<IStructureElement<T>> chain = new ArrayList<>();
        if (customHatches != null) chain.add(customHatches);
        if (hatches != null) chain.add(hatches);
        chain.add(blocks);

        @SuppressWarnings("unchecked")
        IStructureElement<T>[] array = chain.toArray(new IStructureElement[0]);

        return StructureUtility.withChannel(tierData.getChannelName(), StructureUtility.ofChain(array));
    }
}
