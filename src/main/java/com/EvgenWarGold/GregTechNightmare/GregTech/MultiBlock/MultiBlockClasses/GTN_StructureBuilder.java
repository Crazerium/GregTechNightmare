package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import static com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_StructureUtility.createAllTierCoilBlock;
import static com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_StructureUtility.createAllTieredGlass;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.function.Consumer;

import net.minecraft.block.Block;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;

import gregtech.api.enums.Materials;
import gtPlusPlus.core.block.base.BasicBlock;
import gtPlusPlus.core.block.base.BlockBaseModular;
import gtPlusPlus.core.material.Material;

public class GTN_StructureBuilder<T> {

    private final StructureDefinition.Builder<T> builder;
    private final GTN_MultiBlockBase<?> multiblock;
    private final Class<?> multiblockClass;

    public GTN_StructureBuilder(GTN_MultiBlockBase<?> multiblock) {
        this.builder = StructureDefinition.builder();
        this.multiblock = multiblock;
        this.multiblockClass = multiblock.getClass();
    }

    public GTN_StructureBuilder<T> addShape(String name, String[][] shape) {
        builder.addShape(name, shape);
        return this;
    }

    public GTN_StructureBuilder<T> addElement(char name, IStructureElement<T> element) {
        builder.addElement(name, element);
        return this;
    }

    public GTN_StructureBuilder<T> addCasing(char name, GTN_Casings casings) {
        builder.addElement(name, casings.asElement());
        return this;
    }

    public GTN_StructureBuilder<T> addFrame(char name, Materials materials) {
        builder.addElement(name, ofFrame(materials));
        return this;
    }

    public GTN_StructureBuilder<T> addFrame(char name, Material materials) {
        builder.addElement(
            name,
            StructureUtility.ofBlock(BlockBaseModular.getMaterialBlock(materials, BasicBlock.BlockTypes.FRAME), 0));
        return this;
    }

    public GTN_StructureBuilder<T> addAllGlasses(char name, CasingData data) {
        builder.addElement(name, createAllTieredGlass(data));
        return this;
    }

    public GTN_StructureBuilder<T> addAllGlasses(char name) {
        builder.addElement(name, chainAllGlasses());
        return this;
    }

    @SuppressWarnings("unchecked")
    public GTN_StructureBuilder<T> addAllCoil(char name, CasingData data) {
        builder.addElement(name, (IStructureElement<T>) createAllTierCoilBlock(data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public GTN_StructureBuilder<T> addMainCasing(char name, Consumer<ElementBuilder<T>> consumer) {
        ElementBuilder<T> elementBuilder = (ElementBuilder<T>) ElementBuilder.create(multiblockClass, multiblock);
        elementBuilder.casing(multiblock.mainCasing);
        consumer.accept(elementBuilder);
        builder.addElement(name, elementBuilder.build());
        return this;
    }

    @SuppressWarnings("unchecked")
    public GTN_StructureBuilder<T> addTierCasing(char name, CasingData data,
        Consumer<TieredElementBuilder<T>> consumer) {
        TieredElementBuilder<T> elementBuilder = (TieredElementBuilder<T>) TieredElementBuilder
            .create(data, multiblockClass);
        consumer.accept(elementBuilder);
        builder.addElement(name, elementBuilder.build());
        return this;
    }

    public GTN_StructureBuilder<T> addTierCasing(char name, CasingData data, GTN_Casings... casings) {
        builder.addElement(
            name,
            StructureUtility.withChannel(data.getChannelName(), GTN_StructureUtility.createTierBlocks(data, casings)));
        return this;
    }

    public GTN_StructureBuilder<T> addTierBlock(char name, CasingData data, Block... blocks) {
        builder.addElement(
            name,
            StructureUtility.withChannel(data.getChannelName(), GTN_StructureUtility.createTierBlocks(data, blocks)));
        return this;
    }

    public IStructureDefinition<T> build() {
        return builder.build();
    }
}
