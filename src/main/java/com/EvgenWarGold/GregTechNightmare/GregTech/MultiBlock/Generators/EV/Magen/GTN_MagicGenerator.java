package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Generators.EV.Magen;

import static com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HatchElement.DynamoMulti;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.Api.Dimensions;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.CoordMultiBlock;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.BotaniaBlocks;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.ThaumcraftBlocks;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.ThaumicBasesBlocks;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import goodgenerator.loader.Loaders;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;

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
                "MagicGenerator",
                // spotless:off
                new String[][]{
                    {"               ", "               ", "               ", "               ", "               ", "      CCC      ", "     CAAAC     ", "     CAEAC     ", "     CAAAC     ", "      CCC      ", "               ", "               ", "               ", "               ", "               "},
                    {"               ", "               ", "               ", "               ", "     CCCCC     ", "    CCCCCCC    ", "    CC   CC    ", "    CC  ECC    ", "    CC   CC    ", "    CCCCCCC    ", "     CCCCC     ", "               ", "               ", "               ", "               "},
                    {"               ", "               ", "               ", "      CDC      ", "    DCAAACD    ", "    C     C    ", "   CA  E  AC   ", "   DA     AD   ", "   CA     AC   ", "    C     C    ", "    DCAAACD    ", "      CDC      ", "               ", "               ", "               "},
                    {"     FFFFF     ", "   FF     FF   ", "  E         E  ", " F    CDC    F ", " F  DC   CD  F ", "F   C     C   F", "F  C       C  F", "F  D  E    D  F", "F  C       C  F", "F   C     C   F", " F  DC   CD  F ", " F    CDC    F ", "  E         E  ", "   FF     FF   ", "     FFFFF     "},
                    {"               ", "               ", "               ", "    B CDC B    ", "   BDC   CDB   ", "    C     C    ", "   C       C   ", "   D       D   ", "   C   E   C   ", "    C     C    ", "   BDC   CDB   ", "    B CDC B    ", "               ", "               ", "               "},
                    {"               ", "               ", "      CDC      ", "   DC     CD   ", "   C       C   ", "               ", "  C         C  ", "  D     E   D  ", "  C         C  ", "               ", "   C       C   ", "   DC     CD   ", "      CDC      ", "               ", "               "},
                    {"               ", "  F   FFF   F  ", " FF   CDC   FF ", "   DC     CD   ", "   C       C   ", "               ", " FC    E    CF ", " FD         DF ", " FC         CF ", "               ", "   C       C   ", "   DC     CD   ", " FF   CDC   FF ", "  F   FFF   F  ", "               "},
                    {"               ", "   F  FDF  F   ", "  DF  FDF  FD  ", " FFDC     CDFF ", "   C       C   ", "               ", " FF         FF ", " DD    E    DD ", " FF         FF ", "               ", "   C       C   ", " FFDC     CDFF ", "  DF  FDF  FD  ", "   F  FDF  F   ", "               "},
                    {"               ", "       D       ", "  DF  F F  FD  ", "  FDC     CDF  ", "   C       C   ", "               ", "  F   BBB   F  ", " D    BEB    D ", "  F   BBB   F  ", "               ", "   C       C   ", "  FDC     CDF  ", "  DF  F F  FD  ", "       D       ", "               "},
                    {"               ", "       ~       ", "  DF  FDF  FD  ", "  FDDDDDDDDDF  ", "   D   D   D   ", "   D   D   D   ", "  FD  BBB  DF  ", " DDDDDBEBDDDDD ", "  FD  BBB  DF  ", "   D   D   D   ", "   D   D   D   ", "  FDDDDDDDDDF  ", "  DF  FDF  FD  ", "       D       ", "               "},
                    {"               ", "       D       ", "  DF  F F  FD  ", "  FDC     CDF  ", "   C       C   ", "               ", "  F   BBB   F  ", " D    BEB    D ", "  F   BBB   F  ", "               ", "   C       C   ", "  FDC     CDF  ", "  DF  F F  FD  ", "       D       ", "               "},
                    {"               ", "   F  FDF  F   ", "  DF  FDF  FD  ", " FFDC     CDFF ", "   C       C   ", "               ", " FF         FF ", " DD    E    DD ", " FF         FF ", "               ", "   C       C   ", " FFDC     CDFF ", "  DF  FDF  FD  ", "   F  FDF  F   ", "               "},
                    {"               ", "  F   FFF   F  ", " FF   CDC   FF ", "   DC     CD   ", "   C       C   ", "               ", " FC    E    CF ", " FD         DF ", " FC         CF ", "               ", "   C       C   ", "   DC     CD   ", " FF   CDC   FF ", "  F   FFF   F  ", "               "},
                    {"               ", "               ", "      CDC      ", "   DC     CD   ", "   C       C   ", "               ", "  C         C  ", "  D     E   D  ", "  C         C  ", "               ", "   C       C   ", "   DC     CD   ", "      CDC      ", "               ", "               "},
                    {"               ", "               ", "               ", "    B CDC B    ", "   BDC   CDB   ", "    C     C    ", "   C       C   ", "   D       D   ", "   C   E   C   ", "    C     C    ", "   BDC   CDB   ", "    B CDC B    ", "               ", "               ", "               "},
                    {"     FFFFF     ", "   FF     FF   ", "  E         E  ", " F    CDC    F ", " F  DC   CD  F ", "F   C     C   F", "F  C       C  F", "F  D  E    D  F", "F  C       C  F", "F   C     C   F", " F  DC   CD  F ", " F    CDC    F ", "  E         E  ", "   FF     FF   ", "     FFFFF     "},
                    {"               ", "               ", "               ", "      CDC      ", "    DCAAACD    ", "    C     C    ", "   CA  E  AC   ", "   DA     AD   ", "   CA     AC   ", "    C     C    ", "    DCAAACD    ", "      CDC      ", "               ", "               ", "               "},
                    {"               ", "               ", "               ", "               ", "      CCC      ", "     CCCCC     ", "    CC   CC    ", "    CC E CC    ", "    CC   CC    ", "     CCCCC     ", "      CCC      ", "               ", "               ", "               ", "               "},
                    {"               ", "               ", "               ", "               ", "               ", "      CCC      ", "     CAAAC     ", "     CAEAC     ", "     CAAAC     ", "      CCC      ", "               ", "               ", "               ", "               ", "               "}
                },
                //spotless:on
                new MultiblockOffsets(7, 9, 1),
                new MultiblockArea(15, 19, 15),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_MagicGenerator createNewMetaEntity() {
        return new GTN_MagicGenerator(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addDynamoHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_MagicGenerator> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addMainCasing('C', b -> b.hatches(DynamoMulti))
                .addBlock('A', Loaders.essentiaCell, 0)
                .addBlock('B', Loaders.essentiaFilterCasing, 0)
                .addBlock('D', ThaumcraftBlocks.AmberBlock.get(), 0)
                .addBlock('F', ThaumicBasesBlocks.VoidBlock.get(), 0)
                .addBlock('E', BotaniaBlocks.AlfGlass.get(), 0));
    }

    @Override
    public boolean linkClassAllowed(Class<?> clazz) {
        return ALLOWED_LINK_MULTIBLOCK.stream()
            .anyMatch(allowedClass -> allowedClass.isAssignableFrom(clazz));
    }

    @Override
    public boolean linkUseSameType() {
        return true;
    }

    @Override
    public boolean linkUseP2P() {
        return true;
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
        if (multiBlocks.isEmpty()) {
            return processingHelper.resultNoRecipe();
        }

        long generate = 0;
        int activeModules = 0;

        for (CoordMultiBlock coord : multiBlocks.keySet()) {
            IGregTechTileEntity gte = multiBlocks.get(coord);
            IMetaTileEntity mte = gte.getMetaTileEntity();

            if (mte instanceof IMagicGeneratorModule module) {
                if (gte.isActive()) {
                    generate += module.generate();
                    activeModules++;
                }
            }
        }

        if (generate == 0) {
            return processingHelper.resultNoRecipe();
        }

        double bonusMultiplier = 1.0 + (activeModules * 0.25);
        generate = (long) (generate * bonusMultiplier);

        processingHelper.setEnergyGenerate(generate);
        processingHelper.setDurationInSeconds(1);
        return processingHelper.resultGenerating();
    }

    @Override
    public void GTN_WailaNBT(TileEntity tile, Dimensions dimensions, NBTTagCompound tag) {
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
    public void GTN_WailaBody(ItemStack itemStack, List<String> info, NBTTagCompound tag) {
        info.add(
            tag.getBoolean(
                "AirModule")
                    ? EnumChatFormatting.YELLOW + "Air Module "
                        + EnumChatFormatting.RESET
                        + (tag.getBoolean("AirModuleActive") ? EnumChatFormatting.GREEN + "ACTIVE"
                            : EnumChatFormatting.RED + "INACTIVE")
                    : "");
        info.add(
            tag.getBoolean(
                "FireModule")
                    ? EnumChatFormatting.DARK_RED + "Fire Module "
                        + EnumChatFormatting.RESET
                        + (tag.getBoolean("FireModuleActive") ? EnumChatFormatting.GREEN + "ACTIVE"
                            : EnumChatFormatting.RED + "INACTIVE")
                    : "");
        info.add(
            tag.getBoolean(
                "EarthModule")
                    ? EnumChatFormatting.DARK_GREEN + "Earth Module "
                        + EnumChatFormatting.RESET
                        + (tag.getBoolean("EarthModuleActive") ? EnumChatFormatting.GREEN + "ACTIVE"
                            : EnumChatFormatting.RED + "INACTIVE")
                    : "");
        info.add(
            tag.getBoolean(
                "EntropyModule")
                    ? EnumChatFormatting.DARK_GRAY + "Entropy Module "
                        + EnumChatFormatting.RESET
                        + (tag.getBoolean("EntropyModuleActive") ? EnumChatFormatting.GREEN + "ACTIVE"
                            : EnumChatFormatting.RED + "INACTIVE")
                    : "");
        info.add(
            tag.getBoolean(
                "OrderModule")
                    ? EnumChatFormatting.WHITE + "Order Module "
                        + EnumChatFormatting.RESET
                        + (tag.getBoolean("OrderModuleActive") ? EnumChatFormatting.GREEN + "ACTIVE"
                            : EnumChatFormatting.RED + "INACTIVE")
                    : "");
        info.add(
            tag.getBoolean(
                "WaterModule")
                    ? EnumChatFormatting.DARK_BLUE + "Water Module "
                        + EnumChatFormatting.RESET
                        + (tag.getBoolean("WaterModuleActive") ? EnumChatFormatting.GREEN + "ACTIVE"
                            : EnumChatFormatting.RED + "INACTIVE")
                    : "");
    }
}
