package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.iterate;
import static gregtech.GTMod.GT_FML_LOGGER;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.common.blocks.BlockFrameBox;
import gregtech.common.tileentities.debug.MTEAdvDebugStructureWriter;
import gtPlusPlus.core.block.base.BasicBlock;
import gtPlusPlus.core.block.base.BlockBaseModular;
import gtPlusPlus.core.material.Material;
import gtPlusPlus.core.material.MaterialsAlloy;

@Mixin(value = MTEAdvDebugStructureWriter.class, remap = false)
public abstract class MTEAdvancedDebugStructureWriterMixins extends MetaTileEntity {

    private static final String NICE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz=|!@#$%&()[]{};:<>/?_,.*^'`";

    @Shadow(remap = false)
    private short[] numbers;

    @Shadow(remap = false)
    private boolean transpose;

    @Shadow(remap = false)
    private String[] result;

    public MTEAdvancedDebugStructureWriterMixins(int aID, String aBasicName, String aRegionalName, int aInvSlotCount) {
        super(aID, aBasicName, aRegionalName, aInvSlotCount);
    }

    public MTEAdvancedDebugStructureWriterMixins(String aName, int aInvSlotCount) {
        super(aName, aInvSlotCount);
    }

    /**
     * Overrides the structure printing method to add custom logic
     * with support for GTN_Casings and other mods
     *
     * @param aPlayer The player who invoked the method
     * @throws IllegalAccessException If there are errors accessing fields
     * @author EvgenWarGold
     * @reason Original method doesn't handle custom GTN_Casings and other mod blocks properly.
     *         This version adds support for GTN_Casings enumeration, FrameBox blocks from GT,
     *         and modular blocks from GT++, providing more accurate structure definitions
     *         for multiblock validation.
     */
    @Overwrite(remap = false)
    public void printStructure(EntityPlayer aPlayer) throws IllegalAccessException {
        IGregTechTileEntity aBaseMetaTileEntity = getBaseMetaTileEntity();
        String pseudoJavaCode = getPseudoJavaCode(
            aBaseMetaTileEntity.getWorld(),
            ExtendedFacing.of(aBaseMetaTileEntity.getFrontFacing()),
            aBaseMetaTileEntity.getXCoord(),
            aBaseMetaTileEntity.getYCoord(),
            aBaseMetaTileEntity.getZCoord(),
            numbers[0],
            numbers[1],
            numbers[2],
            te -> te.getClass()
                .getCanonicalName(),
            numbers[3],
            numbers[4],
            numbers[5],
            transpose);
        GT_FML_LOGGER.info(pseudoJavaCode);
        result = pseudoJavaCode.split("\\n");
        aPlayer.addChatMessage(new ChatComponentTranslation("Printed structure to console"));
    }

