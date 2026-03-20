package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.GTN_NewMultiBlockBase;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@Mixin(value = MTEMultiBlockBase.class, remap = false)
public abstract class MTEMultiblockBaseMixins {

    @Redirect(
        method = "getWailaBody",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;getInteger(Ljava/lang/String;)I"))
    private int removeParallelFromWaila(NBTTagCompound tag, String key, ItemStack itemStack, List<String> currentTip,
        IWailaDataAccessor accessor, IWailaConfigHandler config) {

        if ("maxParallelRecipes".equals(key)) {
            TileEntity te = accessor.getTileEntity();

            if (te instanceof IGregTechTileEntity gte) {
                if (gte.getMetaTileEntity() instanceof GTN_NewMultiBlockBase<?>) {
                    return 1;
                }
            }
        }

        return tag.getInteger(key);
    }
}
