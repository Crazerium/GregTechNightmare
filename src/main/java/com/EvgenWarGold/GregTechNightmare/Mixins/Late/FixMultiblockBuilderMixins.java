package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import static com.gtnewhorizon.structurelib.StructureLib.LOGGER;
import static com.gtnewhorizon.structurelib.StructureLib.PANIC_MODE;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizon.structurelib.StructureLib;
import com.gtnewhorizon.structurelib.StructureLibAPI;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.AutoPlaceEnvironment;
import com.gtnewhorizon.structurelib.structure.IItemSource;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnewhorizon.structurelib.util.ItemStackPredicate;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

@Mixin(value = StructureUtility.class, remap = false)
public class FixMultiblockBuilderMixins<T> {

    private static Method VISIT;
    private static Method BLOCK_NOT_LOADED;

    static {
        try {
            Class<?> walker = Class.forName("com.gtnewhorizon.structurelib.structure.IStructureWalker");

            VISIT = walker.getDeclaredMethod(
                "visit",
                IStructureElement.class,
                World.class,
                int.class,
                int.class,
                int.class,
                int.class,
                int.class,
                int.class);

            BLOCK_NOT_LOADED = walker.getDeclaredMethod(
                "blockNotLoaded",
                IStructureElement.class,
                World.class,
                int.class,
                int.class,
                int.class,
                int.class,
                int.class,
                int.class);

            VISIT.setAccessible(true);
            BLOCK_NOT_LOADED.setAccessible(true);

        } catch (Exception e) {
            throw new RuntimeException("Failed to init walker reflection", e);
        }
    }

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
    public static IStructureElement.PlaceResult survivalPlaceBlock(ItemStack stack, ItemStackPredicate.NBTMode nbtMode,
        NBTTagCompound tag, boolean assumeStackPresent, World world, int x, int y, int z, IItemSource s,
        EntityPlayer actor, @Nullable Consumer<IChatComponent> chatter) {
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
        if (!stack.copy()
            .tryPlaceItemIntoWorld(actor, world, x, y, z, ForgeDirection.UP.ordinal(), 0.5f, 0.5f, 0.5f)) {
            if (chatter == null) return IStructureElement.PlaceResult.REJECT;
            chatter.accept(new ChatComponentTranslation("GTN.StructureLib.text.invalid_placement"));
            return IStructureElement.PlaceResult.REJECT;
        }
        if (!s.takeOne(stack, false))
            // this is bad! probably an exploit somehow. Let's nullify the block we just placed instead
            world.setBlockToAir(x, y, z);
        return IStructureElement.PlaceResult.ACCEPT;
    }

    private static String describeElement(IStructureElement<?> element) {
        // String simpleName = element.getClass().getSimpleName();
        // return simpleName.isEmpty() ? element.getClass().getName() : simpleName;
        return element.getClass()
            .getName();
    }

    @Inject(method = "iterateV2", at = @At("HEAD"), cancellable = true)
    private static <T> void iterateV2_redirect(IStructureElement<T>[] elements, World world,
        ExtendedFacing extendedFacing, int basePositionX, int basePositionY, int basePositionZ, int basePositionA,
        int basePositionB, int basePositionC, @Coerce Object predicate, String iterateType,
        CallbackInfoReturnable<Boolean> cir) {
        boolean result = iterateV2_custom(
            elements,
            world,
            extendedFacing,
            basePositionX,
            basePositionY,
            basePositionZ,
            basePositionA,
            basePositionB,
            basePositionC,
            predicate,
            iterateType);
        cir.setReturnValue(result);
        cir.cancel();
    }

