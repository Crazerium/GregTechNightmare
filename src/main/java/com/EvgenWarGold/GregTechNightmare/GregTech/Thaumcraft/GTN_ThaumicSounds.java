package com.EvgenWarGold.GregTechNightmare.GregTech.Thaumcraft;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.client.GTSoundLoop;

/**
 * Thaumcraft sounds for the Thaumcraft machines. GT can only loop sounds from its own {@code SoundResource} enum, so
 * the machines keep the loop themselves through {@link #updateLoop}.
 */
public final class GTN_ThaumicSounds {

    public static final ResourceLocation INFUSER_LOOP = new ResourceLocation("thaumcraft", "infuser");
    public static final ResourceLocation CRUCIBLE_LOOP = new ResourceLocation("thaumcraft", "bubble");
    public static final String INFUSION_START = "thaumcraft:craftstart";

    private GTN_ThaumicSounds() {}

    /**
     * Same logic as GT {@code MTEMultiBlockBase.doActivitySound}: plays the loop while the machine works and fades it
     * out when it stops.
     *
     * @return the loop to keep in {@code activitySoundLoop}
     */
    @SideOnly(Side.CLIENT)
    public static GTSoundLoop updateLoop(@Nullable GTSoundLoop loop, IGregTechTileEntity base, ResourceLocation sound) {
        if (loop != null && loop.isDonePlaying()) loop = null;
        if (base.isActive() && !base.isMuffled()) {
            if (loop == null) {
                loop = new GTSoundLoop(sound, base, false, true);
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(loop);
            }
            return loop;
        }
        if (loop != null) loop.setFadeMe(true);
        return null;
    }

    /** Plays a sound for everyone near the machine (server side). */
    public static void playAt(IGregTechTileEntity base, String sound, float volume, float pitch) {
        if (base == null || base.getWorld() == null || sound == null) return;
        base.getWorld()
            .playSoundEffect(
                base.getXCoord() + 0.5,
                base.getYCoord() + 0.5,
                base.getZCoord() + 0.5,
                sound,
                volume,
                pitch);
    }
}
