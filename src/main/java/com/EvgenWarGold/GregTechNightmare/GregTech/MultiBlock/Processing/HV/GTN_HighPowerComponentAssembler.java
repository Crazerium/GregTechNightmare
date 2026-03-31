package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.HV;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.CasingData;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import goodgenerator.api.recipe.GoodGeneratorRecipeMaps;
import goodgenerator.util.ItemRefer;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import it.unimi.dsi.fastutil.Pair;

import java.util.Arrays;
import java.util.List;

import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;

public class GTN_HighPowerComponentAssembler extends GTN_MultiBlockBase<GTN_HighPowerComponentAssembler> {

    private final CasingData component = createCasingData("component");

    public GTN_HighPowerComponentAssembler(int id, String name) {
        super(id, name);
    }

    public GTN_HighPowerComponentAssembler(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_HighPowerComponentAssembler>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "HighPowerComponentAssembler",
                // spotless:off
                new String[][]{
                    {"          "," EECEECEED","   D  D   "},
                    {"DAAAAAAAAD","D  D  D  D","DAAAAAAAAD"},
                    {"~AAAAAAAAD","D        D","DAAAAAAAAD"},
                    {"DDDDDDDDDD","DBBBBBBBBD","DDDDDDDDDD"}
                },
                //spotless:on
                new MultiblockOffsets(0, 2, 0),
                new MultiblockArea(10, 4, 3),
                1,
                GTN_Casings.CleanStainlessSteelMachineCasing));
    }

    @Override
    public GTN_HighPowerComponentAssembler createNewMetaEntity() {
        return new GTN_HighPowerComponentAssembler(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addInputHatch()
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
    public IStructureDefinition<GTN_HighPowerComponentAssembler> getStructureDefinition() {
        return buildStructureDefinition(builder -> builder
            .addAllGlasses('A')
            .addTierBlock('B', component,
                ItemRefer.Compassline_Casing_LV.get(1),
                ItemRefer.Compassline_Casing_MV.get(1),
                ItemRefer.Compassline_Casing_HV.get(1),
                ItemRefer.Compassline_Casing_EV.get(1))
            .addCasing('C', GTN_Casings.SteelGearBoxCasing)
            .addFrame('E', Materials.StainlessSteel)
            .addMainCasing('D', b -> b
                .hatches(InputHatch, InputBus, OutputBus, Maintenance, Energy)));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GoodGeneratorRecipeMaps.componentAssemblyLineRecipes;
    }

    @Override
    public float getSpeedBonus() {
        return 1.20F;
    }

    @Override
    protected Pair<Integer, Integer> getMinMaxEnergyTier() {
        return Pair.of(VoltageIndex.HV, VoltageIndex.EV);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 10;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_MULTI_PRECISE_LOOP;
    }
}
