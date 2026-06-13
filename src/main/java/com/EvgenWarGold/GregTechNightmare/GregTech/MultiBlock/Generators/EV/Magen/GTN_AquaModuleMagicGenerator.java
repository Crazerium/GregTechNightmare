package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Generators.EV.Magen;

import static gregtech.api.enums.HatchElement.InputHatch;

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

import gregtech.api.GregTechAPI;

public class GTN_AquaModuleMagicGenerator extends GTN_MultiBlockBase<GTN_AquaModuleMagicGenerator>
    implements IMagicGeneratorModule {

    private int generate = 100;
    private int boostLevel = 0;

    public GTN_AquaModuleMagicGenerator(int id, String name) {
        super(id, name);
    }

    public GTN_AquaModuleMagicGenerator(String name) {
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
    public List<StructureVariant<GTN_AquaModuleMagicGenerator>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "",
                // spotless:off
                new String[][]{
                    {"A"},
                    {"B"},
                    {"B"},
                    {"~"}
                },
                //spotless:on
                new MultiblockOffsets(0, 3, 0),
                new MultiblockArea(1, 4, 1),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_AquaModuleMagicGenerator createNewMetaEntity() {
        return new GTN_AquaModuleMagicGenerator(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {

    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_AquaModuleMagicGenerator> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addMainCasing('B', b -> b.hatches(InputHatch))
                .addBlock('A', GregTechAPI.sBlockGem1, 3));
    }
}
