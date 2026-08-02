package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_AspectHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_ManaHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_MeAspectHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_SensorHatch;

import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusOutput;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTEHatchCustomFluidBase;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;

@SuppressWarnings("unchecked")
public enum GTN_HatchElement implements IHatchElement<GTN_MultiBlockBase<?>> {

    SteamInputHatch("SteamInputHatch", (base, tile, casing) -> base.hatchControl.addSteamInputHatchToMachineList(tile),
        MTEHatchCustomFluidBase.class) {

        @Override
        public long count(GTN_MultiBlockBase<?> gtnMultiBlockBase) {
            return gtnMultiBlockBase.steamOutputFluids.size();
        }
    },

    SteamInputBus("SteamInputBus", (base, tile, casing) -> base.hatchControl.addSteamInputBusToMachineList(tile),
        MTEHatchSteamBusInput.class) {

        @Override
        public long count(GTN_MultiBlockBase<?> gtnMultiBlockBase) {
            return gtnMultiBlockBase.steamInputBusses.size();
        }
    },

    SteamOutputBus("SteamOutputBus", (base, tile, casing) -> base.hatchControl.addSteamOutputBusToMachineList(tile),
        MTEHatchSteamBusOutput.class) {

        @Override
        public long count(GTN_MultiBlockBase<?> gtnMultiBlockBase) {
            return gtnMultiBlockBase.steamOutputBusses.size();
        }
    },

    SensorHatch("SensorHatch", (base, tile, casing) -> base.hatchControl.addSensorHatchToMachineList(tile),
        GTN_SensorHatch.class) {

        @Override
        public long count(GTN_MultiBlockBase<?> gtnMultiBlockBase) {
            return gtnMultiBlockBase.sensorHatches.size();
        }
    },

    ManaHatch("ManaHatch", (base, tile, casing) -> base.hatchControl.addManaHatchToMachineList(tile),
        GTN_ManaHatch.class) {

        @Override
        public long count(GTN_MultiBlockBase<?> gtnMultiBlockBase) {
            return gtnMultiBlockBase.manaHatches.size();
        }
    },

    AspectHatch("AspectHatch", (base, tile, casing) -> base.hatchControl.addAspectHatchToMachineList(tile),
        GTN_AspectHatch.class) {

        @Override
        public long count(GTN_MultiBlockBase<?> gtnMultiBlockBase) {
            return gtnMultiBlockBase.aspectHatches.size();
        }
    },

    MeAspectHatch("MeAspectHatch", (base, tile, casing) -> base.hatchControl.addMeAspectHatchToMachineList(tile),
        GTN_MeAspectHatch.class) {

        @Override
        public long count(GTN_MultiBlockBase<?> gtnMultiBlockBase) {
            return gtnMultiBlockBase.meAspectHatches.size();
        }
    },

    DynamoMulti("DynamoMulti", (base, tile, casing) -> base.hatchControl.addDynamoMultiHatchToMachineList(tile),
        MTEHatchDynamoMulti.class) {

        @Override
        public long count(GTN_MultiBlockBase<?> gtnMultiBlockBase) {
            return gtnMultiBlockBase.mDynamoHatches.size();
        }
    };

    private final String name;
    private final List<Class<? extends IMetaTileEntity>> mteClasses;
    private final IGTHatchAdder<? super GTN_MultiBlockBase<?>> adder;

    GTN_HatchElement(String name, IGTHatchAdder<? super GTN_MultiBlockBase<?>> adder,
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
        return GTN_Utils.tr(name);
    }

    @Override
    public IGTHatchAdder<? super GTN_MultiBlockBase<?>> adder() {
        return adder;
    }
}
