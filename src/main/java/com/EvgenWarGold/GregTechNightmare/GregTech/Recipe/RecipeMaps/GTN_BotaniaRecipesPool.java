package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import static gregtech.api.enums.TierEU.RECIPE_EV;
import static gregtech.api.enums.TierEU.RECIPE_HV;
import static gregtech.api.enums.TierEU.RECIPE_LV;
import static gregtech.api.enums.TierEU.RECIPE_MV;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import com.EvgenWarGold.GregTechNightmare.GTN_Logger;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData.SimpleMetaData;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.BotaniaBlocks;
import com.EvgenWarGold.GregTechNightmare.ModItems.BotaniaItems;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeElvenTrade;
import vazkii.botania.api.recipe.RecipeManaInfusion;
import vazkii.botania.api.recipe.RecipePetals;
import vazkii.botania.api.recipe.RecipePureDaisy;
import vazkii.botania.api.recipe.RecipeRuneAltar;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;
import vazkii.botania.common.lib.LibBlockNames;

/** Converts the Botania recipes into the recipe maps of the Botania machines. */
public final class GTN_BotaniaRecipesPool {

    /** Mana of the Terrestrial Agglomeration Plate, hardcoded in Botania. */
    private static final int TERRA_PLATE_MANA = 500_000;

    /** Item slots of the runic altar recipe map in NEI, see {@link GTN_Recipe#RuneAltarRecipes}. */
    public static final int RUNE_ALTAR_ITEM_SLOTS = 12;
    public static final int PETAL_APOTHECARY_ITEM_SLOTS = 16;
    public static final int ELVEN_TRADE_ITEM_SLOTS = 4;

    /** Time of the Pure Daisy for one block. */
    private static final int PURE_DAISY_TICKS = 150;

    /** Endoflame: burns half the furnace time of the fuel, up to 16 000 ticks, and makes 3 mana every 2 ticks. */
    private static final int ENDOFLAME_MAX_BURN = 32_000;
    /** Hydroangeas: one water block burns 40 ticks, 1 mana every 2 ticks. */
    private static final int HYDROANGEAS_TICKS = 40;
    private static final int HYDROANGEAS_MANA = 20;
    /** Thermalily: one lava block burns 900 ticks at 20 mana per tick, then the flower cools down for 6000 ticks. */
    private static final int THERMALILY_TICKS = 900 + 6000;
    private static final int THERMALILY_MANA = 900 * 20;
    /** Energy of one flower in the greenhouse. */
    private static final int GREENHOUSE_EUT = 8;

    /** Skip reason -> count, printed once after the conversion. */
    private static final Map<String, Integer> SKIPPED = new TreeMap<>();

    private GTN_BotaniaRecipesPool() {}

    public static void init() {
        int infusionBefore = size(GTN_Recipe.ManaInfusionRecipes);
        int runeBefore = size(GTN_Recipe.RuneAltarRecipes);
        int daisyBefore = size(GTN_Recipe.PureDaisyRecipes);
        int petalBefore = size(GTN_Recipe.PetalApothecaryRecipes);
        int elvenBefore = size(GTN_Recipe.ElvenTradeRecipes);
        int greenhouseBefore = size(GTN_Recipe.GreenhouseRecipes);

        for (RecipeManaInfusion recipe : BotaniaAPI.manaInfusionRecipes) registerManaInfusion(recipe);
        for (RecipeRuneAltar recipe : BotaniaAPI.runeAltarRecipes) registerRuneAltar(recipe);
        registerTerraPlate();
        for (RecipePureDaisy recipe : BotaniaAPI.pureDaisyRecipes) registerPureDaisy(recipe);
        for (RecipePetals recipe : BotaniaAPI.petalRecipes) registerPetalApothecary(recipe);
        for (RecipeElvenTrade recipe : BotaniaAPI.elvenTradeRecipes) registerElvenTrade(recipe);
        registerGreenhouse();

        GTN_Logger.info(
            "Botania recipes converted: mana infusion %d -> %d, runic altar %d -> %d, pure daisy %d -> %d,"
                + " petal apothecary %d -> %d, elven trade %d -> %d, greenhouse %d, skipped: %s",
            BotaniaAPI.manaInfusionRecipes.size(),
            size(GTN_Recipe.ManaInfusionRecipes) - infusionBefore,
            BotaniaAPI.runeAltarRecipes.size(),
            size(GTN_Recipe.RuneAltarRecipes) - runeBefore,
            BotaniaAPI.pureDaisyRecipes.size(),
            size(GTN_Recipe.PureDaisyRecipes) - daisyBefore,
            BotaniaAPI.petalRecipes.size(),
            size(GTN_Recipe.PetalApothecaryRecipes) - petalBefore,
            BotaniaAPI.elvenTradeRecipes.size(),
            size(GTN_Recipe.ElvenTradeRecipes) - elvenBefore,
            size(GTN_Recipe.GreenhouseRecipes) - greenhouseBefore,
            SKIPPED);
        SKIPPED.clear();
    }

