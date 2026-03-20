package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.MV;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
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

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import it.unimi.dsi.fastutil.Pair;

public class GTN_MediumPowerExtruder extends GTN_MultiBlockBase<GTN_MediumPowerExtruder> {

    public GTN_MediumPowerExtruder(int id, String name) {
        super(id, name);
    }

    public GTN_MediumPowerExtruder(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_MediumPowerExtruder>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "MediumPowerExtruder",
                // spotless:off
                new String[][]{
                    {" AAA ","AAAAA","AAAAA","AAAAA"," AAA "},
                    {"     ","     ","A C A","     ","     "},
                    {"     "," CCC ","BCCCB"," CCC ","     "},
                    {"     ","     ","B   B","     ","     "},
                    {" A~A ","A   A","B   B","A   A"," AAA "},
                    {" AAA ","ACCCA","ACCCA","ACCCA"," AAA "}
                },
                //spotless:on
                new MultiblockOffsets(2, 4, 0),
                new MultiblockArea(5, 6, 5),
                1,
                GTN_Casings.FrostProofMachineCasing));
    }

    @Override
    public GTN_MediumPowerExtruder createNewMetaEntity() {
        return new GTN_MediumPowerExtruder(this.mName);
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
    public IStructureDefinition<GTN_MediumPowerExtruder> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addElement('B', ofFrame(Materials.Steel))
                .addElement('C', ofBlock(GregTechAPI.sBlockMetal6, 13))
                .addElement(
                    'A',
                    ElementBuilder.create(GTN_MediumPowerExtruder.class, this)
                        .hatches(InputBus, OutputBus, Energy, Maintenance)
                        .casing(mainCasing)
                        .build()));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.extruderRecipes;
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
