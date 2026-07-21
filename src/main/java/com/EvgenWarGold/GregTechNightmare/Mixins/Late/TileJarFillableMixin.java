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
import thaumcraft.common.tiles.TileJar;
import thaumcraft.common.tiles.TileJarFillable;

@Mixin(value = TileJarFillable.class, remap = false)
public abstract class TileJarFillableMixin extends TileJar implements IEssentiaTransport {

    @Shadow
    public Aspect aspect = null;

    @Shadow
    public Aspect aspectFilter = null;

    @Shadow
    public int amount = 0;

    @Shadow
    public abstract int addToContainer(Aspect tt, int am);

    /**
     * @author EvgenWarGold
     * @reason Overwrite fillJar
     */
    @Overwrite
    void fillJar() {
        TileEntity te = ThaumcraftApiHelper
            .getConnectableTile(this.worldObj, this.xCoord, this.yCoord, this.zCoord, ForgeDirection.UP);
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

            if (!ic.canOutputTo(ForgeDirection.DOWN)) {
                return;
            }

            Aspect ta = null;
            if (this.aspectFilter != null) {
                ta = this.aspectFilter;
            } else if (this.aspect != null && this.amount > 0) {
                ta = this.aspect;
            } else if (ic.getEssentiaAmount(ForgeDirection.DOWN) > 0
                && ic.getSuctionAmount(ForgeDirection.DOWN) < this.getSuctionAmount(ForgeDirection.UP)
                && this.getSuctionAmount(ForgeDirection.UP) >= ic.getMinimumSuction()) {
                    ta = ic.getEssentiaType(ForgeDirection.DOWN);
                }

            if (ta != null && ic.getSuctionAmount(ForgeDirection.DOWN) < this.getSuctionAmount(ForgeDirection.UP)) {
                this.addToContainer(ta, ic.takeEssentia(ta, 1, ForgeDirection.DOWN));
            }
        }

    }
}
