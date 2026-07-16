package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Generators.EV.Magen;

import static gregtech.api.enums.HatchElement.InputHatch;

import java.util.Arrays;
import java.util.List;

import com.EvgenWarGold.GregTechNightmare.ModBlocks.ModBlocks;
import gregtech.api.enums.Materials;
import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.GregTechAPI;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;

public class GTN_FireModuleMagicGenerator extends GTN_MultiBlockBase<GTN_FireModuleMagicGenerator>
    implements IMagicGeneratorModule {

    private int generate = 100;
    private int boostLevel = 0;

    public GTN_FireModuleMagicGenerator(int id, String name) {
        super(id, name);
    }

    public GTN_FireModuleMagicGenerator(String name) {
        super(name);
    }

    @Override
    public int generate() {
        return generate;
    }

    @Override
    public int boostLevel() {
        return boostLevel;
    }

    @Override
    public List<StructureVariant<GTN_FireModuleMagicGenerator>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "FireModuleMagicGenerator",
                // spotless:off
                new String[][]{
                    {"     CCC     ","   CCCCCCC   ","  CCCCCCCCC  "," CCCCCCCCCCC "," CCCCCCCCCCC ","CCCCCCCCCCCCC","CCCCCCCCCCCCC","CCCCCCCCCCCCC"," CCCCCCCCCCC "," CCCCCCCCCCC ","  CCCCCCCCC  ","   CCCCCCC   ","     CCC     "},
                    {"     B       ","   B         ","             "," B           ","             ","             ","B     B      ","             ","             "," B         B ","             ","   B     B   ","      B      "},
                    {"     B       ","   B         ","             "," B           ","             ","      B      ","B     F      ","      B      ","             "," B         D ","             ","   B     B   ","      B      "},
                    {"     B       ","   B         ","             "," B           ","             ","             ","B    BFB     ","             ","             "," B           ","             ","   B     D   ","      B      "},
                    {"     B       ","   B         ","             "," B           ","             ","             ","B     B      ","             ","             "," B         D ","             ","   B         ","      D      "},
                    {"     B       ","   B         ","             "," B           ","             ","      B      ","B    BEB     ","      B      ","             "," B         B ","             ","   D     D   ","             "},
                    {"     B       ","   B         ","             "," B           ","             ","      B      ","B    BE      ","      B      ","             "," D         B ","             ","         B   ","      D      "},
                    {"     B       ","   B         ","             "," B           ","             ","     FEF     ","D    EEE     ","     FEF     ","             ","           B ","             ","   D     B   ","      B      "},
                    {"     B       ","   B         ","             "," D           ","     FEF     ","    FAAAF    ","    EAAAE    ","    FAAAF    ","     FEF     "," D         B ","             ","   B     B   ","      B      "},
                    {"     B       ","   D         ","             ","             ","     FEF     ","    FAAAF    ","D   EAAAE    ","    FAAAF    ","     FEF     "," B         B ","             ","   B     B   ","      B      "},
                    {"     D       ","             ","             "," D           ","     FEF     ","    FAAAF    ","B   EAAAE    ","    FAAAF    ","     FEF     "," B         B ","             ","   B     B   ","      B      "},
                    {"             ","   D         ","             "," B           ","             ","     FEF     ","B    EEE     ","     FEF     ","             "," B         B ","             ","   B     B   ","      B      "},
                    {"     D       ","   B         ","             "," B           ","             ","             ","B            ","             ","             "," B         B ","             ","   B     B   ","      B      "},
                    {"     B       ","   B         ","             "," B           ","             ","             ","B            ","             ","             "," B         B ","             ","   B     B   ","      B      "},
                    {"     C~C     ","   CCCCCCC   ","  CCCCCCCCC  "," CCCCCCCCCCC "," CCCCCCCCCCC ","CCCCCCCCCCCCC","CCCCCCCCCCCCC","CCCCCCCCCCCCC"," CCCCCCCCCCC "," CCCCCCCCCCC ","  CCCCCCCCC  ","   CCCCCCC   ","     CCC     "}
                },
                //spotless:on
                new MultiblockOffsets(6, 14, 0),
                new MultiblockArea(13, 15, 13),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_FireModuleMagicGenerator createNewMetaEntity() {
        return new GTN_FireModuleMagicGenerator(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {

    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_FireModuleMagicGenerator> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addMainCasing('C', b -> b.hatches(InputHatch))
                .addCasing('A', GTN_Casings.HSSGCoilBlock)
                .addFrame('B', Materials.TungstenSteel)
                .addBlock('D', ModBlocks.THAUMCRAFT_BLOCKS.AmberBlock.getBlock(), 0)
                .addBlock(
                    'E',
                    ModBlocks.THAUMIC_BASES_BLOCKS.FireCrystalBlock.getBlock(),
                    ModBlocks.THAUMIC_BASES_BLOCKS.FireCrystalBlock.meta)
                .addBlock('F', ModBlocks.BOTANIA_BLOCKS.AlfGlass.getBlock(), 0));
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        setDurationInSeconds(5);
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return true;
    }

    @Override
    public boolean isEnergyMultiBlock() {
        return false;
    }
}
