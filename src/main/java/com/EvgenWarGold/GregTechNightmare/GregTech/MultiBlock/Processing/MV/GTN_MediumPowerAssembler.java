package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.MV;

import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import it.unimi.dsi.fastutil.Pair;

public class GTN_MediumPowerAssembler extends GTN_MultiBlockBase<GTN_MediumPowerAssembler> {

    public GTN_MediumPowerAssembler(int id, String name) {
        super(id, name);
    }

    public GTN_MediumPowerAssembler(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_MediumPowerAssembler>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "MediumPowerAssembler",
                // spotless:off
                new String[][]{
                    {" C   C "," C   C "," C   C "," CCCCC "," C   C "},
                    {" C   C ","       ","       ","  B    "," C   C "},
                    {" C   C ","       ","       ","       "," C   C "},
                    {" C   C "," AAAAA "," AAAAA "," AAAAA "," C   C "},
                    {" AA~AA ","AAAAAAA","AAAAAAA","AAAAAAA"," AAAAA "}
                },
                //spotless:on
                new MultiblockOffsets(3, 4, 0),
                new MultiblockArea(7, 5, 5),
                1,
                GTN_Casings.FrostProofMachineCasing));
    }

    @Override
    public GTN_MediumPowerAssembler createNewMetaEntity() {
        return new GTN_MediumPowerAssembler(this.mName);
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
    public IStructureDefinition<GTN_MediumPowerAssembler> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addElement('C', ofFrame(Materials.Steel))
                .addElement('B', GTN_Casings.SteelGearBoxCasing.asElement())
                .addElement(
                    'A',
                    ElementBuilder.create(GTN_MediumPowerAssembler.class, this)
                        .hatches(InputBus, OutputBus, Energy, Maintenance, InputHatch)
                        .casing(mainCasing)
                        .build()));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.assemblerRecipes;
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

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_MULTI_PRECISE_LOOP;
    }
}
