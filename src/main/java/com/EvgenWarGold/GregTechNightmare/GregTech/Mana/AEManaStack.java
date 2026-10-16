package com.EvgenWarGold.GregTechNightmare.GregTech.Mana;

import static com.EvgenWarGold.GregTechNightmare.GregTech.Mana.AEManaStackType.MANA_STACK_TYPE;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.EvgenWarGold.GregTechNightmare.GregTech.Items.GTN_Items;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEStackType;
import appeng.api.storage.data.IAETagCompound;
import appeng.util.item.AEStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import vazkii.botania.common.block.mana.BlockPool;

/**
 * Botania mana in the ME network. Mana has no variants, so every stack is of the same type and only the amount
 * matters.
 */
public class AEManaStack extends AEStack<AEManaStack> {

    /** Marks an NBT tag as a mana stack. */
    private static final String NBT_MARKER = "GTNMana";

    public AEManaStack() {
        this.setStackSize(1);
    }

    public AEManaStack(long amount) {
        this.setStackSize(amount);
    }

    private AEManaStack(AEManaStack stack) {
        this.setStackSize(stack.getStackSize());
        this.setCraftable(stack.isCraftable());
        this.setCountRequestable(stack.getCountRequestable());
        this.setCountRequestableCrafts(stack.getCountRequestableCrafts());
        this.setUsedPercent(stack.getUsedPercent());
    }

    @Override
    public IAEStackType<AEManaStack> getStackType() {
        return MANA_STACK_TYPE;
    }

    @Override
    public void add(AEManaStack stack) {
        if (stack == null) return;
        this.incStackSize(stack.getStackSize());
        this.setCountRequestable(this.getCountRequestable() + stack.getCountRequestable());
        this.setCraftable(this.isCraftable() || stack.isCraftable());
        this.setCountRequestableCrafts(this.getCountRequestableCrafts() + stack.getCountRequestableCrafts());
    }

    @Override
    public AEManaStack empty() {
        AEManaStack empty = this.copy();
        empty.reset();
        return empty;
    }

    @Override
    public AEManaStack copy() {
        return new AEManaStack(this);
    }

    @Override
    public int hashCode() {
        return NBT_MARKER.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AEManaStack;
    }

    @Override
    public boolean isSameType(AEManaStack stack) {
        return stack != null;
    }

    @Override
    public boolean isSameType(Object stack) {
        return stack instanceof AEManaStack;
    }

    @Override
    public String getUnlocalizedName() {
        return "gtn.mana";
    }

    @Override
    public String getLocalizedName() {
        return StatCollector.translateToLocal(this.getUnlocalizedName());
    }

    @Override
    public String getDisplayName() {
        return this.getLocalizedName();
    }

    @Override
    public boolean hasTagCompound() {
        return false;
    }

    @Override
    public void setTagCompound(NBTTagCompound tag) {}

    @Override
    public IAETagCompound getTagCompound() {
        return null;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setBoolean(NBT_MARKER, true);
        tag.setLong("Cnt", this.getStackSize());
        tag.setLong("Req", this.getCountRequestable());
        tag.setBoolean("Craft", this.isCraftable());
        if (this.getCountRequestableCrafts() != 0L) tag.setLong("ReqMade", this.getCountRequestableCrafts());
        if (this.getUsedPercent() != 0) tag.setFloat("UsedPercent", this.getUsedPercent());
    }

    public static AEManaStack loadStackFromNBT(NBTTagCompound tag) {
        if (tag == null || !tag.getBoolean(NBT_MARKER)) return null;
        AEManaStack stack = new AEManaStack(tag.getLong("Cnt"));
        stack.setCountRequestable(tag.getLong("Req"));
        stack.setCraftable(tag.getBoolean("Craft"));
        stack.setCountRequestableCrafts(tag.getLong("ReqMade"));
        stack.setUsedPercent(tag.getFloat("UsedPercent"));
        return stack;
    }

    /** Mirror of {@link AEStack#writeToPacket}: mana has no identity bytes. */
    public static AEManaStack loadStackFromPacket(ByteBuf data) throws IOException {
        final byte mask = data.readByte();
        final byte stackType = (byte) ((mask & 0x0C) >> 2);
        final byte countReqType = (byte) ((mask & 0x30) >> 4);
        final boolean craftable = (mask & 0x40) > 0;

        final long stackSize = getPacketValue(stackType, data);
        final long countRequestable = getPacketValue(countReqType, data);

        final byte mask2 = data.readByte();
        final long countRequestableCrafts = getPacketValue((byte) (mask2 & 0x3), data);
        final long usedPercent = getPacketValue((byte) ((mask2 & 0xC) >> 2), data);

        AEManaStack stack = new AEManaStack(stackSize);
        stack.setCountRequestable(countRequestable);
        stack.setCraftable(craftable);
        stack.setCountRequestableCrafts(countRequestableCrafts);
        stack.setUsedPercent(usedPercent / 10000f);
        return stack;
    }

    @Override
    protected void writeIdentity(ByteBuf buffer) throws IOException {}

    @Override
    protected void readNBT(ByteBuf buffer) throws IOException {}

    @Override
    public String getModId() {
        return "Botania";
    }

    @Override
    public boolean fuzzyComparison(Object stack, FuzzyMode mode) {
        return this.isSameType(stack);
    }

    @Override
    public boolean isItem() {
        return false;
    }

    @Override
    public boolean isFluid() {
        return false;
    }

    @Override
    public StorageChannel getChannel() {
        return null;
    }

    @Override
    public int getAmountPerUnit() {
        return MANA_STACK_TYPE.getAmountPerUnit();
    }

    /** Shown by NEI as the tooltip of the stack in the ME terminal. */
    @Override
    public ItemStack getItemStackForNEI() {
        return new ItemStack(
            GTN_Items.MANA_DISPLAY,
            (int) Math.max(1, Math.min(this.getStackSize(), Integer.MAX_VALUE)));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawInGui(Minecraft mc, int x, int y) {
        IIcon icon = BlockPool.manaIcon;
        if (icon == null) return;
        mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1, 1, 1, 1);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 16, y, 0, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(x, y, 0, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(x, y + 16, 0, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(x + 16, y + 16, 0, icon.getMaxU(), icon.getMaxV());
        tessellator.draw();
        GL11.glPopAttrib();
    }

    @Override
    public void drawOnBlockFace(World world) {}
}
