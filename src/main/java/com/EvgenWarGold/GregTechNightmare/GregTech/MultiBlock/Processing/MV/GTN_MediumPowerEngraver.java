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

public class GTN_MediumPowerEngraver extends GTN_NewMultiBlockBase<GTN_MediumPowerEngraver> {

    public GTN_MediumPowerEngraver(int id, String name) {
        super(id, name);
    }

    public GTN_MediumPowerEngraver(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_MediumPowerEngraver>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "MediumPowerEngraver",
                // spotless:off
                new String[][]{
                    {" ABBA"," ABBA"," ABBA"},
                    {" ~BBA"," ABBA","AABBA"},
                    {" AAAA","AAAAA","AAAAA"}
                },
                //spotless:on
                new MultiblockOffsets(1, 1, 0),
                new MultiblockArea(5, 3, 3),
                1,
                GTN_Casings.FrostProofMachineCasing));
    }

    @Override
    public GTN_MediumPowerEngraver createNewMetaEntity() {
        return new GTN_MediumPowerEngraver(this.mName);
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
    public IStructureDefinition<GTN_MediumPowerEngraver> getStructureDefinition() {
        return buildStructureDefinition(builder -> builder
            .addElement('B', GTN_Casings.TintedGlassBlack.asElement())
            .addElement(
                'A',
                ElementBuilder.create(GTN_MediumPowerEngraver.class, this)
                    .hatches(InputBus, OutputBus, Energy, Maintenance)
                    .casing(mainCasing)
                    .build()));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.laserEngraverRecipes;
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
