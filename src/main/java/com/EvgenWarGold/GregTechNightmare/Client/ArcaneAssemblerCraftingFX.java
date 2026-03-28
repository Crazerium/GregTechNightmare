package com.EvgenWarGold.GregTechNightmare.Client;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ArcaneAssemblerCraftingFX extends EntityFX {

    public ArcaneAssemblerCraftingFX(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.prevPosX = this.posX = x;
        this.prevPosY = this.posY = y;
        this.prevPosZ = this.posZ = z;
        this.motionX = (this.rand.nextDouble() - 0.5D) * 0.2D;
        this.motionY = this.rand.nextDouble() * 0.2D;
        this.motionZ = (this.rand.nextDouble() - 0.5D) * 0.2D;
        this.particleScale = 0.1F + this.rand.nextFloat() * 0.2F;
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.particleMaxAge = 40 + this.rand.nextInt(20);
        this.noClip = true;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
            return;
        }
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.90D;
        this.motionY *= 0.90D;
        this.motionZ *= 0.90D;
        this.motionY += 0.004D;
        this.particleAlpha = 1.0F - ((float) this.particleAge / this.particleMaxAge);
    }
}
