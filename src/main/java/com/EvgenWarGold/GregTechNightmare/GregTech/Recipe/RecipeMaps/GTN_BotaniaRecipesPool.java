package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import static gregtech.api.enums.TierEU.RECIPE_EV;
import static gregtech.api.enums.TierEU.RECIPE_HV;
import static gregtech.api.enums.TierEU.RECIPE_MV;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.EvgenWarGold.GregTechNightmare.GTN_Logger;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData.SimpleMetaData;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.BotaniaBlocks;
import com.EvgenWarGold.GregTechNightmare.ModItems.BotaniaItems;

import gregtech.api.enums.GTValues;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeManaInfusion;
import vazkii.botania.api.recipe.RecipeRuneAltar;
import vazkii.botania.common.item.ModItems;

/** Converts the Botania mana recipes into the recipe maps of the Botania machines. */
public final class GTN_BotaniaRecipesPool {

    /** Mana of the Terrestrial Agglomeration Plate, hardcoded in Botania. */
    private static final int TERRA_PLATE_MANA = 500_000;

    /** Item slots of the runic altar recipe map in NEI, see {@link GTN_Recipe#RuneAltarRecipes}. */
    private static final int RUNE_ALTAR_ITEM_SLOTS = 12;

    /** Skip reason -> count, printed once after the conversion. */
    private static final Map<String, Integer> SKIPPED = new TreeMap<>();

    private GTN_BotaniaRecipesPool() {}

    public static void init() {
        int infusionBefore = size(GTN_Recipe.ManaInfusionRecipes);
        int runeBefore = size(GTN_Recipe.RuneAltarRecipes);

        for (RecipeManaInfusion recipe : BotaniaAPI.manaInfusionRecipes) registerManaInfusion(recipe);
        for (RecipeRuneAltar recipe : BotaniaAPI.runeAltarRecipes) registerRuneAltar(recipe);
        registerTerraPlate();

        GTN_Logger.info(
            "Botania recipes converted: mana infusion %d -> %d, runic altar %d -> %d, skipped: %s",
            BotaniaAPI.manaInfusionRecipes.size(),
            size(GTN_Recipe.ManaInfusionRecipes) - infusionBefore,
            BotaniaAPI.runeAltarRecipes.size(),
            size(GTN_Recipe.RuneAltarRecipes) - runeBefore,
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
        // Botania inputs are one item each, identical ones are merged into one stack
        Map<Object, Integer> counts = new LinkedHashMap<>();
        for (Object input : recipe.getInputs()) {
            Object key = toKey(input);
            if (key == null) {
                skip("runic altar: unknown input");
                return;
            }
            counts.merge(key, 1, Integer::sum);
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
