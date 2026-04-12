package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe;

import static gregtech.api.enums.TierEU.RECIPE_EV;
import static gregtech.api.enums.TierEU.RECIPE_HV;
import static gregtech.api.enums.TierEU.RECIPE_IV;
import static gregtech.api.enums.TierEU.RECIPE_LV;
import static gregtech.api.enums.TierEU.RECIPE_LuV;
import static gregtech.api.enums.TierEU.RECIPE_MAX;
import static gregtech.api.enums.TierEU.RECIPE_MV;
import static gregtech.api.enums.TierEU.RECIPE_UEV;
import static gregtech.api.enums.TierEU.RECIPE_UHV;
import static gregtech.api.enums.TierEU.RECIPE_UIV;
import static gregtech.api.enums.TierEU.RECIPE_ULV;
import static gregtech.api.enums.TierEU.RECIPE_UMV;
import static gregtech.api.enums.TierEU.RECIPE_UV;
import static gregtech.api.enums.TierEU.RECIPE_UXV;
import static gregtech.api.enums.TierEU.RECIPE_ZPM;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.GTRecipeBuilder;

public class GTN_RecipeBuilder {

    private final GTRecipeBuilder delegate;

    private GTN_RecipeBuilder() {
        this.delegate = GTRecipeBuilder.builder();
    }

    public static GTN_RecipeBuilder builder() {
        return new GTN_RecipeBuilder();
    }

    public GTN_RecipeBuilder itemInputs(ItemStack... itemStacks) {
        delegate.itemInputs(itemStacks);
        return this;
    }

    public GTN_RecipeBuilder itemOutputs(ItemStack... itemStacks) {
        delegate.itemOutputs(itemStacks);
        return this;
    }

    public GTN_RecipeBuilder fluidInputs(FluidStack... fluidStacks) {
        delegate.fluidInputs(fluidStacks);
        return this;
    }

    public GTN_RecipeBuilder fluidOutputs(FluidStack... fluidStacks) {
        delegate.fluidOutputs(fluidStacks);
        return this;
    }

    public GTN_RecipeBuilder outputChances(int... chances) {
        delegate.outputChances(chances);
        return this;
    }

    public GTN_RecipeBuilder special(Object special) {
        delegate.special(special);
        return this;
    }

    public GTN_RecipeBuilder specialValue(int specialValue) {
        delegate.specialValue(specialValue);
        return this;
    }

    public GTN_RecipeBuilder addTo(IRecipeMap recipeMap) {
        delegate.addTo(recipeMap);
        return this;
    }

    public GTN_RecipeBuilder durationInTicks(int duration) {
        delegate.duration(duration);
        return this;
    }

    public GTN_RecipeBuilder durationInSeconds(int duration) {
        return durationInTicks(duration * 20);
    }

    public GTN_RecipeBuilder durationInMinutes(int duration) {
        return durationInSeconds(duration * 60);
    }

    public GTN_RecipeBuilder durationInHours(int duration) {
        return durationInMinutes(duration * 60);
    }

    public GTN_RecipeBuilder durationInDays(int duration) {
        return durationInHours(duration * 24);
    }

    public GTN_RecipeBuilder recipeULV() {
        delegate.eut(RECIPE_ULV);
        return this;
    }

    public GTN_RecipeBuilder recipeLV() {
        delegate.eut(RECIPE_LV);
        return this;
    }

    public GTN_RecipeBuilder recipeMV() {
        delegate.eut(RECIPE_MV);
        return this;
    }

    public GTN_RecipeBuilder recipeHV() {
        delegate.eut(RECIPE_HV);
        return this;
    }

    public GTN_RecipeBuilder recipeEV() {
        delegate.eut(RECIPE_EV);
        return this;
    }

    public GTN_RecipeBuilder recipeIV() {
        delegate.eut(RECIPE_IV);
        return this;
    }

    public GTN_RecipeBuilder recipeLUV() {
        delegate.eut(RECIPE_LuV);
        return this;
    }

    public GTN_RecipeBuilder recipeZPM() {
        delegate.eut(RECIPE_ZPM);
        return this;
    }

    public GTN_RecipeBuilder recipeUV() {
        delegate.eut(RECIPE_UV);
        return this;
    }

    public GTN_RecipeBuilder recipeUHV() {
        delegate.eut(RECIPE_UHV);
        return this;
    }

    public GTN_RecipeBuilder recipeUEV() {
        delegate.eut(RECIPE_UEV);
        return this;
    }

    public GTN_RecipeBuilder recipeUIV() {
        delegate.eut(RECIPE_UIV);
        return this;
    }

    public GTN_RecipeBuilder recipeUMV() {
        delegate.eut(RECIPE_UMV);
        return this;
    }

    public GTN_RecipeBuilder recipeUXV() {
        delegate.eut(RECIPE_UXV);
        return this;
    }

    public GTN_RecipeBuilder recipeMAX() {
        delegate.eut(RECIPE_MAX);
        return this;
    }

    public GTN_RecipeBuilder eut(int eut) {
        delegate.eut(eut);
        return this;
    }

    public <T> GTN_RecipeBuilder metadata(RecipeMetadataKey<T> key, T value) {
        delegate.metadata(key, value);
        return this;
    }
}
