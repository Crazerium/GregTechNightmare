package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_AspectHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_ManaHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_MeAspectHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_SensorHatch;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusOutput;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTEHatchCustomFluidBase;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;

public class GTN_HatchControl<T extends GTN_MultiBlockBase<T>> {

    // region Variables
    private final T multiblock;
    // endregion

    // region Constructors
    public GTN_HatchControl(T multiblock) {
        this.multiblock = multiblock;
    }
    // endregion

    // region Add hatches
    public final boolean addSteamInputBusToMachineList(IGregTechTileEntity tileEntity) {
        if (baseCheckHatch(tileEntity)) return false;

        if (!(tileEntity.getMetaTileEntity() instanceof MTEHatchSteamBusInput steamBusInput)) return false;

        multiblock.mInputBusses.add(steamBusInput);

        return multiblock.steamInputBusses.add(steamBusInput);
    }

    public final boolean addSteamInputHatchToMachineList(IGregTechTileEntity tileEntity) {
        if (baseCheckHatch(tileEntity)) return false;

        if (!(tileEntity.getMetaTileEntity() instanceof MTEHatchCustomFluidBase steamHatchInput)) return false;

        return multiblock.steamOutputFluids.add(steamHatchInput);
    }

    public final boolean addSteamOutputBusToMachineList(IGregTechTileEntity tileEntity) {
        if (baseCheckHatch(tileEntity)) return false;

        if (!(tileEntity.getMetaTileEntity() instanceof MTEHatchSteamBusOutput steamBusOutput)) return false;

        multiblock.mOutputBusses.add(steamBusOutput);

        return multiblock.steamOutputBusses.add(steamBusOutput);
    }

    public final boolean addSensorHatchToMachineList(IGregTechTileEntity tileEntity) {
        if (baseCheckHatch(tileEntity)) return false;

        if (!(tileEntity.getMetaTileEntity() instanceof GTN_SensorHatch sensorHatch)) return false;

        return multiblock.sensorHatches.add(sensorHatch);
    }

    public final boolean addManaHatchToMachineList(IGregTechTileEntity tileEntity) {
        if (baseCheckHatch(tileEntity)) return false;

        if (!(tileEntity.getMetaTileEntity() instanceof GTN_ManaHatch manaHatch)) return false;

        return multiblock.manaHatches.add(manaHatch);
    }

    public final boolean addAspectHatchToMachineList(IGregTechTileEntity tileEntity) {
        if (baseCheckHatch(tileEntity)) return false;

        if (!(tileEntity.getMetaTileEntity() instanceof GTN_AspectHatch aspectHatch)) return false;

        return multiblock.aspectHatches.add(aspectHatch);
    }

    public final boolean addMeAspectHatchToMachineList(IGregTechTileEntity tileEntity) {
        if (baseCheckHatch(tileEntity)) return false;

        if (!(tileEntity.getMetaTileEntity() instanceof GTN_MeAspectHatch aspectHatch)) return false;

        return multiblock.meAspectHatches.add(aspectHatch);
    }

    public final boolean addDynamoMultiHatchToMachineList(IGregTechTileEntity tileEntity) {
        if (baseCheckHatch(tileEntity)) return false;

        if (!(tileEntity.getMetaTileEntity() instanceof MTEHatchDynamoMulti dynamoMulti)) return false;

        return multiblock.dynamoMultiHatches.add(dynamoMulti);
    }
    // endregion

    // region Update Textures
    public void updateHatches(int textureId) {
        for (IDualInputHatch h : multiblock.mDualInputHatches) h.updateTexture(textureId);
        for (MTEHatch h : multiblock.mInputBusses) h.updateTexture(textureId);
        for (MTEHatch h : multiblock.mMaintenanceHatches) h.updateTexture(textureId);
        for (MTEHatch h : multiblock.mEnergyHatches) h.updateTexture(textureId);
        for (MTEHatch h : multiblock.mOutputBusses) h.updateTexture(textureId);
        for (MTEHatch h : multiblock.mInputHatches) h.updateTexture(textureId);
        for (MTEHatch h : multiblock.mOutputHatches) h.updateTexture(textureId);
        for (MTEHatch h : multiblock.mMufflerHatches) h.updateTexture(textureId);
        for (MTEHatch h : multiblock.getExoticEnergyHatches()) h.updateTexture(textureId);
        for (MTEHatch h : multiblock.getExoticDynamoHatches()) h.updateTexture(textureId);
        for (MTEHatch h : multiblock.sensorHatches) h.updateTexture(textureId);
        for (MTEHatch h : multiblock.manaHatches) h.updateTexture(textureId);
        for (MTEHatch h : multiblock.aspectHatches) h.updateTexture(textureId);
        for (MTEHatch h : multiblock.meAspectHatches) h.updateTexture(textureId);
        for (MTEHatch h : multiblock.mDynamoHatches) h.updateTexture(textureId);
        for (MTEHatch h : multiblock.dynamoMultiHatches) h.updateTexture(textureId);
    }
    // endregion

    // region Clear Hatches
    public void clearHatches() {
        multiblock.steamOutputFluids.clear();
        multiblock.steamInputBusses.clear();
        multiblock.steamOutputBusses.clear();
        multiblock.sensorHatches.clear();
        multiblock.manaHatches.clear();
        multiblock.aspectHatches.clear();
        multiblock.meAspectHatches.clear();
        multiblock.dynamoMultiHatches.clear();
    }
    // endregion

    // region Help methods
    private boolean baseCheckHatch(IGregTechTileEntity tileEntity) {
        if (tileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = tileEntity.getMetaTileEntity();

        return aMetaTileEntity == null;
    }
    // endregion
}
