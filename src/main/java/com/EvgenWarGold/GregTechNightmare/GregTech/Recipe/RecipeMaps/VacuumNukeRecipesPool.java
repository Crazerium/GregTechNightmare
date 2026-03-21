package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;

import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData.SimpleMetaData;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;

public class VacuumNukeRecipesPool {

    public static void init() {
        addVacuumNukeRecipe(ItemList.RodThorium4.get(48), ItemList.DepletedRodThorium4.get(48), 50_000, 1, 9_000);

        addVacuumNukeRecipe(ItemList.RodUranium4.get(48), ItemList.DepletedRodUranium4.get(48), 20_000, 2, 44_000);

        addVacuumNukeRecipe(ItemList.RodMOX4.get(48), ItemList.DepletedRodMOX4.get(48), 10_000, 2, 100_000);

        addVacuumNukeRecipe(
            ItemList.RodHighDensityUranium4.get(48),
            ItemList.DepletedRodHighDensityUranium4.get(48),
            70_000,
            2,
            87_000);

        addVacuumNukeRecipe(
            ItemList.RodHighDensityPlutonium4.get(48),
            ItemList.DepletedRodHighDensityUranium4.get(48),
            70_000,
            2,
            268_000);

        addVacuumNukeRecipe(
            ItemList.RodExcitedUranium4.get(48),
            ItemList.DepletedRodExcitedPlutonium4.get(48),
            6_000,
            3,
            1_000_000);

        addVacuumNukeRecipe(
            ItemList.RodExcitedPlutonium4.get(48),
            ItemList.DepletedRodExcitedPlutonium4.get(48),
            10_000,
            3,
            3_700_000);

        addVacuumNukeRecipe(ItemList.RodNaquadah4.get(48), ItemList.DepletedRodNaquadah4.get(48), 100_000, 2, 87_000);

        addVacuumNukeRecipe(
            ItemList.RodNaquadah32.get(48),
            ItemList.DepletedRodNaquadah32.get(48),
            100_000,
            3,
            5_000_000);

        addVacuumNukeRecipe(
            ItemList.RodNaquadria4.get(48),
            ItemList.DepletedRodNaquadria4.get(48),
            100_000,
            2,
            200_000);

        addVacuumNukeRecipe(ItemList.RodTiberium4.get(48), ItemList.DepletedRodTiberium4.get(48), 50_000, 1, 44_000);
    }

    private static void addVacuumNukeRecipe(ItemStack input, ItemStack output, int duration, int tier, int eu) {
        GTValues.RA.stdBuilder()
            .itemInputs(input)
            .itemOutputs(output)
            .eut(0)
            .duration(duration * 20)
            .metadata(SimpleMetaData.MULTIBLOCK_TIER, tier)
            .metadata(SimpleMetaData.GENERATING_EU, eu)
            .setNEIDesc(
                GTN_Utils.tr("gtn.recipe.utils.multiblock_tier." + tier),
                GTN_Utils.tr("gtn.recipe.utils.generating_eu", formatNumber(eu)))
            .addTo(GTN_Recipe.VacuumNukeRecipes);
    }
}
