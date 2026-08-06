package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.client.renderers.block.BlockRenderer;
import thaumcraft.client.renderers.block.BlockTubeRenderer;
import thaumcraft.common.blocks.BlockTube;
import thaumcraft.common.tiles.TileBellows;
import thaumcraft.common.tiles.TileTube;
import thaumcraft.common.tiles.TileTubeFilter;

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

    /**
     * @author EvgenWarGold
     * @reason Overwrite renderWorldBlock
     */
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 0 || metadata == 1 || metadata == 3 || metadata == 4 || metadata == 5 || metadata == 6) {
            renderer.field_152631_f = true;
            float AX_minx = W7;
            float AX_maxx = W9;
            boolean drawX = false;
            float AX_miny = W7;
            float AX_maxy = W9;
            float AX_minz = W7;
            float AX_maxz = W9;
            float AY_minx = W7;
            float AY_maxx = W9;
            boolean drawY = false;
            float AY_miny = W7;
            float AY_maxy = W9;
            float AY_minz = W7;
            float AY_maxz = W9;
            float AZ_minx = W7;
            float AZ_maxx = W9;
            boolean drawZ = false;
            float AZ_miny = W7;
            float AZ_maxy = W9;
            float AZ_minz = W7;
            float AZ_maxz = W9;
            boolean notConduit = false;
            ForgeDirection fd = null;
            IEssentiaTransport tube = null;
            TileEntity tt = world.getTileEntity(x, y, z);
            if (tt instanceof IEssentiaTransport) {
                tube = (IEssentiaTransport) tt;
            }

            for (int side = 0; side < 6; ++side) {
                fd = ForgeDirection.getOrientation(side);
                if (tube == null || tube.isConnectable(fd)) {
                    TileEntity te = this.getConnectableTile(world, x, y, z, fd);
                    if (te != null && (metadata == 4 || !(te instanceof TileBellows))) {
                        if (!(te instanceof TileTube)) {
                            notConduit = true;
                        }

                        switch (side) {
                            case 0:
                                AY_miny = 0.0F;
                                drawY = true;

                                if (te instanceof IGregTechTileEntity gte) {
                                    IMetaTileEntity mte = gte.getMetaTileEntity();

                                    if (mte instanceof IEssentiaTransport et) {
                                        if (et.renderExtendedTube()) {
                                            AY_miny = -W6;
                                            break;
                                        }
                                    }
                                }

                                if (te instanceof IEssentiaTransport
                                    && ((IEssentiaTransport) te).renderExtendedTube()) {
                                    AY_miny = -W6;
                                }
                                break;
                            case 1:
                                AY_maxy = 1.0F;
                                drawY = true;

                                if (te instanceof IGregTechTileEntity gte) {
                                    IMetaTileEntity mte = gte.getMetaTileEntity();

                                    if (mte instanceof IEssentiaTransport et) {
                                        if (et.renderExtendedTube()) {
                                            AY_maxy = 1.0F + W6;
                                            break;
                                        }
                                    }
                                }

                                if (te instanceof IEssentiaTransport
                                    && ((IEssentiaTransport) te).renderExtendedTube()) {
                                    AY_maxy = 1.0F + W6;
                                }
                                break;
                            case 2:
                                AZ_minz = 0.0F;
                                drawZ = true;

                                if (te instanceof IGregTechTileEntity gte) {
                                    IMetaTileEntity mte = gte.getMetaTileEntity();

                                    if (mte instanceof IEssentiaTransport et) {
                                        if (et.renderExtendedTube()) {
                                            AZ_minz = -W6;
                                            break;
                                        }
                                    }
                                }

                                if (te instanceof IEssentiaTransport
                                    && ((IEssentiaTransport) te).renderExtendedTube()) {
                                    AZ_minz = -W6;
                                }
                                break;
                            case 3:
                                AZ_maxz = 1.0F;
                                drawZ = true;

                                if (te instanceof IGregTechTileEntity gte) {
                                    IMetaTileEntity mte = gte.getMetaTileEntity();

                                    if (mte instanceof IEssentiaTransport et) {
                                        if (et.renderExtendedTube()) {
                                            AZ_maxz = 1.0F + W6;
                                            break;
                                        }
                                    }
                                }

                                if (te instanceof IEssentiaTransport
                                    && ((IEssentiaTransport) te).renderExtendedTube()) {
                                    AZ_maxz = 1.0F + W6;
                                }
                                break;
                            case 4:
                                AX_minx = 0.0F;
                                drawX = true;

                                if (te instanceof IGregTechTileEntity gte) {
                                    IMetaTileEntity mte = gte.getMetaTileEntity();

                                    if (mte instanceof IEssentiaTransport et) {
                                        if (et.renderExtendedTube()) {
                                            AX_minx = -W6;
                                            break;
                                        }
                                    }
                                }

                                if (te instanceof IEssentiaTransport
                                    && ((IEssentiaTransport) te).renderExtendedTube()) {
                                    AX_minx = -W6;
                                }
                                break;
                            case 5:
                                AX_maxx = 1.0F;
                                drawX = true;

                                if (te instanceof IGregTechTileEntity gte) {
                                    IMetaTileEntity mte = gte.getMetaTileEntity();

                                    if (mte instanceof IEssentiaTransport et) {
                                        if (et.renderExtendedTube()) {
                                            AX_maxx = 1.0F + W6;
                                        }
                                    }
                                }

                                if (te instanceof IEssentiaTransport
                                    && ((IEssentiaTransport) te).renderExtendedTube()) {
                                    AX_maxx = 1.0F + W6;
                                }
                        }
                    }
                }
            }

            int drawn = 0;
            if (drawX) {
                ++drawn;
                block.setBlockBounds(AX_minx, AX_miny, AX_minz, AX_maxx, AX_maxy, AX_maxz);
                renderer.setRenderBoundsFromBlock(block);
                renderer.renderStandardBlock(block, x, y, z);
            }

            if (drawY) {
                ++drawn;
                block.setBlockBounds(AY_minx, AY_miny, AY_minz, AY_maxx, AY_maxy, AY_maxz);
                renderer.setRenderBoundsFromBlock(block);
                renderer.renderStandardBlock(block, x, y, z);
            }

            if (drawZ) {
                ++drawn;
                block.setBlockBounds(AZ_minx, AZ_miny, AZ_minz, AZ_maxx, AZ_maxy, AZ_maxz);
                renderer.setRenderBoundsFromBlock(block);
                renderer.renderStandardBlock(block, x, y, z);
            }

            if (metadata == 3) {
                renderer.overrideBlockTexture = ((BlockTube) block).icon[3];
                block.setBlockBounds(
                    W6 - 0.03125F,
                    W6 - 0.03125F,
                    W6 - 0.03125F,
                    W10 + 0.03125F,
                    W10 + 0.03125F,
                    W10 + 0.03125F);
                renderer.setRenderBoundsFromBlock(block);
                renderer.renderStandardBlock(block, x, y, z);
                TileEntity te = world.getTileEntity(x, y, z);
                float r = 1.0F;
                float g = 1.0F;
                float b = 1.0F;
                if (te instanceof TileTubeFilter && ((TileTubeFilter) te).aspectFilter != null) {
                    Color c = new Color(((TileTubeFilter) te).aspectFilter.getColor());
                    r = (float) c.getRed() / 255.0F;
                    g = (float) c.getGreen() / 255.0F;
                    b = (float) c.getBlue() / 255.0F;
                }

                renderer.overrideBlockTexture = ((BlockTube) block).icon[4];
                block.setBlockBounds(
                    W6 - 0.03125F,
                    W6 - 0.03125F,
                    W6 - 0.03125F,
                    W10 + 0.03125F,
                    W10 + 0.03125F,
                    W10 + 0.03125F);
                renderer.setRenderBoundsFromBlock(block);
                renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, r, g, b);
            } else if (metadata == 4) {
                block.setBlockBounds(W4, W4, W4, W12, W12, W12);
                renderer.setRenderBoundsFromBlock(block);
                renderer.renderStandardBlock(block, x, y, z);
            } else if (drawn != 0 && !notConduit && metadata != 1) {
                if (metadata != 5) {
                    renderer.overrideBlockTexture = ((BlockTube) block).icon[2];
                }

                block.setBlockBounds(
                    W7 - 0.03125F,
                    W7 - 0.03125F,
                    W7 - 0.03125F,
                    W9 + 0.03125F,
                    W9 + 0.03125F,
                    W9 + 0.03125F);
                renderer.setRenderBoundsFromBlock(block);
                renderer.renderStandardBlock(block, x, y, z);
            } else {
                renderer.overrideBlockTexture = ((BlockTube) block).icon[1];
                block.setBlockBounds(W6, W6, W6, W10, W10, W10);
                renderer.setRenderBoundsFromBlock(block);
                renderer.renderStandardBlock(block, x, y, z);
            }

            renderer.field_152631_f = false;
        }

        renderer.clearOverrideBlockTexture();
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        renderer.setRenderBoundsFromBlock(block);
        return true;
    }
}
