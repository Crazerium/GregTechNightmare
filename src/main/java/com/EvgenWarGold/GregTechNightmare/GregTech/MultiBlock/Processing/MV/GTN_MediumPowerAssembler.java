package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.MV;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
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

import gregtech.api.enums.Materials;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.MultiblockTooltipBuilder;
import it.unimi.dsi.fastutil.Pair;

public class GTN_MediumPowerAssembler extends GTN_MultiBlockBase<GTN_MediumPowerAssembler> {

    public GTN_MediumPowerAssembler(int id, String name) {
        super(id, name);
    }

    public GTN_MediumPowerAssembler(String name) {
        super(name);
    }

    @Override
    public int getOffsetHorizontal() {
        return 3;
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
    public GTN_MediumPowerAssembler createNewMetaEntity() {
        return new GTN_MediumPowerAssembler(this.mName);
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
        return new String[][] { { " C   C ", " C   C ", " C   C ", " CCCCC ", " C   C " },
            { " C   C ", "       ", "       ", "  B    ", " C   C " },
            { " C   C ", "       ", "       ", "       ", " C   C " },
            { " C   C ", " AAAAA ", " AAAAA ", " AAAAA ", " C   C " },
            { " AA~AA ", "AAAAAAA", "AAAAAAA", "AAAAAAA", " AAAAA " } };
    }

    @Override
    public void createGtnTooltip(MultiblockTooltipBuilder builder) {
        builder.addInfo(tr("tooltip.00"))
            .addInfo(tr("tooltip.01"))
            .addInfo(tr("tooltip.02"))
            .addInfo(Constants.AUTHOR_EVGEN_WAR_GOLD)
            .beginStructureBlock(7, 5, 5, false)
            .addEnergyHatch(EnumChatFormatting.GOLD + "1", 1)
            .addInputBus(EnumChatFormatting.GOLD + "1", 1)
            .addOutputBus(EnumChatFormatting.GOLD + "1", 1)
            .addInputHatch(EnumChatFormatting.GOLD + "1", 1);
    }

    @Override
    public IStructureDefinition<GTN_MediumPowerAssembler> getStructureDefinition() {
        return IStructureDefinition.<GTN_MediumPowerAssembler>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement('C', ofFrame(Materials.Steel))
            .addElement('B', GTN_Casings.SteelGearBoxCasing.asElement())
            .addElement(
                'A',
                buildHatchAdder(GTN_MediumPowerAssembler.class)
                    .atLeast(InputBus, OutputBus, Energy, Maintenance, InputHatch)
                    .casingIndex(getMainCasings().textureId)
                    .dot(1)
                    .buildAndChain(
                        onElementPass(
                            GTN_MediumPowerAssembler::mainCasingAdd,
                            ofBlock(getMainCasings().getBlock(), getMainCasings().meta))))
            .build();
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
    protected Pair<Integer, Integer> getMaxEnergyTier() {
        return Pair.of(0, VoltageIndex.HV);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 10;
    }
}
