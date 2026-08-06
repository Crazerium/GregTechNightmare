package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.glodblock.github.common.item.ItemFluidPacket;

import appeng.api.AEApi;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;

public final class AE2FCFluidPacketBridge {

    private AE2FCFluidPacketBridge() {}

    public static IAEItemStack toPacket(IAEFluidStack aeFluid) {
        if (aeFluid == null || aeFluid.getStackSize() <= 0) return null;
        FluidStack fluid = aeFluid.getFluidStack();
        if (fluid == null) return null;

        FluidStack copy = fluid.copy();
        long amount = aeFluid.getStackSize();
        copy.amount = amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;

        ItemStack packet = ItemFluidPacket.newStack(copy);
        if (packet == null) return null;
        IAEItemStack result = AEApi.instance().storage().createItemStack(packet);
        if (result != null) result.setStackSize(1L);
        return result;
    }
}
