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

import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.CasingData;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.TieredElementBuilder;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

public class GTN_TestMultiBlock extends GTN_MultiBlockBase<GTN_TestMultiBlock> {

    public GTN_TestMultiBlock(int id, String name) {
        super(id, name);
    }

    public GTN_TestMultiBlock(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_TestMultiBlock>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "Gas",
                new String[][] { { "AAA", "AAA", "AAA" }, { "A~A", "A A", "AAA" }, { "AAA", "AAA", "AAA" } },
                new MultiblockOffsets(1, 1, 0),
                new MultiblockArea(4, 5, 10),
                1,
                GTN_Casings.FrostProofMachineCasing),
            new StructureVariant<>(
                "Fuel",
                new String[][] { { "BBB", "BBB", "BBB" }, { "B~B", "B B", "BBB" }, { "BBB", "BBB", "BBB" } },
                new MultiblockOffsets(1, 1, 0),
                new MultiblockArea(3, 3, 3),
                2,
                GTN_Casings.TitaniumGearBoxCasing));
    }

    @Override
    public GTN_TestMultiBlock createNewMetaEntity() {
        return new GTN_TestMultiBlock(this.mName);
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

    private final CasingData casing = createCasingData("casing", true);
    private final CasingData casing1 = createCasingData("casing1", true);

    @Override
    public IStructureDefinition<GTN_TestMultiBlock> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addElement(
                'B',
                TieredElementBuilder.create(casing1, GTN_TestMultiBlock.class)
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
                    TieredElementBuilder.create(casing, GTN_TestMultiBlock.class)
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

    @Override
    protected boolean GTN_checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return super.GTN_checkMachine(aBaseMetaTileEntity, aStack);
    }

    @Override
    public OverclockType getOverclockType() {
        return multiBlockTier > 1 ? OverclockType.PerfectOverclock : OverclockType.NormalOverclock;
    }
}
