package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import static com.gtnewhorizon.structurelib.StructureLib.LOGGER;
import static com.gtnewhorizon.structurelib.StructureLib.PANIC_MODE;

import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.gtnewhorizon.structurelib.StructureLibAPI;
import com.gtnewhorizon.structurelib.structure.AutoPlaceEnvironment;
import com.gtnewhorizon.structurelib.structure.IItemSource;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnewhorizon.structurelib.util.ItemStackPredicate;

import javax.annotation.Nullable;

@Mixin(value = StructureUtility.class, remap = false)
public class FixMultiblockBuilderMixins<T> {

    /**
     * @author EvgenWarGold
     * @reason Add custom survivalPlaceBlock implementation to ofBlock elements
     */
    @Overwrite(remap = false)
    public static <T> IStructureElement<T> ofBlock(Block block, int meta, Block defaultBlock, int defaultMeta) {
        if (block == null || defaultBlock == null) {
            throw new IllegalArgumentException();
        }

        if (block == Blocks.air) {
            if (PANIC_MODE) {
                throw new IllegalArgumentException("ofBlock() does not accept air. use isAir() instead");
            } else {
                LOGGER.warn("ofBlock() does not accept air. use isAir() instead");
                return isAir();
            }
        }

        return new EnhancedStructureElement<>(block, meta, defaultBlock, defaultMeta);
    }

    @Shadow(remap = false)
    public static <T> IStructureElement<T> isAir() {
        throw new AssertionError("Shadow method");
    }

    static class EnhancedStructureElement<T> implements IStructureElement<T> {

        private final Block block;
        private final int meta;
        private final Block defaultBlock;
        private final int defaultMeta;

        public EnhancedStructureElement(Block block, int meta, Block defaultBlock, int defaultMeta) {
            this.block = block;
            this.meta = meta;
            this.defaultBlock = defaultBlock;
            this.defaultMeta = defaultMeta;
        }

        @Override
        public boolean check(T t, World world, int x, int y, int z) {
            Block worldBlock = world.getBlock(x, y, z);
            return block == worldBlock && meta == worldBlock.getDamageValue(world, x, y, z);
        }

        @Override
        public boolean couldBeValid(T t, World world, int x, int y, int z, ItemStack trigger) {
            return check(t, world, x, y, z);
        }

        @Override
        public boolean placeBlock(T t, World world, int x, int y, int z, ItemStack trigger) {
            world.setBlock(x, y, z, defaultBlock, defaultMeta, 2);
            return true;
        }

        @Override
        public boolean spawnHint(T t, World world, int x, int y, int z, ItemStack trigger) {
            StructureLibAPI.hintParticle(world, x, y, z, defaultBlock, defaultMeta);
            return true;
        }

        @Override
        public BlocksToPlace getBlocksToPlace(T t, World world, int x, int y, int z, ItemStack trigger,
            AutoPlaceEnvironment env) {
            return BlocksToPlace.create(block, meta);
        }

        @Override
        public PlaceResult survivalPlaceBlock(T t, World world, int x, int y, int z, ItemStack trigger,
            AutoPlaceEnvironment env) {
            BlocksToPlace blocksToPlace = getBlocksToPlace(t, world, x, y, z, trigger, env);
            IItemSource source = env.getSource();
            EntityPlayer actor = env.getActor();
            Consumer<IChatComponent> chatter = env.getChatter();

            if (check(t, world, x, y, z)) {
                return PlaceResult.SKIP;
            }

            if (blocksToPlace == null) {
                return PlaceResult.REJECT;
            }

            if (blocksToPlace.getStacks() == null) {
                ItemStack taken = source.takeOne(blocksToPlace.getPredicate(), true);
                return StructureUtility.survivalPlaceBlock(
                    taken,
                    ItemStackPredicate.NBTMode.EXACT,
                    taken.stackTagCompound,
                    false,
                    world,
                    x,
                    y,
                    z,
                    source,
                    actor,
                    chatter);
            }

            for (ItemStack stack : blocksToPlace.getStacks()) {
                if (!source.takeOne(stack, true)) {
                    IChatComponent name = new ChatComponentText(stack.getDisplayName());
                    name.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.YELLOW));
                    chatter.accept(new ChatComponentTranslation("GTN.StructureLib.text.missing_block", name));
                    continue;
                }
                return StructureUtility.survivalPlaceBlock(
                    stack,
                    ItemStackPredicate.NBTMode.EXACT,
                    stack.stackTagCompound,
                    false,
                    world,
                    x,
                    y,
                    z,
                    source,
                    actor,
                    chatter);
            }

            return PlaceResult.REJECT;
        }
    }

    /**
     * @author EvgenWarGold
     * @reason Add custom survivalPlaceBlock implementation to ofBlock elements
     */
    @Overwrite
    public static IStructureElement.PlaceResult survivalPlaceBlock(ItemStack stack, ItemStackPredicate.NBTMode nbtMode, NBTTagCompound tag,
                                                                   boolean assumeStackPresent, World world, int x, int y, int z, IItemSource s, EntityPlayer actor,
                                                                   @Nullable Consumer<IChatComponent> chatter) {
        if (stack == null) throw new NullPointerException();
        if (stack.stackSize != 1) throw new IllegalArgumentException();
        if (!(stack.getItem() instanceof ItemBlock)) throw new IllegalArgumentException();
        if (!StructureLibAPI.isBlockTriviallyReplaceable(world, x, y, z, actor)) {
            if (chatter == null) return IStructureElement.PlaceResult.REJECT;
            chatter.accept(new ChatComponentTranslation("GTN.StructureLib.text.invalid_placement"));
            return IStructureElement.PlaceResult.REJECT;
        }
        if (!assumeStackPresent && !s.takeOne(stack, true)) {
            if (chatter != null) chatter.accept(
                new ChatComponentTranslation("structurelib.autoplace.error.no_item_stack", stack.func_151000_E()));
            return IStructureElement.PlaceResult.REJECT;
        }
        if (!stack.copy().tryPlaceItemIntoWorld(actor, world, x, y, z, ForgeDirection.UP.ordinal(), 0.5f, 0.5f, 0.5f)) {
            if (chatter == null) return IStructureElement.PlaceResult.REJECT;
            chatter.accept(new ChatComponentTranslation("GTN.StructureLib.text.invalid_placement"));
            return IStructureElement.PlaceResult.REJECT;
        }
        if (!s.takeOne(stack, false))
            // this is bad! probably an exploit somehow. Let's nullify the block we just placed instead
            world.setBlockToAir(x, y, z);
        return IStructureElement.PlaceResult.ACCEPT;
    }
}
