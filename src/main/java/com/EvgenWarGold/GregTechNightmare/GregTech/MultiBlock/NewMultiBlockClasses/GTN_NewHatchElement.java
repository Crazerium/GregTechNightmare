package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusOutput;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTEHatchCustomFluidBase;

public enum GTN_NewHatchElement implements IHatchElement<GTN_NewMultiBlockBase<?>> {

    SteamInputHatch("SteamInputHatch", GTN_NewMultiBlockBase::addSteamInputHatchToMachineList,
        MTEHatchCustomFluidBase.class) {

        @Override
        public long count(GTN_NewMultiBlockBase<?> gtnMultiBlockBase) {
            return gtnMultiBlockBase.mSteamInputFluids.size();
        }

    },

    SteamInputBus("SteamInputBus", GTN_NewMultiBlockBase::addSteamInputBusToMachineList, MTEHatchSteamBusInput.class) {

        @Override
        public long count(GTN_NewMultiBlockBase<?> gtnMultiBlockBase) {
            return gtnMultiBlockBase.mSteamInputBusses.size();
        }
    },

    SteamOutputBus("SteamOutputBus", GTN_NewMultiBlockBase::addSteamOutputBusToMachineList,
        MTEHatchSteamBusOutput.class) {

        @Override
        public long count(GTN_NewMultiBlockBase<?> gtnMultiBlockBase) {
            return gtnMultiBlockBase.mSteamOutputBusses.size();
        }
    };

    private final String name;
    private final List<Class<? extends IMetaTileEntity>> mteClasses;
    private final IGTHatchAdder<? super GTN_NewMultiBlockBase<?>> adder;

    @SafeVarargs
    GTN_NewHatchElement(String name, IGTHatchAdder<? super GTN_NewMultiBlockBase<?>> adder,
        Class<? extends IMetaTileEntity>... mteClasses) {
        this.name = name;
        this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
        this.adder = adder;
    }

    @Override
    public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
        return mteClasses;
    }

    @Override
    public String getDisplayName() {
        return GTUtility.translate(name);
    }

    @Override
    public IGTHatchAdder<? super GTN_NewMultiBlockBase<?>> adder() {
        return adder;
    }
}
