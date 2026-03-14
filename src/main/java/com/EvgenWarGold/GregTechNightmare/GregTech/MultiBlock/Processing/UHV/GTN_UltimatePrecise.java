package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.UHV;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
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

public class GTN_UltimatePrecise extends GTN_MultiBlockBase<GTN_UltimatePrecise> {

    public GTN_UltimatePrecise(int id, String name) {
        super(id, name);
    }

    public GTN_UltimatePrecise(String name) {
        super(name);
    }

    @Override
    public int getOffsetHorizontal() {
        return 14;
    }

    @Override
    public int getOffsetVertical() {
        return 9;
    }

    @Override
    public int getOffsetDepth() {
        return 2;
    }

    @Override
    public GTN_Casings getMainCasings() {
        return GTN_Casings.PreciseCasingMk4;
    }

    @Override
    public GTN_UltimatePrecise createNewMetaEntity() {
        return new GTN_UltimatePrecise(this.mName);
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
        // spotless:off
        return new String[][]{
            {"                             ","                             ","                             ","            FFFFF            ","            FFFFF            ","            FFFFF            ","            FFFFF            ","            FFFFF            ","            FFFFF            ","            FFFFF            ","                             ","                             ","                             "},
            {"                             ","                             ","             FFF             ","            F A F            ","           F     F           ","           F     F           ","           F     F           ","           F     F           ","           F     F           ","            F A F            ","             FFF             ","                             ","                             "},
            {"                             ","                             ","            FEEEF            ","    C    C F     F C    C    ","  AAAAAAAAAF  A  FAAAAAAAAA  ","    C    C F  A  F C    C    ","           F AAA F           ","    C    C F  A  F C    C    ","  AAAAAAAAAF  A  FAAAAAAAAA  ","    C    C F     F C    C    ","            FEEEF            ","                             ","                             "},
            {"                             ","                             ","            FEEEF            ","    C    C F     F C    C    ","  A C    C F  B  F C    C A  ","    C    C F  B  F C    C    ","    C    C FA B AF C    C    ","    C    C F  B  F C    C    ","  A C    C F  B  F C    C A  ","    C    C F     F C    C    ","            FEEEF            ","                             ","                             "},
            {"                             ","                             ","             FEF             ","    C    C  F   F  C    C    ","  A         F A F         A  ","           F  A  F           ","            F A F            ","           F  A  F           ","  A         F A F         A  ","    C    C  F   F  C    C    ","             FEF             ","                             ","                             "},
            {"                             ","                             ","             FEF             ","    C    C  F   F  C    C    ","  A         F D F         A  ","           F  D  F           ","            F D F            ","           F  D  F           ","  A         F D F         A  ","    C    C  F   F  C    C    ","             FEF             ","                             ","                             "},
            {"                             "," D        D       D        D "," D        D  FEF  D        D "," D  C    CD F   F DC    C  D "," DA       D F A F D       AD "," D        DF  A  FD        D "," D        D F A F D        D "," D        DF  A  FD        D "," DA       D F A F D       AD "," D  C    CD F   F DC    C  D "," D        D  FEF  D        D "," D        D       D        D ","                             "},
            {" D   AA   D       D   AA   D ","FFFFFFFFFFFF     FFFFFFFFFFFF","FFFFFFFFFFFFFFFFFFFFFFFFFFFFF","FFFFFFFFFFFFF   FFFFFFFFFFFFF","FFFFFFFFFFFFF B FFFFFFFFFFFFF","FFFFFFFFFFFFF B FFFFFFFFFFFFF","FFFFFFFFFFFFF B FFFFFFFFFFFFF","FFFFFFFFFFFFF B FFFFFFFFFFFFF","FFFFFFFFFFFFF B FFFFFFFFFFFFF","FFFFFFFFFFFFF   FFFFFFFFFFFFF","FFFFFFFFFFFFFFFFFFFFFFFFFFFFF","FFFFFFFFFFFF     FFFFFFFFFFFF"," D   AA   D       D   AA   D "},
            {" D   AA   D       D   AA   D ","AAEEEAAEEEAA     AAEEEAAEEEAA","A          AFFFFFA          A","E          AF A FA          E","EBBBBBBBBBAAF A FAABBBBBBBBBE","E          AF A FA          E","AAAAAAAAAAAAFAAAFAAAAAAAAAAAA","E          AF A FA          E","EBBBBBBBBBAAF A FAABBBBBBBBBE","E          AF A FA          E","A          AFFFFFA          A","AAEEEAAEEEAA     AAEEEAAEEEAA"," D   AA   D       D   AA   D "},
            {" D   AA   D       D   AA   D ","AAAAAAAAAAAA     AAAAAAAAAAAA","AAAAAAAAAAAAFF~FFAAAAAAAAAAAA","AAAAAAAAAAAAFFFFFAAAAAAAAAAAA","AAAAAAAAAAAAFFFFFAAAAAAAAAAAA","AAAAAAAAAAAAFFFFFAAAAAAAAAAAA","AAAAAAAAAAAAFFFFFAAAAAAAAAAAA","AAAAAAAAAAAAFFFFFAAAAAAAAAAAA","AAAAAAAAAAAAFFFFFAAAAAAAAAAAA","AAAAAAAAAAAAFFFFFAAAAAAAAAAAA","AAAAAAAAAAAAFFFFFAAAAAAAAAAAA","AAAAAAAAAAAA     AAAAAAAAAAAA"," D   AA   D       D   AA   D "}
        };
        //spotless:on
    }

    @Override
    public void createGtnTooltip(MultiblockTooltipBuilder builder) {
        builder.addInfo(tr("tooltip.00"))
            .addInfo(tr("tooltip.01"))
            .addInfo(tr("tooltip.02"))
            .addInfo(Constants.AUTHOR_EVGEN_WAR_GOLD)
            .beginStructureBlock(29, 10, 13, false)
            .addEnergyHatch(EnumChatFormatting.GOLD + "1", 1)
            .addInputBus(EnumChatFormatting.GOLD + "1", 1)
            .addOutputBus(EnumChatFormatting.GOLD + "1", 1)
            .addInputHatch(EnumChatFormatting.GOLD + "1", 1);
    }

    @Override
    public IStructureDefinition<GTN_UltimatePrecise> getStructureDefinition() {
        return IStructureDefinition.<GTN_UltimatePrecise>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement('A', GTN_Casings.NeutroniumStabilizationCasing.asElement())
            .addElement('E', GTN_Casings.TintedGlassWhite.asElement())
            .addElement('B', ofFrame(Materials.Gadolinium))
            .addElement('C', ofFrame(Materials.Shadow))
            .addElement('D', ofFrame(Materials.Infinity))
            .addElement(
                'F',
                buildHatchAdder(GTN_UltimatePrecise.class)
                    .atLeast(InputBus, OutputBus, Energy.or(ExoticEnergy), Maintenance, InputHatch)
                    .casingIndex(getMainCasings().textureId)
                    .dot(1)
                    .buildAndChain(
                        onElementPass(
                            GTN_UltimatePrecise::mainCasingAdd,
                            ofBlock(getMainCasings().getBlock(), getMainCasings().meta))))
            .build();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.assemblerRecipes;
    }

    @Override
    public float getSpeedBonus() {
        return 0.70F;
    }

    @Override
    protected Pair<Integer, Integer> getMinMaxEnergyTier() {
        return Pair.of(VoltageIndex.ULV, VoltageIndex.UIV);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 4096;
    }
}
