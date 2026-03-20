package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.LV;

import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.ElementBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_StructureUtility;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.TierData;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.RecipeMap;

public class GTN_GasCollector extends GTN_MultiBlockBase<GTN_GasCollector> {

    TierData glass = createTierData("glass");

    public GTN_GasCollector(int id, String name) {
        super(id, name);
    }

    public GTN_GasCollector(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_GasCollector>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "GasCollector",
                // spotless:off
                new String[][]{
                    {"BBBBB","BBBBB","BBBBB","BBBBB","BBBBB"},
                    {"CAAAC","A   A","A   A","A   A","CAAAC"},
                    {"CAAAC","A   A","A   A","A   A","CAAAC"},
                    {"CAAAC","A   A","A   A","A   A","CAAAC"},
                    {"CAAAC","A   A","A   A","A   A","CAAAC"},
                    {"CAAAC","A   A","A   A","A   A","CAAAC"},
                    {"BB~BB","BBBBB","BBBBB","BBBBB","BBBBB"}
                },
                //spotless:on
                new MultiblockOffsets(2, 6, 0),
                new MultiblockArea(5, 7, 5),
                1,
                GTN_Casings.HeatProofMachineCasing));
    }

    @Override
    public GTN_GasCollector createNewMetaEntity() {
        return new GTN_GasCollector(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addInputBus()
            .addOutputHatch()
            .addEnergyHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.CRAZER;
    }

    @Override
    public IStructureDefinition<GTN_GasCollector> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addElement('C', ofFrame(Materials.Steel))
                .addElement('A', GTN_StructureUtility.createAllTieredGlass(glass))
                .addElement(
                    'B',
                    ElementBuilder.create(GTN_GasCollector.class, this)
                        .casing(mainCasing)
                        .hatches(InputBus, OutputHatch, Energy)
                        .build()));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTN_Recipe.GasCollectorRecipes;
    }

    @Override
    public boolean GTN_checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        for (MTEHatchEnergy hatch : mEnergyHatches) {
            if (hatch.mTier > glass.getCasingTier()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return true;
    }
}