    private static void registerManaInfusion(RecipeManaInfusion recipe) {
        ItemStack output = recipe.getOutput();
        Object input = toInput(recipe.getInput(), 1);
        if (output == null || input == null) {
            skip("mana infusion: no input or output");
            return;
        }
        List<Object> inputs = new ArrayList<>();
        inputs.add(input);
        // the catalyst stays in the input bus, like the catalyst block under a mana pool
        if (recipe.isAlchemy()) inputs.add(GTUtility.copyAmount(0, BotaniaBlocks.AlchemyCatalyst.getItemStack()));
        if (recipe.isConjuration()) {
            inputs.add(GTUtility.copyAmount(0, BotaniaBlocks.ConjurationCatalyst.getItemStack()));
        }
        addChecked(
            GTN_Recipe.ManaInfusionRecipes,
            "mana infusion",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(inputs.toArray())
                .itemOutputs(output.copy())
                .metadata(SimpleMetaData.MANA_COST, recipe.getManaToConsume())
                .eut(RECIPE_MV)
                .duration(20 * 5)
                .addTo(GTN_Recipe.ManaInfusionRecipes));
    }

    private static void registerRuneAltar(RecipeRuneAltar recipe) {
        ItemStack output = recipe.getOutput();
        if (output == null || recipe.getInputs() == null) {
            skip("runic altar: no inputs or output");
            return;
        }
        Map<Object, Integer> counts = countInputs(recipe.getInputs());
        if (counts == null) {
            skip("runic altar: unknown input");
            return;
        }
        List<Object> inputs = new ArrayList<>();
        for (Map.Entry<Object, Integer> entry : counts.entrySet()) {
            Object input = fromKey(entry.getKey());
            // the runic altar gives the runes of the recipe back after the craft
            ItemStack rune = getRune(input);
            inputs.add(rune != null ? GTUtility.copyAmount(0, rune) : toInput(input, entry.getValue()));
        }
        // the livingrock that finishes the craft
        inputs.add(BotaniaBlocks.LivingRock.getItemStack());
        if (inputs.size() > RUNE_ALTAR_ITEM_SLOTS) {
            skip("runic altar: too many different inputs");
            return;
        }
        addChecked(
            GTN_Recipe.RuneAltarRecipes,
            "runic altar",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(inputs.toArray())
                .itemOutputs(output.copy())
                .metadata(SimpleMetaData.MANA_COST, recipe.getManaUsage())
                .eut(RECIPE_HV)
                .duration(20 * 10)
                .addTo(GTN_Recipe.RuneAltarRecipes));
    }

