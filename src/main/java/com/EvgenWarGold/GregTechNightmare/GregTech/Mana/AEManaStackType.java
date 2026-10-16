package com.EvgenWarGold.GregTechNightmare.GregTech.Mana;

import java.io.IOException;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import appeng.api.storage.data.IAEStackType;
import appeng.api.storage.data.IItemList;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectLongImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectLongPair;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.common.block.mana.BlockPool;

/**
 * AE2 stack type for Botania mana. Mana tablets and other {@link IManaItem}s act as containers, so they can be filled
 * and drained through the terminal.
 */
public class AEManaStackType implements IAEStackType<AEManaStack> {

    public static final AEManaStackType MANA_STACK_TYPE = new AEManaStackType();
    public static final String MANA_STACK_ID = "gtn_mana";
    /** Same density as Applied Botanics: one byte of a cell holds 500 mana. */
    public static final int MANA_PER_BYTE = 500;

    private final AEManaStack testStack = new AEManaStack();

    @Override
    public String getId() {
        return MANA_STACK_ID;
    }

    @Override
    public String getDisplayName() {
        return StatCollector.translateToLocal("gtn.mana");
    }

    @Override
    public String getDisplayUnit() {
        return "";
    }

    @Override
    public EnumChatFormatting getColorDefinition() {
        return EnumChatFormatting.AQUA;
    }

    @Override
    public AEManaStack loadStackFromNBT(NBTTagCompound tag) {
        return AEManaStack.loadStackFromNBT(tag);
    }

    @Override
    public AEManaStack loadStackFromByte(ByteBuf buffer) throws IOException {
        return AEManaStack.loadStackFromPacket(buffer);
    }

    @Override
    public IItemList<AEManaStack> createList() {
        return new ManaList();
    }

    @Override
    public int getAmountPerUnit() {
        return 1;
    }

    @Override
    public boolean isContainerItemForType(ItemStack container) {
        return container != null && container.getItem() instanceof IManaItem;
    }

    @Override
    public AEManaStack getStackFromContainerItem(ItemStack container) {
        if (!(container.getItem() instanceof IManaItem item)) return null;
        int mana = item.getMana(container);
        return mana > 0 ? new AEManaStack(mana) : null;
    }

    @Override
    public AEManaStack convertStackFromItem(ItemStack itemStack) {
        return null;
    }

    @Override
    public long getContainerItemCapacity(ItemStack container, AEManaStack stack) {
        return container.getItem() instanceof IManaItem item ? item.getMaxMana(container) : 0;
    }

    @Override
    public ObjectLongPair<ItemStack> drainStackFromContainer(ItemStack container, AEManaStack stack) {
        if (!(container.getItem() instanceof IManaItem item) || item.isNoExport(container)) {
            return new ObjectLongImmutablePair<>(container, 0);
        }
        int drained = (int) Math.min(item.getMana(container), Math.min(stack.getStackSize(), Integer.MAX_VALUE));
        if (drained <= 0) return new ObjectLongImmutablePair<>(container, 0);
        ItemStack result = container.copy();
        item.addMana(result, -drained);
        return new ObjectLongImmutablePair<>(result, drained);
    }

    @Override
    public ItemStack clearFilledContainer(ItemStack container) {
        if (!(container.getItem() instanceof IManaItem item)) return null;
        ItemStack result = container.copy();
        item.addMana(result, -item.getMana(result));
        return result;
    }

    @Override
    public ObjectLongPair<ItemStack> fillContainer(ItemStack container, AEManaStack stack) {
        if (!(container.getItem() instanceof IManaItem item)) return new ObjectLongImmutablePair<>(null, 0);
        int space = item.getMaxMana(container) - item.getMana(container);
        int filled = (int) Math.min(space, Math.min(stack.getStackSize(), Integer.MAX_VALUE));
        if (filled <= 0) return new ObjectLongImmutablePair<>(null, 0);
        ItemStack result = container.copy();
        item.addMana(result, filled);
        return new ObjectLongImmutablePair<>(result, filled);
    }

    @Override
    public ResourceLocation getButtonTexture() {
        return TextureMap.locationBlocksTexture;
    }

    @Override
    public IIcon getButtonIcon() {
        return BlockPool.manaIcon;
    }

    @Override
    public AEManaStack getTestStack() {
        return this.testStack.copy();
    }

    @Override
    public int getAmountPerByte() {
        return MANA_PER_BYTE;
    }
}
