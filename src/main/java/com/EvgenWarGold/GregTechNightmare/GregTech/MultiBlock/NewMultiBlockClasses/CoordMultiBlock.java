package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses;

import java.util.Objects;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

public class CoordMultiBlock {

    public final int dim;
    public final int x;
    public final int y;
    public final int z;

    public CoordMultiBlock(int dim, int x, int y, int z) {
        this.dim = dim;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public World getWorld() {
        return DimensionManager.getWorld(dim);
    }

    public IGregTechTileEntity getMTEMultiBlockBase() {
        World world = getWorld();
        if (world == null) return null;
        if (!world.blockExists(x, y, z)) return null;
        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof IGregTechTileEntity gte)) return null;

        return gte;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, dim);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CoordMultiBlock other)) return false;
        return this.x == other.x && this.y == other.y && this.z == other.z && this.dim == other.dim;
    }

    @Override
    public String toString() {
        return "CoordMultiBlock: Dim = " + dim + ", X = " + x + ", Y = " + y + ", Z = " + z;
    }
}
