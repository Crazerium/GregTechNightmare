package com.EvgenWarGold.GregTechNightmare.GregTech.Api;

import gregtech.api.util.GTUtility;

public interface ICasing {

    byte getTexturePageIndex();

    byte getTextureIndexInPage(int meta);

    default int getTextureId(byte page, byte index) {
        return GTUtility.getTextureId(page, index);
    }
}
