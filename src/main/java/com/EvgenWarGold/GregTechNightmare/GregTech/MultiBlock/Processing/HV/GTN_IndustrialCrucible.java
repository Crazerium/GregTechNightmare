package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.HV;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.OutputBus;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.GregTech.Thaumcraft.GTN_ThaumicProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.Thaumcraft.GTN_ThaumicSounds;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.ThaumcraftBlocks;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;

public class GTN_IndustrialCrucible extends GTN_MultiBlockBase<GTN_IndustrialCrucible> {

    public GTN_IndustrialCrucible(int id, String name) {
        super(id, name);
    }

    public GTN_IndustrialCrucible(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_IndustrialCrucible>> getStructureVariants() {
        return List.of(
            new StructureVariant<>(
                "IndustrialCrucible",
                // spotless:off
                new String[][] {
                    {"CCCCC", "CGGGC", "CGGGC", "CGGGC", "CCCCC"},
                    {"CCCCC", "C   C", "C   C", "C   C", "CCCCC"},
                    {"CCCCC", "C   C", "C V C", "C   C", "CCCCC"},
                    {"CCCCC", "C   C", "C   C", "C   C", "CCCCC"},
                    {"CC~CC", "CCCCC", "CCCCC", "CCCCC", "CCCCC"}
                },
                // spotless:on
                new MultiblockOffsets(2, 4, 0),
                new MultiblockArea(5, 5, 5),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_IndustrialCrucible createNewMetaEntity() {
        return new GTN_IndustrialCrucible(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addInputBus()
            .addOutputBus()
            .addExoticOrEnergyHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.CRAZER;
    }

    @Override
    public IStructureDefinition<GTN_IndustrialCrucible> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addMainCasing('C', b -> b.hatches(InputBus, OutputBus, Energy, ExoticEnergy))
                .addAllGlasses('G')
                .addElement('V', ofBlock(ThaumcraftBlocks.Crucible.get(), ThaumcraftBlocks.Crucible.getMeta())));
    }

    @Override
    protected boolean GTN_checkMachine(IGregTechTileEntity gte, ItemStack stack) {
        return super.GTN_checkMachine(gte, stack) && (!mInputBusses.isEmpty() || !mDualInputHatches.isEmpty())
            && !mOutputBusses.isEmpty()
            && (!mEnergyHatches.isEmpty() || !mExoticEnergyHatches.isEmpty());
    }

    @Override
    public OverclockType getOverclockType() {
        return OverclockType.NormalOverclock;
    }

    @Override
    public int getMaxParallelRecipes() {
        return GTN_ThaumicProcessingLogic.getTierParallel(getAverageInputVoltage(), VoltageIndex.HV);
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTN_Recipe.IndustrialCrucibleRecipes;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new GTN_ThaumicProcessingLogic(this, VoltageIndex.HV);
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void startActivitySound() {
        activitySoundLoop = GTN_ThaumicSounds
            .updateLoop(activitySoundLoop, getBaseMetaTileEntity(), GTN_ThaumicSounds.CRUCIBLE_LOOP);
    }
}
