package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Example;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.MultiblockTooltipBuilder;
import net.minecraft.util.EnumChatFormatting;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.Dynamo;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.Muffler;
import static gregtech.api.enums.HatchElement.MultiAmpEnergy;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

public class GTN_TestMultiBlock extends GTN_MultiBlockBase<GTN_TestMultiBlock> {

    public GTN_TestMultiBlock(int id, String name) {
        super(id, name);
    }

    public GTN_TestMultiBlock(String name) {
        super(name);
    }

    @Override
    public int getOffsetHorizontal() {
        return 1;
    }

    @Override
    public int getOffsetVertical() {
        return 1;
    }

    @Override
    public int getOffsetDepth() {
        return 0;
    }

    @Override
    public String getStructurePieceMain() {
        return "TST_TestMultiBlock";
    }

    @Override
    public GTN_Casings getMainCasings() {
        return GTN_Casings.SolidSteelMachineCasing;
    }

    @Override
    public GTN_TestMultiBlock createNewMetaEntity() {
        return new GTN_TestMultiBlock(this.mName);
    }

    @Override
    public OverclockType getOverclockType() {
        return OverclockType.NONE;
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.maceratorRecipes;
    }

    @Override
    public String[][] getShape() {
        return new String[][] {
            { "AAA", "AAA", "AAA" },
            { "A~A", "A A", "AAA" },
            { "AAA", "AAA", "AAA" } };
    }

    @Override
    public void createTstTooltip(MultiblockTooltipBuilder builder) {
        builder
            .addMachineType("Test")
            .addInfo("Test")
            .addInfo("Test")
            .addInfo("Test")
            .addStructureInfoSeparator()
            .addInfo("Test")
            .addInfo("Test")
            .addInfo("Test")
            .addInputHatch("InputHatch", 1)
            .toolTipFinisher(EnumChatFormatting.DARK_RED + "GregTechNightmare");
    }

    @Override
    protected int getMainCasingMax() {
        return 10;
    }

    @Override
    public IStructureDefinition<GTN_TestMultiBlock> getStructureDefinition() {
        return IStructureDefinition.<GTN_TestMultiBlock>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement(
                'A',
                buildHatchAdder(GTN_TestMultiBlock.class)
                    .atLeast(InputHatch, OutputHatch, InputBus, OutputBus, Energy, MultiAmpEnergy, ExoticEnergy, Maintenance, Muffler, Dynamo)
                    .casingIndex(getMainCasings().textureId)
                    .dot(1)
                    .buildAndChain(onElementPass(GTN_TestMultiBlock::mainCasingAdd, ofBlock(getMainCasings().getBlock(), getMainCasings().meta))))
            .build();
    }

    @Override
    public boolean isBatchModeEnabled() {
        return false;
    }
}
