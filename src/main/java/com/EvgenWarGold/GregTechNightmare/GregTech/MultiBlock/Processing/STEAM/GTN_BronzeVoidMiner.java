package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.STEAM;

import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.SoundResource;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.ElementBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_NewHatchElement;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_ProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.EvgenWarGold.GregTechNightmare.Utils.VoidMinerUtils;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;

public class GTN_BronzeVoidMiner extends GTN_MultiBlockBase<GTN_BronzeVoidMiner> {

    private static VoidMinerUtils voidMiner = null;
    private static final int ALLOW_DIMENSION = 0;
    private static boolean preGenerated = false;
    private static final FluidStack STEAM;

    static {
        STEAM = Materials.Steam.getGas(8_000);
    }

    public GTN_BronzeVoidMiner(int id, String name) {
        super(id, name);
    }

    public GTN_BronzeVoidMiner(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_BronzeVoidMiner>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "BronzeVoidMiner",
                // spotless:off
                new String[][]{
                    {"   "," C ","   "},
                    {"   "," C ","   "},
                    {"   "," C ","   "},
                    {" C ","CAC"," C "},
                    {" C ","CAC"," C "},
                    {" C ","CAC"," C "},
                    {"B~B","BBB","BBB"}
                },
                //spotless:on
                new MultiblockOffsets(1, 6, 0),
                new MultiblockArea(3, 7, 3),
                1,
                GTN_Casings.BronzeFireboxCasing));
    }

    @Override
    public GTN_BronzeVoidMiner createNewMetaEntity() {
        return new GTN_BronzeVoidMiner(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addSteamHatch()
            .addSteamOutputBus();
    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_BronzeVoidMiner> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addElement('C', ofFrame(Materials.Bronze))
                .addElement('A', GTN_Casings.BronzePlatedBricks.asElement())
                .addElement(
                    'B',
                    ElementBuilder.create(GTN_BronzeVoidMiner.class, this)
                        .casing(mainCasing)
                        .hatches(GTN_NewHatchElement.SteamInputHatch, GTN_NewHatchElement.SteamOutputBus)
                        .build()));
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return true;
    }

    @Override
    public boolean isEnergyMultiBlock() {
        return false;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new GTN_ProcessingLogic() {

            @Override
            public @NotNull CheckRecipeResult process() {
                if (mSteamInputFluids == null) return CheckRecipeResultRegistry.NO_RECIPE;

                if (mSteamInputFluids.get(0) == null) return CheckRecipeResultRegistry.NO_RECIPE;

                FluidStack currentFluidStack = mSteamInputFluids.get(0)
                    .getFluid();

                if (currentFluidStack == null) return CheckRecipeResultRegistry.NO_RECIPE;

                if (currentFluidStack.isFluidEqual(Materials.Steam.getGas(1))) {
                    if (currentFluidStack.isFluidEqual(STEAM)) {
                        if (currentFluidStack.amount >= STEAM.amount) {
                            outputItems = voidMiner.generateStackOre(4)
                                .toArray(new ItemStack[0]);

                            if (isOutputItemsFull(outputItems, machine)) {
                                return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
                            }

                            currentFluidStack.amount = currentFluidStack.amount - STEAM.amount;
                            setDurationInSeconds(1);

                            return CheckRecipeResultRegistry.SUCCESSFUL;
                        }
                    }
                }

                return CheckRecipeResultRegistry.NO_RECIPE;
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);

        if (aBaseMetaTileEntity.isServerSide()) {
            if (aBaseMetaTileEntity.getWorld().provider.dimensionId != ALLOW_DIMENSION) {
                explodeMultiblock();
                return;
            }

            if (!preGenerated) {
                voidMiner = new VoidMinerUtils(Collections.singletonList(ALLOW_DIMENSION));
                voidMiner.initDropMap(aBaseMetaTileEntity);
                preGenerated = true;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GTCEU_LOOP_MINER;
    }
}
