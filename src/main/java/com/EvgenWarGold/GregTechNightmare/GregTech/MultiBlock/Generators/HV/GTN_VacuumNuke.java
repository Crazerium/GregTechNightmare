package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Generators.HV;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_StructureUtility;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.TierData;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.TieredElementBuilder;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import gregtech.api.enums.Materials;

import java.util.Arrays;
import java.util.List;

import static gregtech.api.enums.HatchElement.Dynamo;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.util.GTStructureUtility.ofFrame;

public class GTN_VacuumNuke extends GTN_MultiBlockBase<GTN_VacuumNuke> {

    TierData globalCasing = createTierData("casing", true);
    TierData itemPipe = createTierData("itemPipe");
    TierData coilBlock = createTierData("coil");
    TierData glass = createTierData("glass");

    public GTN_VacuumNuke(int id, String name) {
        super(id, name);
    }

    public GTN_VacuumNuke(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_VacuumNuke>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "VacuumNuke",
                // spotless:off
                new String[][]{
                    {"   E E   ","   CBC   ","   CBC   ","   CBC   ","         ","         ","         ","         ","   CBC   ","   CBC   ","   CBC   ","   E E   "},
                    {"   E E   ","  CCBCC  ","  C   C  ","  C   C  ","  CFFFC  ","  CFFFC  ","  CFFFC  ","  CFFFC  ","  C   C  ","  C   C  ","  CCBCC  ","   E E   "},
                    {"  E B E  "," CCCDCCC "," C  DD C "," CD  DDC "," CDD  DC "," C DD  C "," C  DD C "," CD  DDC "," CDD  DC "," C DD  C "," CCCDCCC ","  E B E  "},
                    {"EE FFF EE","CCC   CCC","C D     C","C D     C"," F    DF "," F    DF "," FD    F "," FD    F ","C     D C","C     D C","CCC   CCC","EE FFF EE"},
                    {"  BF~FB  ","BBD A DBB","B D A D B","B   A   B"," F  A  F "," FD A DF "," FD A DF "," F  A  F ","B   A   B","B D A D B","BBD A DBB","  BFBFB  "},
                    {"EE FFF EE","CCC   CCC","C     D C","C     D C"," FD    F "," FD    F "," F    DF "," F    DF ","C D     C","C D     C","CC    CCC","EE FFF EE"},
                    {"  E B E  "," CCCDCCC "," C DD  C "," CDD  DC "," CD  DDC "," C  DD C "," C DD  C "," CDD  DC "," CD  DDC "," C  DD C "," CCCDCCC ","  E B E  "},
                    {"   E E   ","  CCBCC  ","  C   C  ","  C   C  ","  CFFFC  ","  CFFFC  ","  CFFFC  ","  CFFFC  ","  C   C  ","  C   C  ","  CCBCC  ","   E E   "},
                    {"   E E   ","   CBC   ","   CBC   ","   CBC   ","         ","         ","         ","         ","   CBC   ","   CBC   ","   CBC   ","   E E   "}
                },
                //spotless:on
                new MultiblockOffsets(4, 4, 0),
                new MultiblockArea(9, 9, 12),
                1,
                GTN_Casings.FrostProofMachineCasing));
    }

    @Override
    public GTN_VacuumNuke createNewMetaEntity() {
        return new GTN_VacuumNuke(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addInputBus()
            .addInputHatch()
            .addDynamoOrBufferedHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_VacuumNuke> getStructureDefinition() {
        return buildStructureDefinition(builder -> builder
            .addElement('A', TieredElementBuilder.create(itemPipe, GTN_VacuumNuke.class)
                .casings(
                    GTN_Casings.TinItemPipeCasing,
                    GTN_Casings.BrassItemPipeCasing,
                    GTN_Casings.ElectrumItemPipeCasing,
                    GTN_Casings.PlatinumItemPipeCasing,
                    GTN_Casings.OsmiumItemPipeCasing,
                    GTN_Casings.QuantiumItemPipeCasing,
                    GTN_Casings.FluxedElectrumItemPipeCasing,
                    GTN_Casings.BlackPlutoniumItemPipeCasing)
                .build())
            .addElement('B', GTN_Casings.SolidSteelMachineCasing.asElement())
            .addElement('D', GTN_StructureUtility.createAllTierCoilBlock(coilBlock))
            .addElement('E', ofFrame(Materials.Steel))
            .addElement('F', GTN_StructureUtility.createAllTieredGlass(glass))
            .addElement('C', TieredElementBuilder.create(globalCasing, GTN_VacuumNuke.class)
                .casings(
                    GTN_Casings.FrostProofMachineCasing,
                    GTN_Casings.StableTitaniumMachineCasing,
                    GTN_Casings.RobustTungstenSteelMachineCasing
                )
                .hatches(InputBus, Dynamo, InputHatch)
                .build())
        );
    }

    @Override
    public boolean isEnergyMultiBlock() {
        return false;
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return true;
    }
}
