package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileTube;

@Mixin(value = TileTube.class, remap = false)
public abstract class TileTubeMixin extends TileEntity {

    @Shadow
    public abstract boolean isConnectable(ForgeDirection face);

    @Shadow
    public ForgeDirection facing = ForgeDirection.NORTH;

    @Shadow
    Aspect suctionType = null;

    @Shadow
    int suction = 0;

    @Shadow
    public abstract Aspect getEssentiaType(ForgeDirection loc);

    @Shadow
    public abstract int getEssentiaAmount(ForgeDirection loc);

    @Shadow
    public abstract void setSuction(Aspect aspect, int amount);

    @Shadow
    int venting = 0;

    @Shadow
    int essentiaAmount = 0;

    @Shadow
    public abstract Aspect getSuctionType(ForgeDirection loc);

    @Shadow
    public abstract int getSuctionAmount(ForgeDirection loc);

    @Shadow
    public abstract int addEssentia(Aspect aspect, int amount, ForgeDirection face);

    /**
     * @author EvgenWarGold
     * @reason Overwrite canConnectSide
     */
    @Overwrite
    protected boolean canConnectSide(int side) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        TileEntity tile = this.worldObj
            .getTileEntity(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ);

        if (tile instanceof IGregTechTileEntity gte) {
            IMetaTileEntity mte = gte.getMetaTileEntity();

            return mte instanceof IEssentiaTransport;
        }

        return tile instanceof IEssentiaTransport;
    }

    /**
     * @author EvgenWarGold
     * @reason Overwrite calculateSuction
     */
    @Overwrite
    void calculateSuction(Aspect filter, boolean restrict, boolean directional) {
        this.suction = 0;
        this.suctionType = null;
        ForgeDirection loc = null;

        for (int dir = 0; dir < 6; ++dir) {
            try {
                loc = ForgeDirection.getOrientation(dir);
                if ((!directional || this.facing == loc.getOpposite()) && this.isConnectable(loc)) {
                    TileEntity te = ThaumcraftApiHelper
                        .getConnectableTile(this.worldObj, this.xCoord, this.yCoord, this.zCoord, loc);
                    if (te != null) {
                        IEssentiaTransport ic = null;
                        if (te instanceof IGregTechTileEntity gte) {
                            IMetaTileEntity mte = gte.getMetaTileEntity();
                            if (mte instanceof IEssentiaTransport) {
                                ic = (IEssentiaTransport) mte;
                            }

                        } else {
                            ic = (IEssentiaTransport) te;
                        }

                        if (ic == null) continue;

                        if ((filter == null || ic.getSuctionType(loc.getOpposite()) == null
                            || ic.getSuctionType(loc.getOpposite()) == filter)
                            && (filter != null || this.getEssentiaAmount(loc) <= 0
                                || ic.getSuctionType(loc.getOpposite()) == null
                                || this.getEssentiaType(loc) == ic.getSuctionType(loc.getOpposite()))
                            && (filter == null || this.getEssentiaAmount(loc) <= 0
                                || this.getEssentiaType(loc) == null
                                || ic.getSuctionType(loc.getOpposite()) == null
                                || this.getEssentiaType(loc) == ic.getSuctionType(loc.getOpposite()))) {
                            int suck = ic.getSuctionAmount(loc.getOpposite());
                            if (suck > 0 && suck > this.suction + 1) {
                                Aspect st = ic.getSuctionType(loc.getOpposite());
                                if (st == null) {
                                    st = filter;
                                }

                                this.setSuction(st, restrict ? suck / 2 : suck - 1);
                            }
                        }
                    }
                }
            } catch (Exception var10) {
                ;
            }
        }

    }

    /**
     * @author EvgenWarGold
     * @reason Overwrite checkVenting
     */
    @Overwrite
    void checkVenting() {
        ForgeDirection loc = null;

        for (int dir = 0; dir < 6; ++dir) {
            try {
                loc = ForgeDirection.getOrientation(dir);
                if (this.isConnectable(loc)) {
                    TileEntity te = ThaumcraftApiHelper
                        .getConnectableTile(this.worldObj, this.xCoord, this.yCoord, this.zCoord, loc);
                    if (te != null) {
                        IEssentiaTransport ic = null;
                        if (te instanceof IGregTechTileEntity gte) {
                            IMetaTileEntity mte = gte.getMetaTileEntity();
                            if (mte instanceof IEssentiaTransport) {
                                ic = (IEssentiaTransport) mte;
                            }

                        } else {
                            ic = (IEssentiaTransport) te;
                        }

                        if (ic == null) continue;

                        int suck = ic.getSuctionAmount(loc.getOpposite());
                        if (this.suction > 0 && (suck == this.suction || suck == this.suction - 1)
                            && this.suctionType != ic.getSuctionType(loc.getOpposite())) {
                            int c = -1;
                            if (this.suctionType != null) {
                                c = Config.aspectOrder.indexOf(this.suctionType);
                            }

                            this.worldObj
                                .addBlockEvent(this.xCoord, this.yCoord, this.zCoord, ConfigBlocks.blockTube, 1, c);
                            this.venting = 40;
                        }
                    }
                }
            } catch (Exception var7) {
                ;
            }
        }

    }

    /**
     * @author EvgenWarGold
     * @reason Overwrite equalizeWithNeighbours
     */
    @Overwrite
    void equalizeWithNeighbours(boolean directional) {
        ForgeDirection loc = null;
        if (this.essentiaAmount <= 0) {
            for (int dir = 0; dir < 6; ++dir) {
                try {
                    loc = ForgeDirection.getOrientation(dir);
                    if ((!directional || this.facing != loc.getOpposite()) && this.isConnectable(loc)) {
                        TileEntity te = ThaumcraftApiHelper
                            .getConnectableTile(this.worldObj, this.xCoord, this.yCoord, this.zCoord, loc);
                        if (te != null) {
                            IEssentiaTransport ic = null;
                            if (te instanceof IGregTechTileEntity gte) {
                                IMetaTileEntity mte = gte.getMetaTileEntity();
                                if (mte instanceof IEssentiaTransport) {
                                    ic = (IEssentiaTransport) mte;
                                }

                            } else {
                                ic = (IEssentiaTransport) te;
                            }

                            if (ic == null) continue;
                            if (ic.canOutputTo(loc.getOpposite())
                                && (this.getSuctionType((ForgeDirection) null) == null
                                    || this.getSuctionType((ForgeDirection) null)
                                        == ic.getEssentiaType(loc.getOpposite())
                                    || ic.getEssentiaType(loc.getOpposite()) == null)
                                && this.getSuctionAmount((ForgeDirection) null) > ic.getSuctionAmount(loc.getOpposite())
                                && this.getSuctionAmount((ForgeDirection) null) >= ic.getMinimumSuction()) {
                                Aspect a = this.getSuctionType((ForgeDirection) null);
                                if (a == null) {
                                    a = ic.getEssentiaType(loc.getOpposite());
                                    if (a == null) {
                                        a = ic.getEssentiaType(ForgeDirection.UNKNOWN);
                                    }
                                }

                                int am = this.addEssentia(a, ic.takeEssentia(a, 1, loc.getOpposite()), loc);
                                if (am > 0) {
                                    if (this.worldObj.rand.nextInt(100) == 0) {
                                        this.worldObj.addBlockEvent(
                                            this.xCoord,
                                            this.yCoord,
                                            this.zCoord,
                                            ConfigBlocks.blockTube,
                                            0,
                                            0);
                                    }

                                    return;
                                }
                            }
                        }
                    }
                } catch (Exception var8) {
                    ;
                }
            }

        }
    }
}
