package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.IV;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.TierEU.RECIPE_MV;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.EvgenWarGold.GregTechNightmare.ModItems.ModItems;
import com.EvgenWarGold.GregTechNightmare.Utils.BlockHighlighter;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.objects.ItemData;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class GTN_LaserMeteorMiner extends GTN_MultiBlockBase<GTN_LaserMeteorMiner> {

    private int fortuneTier = 0;
    private int multiTier = 0;
    private int currentRadius = MAX_RADIUS;
    private int xDrill, yDrill, zDrill;
    private int xStart, yStart, zStart;
    private boolean isStartInitialized = false;
    private boolean hasFinished = true;
    private boolean isWaiting = false;
    private boolean isResetting = false;
    private static final int MAX_RADIUS = 40;
    private static final int distanceFromMeteor = 48;
    Collection<ItemStack> res = new HashSet<>();
    private boolean showBlockHighlight = true;
    private final BlockHighlighter blockHighlighter = new BlockHighlighter();

    public GTN_LaserMeteorMiner(int id, String name) {
        super(id, name);
    }

    public GTN_LaserMeteorMiner(String name) {
        super(name);
    }

    @Override
    public int getOffsetHorizontal() {
        return 9;
    }

    @Override
    public int getOffsetVertical() {
        return 13;
    }

    @Override
    public int getOffsetDepth() {
        return 7;
    }

    @Override
    public GTN_Casings getMainCasings() {
        return GTN_Casings.StructuralSolarCasing;
    }

    @Override
    public GTN_LaserMeteorMiner createNewMetaEntity() {
        return new GTN_LaserMeteorMiner(this.mName);
    }

    @Override
    public OverclockType getOverclockType() {
        return OverclockType.NONE;
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return false;
    }

    @Override
    public String[][] getShape() {
        return new String[][] {
            { "                   ", "                   ", "                   ", "                   ",
                "                   ", "                   ", "                   ", "                   ",
                "         J         ", "        J J        ", "         J         ", "                   ",
                "                   ", "                   ", "                   ", "                   ",
                "                   ", "                   ", "                   " },
            { "                   ", "                   ", "                   ", "                   ",
                "                   ", "                   ", "                   ", "         J         ",
                "        J J        ", "       J   J       ", "        J J        ", "         J         ",
                "                   ", "                   ", "                   ", "                   ",
                "                   ", "                   ", "                   " },
            { "                   ", "                   ", "                   ", "                   ",
                "                   ", "                   ", "         J         ", "       J   J       ",
                "                   ", "      J     J      ", "                   ", "       J   J       ",
                "         J         ", "                   ", "                   ", "                   ",
                "                   ", "                   ", "                   " },
            { "                   ", "                   ", "                   ", "                   ",
                "                   ", "         J         ", "      J     J      ", "                   ",
                "                   ", "     J   J   J     ", "                   ", "                   ",
                "      J     J      ", "         J         ", "                   ", "                   ",
                "                   ", "                   ", "                   " },
            { "                   ", "                   ", "                   ", "                   ",
                "         J         ", "     J       J     ", "                   ", "                   ",
                "    J              ", "    J    B    J    ", "                   ", "                   ",
                "                   ", "     J       J     ", "         J         ", "                   ",
                "                   ", "                   ", "                   " },
            { "                   ", "                   ", "                   ", "         J         ",
                "    J         J    ", "                   ", "                   ", "                   ",
                "         I         ", "   J    IBI    J   ", "         I         ", "                   ",
                "                   ", "                   ", "    J         J    ", "         J         ",
                "                   ", "                   ", "                   " },
            { "                   ", "                   ", "    JJJJJJJJJJJ    ", "   JJLLLLLLLLLJJ   ",
                "  JJLL       LLJJ  ", "  JLL         LLJ  ", "  JL           LJ  ", "  JL           LJ  ",
                "  JL     I     LJ  ", "  JL    IBI    LJ  ", "  JL     I     LJ  ", "  JL           LJ  ",
                "  JL           LJ  ", "  JLL         LLJ  ", "  JJLL       LLJJ  ", "   JJLLLLLLLLLJJ   ",
                "    JJJJJJJJJJJ    ", "                   ", "                   " },
            { "                   ", "                   ", "                   ", "         J         ",
                "      LLLLLLL      ", "     LL     LL     ", "    LL       LL    ", "    L         L    ",
                "    L    I    L    ", "   JL   IBI   LJ   ", "    L    I    L    ", "    L         L    ",
                "    LL       LL    ", "     LL     LL     ", "      LLLLLLL      ", "         J         ",
                "                   ", "                   ", "                   " },
            { "                   ", "                   ", "                   ", "                   ",
                "         J         ", "       LLLLL       ", "      LL   LL      ", "     LL  I  LL     ",
                "     L  III  L     ", "    JL IIBII LJ    ", "     L  III  L     ", "     LL  I  LL     ",
                "      LL   LL      ", "       LLLLL       ", "         J         ", "                   ",
                "                   ", "                   ", "                   " },
            { "                   ", "                   ", "                   ", "                   ",
                "                   ", "         J         ", "        LLL        ", "       LLLLL       ",
                "      LLLLLLL      ", "     JLLLBLLLJ     ", "      LLLLLLL      ", "       LLLLL       ",
                "        LLL        ", "         J         ", "                   ", "                   ",
                "                   ", "                   ", "                   " },
            { "                   ", "                   ", "                   ", "                   ",
                "                   ", "                   ", "         J         ", "        JJJ        ",
                "       JKAKJ       ", "      JJABAJJ      ", "       JKAKJ       ", "        JJJ        ",
                "         J         ", "                   ", "                   ", "                   ",
                "                   ", "                   ", "                   " },
            { "                   ", "                   ", "                   ", "                   ",
                "                   ", "                   ", "                   ", "                   ",
                "        KAK        ", "        ABA        ", "        KAK        ", "                   ",
                "                   ", "                   ", "                   ", "                   ",
                "                   ", "                   ", "                   " },
            { "                   ", "                   ", "                   ", "                   ",
                "                   ", "                   ", "                   ", "                   ",
                "        KAK        ", "        ABA        ", "        KAK        ", "                   ",
                "                   ", "                   ", "                   ", "                   ",
                "                   ", "                   ", "                   " },
            { "                   ", "                   ", "                   ", "                   ",
                "                   ", "                   ", "                   ", "        K~K        ",
                "       KKKKK       ", "       KKBKK       ", "       KKKKK       ", "        KKK        ",
                "                   ", "                   ", "                   ", "                   ",
                "                   ", "                   ", "                   " },
            { "                   ", "                   ", "                   ", "                   ",
                "                   ", "                   ", "        KKK        ", "       KKKKK       ",
                "      KKBBBKK      ", "      KKBBBKK      ", "      KKBBBKK      ", "       KKKKK       ",
                "        KKK        ", "                   ", "                   ", "                   ",
                "                   ", "                   ", "                   " },
            { "                   ", "                   ", "                   ", "                   ",
                "        K K        ", "       K   K       ", "      A     A      ", "     K       K     ",
                "    K         K    ", "                   ", "    K         K    ", "     K       K     ",
                "      A     A      ", "       K   K       ", "        K K        ", "                   ",
                "                   ", "                   ", "                   " },
            { "                   ", "                   ", "                   ", "        K K        ",
                "                   ", "       K   K       ", "      A     A      ", "     K       K     ",
                "   K           K   ", "                   ", "   K           K   ", "     K       K     ",
                "      A     A      ", "       K   K       ", "                   ", "        K K        ",
                "                   ", "                   ", "                   " },
            { "                   ", "                   ", "        K K        ", "                   ",
                "                   ", "       K   K       ", "      A     A      ", "     K       K     ",
                "  K             K  ", "                   ", "  K             K  ", "     K       K     ",
                "      A     A      ", "       K   K       ", "                   ", "                   ",
                "        K K        ", "                   ", "                   " },
            { "         K         ", "        K K        ", "       K   K       ", "       K   K       ",
                "       K   K       ", "      K     K      ", "     KK     KK     ", "  KKK         KKK  ",
                " K               K ", "K                 K", " K               K ", "  KKK         KKK  ",
                "     KK     KK     ", "      K     K      ", "       K   K       ", "       K   K       ",
                "       K   K       ", "        K K        ", "         K         " } };
    }

    @Override
    public void createGtnTooltip(MultiblockTooltipBuilder builder) {

    }

    @Override
    public IStructureDefinition<GTN_LaserMeteorMiner> getStructureDefinition() {
        return IStructureDefinition.<GTN_LaserMeteorMiner>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement('A', chainAllGlasses())
            .addElement('B', GTN_Casings.SuperconductingCoilBlock.asElement())
            .addElement('I', GTN_Casings.NaquadahCoilBlock.asElement())
            .addElement('J', GTN_Casings.CleanStainlessSteelMachineCasing.asElement())
            .addElement('L', GTN_Casings.ThermallyInsulatedCasing.asElement())
            .addElement(
                'K',
                buildHatchAdder(GTN_LaserMeteorMiner.class)
                    .atLeast(InputBus, OutputBus, Energy, Maintenance, InputHatch)
                    .casingIndex(getMainCasings().textureId)
                    .dot(1)
                    .buildAndChain(onElementPass(GTN_LaserMeteorMiner::mainCasingAdd, getMainCasings().asElement())))
            .build();
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
                assert getBaseMetaTileEntity() != null;
                getBaseMetaTileEntity().issueClientUpdate();
        })
            .setPlayClickSound(true)
            .setBackground(() -> new IDrawable[] { GTUITextures.BUTTON_STANDARD, GTUITextures.OVERLAY_BUTTON_CYCLIC })
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
            .setRecipeEUt(RECIPE_MV)
            .setDuration(10 * 20)
            .setAmperageOC(mEnergyHatches.size() != 1)
            .enablePerfectOC();
        calculator.calculate();
        this.mMaxProgresstime = (isWaiting) ? 20 * 10 : calculator.getDuration();
        this.mEUt = (int) (isWaiting ? 0 : -calculator.getConsumption());
    }

    private boolean isEnergyEnough() {
        long requiredEnergy = 512 + getMaxInputVoltage() * 4;
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
    }

    private boolean checkCenter() {
        return !getBaseMetaTileEntity().getWorld()
            .isAirBlock(xStart, yStart + 1, zStart);
    }

    private void findBestRadius() {
        currentRadius = MAX_RADIUS;
        int delta = 0;
        for (int zCoord = zStart - currentRadius; delta < MAX_RADIUS - 1; zCoord++) {
            if (!getBaseMetaTileEntity().getWorld()
                .isAirBlock(xStart, yStart, zCoord)) {
                break;
            }
            delta++;
        }
        currentRadius -= delta;
    }

    private void setStartCoords() {
        ForgeDirection facing = getBaseMetaTileEntity().getBackFacing();
        if (facing == ForgeDirection.NORTH || facing == ForgeDirection.SOUTH) {
            xStart = getBaseMetaTileEntity().getXCoord();
            zStart = 2 * getExtendedFacing().getRelativeBackInWorld().offsetZ + getBaseMetaTileEntity().getZCoord();
        } else {
            xStart = 2 * getExtendedFacing().getRelativeBackInWorld().offsetX + getBaseMetaTileEntity().getXCoord();
            zStart = getBaseMetaTileEntity().getZCoord();
        }
        yStart = distanceFromMeteor + 15 + getBaseMetaTileEntity().getYCoord();
    }

    private ItemStack multiplyStackSize(ItemStack itemStack) {
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

    private void mineBlock(int currentX, int currentY, int currentZ) {
        Block target = getBaseMetaTileEntity().getBlock(currentX, currentY, currentZ);
        if (target.getBlockHardness(getBaseMetaTileEntity().getWorld(), currentX, currentY, currentZ) > 0) {
            final int targetMeta = getBaseMetaTileEntity().getMetaID(currentX, currentY, currentZ);
            Collection<ItemStack> drops = target
                .getDrops(getBaseMetaTileEntity().getWorld(), currentX, currentY, currentZ, targetMeta, 0);
            if (GTUtility.isOre(target, targetMeta)) {
                res.addAll(getOutputByDrops(drops));
            } else res.addAll(drops);
            getBaseMetaTileEntity().getWorld()
                .setBlockToAir(currentX, currentY, currentZ);
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
            case 2 -> this.mineRow();
            default -> throw new IllegalArgumentException("Invalid Multiblock Tier");
        }
    }

    private void mineRow() {
        int currentX = this.xDrill;
        int currentY = this.yDrill;
        while (getBaseMetaTileEntity().getWorld() // Skips empty rows
            .isAirBlock(currentX, currentY, this.zStart)) {
            this.moveToNextColumn();
            if (this.hasFinished) return;
            currentX = this.xDrill;
            currentY = this.yDrill;
        }

        int opposite = 0;
        for (int z = -currentRadius; z <= (currentRadius - opposite); z++) {
            int currentZ = this.zStart + z;
            if (!getBaseMetaTileEntity().getWorld()
                .isAirBlock(this.xDrill, this.yDrill, currentZ)) {
                this.mineBlock(this.xDrill, this.yDrill, currentZ);
            } else opposite++;
        }
        this.moveToNextColumn();
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        if (this.multiTier != this.getMultiTier(mInventory[1])) {
            return SimpleCheckRecipeResult.ofFailure("missing_schematic");
        }

        setElectricityStats();
        if (!isEnergyEnough()) {
            stopMachine(ShutDownReasonRegistry.NONE);
            return SimpleCheckRecipeResult.ofFailure("not_enough_energy");
        }

        if (!isStartInitialized) {
            this.setStartCoords();
            this.findBestRadius();
            this.initializeDrillPos();
        }

        if (!hasFinished) {
            this.setFortuneTier();
            this.startMining(this.multiTier);
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
    }

    private void setFortuneTier() {
        this.fortuneTier = 0;
        if (this.multiTier == 2) {
            this.fortuneTier = 3;
            return;
        }
        if (!mInputBusses.isEmpty()) {
            Optional<ItemStack> input = Optional.ofNullable(
                mInputBusses.get(0)
                    .getInventoryHandler()
                    .getStackInSlot(0));
            if (input.isPresent()) {
                this.fortuneTier = getFortuneTier(input.get());
            }
        }
    }

    private static int getFortuneTier(ItemStack itemStack) {
        if (itemStack == null || itemStack.stackSize < 1) return 0;

        if (itemStack.isItemEqual(ModItems.THAUMCRAFT_ITEMS.PickaxeElemental.get(1))) return 1;
        if (itemStack.isItemEqual(ModItems.BLOOD_MAGIC_ITEMS.BoundPickaxe.get(1))) return 2;
        if (itemStack.isItemEqual(ModItems.BOTANIA_ITEMS.TerraShatterer.get(1))) return 3;
        return 0;
    }

    private int getMultiTier(ItemStack inventory) {
        if (inventory == null || inventory.stackSize < 1) return 0;
        return GTN_ItemList.MeteorMinerSchematic2.equal(inventory) ? 2
            : GTN_ItemList.MeteorMinerSchematic1.equal(inventory) ? 1 : 0;
    }

    @Override
    protected boolean GTN_checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        this.multiTier = getMultiTier(mInventory[1]);

        return multiTier > 0;
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
                    xStart, yStart, zStart,
                    aBaseMetaTileEntity.getWorld().provider.dimensionId,
                    showBlockHighlight
                );
            } else {
                blockHighlighter.disable();
            }
        }
    }

    @Override
    protected String tr(String key) {
        return GTN_Utils.tr("multiblock.LaserMeteorMiner." + key);
    }
}
