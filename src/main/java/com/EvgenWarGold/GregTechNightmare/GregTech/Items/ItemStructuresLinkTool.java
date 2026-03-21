package com.EvgenWarGold.GregTechNightmare.GregTech.Items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.CoordMultiBlock;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.GTN_NewMultiBlockBase;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

public class ItemStructuresLinkTool extends Item {

    public enum Status {
        FAIL,
        SUCCESS,
        EMPTY
    }

    public ItemStructuresLinkTool() {
        super();
        this.setUnlocalizedName("StructuresLinkTool");
    }

    @SubscribeEvent
    public void onClickBlock(PlayerInteractEvent event) {
        World world = event.world;
        EntityPlayer player = event.entityPlayer;
        ItemStack stack = player.getCurrentEquippedItem();

        if (world.isRemote) return;

        if (stack == null || !(stack.getItem() instanceof ItemStructuresLinkTool)) return;

        int x = event.x;
        int y = event.y;
        int z = event.z;

        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            if (saveLink(world, stack, x, y, z)) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.AQUA + "Link save"));
            }

            event.setCanceled(true);
            event.useBlock = PlayerInteractEvent.Result.DENY;
            event.useItem = PlayerInteractEvent.Result.DENY;
            return;
        }

        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            switch (useLink(world, stack, x, y, z)) {
                case FAIL -> player
                    .addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Link failed"));
                case EMPTY -> player
                    .addChatComponentMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Link empty"));
                case SUCCESS -> {
                    stack.setTagCompound(new NBTTagCompound());
                    player
                        .addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Link successfully"));
                }
            }

            event.setCanceled(true);
            event.useBlock = PlayerInteractEvent.Result.DENY;
            event.useItem = PlayerInteractEvent.Result.DENY;
            return;
        }

        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR && player.isSneaking()) {
            stack.setTagCompound(new NBTTagCompound());
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Link clear"));

            event.setCanceled(true);
            event.useBlock = PlayerInteractEvent.Result.DENY;
            event.useItem = PlayerInteractEvent.Result.DENY;
        }
    }

    private static boolean saveLink(World world, ItemStack stack, int x, int y, int z) {
        if (world == null || stack == null) return false;

        if (!world.blockExists(x, y, z)) return false;

        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof IGregTechTileEntity)) return false;

        NBTTagCompound tag = new NBTTagCompound();

        tag.setString("type", "LinkTool");
        tag.setInteger("dim", world.provider.dimensionId);
        tag.setInteger("x", x);
        tag.setInteger("y", y);
        tag.setInteger("z", z);

        stack.setTagCompound(tag);

        return true;
    }

    private static Status useLink(World world, ItemStack stack, int x, int y, int z) {
        if (world == null || stack == null) return Status.FAIL;

        NBTTagCompound tag = stack.getTagCompound();

        if (tag == null) return Status.EMPTY;

        if (!tag.hasKey("type") && !tag.getString("type")
            .equals("LinkTool")) return Status.EMPTY;

        int curDim = tag.getInteger("dim");
        int curX = tag.getInteger("x");
        int curY = tag.getInteger("y");
        int curZ = tag.getInteger("z");

        CoordMultiBlock coordMultiBlock = new CoordMultiBlock(curDim, curX, curY, curZ);

        if (!world.blockExists(x, y, z)) return Status.FAIL;

        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof IGregTechTileEntity gte)) return Status.FAIL;

        if (!(gte.getMetaTileEntity() instanceof GTN_NewMultiBlockBase<?>gtn)) return Status.FAIL;

        if (gtn.tryLink(coordMultiBlock)) {
            return Status.SUCCESS;
        }

        return Status.FAIL;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        NBTTagCompound tag = stack.getTagCompound();

        if (tag != null && tag.hasKey("type")) {
            if (tag.getString("type")
                .equals("LinkTool")) {
                list.add(EnumChatFormatting.AQUA + "Dim: " + EnumChatFormatting.YELLOW + tag.getInteger("dim"));
                list.add(EnumChatFormatting.AQUA + "X: " + EnumChatFormatting.YELLOW + tag.getInteger("x"));
                list.add(EnumChatFormatting.AQUA + "Y: " + EnumChatFormatting.YELLOW + tag.getInteger("y"));
                list.add(EnumChatFormatting.AQUA + "Z: " + EnumChatFormatting.YELLOW + tag.getInteger("z"));
            }
        }
    }
}
