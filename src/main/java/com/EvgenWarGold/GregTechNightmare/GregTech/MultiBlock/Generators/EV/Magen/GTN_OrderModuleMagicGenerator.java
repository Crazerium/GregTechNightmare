package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Generators.EV.Magen;

import static com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HatchElement.MeAspectHatch;
import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.Api.Dimensions;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.BotaniaBlocks;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.ThaumcraftBlocks;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.ThaumicBasesBlocks;
import com.EvgenWarGold.GregTechNightmare.ModItems.ThaumcraftItems;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TCAspects;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.config.ConfigBlocks;

public class GTN_OrderModuleMagicGenerator extends GTN_MultiBlockBase<GTN_OrderModuleMagicGenerator>
    implements IMagicGeneratorModule {

    private int generate = 0;
    private int boostLevel = 1;
    private CatalystData catalystData;

    private static final Map<Aspect, Integer> TIER_1 = new HashMap<>();
    private static final Map<Aspect, Integer> TIER_2 = new HashMap<>();
    private static final Map<Aspect, Integer> TIER_3 = new HashMap<>();
    private static final Map<Aspect, Integer> TIER_4 = new HashMap<>();

    private static final int PERMUTATION_CONSUMPTION = 1_000;
    private static final int INSTRUMENTUM_CONSUMPTION = 2_000;
    private static final int TEMPUS_CONSUMPTION = 3_000;
    private static final int AEQUALITAS_CONSUMPTION = 4_000;

    private static FluidStack LIQUID_OXYGEN;
    private static FluidStack MOLTEN_PRASEODIUM;
    private static FluidStack LIQUID_DEATH;

    private static final int LIQUID_OXYGEN_CONSUMPTION = 100_000;
    private static final int MOLTEN_PRASEODIUM_CONSUMPTION = 2_000;
    private static final int LIQUID_DEATH_CONSUMPTION = 20_000;

    private static ItemStack SUPER_FUEL_BINDER;
    private static ItemStack MAGIC_SOLID_SUPER_FUEL;
    private static ItemStack PRIMORDIAL_PEARL;

    private static final int SUPER_FUEL_BINDER_CONSUMPTION = 512;
    private static final int MAGIC_SOLID_SUPER_FUEL_CONSUMPTION = 512;
    private static final int PRIMORDIAL_PEARL_CONSUMPTION = 4;

    private static final int CATALYST_BUFF_DURATION = 3_600;

    private static final int TIER_1_GENERATE = 75_000_000;
    private static final int TIER_2_GENERATE = 187_500_000;
    private static final int TIER_3_GENERATE = 262_500_000;
    private static final int TIER_4_GENERATE = 375_000_000;

    private static final int VALID_DIMENSION = 42;

    static {
        TIER_1.put(Aspect.EXCHANGE, PERMUTATION_CONSUMPTION);

        TIER_2.put(Aspect.EXCHANGE, PERMUTATION_CONSUMPTION);
        TIER_2.put(Aspect.TOOL, INSTRUMENTUM_CONSUMPTION);

        TIER_3.put(Aspect.EXCHANGE, PERMUTATION_CONSUMPTION);
        TIER_3.put(Aspect.TOOL, INSTRUMENTUM_CONSUMPTION);
        TIER_3.put(Aspect.MOTION, TEMPUS_CONSUMPTION);

        TIER_4.put(Aspect.EXCHANGE, PERMUTATION_CONSUMPTION);
        TIER_4.put(Aspect.TOOL, INSTRUMENTUM_CONSUMPTION);
        TIER_4.put(Aspect.MOTION, TEMPUS_CONSUMPTION);
        TIER_4.put(TCAspects.AEQUALITAS.getAspect(), AEQUALITAS_CONSUMPTION);
    }

    public GTN_OrderModuleMagicGenerator(int id, String name) {
        super(id, name);
    }

    public GTN_OrderModuleMagicGenerator(String name) {
        super(name);
    }

    @Override
    public long generate() {
        return generate;
    }

    @Override
    public int boostLevel() {
        return boostLevel;
    }

    @Override
    public List<StructureVariant<GTN_OrderModuleMagicGenerator>> getStructureVariants() {
        return List.of(
            new StructureVariant<>(
                "OrderModuleMagicGenerator",
                // spotless:off
                new String[][]{
                    {"     CCC     ", "   CCCCCCC   ", "  CCCCCCCCC  ", " CCCCCCCCCCC ", " CCCCCCCCCCC ", "CCCCCCCCCCCCC", "CCCCCCCCCCCCC", "CCCCCCCCCCCCC", " CCCCCCCCCCC ", " CCCCCCCCCCC ", "  CCCCCCCCC  ", "   CCCCCCC   ", "     CCC     "},
                    {"     B       ", "   B         ", "             ", " B           ", "             ", "             ", "B     B      ", "             ", "             ", " B         B ", "             ", "   B     B   ", "      B      "},
                    {"     B       ", "   B         ", "             ", " B           ", "             ", "      B      ", "B     F      ", "      B      ", "             ", " B         D ", "             ", "   B     B   ", "      B      "},
                    {"     B       ", "   B         ", "             ", " B           ", "             ", "             ", "B    BFB     ", "             ", "             ", " B           ", "             ", "   B     D   ", "      B      "},
                    {"     B       ", "   B         ", "             ", " B           ", "             ", "             ", "B     B      ", "             ", "             ", " B         D ", "             ", "   B         ", "      D      "},
                    {"     B       ", "   B         ", "             ", " B           ", "             ", "      B      ", "B    BEB     ", "      B      ", "             ", " B         B ", "             ", "   D     D   ", "             "},
                    {"     B       ", "   B         ", "             ", " B           ", "             ", "      B      ", "B    BE      ", "      B      ", "             ", " D         B ", "             ", "         B   ", "      D      "},
                    {"     B       ", "   B         ", "             ", " B           ", "             ", "     FEF     ", "D    EEE     ", "     FEF     ", "             ", "           B ", "             ", "   D     B   ", "      B      "},
                    {"     B       ", "   B         ", "             ", " D           ", "     FEF     ", "    FAAAF    ", "    EAAAE    ", "    FAAAF    ", "     FEF     ", " D         B ", "             ", "   B     B   ", "      B      "},
                    {"     B       ", "   D         ", "             ", "             ", "     FEF     ", "    FAAAF    ", "D   EAAAE    ", "    FAAAF    ", "     FEF     ", " B         B ", "             ", "   B     B   ", "      B      "},
                    {"     D       ", "             ", "             ", " D           ", "     FEF     ", "    FAAAF    ", "B   EAAAE    ", "    FAAAF    ", "     FEF     ", " B         B ", "             ", "   B     B   ", "      B      "},
                    {"             ", "   D         ", "             ", " B           ", "             ", "     FEF     ", "B    EEE     ", "     FEF     ", "             ", " B         B ", "             ", "   B     B   ", "      B      "},
                    {"     D       ", "   B         ", "             ", " B           ", "             ", "             ", "B            ", "             ", "             ", " B         B ", "             ", "   B     B   ", "      B      "},
                    {"     B       ", "   B         ", "             ", " B           ", "             ", "             ", "B            ", "             ", "             ", " B         B ", "             ", "   B     B   ", "      B      "},
                    {"     C~C     ", "   CCCCCCC   ", "  CCCCCCCCC  ", " CCCCCCCCCCC ", " CCCCCCCCCCC ", "CCCCCCCCCCCCC", "CCCCCCCCCCCCC", "CCCCCCCCCCCCC", " CCCCCCCCCCC ", " CCCCCCCCCCC ", "  CCCCCCCCC  ", "   CCCCCCC   ", "     CCC     "}
                },
                //spotless:on
                new MultiblockOffsets(6, 14, 0),
                new MultiblockArea(13, 15, 13),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_OrderModuleMagicGenerator createNewMetaEntity() {
        return new GTN_OrderModuleMagicGenerator(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addInputBus()
            .addInputHatch()
            .addMeAspectHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_OrderModuleMagicGenerator> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addMainCasing('C', b -> b.hatches(InputHatch, InputBus, MeAspectHatch))
                .addCasing('A', GTN_Casings.ElectrumFluxCoilBlock)
                .addFrame('B', Materials.Neutronium)
                .addBlock('D', ThaumcraftBlocks.AmberBlock.get(), 0)
                .addBlock(
                    'E',
                    ThaumicBasesBlocks.OrderCrystalBlock.get(),
                    ThaumicBasesBlocks.OrderCrystalBlock.getMeta())
                .addBlock('F', BotaniaBlocks.AlfGlass.get(), 0));
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        IGregTechTileEntity gte = this.getBaseMetaTileEntity();
        if (gte != null) {
            World world = gte.getWorld();
            if (world != null && world.provider.dimensionId != VALID_DIMENSION) {
                return processingHelper.resultFailureMessage("Invalid Dimension");
            }
        }

        generate = 0;
        boostLevel = 1;
        double catalystBuff = 1;

        if (catalystData == null) {
            if (processingHelper.consumeItem(PRIMORDIAL_PEARL, PRIMORDIAL_PEARL_CONSUMPTION)) {
                catalystData = new CatalystData(CATALYST_BUFF_DURATION, 0.25);
            } else if (processingHelper.consumeItem(MAGIC_SOLID_SUPER_FUEL, MAGIC_SOLID_SUPER_FUEL_CONSUMPTION)) {
                catalystData = new CatalystData(CATALYST_BUFF_DURATION, 0.5);
            } else if (processingHelper.consumeItem(SUPER_FUEL_BINDER, SUPER_FUEL_BINDER_CONSUMPTION)) {
                catalystData = new CatalystData(CATALYST_BUFF_DURATION, 0.75);
            }
        }

        if (catalystData != null && catalystData.getDuration() != 0) {
            catalystBuff = catalystData.getBoost();
        }

        if (processingHelper.consumeMeAspect(TIER_4)) {
            generate = TIER_4_GENERATE;
        } else if (processingHelper.consumeMeAspect(TIER_3)) {
            generate = TIER_3_GENERATE;
        } else if (processingHelper.consumeMeAspect(TIER_2)) {
            generate = TIER_2_GENERATE;
        } else if (processingHelper.consumeMeAspect(TIER_1)) {
            generate = TIER_1_GENERATE;
        }

        if (processingHelper.consumeFluid(LIQUID_DEATH, (int) (LIQUID_DEATH_CONSUMPTION * catalystBuff))) {
            boostLevel = 4;
        } else if (processingHelper
            .consumeFluid(MOLTEN_PRASEODIUM, (int) (MOLTEN_PRASEODIUM_CONSUMPTION * catalystBuff))) {
                boostLevel = 3;
            } else if (processingHelper.consumeFluid(LIQUID_OXYGEN, (int) (LIQUID_OXYGEN_CONSUMPTION * catalystBuff))) {
                boostLevel = 2;
            }

        generate *= boostLevel;

        if (generate > 0) {
            processingHelper.setDurationInSeconds(1);
            return processingHelper.resultSuccess();
        }

        return processingHelper.resultNoRecipe();
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
    public void GTN_WailaBody(ItemStack itemStack, List<String> info, NBTTagCompound tag) {
        info.add(
            EnumChatFormatting.GREEN + "Generate: " + EnumChatFormatting.AQUA + formatNumber(tag.getLong("generate")));
        info.add(
            EnumChatFormatting.GREEN + "Catalyst duration: "
                + EnumChatFormatting.AQUA
                + formatNumber(tag.getInteger("catalyst")));
    }

    @Override
    public void GTN_WailaNBT(TileEntity tile, Dimensions dimensions, NBTTagCompound tag) {
        tag.setLong("generate", generate);

        if (catalystData != null) {
            tag.setInteger("catalyst", catalystData.getDuration());
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity gte, long timer) {
        super.onPostTick(gte, timer);
        if (gte.isServerSide()) {
            if (timer % 20 == 0) {
                if (catalystData != null) {
                    catalystData.decreaseDuration();

                    if (catalystData.getDuration() <= 0) {
                        catalystData = null;
                    }
                }
            }
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        if (catalystData != null) {
            catalystData.writeToNBT(nbt);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        catalystData = CatalystData.readFromNBT(nbt);
    }

    @Override
    protected void initialize() {
        super.initialize();
        SUPER_FUEL_BINDER = ItemList.SFMixture.get(1);
        MAGIC_SOLID_SUPER_FUEL = ItemList.MSFMixture.get(1);
        PRIMORDIAL_PEARL = ThaumcraftItems.PrimordialPearl.get();

        LIQUID_OXYGEN = Materials.LiquidOxygen.getGas(1);
        MOLTEN_PRASEODIUM = Materials.Praseodymium.getMolten(1);
        LIQUID_DEATH = new FluidStack(ConfigBlocks.FLUIDDEATH, 1);
    }
}
