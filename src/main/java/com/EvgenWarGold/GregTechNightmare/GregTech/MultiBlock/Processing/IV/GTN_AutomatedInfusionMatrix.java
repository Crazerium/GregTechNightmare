package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.IV;

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

public class GTN_AutomatedInfusionMatrix extends GTN_MultiBlockBase<GTN_AutomatedInfusionMatrix>
    implements IMTERenderer {

    /** Client-side playback of the matrix model animation. */
    private final GTN_LoopAnimationState animationState = new GTN_LoopAnimationState();
    /** The 3x2x3 volume above the base: right -1..1, up 1..2, back 0..2 relative to the controller. */
    private final GTN_HiddenCasings hiddenCasings = new GTN_HiddenCasings(-1, 1, 1, 2, 0, 2);

    public GTN_AutomatedInfusionMatrix(int id, String name) {
        super(id, name);
    }

    public GTN_AutomatedInfusionMatrix(String name) {
        super(name);
    }

    // 'M' - Runic Matrix Casings: they turn invisible once the structure is formed and the animated matrix model is
    // rendered in their place. 'C' - the base with the controller and all hatches, it always stays visible.
    @Override
    public List<StructureVariant<GTN_AutomatedInfusionMatrix>> getStructureVariants() {
        return List.of(
            new StructureVariant<>(
                "AutomatedInfusionMatrix",
                // spotless:off
                new String[][] {
                    {"MMM", "MMM", "MMM"},
                    {"MMM", "MMM", "MMM"},
                    {"C~C", "CCC", "CCC"}
                },
                // spotless:on
                new MultiblockOffsets(1, 2, 0),
                new MultiblockArea(3, 3, 3),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_AutomatedInfusionMatrix createNewMetaEntity() {
        return new GTN_AutomatedInfusionMatrix(this.mName);
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
    public IStructureDefinition<GTN_AutomatedInfusionMatrix> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addMainCasing('C', b -> b.hatches(InputBus, OutputBus, Energy, ExoticEnergy))
                .addElement('M', GTN_HiddenCasings.ofHideable(GTN_Casings.RunicMatrixCasing)));
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
        return GTN_ThaumicProcessingLogic.getTierParallel(getAverageInputVoltage(), VoltageIndex.IV);
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTN_Recipe.AutomatedInfusionRecipes;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new GTN_ThaumicProcessingLogic(this, VoltageIndex.IV).setStartSound(GTN_ThaumicSounds.INFUSION_START);
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void startActivitySound() {
        activitySoundLoop = GTN_ThaumicSounds
            .updateLoop(activitySoundLoop, getBaseMetaTileEntity(), GTN_ThaumicSounds.INFUSER_LOOP);
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

        float time = animationState
            .update(base.isActive(), GTN_MachineModelRenderer.AUTOMATED_INFUSION_MATRIX.getAnimationLength());
        GTN_MachineModelRenderer.AUTOMATED_INFUSION_MATRIX.render(
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
        // the model sits right behind the controller and is 3x2x3 blocks large; facing can point anywhere
        return AxisAlignedBB.getBoundingBox(x - 5, y - 5, z - 5, x + 6, y + 6, z + 6);
    }
    // endregion
}
