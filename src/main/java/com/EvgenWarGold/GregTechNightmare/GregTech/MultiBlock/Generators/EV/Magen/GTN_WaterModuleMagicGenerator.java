package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Generators.EV.Magen;

import static com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HatchElement.MeAspectHatch;
import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.BotaniaBlocks;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.ThaumcraftBlocks;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.ThaumicBasesBlocks;
import com.EvgenWarGold.GregTechNightmare.ModItems.BotaniaItems;
import com.EvgenWarGold.GregTechNightmare.ModItems.NewHorizonsCoreModItems;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gtPlusPlus.xmod.thermalfoundation.fluid.TFFluids;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import thaumcraft.api.aspects.Aspect;

public class GTN_WaterModuleMagicGenerator extends GTN_MultiBlockBase<GTN_WaterModuleMagicGenerator>
    implements IMagicGeneratorModule {

    private int generate = 0;
    private int boostLevel = 1;
    private CatalystData catalystData;

    private static final Map<Aspect, Integer> TIER_1 = new HashMap<>();
    private static final Map<Aspect, Integer> TIER_2 = new HashMap<>();
    private static final Map<Aspect, Integer> TIER_3 = new HashMap<>();
    private static final Map<Aspect, Integer> TIER_4 = new HashMap<>();

    private static final int VICTUS_CONSUMPTION = 1_000;
    private static final int LIMUS_CONSUMPTION = 2_000;
    private static final int VENENUM_CONSUMPTION = 3_000;
    private static final int TEMPESTAS_CONSUMPTION = 4_000;

    private static FluidStack GELID_CRYOTHEUM;
    private static FluidStack FIERY_BLOOD;
    private static FluidStack LIQUID_DNA;

    private static final int GELID_CRYOTHEUM_CONSUMPTION = 20_000;
    private static final int FIERY_BLOOD_CONSUMPTION = 10_000;
    private static final int LIQUID_DNA_CONSUMPTION = 50_000;

    private static ItemStack SNOW_QUEEN_BLOOD;
    private static ItemStack NETHER_STAR_BLOCK;
    private static ItemStack DRAGON_STONE;

    private static final int SNOW_QUEEN_BLOOD_CONSUMPTION = 512;
    private static final int NETHER_STAR_BLOCK_CONSUMPTION = 512;
    private static final int DRAGON_STONE_CONSUMPTION = 512;

    private static final int CATALYST_BUFF_DURATION = 3_600;

    private static final int TIER_1_GENERATE = 7_500_000;
    private static final int TIER_2_GENERATE = 18_750_000;
    private static final int TIER_3_GENERATE = 26_250_000;
    private static final int TIER_4_GENERATE = 37_500_000;

    private static final int VALID_DIMENSION = 35;

    static {
        TIER_1.put(Aspect.LIFE, VICTUS_CONSUMPTION);

        TIER_2.put(Aspect.LIFE, VICTUS_CONSUMPTION);
        TIER_2.put(Aspect.SLIME, LIMUS_CONSUMPTION);

        TIER_3.put(Aspect.LIFE, VICTUS_CONSUMPTION);
        TIER_3.put(Aspect.SLIME, LIMUS_CONSUMPTION);
        TIER_3.put(Aspect.POISON, VENENUM_CONSUMPTION);

        TIER_4.put(Aspect.LIFE, VICTUS_CONSUMPTION);
        TIER_4.put(Aspect.SLIME, LIMUS_CONSUMPTION);
        TIER_4.put(Aspect.POISON, VENENUM_CONSUMPTION);
        TIER_4.put(Aspect.WEATHER, TEMPESTAS_CONSUMPTION);
    }

    public GTN_WaterModuleMagicGenerator(int id, String name) {
        super(id, name);
    }

    public GTN_WaterModuleMagicGenerator(String name) {
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
    public List<StructureVariant<GTN_WaterModuleMagicGenerator>> getStructureVariants() {
        return List.of(
            new StructureVariant<>(
                "WaterModuleMagicGenerator",
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
    public GTN_WaterModuleMagicGenerator createNewMetaEntity() {
        return new GTN_WaterModuleMagicGenerator(this.mName);
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
    public IStructureDefinition<GTN_WaterModuleMagicGenerator> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addMainCasing('C', b -> b.hatches(InputHatch, InputBus, MeAspectHatch))
                .addCasing('A', GTN_Casings.NaquadahAlloyCoilBlock)
                .addFrame('B', Materials.Americium)
                .addBlock('D', ThaumcraftBlocks.AmberBlock.get(), 0)
                .addBlock(
                    'E',
                    ThaumicBasesBlocks.WaterCrystalBlock.get(),
                    ThaumicBasesBlocks.WaterCrystalBlock.getMeta())
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
            if (processingHelper.consumeItem(DRAGON_STONE, DRAGON_STONE_CONSUMPTION)) {
                catalystData = new CatalystData(CATALYST_BUFF_DURATION, 0.25);
            } else if (processingHelper.consumeItem(NETHER_STAR_BLOCK, NETHER_STAR_BLOCK_CONSUMPTION)) {
                catalystData = new CatalystData(CATALYST_BUFF_DURATION, 0.5);
            } else if (processingHelper.consumeItem(SNOW_QUEEN_BLOOD, SNOW_QUEEN_BLOOD_CONSUMPTION)) {
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

        if (processingHelper.consumeFluid(LIQUID_DNA, (int) (LIQUID_DNA_CONSUMPTION * catalystBuff))) {
            boostLevel = 4;
        } else if (processingHelper.consumeFluid(FIERY_BLOOD, (int) (FIERY_BLOOD_CONSUMPTION * catalystBuff))) {
            boostLevel = 3;
        } else if (processingHelper.consumeFluid(GELID_CRYOTHEUM, (int) (GELID_CRYOTHEUM_CONSUMPTION * catalystBuff))) {
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
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);

        NBTTagCompound tag = accessor.getNBTData();

        currentTip.add(
            EnumChatFormatting.GREEN + "Generate: " + EnumChatFormatting.AQUA + formatNumber(tag.getLong("generate")));
        currentTip.add(
            EnumChatFormatting.GREEN + "Catalyst duration: "
                + EnumChatFormatting.AQUA
                + formatNumber(tag.getInteger("catalyst")));
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);

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
        SNOW_QUEEN_BLOOD = NewHorizonsCoreModItems.SnowQueenBlood.get();
        NETHER_STAR_BLOCK = Materials.NetherStar.getBlocks(1);
        DRAGON_STONE = BotaniaItems.DragonStone.get();

        GELID_CRYOTHEUM = new FluidStack(TFFluids.fluidCryotheum, 0);
        FIERY_BLOOD = Materials.FierySteel.getFluid(1);
        LIQUID_DNA = FluidRegistry.getFluidStack("liquiddna", 500);
    }
}
