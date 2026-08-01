package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeResult;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizons.modularui.common.internal.network.NetworkUtils;

import gregtech.api.recipe.check.CheckRecipeResult;

public class ResultMessage implements CheckRecipeResult {

    private String key;

    ResultMessage(String key) {
        this.key = key;
    }

    public @NotNull String getID() {
        return "result_message";
    }

    public boolean wasSuccessful() {
        return true;
    }

    @Nonnull
    public @NotNull String getDisplayString() {
        return this.key;
    }

    public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound tag) {
        tag.setString("key", this.key);
        return tag;
    }

    public void readFromNBT(@NotNull NBTTagCompound tag) {
        this.key = tag.getString("key");
    }

    @Nonnull
    public @NotNull CheckRecipeResult newInstance() {
        return new ResultMessage("");
    }

    public void encode(@Nonnull PacketBuffer buffer) {
        NetworkUtils.writeStringSafe(buffer, this.key);
    }

    public void decode(@Nonnull PacketBuffer buffer) {
        this.key = NetworkUtils.readStringSafe(buffer);
    }

    @Nonnull
    public static CheckRecipeResult of(String key) {
        return new ResultMessage(key);
    }
}
