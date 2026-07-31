package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Generators.EV.Magen;

import net.minecraft.nbt.NBTTagCompound;

public class CatalystData {

    private static final String TAG_DURATION = "duration";
    private static final String TAG_BOOST = "boost";

    private int duration;
    private final double boost;

    public CatalystData(int duration, double boost) {
        this.duration = duration;
        this.boost = boost;
    }

    public double getBoost() {
        return boost;
    }

    public int getDuration() {
        return duration;
    }

    public void decreaseDuration() {
        this.duration--;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger(TAG_DURATION, duration);
        nbt.setDouble(TAG_BOOST, boost);
        return nbt;
    }

    public static CatalystData readFromNBT(NBTTagCompound nbt) {
        int duration = nbt.getInteger(TAG_DURATION);
        double boost = nbt.getDouble(TAG_BOOST);
        return new CatalystData(duration, boost);
    }
}