    private static String getPseudoJavaCode(World world, ExtendedFacing extendedFacing, int basePositionX,
        int basePositionY, int basePositionZ, int basePositionA, int basePositionB, int basePositionC,
        Function<? super TileEntity, String> tileEntityClassifier, int sizeA, int sizeB, int sizeC, boolean transpose)
        throws IllegalAccessException {

        Map<Block, Set<Integer>> blocks = new TreeMap<>(Comparator.comparing(Block::getUnlocalizedName));

        Set<Class<? extends TileEntity>> tiles = new HashSet<>();
        Set<String> specialTiles = new HashSet<>();

        boolean[] hasController = new boolean[1];
        int[] controllerPos = new int[3];

        Function<? super TileEntity, String> wrappedClassifier = tileEntity -> {
            if (tileEntity != null) {

                if (tileEntity.getClass()
                    .getName()
                    .contains("BaseMetaTileEntity")) {
                    try {
                        Object metaTileEntity = tileEntity.getClass()
                            .getMethod("getMetaTileEntity")
                            .invoke(tileEntity);

                        if (metaTileEntity != null) {
                            String mteClassName = metaTileEntity.getClass()
                                .getName();

                            if (mteClassName.contains("MTEAdvDebugStructureWriter") || metaTileEntity.getClass()
                                .getSimpleName()
                                .equals("MTEAdvDebugStructureWriter")) {
                                return "CONTROLLER";
                            }
                        }
                    } catch (Exception ignored) {}
                }

                String className = tileEntity.getClass()
                    .getName();

                if (className.contains("MTEAdvDebugStructureWriter") || tileEntity.getClass()
                    .getSimpleName()
                    .equals("MTEAdvDebugStructureWriter")) {
                    return "CONTROLLER";
                }
            }

            return tileEntityClassifier.apply(tileEntity);
        };

        iterate(
            world,
            extendedFacing,
            basePositionX,
            basePositionY,
            basePositionZ,
            basePositionA,
            basePositionB,
            basePositionC,
            sizeA,
            sizeB,
            sizeC,
            (w, x, y, z) -> {

                TileEntity tileEntity = w.getTileEntity(x, y, z);

                if (tileEntity == null) {

                    Block block = w.getBlock(x, y, z);

                    if (block != null && block != Blocks.air) {
                        blocks.compute(block, (b, set) -> {
                            if (set == null) {
                                set = new TreeSet<>();
                            }

                            set.add(block.getDamageValue(world, x, y, z));
                            return set;
                        });
                    }

                } else {

                    String classification = wrappedClassifier.apply(tileEntity);

                    if ("CONTROLLER".equals(classification)) {

                        hasController[0] = true;
                        controllerPos[0] = x;
                        controllerPos[1] = y;
                        controllerPos[2] = z;

                    } else if (classification == null) {

                        tiles.add(tileEntity.getClass());

                    } else {

                        specialTiles.add(classification);
                    }
                }
            });

        Map<String, Character> map = new HashMap<>();
        Map<String, String> casingMap = new HashMap<>();

        for (GTN_Casings casing : GTN_Casings.values()) {
            String key = casing.getBlock()
                .getUnlocalizedName() + '\0'
                + casing.getBlockMeta();

            casingMap.put(key, casing.name());
        }

        StringBuilder builder = new StringBuilder();

        int i = 0;
        char c;

        builder.append("\n")
            .append("\nBlocks:\n");

        for (Map.Entry<Block, Set<Integer>> entry : blocks.entrySet()) {

            Block block = entry.getKey();
            Set<Integer> metas = entry.getValue();

            for (Integer meta : metas) {

                c = NICE_CHARS.charAt(i++);

                if (i > NICE_CHARS.length()) {
                    return "Too complicated for nice chars";
                }

                String key = block.getUnlocalizedName() + '\0' + meta;
                map.put(key, c);

                String casingName = casingMap.get(key);

                if (casingName != null) {
                    builder.append(c)
                        .append(" -> ")
                        .append(".addElement('")
                        .append(c)
                        .append("', ")
                        .append("GTN_Casings.")
                        .append(casingName)
                        .append(".asElement()\n");

                } else if (block instanceof BlockFrameBox) {
                    String materialName = BlockFrameBox.getMaterial(meta)
                        .toString();

                    builder.append(c)
                        .append(" -> ")
                        .append(".addElement('")
                        .append(c)
                        .append("', ")
                        .append("ofFrame(Materials.")
                        .append(materialName)
                        .append(")\n");
                } else if (block instanceof BlockBaseModular blockBaseModular) {
                    if (BasicBlock.BlockTypes.FRAME == blockBaseModular.thisBlock) {
                        Field[] fields = MaterialsAlloy.class.getDeclaredFields();

                        for (Field field : fields) {
                            String materialName = blockBaseModular.getMaterialEx()
                                .getLocalizedName();
                            Material fieldMaterial = (Material) field.get(0);
                            String fieldMaterialName = fieldMaterial.getLocalizedName();

                            if (materialName.equals(fieldMaterialName)) {
                                builder.append(c)
                                    .append(" -> ")
                                    .append(".addElement('")
                                    .append(c)
                                    .append("', ")
                                    .append("GTN_StructureUtility.ofFrame(")
                                    .append("MaterialsAlloy.")
                                    .append(field.getName())
                                    .append(")\n");
                            }
                        }
                    }

                } else {
                    builder.append(c)
                        .append(" -> ")
                        .append(".addElement('")
                        .append(c)
                        .append("', ")
                        .append("ofBlock(\"")
                        .append(block.getUnlocalizedName())
                        .append("\", ")
                        .append(meta)
                        .append(")\n");
                }
            }
        }

        if (!tiles.isEmpty()) {
            builder.append("\nTiles:\n");

            for (Class<? extends TileEntity> tile : tiles) {

                c = NICE_CHARS.charAt(i++);

                if (i > NICE_CHARS.length()) {
                    return "Too complicated for nice chars";
                }

                map.put(tile.getCanonicalName(), c);

                builder.append(c)
                    .append(" -> ")
                    .append(".addElement('")
                    .append(c)
                    .append("', ")
                    .append("ofTileAdder(")
                    .append(tile)
                    .append(")\n");
            }
        }

        if (!specialTiles.isEmpty()) {
            builder.append("\nSpecial Tiles:\n");

            for (String tile : specialTiles) {

                c = NICE_CHARS.charAt(i++);

                if (i > NICE_CHARS.length()) {
                    return "Too complicated for nice chars";
                }

                map.put(tile, c);

                builder.append(c)
                    .append(" -> ")
                    .append(".addElement('")
                    .append(c)
                    .append("', ")
                    .append("ofSpecialTileAdder(")
                    .append(tile)
                    .append(")\n");
            }
        }

        builder.append("\nOFFSETS\n")
            .append("Horizontal: ")
            .append(basePositionA)
            .append('\n')
            .append("Vertical: ")
            .append(basePositionB)
            .append('\n')
            .append("Depth: ")
            .append(basePositionC)
            .append('\n');

        builder.append("\nMULTIBLOCK SIZE\n")
            .append("Width: ")
            .append(sizeA)
            .append('\n')
            .append("Height: ")
            .append(sizeB)
            .append('\n')
            .append("Length: ")
            .append(sizeC)
            .append('\n');

        if (transpose) {

            builder.append("\nTransposed Scan:\n")
                .append("new String[][]{\n")
                .append("    {\"");

            iterate(
                world,
                extendedFacing,
                basePositionX,
                basePositionY,
                basePositionZ,
                basePositionA,
                basePositionB,
                basePositionC,
                true,
                sizeA,
                sizeB,
                sizeC,
                (w, x, y, z) -> {

                    TileEntity tileEntity = w.getTileEntity(x, y, z);

                    if (tileEntity != null) {

                        String classification = wrappedClassifier.apply(tileEntity);

                        if ("CONTROLLER".equals(classification)) {
                            builder.append('~');
                            return;
                        }
                    }

                    if (tileEntity == null) {

                        Block block = w.getBlock(x, y, z);

                        if (block != null && block != Blocks.air) {

                            String key = block.getUnlocalizedName() + '\0' + block.getDamageValue(world, x, y, z);

                            Character ch = map.get(key);

                            builder.append(ch != null ? ch : '?');

                        } else {
                            builder.append(' ');
                        }

                    } else {

                        String classification = wrappedClassifier.apply(tileEntity);

                        if (classification == null) {
                            classification = tileEntity.getClass()
                                .getCanonicalName();
                        }

                        Character ch = map.get(classification);
                        builder.append(ch != null ? ch : '?');
                    }
                },
                () -> builder.append("\",\""),
                () -> {
                    builder.setLength(builder.length() - 2);
                    builder.append("},\n    {\"");
                });

            builder.setLength(builder.length() - 8);
            builder.append("\n}\n\n");

        } else {

            builder.append("\nNormal Scan:\n")
                .append("new String[][]{{\n")
                .append("    \"");

            iterate(
                world,
                extendedFacing,
                basePositionX,
                basePositionY,
                basePositionZ,
                basePositionA,
                basePositionB,
                basePositionC,
                false,
                sizeA,
                sizeB,
                sizeC,
                (w, x, y, z) -> {

                    TileEntity tileEntity = w.getTileEntity(x, y, z);

                    if (tileEntity != null) {

                        String classification = wrappedClassifier.apply(tileEntity);

                        if ("CONTROLLER".equals(classification)) {
                            builder.append('~');
                            return;
                        }
                    }

                    if (tileEntity == null) {

                        Block block = w.getBlock(x, y, z);

                        if (block != null && block != Blocks.air) {

                            String key = block.getUnlocalizedName() + '\0' + block.getDamageValue(world, x, y, z);

                            Character ch = map.get(key);

                            builder.append(ch != null ? ch : '?');

                        } else {
                            builder.append(' ');
                        }

                    } else {

                        String classification = wrappedClassifier.apply(tileEntity);

                        if (classification == null) {
                            classification = tileEntity.getClass()
                                .getCanonicalName();
                        }

                        Character ch = map.get(classification);
                        builder.append(ch != null ? ch : '?');
                    }
                },
                () -> builder.append("\",\n")
                    .append("    \""),
                () -> {
                    builder.setLength(builder.length() - 7);
                    builder.append("\n},{\n")
                        .append("    \"");
                });

            builder.setLength(builder.length() - 8);
            builder.append("}\n\n");
        }

        return builder.toString()
            .replaceAll("\"\"", "E");
    }

}
