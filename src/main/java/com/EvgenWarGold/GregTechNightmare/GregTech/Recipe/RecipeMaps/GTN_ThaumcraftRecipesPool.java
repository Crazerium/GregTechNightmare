package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GTN_Logger;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData.SimpleMetaData;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMapFrontends.GTN_EssentiaRecipeFrontend;
import com.google.common.collect.ImmutableSet;

import gregtech.api.enums.GTValues;
import gregtech.api.recipe.RecipeMap;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;

public final class GTN_ThaumcraftRecipesPool {

    /** Skip reason -> count, printed once after the conversion to see which Thaumcraft recipes were not converted. */
    private static final Map<String, Integer> SKIPPED = new TreeMap<>();

    /**
     * Infusion recipes that only turn ingredients into an item. GTNH registers most recipes through the enhanced
     * classes (alternative ingredients, exact NBT match); their base fields still hold the default ingredients. Other
     * subclasses modify the NBT of the central item (upgrades, augments, familiars) and cannot be converted.
     */
    private static final Set<String> PLAIN_INFUSION_CLASSES = ImmutableSet
        .of("InfusionRecipe", "EnhancedInfusionRecipe", "NonFuzzyInfusionRecipe");

    private GTN_ThaumcraftRecipesPool() {}

    public static void init() {
        int crucibleSource = 0;
        int infusionSource = 0;
        int crucibleBefore = size(GTN_Recipe.IndustrialCrucibleRecipes);
        int infusionBefore = size(GTN_Recipe.AutomatedInfusionRecipes);

        for (Object entry : ThaumcraftApi.getCraftingRecipes()) {
            if (entry instanceof CrucibleRecipe crucible) {
                crucibleSource++;
                registerCrucible(crucible);
            } else if (entry instanceof InfusionRecipe infusion) {
                infusionSource++;
                registerInfusion(infusion);
            }
        }

        GTN_Logger.info(
            "Thaumcraft recipes converted: crucible %d -> %d, infusion %d -> %d, skipped: %s",
            crucibleSource,
            size(GTN_Recipe.IndustrialCrucibleRecipes) - crucibleBefore,
            infusionSource,
            size(GTN_Recipe.AutomatedInfusionRecipes) - infusionBefore,
            SKIPPED);
        SKIPPED.clear();
    }

    private static void registerCrucible(CrucibleRecipe recipe) {
        ItemStack output = recipe.getRecipeOutput();
        if (output == null || recipe.aspects == null) {
            skip("crucible: no output or aspects");
            return;
        }
        Object catalyst = recipe.catalyst;
        if (catalyst instanceof ItemStack item) {
            addCrucibleRecipe(item, output, recipe.aspects, recipe.key);
        } else if (catalyst instanceof List<?>options) {
            for (Object option : options) {
                if (option instanceof ItemStack item) addCrucibleRecipe(item, output, recipe.aspects, recipe.key);
            }
        } else {
            skip(
                "crucible: catalyst " + (catalyst == null ? "null"
                    : catalyst.getClass()
                        .getSimpleName()));
        }
    }

    private static void addCrucibleRecipe(ItemStack catalyst, ItemStack output, AspectList aspects, String research) {
        if (catalyst.stackSize <= 0) {
            skip("crucible: empty catalyst");
            return;
        }
        addChecked(
            GTN_Recipe.IndustrialCrucibleRecipes,
            "crucible",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(catalyst.copy())
                .itemOutputs(output.copy())
                .metadata(SimpleMetaData.RESEARCH_KEY, research == null ? "" : research)
                .metadata(SimpleMetaData.ASPECT_COST, aspects)
                .eut(480)
                .duration(20 * 16)
                .addTo(GTN_Recipe.IndustrialCrucibleRecipes));
    }

    private static void registerInfusion(InfusionRecipe recipe) {
        if (!PLAIN_INFUSION_CLASSES.contains(
            recipe.getClass()
                .getSimpleName())) {
            skip(
                "infusion: subclass " + recipe.getClass()
                    .getSimpleName());
            return;
        }
        if (!(recipe.getRecipeOutput() instanceof ItemStack output)) {
            skip("infusion: output is not an item");
            return;
        }
        if (recipe.getRecipeInput() == null || recipe.getRecipeInput().stackSize <= 0 || recipe.getAspects() == null) {
            skip("infusion: no center item or aspects");
            return;
        }
        List<ItemStack> inputs = new ArrayList<>();
        inputs.add(
            recipe.getRecipeInput()
                .copy());
        ItemStack[] components = recipe.getComponents();
        if (components == null) {
            skip("infusion: no components");
            return;
        }
        for (ItemStack component : components) {
            if (component == null || component.stackSize <= 0) {
                skip("infusion: empty component");
                return;
            }
            inputs.add(component.copy());
        }
        inputs = mergeIdenticalInputs(inputs);
        if (inputs.size() > GTN_EssentiaRecipeFrontend.INFUSION_ITEM_SLOTS) {
            skip("infusion: too many different inputs");
            return;
        }
        ItemStack[] itemInputs = inputs.toArray(new ItemStack[0]);
        addChecked(
            GTN_Recipe.AutomatedInfusionRecipes,
            "infusion",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(itemInputs)
                .itemOutputs(output.copy())
                .metadata(SimpleMetaData.RESEARCH_KEY, recipe.getResearch() == null ? "" : recipe.getResearch())
                .metadata(SimpleMetaData.ASPECT_COST, recipe.getAspects())
                .eut(7680)
                .duration(20 * 40)
                .addTo(GTN_Recipe.AutomatedInfusionRecipes));
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

    private static List<ItemStack> mergeIdenticalInputs(List<ItemStack> inputs) {
        List<ItemStack> merged = new ArrayList<>();
        for (ItemStack input : inputs) {
            if (input == null || input.stackSize <= 0) continue;
            boolean found = false;
            for (ItemStack stack : merged) {
                if (stack.isItemEqual(input) && ItemStack.areItemStackTagsEqual(stack, input)
                    && (long) stack.stackSize + input.stackSize <= Integer.MAX_VALUE) {
                    stack.stackSize += input.stackSize;
                    found = true;
                    break;
                }
            }
            if (!found) merged.add(input.copy());
        }
        return merged;
    }
}
