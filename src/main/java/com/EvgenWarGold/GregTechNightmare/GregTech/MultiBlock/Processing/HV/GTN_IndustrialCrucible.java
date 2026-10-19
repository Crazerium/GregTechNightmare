package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.HV;

import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.OutputBus;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HiddenCasings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.GregTech.Render.BBModel.GTN_LoopAnimationState;
import com.EvgenWarGold.GregTechNightmare.GregTech.Render.GTN_MachineModelRenderer;
import com.EvgenWarGold.GregTechNightmare.GregTech.Thaumcraft.GTN_ThaumicProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.Thaumcraft.GTN_ThaumicSounds;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.structure.error.StructureError;
import gregtech.common.render.IMTERenderer;
import io.netty.buffer.ByteBuf;

public class GTN_IndustrialCrucible extends GTN_MultiBlockBase<GTN_IndustrialCrucible> implements IMTERenderer {

    /** Client-side playback of the alchemy construct model animation. */
    private final GTN_LoopAnimationState animationState = new GTN_LoopAnimationState();
    /**
     * The 6x4x2 volume above the middle rows of the base: right -3..2, up 1..4, back 1..2 relative to the controller.
     */
    private final GTN_HiddenCasings hiddenCasings = new GTN_HiddenCasings(-3, 2, 1, 4, 1, 2);

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
                    {"      ", "AAAAAA", "AAAAAA", "      "},
                    {"      ", "AAAAAA", "AAAAAA", "      "},
                    {"      ", "AAAAAA", "AAAAAA", "      "},
                    {"      ", "AAAAAA", "AAAAAA", "      "},
                    {"CC~CCC", "CCCCCC", "CCCCCC", "CCCCCC"}
                },
                // spotless:on
                new MultiblockOffsets(2, 4, 0),
                new MultiblockArea(6, 5, 4),
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
                .addElement('A', GTN_HiddenCasings.ofHideable(GTN_Casings.AlchemyConstructCasing)));
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

    // region Hidden casings
    @Override
    public void checkMachine(IGregTechTileEntity gte, ItemStack stack, List<StructureError> errors) {
        super.checkMachine(gte, stack, errors);
        hiddenCasings.onStructureChecked();
    }

    @Override
    public void onPostTick(IGregTechTileEntity gte, long timer) {
        super.onPostTick(gte, timer);
        hiddenCasings.update(gte, getExtendedFacing(), mMachine);
    }

    @Override
    public void onBlockDestroyed() {
        IGregTechTileEntity gte = getBaseMetaTileEntity();
        if (gte != null && gte.isServerSide()) hiddenCasings.show(gte);
        super.onBlockDestroyed();
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        hiddenCasings.save(nbt);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        hiddenCasings.load(nbt);
    }
    // endregion

    // region Render
    @Override
    public void writeToStream(ByteBuf buffer) {
        super.writeToStream(buffer);
        buffer.writeBoolean(mMachine);
    }

    @Override
    public void readFromStream(ByteBuf buffer) {
        super.readFromStream(buffer);
        mMachine = buffer.readBoolean();
    }

    @Override
    public void renderTESR(double x, double y, double z, float timeSinceLastTick) {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null || !mMachine) return;

        GTN_MachineModelRenderer renderer = GTN_MachineModelRenderer.INDUSTRIAL_CRUCIBLE;
        float time = animationState.update(base.isActive(), renderer.getAnimationLength());
        renderer.render(
            x,
            y,
            z,
            getExtendedFacing(),
            base.getWorld(),
            base.getXCoord(),
            base.getYCoord(),
            base.getZCoord(),
            time,
            animationState.getSpeed());
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox(int x, int y, int z) {
        // the model stands behind the controller and is 6x4x2 blocks large; facing can point anywhere
        return AxisAlignedBB.getBoundingBox(x - 6, y - 6, z - 6, x + 7, y + 7, z + 7);
    }
    // endregion
}
