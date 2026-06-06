package com.EvgenWarGold.GregTechNightmare.GregTech.Items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidStack;

import detrav.net.DetravNetwork;
import detrav.net.ProspectingPacket;
import gregtech.common.UndergroundOil;
import gregtech.common.ores.OreInfo;
import gregtech.common.ores.OreManager;
import gregtech.common.pollution.Pollution;
import vazkii.botania.api.mana.IManaGivingItem;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.common.core.helper.ItemNBTHelper;

public class ItemManaProspector extends Item implements IManaUsingItem, IManaItem {

    private static final int BASE_RADIUS = 3;
    private static final int RADIUS_PER_TIER = 1;
    private static final String TAG_MANA = "mana";
    private static final int MAX_MANA = Integer.MAX_VALUE;
    private static final int MANA_COST = 1_000_000 / 100;

    public static final int[] LEVELS = new int[] { 0, 100_000, 1_000_000, 10_000_000, 100_000_000, 1_000_000_000 };

    public ItemManaProspector() {
        super();
        setMaxStackSize(1);
        setUnlocalizedName("ManaProspector");
    }

    public static void setMana(ItemStack stack, int mana) {
        ItemNBTHelper.setInt(stack, TAG_MANA, mana);
    }

    public static int getMana_(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, TAG_MANA, 0);
    }

    public static int getLevel(ItemStack stack) {
        int mana = getMana_(stack);
        for (int i = LEVELS.length - 1; i > 0; i--) if (mana >= LEVELS[i]) return i;

        return 0;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            if (player.isSneaking()) {
                int currentMode = getMode(stack);
                currentMode = (currentMode + 1) % 4;
                setMode(stack, currentMode);
                player.addChatMessage(
                    new ChatComponentText(StatCollector.translateToLocal("detrav.scanner.mode." + currentMode)));
            } else {
                int currentMana = getMana(stack);

                if (currentMana >= MANA_COST) {
                    addMana(stack, -MANA_COST);
                    scanWithDetravNetwork(world, player, stack);
                } else {
                    player.addChatMessage(
                        new ChatComponentText(
                            StatCollector.translateToLocal("GTN.Tooltip.item.ManaProspector.error_scan")));
                }
            }
        }
        return stack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        int radius = getCurrentRadius(stack);

        tooltip.add(StatCollector.translateToLocal("GTN.Tooltip.item.ManaProspector.0"));
        tooltip.add(StatCollector.translateToLocalFormatted("GTN.Tooltip.item.ManaProspector.1", radius));
        tooltip.add(StatCollector.translateToLocal("GTN.Tooltip.item.ManaProspector.2"));
        tooltip.add(StatCollector.translateToLocal("GTN.Tooltip.item.ManaProspector.3"));
        tooltip.add(StatCollector.translateToLocal("GTN.Tooltip.item.ManaProspector.4"));
        tooltip.add(StatCollector.translateToLocal("GTN.Tooltip.item.ManaProspector.5"));
    }

    private int getMode(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound()
            .hasKey("ScanMode")) {
            return stack.getTagCompound()
                .getInteger("ScanMode");
        }
        return 0;
    }

    private void setMode(ItemStack stack, int mode) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
        }
        stack.getTagCompound()
            .setInteger("ScanMode", mode);
    }

    private void scanWithDetravNetwork(World world, EntityPlayer player, ItemStack stack) {
        int mode = getMode(stack);

        final int cX = ((int) player.posX) >> 4;
        final int cZ = ((int) player.posZ) >> 4;
        final List<Chunk> chunks = new ArrayList<>();
        int currentRadius = getCurrentRadius(stack);

        for (int i = -currentRadius; i <= currentRadius; i++) {
            for (int j = -currentRadius; j <= currentRadius; j++) {
                if (i != -currentRadius && i != currentRadius && j != -currentRadius && j != currentRadius) {
                    chunks.add(world.getChunkFromChunkCoords(cX + i, cZ + j));
                }
            }
        }

        final ProspectingPacket packet = new ProspectingPacket(
            cX,
            cZ,
            (int) player.posX,
            (int) player.posZ,
            currentRadius - 1,
            mode);

        switch (mode) {
            case 0:
                scanOres(chunks, packet, false);
                break;
            case 1:
                scanOres(chunks, packet, true);
                break;
            case 2:
                scanOil(world, chunks, packet);
                break;
            case 3:
                scanPollution(world, chunks, packet);
                break;
        }

        DetravNetwork.INSTANCE.sendToPlayer(packet, (EntityPlayerMP) player);

        player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("detrav.scanner.success")));
    }

    private void scanOres(List<Chunk> chunks, ProspectingPacket packet, boolean includeSmallOres) {
        for (Chunk c : chunks) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    final int height = c.getHeightValue(x, z);

                    for (int y = 1; y < height; y++) {
                        Block block = c.getBlock(x, y, z);
                        int meta = c.getBlockMetadata(x, y, z);

                        OreInfo<?> info = OreManager.getOreInfo(block, meta);
                        if (info == null || !info.isNatural) continue;
                        if (!includeSmallOres && info.isSmall) continue;

                        packet.addBlock(c.xPosition * 16 + x, y, c.zPosition * 16 + z, block, meta);
                    }
                }
            }
        }
    }

    private void scanOil(World world, List<Chunk> chunks, ProspectingPacket packet) {
        for (Chunk c : chunks) {
            FluidStack fStack = UndergroundOil.undergroundOil(c, -1);
            if (fStack != null && fStack.amount > 0) {
                packet.addFluid(c.xPosition, c.zPosition, fStack);
            }
        }
    }

    private void scanPollution(World world, List<Chunk> chunks, ProspectingPacket packet) {
        for (Chunk c : chunks) {
            int pollution = Pollution.getPollution(c);
            packet.addPollution(c.xPosition, c.zPosition, pollution);
        }
    }

    private int getCurrentRadius(ItemStack stack) {
        return BASE_RADIUS + (getLevel(stack) * RADIUS_PER_TIER);
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean usesMana(ItemStack stack) {
        return true;
    }

    @Override
    public int getMana(ItemStack stack) {
        return getMana_(stack);
    }

    @Override
    public int getMaxMana(ItemStack stack) {
        return MAX_MANA;
    }

    @Override
    public void addMana(ItemStack stack, int mana) {
        setMana(stack, Math.min(getMana(stack) + mana, MAX_MANA));
    }

    @Override
    public boolean canReceiveManaFromPool(ItemStack stack, TileEntity pool) {
        return true;
    }

    @Override
    public boolean canReceiveManaFromItem(ItemStack stack, ItemStack otherStack) {
        return !(otherStack.getItem() instanceof IManaGivingItem);
    }

    @Override
    public boolean canExportManaToPool(ItemStack stack, TileEntity pool) {
        return false;
    }

    @Override
    public boolean canExportManaToItem(ItemStack stack, ItemStack otherStack) {
        return false;
    }

    @Override
    public boolean isNoExport(ItemStack stack) {
        return true;
    }
}
