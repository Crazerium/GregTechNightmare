package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.common.entity.EntityManaBurst;

@Mixin(value = EntityManaBurst.class, remap = false)
public abstract class EntityManaBurstMixin {

    @Shadow
    protected abstract void onRecieverImpact(IManaReceiver tile, int x, int y, int z);

    @Shadow
    private boolean fake;

    @Shadow
    private boolean noParticles;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "onImpact", at = @At("HEAD"), cancellable = true, remap = false)
    private void onImpact(MovingObjectPosition movingobjectposition, CallbackInfo ci) {
        if (movingobjectposition.entityHit != null) return;

        EntityManaBurst self = (EntityManaBurst) (Object) this;
        TileEntity tile = self.worldObj
            .getTileEntity(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ);

        if (tile == null) return;

        if (tile instanceof IGregTechTileEntity gtTile) {

            if (gtTile.getMetaTileEntity() instanceof IManaReceiver receiver) {
                if (!fake && !noParticles && receiver.canRecieveManaFromBursts()) {
                    onRecieverImpact(receiver, tile.xCoord, tile.yCoord, tile.zCoord);
                    self.setDead();
                    ci.cancel();
                }
            }
        }
    }
}
