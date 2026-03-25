package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData;

import net.minecraftforge.fluids.FluidStack;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.recipe.metadata.SimpleRecipeMetadataKey;
import thaumcraft.api.aspects.AspectList;

public class SimpleMetaData {

    public static final RecipeMetadataKey<AspectList> ASPECT_COST = SimpleRecipeMetadataKey
        .create(AspectList.class, "aspect_cost");

    public static final RecipeMetadataKey<String> RESEARCH_KEY = SimpleRecipeMetadataKey
        .create(String.class, "research_key");

    public static final RecipeMetadataKey<Integer> MULTIBLOCK_TIER = SimpleRecipeMetadataKey
        .create(Integer.class, "multiblock_tier");

    public static final RecipeMetadataKey<Integer> GENERATING_EU = SimpleRecipeMetadataKey
        .create(Integer.class, "generating_eu");

    public static final RecipeMetadataKey<FluidStack> FLUID_INPUT = SimpleRecipeMetadataKey
        .create(FluidStack.class, "fluid_input");

    public static final RecipeMetadataKey<FluidStack> FLUID_OUTPUT = SimpleRecipeMetadataKey
        .create(FluidStack.class, "fluid_output");

    public static final RecipeMetadataKey<Double> TEMPERATURE_INCREASE = SimpleRecipeMetadataKey
        .create(Double.class, "temperature_increase");
}
