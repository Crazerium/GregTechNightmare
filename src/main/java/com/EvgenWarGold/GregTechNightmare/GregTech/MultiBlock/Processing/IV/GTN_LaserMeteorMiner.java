package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.IV;

import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.TierEU.RECIPE_LuV;
import static gregtech.api.enums.TierEU.RECIPE_ZPM;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.ElementBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.EvgenWarGold.GregTechNightmare.Utils.BlockHighlighter;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;
import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.objects.ItemData;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.OverclockCalculator;
import it.unimi.dsi.fastutil.Pair;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import vazkii.botania.common.item.equipment.tool.terrasteel.ItemTerraPick;

public class GTN_LaserMeteorMiner extends GTN_MultiBlockBase<GTN_LaserMeteorMiner> {

    private int fortuneTier = 0;
    private int multiTier = 0;
    private int currentRadius = MAX_RADIUS;
    private int xDrill, yDrill, zDrill;
    private int xStart, yStart, zStart;
    private boolean isStartInitialized = false;
    private boolean hasFinished = true;
    private boolean isWaiting = false;
    private static final int MAX_RADIUS = 40;
    private static final int distanceFromMeteor = 48;
    private final List<ItemStack> res = new ArrayList<>();
    private boolean showBlockHighlight = true;
    private final BlockHighlighter blockHighlighter = new BlockHighlighter();
    private int currentLayer = 0;
    private int minY = 0;
    private int maxY = 0;
    private boolean isLayerInitialized = false;

    public GTN_LaserMeteorMiner(int id, String name) {
        super(id, name);
    }

