package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.STEAM;

import static com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HatchElement.SteamInputBus;
import static com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HatchElement.SteamOutputBus;
import static com.gtnewhorizon.structurelib.util.XSTR.XSTR_INSTANCE;
import static gregtech.api.enums.HatchElement.OutputHatch;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Gui.GTN_AdvancedCokeOvenGui;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.HarvestTool;
import gregtech.api.enums.ParticleFX;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.IOutputBus;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.modularui2.GTGuiTheme;
import gregtech.api.modularui2.GTGuiThemes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.WorldSpawnedEventBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

public class GTN_AdvancedCokeOven extends GTN_MultiBlockBase<GTN_AdvancedCokeOven> {

    public final static int INPUT_SLOT = 0;
    public final static int OUTPUT_SLOT = 1;
    public final static int FLUID_CAPACITY = 2_000;

    private FluidStack fluid;

    public GTN_AdvancedCokeOven(int id, String name) {
        super(id, name);
    }

    public GTN_AdvancedCokeOven(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_AdvancedCokeOven>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "CokeOven",
                // spotless:off
                new String[][]{
                    {"  A  "," AAA ","  A  ","     "},
                    {" AAA "," AAA "," AAA ","     "},
                    {" A~A ","AAAAA"," AAA ","  A  "},
                    {" AAA ","AAAAA"," AAA ","  A  "}
                },
                //spotless:on
                new MultiblockOffsets(2, 2, 0),
                new MultiblockArea(5, 4, 4),
                1,
                GTN_Casings.CokeOvenCasing));
    }

    @Override
    public GTN_AdvancedCokeOven createNewMetaEntity() {
        return new GTN_AdvancedCokeOven(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addSteamInputBus()
            .addSteamOutputBus();
    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_AdvancedCokeOven> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addMainCasing('A', b -> b.hatches(SteamInputBus, SteamOutputBus, OutputHatch)));
    }

    @Override
    public boolean isEnergyMultiBlock() {
        return false;
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTN_Recipe.CokeOvenRecipes;
    }

    @Override
    public void onPostTick(IGregTechTileEntity baseMetaTileEntity, long tick) {
        if (baseMetaTileEntity.isClientSide()) onPostTickClient();
        if (baseMetaTileEntity.isServerSide()) onPostTickServer(baseMetaTileEntity, tick);
    }

    private void onPostTickClient() {
        doActivitySound(SoundResource.GTCEU_LOOP_FURNACE);
    }

    private void onPostTickServer(IGregTechTileEntity baseMetaTileEntity, long tick) {
        checkRecipeProgress(baseMetaTileEntity);
        inputItem();
        outputItem();
        outputFluid();

        if (tick % 20 == 0) {
            mMachine = checkStructure(true, baseMetaTileEntity);

            setErrorDisplayID(mMachine ? 0 : 64);
        }

        baseMetaTileEntity.setActive(mMaxProgresstime > 0 && mMachine);
    }

    private void inputItem() {
        List<ItemStack> inputs = getStoredInputs();
        final ItemStack inputSlot = mInventory[INPUT_SLOT];

        for (ItemStack itemStack : inputs) {
            if (itemStack == null || itemStack.stackSize <= 0) continue;

            if (inputSlot == null) {
                mInventory[INPUT_SLOT] = itemStack.copy();
                itemStack.stackSize = 0;
            } else if (inputSlot.isItemEqual(itemStack)) {
                int maxStackSize = inputSlot.getMaxStackSize();
                int currentSize = inputSlot.stackSize;
                int spaceAvailable = maxStackSize - currentSize;

                if (spaceAvailable > 0 && itemStack.stackSize > 0) {
                    int transferAmount = Math.min(spaceAvailable, itemStack.stackSize);
                    inputSlot.stackSize += transferAmount;
                    itemStack.stackSize -= transferAmount;
                }
            }
        }
    }

    private void outputItem() {
        List<IOutputBus> outputs = getOutputBusses();
        final ItemStack outputSlot = mInventory[OUTPUT_SLOT];
        if (outputSlot == null || outputSlot.stackSize <= 0) return;

        for (IOutputBus outputBus : outputs) {
            outputBus.storePartial(outputSlot);

            if (outputSlot.stackSize <= 0) {
                mInventory[OUTPUT_SLOT] = null;
                break;
            }
        }
    }

    private void outputFluid() {
        if (fluid == null || fluid.amount <= 0) return;

        for (MTEHatchOutput hatchOutput : mOutputHatches) {
            if (hatchOutput == null) continue;
            if (fluid.amount <= 0) break;

            if (!hatchOutput.canStoreFluid(fluid)) continue;

            FluidStack hatchFluid = hatchOutput.getFluid();

            if (hatchFluid == null) {
                int maxCapacity = hatchOutput.getCapacity();
                int transferAmount = Math.min(maxCapacity, fluid.amount);

                hatchOutput.mFluid = fluid.copy();
                hatchOutput.mFluid.amount = transferAmount;
                fluid.amount -= transferAmount;

            } else if (hatchFluid.isFluidEqual(fluid)) {
                int maxCapacity = hatchOutput.getCapacity();
                int currentAmount = hatchFluid.amount;
                int spaceAvailable = maxCapacity - currentAmount;

                if (spaceAvailable > 0) {
                    int transferAmount = Math.min(spaceAvailable, fluid.amount);
                    hatchFluid.amount += transferAmount;
                    fluid.amount -= transferAmount;
                }
            }
        }

        if (fluid.amount <= 0) {
            fluid = null;
        }
    }

    private void checkRecipeProgress(IGregTechTileEntity baseMetaTileEntity) {
        if (!mMachine) return;

        if (mMaxProgresstime > 0 && ++mProgresstime >= mMaxProgresstime) {
            addOutput();
            addOutputFluid();
            mOutputItems = null;
            mOutputFluids = null;
            mProgresstime = 0;
            mMaxProgresstime = 0;
        }

        if (mMaxProgresstime == 0 && baseMetaTileEntity.isAllowedToWork()) {
            final ItemStack input = mInventory[INPUT_SLOT];
            final ItemStack output = mInventory[OUTPUT_SLOT];

            final GTRecipe recipe = getRecipeMap().findRecipeQuery()
                .items(input)
                .find();

            if (recipe == null) return;

            final ItemStack recipeOutput = recipe.getOutput(0);
            if (output != null && recipeOutput != null) {
                if (output.stackSize + recipeOutput.stackSize > output.getMaxStackSize()) return;
                if (!GTUtility.areStacksEqual(output, recipeOutput)) return;
            }

            final FluidStack recipeFluid = recipe.getFluidOutput(0);
            if (fluid != null && recipeFluid != null) {
                if (!GTUtility.areFluidsEqual(fluid, recipeFluid)) return;
            }

            if (!recipe.isRecipeInputEqual(true, null, input)) return;
            if (input != null && input.stackSize == 0) mInventory[INPUT_SLOT] = null;

            mMaxProgresstime = recipe.mDuration;
            mOutputItems = recipe.mOutputs;
            mOutputFluids = recipe.mFluidOutputs;
        }
    }

    @Override
    protected GTGuiTheme getGuiTheme() {
        return GTGuiThemes.COKE_OVEN;
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new GTN_AdvancedCokeOvenGui(this);
    }

    @Override
    public FluidStack getFluid() {
        return fluid;
    }

    public void setFluid(FluidStack fluid) {
        this.fluid = fluid;
    }

    @Override
    public int getCapacity() {
        return FLUID_CAPACITY;
    }

    private void addOutput() {
        if (mOutputItems == null) return;
        if (mOutputItems.length == 0) return;

        final ItemStack output = mInventory[OUTPUT_SLOT];
        final ItemStack recipeOutput = mOutputItems[0];
        if (recipeOutput == null) return;

        if (output == null) {
            mInventory[OUTPUT_SLOT] = recipeOutput.copy();
            return;
        }

        if (GTUtility.areStacksEqual(output, recipeOutput)) {
            output.stackSize = Math.min(output.getMaxStackSize(), output.stackSize + recipeOutput.stackSize);
        }
    }

    private void addOutputFluid() {
        if (mOutputFluids == null) return;
        if (mOutputFluids.length == 0) return;

        final FluidStack recipeFluid = mOutputFluids[0];
        if (recipeFluid == null) return;

        if (fluid == null) {
            fluid = recipeFluid.copy();
            return;
        }

        if (GTUtility.areFluidsEqual(fluid, recipeFluid)) {
            fluid.amount = Math.min(FLUID_CAPACITY, fluid.amount + recipeFluid.amount);
        }
    }

    @Override
    public boolean isValidSlot(int index) {
        return index == INPUT_SLOT || index == OUTPUT_SLOT;
    }

    @Override
    protected boolean supportsSlotAutomation(int index) {
        return index == INPUT_SLOT || index == OUTPUT_SLOT;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onRandomDisplayTick(IGregTechTileEntity baseMetaTileEntity) {
        if (!baseMetaTileEntity.isActive()) return;

        final ForgeDirection frontFacing = baseMetaTileEntity.getFrontFacing();

        final double oX = baseMetaTileEntity.getOffsetX(frontFacing, 1) + 0.5;
        final double oY = baseMetaTileEntity.getOffsetY(frontFacing, 1);
        final double oZ = baseMetaTileEntity.getOffsetZ(frontFacing, 1) + 0.5;
        final double offset = -0.48;
        final double horizontal = XSTR_INSTANCE.nextDouble() * 8.0 / 16.0 - 4.0 / 16.0;
        final double vertical = XSTR_INSTANCE.nextDouble() * 10.0 / 16.0 + 5.0 / 16.0;

        switch (frontFacing) {
            case NORTH -> {
                final double x = oX + horizontal;
                final double y = oY + vertical;
                final double z = oZ - offset;
                createParticles(baseMetaTileEntity, x, y, z);
            }
            case SOUTH -> {
                final double x = oX + horizontal;
                final double y = oY + vertical;
                final double z = oZ + offset;
                createParticles(baseMetaTileEntity, x, y, z);
            }
            case WEST -> {
                final double x = oX - offset;
                final double y = oY + vertical;
                final double z = oZ + horizontal;
                createParticles(baseMetaTileEntity, x, y, z);
            }
            case EAST -> {
                final double x = oX + offset;
                final double y = oY + vertical;
                final double z = oZ + horizontal;
                createParticles(baseMetaTileEntity, x, y, z);
            }
            default -> throw new IllegalStateException("Unexpected facing: " + frontFacing);
        }
    }

    private void createParticles(IGregTechTileEntity baseMetaTileEntity, double x, double y, double z) {
        WorldSpawnedEventBuilder.ParticleEventBuilder particleEventBuilder = new WorldSpawnedEventBuilder.ParticleEventBuilder()
            .setMotion(0, 0, 0)
            .setPosition(x, y, z)
            .setWorld(baseMetaTileEntity.getWorld());
        particleEventBuilder.setIdentifier(ParticleFX.SMOKE)
            .run();
        particleEventBuilder.setIdentifier(ParticleFX.FLAME)
            .run();
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        if (fluid != null) nbt.setTag("fluid", this.fluid.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        fluid = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("fluid"));
    }

    @Override
    public byte getTileEntityBaseType() {
        return HarvestTool.PickaxeLevel0.toTileEntityBaseType();
    }

    @Override
    public boolean allowCoverOnSide(ForgeDirection side, ItemStack coverItem) {
        return false;
    }
}