    // private static <T> boolean iterateV2_custom(
    // IStructureElement<T>[] elements,
    // World world,
    // ExtendedFacing extendedFacing,
    // int basePositionX, int basePositionY, int basePositionZ,
    // int basePositionA, int basePositionB, int basePositionC,
    // Object predicate,
    // String iterateType
    // ) {
    // // change base position to base offset
    // basePositionA = -basePositionA;
    // basePositionB = -basePositionB;
    // basePositionC = -basePositionC;
    //
    // int[] abc = new int[]{basePositionA, basePositionB, basePositionC};
    // int[] xyz = new int[3];
    //
    // for (int elementIndex = 0; elementIndex < elements.length; elementIndex++) {
    // IStructureElement<T> element = elements[elementIndex];
    // if (element.isNavigating()) {
    // abc[0] = (element.resetA() ? basePositionA : abc[0]) + element.getStepA();
    // abc[1] = (element.resetB() ? basePositionB : abc[1]) + element.getStepB();
    // abc[2] = (element.resetC() ? basePositionC : abc[2]) + element.getStepC();
    // StructureLib.LOGGER.info(
    // "Multi [{}, {}, {}] {} nav #{} {} -> {}",
    // basePositionX,
    // basePositionY,
    // basePositionZ,
    // iterateType,
    // elementIndex,
    // describeElement(element),
    // Arrays.toString(abc));
    // } else {
    // extendedFacing.getWorldOffset(abc, xyz);
    // xyz[0] += basePositionX;
    // xyz[1] += basePositionY;
    // xyz[2] += basePositionZ;
    //
    // StructureLib.LOGGER.info(
    // "Multi [{}, {}, {}] {} step #{} {} @ {} {}",
    // basePositionX,
    // basePositionY,
    // basePositionZ,
    // iterateType,
    // elementIndex,
    // describeElement(element),
    // Arrays.toString(xyz),
    // Arrays.toString(abc));
    //
    // if (world.blockExists(xyz[0], xyz[1], xyz[2])) {
    // if (StructureLibAPI.isInstrumentEnabled()) {
    // StructureEvent.StructureElementVisitedEvent.fireEvent(
    // world,
    // xyz[0],
    // xyz[1],
    // xyz[2],
    // abc[0] - basePositionA,
    // abc[1] - basePositionB,
    // abc[2] - basePositionC,
    // element);
    // }
    // try {
    // boolean result = (boolean) VISIT.invoke(predicate, element, world, xyz[0], xyz[1], xyz[2], abc[0], abc[1],
    // abc[2]);
    // if (!result) {
    // StructureLib.LOGGER.info(
    // "Multi [{}, {}, {}] {} stop #{} {} @ {} {}",
    // basePositionX,
    // basePositionY,
    // basePositionZ,
    // iterateType,
    // elementIndex,
    // describeElement(element),
    // Arrays.toString(xyz),
    // Arrays.toString(abc));
    // return false;
    // }
    // } catch (Exception e) {
    // throw new RuntimeException(e);
    // }
    // } else {
    // StructureLib.LOGGER.info(
    // "Multi [{}, {}, {}] {} !blockExists #{} {} @ {} {}",
    // basePositionX,
    // basePositionY,
    // basePositionZ,
    // iterateType,
    // elementIndex,
    // describeElement(element),
    // Arrays.toString(xyz),
    // Arrays.toString(abc));
    // try {
    // boolean result = (boolean) BLOCK_NOT_LOADED.invoke(predicate, element, world, xyz[0], xyz[1], xyz[2], abc[0],
    // abc[1], abc[2]);
    // if (!result) {
    // return false;
    // }
    // } catch (Exception e) {
    // throw new RuntimeException(e);
    // }
    // }
    // abc[0] += 1;
    // }
    // }
    // return true;
    // }

