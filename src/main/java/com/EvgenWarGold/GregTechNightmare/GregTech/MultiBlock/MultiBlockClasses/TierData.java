package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import static gregtech.api.enums.Textures.BlockIcons.ERROR_TEXTURE_INDEX;

import gregtech.api.enums.HeatingCoilLevel;

public class TierData {

    private int casingTier = -1;
    public int countCasing = 0;
    private int casingTextureId = ERROR_TEXTURE_INDEX;
    private GTN_Casings casing = GTN_Casings.SolidSteelMachineCasing;
    private String channelName = "";
    private boolean isMainCasing = false;
    private HeatingCoilLevel coilLevel;

    public HeatingCoilLevel getCoilLevel() {
        return coilLevel;
    }

    public void setCoilLevel(HeatingCoilLevel aCoilLevel) {
        coilLevel = aCoilLevel;
    }

    public int getCasingTier() {
        return casingTier;
    }

    public boolean getIsMainCasing() {
        return isMainCasing;
    }

    public void setIsMainCasing(boolean mainCasing) {
        this.isMainCasing = mainCasing;
    }

    public int getCountCasing() {
        return countCasing;
    }

    public void setCasingTier(int casingTier) {
        this.casingTier = casingTier;
    }

    public void setCasingTextureId(int casingTextureId) {
        this.casingTextureId = casingTextureId;
    }

    public int getCasingTextureId() {
        return casingTextureId;
    }

    public void setCasing(GTN_Casings casing) {
        this.casing = casing;
    }

    public GTN_Casings getCasing() {
        return casing;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void reset() {
        casingTier = -1;
        countCasing = 0;
    }
}
