package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.EV;

import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
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
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import it.unimi.dsi.fastutil.Pair;

public class GTN_ExtremePowerCircuitAssembler extends GTN_MultiBlockBase<GTN_ExtremePowerCircuitAssembler> {

    private final CasingData glass = createCasingData("glass");

    public GTN_ExtremePowerCircuitAssembler(int id, String name) {
        super(id, name);
    }

    public GTN_ExtremePowerCircuitAssembler(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_ExtremePowerCircuitAssembler>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "ExtremePowerCircuitAssembler",
                // spotless:off
                new String[][]{
                    {"DDDDDDD ","DBBBCC  ","DAAACC  ","DBBBCC  ","DDDDDDD "},
                    {"DEEECCCD","B   CCC ","A   CCC ","B   CCC ","DEEECCCD"},
                    {"DEEEC~CD","B   CCC ","A   CCC ","B   CCC ","DEEECCCD"},
                    {"DEEECCCD","B   CCC ","A   CCC ","B   CCC ","DEEECCCD"},
                    {"DDDDDDDD","DCCCCCCD","DCCCCCCD","DCCCCCCD","DDDDDDDD"}
                },
                //spotless:on
                new MultiblockOffsets(5, 2, 0),
                new MultiblockArea(8, 5, 5),
                1,
                GTN_Casings.StableTitaniumMachineCasing));
    }

    @Override
    public GTN_ExtremePowerCircuitAssembler createNewMetaEntity() {
        return new GTN_ExtremePowerCircuitAssembler(this.mName);
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
    public IStructureDefinition<GTN_ExtremePowerCircuitAssembler> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addMainCasing('C', b -> b.hatches(InputBus, OutputBus, Energy, Maintenance, InputHatch))
                .addFrame('D', Materials.Aluminium)
                .addAllGlasses('E', glass)
                .addCasing('B', GTN_Casings.TitaniumPipeCasing)
                .addCasing('A', GTN_Casings.TitaniumGearBoxCasing));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.circuitAssemblerRecipes;
    }

    @Override
    public float getSpeedBonus() {
        return 1.35F;
    }

    @Override
    protected Pair<Integer, Integer> getMinMaxEnergyTier() {
        return Pair.of(VoltageIndex.EV, VoltageIndex.IV);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 20;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GTCEU_LOOP_ASSEMBLER;
    }
}
