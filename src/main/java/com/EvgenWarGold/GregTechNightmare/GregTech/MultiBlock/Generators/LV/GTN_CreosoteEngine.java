package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Generators.LV;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.removeFluids;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.Dynamo;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

public class GTN_CreosoteEngine extends GTN_MultiBlockBase<GTN_CreosoteEngine> {
    private static int DYNAMO_TIER;
    private static long DYNAMO_AMP;
    private static final int CREOSOTE_USAGE_PER_SEC = 25;
    private final static FluidStack CREOSOTE;

    static {
        CREOSOTE = Materials.Creosote.getFluid(CREOSOTE_USAGE_PER_SEC);
    }

    public GTN_CreosoteEngine(int id, String name) {
        super(id, name);
    }

    public GTN_CreosoteEngine(String name) {
        super(name);
    }

    @Override
    public int getOffsetHorizontal() {
        return 5;
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
        return GTN_Casings.SolidSteelMachineCasing;
    }

    @Override
    public GTN_CreosoteEngine createNewMetaEntity() {
        return new GTN_CreosoteEngine(this.mName);
    }

    @Override
    public OverclockType getOverclockType() {
        return OverclockType.NONE;
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return false;
    }

    @Override
    public String[][] getShape() {
        return new String[][]{
            {"      B ","     E B","     E B","      B "},
            {"BFFFB~B ","     E B","     E B","BFFFBBB "},
            {"BCCCBD  ","AAAAABBB","AAAAABBB","BCCCBD  "}
        };
    }

    @Override
    public void createGtnTooltip(MultiblockTooltipBuilder builder) {
        builder.addInfo(tr("tooltip.00"))
            .addInfo(tr("tooltip.01"))
            .addInfo(tr("tooltip.02"))
            .addInfo(tr("tooltip.03"))
            .addInfo(tr("tooltip.04"))
            .addInfo(Constants.AUTHOR_EVGEN_WAR_GOLD)
            .beginStructureBlock(8, 3, 4, false)
            .addDynamoHatch(EnumChatFormatting.GOLD + "1", 1)
            .addMaintenanceHatch(EnumChatFormatting.GOLD + "1", 1)
            .addInputHatch(EnumChatFormatting.GOLD + "1", 1);
    }

    @Override
    public IStructureDefinition<GTN_CreosoteEngine> getStructureDefinition() {
        return IStructureDefinition.<GTN_CreosoteEngine>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement('D', ofFrame(Materials.Iron))
            .addElement('E', ofFrame(Materials.Steel))
            .addElement('F', ofBlock(Blocks.glass, 0))
            .addElement('C', GTN_Casings.SteelGearBoxCasing.asElement())
            .addElement('A', GTN_Casings.ULVMachineCasing.asElement())
            .addElement(
                'B',
                buildHatchAdder(GTN_CreosoteEngine.class).atLeast(InputHatch, Dynamo, Maintenance)
                    .casingIndex(getMainCasings().textureId)
                    .dot(1)
                    .buildAndChain(
                        onElementPass(
                            GTN_CreosoteEngine::mainCasingAdd,
                            ofBlock(getMainCasings().getBlock(), getMainCasings().meta))))
            .build();
    }

    @Override
    public boolean isEnergyMultiBlock() {
        return false;
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        List<FluidStack> fluids = getStoredFluids();
        List<FluidStack> fluidUsage = Collections.singletonList(CREOSOTE);

        if (fluids.isEmpty()) return CheckRecipeResultRegistry.NO_RECIPE;

        for (FluidStack fluid : fluids) {
            if (fluid.isFluidEqual(CREOSOTE)) {
                switch (DYNAMO_TIER) {
                    case 1 -> {
                        CREOSOTE.amount = Math.toIntExact(CREOSOTE_USAGE_PER_SEC * DYNAMO_AMP);
                        setEnergyGenerate(32 * DYNAMO_AMP);
                    }
                    case 2 -> {
                        CREOSOTE.amount = Math.toIntExact(CREOSOTE_USAGE_PER_SEC * DYNAMO_AMP) * 4;
                        setEnergyGenerate(128 * DYNAMO_AMP);
                    }
                }

                if (getAllMaxDynamoBuffer() != getAllDynamoBuffer()) {
                    if (removeFluids(fluids, fluidUsage, true)) {
                        removeFluids(fluids, fluidUsage);
                        mEfficiency = getEfficiency();
                        setDurationInSeconds(1);
                        return CheckRecipeResultRegistry.GENERATING;
                    }
                }
            }
        }

        super.lEUt = 0;
        super.mEfficiency = 0;
        super.mMaxProgresstime = 0;
        return CheckRecipeResultRegistry.NO_FUEL_FOUND;
    }

    @Override
    protected boolean GTN_checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        DYNAMO_AMP = getDynamoAmperage();
        DYNAMO_TIER = getTierDynamo();
        return checkCountDynamo(1) && setDynamoTier(2, false);
    }
}
