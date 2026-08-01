package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Generators.EV.Magen;

import static com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HatchElement.MeAspectHatch;
import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.EvgenWarGold.GregTechNightmare.ModItems.ThaumcraftItems;
import com.EvgenWarGold.GregTechNightmare.ModItems.TinkerConstructItems;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.TCAspects;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gtPlusPlus.core.material.MaterialsElements;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
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
import com.EvgenWarGold.GregTechNightmare.ModBlocks.ModBlocks;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.enums.Materials;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.config.ConfigBlocks;

public class GTN_EntropyModuleMagicGenerator extends GTN_MultiBlockBase<GTN_EntropyModuleMagicGenerator>
    implements IMagicGeneratorModule {

    private long generate = 0;
    private int boostLevel = 1;
    private CatalystData catalystData;

    private static final Map<Aspect, Integer> TIER_1 = new HashMap<>();
    private static final Map<Aspect, Integer> TIER_2 = new HashMap<>();
    private static final Map<Aspect, Integer> TIER_3 = new HashMap<>();
    private static final Map<Aspect, Integer> TIER_4 = new HashMap<>();
    private static final int VACUOS_CONSUMPTION = 2_000;
    private static final int MORTUS_CONSUMPTION = 4_000;
    private static final int VITIUM_CONSUMPTION = 12_000;
    private static final int STRONTIO_CONSUMPTION = 16_000;

    private static FluidStack UNKNOWN_LIQUID;
    private static FluidStack UU_AMPLIFIER;
    private static FluidStack DRAGON_BLOOD;

    private static final int UNKNOWN_LIQUID_CONSUMPTION = 5_000;
    private static final int UU_AMPLIFIER_CONSUMPTION = 20_000;
    private static final int DRAGON_BLOOD_CONSUMPTION = 10_000;

    private static ItemStack RED_HEART;
    private static ItemStack YELLOW_HEART;
    private static ItemStack GREEN_HEART;

    private static final int RED_HEART_CONSUMPTION = 2_048;
    private static final int YELLOW_HEART_CONSUMPTION = 2_048;
    private static final int GREEN_HEART_CONSUMPTION = 2_048;

    private static final int CATALYST_BUFF_DURATION = 3_600;

    private static final int TIER_1_GENERATE = 500_000_000;
    private static final int TIER_2_GENERATE = 1_250_000_000;
    private static final int TIER_3_GENERATE = 1_750_000_000;
    private static final long TIER_4_GENERATE = 2_500_000_000L;

    private static final int VALID_DIMENSION = 81;

    static {
        TIER_1.put(Aspect.VOID, VACUOS_CONSUMPTION);

        TIER_2.put(Aspect.VOID, VACUOS_CONSUMPTION);
        TIER_2.put(Aspect.DEATH, MORTUS_CONSUMPTION);

        TIER_3.put(Aspect.VOID, VACUOS_CONSUMPTION);
        TIER_3.put(Aspect.DEATH, MORTUS_CONSUMPTION);
        TIER_3.put(Aspect.TAINT, VITIUM_CONSUMPTION);

        TIER_4.put(Aspect.VOID, VACUOS_CONSUMPTION);
        TIER_4.put(Aspect.DEATH, MORTUS_CONSUMPTION);
        TIER_4.put(Aspect.TAINT, VITIUM_CONSUMPTION);
        TIER_4.put(TCAspects.STRONTIO.getAspect(), STRONTIO_CONSUMPTION);
    }

    public GTN_EntropyModuleMagicGenerator(int id, String name) {
        super(id, name);
    }

    public GTN_EntropyModuleMagicGenerator(String name) {
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
    public List<StructureVariant<GTN_EntropyModuleMagicGenerator>> getStructureVariants() {
        return List.of(
            new StructureVariant<>(
                "EntropyModuleMagicGenerator",
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
    public GTN_EntropyModuleMagicGenerator createNewMetaEntity() {
        return new GTN_EntropyModuleMagicGenerator(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {

    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_EntropyModuleMagicGenerator> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addMainCasing('C', b -> b.hatches(InputHatch, InputBus, MeAspectHatch))
                .addCasing('A', GTN_Casings.AwakenedDraconiumCoilBlock)
                .addFrame('B', Materials.CosmicNeutronium)
                .addBlock('D', ModBlocks.THAUMCRAFT_BLOCKS.AmberBlock.getBlock(), 0)
                .addBlock(
                    'E',
                    ModBlocks.THAUMIC_BASES_BLOCKS.EntropyCrystalBlock.getBlock(),
                    ModBlocks.THAUMIC_BASES_BLOCKS.EntropyCrystalBlock.meta)
                .addBlock('F', ModBlocks.BOTANIA_BLOCKS.AlfGlass.getBlock(), 0));
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        IGregTechTileEntity gte = this.getBaseMetaTileEntity();
        if (gte != null) {
            World world = gte.getWorld();
            if (world != null && world.provider.dimensionId != VALID_DIMENSION) {
                return CheckRecipeResultRegistry.NO_RECIPE;
            }
        }

        generate = 0;
        boostLevel = 1;
        double catalystBuff = 1;

        if (catalystData == null) {
            if (consumeItemFromHatches(GREEN_HEART, GREEN_HEART_CONSUMPTION, true)) {
                consumeItemFromHatches(GREEN_HEART, GREEN_HEART_CONSUMPTION, false);
                catalystData = new CatalystData(CATALYST_BUFF_DURATION, 0.25);
            } else if (consumeItemFromHatches(YELLOW_HEART, YELLOW_HEART_CONSUMPTION, true)) {
                consumeItemFromHatches(YELLOW_HEART, YELLOW_HEART_CONSUMPTION, false);
                catalystData = new CatalystData(CATALYST_BUFF_DURATION, 0.5);
            } else if (consumeItemFromHatches(RED_HEART, RED_HEART_CONSUMPTION, true)) {
                consumeItemFromHatches(RED_HEART, RED_HEART_CONSUMPTION, false);
                catalystData = new CatalystData(CATALYST_BUFF_DURATION, 0.75);
            }
        }

        if (consumeAspectFromMeHatches(TIER_4, true)) {
            consumeAspectFromMeHatches(TIER_4, false);
            generate = TIER_4_GENERATE;
        } else if (consumeAspectFromMeHatches(TIER_3, true)) {
            consumeAspectFromMeHatches(TIER_3, false);
            generate = TIER_3_GENERATE;
        } else if (consumeAspectFromMeHatches(TIER_2, true)) {
            consumeAspectFromMeHatches(TIER_2, false);
            generate = TIER_2_GENERATE;
        } else if (consumeAspectFromMeHatches(TIER_1, true)) {
            consumeAspectFromMeHatches(TIER_1, false);
            generate = TIER_1_GENERATE;
        }

        if (catalystData != null && catalystData.getDuration() != 0) {
            catalystBuff = catalystData.getBoost();
        }

        if (consumeFluidFromHatches(DRAGON_BLOOD, (int) (DRAGON_BLOOD_CONSUMPTION * catalystBuff), true)) {
            consumeFluidFromHatches(DRAGON_BLOOD, (int) (DRAGON_BLOOD_CONSUMPTION * catalystBuff), false);
            boostLevel = 4;
        } else if (consumeFluidFromHatches(UU_AMPLIFIER, (int) (UU_AMPLIFIER_CONSUMPTION * catalystBuff), true)) {
            consumeFluidFromHatches(UU_AMPLIFIER, (int) (UU_AMPLIFIER_CONSUMPTION * catalystBuff), false);
            boostLevel = 3;
        } else if (consumeFluidFromHatches(UNKNOWN_LIQUID, (int) (UNKNOWN_LIQUID_CONSUMPTION * catalystBuff), true)) {
            consumeFluidFromHatches(UNKNOWN_LIQUID, (int) (UNKNOWN_LIQUID_CONSUMPTION * catalystBuff), false);
            boostLevel = 2;
        }

        generate *= boostLevel;

        if (generate > 0) {
            setDurationInSeconds(1);
            return CheckRecipeResultRegistry.SUCCESSFUL;
        }

        return CheckRecipeResultRegistry.NO_RECIPE;
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
            EnumChatFormatting.GREEN + "Generate: "
                + EnumChatFormatting.AQUA
                + formatNumber(tag.getLong("generate")));
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
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        super.onPostTick(aBaseMetaTileEntity, aTimer);
        if (aBaseMetaTileEntity.isServerSide()) {
            if (aTimer % 20 == 0) {
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
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        if (catalystData != null) {
            catalystData.writeToNBT(aNBT);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        catalystData = CatalystData.readFromNBT(aNBT);
    }

    @Override
    protected void initialize() {
        super.initialize();
        RED_HEART = TinkerConstructItems.MiniatureRedHeart.get();
        YELLOW_HEART = TinkerConstructItems.MiniatureYellowHeart.get();
        GREEN_HEART = TinkerConstructItems.MiniatureGreenHeart.get();

        UNKNOWN_LIQUID = FluidRegistry.getFluidStack("unknowwater", 1);
        UU_AMPLIFIER = Materials.UUAmplifier.getFluid(1);
        DRAGON_BLOOD = MaterialsElements.STANDALONE.DRAGON_METAL.getFluidStack(1);
    }
}
