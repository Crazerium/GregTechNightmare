package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeResult;

import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;
import gregtech.api.recipe.check.CheckRecipeResult;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.StatCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static gregtech.api.enums.GTValues.V;
import static gregtech.api.enums.GTValues.VN;

public class ResultInsufficientRangeTier implements CheckRecipeResult {

    private int minTier;
    private int maxTier;

    public ResultInsufficientRangeTier(int minTier, int maxTier) {
        this.minTier = minTier;
        this.maxTier = maxTier;
    }

    @Override
    public boolean wasSuccessful() {
        return false;
    }

    @Override
    public @NotNull String getID() {
        return "insufficient_range_tier";
    }

    @Override
    public @NotNull String getDisplayString() {
        if (minTier == 0) {
            return Objects.requireNonNull(GTN_Utils.tr("multiblock.reciperesult.max_tier", VN[maxTier]));
        } else {
            return Objects.requireNonNull(GTN_Utils.tr("multiblock.reciperesult.range_tier", VN[minTier], VN[maxTier]));
        }
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound tag) {
        tag.setInteger("minTier", minTier);
        tag.setInteger("maxTier", maxTier);

        return tag;
    }

    @Override
    public void readFromNBT(@NotNull NBTTagCompound tag) {
        minTier = tag.getInteger("minTier");
        maxTier = tag.getInteger("maxTier");
    }

    @Override
    public @NotNull CheckRecipeResult newInstance() {
        return new ResultInsufficientRangeTier(0, 0);
    }

    @Override
    public void encode(@NotNull PacketBuffer buffer) {
        buffer.writeVarIntToBuffer(minTier);
        buffer.writeVarIntToBuffer(maxTier);
    }

    @Override
    public void decode(PacketBuffer buffer) {
        minTier = buffer.readVarIntFromBuffer();
        maxTier = buffer.readVarIntFromBuffer();
    }

    public static ResultInsufficientRangeTier of(int minTier, int maxTier) {
        return new ResultInsufficientRangeTier(minTier, maxTier);
    }
}
