package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Generators.EV.Magen;

import static gregtech.api.enums.HatchElement.InputHatch;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.GregTechAPI;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;

public class GTN_EarthModuleMagicGenerator extends GTN_MultiBlockBase<GTN_EarthModuleMagicGenerator>
    implements IMagicGeneratorModule {

    private int generate = 100;
    private int boostLevel = 0;

    public GTN_EarthModuleMagicGenerator(int id, String name) {
        super(id, name);
    }

    public GTN_EarthModuleMagicGenerator(String name) {
        super(name);
    }

    @Override
    public int generate() {
        return generate;
    }

    @Override
    public int boostLevel() {
        return boostLevel;
    }

    @Override
    public List<StructureVariant<GTN_EarthModuleMagicGenerator>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "",
                // spotless:off
                new String[][]{
                    {"A"},
                    {"B"},
                    {"B"},
                    {"~"}
                },
                //spotless:on
                new MultiblockOffsets(0, 3, 0),
                new MultiblockArea(1, 4, 1),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_EarthModuleMagicGenerator createNewMetaEntity() {
        return new GTN_EarthModuleMagicGenerator(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {

    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_EarthModuleMagicGenerator> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addMainCasing('B', b -> b.hatches(InputHatch))
                .addBlock('A', GregTechAPI.sBlockGem2, 15));
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        setDurationInSeconds(5);
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return true;
    }

    @Override
    public boolean isEnergyMultiBlock() {
        return false;
    }
}
