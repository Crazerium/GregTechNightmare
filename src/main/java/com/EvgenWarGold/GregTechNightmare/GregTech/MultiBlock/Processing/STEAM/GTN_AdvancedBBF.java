package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.STEAM;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import net.minecraft.util.EnumChatFormatting;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HatchElement;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.MultiblockTooltipBuilder;

public class GTN_AdvancedBBF extends GTN_MultiBlockBase<GTN_AdvancedBBF> {

    public GTN_AdvancedBBF(int id, String name) {
        super(id, name);
    }

    public GTN_AdvancedBBF(String name) {
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
    public String getStructurePieceMain() {
        return this.mName;
    }

    @Override
    public GTN_Casings getMainCasings() {
        return GTN_Casings.Firebricks;
    }

    @Override
    public GTN_AdvancedBBF createNewMetaEntity() {
        return new GTN_AdvancedBBF(this.mName);
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
    public String[][] getShape() {
        return new String[][] { { "     ", " AAA ", " A A ", " AAA ", "     " },
            { " AAA ", "A   A", "A A A", "A   A", " AAA " }, { " AAA ", "A   A", "A A A", "A   A", " AAA " },
            { "AAAAA", "A   A", "A A A", "A   A", "AAAAA" }, { "BA~AB", "AAAAA", "AAAAA", "AAAAA", "BAAAB" },
            { "B   B", "     ", "     ", "     ", "B   B" } };
    }

    @Override
    public void createGtnTooltip(MultiblockTooltipBuilder builder) {
        builder.addInfo(tr("tooltip.00"))
            .addInfo(tr("tooltip.01"))
            .addInfo(Constants.AUTHOR_EVGEN_WAR_GOLD)
            .beginStructureBlock(5, 6, 5, true)
            .addSteamInputBus(EnumChatFormatting.GOLD + "1", 1)
            .addSteamOutputBus(EnumChatFormatting.GOLD + "1", 1);
    }

    @Override
    public String getMachineType() {
        return tr("machine_type");
    }

    @Override
    public IStructureDefinition<GTN_AdvancedBBF> getStructureDefinition() {
        return IStructureDefinition.<GTN_AdvancedBBF>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement('B', ofFrame(Materials.Steel))
            .addElement(
                'A',
                buildHatchAdder(GTN_AdvancedBBF.class)
                    .atLeast(GTN_HatchElement.SteamInputBus, GTN_HatchElement.SteamOutputBus)
                    .casingIndex(getMainCasings().textureId)
                    .dot(1)
                    .buildAndChain(
                        onElementPass(
                            GTN_AdvancedBBF::mainCasingAdd,
                            ofBlock(getMainCasings().getBlock(), getMainCasings().meta))))
            .build();
    }

    @Override
    protected int getMainCasingMax() {
        return 69;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTN_Recipe.AdvancedBBFRecipes;
    }

    @Override
    public boolean isEnergyMultiBlock() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GTCEU_LOOP_FIRE;
    }
}
