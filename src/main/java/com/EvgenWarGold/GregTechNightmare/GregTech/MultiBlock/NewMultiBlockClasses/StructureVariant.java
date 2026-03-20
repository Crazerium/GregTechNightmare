package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;

public class StructureVariant<T extends GTN_NewMultiBlockBase<T>> {

    public final String piece;
    public final String[][] shape;
    public final MultiblockOffsets multiblockOffsets;
    public final MultiblockArea multiblockArea;
    public final int tier;
    public final GTN_Casings casing;

    // spotless:off
    public StructureVariant(String piece,
                            String[][] shape,
                            MultiblockOffsets multiblockOffsets,
                            MultiblockArea multiblockArea,
                            int tier,
                            GTN_Casings casing) {
        this.piece = piece;
        this.shape = shape;
        this.multiblockOffsets = multiblockOffsets;
        this.multiblockArea = multiblockArea;
        this.tier = tier;
        this.casing = casing;
    }
    //spotless:on

    public boolean check(T multiblock) {
        multiblock.clearHatches();
        boolean valid = multiblock.checkPieceProxy(
            piece,
            multiblockOffsets.offsetHorizontal,
            multiblockOffsets.offsetVertical,
            multiblockOffsets.offsetDepth);

        if (valid) {
            multiblock.setMultiBlockTier(tier);
            multiblock.setMainCasing(casing);
        }

        return valid;
    }

}
