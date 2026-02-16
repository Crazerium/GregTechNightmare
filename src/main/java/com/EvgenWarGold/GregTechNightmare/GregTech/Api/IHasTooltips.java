package com.EvgenWarGold.GregTechNightmare.GregTech.Api;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IHasTooltips {

    void setTooltips(int metaValue, @Nullable String[] tooltips, boolean advancedMode);

    @Nullable
    String[] getTooltips(int metaValue, boolean advancedMode);

    @SideOnly(Side.CLIENT)
    static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }
}
