package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.UHV;

import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;

import java.util.Arrays;
import java.util.List;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import it.unimi.dsi.fastutil.Pair;

public class GTN_UltimatePrecise extends GTN_MultiBlockBase<GTN_UltimatePrecise> {

    public GTN_UltimatePrecise(int id, String name) {
        super(id, name);
    }

    public GTN_UltimatePrecise(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_UltimatePrecise>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "UltimatePrecise",
                // spotless:off
                new String[][]{
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
                },
                //spotless:on
                new MultiblockOffsets(14, 9, 2),
                new MultiblockArea(29, 10, 13),
                1,
                GTN_Casings.PreciseCasingMk4));
    }

    @Override
    public GTN_UltimatePrecise createNewMetaEntity() {
        return new GTN_UltimatePrecise(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addInputBus()
            .addOutputBus()
            .addInputHatch()
            .addExoticOrEnergyHatch()
            .addMaintenanceHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_UltimatePrecise> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addCasing('A', GTN_Casings.NeutroniumStabilizationCasing)
                .addAllGlasses('E')
                .addFrame('B', Materials.Gadolinium)
                .addFrame('C', Materials.Shadow)
                .addFrame('D', Materials.Infinity)
                .addMainCasing(
                    'F',
                    b -> b.hatches(InputBus, OutputBus, Energy.or(ExoticEnergy), Maintenance, InputHatch)));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.assemblerRecipes;
    }

    @Override
    public float getSpeedBonus() {
        return 2F;
    }

    @Override
    protected Pair<Integer, Integer> getMinMaxEnergyTier() {
        return Pair.of(VoltageIndex.ULV, VoltageIndex.UIV);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 2048;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_MULTI_PRECISE_LOOP;
    }
}
