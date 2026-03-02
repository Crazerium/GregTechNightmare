package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.MV;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import gregtech.api.GregTechAPI;
import gregtech.api.util.MultiblockTooltipBuilder;
import net.minecraft.util.EnumChatFormatting;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

public class GTN_MediumPowerEngraver extends GTN_MultiBlockBase<GTN_MediumPowerEngraver> {

    public GTN_MediumPowerEngraver(int id, String name) {
        super(id, name);
    }

    public GTN_MediumPowerEngraver(String name) {
        super(name);
    }

    @Override
    public int getOffsetHorizontal() {
        return 1;
    }

    @Override
    public int getOffsetVertical() {
        return 1;
    }

    @Override
    public int getOffsetDepth() {
        return 0;
    }

    @Override
    public GTN_Casings getMainCasings() {
        return GTN_Casings.FrostProofMachineCasing;
    }

    @Override
    public GTN_MediumPowerEngraver createNewMetaEntity() {
        return new GTN_MediumPowerEngraver(this.mName);
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
            {" ABBA"," ABBA"," ABBA"},
            {" ~BBA"," ABBA","AABBA"},
            {" AAAA","AAAAA","AAAAA"}
        };
    }

    @Override
    public void createGtnTooltip(MultiblockTooltipBuilder builder) {
        builder.addInfo(tr("tooltip.00"))
            .addInfo(tr("tooltip.01"))
            .addInfo(tr("tooltip.02"))
            .addInfo(Constants.AUTHOR_EVGEN_WAR_GOLD)
            .beginStructureBlock(5, 3, 3, false)
            .addEnergyHatch(EnumChatFormatting.GOLD + "1", 1)
            .addInputBus(EnumChatFormatting.GOLD + "1", 1)
            .addOutputBus(EnumChatFormatting.GOLD + "1", 1);
    }

    @Override
    public IStructureDefinition<GTN_MediumPowerEngraver> getStructureDefinition() {
        return IStructureDefinition.<GTN_MediumPowerEngraver>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement('B', ofBlockAnyMeta(GregTechAPI.sBlockTintedGlass, 3))
            .addElement(
                'A',
                buildHatchAdder(GTN_MediumPowerEngraver.class).atLeast(InputBus, OutputBus, Energy, Maintenance)
                    .casingIndex(getMainCasings().textureId)
                    .dot(1)
                    .buildAndChain(
                        onElementPass(
                            GTN_MediumPowerEngraver::mainCasingAdd,
                            ofBlock(getMainCasings().getBlock(), getMainCasings().meta))))
            .build();
    }
}
