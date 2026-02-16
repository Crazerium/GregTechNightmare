package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.util.GTOreDictUnificator;

public class AdvancedBBFRecipesPool {

    private static final List<DataFuels> fuels = new ArrayList<>();
    private static final ItemStack iron;
    private static final ItemStack wroughtIron;
    private static final ItemStack steel;
    private static final int DURATION = 10 * 20;

    static {
        // spotless:off
        iron = GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Iron, 1L);
        wroughtIron = GTOreDictUnificator.get(OrePrefixes.ingot, Materials.WroughtIron, 1L);
        steel = GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Steel, 1L);

        fuels.add(new DataFuels(
            GTOreDictUnificator.get(OrePrefixes.gem, Materials.Coal, 1L),
            1,
            1
        ));

        fuels.add(new DataFuels(
            GTOreDictUnificator.get(OrePrefixes.gem, Materials.Charcoal, 1L),
            1,
            1
        ));

        fuels.add(new DataFuels(
            GTOreDictUnificator.get(OrePrefixes.gem, Materials.Diamond, 1L),
            40,
            64
        ));

        fuels.add(new DataFuels(
            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal,1L),
            1,
            1
        ));

        fuels.add(new DataFuels(
            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Charcoal,1L),
            1,
            1
        ));

        fuels.add(new DataFuels(
            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond,1L),
            40,
            64
        ));

        fuels.add(new DataFuels(
            GTOreDictUnificator.get(OrePrefixes.block, Materials.Coal, 1L),
            8,
            10
        ));

        fuels.add(new DataFuels(
            GTOreDictUnificator.get(OrePrefixes.block, Materials.Charcoal, 1L),
            8,
            10
        ));
        //spotless:on
    }

    public static void init() {
        final IRecipeMap AdvancedBBF = GTN_Recipe.AdvancedBBFRecipes;

        for (DataFuels fuel : fuels) {
            iron.stackSize = fuel.modifierOutput;
            wroughtIron.stackSize = fuel.modifierOutput;
            steel.stackSize = fuel.modifierOutput;

            GTValues.RA.stdBuilder()
                .itemInputs(iron, fuel.itemStack)
                .itemOutputs(wroughtIron)
                .eut(0)
                .duration(DURATION * fuel.modifierDuration)
                .addTo(AdvancedBBF);

            GTValues.RA.stdBuilder()
                .itemInputs(wroughtIron, fuel.itemStack)
                .itemOutputs(steel)
                .eut(0)
                .duration(DURATION * fuel.modifierDuration)
                .addTo(AdvancedBBF);
        }
    }

    private static final class DataFuels {

        public final ItemStack itemStack;
        public final int modifierDuration;
        public final int modifierOutput;

        public DataFuels(ItemStack itemStack, int modifierDuration, int modifierOutput) {
            this.itemStack = itemStack;
            this.modifierDuration = modifierDuration;
            this.modifierOutput = modifierOutput;
        }
    }
}
