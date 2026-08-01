package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.IEssentiaTransport;

@Mixin(value = ThaumcraftApiHelper.class, remap = false)
public class ThaumcraftApiHelperMixin {

    /**
     * @author EvgenWarGold
     * @reason Overwrite getConnectableTile
     */
    @Overwrite
    public static TileEntity getConnectableTile(World world, int x, int y, int z, ForgeDirection face) {
        TileEntity te = world.getTileEntity(x + face.offsetX, y + face.offsetY, z + face.offsetZ);
        switch (te) {
            case null -> {
                return null;
            }
            case IEssentiaTransport iEssentiaTransport -> {
                return iEssentiaTransport.isConnectable(face.getOpposite()) ? te : null;
            }

            case IGregTechTileEntity gte -> {
                IMetaTileEntity mte = gte.getMetaTileEntity();
                if (mte instanceof IEssentiaTransport) {
                    return ((IEssentiaTransport) mte).isConnectable(face.getOpposite()) ? te : null;
                }
            }
            default -> {
            }
        }

        return null;
    }

    /**
     * @author EvgenWarGold
     * @reason Overwrite getConnectableTile
     */
    @Overwrite
    public static TileEntity getConnectableTile(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        TileEntity te = world.getTileEntity(x + face.offsetX, y + face.offsetY, z + face.offsetZ);
        switch (te) {
            case null -> {
                return null;
            }

            case IEssentiaTransport iEssentiaTransport -> {
                return iEssentiaTransport.isConnectable(face.getOpposite()) ? te : null;
            }

            case IGregTechTileEntity gte -> {
                IMetaTileEntity mte = gte.getMetaTileEntity();
                if (mte instanceof IEssentiaTransport) {
                    return ((IEssentiaTransport) mte).isConnectable(face.getOpposite()) ? te : null;
                }
            }
            default -> {
            }
        }

        return null;
    }
}
