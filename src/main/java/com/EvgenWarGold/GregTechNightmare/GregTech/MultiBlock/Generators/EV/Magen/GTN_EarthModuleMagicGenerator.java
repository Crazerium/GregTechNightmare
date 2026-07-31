package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Generators.EV.Magen;

import static com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HatchElement.AspectHatch;
import static com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HatchElement.ManaHatch;
import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.EvgenWarGold.GregTechNightmare.ModItems.BotaniaItems;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
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
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import thaumcraft.api.aspects.Aspect;

public class GTN_EarthModuleMagicGenerator extends GTN_MultiBlockBase<GTN_EarthModuleMagicGenerator>
    implements IMagicGeneratorModule {

    private int generate = 0;
    private int boostLevel = 1;
    private CatalystData catalystData;

    private static final Map<Aspect, Integer> TIER_1 = new HashMap<>();
    private static final Map<Aspect, Integer> TIER_2 = new HashMap<>();
    private static final Map<Aspect, Integer> TIER_3 = new HashMap<>();
    private static final Map<Aspect, Integer> TIER_4 = new HashMap<>();

    private static final FluidStack LUBRICANT;
    private static final FluidStack RADON;

    private static final int ASPECT_CONSUMPTION = 4;
    private static final int LUBRICANT_CONSUMPTION = 3_000;
    private static final int RADON_CONSUMPTION = 500;
    private static final int MANA_CONSUMPTION = 3_000;

    private static ItemStack MANASTEEL_INGOT;
    private static ItemStack RUNE_OF_EARTH;
    private static ItemStack TERRASTELL_INGOT;

    private static final int MANASTEEL_INGOT_CONSUMPTION = 32;
    private static final int RUNE_OF_EARTH_CONSUMPTION = 8;
    private static final int TERRASTEEL_INGOT_CONSUMPTION = 1;

    private static final int CATALYST_BUFF_DURATION = 3_600;

    private static final int TIER_1_GENERATE = 10_000;
    private static final int TIER_2_GENERATE = 25_000;
    private static final int TIER_3_GENERATE = 35_000;
    private static final int TIER_4_GENERATE = 50_000;

    private static final int VALID_DIMENSION = 7;

    static {
        TIER_1.put(Aspect.METAL, ASPECT_CONSUMPTION);

        TIER_2.put(Aspect.METAL, ASPECT_CONSUMPTION);
        TIER_2.put(Aspect.CRYSTAL, ASPECT_CONSUMPTION);

        TIER_3.put(Aspect.METAL, ASPECT_CONSUMPTION);
        TIER_3.put(Aspect.CRYSTAL, ASPECT_CONSUMPTION);
        TIER_3.put(Aspect.PLANT, ASPECT_CONSUMPTION);

        TIER_4.put(Aspect.METAL, ASPECT_CONSUMPTION);
        TIER_4.put(Aspect.CRYSTAL, ASPECT_CONSUMPTION);
        TIER_4.put(Aspect.PLANT, ASPECT_CONSUMPTION);
        TIER_4.put(Aspect.TRAVEL, ASPECT_CONSUMPTION);

        LUBRICANT = Materials.Lubricant.getFluid(1);
        RADON = Materials.Radon.getGas(1);
    }

    public GTN_EarthModuleMagicGenerator(int id, String name) {
        super(id, name);
    }

    public GTN_EarthModuleMagicGenerator(String name) {
        super(name);
    }

    @Override
    public int generate() {
        return generate;
    }

    @Override
    public int boostLevel() {
        return boostLevel;
    }

    @Override
    public List<StructureVariant<GTN_EarthModuleMagicGenerator>> getStructureVariants() {
        return List.of(
            new StructureVariant<>(
                "EarthModuleMagicGenerator",
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
    public GTN_EarthModuleMagicGenerator createNewMetaEntity() {
        return new GTN_EarthModuleMagicGenerator(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addInputBus()
            .addInputHatch()
            .addManaHatch()
            .addAspectHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_EarthModuleMagicGenerator> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addMainCasing('C', b -> b.hatches(AspectHatch, ManaHatch, InputHatch, InputBus))
                .addCasing('A', GTN_Casings.TPVCoilBlock)
                .addFrame('B', Materials.Titanium)
                .addBlock('D', ModBlocks.THAUMCRAFT_BLOCKS.AmberBlock.getBlock(), 0)
                .addBlock(
                    'E',
                    ModBlocks.THAUMIC_BASES_BLOCKS.EarthCrystalBlock.getBlock(),
                    ModBlocks.THAUMIC_BASES_BLOCKS.EarthCrystalBlock.meta)
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
            if (consumeItemFromHatches(TERRASTELL_INGOT, TERRASTEEL_INGOT_CONSUMPTION, true)) {
                consumeItemFromHatches(TERRASTELL_INGOT, TERRASTEEL_INGOT_CONSUMPTION, false);
                catalystData = new CatalystData(CATALYST_BUFF_DURATION, 0.25);
            } else if (consumeItemFromHatches(RUNE_OF_EARTH, RUNE_OF_EARTH_CONSUMPTION, true)) {
                consumeItemFromHatches(RUNE_OF_EARTH, RUNE_OF_EARTH_CONSUMPTION, false);
                catalystData = new CatalystData(CATALYST_BUFF_DURATION, 0.5);
            } else if (consumeItemFromHatches(MANASTEEL_INGOT, MANASTEEL_INGOT_CONSUMPTION, true)) {
                consumeItemFromHatches(MANASTEEL_INGOT, MANASTEEL_INGOT_CONSUMPTION, false);
                catalystData = new CatalystData(CATALYST_BUFF_DURATION, 0.75);
            }
        }

        if (consumeAspectFromHatches(TIER_4, true)) {
            consumeAspectFromHatches(TIER_4, false);
            generate = TIER_4_GENERATE;
        } else if (consumeAspectFromHatches(TIER_3, true)) {
            consumeAspectFromHatches(TIER_3, false);
            generate = TIER_3_GENERATE;
        } else if (consumeAspectFromHatches(TIER_2, true)) {
            consumeAspectFromHatches(TIER_2, false);
            generate = TIER_2_GENERATE;
        } else if (consumeAspectFromHatches(TIER_1, true)) {
            consumeAspectFromHatches(TIER_1, false);
            generate = TIER_1_GENERATE;
        }

        if (catalystData != null && catalystData.getDuration() != 0) {
            catalystBuff = catalystData.getBoost();
        }

        if (consumeManaFromHatches((int) (MANA_CONSUMPTION * catalystBuff), true)) {
            consumeManaFromHatches((int) (MANA_CONSUMPTION * catalystBuff), false);
            boostLevel = 4;
        } else if (consumeFluidFromHatches(RADON, (int) (RADON_CONSUMPTION * catalystBuff), true)) {
            consumeFluidFromHatches(RADON, (int) (RADON_CONSUMPTION * catalystBuff), false);
            boostLevel = 3;
        } else if (consumeFluidFromHatches(LUBRICANT, (int) (LUBRICANT_CONSUMPTION * catalystBuff), true)) {
            consumeFluidFromHatches(LUBRICANT, (int) (LUBRICANT_CONSUMPTION * catalystBuff), false);
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
                + formatNumber(tag.getInteger("generate")));
        currentTip.add(
            EnumChatFormatting.GREEN + "Catalyst duration: "
                + EnumChatFormatting.AQUA
                + formatNumber(tag.getInteger("catalyst")));
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);

        tag.setInteger("generate", generate);

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
        MANASTEEL_INGOT = BotaniaItems.ManaSteelIngot.get();
        RUNE_OF_EARTH = BotaniaItems.RuneOfEarth.get();
        TERRASTELL_INGOT = BotaniaItems.TerrasteelIngot.get();
    }
}
