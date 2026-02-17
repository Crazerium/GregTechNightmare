package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.STEAM;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.Collections;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HatchElement;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_ProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.EvgenWarGold.GregTechNightmare.Utils.VoidMinerUtils;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;

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
    public int getOffsetHorizontal() {
        return 1;
    }

    @Override
    public int getOffsetVertical() {
        return 6;
    }

    @Override
    public int getOffsetDepth() {
        return 0;
    }

    @Override
    public String getStructurePieceMain() {
        return this.mName;
    }

    @Override
    public GTN_Casings getMainCasings() {
        return GTN_Casings.BronzeFireboxCasing;
    }

    @Override
    public GTN_BronzeVoidMiner createNewMetaEntity() {
        return new GTN_BronzeVoidMiner(this.mName);
    }

    @Override
    public OverclockType getOverclockType() {
        return OverclockType.NONE;
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return true;
    }

    @Override
    public String[][] getShape() {
        return new String[][] { { "   ", " C ", "   " }, { "   ", " C ", "   " }, { "   ", " C ", "   " },
            { " C ", "CAC", " C " }, { " C ", "CAC", " C " }, { " C ", "CAC", " C " }, { "B~B", "BBB", "BBB" } };
    }

    @Override
    public void createGtnTooltip(MultiblockTooltipBuilder builder) {
        builder.addInfo(tr("tooltip.00"))
            .addInfo(tr("tooltip.01"))
            .addInfo(tr("tooltip.02"))
            .addInfo(Constants.AUTHOR_EVGEN_WAR_GOLD)
            .beginStructureBlock(5, 6, 5, true)
            .addInputHatch(EnumChatFormatting.GOLD + "1", 1)
            .addSteamOutputBus(EnumChatFormatting.GOLD + "1", 1);
    }

    @Override
    public IStructureDefinition<GTN_BronzeVoidMiner> getStructureDefinition() {
        return IStructureDefinition.<GTN_BronzeVoidMiner>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement('C', ofFrame(Materials.Bronze))
            .addElement('A', GTN_Casings.BronzePlatedBricks.asElement())
            .addElement(
                'B',
                ofChain(
                    buildHatchAdder(GTN_BronzeVoidMiner.class).atLeast(GTN_HatchElement.SteamInputHatch)
                        .casingIndex(getMainCasings().textureId)
                        .hatchIds(31040)
                        .dot(1)
                        .build(),
                    buildHatchAdder(GTN_BronzeVoidMiner.class).atLeast(GTN_HatchElement.SteamOutputBus)
                        .casingIndex(getMainCasings().textureId)
                        .dot(1)
                        .buildAndChain(
                            onElementPass(
                                GTN_BronzeVoidMiner::mainCasingAdd,
                                ofBlock(getMainCasings().getBlock(), getMainCasings().meta)))))
            .build();
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
}
