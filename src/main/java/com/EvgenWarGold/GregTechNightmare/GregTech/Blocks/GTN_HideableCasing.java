package com.EvgenWarGold.GregTechNightmare.GregTech.Blocks;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Casing that becomes invisible while its multiblock is formed, so the controller can draw a model in its place.
 * <p>
 * Metadata {@code meta | HIDDEN_FLAG} is the same casing in the hidden state: it keeps collision, drops the normal
 * casing and lets light through, but none of its faces are rendered.
 */
public class GTN_HideableCasing extends MetaBlockCasing {

    public static final int HIDDEN_FLAG = 8;

    public GTN_HideableCasing(String unlocalizedName, byte textureIdOffsite) {
        super(unlocalizedName, textureIdOffsite);
    }

    public static boolean isHidden(int meta) {
        return (meta & HIDDEN_FLAG) != 0;
    }

    /** Switches the casing at the given position, if it is one; returns false otherwise. */
    public static boolean setHidden(World world, int x, int y, int z, boolean hidden) {
        Block block = world.getBlock(x, y, z);
        if (!(block instanceof GTN_HideableCasing)) return false;
        int meta = world.getBlockMetadata(x, y, z);
        int newMeta = hidden ? meta | HIDDEN_FLAG : meta & ~HIDDEN_FLAG;
        if (meta == newMeta) return true;
        world.setBlockMetadataWithNotify(x, y, z, newMeta, 2);
        world.func_147451_t(x, y, z); // relight, the light opacity depends on the state
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        return isHidden(world.getBlockMetadata(x, y, z)) ? 0 : 255;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        // x, y, z are the coordinates of the neighbour on that side
        ForgeDirection direction = ForgeDirection.getOrientation(side);
        int selfMeta = world.getBlockMetadata(x - direction.offsetX, y - direction.offsetY, z - direction.offsetZ);
        if (isHidden(selfMeta)) return false;
        // the block is not opaque, so cull faces between visible casings manually
        if (world.getBlock(x, y, z) == this && !isHidden(world.getBlockMetadata(x, y, z))) return false;
        return super.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return super.getIcon(side, meta & ~HIDDEN_FLAG);
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata & ~HIDDEN_FLAG;
    }

    @Override
    public int getDamageValue(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) & ~HIDDEN_FLAG;
    }
}