    public GTN_LaserMeteorMiner(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_LaserMeteorMiner>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "LaserMeteorMiner",
                // spotless:off
                new String[][]{
                    {"                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","         B         ","        B B        ","         B         ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   "},
                    {"                   ","                   ","                   ","                   ","                   ","                   ","                   ","         B         ","        B B        ","       B   B       ","        B B        ","         B         ","                   ","                   ","                   ","                   ","                   ","                   ","                   "},
                    {"                   ","                   ","                   ","                   ","                   ","                   ","         B         ","       B   B       ","                   ","      B     B      ","                   ","       B   B       ","         B         ","                   ","                   ","                   ","                   ","                   ","                   "},
                    {"                   ","                   ","                   ","                   ","                   ","         B         ","      B     B      ","                   ","                   ","     B   B   B     ","                   ","                   ","      B     B      ","         B         ","                   ","                   ","                   ","                   ","                   "},
                    {"                   ","                   ","                   ","                   ","         B         ","     B       B     ","                   ","                   ","    B              ","    B    C    B    ","                   ","                   ","                   ","     B       B     ","         B         ","                   ","                   ","                   ","                   "},
                    {"                   ","                   ","                   ","         B         ","    B         B    ","                   ","                   ","                   ","         C         ","   B    CCC    B   ","         C         ","                   ","                   ","                   ","    B         B    ","         B         ","                   ","                   ","                   "},
                    {"                   ","                   ","    BBBBBBBBBBB    ","   BBEEEEEEEEEBB   ","  BBEE       EEBB  ","  BEE         EEB  ","  BE           EB  ","  BE           EB  ","  BE     C     EB  ","  BE    CCC    EB  ","  BE     C     EB  ","  BE           EB  ","  BE           EB  ","  BEE         EEB  ","  BBEE       EEBB  ","   BBEEEEEEEEEBB   ","    BBBBBBBBBBB    ","                   ","                   "},
                    {"                   ","                   ","                   ","         B         ","      EEEEEEE      ","     EE     EE     ","    EE       EE    ","    E         E    ","    E    C    E    ","   BE   CCC   EB   ","    E    C    E    ","    E         E    ","    EE       EE    ","     EE     EE     ","      EEEEEEE      ","         B         ","                   ","                   ","                   "},
                    {"                   ","                   ","                   ","                   ","         B         ","       EEEEE       ","      EE   EE      ","     EE  C  EE     ","     E  CCC  E     ","    BE CCCCC EB    ","     E  CCC  E     ","     EE  C  EE     ","      EE   EE      ","       EEEEE       ","         B         ","                   ","                   ","                   ","                   "},
                    {"                   ","                   ","                   ","                   ","                   ","         B         ","        EEE        ","       EEEEE       ","      EEEEEEE      ","     BEEECEEEB     ","      EEEEEEE      ","       EEEEE       ","        EEE        ","         B         ","                   ","                   ","                   ","                   ","                   "},
                    {"                   ","                   ","                   ","                   ","                   ","                   ","         B         ","        BBB        ","       BDADB       ","      BBACABB      ","       BDADB       ","        BBB        ","         B         ","                   ","                   ","                   ","                   ","                   ","                   "},
                    {"                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","        DAD        ","        ACA        ","        DAD        ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   "},
                    {"                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","        DAD        ","        ACA        ","        DAD        ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   "},
                    {"                   ","                   ","                   ","                   ","                   ","                   ","                   ","        D~D        ","       DDDDD       ","       DDCDD       ","       DDDDD       ","        DDD        ","                   ","                   ","                   ","                   ","                   ","                   ","                   "},
                    {"                   ","                   ","                   ","                   ","                   ","                   ","        DDD        ","       DDDDD       ","      DDCCCDD      ","      DDCCCDD      ","      DDCCCDD      ","       DDDDD       ","        DDD        ","                   ","                   ","                   ","                   ","                   ","                   "},
                    {"                   ","                   ","                   ","                   ","        D D        ","       D   D       ","      A     A      ","     D       D     ","    D         D    ","                   ","    D         D    ","     D       D     ","      A     A      ","       D   D       ","        D D        ","                   ","                   ","                   ","                   "},
                    {"                   ","                   ","                   ","        D D        ","                   ","       D   D       ","      A     A      ","     D       D     ","   D           D   ","                   ","   D           D   ","     D       D     ","      A     A      ","       D   D       ","                   ","        D D        ","                   ","                   ","                   "},
                    {"                   ","                   ","        D D        ","                   ","                   ","       D   D       ","      A     A      ","     D       D     ","  D             D  ","                   ","  D             D  ","     D       D     ","      A     A      ","       D   D       ","                   ","                   ","        D D        ","                   ","                   "},
                    {"         D         ","        D D        ","       D   D       ","       D   D       ","       D   D       ","      D     D      ","     DD     DD     ","  DDD         DDD  "," D               D ","D                 D"," D               D ","  DDD         DDD  ","     DD     DD     ","      D     D      ","       D   D       ","       D   D       ","       D   D       ","        D D        ","         D         "}
                },
                //spotless:on
                new MultiblockOffsets(9, 13, 7),
                new MultiblockArea(19, 19, 19),
                1,
                GTN_Casings.StructuralSolarCasing));
    }

    @Override
    public GTN_LaserMeteorMiner createNewMetaEntity() {
        return new GTN_LaserMeteorMiner(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addAuthor(Authors.EVGEN_WAR_GOLD)
            .addInputBus()
            .addOutputBus()
            .addEnergyHatch()
            .addMaintenanceHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.TOTTO;
    }

    @Override
    public IStructureDefinition<GTN_LaserMeteorMiner> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addElement('A', chainAllGlasses())
                .addElement('C', GTN_Casings.NaquadahCoilBlock.asElement())
                .addElement('B', GTN_Casings.CleanStainlessSteelMachineCasing.asElement())
                .addElement('E', GTN_Casings.ThermallyInsulatedCasing.asElement())
                .addElement(
                    'D',
                    ElementBuilder.create(GTN_LaserMeteorMiner.class, this)
                        .casing(mainCasing)
                        .hatches(InputBus, OutputBus, Energy, Maintenance)
                        .build()));
    }

    @Override
    public OverclockType getOverclockType() {
        return OverclockType.NONE;
    }

    @Override
    protected IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> (d.flag & (ForgeDirection.UP.flag | ForgeDirection.DOWN.flag)) == 0 && r.isNotRotated()
            && !f.isVerticallyFliped();
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setInteger("fortune", this.fortuneTier);
        tag.setInteger("tier", this.multiTier);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(tr("tier." + tag.getInteger("tier")) + EnumChatFormatting.RESET);
        currentTip.add(tr("fortune." + tag.getInteger("fortune")) + EnumChatFormatting.RESET);
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);

        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            showBlockHighlight = !showBlockHighlight;

            if (getBaseMetaTileEntity() == null) return;

            getBaseMetaTileEntity().issueClientUpdate();
        })
            .setPlayClickSound(true)
            .setBackground(() -> new IDrawable[] { GTUITextures.BUTTON_STANDARD, GTUITextures.OVERLAY_BUTTON_SHUFFLE })
            .setPos(new Pos2d(174, 112))
            .addTooltip(tr("button.highlight"))
            .setSize(16, 16));
    }

    protected void setElectricityStats() {
        this.mOutputItems = new ItemStack[0];

        this.mEfficiency = 10000;
        this.mEfficiencyIncrease = 10000;

        OverclockCalculator calculator = new OverclockCalculator().setEUt(getAverageInputVoltage())
            .setAmperage(getMaxInputAmps())
            .setRecipeEUt(this.multiTier == 2 ? RECIPE_ZPM : RECIPE_LuV)
            .setDuration(this.multiTier == 2 ? 10 : 1);
        calculator.calculate();
        this.mMaxProgresstime = (isWaiting) ? 20 * 10 : calculator.getDuration();
        this.lEUt = (int) (isWaiting ? 0 : -calculator.getConsumption());
    }

    private boolean isEnergyEnough() {
        long requiredEnergy = this.multiTier == 2 ? 128_000 : 30_000;
        for (MTEHatchEnergy energyHatch : mEnergyHatches) {
            requiredEnergy -= energyHatch.getEUVar();
            if (requiredEnergy <= 0) return true;
        }
        return false;
    }

    private void setReady() {
        this.findBestRadius();
        this.initializeDrillPos();
    }

    private void initializeDrillPos() {
        this.xDrill = this.xStart - currentRadius;
        this.yDrill = this.yStart - currentRadius;
        this.zDrill = this.zStart - currentRadius;

        this.isStartInitialized = true;
        this.hasFinished = false;

        if (this.multiTier == 2) {
            this.currentLayer = 0;
            this.isLayerInitialized = false;
            this.initializeLayerBounds();
        }
    }

    private boolean checkCenter() {
        if (getBaseMetaTileEntity() == null) return false;
        return !getBaseMetaTileEntity().getWorld()
            .isAirBlock(xStart, yStart + 1, zStart);
    }

    private void findBestRadius() {
        currentRadius = MAX_RADIUS;
        int delta = 0;
        for (int zCoord = zStart - currentRadius; delta < MAX_RADIUS - 1; zCoord++) {
            if (getBaseMetaTileEntity() == null) return;

            if (!getBaseMetaTileEntity().getWorld()
                .isAirBlock(xStart, yStart, zCoord)) {
                break;
            }
            delta++;
        }
        currentRadius -= delta;
    }

    private void setStartCoords() {
        if (getBaseMetaTileEntity() == null) return;
        IGregTechTileEntity gte = getBaseMetaTileEntity();
        ForgeDirection direction = getExtendedFacing().getRelativeBackInWorld();

        ForgeDirection facing = gte.getBackFacing();
        if (facing == ForgeDirection.NORTH || facing == ForgeDirection.SOUTH) {
            xStart = gte.getXCoord();
            zStart = 2 * direction.offsetZ + gte.getZCoord();
        } else {
            xStart = 2 * direction.offsetX + gte.getXCoord();
            zStart = gte.getZCoord();
        }
        yStart = distanceFromMeteor + 15 + gte.getYCoord();
    }

    private ItemStack multiplyStackSize(ItemStack itemStack) {
        if (getBaseMetaTileEntity() == null) return null;
        itemStack.stackSize *= getBaseMetaTileEntity().getRandomNumber(this.fortuneTier + 1) + 1;
        return itemStack;
    }

    private boolean doUseMaceratorRecipe(ItemStack currentItem) {
        ItemData itemData = GTOreDictUnificator.getItemData(currentItem);
        return itemData == null || itemData.mPrefix != OrePrefixes.crushed && itemData.mPrefix != OrePrefixes.dustImpure
            && itemData.mPrefix != OrePrefixes.dust
            && itemData.mPrefix != OrePrefixes.gem
            && itemData.mPrefix != OrePrefixes.gemChipped
            && itemData.mPrefix != OrePrefixes.gemExquisite
            && itemData.mPrefix != OrePrefixes.gemFlawed
            && itemData.mPrefix != OrePrefixes.gemFlawless
            && itemData.mMaterial.mMaterial != Materials.Oilsands;
    }

    private void moveToNextColumn() {
        if (this.xDrill <= this.xStart + currentRadius) {
            this.xDrill++;
        } else if (this.yDrill <= this.yStart + currentRadius) {
            this.xDrill = this.xStart - currentRadius;
            this.yDrill++;
        } else {
            this.hasFinished = true;
        }
    }

    private void moveToNextLayer() {
        if (this.currentLayer < (this.maxY - this.minY)) {
            this.currentLayer++;
            this.yDrill = this.minY + this.currentLayer;
        } else {
            this.hasFinished = true;
            this.isLayerInitialized = false;
        }
    }

    private void initializeLayerBounds() {
        if (getBaseMetaTileEntity() == null) return;
        World world = getBaseMetaTileEntity().getWorld();

        this.minY = yStart + currentRadius;
        for (int y = yStart - currentRadius; y <= yStart + currentRadius; y++) {
            for (int xOffset = -2; xOffset <= 2; xOffset++) {
                int currentX = this.xStart + xOffset;
                if (!world.isAirBlock(currentX, y, zStart)) {
                    if (y < this.minY) {
                        this.minY = y;
                    }
                    break;
                }
            }
        }

        this.maxY = yStart - currentRadius;
        for (int y = yStart + currentRadius; y >= yStart - currentRadius; y--) {
            for (int xOffset = -2; xOffset <= 2; xOffset++) {
                int currentX = this.xStart + xOffset;
                if (!world.isAirBlock(currentX, y, zStart)) {
                    if (y > this.maxY) {
                        this.maxY = y;
                    }
                    break;
                }
            }
        }

        if (this.minY > this.maxY) {
            this.minY = yStart - currentRadius;
            this.maxY = yStart + currentRadius;
        }

        this.yDrill = this.minY;
        this.currentLayer = 0;
        this.isLayerInitialized = true;
    }

    private void setBlockToAir(World world, int x, int y, int z) {
        if (y < 0 || y > 255 || !world.blockExists(x, y, z)) {
            return;
        }

        Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
        if (chunk == null) return;

        int localX = x & 15;
        int localY = y & 15;
        int localZ = z & 15;
        int storageIndex = y >> 4;

        ExtendedBlockStorage storage = chunk.getBlockStorageArray()[storageIndex];
        if (storage == null) {
            storage = new ExtendedBlockStorage(storageIndex << 4, world.provider.hasNoSky);
            chunk.getBlockStorageArray()[storageIndex] = storage;
        }

        storage.func_150818_a(localX, localY, localZ, Blocks.air);
        storage.setExtBlockMetadata(localX, localY, localZ, 0);

        if (!world.isRemote) {
            world.markBlockForUpdate(x, y, z);
        }

        chunk.isModified = true;
    }

    private void mineBlock(int currentX, int currentY, int currentZ) {
        IGregTechTileEntity gte = getBaseMetaTileEntity();

        if (gte == null) return;
        World world = gte.getWorld();

        Block target = gte.getBlock(currentX, currentY, currentZ);
        if (target.getBlockHardness(world, currentX, currentY, currentZ) > 0) {
            final int targetMeta = gte.getMetaID(currentX, currentY, currentZ);
            Collection<ItemStack> drops = target.getDrops(world, currentX, currentY, currentZ, targetMeta, 0);
            if (GTUtility.isOre(target, targetMeta)) {
                res.addAll(getOutputByDrops(drops));
            } else {
                res.addAll(drops);
            }
            setBlockToAir(world, currentX, currentY, currentZ);
        }
    }

    private Collection<ItemStack> getOutputByDrops(Collection<ItemStack> oreBlockDrops) {
        long voltage = getMaxInputVoltage();
        Collection<ItemStack> outputItems = new HashSet<>();
        oreBlockDrops.forEach(currentItem -> {
            if (!doUseMaceratorRecipe(currentItem)) {
                outputItems.add(multiplyStackSize(currentItem));
                return;
            }
            GTRecipe tRecipe = RecipeMaps.maceratorRecipes.findRecipeQuery()
                .items(currentItem)
                .voltage(voltage)
                .find();
            if (tRecipe == null) {
                outputItems.add(currentItem);
                return;
            }
            for (int i = 0; i < tRecipe.mOutputs.length; i++) {
                ItemStack recipeOutput = tRecipe.mOutputs[i].copy();
                if (getBaseMetaTileEntity() == null) return;
                if (getBaseMetaTileEntity().getRandomNumber(10000) < tRecipe.getOutputChance(i))
                    multiplyStackSize(recipeOutput);
                outputItems.add(recipeOutput);
            }
        });
        return outputItems;
    }

    private void startMining(int tier) {
        switch (tier) {
            case 1 -> this.mineRow();
            case 2 -> this.mineFullLayer();
            default -> throw new IllegalArgumentException("Invalid Multiblock Tier");
        }
    }

    private void mineFullLayer() {
        if (!this.isLayerInitialized) {
            this.initializeLayerBounds();
        }

        for (int xOffset = -currentRadius; xOffset <= currentRadius; xOffset++) {
            int currentX = this.xStart + xOffset;

            for (int zOffset = -currentRadius; zOffset <= currentRadius; zOffset++) {
                int currentZ = this.zStart + zOffset;

                if (getBaseMetaTileEntity() == null) return;

                World world = getBaseMetaTileEntity().getWorld();
                if (!world.blockExists(currentX, this.yDrill, currentZ)) {
                    continue;
                }

                if (!world.isAirBlock(currentX, this.yDrill, currentZ)) {
                    this.mineBlock(currentX, this.yDrill, currentZ);
                }
            }
        }

        this.moveToNextLayer();
    }

    private void mineRow() {
        int currentX = this.xDrill;
        int currentY = this.yDrill;

        if (getBaseMetaTileEntity() == null) return;

        World world = getBaseMetaTileEntity().getWorld();

        while (world.isAirBlock(currentX, currentY, this.zStart)) {

            this.moveToNextColumn();
            if (this.hasFinished) return;
            currentX = this.xDrill;
            currentY = this.yDrill;
        }

        int opposite = 0;
        for (int z = -currentRadius; z <= (currentRadius - opposite); z++) {
            int currentZ = this.zStart + z;
            if (!world.isAirBlock(this.xDrill, this.yDrill, currentZ)) {
                this.mineBlock(this.xDrill, this.yDrill, currentZ);
            } else {
                opposite++;
            }
        }
        this.moveToNextColumn();
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        this.setMultiTier(getMultiTier(mInventory[1]));
        this.setFortuneTier();

        if (this.multiTier <= 0) {
            return SimpleCheckRecipeResult.ofFailure("missing_schematic");
        }

        long availableEUt = getMaxInputPower();

        switch (this.multiTier) {
            case 1 -> {
                if (availableEUt < 32_768) {
                    return CheckRecipeResultRegistry.insufficientPower(32_768);
                }
            }

            case 2 -> {
                if (availableEUt < 131_072) {
                    return CheckRecipeResultRegistry.insufficientPower(131_072);
                }
            }
        }

        this.setElectricityStats();

        if (!isStartInitialized) {
            this.setStartCoords();
            this.findBestRadius();
            this.initializeDrillPos();
        }

        if (!hasFinished) {
            this.startMining(this.multiTier);
            GTN_InventoryUtils.mergeAllSameItems(res);
            mOutputItems = res.toArray(new ItemStack[0]);
            res.clear();
        } else {
            this.isWaiting = true;
            this.setElectricityStats();
            boolean isReady = checkCenter();
            if (isReady) {
                this.isWaiting = false;
                this.setElectricityStats();
                this.setReady();
                this.hasFinished = false;
            } else {
                return SimpleCheckRecipeResult.ofSuccess("meteor_waiting");
            }
        }

        return SimpleCheckRecipeResult.ofSuccess("meteor_mining");
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("currentRadius", currentRadius);
        aNBT.setInteger("xDrill", xDrill);
        aNBT.setInteger("yDrill", yDrill);
        aNBT.setInteger("zDrill", zDrill);
        aNBT.setInteger("xStart", xStart);
        aNBT.setInteger("yStart", yStart);
        aNBT.setInteger("zStart", zStart);
        aNBT.setBoolean("isStartInitialized", isStartInitialized);
        aNBT.setBoolean("hasFinished", hasFinished);
        aNBT.setBoolean("isWaiting", isWaiting);
        aNBT.setInteger("multiTier", multiTier);
        aNBT.setInteger("fortuneTier", fortuneTier);
        aNBT.setBoolean("showBlockHighlight", showBlockHighlight);
        aNBT.setInteger("currentLayer", currentLayer);
        aNBT.setInteger("minY", minY);
        aNBT.setInteger("maxY", maxY);
        aNBT.setBoolean("isLayerInitialized", isLayerInitialized);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        currentRadius = aNBT.getInteger("currentRadius");
        xDrill = aNBT.getInteger("xDrill");
        yDrill = aNBT.getInteger("yDrill");
        zDrill = aNBT.getInteger("zDrill");
        xStart = aNBT.getInteger("xStart");
        yStart = aNBT.getInteger("yStart");
        zStart = aNBT.getInteger("zStart");
        isStartInitialized = aNBT.getBoolean("isStartInitialized");
        hasFinished = aNBT.getBoolean("hasFinished");
        isWaiting = aNBT.getBoolean("isWaiting");
        multiTier = aNBT.getInteger("multiTier");
        fortuneTier = aNBT.getInteger("fortuneTier");
        showBlockHighlight = aNBT.getBoolean("showBlockHighlight");
        currentLayer = aNBT.getInteger("currentLayer");
        minY = aNBT.getInteger("minY");
        maxY = aNBT.getInteger("maxY");
        isLayerInitialized = aNBT.getBoolean("isLayerInitialized");
    }

    private void setFortuneTier() {
        this.fortuneTier = 0;
        if (!mInputBusses.isEmpty()) {
            Optional<ItemStack> input = Optional.ofNullable(
                mInputBusses.get(0)
                    .getInventoryHandler()
                    .getStackInSlot(0));
            input.ifPresent(itemStack -> this.fortuneTier = getFortuneTier(itemStack));
        }
    }

    private static int getFortuneTier(ItemStack itemStack) {
        if (itemStack == null || itemStack.stackSize < 1) return 0;

        if (itemStack.getItem() instanceof ItemTerraPick) {
            return ItemTerraPick.getLevel(itemStack) + 1;
        }

        return 0;
    }

    private int getMultiTier(ItemStack inventory) {
        if (inventory == null || inventory.stackSize < 1) return 0;
        return GTN_ItemList.MeteorMinerSchematic2.equal(inventory) ? 2
            : GTN_ItemList.MeteorMinerSchematic1.equal(inventory) ? 1 : 0;
    }

    private void setMultiTier(int tier) {
        this.multiTier = tier;
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        BlockHighlighter.registerHighlighter(this, blockHighlighter);
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        BlockHighlighter.removeHighlighter(this);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);

        if (aBaseMetaTileEntity.isServerSide()) {
            if (isStartInitialized) {
                blockHighlighter.updatePosition(
                    xStart,
                    yStart,
                    zStart,
                    aBaseMetaTileEntity.getWorld().provider.dimensionId,
                    showBlockHighlight);
            } else {
                blockHighlighter.disable();
            }
        }
    }

    @Override
    protected String tr(String key) {
        return GTN_Utils.tr("multiblock.LaserMeteorMiner." + key);
    }

    @Override
    protected Pair<Integer, Integer> getMinMaxEnergyTier() {
        return Pair.of(VoltageIndex.IV, VoltageIndex.LuV);
    }
}
