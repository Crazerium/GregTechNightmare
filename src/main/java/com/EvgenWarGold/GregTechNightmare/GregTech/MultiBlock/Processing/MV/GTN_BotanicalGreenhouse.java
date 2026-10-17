package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.MV;

import static com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HatchElement.ManaHatch;
import static com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HatchElement.MeManaOutputHatch;
import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import com.EvgenWarGold.GregTechNightmare.Api.Dimensions;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_ManaHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_MeManaOutputHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_BotanicMultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_ProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData.SimpleMetaData;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.ParallelHelper;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

/**
 * Generating flowers in a greenhouse: the flowers stay in the input bus, their fuel is used like in Botania and the
 * mana goes into ME Mana Output Hatches or Mana Hatches. Every flower in the input bus is one parallel, up to the limit
 * of the energy tier. Passive flowers wither after their Botania lifetime of work and are used up.
 */
public class GTN_BotanicalGreenhouse extends GTN_BotanicMultiBlockBase<GTN_BotanicalGreenhouse> {

    /** Flowers that work at the base tier, doubled with every energy tier above it. */
    public static final int FLOWERS_PER_TIER = 16;
    private static final String TAG_PENDING_MANA = "pendingMana";
    private static final String TAG_FLOWER_WEAR = "flowerWear";

    /** Mana of the running recipe, given out when it ends. */
    private long pendingMana;
    /** Work ticks of the passive flowers since the last one withered, by flower type. */
    private final Map<String, Long> flowerWear = new HashMap<>();

    public GTN_BotanicalGreenhouse(int id, String name) {
        super(id, name);
    }

