package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.MV;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.ElementBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.GTN_NewMultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import gregtech.api.enums.Materials;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import it.unimi.dsi.fastutil.Pair;

import java.util.Arrays;
import java.util.List;

import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.ofFrame;

public class GTN_MediumPowerBender extends GTN_NewMultiBlockBase<GTN_MediumPowerBender> {

    public GTN_MediumPowerBender(int id, String name) {
        super(id, name);
    }

    public GTN_MediumPowerBender(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_MediumPowerBender>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "MediumPowerAssembler",
                // spotless:off
                new String[][]{
                    {"BB   B","AAAAAA","BB   B"},
                    {"A~   A","AA   A","AA   A"},
                    {"AABBBA","AAAAAA","AABBBA"}
                },
                //spotless:on
                new MultiblockOffsets(1, 1, 0),
                new MultiblockArea(6, 3, 3),
                1,
                GTN_Casings.FrostProofMachineCasing));
    }

    @Override
    public GTN_MediumPowerBender createNewMetaEntity() {
        return new GTN_MediumPowerBender(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder
            .addInputBus()
            .addOutputBus()
            .addEnergyHatch()
            .addMaintenanceHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_MediumPowerBender> getStructureDefinition() {
        return buildStructureDefinition(builder -> builder
            .addElement('B', ofFrame(Materials.Steel))
            .addElement('A', ElementBuilder.create(GTN_MediumPowerBender.class, this)
                .hatches(InputBus, OutputBus, Energy, Maintenance)
                .casing(mainCasing)
                .build())
        );
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.benderRecipes;
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
