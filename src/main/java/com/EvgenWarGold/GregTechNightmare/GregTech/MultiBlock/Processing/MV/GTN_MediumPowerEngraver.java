package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.MV;

import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;

import java.util.Arrays;
import java.util.List;

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
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import it.unimi.dsi.fastutil.Pair;

public class GTN_MediumPowerEngraver extends GTN_MultiBlockBase<GTN_MediumPowerEngraver> {

    private final CasingData glass = createCasingData("glass");

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
    public IStructureDefinition<GTN_MediumPowerEngraver> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addAllGlasses('B', glass)
                .addMainCasing('A', b -> b.hatches(InputBus, OutputBus, Energy, Maintenance)));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.laserEngraverRecipes;
    }

    @Override
    public float getSpeedBonus() {
        return 1.20F;
    }

    @Override
    protected Pair<Integer, Integer> getMinMaxEnergyTier() {
        return Pair.of(VoltageIndex.MV, VoltageIndex.HV);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 10;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GTCEU_LOOP_ELECTROLYZER;
    }
}
