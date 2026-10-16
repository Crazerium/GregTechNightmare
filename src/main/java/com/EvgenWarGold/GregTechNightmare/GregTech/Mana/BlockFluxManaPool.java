package com.EvgenWarGold.GregTechNightmare.GregTech.Mana;

import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.client.core.handler.HUDHandler;

/** See {@link TileFluxManaPool}. */
public class BlockFluxManaPool extends BlockContainer implements IWandHUD {

    /** Same color as the Botania mana pool HUD. */
    private static final int HUD_COLOR = 0x4444FF;

    private IIcon sideIcon;
    private IIcon topIcon;

    public BlockFluxManaPool() {
        super(Material.rock);
        setBlockName("GTN_FluxManaPool");
        setHardness(2.0F);
        setResistance(10.0F);
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileFluxManaPool();
    }

    /** Placeholder textures: the AE2 fluix block on the sides and Botania mana on top. */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.sideIcon = register.registerIcon("appliedenergistics2:BlockFluix");
        this.topIcon = register.registerIcon("botania:manaWater");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.topIcon : this.sideIcon;
    }

    /** Mana bar like the Botania pool has, plus the exact numbers since the network can hold far more than a pool. */
    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUD(Minecraft mc, ScaledResolution res, World world, int x, int y, int z) {
        if (!(world.getTileEntity(x, y, z) instanceof TileFluxManaPool pool)) return;
        long mana = pool.getManaLong();
        long capacity = pool.getCapacityLong();

        // the Botania bar works with ints, scale both values down for huge networks
        int shift = 0;
        while ((capacity >> shift) > Integer.MAX_VALUE) shift++;
        HUDHandler.drawSimpleManaHUD(
            HUD_COLOR,
            (int) (mana >> shift),
            (int) Math.max(1, capacity >> shift),
            this.getLocalizedName(),
            res);

        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        String text = format.format(mana) + " / " + format.format(capacity);
        mc.fontRenderer.drawStringWithShadow(
            text,
            res.getScaledWidth() / 2 - mc.fontRenderer.getStringWidth(text) / 2,
            res.getScaledHeight() / 2 + 30,
            HUD_COLOR);
    }
}
