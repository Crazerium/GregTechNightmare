package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.ZPM;

import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;

import java.util.Arrays;
import java.util.List;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import it.unimi.dsi.fastutil.Pair;

public class GTN_ZeroPowerWireMill extends GTN_MultiBlockBase<GTN_ZeroPowerWireMill> {

    public GTN_ZeroPowerWireMill(int id, String name) {
        super(id, name);
    }

    public GTN_ZeroPowerWireMill(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_ZeroPowerWireMill>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "",
                // spotless:off
                new String[][]{
                    {"       ","       "," BBBBB ","       "," BBBBB ","       ","       "},
                    {"       "," B   B ","BC   CB","B     B","BC   CB"," B   B ","       "},
                    {" B   B ","       ","BC   CB","  A A  ","BC   CB","       "," B   B "},
                    {" B   B ","       ","BCA ACB"," BBBBB ","BCA ACB","       "," B   B "},
                    {" BB~BB ","BB   BB","BC   CB","B A A B","BC   CB","BB   BB"," BBBBB "}
                },
                //spotless:on
                new MultiblockOffsets(3, 4, 0),
                new MultiblockArea(7, 5, 7),
                1,
                GTN_Casings.StabilizedNaquadahWaterPlantCasing));
    }

    @Override
    public GTN_ZeroPowerWireMill createNewMetaEntity() {
        return new GTN_ZeroPowerWireMill(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addInputBus()
            .addOutputBus()
            .addMaintenanceHatch()
            .addEnergyHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_ZeroPowerWireMill> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addCasing('A', GTN_Casings.TitaniumGearBoxCasing)
                .addFrame('C', Materials.Osmium)
                .addMainCasing('B', b -> b.hatches(InputBus, OutputBus, Maintenance, Energy)));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.wiremillRecipes;
    }

    @Override
    public float getSpeedBonus() {
        return 5F;
    }

    @Override
    public float getEuModifier() {
        return 0.7F;
    }

    @Override
    protected Pair<Integer, Integer> getMinMaxEnergyTier() {
        return Pair.of(VoltageIndex.ZPM, VoltageIndex.UV);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 128;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GTCEU_LOOP_MOTOR;
    }
}
