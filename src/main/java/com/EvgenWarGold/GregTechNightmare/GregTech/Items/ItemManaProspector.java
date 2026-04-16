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
import detrav.utils.BartWorksHelper;
import detrav.utils.GTppHelper;
import gregtech.api.util.GTLanguageManager;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.common.UndergroundOil;
import gregtech.common.blocks.BlockOresAbstract;
import gregtech.common.blocks.TileEntityOres;
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
        final String small_ore_keyword = StatCollector.translateToLocal("detrav.scanner.small_ore.keyword");

        for (Chunk c : chunks) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    final int ySize = c.getHeightValue(x, z);
                    for (int y = 1; y < ySize; y++) {
                        final Block tBlock = c.getBlock(x, y, z);
                        short tMetaID = (short) c.getBlockMetadata(x, y, z);

                        if (tBlock instanceof BlockOresAbstract) {
                            TileEntity tTileEntity = c.getTileEntityUnsafe(x, y, z);
                            if ((tTileEntity instanceof TileEntityOres) && ((TileEntityOres) tTileEntity).mNatural) {
                                tMetaID = ((TileEntityOres) tTileEntity).getMetaData();
                                try {
                                    String name = GTLanguageManager
                                        .getTranslation(tBlock.getUnlocalizedName() + "." + tMetaID + ".name");
                                    if (!includeSmallOres && name.startsWith(small_ore_keyword)) continue;
                                    packet.addBlock(c.xPosition * 16 + x, y, c.zPosition * 16 + z, tMetaID);
                                } catch (Exception e) {
                                    String name = tBlock.getUnlocalizedName() + ".";
                                    if (!includeSmallOres && name.contains(".small.")) continue;
                                    packet.addBlock(c.xPosition * 16 + x, y, c.zPosition * 16 + z, tMetaID);
                                }
                            }
                        }

                        else if (GTppHelper.isGTppBlock(tBlock)) {
                            packet.addBlock(
                                c.xPosition * 16 + x,
                                y,
                                c.zPosition * 16 + z,
                                GTppHelper.getMetaFromBlock(tBlock));
                        }

                        else if (BartWorksHelper.isOre(tBlock)) {
                            if (!includeSmallOres && BartWorksHelper.isSmallOre(tBlock)) continue;
                            packet.addBlock(
                                c.xPosition * 16 + x,
                                y,
                                c.zPosition * 16 + z,
                                BartWorksHelper.getMetaFromBlock(c, x, y, z, tBlock));
                        }

                        else if (includeSmallOres) {
                            var tAssociation = GTOreDictUnificator.getAssociation(new ItemStack(tBlock, 1, tMetaID));
                            if (tAssociation != null && tAssociation.mPrefix.toString()
                                .startsWith("ore")) {
                                packet.addBlock(
                                    c.xPosition * 16 + x,
                                    y,
                                    c.zPosition * 16 + z,
                                    (short) tAssociation.mMaterial.mMaterial.mMetaItemSubID);
                            }
                        }
                    }
                }
            }
        }
    }

    private void scanOil(World world, List<Chunk> chunks, ProspectingPacket packet) {
        for (Chunk c : chunks) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    FluidStack fStack = UndergroundOil
                        .undergroundOil(world.getChunkFromBlockCoords(c.xPosition * 16 + x, c.zPosition * 16 + z), -1);
                    if (fStack != null && fStack.amount > 0) {
                        packet.addBlock(c.xPosition * 16 + x, 1, c.zPosition * 16 + z, (short) fStack.getFluidID());
                        packet.addBlock(c.xPosition * 16 + x, 2, c.zPosition * 16 + z, (short) fStack.amount);
                    }
                }
            }
        }
    }

    private void scanPollution(World world, List<Chunk> chunks, ProspectingPacket packet) {
        for (Chunk c : chunks) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int pollution = Pollution
                        .getPollution(world.getChunkFromBlockCoords(c.xPosition * 16 + x, c.zPosition * 16 + z));
                    float pollutionValue = (float) pollution;
                    pollutionValue /= 2000000;
                    pollutionValue *= -0xFF;
                    if (pollutionValue > 0xFF) pollutionValue = 0xFF;
                    pollutionValue = 0xFF - pollutionValue;

                    packet.addBlock(c.xPosition * 16 + x, 1, c.zPosition * 16 + z, (short) pollutionValue);
                }
            }
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
