package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.MV;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import net.minecraft.util.EnumChatFormatting;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.MultiblockTooltipBuilder;
import it.unimi.dsi.fastutil.Pair;

public class GTN_MediumPowerExtruder extends GTN_MultiBlockBase<GTN_MediumPowerExtruder> {

    public GTN_MediumPowerExtruder(int id, String name) {
        super(id, name);
    }

    public GTN_MediumPowerExtruder(String name) {
        super(name);
    }

    @Override
    public int getOffsetHorizontal() {
        return 2;
    }

    @Override
    public int getOffsetVertical() {
        return 4;
    }

    @Override
    public int getOffsetDepth() {
        return 0;
    }

    @Override
    public GTN_Casings getMainCasings() {
        return GTN_Casings.FrostProofMachineCasing;
    }

    @Override
    public GTN_MediumPowerExtruder createNewMetaEntity() {
        return new GTN_MediumPowerExtruder(this.mName);
    }

    @Override
    public OverclockType getOverclockType() {
        return OverclockType.NormalOverclock;
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return false;
    }

    @Override
    public String[][] getShape() {
        return new String[][] { { " AAA ", "AAAAA", "AAAAA", "AAAAA", " AAA " },
            { "     ", "     ", "A C A", "     ", "     " }, { "     ", " CCC ", "BCCCB", " CCC ", "     " },
            { "     ", "     ", "B   B", "     ", "     " }, { " A~A ", "A   A", "B   B", "A   A", " AAA " },
            { " AAA ", "ACCCA", "ACCCA", "ACCCA", " AAA " } };
    }

    @Override
    public void createGtnTooltip(MultiblockTooltipBuilder builder) {
        builder.addInfo(tr("tooltip.00"))
            .addInfo(tr("tooltip.01"))
            .addInfo(tr("tooltip.02"))
            .addInfo(Constants.AUTHOR_EVGEN_WAR_GOLD)
            .beginStructureBlock(5, 6, 5, false)
            .addEnergyHatch(EnumChatFormatting.GOLD + "1", 1)
            .addInputBus(EnumChatFormatting.GOLD + "1", 1)
            .addOutputBus(EnumChatFormatting.GOLD + "1", 1);
    }

    @Override
    public IStructureDefinition<GTN_MediumPowerExtruder> getStructureDefinition() {
        return IStructureDefinition.<GTN_MediumPowerExtruder>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement('B', ofFrame(Materials.Steel))
            .addElement('C', ofBlock(GregTechAPI.sBlockMetal6, 13))
            .addElement(
                'A',
                buildHatchAdder(GTN_MediumPowerExtruder.class).atLeast(InputBus, OutputBus, Energy, Maintenance)
                    .casingIndex(getMainCasings().textureId)
                    .dot(1)
                    .buildAndChain(
                        onElementPass(
                            GTN_MediumPowerExtruder::mainCasingAdd,
                            ofBlock(getMainCasings().getBlock(), getMainCasings().meta))))
            .build();
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
    protected Pair<Integer, Integer> getMaxEnergyTier() {
        return Pair.of(VoltageIndex.MV, VoltageIndex.HV);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 10;
    }
}