    private static void registerTerraPlate() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                BotaniaItems.ManaSteelIngot.get(1),
                BotaniaItems.ManaPearl.get(1),
                BotaniaItems.ManaDiamond.get(1))
            .itemOutputs(BotaniaItems.TerrasteelIngot.get(1))
            .metadata(SimpleMetaData.MANA_COST, TERRA_PLATE_MANA)
            .eut(RECIPE_EV)
            .duration(20 * 20)
            .addTo(GTN_Recipe.TerraPlateRecipes);
    }

    private static void registerPureDaisy(RecipePureDaisy recipe) {
        Object input = recipe.getInput() instanceof Block block ? toBlockStack(block, GTValues.W) : recipe.getInput();
        ItemStack output = toBlockStack(recipe.getOutput(), recipe.getOutputMeta());
        Object itemInput = toInput(input, 1);
        // blocks without an item (water, lava) cannot go through an input bus
        if (output == null || itemInput == null) {
            skip("pure daisy: block without item");
            return;
        }
        addChecked(
            GTN_Recipe.PureDaisyRecipes,
            "pure daisy",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(itemInput)
                .itemOutputs(output)
                .eut(RECIPE_LV)
                .duration(PURE_DAISY_TICKS)
                .addTo(GTN_Recipe.PureDaisyRecipes));
    }

    private static void registerPetalApothecary(RecipePetals recipe) {
        ItemStack output = recipe.getOutput();
        Map<Object, Integer> counts = countInputs(recipe.getInputs());
        if (output == null || counts == null) {
            skip("petal apothecary: unknown input or no output");
            return;
        }
        List<Object> inputs = toInputs(counts);
        // the seeds that finish the craft
        inputs.add(new ItemStack(Items.wheat_seeds));
        if (inputs.size() > PETAL_APOTHECARY_ITEM_SLOTS) {
            skip("petal apothecary: too many different inputs");
            return;
        }
        addChecked(
            GTN_Recipe.PetalApothecaryRecipes,
            "petal apothecary",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(inputs.toArray())
                .fluidInputs(Materials.Water.getFluid(1_000))
                .itemOutputs(output.copy())
                .eut(RECIPE_LV)
                .duration(20 * 5)
                .addTo(GTN_Recipe.PetalApothecaryRecipes));
    }

    private static void registerElvenTrade(RecipeElvenTrade recipe) {
        ItemStack output = recipe.getOutput();
        Map<Object, Integer> counts = countInputs(recipe.getInputs());
        if (output == null || counts == null) {
            skip("elven trade: unknown input or no output");
            return;
        }
        List<Object> inputs = toInputs(counts);
        if (inputs.size() > ELVEN_TRADE_ITEM_SLOTS) {
            skip("elven trade: too many different inputs");
            return;
        }
        addChecked(
            GTN_Recipe.ElvenTradeRecipes,
            "elven trade",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(inputs.toArray())
                .itemOutputs(output.copy())
                .eut(RECIPE_EV)
                .duration(20 * 5)
                .addTo(GTN_Recipe.ElvenTradeRecipes));
    }

    private static void registerGreenhouse() {
        ItemStack endoflame = ItemBlockSpecialFlower.ofType(LibBlockNames.SUBTILE_ENDOFLAME);
        Object[] fuels = { new ItemStack(Items.coal, 1, 0), new ItemStack(Items.coal, 1, 1),
            new ItemStack(Blocks.coal_block), new ItemStack(Items.blaze_rod), "logWood", "plankWood", "stickWood" };
        for (Object fuel : fuels) registerEndoflame(endoflame, fuel);

        // passive flowers wither after the Botania decay time, like a flower placed in the world
        int decay = Math.max(0, BotaniaAPI.internalHandler.getPassiveFlowerDecay());
        addGreenhouse(
            ItemBlockSpecialFlower.ofType(LibBlockNames.SUBTILE_HYDROANGEAS),
            null,
            Materials.Water.getFluid(1_000),
            HYDROANGEAS_TICKS,
            HYDROANGEAS_MANA,
            decay);
        addGreenhouse(
            ItemBlockSpecialFlower.ofType(LibBlockNames.SUBTILE_THERMALILY),
            null,
            Materials.Lava.getFluid(1_000),
            THERMALILY_TICKS,
            THERMALILY_MANA,
            0);
    }

    private static void registerEndoflame(ItemStack flower, Object fuel) {
        ItemStack sample = fuel instanceof String oreName ? getFirstOre(oreName) : (ItemStack) fuel;
        int burnTicks = sample == null ? 0
            : Math.min(ENDOFLAME_MAX_BURN, TileEntityFurnace.getItemBurnTime(sample)) / 2;
        if (burnTicks <= 0) {
            skip("greenhouse: fuel without burn time");
            return;
        }
        addGreenhouse(flower, toInput(fuel, 1), null, burnTicks, burnTicks * 3 / 2, 0);
    }

    /** The flower stays in the input bus, a lifetime above 0 makes it wither after that many ticks of work. */
    private static void addGreenhouse(ItemStack flower, Object itemFuel, FluidStack fluidFuel, int ticks, int mana,
        int lifetime) {
        List<Object> inputs = new ArrayList<>();
        inputs.add(GTUtility.copyAmount(0, flower));
        if (itemFuel != null) inputs.add(itemFuel);
        addChecked(GTN_Recipe.GreenhouseRecipes, "greenhouse", () -> {
            GTRecipeBuilder builder = GTValues.RA.stdBuilder()
                .itemInputs(inputs.toArray())
                .metadata(SimpleMetaData.MANA_OUTPUT, mana)
                .nbtSensitive()
                .eut(GREENHOUSE_EUT)
                .duration(ticks);
            if (fluidFuel != null) builder.fluidInputs(fluidFuel);
            if (lifetime > 0) builder.metadata(SimpleMetaData.FLOWER_LIFETIME, lifetime);
            builder.addTo(GTN_Recipe.GreenhouseRecipes);
        });
    }

    private static ItemStack getFirstOre(String oreName) {
        List<ItemStack> ores = OreDictionary.getOres(oreName);
        return ores.isEmpty() ? null : ores.get(0);
    }

    /** Botania inputs are one item each, identical ones are counted together. Null if an input is unknown. */
    private static Map<Object, Integer> countInputs(List<Object> botaniaInputs) {
        if (botaniaInputs == null) return null;
        Map<Object, Integer> counts = new LinkedHashMap<>();
        for (Object input : botaniaInputs) {
            Object key = toKey(input);
            if (key == null) return null;
            counts.merge(key, 1, Integer::sum);
        }
        return counts;
    }

    private static List<Object> toInputs(Map<Object, Integer> counts) {
        List<Object> inputs = new ArrayList<>();
        for (Map.Entry<Object, Integer> entry : counts.entrySet()) {
            inputs.add(toInput(fromKey(entry.getKey()), entry.getValue()));
        }
        return inputs;
    }

    private static ItemStack toBlockStack(Block block, int meta) {
        Item item = block == null ? null : Item.getItemFromBlock(block);
        return item == null ? null : new ItemStack(item, 1, meta);
    }

    /** Ore names stay strings, items are compared by item, damage and NBT. */
    private static Object toKey(Object input) {
        if (input instanceof String) return input;
        if (input instanceof ItemStack stack && stack.getItem() != null) return new StackKey(stack);
        return null;
    }

    private static Object fromKey(Object key) {
        return key instanceof StackKey stackKey ? stackKey.stack : key;
    }

    /** The rune an input stands for (an item or an ore name of runes), null if it is not a rune. */
    private static ItemStack getRune(Object input) {
        if (input instanceof ItemStack stack) return stack.getItem() == ModItems.rune ? stack : null;
        if (input instanceof String oreName) {
            for (ItemStack ore : OreDictionary.getOres(oreName)) {
                if (ore != null && ore.getItem() == ModItems.rune) return ore;
            }
        }
        return null;
    }

    /** GT recipe input: an item stack or {@code {oreName, count}}. */
    private static Object toInput(Object input, int count) {
        if (input instanceof String oreName) return new Object[] { oreName, count };
        if (input instanceof ItemStack stack && stack.getItem() != null) return GTUtility.copyAmount(count, stack);
        return null;
    }

    /** Adds a recipe and records it if GT rejected it while building. */
    private static void addChecked(RecipeMap<?> map, String kind, Runnable adder) {
        int before = size(map);
        try {
            adder.run();
        } catch (RuntimeException e) {
            skip(
                kind + ": "
                    + e.getClass()
                        .getSimpleName());
            return;
        }
        if (size(map) == before) skip(kind + ": rejected by GT");
    }

    private static int size(RecipeMap<?> map) {
        return map.getAllRecipes()
            .size();
    }

    private static void skip(String reason) {
        SKIPPED.merge(reason, 1, Integer::sum);
    }

    private static final class StackKey {

        final ItemStack stack;

        StackKey(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof StackKey key && key.stack.getItem() == stack.getItem()
                && key.stack.getItemDamage() == stack.getItemDamage()
                && ItemStack.areItemStackTagsEqual(key.stack, stack);
        }

        @Override
        public int hashCode() {
            return 31 * stack.getItem()
                .hashCode() + stack.getItemDamage();
        }
    }
}
