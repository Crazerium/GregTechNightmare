package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.IRecipeMap;

public class AdvancedBBFRecipesPool {

    private static final List<DataFuels> fuels = new ArrayList<>();
    private static final List<DataMaterial> materials = new ArrayList<>();
    private static final ItemStack iron;
    private static final ItemStack wroughtIron;
    private static final ItemStack steel;
    private static final int DURATION = 10 * 20;

    static {
        // spotless:off
        iron = GTN_OreDict.getIngot(Materials.Iron);
        wroughtIron = GTN_OreDict.getIngot(Materials.WroughtIron);
        steel = GTN_OreDict.getIngot(Materials.Steel);

        fuels.add(new DataFuels(
            GTN_OreDict.getGem(Materials.Coal),
            1,
            1,
            false
        ));

        fuels.add(new DataFuels(
            GTN_OreDict.getGem(Materials.Charcoal),
            1,
            1,
            false
        ));

        fuels.add(new DataFuels(
            GTN_OreDict.getGem(Materials.Diamond),
            40,
            64,
            true
        ));

        fuels.add(new DataFuels(
            GTN_OreDict.getDust(Materials.Coal),
            1,
            1,
            false
        ));

        fuels.add(new DataFuels(
            GTN_OreDict.getDust(Materials.Charcoal),
            1,
            1,
            false
        ));

        fuels.add(new DataFuels(
            GTN_OreDict.getDust(Materials.Diamond),
            40,
            64,
            true
        ));

        fuels.add(new DataFuels(
            GTN_OreDict.getBlock(Materials.Coal),
            8,
            10,
            false
        ));

        fuels.add(new DataFuels(
            GTN_OreDict.getBlock(Materials.Charcoal),
            8,
            10,
            false
        ));

        materials.add(new DataMaterial(Materials.Garnierite, Materials.Nickel));
        materials.add(new DataMaterial(Materials.BandedIron, Materials.Iron));
        materials.add(new DataMaterial(Materials.Tetrahedrite, Materials.Copper));
        materials.add(new DataMaterial(Materials.BasalticMineralSand, Materials.Iron));
        materials.add(new DataMaterial(Materials.Stibnite, Materials.Antimony));
        materials.add(new DataMaterial(Materials.Malachite, Materials.Copper));
        materials.add(new DataMaterial(Materials.Cassiterite, Materials.Tin));
        materials.add(new DataMaterial(Materials.Magnetite, Materials.Iron));
        materials.add(new DataMaterial(Materials.Sphalerite, Materials.Zinc));
        materials.add(new DataMaterial(Materials.Pyrite, Materials.Iron));
        materials.add(new DataMaterial(Materials.Galena, Materials.Lead));
        materials.add(new DataMaterial(Materials.Pentlandite, Materials.Nickel));
        materials.add(new DataMaterial(Materials.BrownLimonite, Materials.Iron));
        materials.add(new DataMaterial(Materials.YellowLimonite, Materials.Iron));
        
        materials.add(new DataMaterial(Materials.RoastedCopper, Materials.Copper));
        materials.add(new DataMaterial(Materials.RoastedAntimony, Materials.Antimony));
        materials.add(new DataMaterial(Materials.RoastedIron, Materials.Iron));
        materials.add(new DataMaterial(Materials.RoastedNickel, Materials.Nickel));
        materials.add(new DataMaterial(Materials.RoastedZinc, Materials.Zinc));
        materials.add(new DataMaterial(Materials.RoastedCobalt, Materials.Cobalt));
        materials.add(new DataMaterial(Materials.RoastedLead, Materials.Lead));
        materials.add(new DataMaterial(Materials.RoastedArsenic, Materials.Arsenic));
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

        for (DataMaterial mat : materials) {
            for (DataFuels fuel : fuels) {
                if (fuel.skipForCustom) continue;

                ItemStack dustInput = GTN_OreDict.getDust(mat.inputMaterial)
                    .copy();
                dustInput.stackSize = 2 * fuel.modifierOutput;

                ItemStack fuelInput = fuel.itemStack.copy();
                fuelInput.stackSize = 2;

                ItemStack ingotOutput = GTN_OreDict.getIngot(mat.outputMaterial)
                    .copy();
                ingotOutput.stackSize = 3 * fuel.modifierOutput;

                GTValues.RA.stdBuilder()
                    .itemInputs(dustInput, fuelInput)
                    .itemOutputs(ingotOutput)
                    .eut(0)
                    .duration(DURATION * fuel.modifierDuration)
                    .addTo(AdvancedBBF);
            }
        }
    }

    private static final class DataFuels {

        public final ItemStack itemStack;
        public final int modifierDuration;
        public final int modifierOutput;
        public final boolean skipForCustom;

        public DataFuels(ItemStack itemStack, int modifierDuration, int modifierOutput, boolean skipForCustom) {
            this.itemStack = itemStack;
            this.modifierDuration = modifierDuration;
            this.modifierOutput = modifierOutput;
            this.skipForCustom = skipForCustom;
        }
    }

    private static final class DataMaterial {

        public final Materials inputMaterial;
        public final Materials outputMaterial;

        public DataMaterial(Materials inputMaterial, Materials outputMaterial) {
            this.inputMaterial = inputMaterial;
            this.outputMaterial = outputMaterial;
        }
    }
}
