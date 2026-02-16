package com.EvgenWarGold.GregTechNightmare.GregTech.Api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import com.EvgenWarGold.GregTechNightmare.GregTechNightmare;
import com.EvgenWarGold.GregTechNightmare.Utils.GTNUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AbstractGtnMetaItem extends Item implements IHasVariantAndTooltips {

    protected final HashSet<Integer> usedMetaIds = new HashSet<>();
    protected final HashMap<Integer, String[]> tooltipMap = new HashMap<>();

    protected Map<Integer, IIcon> iconMap = new HashMap<>();

    public AbstractGtnMetaItem(String unlocalizedName) {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setUnlocalizedName(unlocalizedName);
    }

    @Override
    public int getMetadata(int aMeta) {
        return aMeta;
    }

    @Override
    public final String getUnlocalizedName(ItemStack aItemStack) {
        return super.getUnlocalizedName() + "." + aItemStack.getItemDamage();
    }

    @Override
    public final String getUnlocalizedName() {
        return super.getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item aItem, CreativeTabs aCreativeTabs, List<ItemStack> aList) {
        ItemStack[] variants = getVariants();
        aList.addAll(Arrays.asList(variants));
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack aItemStack, EntityPlayer aEntityPlayer, List<String> aTooltipsList,
        boolean isAdvancedMode) {
        String[] tooltips = getTooltips(aItemStack.getItemDamage(), IHasTooltips.isShiftKeyDown());
        if (tooltips != null) {
            aTooltipsList.addAll(Arrays.asList(tooltips));
        }
    }

    @Override
    public void registerIcons(IIconRegister register) {
        this.iconMap = registerAllVariantIcons(
            register,
            meta -> GregTechNightmare.RESOURCE_ROOT_ID + ":" + unlocalizedName + "/" + meta);
        this.itemIcon = iconMap.get(0);
    }

    @Override
    public IIcon getIconFromDamage(int meta) {
        return iconMap.get(meta);
    }

    @Override
    public ItemStack getVariant(int meta) throws IllegalArgumentException {
        return checkAndGetVariant(this, meta, usedMetaIds);
    }

    @Override
    public ItemStack[] getVariants() {
        return getAllVariants(this, usedMetaIds);
    }

    @Override
    public ItemStack registerVariant(int meta) throws IllegalArgumentException {
        return checkAndRegisterVariant(this, meta, usedMetaIds);
    }

    @Override
    public @Unmodifiable Set<Integer> getVariantIds() {
        return new HashSet<>(usedMetaIds);
    }

    @Override
    public void setTooltips(int metaValue, @Nullable String[] tooltips, boolean advanced) {
        tooltipMap.put(metaValue, tooltips);
    }

    @Override
    public @Nullable String[] getTooltips(int metaValue, boolean advanced) {
        return tooltipMap.get(metaValue);
    }

    @ApiStatus.Internal
    protected static ItemStack checkAndGetVariant(Item self, int meta, Collection<Integer> allowMetaValues)
        throws IllegalArgumentException {
        if (allowMetaValues.contains(meta)) {
            return GTNUtils.newItemWithMeta(self, meta);
        } else {
            throw new IllegalArgumentException("Invalid meta value: " + meta);
        }
    }

    @ApiStatus.Internal
    protected static ItemStack checkAndRegisterVariant(Item self, int meta, Collection<Integer> allowMetaValues)
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
            return GTNUtils.newItemWithMeta(self, meta);
        }
    }

    @ApiStatus.Internal
    protected static ItemStack[] getAllVariants(Item self, Collection<Integer> allowMetaValues) {
        return allowMetaValues.stream()
            .map(m -> GTNUtils.newItemWithMeta(self, m))
            .toArray(ItemStack[]::new);
    }
}
