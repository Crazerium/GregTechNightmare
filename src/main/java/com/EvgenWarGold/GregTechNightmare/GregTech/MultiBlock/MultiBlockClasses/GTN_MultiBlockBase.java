package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils.tr;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_GLOW;
import static mcp.mobius.waila.api.SpecialChars.RED;
import static mcp.mobius.waila.api.SpecialChars.RESET;
import static net.minecraft.util.StatCollector.translateToLocalFormatted;

import java.util.ArrayList;
import java.util.List;

import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusOutput;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public abstract class GTN_MultiBlockBase<T extends GTN_MultiBlockBase<T>> extends MTEExtendedPowerMultiBlockBase<T>
    implements IConstructable, ISurvivalConstructable {

    // region Abstract
    public abstract int getOffsetHorizontal();

    public abstract int getOffsetVertical();

    public abstract int getOffsetDepth();

    public abstract String getStructurePieceMain();

    public abstract GTN_Casings getMainCasings();

    public abstract T createNewMetaEntity();

    public abstract OverclockType getOverclockType();

    public abstract boolean isNoMaintenanceIssue();

    public abstract String[][] getShape();

    public abstract void createGtnTooltip(MultiblockTooltipBuilder builder);

    public abstract String getMachineType();
    // endregion

    // region Class Construct
    private static final String TRANSLATE_KEY = "multiblock.";

    public GTN_MultiBlockBase(int id, String name) {
        super(id, TRANSLATE_KEY + name, GTN_Utils.tr(TRANSLATE_KEY + name));
    }

    public GTN_MultiBlockBase(String name) {
        super(name);
    }
    // endregion

    // region Create Meta
    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return createNewMetaEntity();
    }
    // endregion

    // region Construct MultiBlock
    protected int mainCasingAmount = 0;

    protected void mainCasingAdd() {
        mainCasingAmount++;
    }

    protected int getMainCasingMax() {
        return 0;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        boolean built = false;
        boolean hasEnoughCasing = true;
        mainCasingAmount = 0;

        if (checkPiece(getStructurePieceMain(), getOffsetHorizontal(), getOffsetVertical(), getOffsetDepth())) {
            built = true;

            if (getMainCasingMax() > 0) {
                hasEnoughCasing = mainCasingAmount >= getMainCasingMax();
            }
        }

        if (isNoMaintenanceIssue()) {
            repairMachine();
        }
        return built && hasEnoughCasing;
    }

    @Override
    public void construct(ItemStack itemStack, boolean hintsOnly) {
        buildPiece(
            getStructurePieceMain(),
            itemStack,
            hintsOnly,
            getOffsetHorizontal(),
            getOffsetVertical(),
            getOffsetDepth());
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return this.survivalBuildPiece(
            getStructurePieceMain(),
            stackSize,
            getOffsetHorizontal(),
            getOffsetVertical(),
            getOffsetDepth(),
            elementBudget,
            env,
            false,
            true);
    }
    // endregion

    // region Textures
    public Textures.BlockIcons getMainOverlay() {
        return OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE;
    }

    public Textures.BlockIcons getMainOverlayActive() {
        return OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE;
    }

    public Textures.BlockIcons getMainOverlayGlow() {
        return OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_GLOW;
    }

    public Textures.BlockIcons getMainOverlayActiveGlow() {
        return OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE_GLOW;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getMainCasings().textureId),
                TextureFactory.builder()
                    .addIcon(getMainOverlayActive())
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(getMainOverlayActiveGlow())
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getMainCasings().textureId),
                TextureFactory.builder()
                    .addIcon(getMainOverlay())
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(getMainOverlayGlow())
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getMainCasings().textureId) };
    }
    // endregion

    // region ProcessingLogic
    protected int maxParallel = 1;
    protected float euModifier = 1;
    protected float speedBonus = 1;

    public int getMaxParallelRecipes() {
        return maxParallel;
    }

    public float getEuModifier() {
        return euModifier;
    }

    public float getSpeedBonus() {
        return speedBonus;
    }

    protected ProcessingLogic createProcessingLogic() {
        return new GTN_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {

                setEuModifier(isEnergyMultiBlock() ? getEuModifier() : 0);
                setSpeedBonus(isEnergyMultiBlock() ? getSpeedBonus() : 1);
                setOverclockType(isEnergyMultiBlock() ? getOverclockType() : OverclockType.NONE);
                return super.process();
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }
    // endregion

    // region NBT
    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("maxParallel", getMaxParallelRecipes());
        aNBT.setFloat("euModifier", getEuModifier());
        aNBT.setFloat("speedBonus", getSpeedBonus());
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        maxParallel = Math.max(aNBT.getInteger("maxParallel"), 1);
        euModifier = aNBT.getFloat("euModifier");
        if (euModifier <= 0) euModifier = 1;
        speedBonus = aNBT.getFloat("speedBonus");
        if (speedBonus <= 0) speedBonus = 1;
    }
    // endregion

    // region Tooltip

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType());
        createGtnTooltip(tt);
        tt.toolTipFinisher(Constants.MOD_NAME);
        return tt;
    }
    // endregion

    // region Waila
    private final static String PARALLEL_WAILA_NBT_KEY = "trueParallel";
    private final static String EU_MODIFIER_WAILA_NBT_KEY = "euModifier";
    private final static String SPEED_BONUS_WAILA_NBT_KEY = "speedBonus";
    private final static String TIME_REDUCTION_WAILA_NBT_KEY = "timeReduction";
    private final static String POWER_INCREASE_WAILA_NBT_KEY = "powerIncrease";

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        final NBTTagCompound tag = accessor.getNBTData();

        int parallel = tag.getInteger(PARALLEL_WAILA_NBT_KEY);
        float euModifier = tag.getFloat(EU_MODIFIER_WAILA_NBT_KEY);
        float speedBonus = tag.getFloat(SPEED_BONUS_WAILA_NBT_KEY);
        int timeReduction = tag.getInteger(TIME_REDUCTION_WAILA_NBT_KEY);
        int powerIncrease = tag.getInteger(POWER_INCREASE_WAILA_NBT_KEY);

        if (tag.getBoolean("incompleteStructure")) {
            currentTip.add(RED + translateToLocalFormatted("GT5U.waila.multiblock.status.incomplete") + RESET);
        } else {
            if (isEnergyMultiBlock()) {
                if (parallel > 0) {
                    currentTip.add(tr("multiblock.waila.max_parallel", parallel));
                }

                if (euModifier > 0) {
                    currentTip.add(tr("multiblock.waila.eu_modifier", euModifier));
                }

                if (speedBonus > 0) {
                    currentTip.add(tr("multiblock.waila.speed_bonus", speedBonus));
                }

                if (getOverclockType() != null) {
                    currentTip.add(tr("multiblock.waila.overclock", timeReduction, powerIncrease));
                }
            }
            super.getWailaBody(itemStack, currentTip, accessor, config);
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);

        tag.setInteger(PARALLEL_WAILA_NBT_KEY, getMaxParallelRecipes());
        tag.setFloat(EU_MODIFIER_WAILA_NBT_KEY, getEuModifier());
        tag.setFloat(SPEED_BONUS_WAILA_NBT_KEY, getSpeedBonus());
        tag.setInteger(POWER_INCREASE_WAILA_NBT_KEY, getOverclockType().powerIncrease);
        tag.setInteger(TIME_REDUCTION_WAILA_NBT_KEY, getOverclockType().timeReduction);
    }
    // endregion

    // region Buttons
    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    @Override
    public boolean supportsBatchMode() {
        return true;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return true;
    }
    // endregion

    // region Nei
    @Override
    public int getRecipeCatalystPriority() {
        return 1000;
    }
    // endregion

    // region Hatches
    public ArrayList<MTEHatchSteamBusInput> mSteamInputBusses = new ArrayList<>();
    public ArrayList<MTEHatchSteamBusOutput> mSteamOutputBusses = new ArrayList<>();

    private boolean baseCheckHatch(IGregTechTileEntity tileEntity, int baseCasingIndex) {
        if (tileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = tileEntity.getMetaTileEntity();

        return aMetaTileEntity == null;
    }

    public final boolean addSteamInputBusToMachineList(IGregTechTileEntity tileEntity, int baseCasingIndex) {
        if (baseCheckHatch(tileEntity, baseCasingIndex)) return false;

        if (tileEntity.getMetaTileEntity() instanceof MTEHatchSteamBusInput steamBusInput) {
            steamBusInput.updateTexture(baseCasingIndex);
            steamBusInput.updateCraftingIcon(this.getMachineCraftingIcon());
            mInputBusses.add(steamBusInput);
            return mSteamInputBusses.add(steamBusInput);
        }
        return false;
    }

    public final boolean addSteamOutputBusToMachineList(IGregTechTileEntity tileEntity, int baseCasingIndex) {
        if (baseCheckHatch(tileEntity, baseCasingIndex)) return false;

        if (tileEntity.getMetaTileEntity() instanceof MTEHatchSteamBusOutput steamBusOutput) {
            steamBusOutput.updateTexture(baseCasingIndex);
            steamBusOutput.updateCraftingIcon(this.getMachineCraftingIcon());
            mOutputBusses.add(steamBusOutput);
            return mSteamOutputBusses.add(steamBusOutput);
        }
        return false;
    }
    // endregion

    // region Others methods
    public void repairMachine() {
        mHardHammer = true;
        mSoftMallet = true;
        mScrewdriver = true;
        mCrowbar = true;
        mSolderingTool = true;
        mWrench = true;
    }

    public boolean isEnergyMultiBlock() {
        return true;
    }

    protected String tr(String key) {
        return GTN_Utils.tr(this.mName + "." + key);
    }

    protected String tr(String key, Object... formatted) {
        return GTN_Utils.tr(this.mName + "." + key, formatted);
    }
    // endregion
}
