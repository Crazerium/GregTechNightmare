package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Example;

import static gregtech.api.enums.HatchElement.Dynamo;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.Muffler;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;

import java.util.Arrays;
import java.util.List;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.GTN_NewMultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.TierData;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.TieredElementBuilder;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

public class GTN_NewTestMultiBlock extends GTN_NewMultiBlockBase<GTN_NewTestMultiBlock> {

    public GTN_NewTestMultiBlock(int id, String name) {
        super(id, name);
    }

    public GTN_NewTestMultiBlock(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_NewTestMultiBlock>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "Gas",
                new String[][] { { "AAA", "AAA", "AAA" }, { "A~A", "A A", "AAA" }, { "AAA", "AAA", "AAA" } },
                new MultiblockOffsets(1, 1, 0),
                1,
                GTN_Casings.FrostProofMachineCasing),
            new StructureVariant<>(
                "Fuel",
                new String[][] { { "BBB", "BBB", "BBB" }, { "B~B", "B B", "BBB" }, { "BBB", "BBB", "BBB" } },
                new MultiblockOffsets(1, 1, 0),
                2,
                GTN_Casings.TitaniumGearBoxCasing));
    }

    @Override
    public GTN_NewTestMultiBlock createNewMetaEntity() {
        return new GTN_NewTestMultiBlock(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addExtraInfo("CRAZER")
            .addExtraInfoWithSpace("FAOTIK");
    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public MultiblockArea getMultiblockArea() {
        return new MultiblockArea(4, 5, 10);
    }

    private final TierData casing = createTierData("casing");
    private final TierData casing1 = createTierData("casing1");

    @Override
    public IStructureDefinition<GTN_NewTestMultiBlock> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addElement(
                'B',
                TieredElementBuilder.create(casing1, GTN_NewTestMultiBlock.class)
                    .casings(GTN_Casings.TitaniumGearBoxCasing, GTN_Casings.SolidifierCasing)
                    .hatches(
                        InputHatch,
                        OutputHatch,
                        InputBus,
                        OutputBus,
                        Energy,
                        ExoticEnergy,
                        Maintenance,
                        Muffler,
                        Dynamo)
                    .build())
                .addElement(
                    'A',
                    TieredElementBuilder.create(casing, GTN_NewTestMultiBlock.class)
                        .casings(GTN_Casings.FrostProofMachineCasing, GTN_Casings.Firebricks)
                        .hatches(
                            InputHatch,
                            OutputHatch,
                            InputBus,
                            OutputBus,
                            Energy,
                            ExoticEnergy,
                            Maintenance,
                            Muffler,
                            Dynamo)
                        .build())
                .build());
    }
}
