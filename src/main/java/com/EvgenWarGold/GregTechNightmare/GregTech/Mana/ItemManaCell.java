package com.EvgenWarGold.GregTechNightmare.GregTech.Mana;

import static com.EvgenWarGold.GregTechNightmare.GregTech.Mana.AEManaStackType.MANA_STACK_TYPE;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;

import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.exceptions.AppEngException;
import appeng.api.implementations.items.IStorageCell;
import appeng.api.implementations.tiles.IChestOrDrive;
import appeng.api.storage.ICellHandler;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEStackType;
import appeng.core.sync.GuiBridge;
import appeng.items.contents.CellUpgrades;
import appeng.tile.inventory.IAEStackInventory;
import appeng.util.Platform;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Mana storage cells. Sizes and density follow Applied Botanics: a {@code N}k cell holds {@code N * 1000} bytes of
 * 500 mana each, so the 1k cell stores half of a Botania mana pool.
 */
public class ItemManaCell extends Item implements ICellHandler, IStorageCell {

    /** Cell size in kilobytes for every meta value. */
    private static final int[] KILOBYTES = { 1, 4, 16, 64, 256 };
    private static final double[] IDLE_DRAIN = { 0.5, 1.0, 1.5, 2.0, 2.5 };
    /** Placeholder icons until the cells get their own textures. */
    private static final String[] ICONS = { "ae2fc:fluid_storage.1", "ae2fc:fluid_storage.4", "ae2fc:fluid_storage.16",
        "ae2fc:fluid_storage.64", "ae2fc:fluid_storage.256" };

    private IIcon[] icons;

    public ItemManaCell() {
        setUnlocalizedName("gtn.ManaCell");
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabMisc);
        AEApi.instance()
            .registries()
            .cell()
            .addCellHandler(this);
    }

    public static int getSizeCount() {
        return KILOBYTES.length;
    }

    private static int getIndex(ItemStack stack) {
        return MathHelper.clamp_int(stack.getItemDamage(), 0, KILOBYTES.length - 1);
    }

    /** Maximum amount of mana the cell can hold. */
    public static long getCapacity(ItemStack stack) {
        return KILOBYTES[getIndex(stack)] * 1000L * AEManaStackType.MANA_PER_BYTE;
    }

    // region Item
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedName() + "." + KILOBYTES[getIndex(stack)] + "k";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[ICONS.length];
        for (int i = 0; i < ICONS.length; i++) icons[i] = register.registerIcon(ICONS[i]);
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        return icons[MathHelper.clamp_int(damage, 0, icons.length - 1)];
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < KILOBYTES.length; i++) list.add(new ItemStack(item, 1, i));
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        long stored = 0;
        IMEInventoryHandler<?> handler = getCellInventory(stack, null, MANA_STACK_TYPE);
        if (handler instanceof ManaCellInventoryHandler cell && cell.getCellInv() != null) {
            stored = cell.getCellInv()
                .getStoredItemCount();
        }
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        tooltip
            .add(GTN_Utils.tr("GTN.ManaCell.tooltip.stored", format.format(stored), format.format(getCapacity(stack))));
    }
    // endregion

    // region ICellHandler
    @Override
    public boolean isCell(ItemStack stack) {
        return stack != null && stack.getItem() == this;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public IMEInventoryHandler getCellInventory(ItemStack stack, ISaveProvider saveProvider, IAEStackType<?> type) {
        if (stack == null || type != MANA_STACK_TYPE || stack.getItem() != this) return null;
        try {
            return new ManaCellInventoryHandler(new ManaCellInventory(stack, saveProvider));
        } catch (AppEngException e) {
            return null;
        }
    }

    @Override
    public IIcon getTopTexture_Light() {
        return null;
    }

    @Override
    public IIcon getTopTexture_Medium() {
        return null;
    }

    @Override
    public IIcon getTopTexture_Dark() {
        return null;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void openChestGui(EntityPlayer player, IChestOrDrive chest, ICellHandler cellHandler,
        IMEInventoryHandler inv, ItemStack stack, StorageChannel channel) {
        Platform.openGUI(player, (TileEntity) chest, chest.getUp(), GuiBridge.GUI_ME);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public int getStatusForCell(ItemStack stack, IMEInventory handler) {
        return handler instanceof ManaCellInventoryHandler cell ? cell.getCellStatus() : 0;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public double cellIdleDrain(ItemStack stack, IMEInventory handler) {
        return IDLE_DRAIN[getIndex(stack)];
    }
    // endregion

    // region IStorageCell
    @Override
    public int getBytes(ItemStack stack) {
        return KILOBYTES[getIndex(stack)] * 1000;
    }

    @Override
    public long getBytesLong(ItemStack stack) {
        return getBytes(stack);
    }

    /** Mana has a single type, so no bytes are reserved per type and the whole cell holds mana. */
    @Override
    public int BytePerType(ItemStack stack) {
        return 0;
    }

    @Override
    public int getBytesPerType(ItemStack stack) {
        return 0;
    }

    @Override
    public int getTotalTypes(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean storableInStorageCell() {
        return false;
    }

    @Override
    public boolean isStorageCell(ItemStack stack) {
        return isCell(stack);
    }

    @Override
    public double getIdleDrain() {
        return IDLE_DRAIN[0];
    }

    @Override
    public double getIdleDrain(ItemStack stack) {
        return IDLE_DRAIN[getIndex(stack)];
    }

    @Override
    public boolean isEditable(ItemStack stack) {
        return false;
    }

    @Override
    public IInventory getUpgradesInventory(ItemStack stack) {
        return new CellUpgrades(stack, 0);
    }

    @Override
    public IAEStackInventory getConfigAEInventory(ItemStack stack) {
        return new IAEStackInventory(null, 0);
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack stack) {
        return FuzzyMode.IGNORE_ALL;
    }

    @Override
    public void setFuzzyMode(ItemStack stack, FuzzyMode fuzzyMode) {}

    @Override
    public IAEStackType<?> getStackType() {
        return MANA_STACK_TYPE;
    }
    // endregion
}
