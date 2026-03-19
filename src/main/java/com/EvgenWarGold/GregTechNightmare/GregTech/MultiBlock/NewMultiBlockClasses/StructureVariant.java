package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;

public class StructureVariant<T extends GTN_NewMultiBlockBase<T>> {

    public final String piece;
    public final String[][] shape;
    public final int offsetHorizontal;
    public final int offsetVertical;
    public final int offsetDepth;
    public final int tier;
    public final GTN_Casings casing;

    // spotless:off
    public StructureVariant(String piece,
                            String[][] shape,
                            int offsetHorizontal,
                            int offsetVertical,
                            int offsetDepth,
                            int tier,
                            GTN_Casings casing) {
        this.piece = piece;
        this.shape = shape;
        this.offsetHorizontal = offsetHorizontal;
        this.offsetVertical = offsetVertical;
        this.offsetDepth = offsetDepth;
        this.tier = tier;
        this.casing = casing;
    }
    //spotless:on

    public boolean check(T multiblock) {
        multiblock.clearHatches();
        boolean valid = multiblock.checkPieceProxy(piece, offsetHorizontal, offsetVertical, offsetDepth);

        if (valid) {
            multiblock.setMultiBlockTier(tier);
            multiblock.setMainCasing(casing);
        }

        return valid;
    }

}