    public GTN_BotanicalGreenhouse(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_BotanicalGreenhouse>> getStructureVariants() {
        return List.of(
            new StructureVariant<>(
                "BotanicalGreenhouse",
                // spotless:off
                new String[][] {
                    {"CGGGC", "GGGGG", "GGGGG", "GGGGG", "CGGGC"},
                    {"CGGGC", "G   G", "G   G", "G   G", "CGGGC"},
                    {"CGGGC", "G   G", "G   G", "G   G", "CGGGC"},
                    {"CC~CC", "CDDDC", "CDDDC", "CDDDC", "CCCCC"}
                },
                // spotless:on
                new MultiblockOffsets(2, 3, 0),
                new MultiblockArea(5, 4, 5),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_BotanicalGreenhouse createNewMetaEntity() {
        return new GTN_BotanicalGreenhouse(this.mName);
    }

    @Override
    protected int getBaseTier() {
        return VoltageIndex.MV;
    }

    @Override
    public IStructureDefinition<GTN_BotanicalGreenhouse> getStructureDefinition() {
        return buildStructureDefinition(builder -> {
            builder.addMainCasing(
                'C',
                b -> b.hatches(InputBus, InputHatch, Energy, ExoticEnergy, MeManaOutputHatch, ManaHatch));
            builder.addAllGlasses('G')
                .addBlock('D', Blocks.dirt, 0);
        });
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTN_Recipe.GreenhouseRecipes;
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addInputBus()
            .addInputHatch()
            .addExoticOrEnergyHatch()
            .addMeManaOutputHatch()
            .addManaHatch();
    }

    @Override
    protected boolean needsOutputBus() {
        return false;
    }

    @Override
    protected boolean GTN_checkMachine(IGregTechTileEntity gte, ItemStack stack) {
        return super.GTN_checkMachine(gte, stack) && (!meManaOutputHatches.isEmpty() || !manaHatches.isEmpty());
    }

    @Override
    public OverclockType getOverclockType() {
        return OverclockType.NONE;
    }

    @Override
    public int getMaxParallelRecipes() {
        return FLOWERS_PER_TIER * super.getMaxParallelRecipes();
    }

    // region Mana output
    /** Mana the output hatches can take right now. */
    private long getOutputSpace() {
        long space = 0;
        for (GTN_MeManaOutputHatch hatch : meManaOutputHatches) space += hatch.getFreeSpace();
        for (GTN_ManaHatch hatch : manaHatches) space += Math.max(0, hatch.getMaxMana() - hatch.getCurrentMana());
        return space;
    }

    @Override
    protected void outputAfterRecipe() {
        super.outputAfterRecipe();
        long left = pendingMana;
        for (GTN_MeManaOutputHatch hatch : meManaOutputHatches) {
            if (left <= 0) break;
            left -= hatch.addMana(left);
        }
        for (GTN_ManaHatch hatch : manaHatches) {
            if (left <= 0) break;
            int added = (int) Math.min(left, Math.max(0, hatch.getMaxMana() - hatch.getCurrentMana()));
            if (added <= 0) continue;
            hatch.recieveMana(added);
            left -= added;
        }
        pendingMana = 0;
    }
    // endregion

    // region Flowers
    /** The generating flower of a recipe: the special flower among its inputs. */
    private static ItemStack getFlower(GTRecipe recipe) {
        for (ItemStack input : recipe.mInputs) {
            if (input != null && !ItemBlockSpecialFlower.getType(input)
                .isEmpty()) return input;
        }
        return null;
    }

    private static int countFlowers(ItemStack flower, ItemStack[] inputs) {
        if (flower == null || inputs == null) return 0;
        int count = 0;
        for (ItemStack stack : inputs) {
            if (stack != null && GTUtility.areStacksEqual(flower, stack)) count += stack.stackSize;
        }
        return count;
    }

    /** Adds the work of the passive flowers, the withered ones are taken from the input bus. */
    private void wearFlowers(GTRecipe recipe, ItemStack flower, int flowers, int ticks) {
        int lifetime = recipe.getMetadataOrDefault(SimpleMetaData.FLOWER_LIFETIME, 0);
        if (lifetime <= 0 || flower == null) return;
        String type = ItemBlockSpecialFlower.getType(flower);
        long wear = flowerWear.getOrDefault(type, 0L) + (long) flowers * ticks;
        ItemStack withered = GTUtility.copyAmount(1, flower);
        while (wear >= lifetime && depleteInput(withered)) wear -= lifetime;
        // a flower that cannot be taken withers as soon as it can
        flowerWear.put(type, Math.min(wear, lifetime));
    }
    // endregion

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new GTN_ProcessingLogic() {

            private int manaPerCraft(GTRecipe recipe) {
                return recipe.getMetadataOrDefault(SimpleMetaData.MANA_OUTPUT, 0);
            }

            @Nonnull
            @Override
            protected CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                if (countFlowers(getFlower(recipe), inputItems) <= 0) return CheckRecipeResultRegistry.NO_RECIPE;
                if (getOutputSpace() < manaPerCraft(recipe))
                    return SimpleCheckRecipeResult.ofFailure("mana_output_full");
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @Nonnull
            @Override
            protected ParallelHelper createParallelHelper(@Nonnull GTRecipe recipe) {
                long flowers = countFlowers(getFlower(recipe), inputItems);
                int mana = manaPerCraft(recipe);
                long spaceParallel = mana <= 0 ? Integer.MAX_VALUE : getOutputSpace() / mana;
                return super.createParallelHelper(recipe)
                    .setMaxParallel((int) Math.max(1, Math.min(maxParallel, Math.min(flowers, spaceParallel))));
            }

            @Nonnull
            @Override
            protected OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                // the fuel burns as long as in Botania, more energy does not make it faster
                return super.createOverclockCalculator(recipe).setNoOverclock(true);
            }

            @Nonnull
            @Override
            protected CheckRecipeResult onRecipeStart(@Nonnull GTRecipe recipe) {
                int parallel = Math.max(1, calculatedParallels);
                pendingMana = (long) manaPerCraft(recipe) * parallel;
                wearFlowers(recipe, getFlower(recipe), parallel, recipe.mDuration);
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        nbt.setLong(TAG_PENDING_MANA, pendingMana);
        NBTTagCompound wear = new NBTTagCompound();
        for (Map.Entry<String, Long> entry : flowerWear.entrySet()) wear.setLong(entry.getKey(), entry.getValue());
        nbt.setTag(TAG_FLOWER_WEAR, wear);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        pendingMana = nbt.getLong(TAG_PENDING_MANA);
        flowerWear.clear();
        NBTTagCompound wear = nbt.getCompoundTag(TAG_FLOWER_WEAR);
        for (Object key : wear.func_150296_c()) flowerWear.put((String) key, wear.getLong((String) key));
    }

    @Override
    public void GTN_WailaNBT(TileEntity tile, Dimensions dimensions, NBTTagCompound tag) {
        // mana per second of the running recipe
        tag.setLong("manaPerSecond", mMaxProgresstime > 0 ? pendingMana * 20 / mMaxProgresstime : 0);
    }

    @Override
    public void GTN_WailaBody(ItemStack itemStack, List<String> info, NBTTagCompound tag) {
        info.add(
            GTN_Utils.tr("multiblock.BotanicalGreenhouse.waila.mana_rate", formatNumber(tag.getLong("manaPerSecond"))));
    }
}
