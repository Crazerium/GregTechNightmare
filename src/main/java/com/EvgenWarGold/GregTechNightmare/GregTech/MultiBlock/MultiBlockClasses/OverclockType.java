package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

public enum OverclockType {

    NONE(1, 1),
    NormalOverclock(2, 4),
    LowSpeedPerfectOverclock(2, 2),
    PerfectOverclock(4, 4);

    public final int timeReduction;
    public final int powerIncrease;

    OverclockType(int timeReduction, int powerIncrease) {
        this.timeReduction = timeReduction;
        this.powerIncrease = powerIncrease;
    }

    public static OverclockType checkOverclockType(int timeReduction, int powerIncrease) {
        for (OverclockType type : OverclockType.values()) {
            if (type.timeReduction == timeReduction && type.powerIncrease == powerIncrease) {
                return type;
            }
        }
        return NormalOverclock;
    }
}
