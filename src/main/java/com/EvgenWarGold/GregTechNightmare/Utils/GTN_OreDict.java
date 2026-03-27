package com.EvgenWarGold.GregTechNightmare.Utils;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;

public class GTN_OreDict {

    // Ingot
    public static ItemStack getIngot(Materials materials, long amount) {
        return GTOreDictUnificator.get(OrePrefixes.ingot, materials, amount);
    }

    public static ItemStack getIngot(Materials materials) {
        return getIngot(materials, 1L);
    }

    // Dust
    public static ItemStack getDust(Materials materials, long amount) {
        return GTOreDictUnificator.get(OrePrefixes.dust, materials, amount);
    }

    public static ItemStack getDust(Materials materials) {
        return GTOreDictUnificator.get(OrePrefixes.dust, materials, 1L);
    }

    // Gem
    public static ItemStack getGem(Materials materials, long amount) {
        return GTOreDictUnificator.get(OrePrefixes.gem, materials, amount);
    }

    public static ItemStack getGem(Materials materials) {
        return getGem(materials, 1L);
    }

    // Block
    public static ItemStack getBlock(Materials materials, long amount) {
        return GTOreDictUnificator.get(OrePrefixes.block, materials, amount);
    }

    public static ItemStack getBlock(Materials materials) {
        return getBlock(materials, 1L);
    }

    // SuperDense
    public static ItemStack getSuperDense(Materials materials, long amount) {
        return GTOreDictUnificator.get(OrePrefixes.plateSuperdense, materials, amount);
    }

    public static ItemStack getSuperDense(Materials materials) {
        return getSuperDense(materials, 1L);
    }

    // FrameGt
    public static ItemStack getFrameGt(Materials materials, long amount) {
        return GTOreDictUnificator.get(OrePrefixes.frameGt, materials, amount);
    }

    public static ItemStack getFrameGt(Materials materials) {
        return getFrameGt(materials, 1L);
    }

    // GearGt
    public static ItemStack getGearGt(Materials materials, long amount) {
        return GTOreDictUnificator.get(OrePrefixes.gearGt, materials, amount);
    }

    public static ItemStack getGearGt(Materials materials) {
        return getGearGt(materials, 1L);
    }

    // WireGt01
    public static ItemStack getWireGt01(Materials materials, long amount) {
        return GTOreDictUnificator.get(OrePrefixes.wireGt01, materials, amount);
    }

    public static ItemStack getWireGt01(Materials materials) {
        return getWireGt01(materials, 1L);
    }

    // WireGt16
    public static ItemStack getWireGt16(Materials materials, long amount) {
        return GTOreDictUnificator.get(OrePrefixes.wireGt16, materials, amount);
    }

    public static ItemStack getWireGt16(Materials materials) {
        return getWireGt16(materials, 1L);
    }

    // Nanite
    public static ItemStack getNanite(Materials materials, long amount) {
        return GTOreDictUnificator.get(OrePrefixes.nanite, materials, amount);
    }

    public static ItemStack getNanite(Materials materials) {
        return getNanite(materials, 1L);
    }

    // Fluid
    public static FluidStack getFluid(Materials materials, long amount) {
        return materials.getFluid(amount);
    }

    public static FluidStack getFluid(Materials materials) {
        return getFluid(materials, 1L);
    }

    // Gas
    public static FluidStack getGas(Materials materials, long amount) {
        return materials.getGas(amount);
    }

    public static FluidStack getGas(Materials materials) {
        return getGas(materials, 1L);
    }

    // Dense
    public static ItemStack getDense(Materials materials, long amount) {
        return GTOreDictUnificator.get(OrePrefixes.plateDense, materials, amount);
    }

    public static ItemStack getDense(Materials materials) {
        return getDense(materials, 1);
    }

    // Plate
    public static ItemStack getplate(Materials materials, long amount) {
        return GTOreDictUnificator.get(OrePrefixes.plate, materials, amount);
    }

    public static ItemStack getplate(Materials materials) {
        return getplate(materials, 1);
    }

}
