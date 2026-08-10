package com.EvgenWarGold.GregTechNightmare.GregTech.Wireless;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_WirelessMEIOHub;

import gregtech.api.enums.ItemList;

public final class GTN_WirelessMEDataStick {

    private static final String TAG_LINK = "GTNWirelessME";
    private static final String TAG_NETWORK_ID = "NetworkId";
    private static final String TAG_NETWORK_NAME = "NetworkName";

    private GTN_WirelessMEDataStick() {}

    public static boolean isDataStick(ItemStack stack) {
        return ItemList.Tool_DataStick.isStackEqual(stack, false, true);
    }

    public static boolean isWirelessLinkStick(ItemStack stack) {
        return isDataStick(stack) && stack.stackTagCompound != null && stack.stackTagCompound.hasKey(TAG_LINK);
    }

    public static void write(ItemStack stack, GTN_WirelessMEIOHub hub) {
        if (!isDataStick(stack) || hub.getNetworkId() == null) return;

        if (stack.stackTagCompound == null) {
            stack.stackTagCompound = new NBTTagCompound();
        }

        NBTTagCompound linkTag = new NBTTagCompound();
        linkTag.setString(TAG_NETWORK_ID, hub.getNetworkId().toString());
        linkTag.setString(TAG_NETWORK_NAME, hub.getNetworkName());
        stack.stackTagCompound.setTag(TAG_LINK, linkTag);
        stack.setStackDisplayName("Wireless ME Link: " + hub.getNetworkName());
    }

    public static UUID getNetworkId(ItemStack stack) {
        if (!isWirelessLinkStick(stack)) return null;

        String networkId = stack.stackTagCompound.getCompoundTag(TAG_LINK).getString(TAG_NETWORK_ID);
        if (networkId.isEmpty()) return null;

        try {
            return UUID.fromString(networkId);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static String getNetworkName(ItemStack stack) {
        if (!isWirelessLinkStick(stack)) return "";
        return stack.stackTagCompound.getCompoundTag(TAG_LINK).getString(TAG_NETWORK_NAME);
    }
}
