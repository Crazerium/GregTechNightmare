package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.EV;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.MV.GTN_MediumPowerAssembler;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.MultiblockTooltipBuilder;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.util.EnumChatFormatting;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

public class GTN_ExtremePowerCircuitAssembler extends GTN_MultiBlockBase<GTN_ExtremePowerCircuitAssembler> {

    public GTN_ExtremePowerCircuitAssembler(int id, String name) {
        super(id, name);
    }

    public GTN_ExtremePowerCircuitAssembler(String name) {
        super(name);
    }

    @Override
    public int getOffsetHorizontal() {
        return 5;
    }

    @Override
    public int getOffsetVertical() {
        return 2;
    }

    @Override
    public int getOffsetDepth() {
        return 0;
    }

    @Override
    public GTN_Casings getMainCasings() {
        return GTN_Casings.StableTitaniumMachineCasing;
    }

    @Override
    public GTN_ExtremePowerCircuitAssembler createNewMetaEntity() {
        return new GTN_ExtremePowerCircuitAssembler(this.mName);
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
            {"DDDDDDD ","DBBBCC  ","DAAACC  ","DBBBCC  ","DDDDDDD "},
            {"DEEECCCD","B   CCC ","A   CCC ","B   CCC ","DEEECCCD"},
            {"DEEEC~CD","B   CCC ","A   CCC ","B   CCC ","DEEECCCD"},
            {"DEEECCCD","B   CCC ","A   CCC ","B   CCC ","DEEECCCD"},
            {"DDDDDDDD","DCCCCCCD","DCCCCCCD","DCCCCCCD","DDDDDDDD"}
        };
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
    public IStructureDefinition<GTN_ExtremePowerCircuitAssembler> getStructureDefinition() {
        return IStructureDefinition.<GTN_ExtremePowerCircuitAssembler>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement('D', ofFrame(Materials.Aluminium))
            .addElement('E', ofBlockAnyMeta(GregTechAPI.sBlockTintedGlass, 0))
            .addElement('B', GTN_Casings.TitaniumPipeCasing.asElement())
            .addElement('A', GTN_Casings.TitaniumGearBoxCasing.asElement())
            .addElement(
                'C',
                buildHatchAdder(GTN_ExtremePowerCircuitAssembler.class)
                    .atLeast(InputBus, OutputBus, Energy, Maintenance, InputHatch)
                    .casingIndex(getMainCasings().textureId)
                    .dot(1)
                    .buildAndChain(
                        onElementPass(
                            GTN_ExtremePowerCircuitAssembler::mainCasingAdd,
                            ofBlock(getMainCasings().getBlock(), getMainCasings().meta))))
            .build();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.circuitAssemblerRecipes;
    }

    @Override
    public float getSpeedBonus() {
        return 0.74F;
    }

    @Override
    protected Pair<Integer, Integer> getMinMaxEnergyTier() {
        return Pair.of(VoltageIndex.EV, VoltageIndex.IV);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 20;
    }
}
