package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.STEAM;

import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.Arrays;
import java.util.List;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.ElementBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.GTN_NewHatchElement;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.GTN_NewMultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.recipe.RecipeMap;

public class GTN_AdvancedBBF extends GTN_NewMultiBlockBase<GTN_AdvancedBBF> {

    public GTN_AdvancedBBF(int id, String name) {
        super(id, name);
    }

    public GTN_AdvancedBBF(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_AdvancedBBF>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "AdvancedBBF",
                // spotless:off
                new String[][]{
                    {"     "," AAA "," A A "," AAA ","     "},
                    {" AAA ","A   A","A A A","A   A"," AAA "},
                    {" AAA ","A   A","A A A","A   A"," AAA "},
                    {"AAAAA","A   A","A A A","A   A","AAAAA"},
                    {"BA~AB","AAAAA","AAAAA","AAAAA","BAAAB"},
                    {"B   B","     ","     ","     ","B   B"}
                },
                //spotless:on
                new MultiblockOffsets(2, 4, 0),
                1,
                GTN_Casings.Firebricks));
    }

    @Override
    public GTN_AdvancedBBF createNewMetaEntity() {
        return new GTN_AdvancedBBF(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addSteamInputBus()
            .addSteamOutputBus();
    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public MultiblockArea getMultiblockArea() {
        return new MultiblockArea(5, 6, 5);
    }

    @Override
    public IStructureDefinition<GTN_AdvancedBBF> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addElement(
                'A',
                ElementBuilder.create(GTN_AdvancedBBF.class, this)
                    .casing(mainCasing)
                    .hatches(GTN_NewHatchElement.SteamInputBus, GTN_NewHatchElement.SteamOutputBus)
                    .build())
                .addElement('B', ofFrame(Materials.Steel)));
    }

    @Override
    public boolean isEnergyMultiBlock() {
        return false;
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTN_Recipe.AdvancedBBFRecipes;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GTCEU_LOOP_FIRE;
    }
}