    private static <T> boolean iterateV2_custom(IStructureElement<T>[] elements, World world,
        ExtendedFacing extendedFacing, int basePositionX, int basePositionY, int basePositionZ, int basePositionA,
        int basePositionB, int basePositionC, Object predicate, String iterateType) {
        StructureLib.LOGGER.info(
            "=== START iterate={} baseXYZ=[{}, {}, {}] baseABC(orig)=[{}, {}, {}] facing={} ===",
            iterateType,
            basePositionX,
            basePositionY,
            basePositionZ,
            basePositionA,
            basePositionB,
            basePositionC,
            extendedFacing);

        StructureLib.LOGGER.info("=== CALL STACK TRACE ===");
        StackTraceElement[] stackTrace = Thread.currentThread()
            .getStackTrace();
        for (int i = 2; i < Math.min(stackTrace.length, 15); i++) {
            StructureLib.LOGGER.info("  at {}", stackTrace[i].toString());
        }
        StructureLib.LOGGER.info("=== END STACK TRACE ===");

        basePositionA = -basePositionA;
        basePositionB = -basePositionB;
        basePositionC = -basePositionC;

        StructureLib.LOGGER.info("INVERTED baseABC=[{}, {}, {}]", basePositionA, basePositionB, basePositionC);

        int[] abc = new int[] { basePositionA, basePositionB, basePositionC };
        int[] xyz = new int[3];

        for (int elementIndex = 0; elementIndex < elements.length; elementIndex++) {
            IStructureElement<T> element = elements[elementIndex];

            StructureLib.LOGGER.info(
                "ELEMENT #{} {} navigating={}",
                elementIndex,
                element.getClass()
                    .getName(),
                element.isNavigating());

            if (element.isNavigating()) {
                int oldA = abc[0], oldB = abc[1], oldC = abc[2];

                abc[0] = (element.resetA() ? basePositionA : abc[0]) + element.getStepA();
                abc[1] = (element.resetB() ? basePositionB : abc[1]) + element.getStepB();
                abc[2] = (element.resetC() ? basePositionC : abc[2]) + element.getStepC();

                StructureLib.LOGGER.info(
                    "NAV step #{} abc {} -> {} (stepA={} stepB={} stepC={} resetA={} resetB={} resetC={})",
                    elementIndex,
                    Arrays.toString(new int[] { oldA, oldB, oldC }),
                    Arrays.toString(abc),
                    element.getStepA(),
                    element.getStepB(),
                    element.getStepC(),
                    element.resetA(),
                    element.resetB(),
                    element.resetC());
                continue;
            }

            StructureLib.LOGGER.info("OFFSET input abc={} facing={}", Arrays.toString(abc), extendedFacing);
            extendedFacing.getWorldOffset(abc, xyz);
            StructureLib.LOGGER.info("OFFSET raw xyz={}", Arrays.toString(xyz));

            xyz[0] += basePositionX;
            xyz[1] += basePositionY;
            xyz[2] += basePositionZ;

            StructureLib.LOGGER
                .info("STEP #{} worldXYZ={} abc={}", elementIndex, Arrays.toString(xyz), Arrays.toString(abc));

            boolean exists = world.blockExists(xyz[0], xyz[1], xyz[2]);
            StructureLib.LOGGER.info("blockExists={} at {}", exists, Arrays.toString(xyz));

            if (exists) {
                Block block = world.getBlock(xyz[0], xyz[1], xyz[2]);
                int meta = world.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
                TileEntity te = world.getTileEntity(xyz[0], xyz[1], xyz[2]);
                String teName = "null";

                if (te instanceof IGregTechTileEntity gte) {
                    teName = gte.getMetaTileEntity()
                        .getLocalName();
                } else if (te != null) {
                    teName = te.getClass()
                        .getName();
                }

                StructureLib.LOGGER.info(
                    "WORLD BLOCK at {} -> {} meta={} tile={}",
                    Arrays.toString(xyz),
                    block != null ? block.getUnlocalizedName() : "null",
                    meta,
                    teName);

                try {
                    boolean result = (boolean) VISIT
                        .invoke(predicate, element, world, xyz[0], xyz[1], xyz[2], abc[0], abc[1], abc[2]);

                    StructureLib.LOGGER.info(
                        "VISIT result={} element #{} at xyz={} abc={} relABC={}",
                        result,
                        elementIndex,
                        Arrays.toString(xyz),
                        Arrays.toString(abc),
                        Arrays.toString(
                            new int[] { abc[0] - basePositionA, abc[1] - basePositionB, abc[2] - basePositionC }));

                    if (!result) {
                        StructureLib.LOGGER.error("STOP (predicate=false) at element #{}", elementIndex);
                        return false;
                    }

                } catch (Exception e) {
                    StructureLib.LOGGER.error("EXCEPTION in VISIT at element #" + elementIndex, e);
                    throw new RuntimeException(e);
                }

            } else {
                StructureLib.LOGGER.warn("BLOCK NOT LOADED at {}", Arrays.toString(xyz));

                try {
                    boolean result = (boolean) BLOCK_NOT_LOADED
                        .invoke(predicate, element, world, xyz[0], xyz[1], xyz[2], abc[0], abc[1], abc[2]);

                    if (!result) {
                        return false;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            int oldA = abc[0], oldB = abc[1], oldC = abc[2];
            abc[0] += 1;

            StructureLib.LOGGER.info(
                "STEP FORWARD abc {} -> {}",
                Arrays.toString(new int[] { oldA, oldB, oldC }),
                Arrays.toString(abc));
        }

        StructureLib.LOGGER.info("=== END iterate={} SUCCESS ===", iterateType);
        return true;
    }
}
