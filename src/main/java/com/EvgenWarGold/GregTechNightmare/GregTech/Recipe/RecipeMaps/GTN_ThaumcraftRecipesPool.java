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
import gregtech.api.util.GTUtility;
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
        List<PendingRecipe> crucibleRecipes = new ArrayList<>();
        List<PendingRecipe> infusionRecipes = new ArrayList<>();
        int crucibleSource = 0;
        int infusionSource = 0;
        int crucibleBefore = size(GTN_Recipe.IndustrialCrucibleRecipes);
        int infusionBefore = size(GTN_Recipe.AutomatedInfusionRecipes);

        for (Object entry : ThaumcraftApi.getCraftingRecipes()) {
            if (entry instanceof CrucibleRecipe crucible) {
                crucibleSource++;
                registerCrucible(crucible, crucibleRecipes);
            } else if (entry instanceof InfusionRecipe infusion) {
                infusionSource++;
                registerInfusion(infusion, infusionRecipes);
            }
        }

        registerWithCircuits(crucibleRecipes, GTN_Recipe.IndustrialCrucibleRecipes, "crucible", 480, 20 * 16);
        registerWithCircuits(infusionRecipes, GTN_Recipe.AutomatedInfusionRecipes, "infusion", 7680, 20 * 40);

        GTN_Logger.info(
            "Thaumcraft recipes converted: crucible %d -> %d, infusion %d -> %d, skipped: %s",
            crucibleSource,
            size(GTN_Recipe.IndustrialCrucibleRecipes) - crucibleBefore,
            infusionSource,
            size(GTN_Recipe.AutomatedInfusionRecipes) - infusionBefore,
            SKIPPED);
        SKIPPED.clear();
    }

    private static void registerCrucible(CrucibleRecipe recipe, List<PendingRecipe> recipes) {
        ItemStack output = recipe.getRecipeOutput();
        if (output == null || recipe.aspects == null) {
            skip("crucible: no output or aspects");
            return;
        }
        Object catalyst = recipe.catalyst;
        PendingRecipe pending = new PendingRecipe(output, recipe.aspects, recipe.key);
        if (catalyst instanceof ItemStack item) {
            addCrucibleInput(pending, item);
        } else if (catalyst instanceof List<?>options) {
            for (Object option : options) {
                if (option instanceof ItemStack item) addCrucibleInput(pending, item);
            }
        } else {
            skip(
                "crucible: catalyst " + (catalyst == null ? "null"
                    : catalyst.getClass()
                        .getSimpleName()));
        }
        if (!pending.inputs.isEmpty()) recipes.add(pending);
    }

    private static void addCrucibleInput(PendingRecipe pending, ItemStack catalyst) {
        if (catalyst.stackSize <= 0) {
            skip("crucible: empty catalyst");
            return;
        }
        pending.inputs.add(new ItemStack[] { catalyst.copy() });
    }

    private static void registerInfusion(InfusionRecipe recipe, List<PendingRecipe> recipes) {
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
        if (inputs.size() > GTN_EssentiaRecipeFrontend.INFUSION_ITEM_SLOTS - 1) {
            skip("infusion: too many different inputs");
            return;
        }
        PendingRecipe pending = new PendingRecipe(output, recipe.getAspects(), recipe.getResearch());
        pending.inputs.add(inputs.toArray(new ItemStack[0]));
        recipes.add(pending);
    }

    private static final class PendingRecipe {

        private final List<ItemStack[]> inputs = new ArrayList<>();
        private final ItemStack output;
        private final AspectList aspects;
        private final String research;
        private int circuit;

        private PendingRecipe(ItemStack output, AspectList aspects, String research) {
            this.output = output;
            this.aspects = aspects;
            this.research = research == null ? "" : research;
        }
    }

    private static void registerWithCircuits(List<PendingRecipe> recipes, RecipeMap<?> map, String kind, int eut,
        int duration) {
        // Color the conflict graph before registering anything: both sides must require a circuit.
        // Alternative catalysts of the same Thaumcraft recipe always use the same configuration.
        boolean[][] conflicts = new boolean[recipes.size()][recipes.size()];
        for (int i = 0; i < recipes.size(); i++) {
            for (int j = 0; j < i; j++) {
                conflicts[i][j] = conflicts[j][i] = conflicts(recipes.get(i), recipes.get(j));
            }
        }
        int assigned = 0;
        for (int i = 0; i < recipes.size(); i++) {
            boolean hasConflict = false;
            boolean[] used = new boolean[25];
            for (int j = 0; j < recipes.size(); j++) {
                if (!conflicts[i][j]) continue;
                hasConflict = true;
                used[recipes.get(j).circuit] = true;
            }
            if (!hasConflict) continue;
            int circuit = 1;
            while (circuit <= 24 && used[circuit]) circuit++;
            if (circuit > 24) {
                throw new IllegalStateException(
                    "Too many conflicting Thaumcraft " + kind + " recipes for circuits 1-24");
            }
            recipes.get(i).circuit = circuit;
            assigned++;
        }
        for (PendingRecipe recipe : recipes) {
            for (ItemStack[] variant : recipe.inputs) {
                List<ItemStack> inputs = new ArrayList<>();
                for (ItemStack item : variant) inputs.add(item.copy());
                if (recipe.circuit > 0) inputs.add(GTUtility.getIntegratedCircuit(recipe.circuit));
                addChecked(
                    map,
                    kind,
                    () -> GTValues.RA.stdBuilder()
                        .itemInputs(inputs.toArray(new ItemStack[0]))
                        .itemOutputs(recipe.output.copy())
                        .metadata(SimpleMetaData.RESEARCH_KEY, recipe.research)
                        .metadata(SimpleMetaData.ASPECT_COST, recipe.aspects)
                        .eut(eut)
                        .duration(duration)
                        .addTo(map));
            }
        }
        GTN_Logger.info("Thaumcraft %s recipes requiring selector circuits: %d", kind, assigned);
    }

    private static boolean conflicts(PendingRecipe first, PendingRecipe second) {
        for (ItemStack[] a : first.inputs) {
            for (ItemStack[] b : second.inputs) {
                if (containsInputTypes(a, b) || containsInputTypes(b, a)) return true;
            }
        }
        return false;
    }

    private static boolean containsInputTypes(ItemStack[] available, ItemStack[] required) {
        // Counts cannot disambiguate batches. Ignore NBT conservatively, as ordinary GT recipes do,
        // and account for ore unification and wildcard damage when comparing ingredient types.
        for (ItemStack ingredient : required) {
            boolean found = false;
            for (ItemStack input : available) {
                if (GTUtility.areUnificationsEqual(input, ingredient, true)) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
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
