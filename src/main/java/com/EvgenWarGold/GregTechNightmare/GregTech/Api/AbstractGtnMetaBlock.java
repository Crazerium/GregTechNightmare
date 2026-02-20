package com.EvgenWarGold.GregTechNightmare.GregTech.Api;

import static com.EvgenWarGold.GregTechNightmare.GregTechNightmare.RESOURCE_ROOT_ID;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AbstractGtnMetaBlock extends Block implements IHasVariantAndTooltips {

    protected final Set<Integer> usedMetaSet = new HashSet<>(16);
    protected final Map<Integer, String[]> tooltipMap = new HashMap<>();

    protected Map<Integer, IIcon> iconMap = new HashMap<>();

    public AbstractGtnMetaBlock(String unlocalizedName) {
        this(Material.iron, unlocalizedName);
    }

    public AbstractGtnMetaBlock(Material material, String unlocalizedName) {
        super(material);
        this.setBlockName(unlocalizedName);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return iconMap.get(meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        iconMap = registerAllVariantIcons(register, meta -> RESOURCE_ROOT_ID + ":" + unlocalizedName + "/" + meta);
        blockIcon = iconMap.get(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item aItem, CreativeTabs aCreativeTabs, List<ItemStack> list) {
        list.addAll(Arrays.asList(getVariants()));
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }

    @Override
    public int getDamageValue(World aWorld, int aX, int aY, int aZ) {
        return aWorld.getBlockMetadata(aX, aY, aZ);
    }

    @Override
    public String[] getTooltips(int meta, boolean advanced) {
        return tooltipMap.get(meta);
    }

    @Override
    public void setTooltips(int metaValue, @Nullable String[] tooltips, boolean advanced) {
        tooltipMap.put(metaValue, tooltips);
    }

    @Override
    public ItemStack getVariant(int meta) throws IllegalArgumentException {
        return checkAndGetVariant(this, meta, usedMetaSet);
    }

    @Override
    public ItemStack[] getVariants() {
        return getAllVariants(this, usedMetaSet);
    }

    @Override
    public ItemStack registerVariant(int meta) throws IllegalArgumentException {
        return checkAndRegisterVariant(this, meta, usedMetaSet);
    }

    @Override
    public @Unmodifiable Set<Integer> getVariantIds() {
        return new HashSet<>(usedMetaSet);
    }

    @ApiStatus.Internal
    protected static ItemStack checkAndGetVariant(Block self, int meta, Collection<Integer> allowMetaValues)
        throws IllegalArgumentException {
        if (allowMetaValues.contains(meta)) {
            return GTN_InventoryUtils.createItemWithMeta(self, meta);
        } else {
            throw new IllegalArgumentException("Invalid meta value: " + meta);
        }
    }

    @ApiStatus.Internal
    protected static ItemStack checkAndRegisterVariant(Block self, int meta, Collection<Integer> allowMetaValues)
        throws IllegalArgumentException {
        if (allowMetaValues.contains(meta)) {
            throw new IllegalArgumentException(
                "Meta value already exists: " + meta
                    + " in "
                    + self.getUnlocalizedName()
                    + " ("
                    + self.getClass()
                        .getSimpleName()
                    + ")");
        } else {
            allowMetaValues.add(meta);
            return GTN_InventoryUtils.createItemWithMeta(self, meta);
        }
    }

    @ApiStatus.Internal
    protected static ItemStack[] getAllVariants(Block self, Collection<Integer> allowMetaValues) {
        return allowMetaValues.stream()
            .map(m -> GTN_InventoryUtils.createItemWithMeta(self, m))
            .toArray(ItemStack[]::new);
    }
}
