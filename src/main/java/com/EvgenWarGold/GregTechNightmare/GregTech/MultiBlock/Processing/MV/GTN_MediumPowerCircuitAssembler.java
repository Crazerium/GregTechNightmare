package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.MV;

import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.Arrays;
import java.util.List;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.ElementBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.enums.Materials;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import it.unimi.dsi.fastutil.Pair;

public class GTN_MediumPowerCircuitAssembler extends GTN_MultiBlockBase<GTN_MediumPowerCircuitAssembler> {

    public GTN_MediumPowerCircuitAssembler(int id, String name) {
        super(id, name);
    }

    public GTN_MediumPowerCircuitAssembler(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_MediumPowerCircuitAssembler>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "MediumPowerCircuitAssembler",
                // spotless:off
                new String[][]{
                    {" B   AAA ","AA   AAAA"," B   AAA "},
                    {"A~     AA","AABBABAAA","AA     AA"},
                    {"AABBABAAA","AABBABAAA","AABBABAAA"}
                },
                //spotless:on
                new MultiblockOffsets(1, 1, 0),
                new MultiblockArea(9, 3, 3),
                1,
                GTN_Casings.FrostProofMachineCasing));
    }

    @Override
    public GTN_MediumPowerCircuitAssembler createNewMetaEntity() {
        return new GTN_MediumPowerCircuitAssembler(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addInputBus()
            .addOutputBus()
            .addInputHatch()
            .addEnergyHatch()
            .addMaintenanceHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_MediumPowerCircuitAssembler> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addElement('B', ofFrame(Materials.Steel))
                .addElement(
                    'A',
                    ElementBuilder.create(GTN_MediumPowerCircuitAssembler.class, this)
                        .hatches(InputBus, OutputBus, Energy, Maintenance, InputHatch)
                        .casing(mainCasing)
                        .build()));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.circuitAssemblerRecipes;
    }

    @Override
    public float getSpeedBonus() {
        return 0.80F;
    }

    @Override
    protected Pair<Integer, Integer> getMinMaxEnergyTier() {
        return Pair.of(VoltageIndex.MV, VoltageIndex.HV);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 10;
    }
}
