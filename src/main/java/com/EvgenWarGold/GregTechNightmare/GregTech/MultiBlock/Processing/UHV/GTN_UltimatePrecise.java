package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.UHV;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
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

import gregtech.api.GregTechAPI;
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
        return new String[][] {
            { "                             ", "                             ", "                             ",
                "            GGGGG            ", "            GGGGG            ", "            GGGGG            ",
                "            GGGGG            ", "            GGGGG            ", "            GGGGG            ",
                "            GGGGG            ", "                             ", "                             ",
                "                             " },
            { "                             ", "                             ", "             GGG             ",
                "            G B G            ", "           G     G           ", "           G     G           ",
                "           G     G           ", "           G     G           ", "           G     G           ",
                "            G B G            ", "             GGG             ", "                             ",
                "                             " },
            { "                             ", "                             ", "            GFFFG            ",
                "    D    D G     G D    D    ", "  BBBBBBBBBG  B  GBBBBBBBBB  ", "    D    D G  B  G D    D    ",
                "           G BBB G           ", "    D    D G  B  G D    D    ", "  BBBBBBBBBG  B  GBBBBBBBBB  ",
                "    D    D G     G D    D    ", "            GFFFG            ", "                             ",
                "                             " },
            { "                             ", "                             ", "            GFFFG            ",
                "    D    D G     G D    D    ", "  B D    D G  C  G D    D B  ", "    D    D G  C  G D    D    ",
                "    D    D GB C BG D    D    ", "    D    D G  C  G D    D    ", "  B D    D G  C  G D    D B  ",
                "    D    D G     G D    D    ", "            GFFFG            ", "                             ",
                "                             " },
            { "                             ", "                             ", "             GFG             ",
                "    D    D  G   G  D    D    ", "  B         G B G         B  ", "           G  B  G           ",
                "            G B G            ", "           G  B  G           ", "  B         G B G         B  ",
                "    D    D  G   G  D    D    ", "             GFG             ", "                             ",
                "                             " },
            { "                             ", "                             ", "             GFG             ",
                "    D    D  G   G  D    D    ", "  B         G E G         B  ", "           G  E  G           ",
                "            G E G            ", "           G  E  G           ", "  B         G E G         B  ",
                "    D    D  G   G  D    D    ", "             GFG             ", "                             ",
                "                             " },
            { "                             ", " E        E       E        E ", " E        E  GFG  E        E ",
                " E  D    DE G   G ED    D  E ", " EB       E G B G E       BE ", " E        EG  B  GE        E ",
                " E        E G B G E        E ", " E        EG  B  GE        E ", " EB       E G B G E       BE ",
                " E  D    DE G   G ED    D  E ", " E        E  GFG  E        E ", " E        E       E        E ",
                "                             " },
            { " E   BB   E       E   BB   E ", "GGGGGGGGGGGG     GGGGGGGGGGGG", "GGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "GGGGGGGGGGGGG   GGGGGGGGGGGGG", "GGGGGGGGGGGGG C GGGGGGGGGGGGG", "GGGGGGGGGGGGG C GGGGGGGGGGGGG",
                "GGGGGGGGGGGGG C GGGGGGGGGGGGG", "GGGGGGGGGGGGG C GGGGGGGGGGGGG", "GGGGGGGGGGGGG C GGGGGGGGGGGGG",
                "GGGGGGGGGGGGG   GGGGGGGGGGGGG", "GGGGGGGGGGGGGGGGGGGGGGGGGGGGG", "GGGGGGGGGGGG     GGGGGGGGGGGG",
                " E   BB   E       E   BB   E " },
            { " E   BB   E       E   BB   E ", "BBFFFBBFFFBB     BBFFFBBFFFBB", "B          BGGGGGB          B",
                "F          BG B GB          F", "FCCCCCCCCCBBG B GBBCCCCCCCCCF", "F          BG B GB          F",
                "BBBBBBBBBBBBGBBBGBBBBBBBBBBBB", "F          BG B GB          F", "FCCCCCCCCCBBG B GBBCCCCCCCCCF",
                "F          BG B GB          F", "B          BGGGGGB          B", "BBFFFBBFFFBB     BBFFFBBFFFBB",
                " E   BB   E       E   BB   E " },
            { " E   BB   E       E   BB   E ", "BBBBBBBBBBBB     BBBBBBBBBBBB", "BBBBBBBBBBBBGG~GGBBBBBBBBBBBB",
                "BBBBBBBBBBBBGGGGGBBBBBBBBBBBB", "BBBBBBBBBBBBGGGGGBBBBBBBBBBBB", "BBBBBBBBBBBBGGGGGBBBBBBBBBBBB",
                "BBBBBBBBBBBBGGGGGBBBBBBBBBBBB", "BBBBBBBBBBBBGGGGGBBBBBBBBBBBB", "BBBBBBBBBBBBGGGGGBBBBBBBBBBBB",
                "BBBBBBBBBBBBGGGGGBBBBBBBBBBBB", "BBBBBBBBBBBBGGGGGBBBBBBBBBBBB", "BBBBBBBBBBBB     BBBBBBBBBBBB",
                " E   BB   E       E   BB   E " } };
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
    public IStructureDefinition<GTN_UltimatePrecise> getStructureDefinition() {
        return IStructureDefinition.<GTN_UltimatePrecise>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement('B', GTN_Casings.NeutroniumStabilizationCasing.asElement())
            .addElement('F', ofBlockAnyMeta(GregTechAPI.sBlockTintedGlass, 0))
            .addElement('C', ofFrame(Materials.Gadolinium))
            .addElement('D', ofFrame(Materials.Shadow))
            .addElement('E', ofFrame(Materials.Infinity))
            .addElement(
                'G',
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
