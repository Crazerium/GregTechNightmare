package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.MV;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HatchElement;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.STEAM.GTN_AdvancedBBF;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.MultiblockTooltipBuilder;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.util.EnumChatFormatting;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

public class GTN_MediumPowerBender extends GTN_MultiBlockBase<GTN_MediumPowerBender> {

    public GTN_MediumPowerBender(int id, String name) {
        super(id, name);
    }

    public GTN_MediumPowerBender(String name) {
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
    public GTN_Casings getMainCasings() {
        return GTN_Casings.FrostProofMachineCasing;
    }

    @Override
    public GTN_MediumPowerBender createNewMetaEntity() {
        return new GTN_MediumPowerBender(this.mName);
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
        return new String[][]{
            {"BB   B","AAAAAA","BB   B"},
            {"A~   A","AA   A","AA   A"},
            {"AABBBA","AAAAAA","AABBBA"}
        };
    }

    @Override
    public void createGtnTooltip(MultiblockTooltipBuilder builder) {
        builder.addInfo(tr("tooltip.00"))
            .addInfo(tr("tooltip.01"))
            .addInfo(tr("tooltip.02"))
            .addInfo(Constants.AUTHOR_EVGEN_WAR_GOLD)
            .beginStructureBlock(6, 3, 3, false)
            .addEnergyHatch(EnumChatFormatting.GOLD + "1", 1)
            .addInputBus(EnumChatFormatting.GOLD + "1", 1)
            .addOutputBus(EnumChatFormatting.GOLD + "1", 1);
    }

    @Override
    public IStructureDefinition<GTN_MediumPowerBender> getStructureDefinition() {
        return IStructureDefinition.<GTN_MediumPowerBender>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement('B', ofFrame(Materials.Steel))
            .addElement(
                'A',
                buildHatchAdder(GTN_MediumPowerBender.class)
                    .atLeast(InputBus, OutputBus, Energy, Maintenance)
                    .casingIndex(getMainCasings().textureId)
                    .dot(1)
                    .buildAndChain(
                        onElementPass(
                            GTN_MediumPowerBender::mainCasingAdd,
                            ofBlock(getMainCasings().getBlock(), getMainCasings().meta))))
            .build();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.benderRecipes;
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
