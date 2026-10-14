package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.IV;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.OutputBus;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.Blocks.GTN_Blocks;
import com.EvgenWarGold.GregTechNightmare.GregTech.Blocks.GTN_HideableCasing;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.GregTech.Render.BBModel.GTN_LoopAnimationState;
import com.EvgenWarGold.GregTechNightmare.GregTech.Render.GTN_AutomatedInfusionMatrixRenderer;
import com.EvgenWarGold.GregTechNightmare.GregTech.Thaumcraft.GTN_ThaumicProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.Thaumcraft.GTN_ThaumicSounds;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
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
    /** Last structure state sent to clients, so the model appears/disappears together with the structure. */
    private boolean syncedMachine;

    private static final String NBT_HIDDEN_CASINGS = "gtnHiddenCasings";
    /** World coordinates (x, y, z triples) of the casings hidden by this controller. */
    private int[] hiddenCasings = new int[0];
    /** Set after the first structure check since loading. */
    private boolean structureChecked;

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
                .addElement('M', ofBlockAnyMeta(GTN_Blocks.MATRIX_CASING, 0)));
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
        structureChecked = true;
    }

    @Override
    public void onPostTick(IGregTechTileEntity gte, long timer) {
        super.onPostTick(gte, timer);
        if (!gte.isServerSide()) return;
        // wait for the first structure check after loading, otherwise the casings would blink on chunk load
        if (structureChecked && (hiddenCasings.length > 0) != mMachine) {
            if (mMachine) hideCasings(gte);
            else showCasings(gte);
        }
        if (syncedMachine != mMachine) {
            syncedMachine = mMachine;
            gte.issueTileUpdate();
        }
    }

    @Override
    public void onBlockDestroyed() {
        IGregTechTileEntity gte = getBaseMetaTileEntity();
        if (gte != null && gte.isServerSide()) showCasings(gte);
        super.onBlockDestroyed();
    }

    /** Hides the 3x2x3 volume above the base: right -1..1, up 1..2, back 0..2 relative to the controller. */
    private void hideCasings(IGregTechTileEntity gte) {
        ExtendedFacing facing = getExtendedFacing();
        ForgeDirection right = facing.getRelativeRightInWorld();
        ForgeDirection up = facing.getRelativeUpInWorld();
        ForgeDirection back = facing.getRelativeBackInWorld();
        World world = gte.getWorld();

        List<Integer> hidden = new ArrayList<>();
        for (int r = -1; r <= 1; r++) for (int u = 1; u <= 2; u++) for (int b = 0; b <= 2; b++) {
            int x = gte.getXCoord() + right.offsetX * r + up.offsetX * u + back.offsetX * b;
            int y = gte.getYCoord() + right.offsetY * r + up.offsetY * u + back.offsetY * b;
            int z = gte.getZCoord() + right.offsetZ * r + up.offsetZ * u + back.offsetZ * b;
            if (GTN_HideableCasing.setHidden(world, x, y, z, true)) {
                hidden.add(x);
                hidden.add(y);
                hidden.add(z);
            }
        }
        hiddenCasings = hidden.stream()
            .mapToInt(Integer::intValue)
            .toArray();
        gte.markDirty();
    }

    private void showCasings(IGregTechTileEntity gte) {
        World world = gte.getWorld();
        for (int i = 0; i + 2 < hiddenCasings.length; i += 3) {
            if (world.blockExists(hiddenCasings[i], hiddenCasings[i + 1], hiddenCasings[i + 2])) {
                GTN_HideableCasing
                    .setHidden(world, hiddenCasings[i], hiddenCasings[i + 1], hiddenCasings[i + 2], false);
            }
        }
        hiddenCasings = new int[0];
        gte.markDirty();
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        nbt.setIntArray(NBT_HIDDEN_CASINGS, hiddenCasings);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        hiddenCasings = nbt.getIntArray(NBT_HIDDEN_CASINGS);
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

        float time = animationState.update(base.isActive(), GTN_AutomatedInfusionMatrixRenderer.getAnimationLength());
        GTN_AutomatedInfusionMatrixRenderer.render(
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
