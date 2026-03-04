package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.LV;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import bartworks.API.BorosilicateGlass;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;

public class GTN_GasCollector extends GTN_MultiBlockBase<GTN_GasCollector> {

    private byte glassTier;

    public GTN_GasCollector(int id, String name) {
        super(id, name);
    }

    public GTN_GasCollector(String name) {
        super(name);
    }

    @Override
    public int getOffsetHorizontal() {
        return 2;
    }

    @Override
    public int getOffsetVertical() {
        return 6;
    }

    @Override
    public int getOffsetDepth() {
        return 0;
    }

    @Override
    public GTN_Casings getMainCasings() {
        return GTN_Casings.HeatProofMachineCasing;
    }

    @Override
    public GTN_GasCollector createNewMetaEntity() {
        return new GTN_GasCollector(this.mName);
    }

    @Override
    public OverclockType getOverclockType() {
        return OverclockType.NormalOverclock;
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setByte("glassTier", glassTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        glassTier = aNBT.getByte("glassTier");
    }

    @Override
    public boolean GTN_checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        for (MTEHatchEnergy hatch : mEnergyHatches) {
            if (hatch.mTier > glassTier) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String[][] getShape() {
        return new String[][] { { "DDDDD", "DDDDD", "DDDDD", "DDDDD", "DDDDD" },
            { "EAAAE", "A   A", "A   A", "A   A", "EAAAE" }, { "EAAAE", "A   A", "A   A", "A   A", "EAAAE" },
            { "EAAAE", "A   A", "A   A", "A   A", "EAAAE" }, { "EAAAE", "A   A", "A   A", "A   A", "EAAAE" },
            { "EAAAE", "A   A", "A   A", "A   A", "EAAAE" }, { "DD~DD", "DDDDD", "DDDDD", "DDDDD", "DDDDD" } };
    }

    public void createGtnTooltip(MultiblockTooltipBuilder builder) {
        builder.addInfo(this.tr("tooltip.00"))
            .addInfo(this.tr("tooltip.01"))
            .addInfo(this.tr("tooltip.02"))
            .addInfo(Constants.AUTHOR_CRAZER)
            .beginStructureBlock(5, 6, 5, false)
            .addEnergyHatch(EnumChatFormatting.GOLD + "1", new int[] { 1 })
            .addInputBus(EnumChatFormatting.GOLD + "1", new int[] { 1 })
            .addOutputBus(EnumChatFormatting.GOLD + "1", new int[] { 1 });
    }

    @Override
    public IStructureDefinition<GTN_GasCollector> getStructureDefinition() {
        return IStructureDefinition.<GTN_GasCollector>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement(
                'A',
                withChannel(
                    "glass",
                    BorosilicateGlass.ofBoroGlass(
                        (byte) 0,
                        (byte) 1,
                        Byte.MAX_VALUE,
                        (te, t) -> te.glassTier = t,
                        te -> te.glassTier)))
            .addElement('E', ofFrame(Materials.Steel))
            .addElement(
                'D',
                buildHatchAdder(GTN_GasCollector.class).atLeast(InputBus, OutputHatch, Energy)
                    .casingIndex(getMainCasings().textureId)
                    .dot(1)
                    .buildAndChain(
                        onElementPass(
                            GTN_GasCollector::mainCasingAdd,
                            ofBlock(getMainCasings().getBlock(), getMainCasings().meta))))
            .build();
    }

    @Override
    public CheckRecipeResult checkProcessing() {
        IGregTechTileEntity tile = getBaseMetaTileEntity();
        if (tile == null || tile.getWorld() == null) return CheckRecipeResultRegistry.INTERNAL_ERROR;
        if (tile.getWorld().provider.dimensionId != 0) return CheckRecipeResultRegistry.NO_RECIPE;
        long inputEut = getMaxInputVoltage();
        int parallel = (int) (inputEut / 32);
        if (inputEut < TierEU.RECIPE_LV) return CheckRecipeResultRegistry.insufficientPower(TierEU.RECIPE_LV);
        mOutputFluids = new FluidStack[] { Materials.Air.getGas(1000 * parallel) };
        mEUt = (int) (-TierEU.RECIPE_LV * parallel);
        mMaxProgresstime = 20;
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }
}
