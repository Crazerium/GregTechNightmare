package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.MV;

import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;

import java.util.Arrays;
import java.util.List;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.ElementBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.GTN_NewMultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.enums.VoltageIndex;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import it.unimi.dsi.fastutil.Pair;

public class GTN_MediumPowerWireMill extends GTN_NewMultiBlockBase<GTN_MediumPowerWireMill> {

    public GTN_MediumPowerWireMill(int id, String name) {
        super(id, name);
    }

    public GTN_MediumPowerWireMill(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_MediumPowerWireMill>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "MediumPowerWireMill",
                // spotless:off
                new String[][]{
                    {"A  A    ","A  A    ","A  A    "},
                    {"~  A   A","ACCCCCCA","A  A   A"},
                    {"AAAAAAAA","ABBBBBBA","AAAAAAAA"}
                },
                //spotless:on
                new MultiblockOffsets(0, 1, 0),
                new MultiblockArea(8, 3, 3),
                1,
                GTN_Casings.FrostProofMachineCasing));
    }

    @Override
    public GTN_MediumPowerWireMill createNewMetaEntity() {
        return new GTN_MediumPowerWireMill(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addInputBus()
            .addOutputBus()
            .addEnergyHatch()
            .addMaintenanceHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_MediumPowerWireMill> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addElement('B', GTN_Casings.SteelGearBoxCasing.asElement())
                .addElement('C', GTN_Casings.TintedGlassBlack.asElement())
                .addElement(
                    'A',
                    ElementBuilder.create(GTN_MediumPowerWireMill.class, this)
                        .hatches(InputBus, OutputBus, Energy, Maintenance)
                        .casing(mainCasing)
                        .build()));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.wiremillRecipes;
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
