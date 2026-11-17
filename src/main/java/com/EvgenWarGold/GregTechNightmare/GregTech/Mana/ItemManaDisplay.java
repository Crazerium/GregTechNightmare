package com.EvgenWarGold.GregTechNightmare.GregTech.Mana;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import com.EvgenWarGold.GregTechNightmare.GregTech.Items.GTN_Items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vazkii.botania.common.block.mana.BlockPool;

/**
 * Display-only item that stands for mana: in NEI tooltips of the ME terminal ({@link AEManaStack#getItemStackForNEI})
 * and as the mana input of Botania machine recipes in NEI, which becomes mana when the recipe is moved into a pattern.
 * It has no recipe and is hidden from the creative tabs and NEI.
 */
public class ItemManaDisplay extends Item {

    private static final String MANA_TAG = "gtnMana";

    public ItemManaDisplay() {
        setUnlocalizedName("gtn.ManaDisplay");
        setMaxStackSize(64);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber() {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        // Botania registers the shared mana texture in the block atlas.
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return BlockPool.manaIcon;
    }

    /** Stack that stands for {@code amount} mana. */
    public static ItemStack create(long amount) {
        ItemStack stack = new ItemStack(GTN_Items.MANA_DISPLAY);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setLong(MANA_TAG, amount);
        stack.setTagCompound(tag);
        return stack;
    }

    /** Mana of a stack made by {@link #create}, otherwise its stack size. */
    public static long getAmount(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey(MANA_TAG)) return tag.getLong(MANA_TAG);
        return stack.stackSize;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return StatCollector.translateToLocal("gtn.mana");
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey(MANA_TAG)) {
            tooltip.add(
                StatCollector
                    .translateToLocalFormatted("GTN.ManaDisplay.tooltip.amount", formatNumber(getAmount(stack))));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("rawtypes")
    public void getSubItems(Item item, CreativeTabs tab, List list) {}
}
