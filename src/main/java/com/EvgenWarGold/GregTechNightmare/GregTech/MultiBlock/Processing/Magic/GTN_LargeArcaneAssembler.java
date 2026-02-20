package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.Magic;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static thaumcraft.common.config.ConfigBlocks.blockMetalDevice;

import net.minecraft.util.EnumChatFormatting;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.MultiblockTooltipBuilder;

public class GTN_LargeArcaneAssembler extends GTN_MultiBlockBase<GTN_LargeArcaneAssembler> {

    public GTN_LargeArcaneAssembler(int id, String name) {
        super(id, name);
    }

    public GTN_LargeArcaneAssembler(String name) {
        super(name);
    }

    public int getOffsetHorizontal() {
        return 1;
    }

    public int getOffsetVertical() {
        return 1;
    }

    public int getOffsetDepth() {
        return 0;
    }

    public GTN_Casings getMainCasings() {
        return GTN_Casings.FrostProofMachineCasing;
    }

    public GTN_LargeArcaneAssembler createNewMetaEntity() {
        return new GTN_LargeArcaneAssembler(this.mName);
    }

    public OverclockType getOverclockType() {
        return OverclockType.NormalOverclock;
    }

    public boolean isNoMaintenanceIssue() {
        return true;
    }

    public String[][] getShape() {
        return new String[][] { { "AAA", "AAA", "AAA" }, { "B~B", "B B", "BBB" }, { "AAA", "AAA", "AAA" } };
    }

    public void createGtnTooltip(MultiblockTooltipBuilder builder) {
        builder.addInfo(this.tr("tooltip.00"))
            .addInfo(this.tr("tooltip.01"))
            .addInfo(this.tr("tooltip.02"))
            .addInfo(Constants.AUTHOR_CRAZER)
            .beginStructureBlock(3, 3, 3, false)
            .addEnergyHatch(EnumChatFormatting.GOLD + "1", new int[] { 1 })
            .addInputBus(EnumChatFormatting.GOLD + "1", new int[] { 1 })
            .addOutputBus(EnumChatFormatting.GOLD + "1", new int[] { 1 });
    }

    @Override
    public IStructureDefinition<GTN_LargeArcaneAssembler> getStructureDefinition() {
        return IStructureDefinition.<GTN_LargeArcaneAssembler>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement('B', ofBlock(blockMetalDevice, 9))
            .addElement(
                'A',
                buildHatchAdder(GTN_LargeArcaneAssembler.class).atLeast(InputBus, OutputBus, Energy, ExoticEnergy)
                    .casingIndex(getMainCasings().textureId)
                    .dot(1)
                    .buildAndChain(
                        onElementPass(
                            GTN_LargeArcaneAssembler::mainCasingAdd,
                            ofBlock(getMainCasings().getBlock(), getMainCasings().meta))))
            .build();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTN_Recipe.ARCANE_ASSEMBLER_RECIPES;
    }

}
