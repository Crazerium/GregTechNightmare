package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses;

import static gregtech.api.enums.Textures.BlockIcons.ERROR_TEXTURE_INDEX;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;

public class TierData {

    public int casingTier = -1;
    public int countCasing = 0;
    public int casingTextureId = ERROR_TEXTURE_INDEX;
    public GTN_Casings casing = GTN_Casings.SolidSteelMachineCasing;
    public String channelName = "";

    public int getCasingTier() {
        return casingTier;
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
