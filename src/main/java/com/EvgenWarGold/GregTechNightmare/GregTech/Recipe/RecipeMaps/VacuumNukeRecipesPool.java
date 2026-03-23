package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import java.text.DecimalFormat;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData.SimpleMetaData;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtPlusPlus.xmod.thermalfoundation.fluid.TFFluids;
import gtnhlanth.common.register.WerkstoffMaterialPool;

public class VacuumNukeRecipesPool {

    private final static DecimalFormat df = new DecimalFormat("0.#");
    private final static int ROD_COUNT = 40;
    private static FluidStack IC2_COOLANT;
    private static FluidStack HOT_IC2_COOLANT;
    private static FluidStack SUPER_COOLANT;
    private static FluidStack HOT_SUPER_COOLANT;
    private static FluidStack GELID_CRYOTHEUM;

    public static void getFluids() {
        IC2_COOLANT = FluidRegistry.getFluidStack("ic2coolant", 0);
        HOT_IC2_COOLANT = FluidRegistry.getFluidStack("ic2hotcoolant", 0);
        SUPER_COOLANT = Materials.SuperCoolant.getFluid(0);
        HOT_SUPER_COOLANT = WerkstoffMaterialPool.HotSuperCoolant.getFluidOrGas(0);
        GELID_CRYOTHEUM = new FluidStack(TFFluids.fluidCryotheum, 0);
    }

    public static void init() {
        getFluids();

        addVacuumNukeRecipe(
            ItemList.RodThorium4,
            ItemList.DepletedRodThorium4,
            IC2_COOLANT,
            HOT_IC2_COOLANT,
            50_000,
            1,
            9_000,
            0.6);

        addVacuumNukeRecipe(
            ItemList.RodUranium4,
            ItemList.DepletedRodUranium4,
            IC2_COOLANT,
            HOT_IC2_COOLANT,
            20_000,
            2,
            44_000,
            1.4);

        addVacuumNukeRecipe(
            ItemList.RodMOX4,
            ItemList.DepletedRodMOX4,
            IC2_COOLANT,
            HOT_IC2_COOLANT,
            10_000,
            2,
            100_000,
            2);

        addVacuumNukeRecipe(
            ItemList.RodHighDensityUranium4,
            ItemList.DepletedRodHighDensityUranium4,
            IC2_COOLANT,
            HOT_IC2_COOLANT,
            70_000,
            2,
            87_000,
            3);

        addVacuumNukeRecipe(
            ItemList.RodHighDensityPlutonium4,
            ItemList.DepletedRodHighDensityUranium4,
            SUPER_COOLANT,
            HOT_SUPER_COOLANT,
            70_000,
            2,
            268_000,
            5);

        addVacuumNukeRecipe(
            ItemList.RodExcitedUranium4,
            ItemList.DepletedRodExcitedPlutonium4,
            SUPER_COOLANT,
            HOT_SUPER_COOLANT,
            6_000,
            3,
            1_000_000,
            10);

        addVacuumNukeRecipe(
            ItemList.RodExcitedPlutonium4,
            ItemList.DepletedRodExcitedPlutonium4,
            GELID_CRYOTHEUM,
            null,
            10_000,
            3,
            3_700_000,
            16);

        addVacuumNukeRecipe(
            ItemList.RodNaquadah4,
            ItemList.DepletedRodNaquadah4,
            IC2_COOLANT,
            HOT_IC2_COOLANT,
            100_000,
            2,
            87_000,
            3);

        addVacuumNukeRecipe(
            ItemList.RodNaquadah32,
            ItemList.DepletedRodNaquadah32,
            GELID_CRYOTHEUM,
            null,
            100_000,
            3,
            5_000_000,
            20);

        addVacuumNukeRecipe(
            ItemList.RodNaquadria4,
            ItemList.DepletedRodNaquadria4,
            SUPER_COOLANT,
            HOT_SUPER_COOLANT,
            100_000,
            2,
            200_000,
            6);

        addVacuumNukeRecipe(
            ItemList.RodTiberium4,
            ItemList.DepletedRodTiberium4,
            IC2_COOLANT,
            HOT_IC2_COOLANT,
            50_000,
            1,
            44_000,
            1.4);
    }

    private static void addVacuumNukeRecipe(ItemList input, ItemList output, FluidStack fluidInput,
        FluidStack fluidOutput, int duration, int tier, int eu, double temperature) {
        GTRecipeBuilder recipeFake = GTValues.RA.stdBuilder();

        recipeFake.itemInputs(input.get(ROD_COUNT))
            .itemOutputs(output.get(ROD_COUNT))
            .eut(0)
            .duration(duration * 20)
            .fake()
            .setNEIDesc(
                GTN_Utils.tr("gtn.recipe.utils.multiblock_tier." + tier),
                GTN_Utils.tr("gtn.recipe.utils.generating_eu", GTUtility.formatNumbers(eu)),
                GTN_Utils.tr("gtn.recipe.utils.temperature_increase", df.format(temperature)));

        if (fluidInput != null && fluidInput.getFluid() != null) {
            recipeFake.fluidInputs(fluidInput);
        }

        if (fluidOutput != null && fluidOutput.getFluid() != null) {
            recipeFake.fluidOutputs(fluidOutput);
        }

        recipeFake.addTo(GTN_Recipe.VacuumNukeRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(input.get(ROD_COUNT))
            .itemOutputs(output.get(ROD_COUNT))
            .eut(0)
            .duration(duration * 20)
            .metadata(SimpleMetaData.MULTIBLOCK_TIER, tier)
            .metadata(SimpleMetaData.GENERATING_EU, eu)
            .metadata(SimpleMetaData.FLUID_INPUT, fluidInput)
            .metadata(SimpleMetaData.FLUID_OUTPUT, fluidOutput)
            .metadata(SimpleMetaData.TEMPERATURE_INCREASE, temperature)
            .hidden()
            .addTo(GTN_Recipe.VacuumNukeRecipes);
    }
}
