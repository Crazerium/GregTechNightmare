package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusOutput;

public enum GTN_HatchElement implements IHatchElement<GTN_MultiBlockBase<?>> {

    SteamInputBus("SteamInputBus", GTN_MultiBlockBase::addSteamInputBusToMachineList, MTEHatchSteamBusInput.class) {

        @Override
        public long count(GTN_MultiBlockBase gtnMultiBlockBase) {
            return gtnMultiBlockBase.mSteamInputBusses.size();
        }
    },

    SteamOutputBus("SteamOutputBus", GTN_MultiBlockBase::addSteamOutputBusToMachineList, MTEHatchSteamBusOutput.class) {

        @Override
        public long count(GTN_MultiBlockBase gtnMultiBlockBase) {
            return gtnMultiBlockBase.mSteamOutputBusses.size();
        }
    };

    private final String name;
    private final List<Class<? extends IMetaTileEntity>> mteClasses;
    private final IGTHatchAdder<GTN_MultiBlockBase<?>> adder;

    @SafeVarargs
    GTN_HatchElement(String name, IGTHatchAdder<GTN_MultiBlockBase<?>> adder,
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

    public IGTHatchAdder<? super GTN_MultiBlockBase<?>> adder() {
        return adder;
    }
}
