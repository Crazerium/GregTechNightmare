package com.EvgenWarGold.GregTechNightmare.GregTech.Textures;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.IIconContainer;

public enum GTN_BlockIcons implements IIconContainer, Runnable {

    MANA_HATCH_OVERLAY,;

    public static final String RES_PATH = "gregtechnightmare:";
    private IIcon mIcon;

    GTN_BlockIcons() {
        GregTechAPI.sGTBlockIconload.add(this);
    }

    @Override
    public IIcon getIcon() {
        return mIcon;
    }

    @Override
    public IIcon getOverlayIcon() {
        return null;
    }

    @Override
    public ResourceLocation getTextureFile() {
        return TextureMap.locationBlocksTexture;
    }

    @Override
    public void run() {
        mIcon = GregTechAPI.sBlockIcons.registerIcon(RES_PATH + "overlays/" + this.name());
    }
}
