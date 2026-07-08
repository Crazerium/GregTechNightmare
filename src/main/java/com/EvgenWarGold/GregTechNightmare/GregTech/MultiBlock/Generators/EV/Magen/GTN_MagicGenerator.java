package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Generators.EV.Magen;

import static gregtech.api.enums.HatchElement.InputHatch;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.CoordMultiBlock;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class GTN_MagicGenerator extends GTN_MultiBlockBase<GTN_MagicGenerator> {

    private static final List<Class<? extends GTN_MultiBlockBase<?>>> ALLOWED_LINK_MULTIBLOCK = Arrays.asList(
        GTN_WaterModuleMagicGenerator.class,
        GTN_AirModuleMagicGenerator.class,
        GTN_FireModuleMagicGenerator.class,
        GTN_EntropyModuleMagicGenerator.class,
        GTN_OrderModuleMagicGenerator.class,
        GTN_EarthModuleMagicGenerator.class);

    public GTN_MagicGenerator(int id, String name) {
        super(id, name);
    }

    public GTN_MagicGenerator(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_MagicGenerator>> getStructureVariants() {
        return List.of(
            new StructureVariant<>(
                "",
                // spotless:off
                new String[][]{
                    {"A"},
                    {"B"},
                    {"B"},
                    {"~"}
                },
                //spotless:on
                new MultiblockOffsets(0, 3, 0),
                new MultiblockArea(1, 4, 1),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_MagicGenerator createNewMetaEntity() {
        return new GTN_MagicGenerator(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {

    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_MagicGenerator> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addMainCasing('B', b -> b.hatches(InputHatch))
                .addCasing('A', GTN_Casings.VacuumCasing));
    }

    @Override
    public boolean tryLink(CoordMultiBlock coord) {
        if (getCoord().equals(coord)) return false;

        IGregTechTileEntity gte = multiBlocks.get(coord);

        if (gte == null) {
            IGregTechTileEntity newGte = coord.getMTEMultiBlockBase();
            if (newGte != null) {
                IMetaTileEntity mte = newGte.getMetaTileEntity();
                if (mte instanceof GTN_MultiBlockBase && isClassAllowed(mte.getClass())) {
                    removeExistingLinkOfSameType(mte.getClass(), coord);

                    multiBlocks.put(coord, newGte);
                    return true;
                }
            }
            return false;
        }

        IMetaTileEntity mte = gte.getMetaTileEntity();
        if (mte != null) return false;

        multiBlocks.remove(coord);

        IGregTechTileEntity newGte = coord.getMTEMultiBlockBase();
        if (newGte != null) {
            IMetaTileEntity newMte = newGte.getMetaTileEntity();
            if (newMte instanceof GTN_MultiBlockBase && isClassAllowed(newMte.getClass())) {
                removeExistingLinkOfSameType(newMte.getClass(), coord);

                multiBlocks.put(coord, newGte);
                return true;
            }
        }

        return false;
    }

    private void removeExistingLinkOfSameType(Class<?> mteClass, CoordMultiBlock exceptCoord) {
        multiBlocks.entrySet()
            .removeIf(entry -> {
                if (entry.getKey()
                    .equals(exceptCoord)) return false;
                if (entry.getValue() == null) return false;

                IMetaTileEntity existingMte = entry.getValue()
                    .getMetaTileEntity();
                return mteClass.isInstance(existingMte);
            });
    }

    private boolean isClassAllowed(Class<?> clazz) {
        return ALLOWED_LINK_MULTIBLOCK.stream()
            .anyMatch(allowedClass -> allowedClass.isAssignableFrom(clazz));
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return true;
    }

    @Override
    public boolean isEnergyMultiBlock() {
        return false;
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        if (multiBlocks.isEmpty()) return CheckRecipeResultRegistry.NO_RECIPE;

        int generate = 0;

        for (CoordMultiBlock coord : multiBlocks.keySet()) {
            IGregTechTileEntity gte = multiBlocks.get(coord);
            IMetaTileEntity mte = gte.getMetaTileEntity();

            if (mte instanceof IMagicGeneratorModule module) {
                if (gte.isActive()) {
                    generate += module.generate();
                }
            }
        }

        if (generate == 0) return CheckRecipeResultRegistry.NO_RECIPE;

        setEnergyGenerate(generate);
        super.mEfficiency = getEfficiency();
        setDurationInSeconds(5);
        return CheckRecipeResultRegistry.GENERATING;
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);

        for (CoordMultiBlock coord : multiBlocks.keySet()) {
            IGregTechTileEntity gte = multiBlocks.get(coord);
            IMetaTileEntity mte = gte.getMetaTileEntity();

            if (mte instanceof GTN_AirModuleMagicGenerator) {
                tag.setBoolean("AirModule", true);
                tag.setBoolean("AirModuleActive", gte.isActive());
            }

            if (mte instanceof GTN_FireModuleMagicGenerator) {
                tag.setBoolean("FireModule", true);
                tag.setBoolean("FireModuleActive", gte.isActive());
            }

            if (mte instanceof GTN_EarthModuleMagicGenerator) {
                tag.setBoolean("EarthModule", true);
                tag.setBoolean("EarthModuleActive", gte.isActive());
            }

            if (mte instanceof GTN_EntropyModuleMagicGenerator) {
                tag.setBoolean("EntropyModule", true);
                tag.setBoolean("EntropyModuleActive", gte.isActive());
            }

            if (mte instanceof GTN_OrderModuleMagicGenerator) {
                tag.setBoolean("OrderModule", true);
                tag.setBoolean("OrderModuleActive", gte.isActive());
            }

            if (mte instanceof GTN_WaterModuleMagicGenerator) {
                tag.setBoolean("WaterModule", true);
                tag.setBoolean("WaterModuleActive", gte.isActive());
            }
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);

        NBTTagCompound tag = accessor.getNBTData();

        currentTip
            .add(
                tag.getBoolean("AirModule")
                    ? EnumChatFormatting.YELLOW + "Air Module "
                        + EnumChatFormatting.RESET
                        + (tag.getBoolean("AirModuleActive") ? EnumChatFormatting.GREEN + "ACTIVE"
                            : EnumChatFormatting.RED + "INACTIVE")
                    : "");
        currentTip
            .add(
                tag.getBoolean("FireModule")
                    ? EnumChatFormatting.DARK_RED + "Fire Module "
                    + EnumChatFormatting.RESET
                    + (tag.getBoolean("FireModuleActive") ? EnumChatFormatting.GREEN + "ACTIVE"
                    : EnumChatFormatting.RED + "INACTIVE")
                    : "");
        currentTip
            .add(
                tag.getBoolean("EarthModule")
                    ? EnumChatFormatting.DARK_GREEN + "Earth Module "
                    + EnumChatFormatting.RESET
                    + (tag.getBoolean("EarthModuleActive") ? EnumChatFormatting.GREEN + "ACTIVE"
                    : EnumChatFormatting.RED + "INACTIVE")
                    : "");
        currentTip
            .add(
                tag.getBoolean("EntropyModule")
                    ? EnumChatFormatting.DARK_GRAY + "Entropy Module "
                    + EnumChatFormatting.RESET
                    + (tag.getBoolean("EntropyModuleActive") ? EnumChatFormatting.GREEN + "ACTIVE"
                    : EnumChatFormatting.RED + "INACTIVE")
                    : "");
        currentTip
            .add(
                tag.getBoolean("OrderModule")
                    ? EnumChatFormatting.WHITE + "Order Module "
                    + EnumChatFormatting.RESET
                    + (tag.getBoolean("OrderModuleActive") ? EnumChatFormatting.GREEN + "ACTIVE"
                    : EnumChatFormatting.RED + "INACTIVE")
                    : "");
        currentTip
            .add(
                tag.getBoolean("WaterModule")
                    ? EnumChatFormatting.DARK_BLUE + "Water Module "
                    + EnumChatFormatting.RESET
                    + (tag.getBoolean("WaterModuleActive") ? EnumChatFormatting.GREEN + "ACTIVE"
                    : EnumChatFormatting.RED + "INACTIVE")
                    : "");
    }
}
