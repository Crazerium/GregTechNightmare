package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.client.renderers.block.BlockRenderer;
import thaumcraft.client.renderers.block.BlockTubeRenderer;
import thaumcraft.common.tiles.TileBellows;

@Mixin(value = BlockTubeRenderer.class, remap = false)
public abstract class BlockTubeRendererMixin extends BlockRenderer {

    /**
     * @author EvgenWarGold
     * @reason Overwrite getConnectableTile
     */
    @Overwrite
    public TileEntity getConnectableTile(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        TileEntity te = world.getTileEntity(x + face.offsetX, y + face.offsetY, z + face.offsetZ);

        if (te instanceof IGregTechTileEntity gte) {
            IMetaTileEntity mte = gte.getMetaTileEntity();

            if (mte instanceof IEssentiaTransport && ((IEssentiaTransport) mte).isConnectable(face.getOpposite())) {
                return te;
            }
        }

        return te instanceof IEssentiaTransport && ((IEssentiaTransport) te).isConnectable(face.getOpposite()) ? te
            : (te instanceof TileBellows && ((TileBellows) te).orientation == face.getOpposite()
                .ordinal() ? te : null);
    }

    @Inject(method = "renderWorldBlock", at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 1))
    private void patchTubeBounds(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer, CallbackInfoReturnable<Boolean> cir, @Local(name = "AX_minx") LocalFloatRef AX_minx,
        @Local(name = "AX_maxx") LocalFloatRef AX_maxx, @Local(name = "drawX") LocalBooleanRef drawX,
        @Local(name = "AY_miny") LocalFloatRef AY_miny, @Local(name = "AY_maxy") LocalFloatRef AY_maxy,
        @Local(name = "drawY") LocalBooleanRef drawY, @Local(name = "AZ_minz") LocalFloatRef AZ_minz,
        @Local(name = "AZ_maxz") LocalFloatRef AZ_maxz, @Local(name = "drawZ") LocalBooleanRef drawZ,
        @Local(name = "metadata") int metadata, @Local(name = "tube") IEssentiaTransport tube) {
        for (int side = 0; side < 6; ++side) {
            ForgeDirection fd = ForgeDirection.getOrientation(side);
            if (tube == null || tube.isConnectable(fd)) {
                TileEntity te = this.getConnectableTile(world, x, y, z, fd);
                if (te != null && (metadata == 4 || !(te instanceof TileBellows))) {
                    boolean extended = false;
                    if (te instanceof IGregTechTileEntity gte) {
                        IMetaTileEntity mte = gte.getMetaTileEntity();
                        if (mte instanceof IEssentiaTransport et) {
                            extended = et.renderExtendedTube();
                        }
                    }

                    if (!extended && te instanceof IEssentiaTransport) {
                        extended = ((IEssentiaTransport) te).renderExtendedTube();
                    }

                    switch (side) {
                        case 0:
                            AY_miny.set(0.0F);
                            drawY.set(true);
                            if (extended) {
                                AY_miny.set(-W6);
                            }
                            break;
                        case 1:
                            AY_maxy.set(1.0F);
                            drawY.set(true);
                            if (extended) {
                                AY_maxy.set(1.0F + W6);
                            }
                            break;
                        case 2:
                            AZ_minz.set(0.0F);
                            drawZ.set(true);
                            if (extended) {
                                AZ_minz.set(-W6);
                            }
                            break;
                        case 3:
                            AZ_maxz.set(1.0F);
                            drawZ.set(true);
                            if (extended) {
                                AZ_maxz.set(1.0F + W6);
                            }
                            break;
                        case 4:
                            AX_minx.set(0.0F);
                            drawX.set(true);
                            if (extended) {
                                AX_minx.set(-W6);
                            }
                            break;
                        case 5:
                            AX_maxx.set(1.0F);
                            drawX.set(true);
                            if (extended) {
                                AX_maxx.set(1.0F + W6);
                            }
                            break;
                    }
                }
            }
        }
    }
}
