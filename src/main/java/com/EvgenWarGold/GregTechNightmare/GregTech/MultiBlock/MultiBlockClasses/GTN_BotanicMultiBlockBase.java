package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import static com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HatchElement.ManaHatch;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;

import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Mana.GTN_BotanicProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.Thaumcraft.GTN_ThaumicProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;

/**
 * Automated Botania machine: crafts cost the fixed recipe mana from Mana Hatches or ME Magic Crafting Input Hatches,
 * parallels double with every energy tier above {@link #getBaseTier()}.
 */
public abstract class GTN_BotanicMultiBlockBase<T extends GTN_BotanicMultiBlockBase<T>> extends GTN_MultiBlockBase<T> {

    protected GTN_BotanicMultiBlockBase(int id, String name) {
        super(id, name);
    }

    protected GTN_BotanicMultiBlockBase(String name) {
        super(name);
    }

    /** Energy tier of one parallel, see {@link gregtech.api.enums.VoltageIndex}. */
    protected abstract int getBaseTier();

    /** Main casing with all hatches of a Botania machine. */
    protected void addBotanicCasing(GTN_StructureBuilder<T> builder, char name) {
        builder.addMainCasing(name, b -> b.hatches(InputBus, InputHatch, OutputBus, Energy, ExoticEnergy, ManaHatch));
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addInputBus()
            .addOutputBus()
            .addExoticOrEnergyHatch()
            .addManaHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.CRAZER;
    }

    /** False for machines that make no items. */
    protected boolean needsOutputBus() {
        return true;
    }

    @Override
    protected boolean GTN_checkMachine(IGregTechTileEntity gte, ItemStack stack) {
        return super.GTN_checkMachine(gte, stack) && (!mInputBusses.isEmpty() || !mDualInputHatches.isEmpty())
            && (!needsOutputBus() || !mOutputBusses.isEmpty())
            && (!mEnergyHatches.isEmpty() || !mExoticEnergyHatches.isEmpty());
    }

    @Override
    public OverclockType getOverclockType() {
        return OverclockType.NormalOverclock;
    }

    @Override
    public int getMaxParallelRecipes() {
        return GTN_ThaumicProcessingLogic.getTierParallel(getAverageInputVoltage(), getBaseTier());
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return true;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new GTN_BotanicProcessingLogic(this, getBaseTier());
    }
}
