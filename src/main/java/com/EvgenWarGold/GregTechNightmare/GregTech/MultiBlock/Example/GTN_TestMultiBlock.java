package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Example;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.createItem;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.createItemsWithLong;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.fluidListToArray;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.itemListToArray;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.removeFluids;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.removeItems;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.Dynamo;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.Muffler;
import static gregtech.api.enums.HatchElement.MultiAmpEnergy;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_ProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.enums.Materials;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;

public class GTN_TestMultiBlock extends GTN_MultiBlockBase<GTN_TestMultiBlock> {

    public GTN_TestMultiBlock(int id, String name) {
        super(id, name);
    }

    public GTN_TestMultiBlock(String name) {
        super(name);
    }

    @Override
    public int getOffsetHorizontal() {
        return 1;
    }

    @Override
    public int getOffsetVertical() {
        return 1;
    }

    @Override
    public int getOffsetDepth() {
        return 0;
    }

    @Override
    public String getStructurePieceMain() {
        return "TST_TestMultiBlock";
    }

    @Override
    public GTN_Casings getMainCasings() {
        return GTN_Casings.SolidSteelMachineCasing;
    }

    @Override
    public GTN_TestMultiBlock createNewMetaEntity() {
        return new GTN_TestMultiBlock(this.mName);
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
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.maceratorRecipes;
    }

    @Override
    public String[][] getShape() {
        return new String[][] { { "AAA", "AAA", "AAA" }, { "A~A", "A A", "AAA" }, { "AAA", "AAA", "AAA" } };
    }

    @Override
    public void createGtnTooltip(MultiblockTooltipBuilder builder) {}

    @Override
    public String getMachineType() {
        return "TEST";
    }

    @Override
    protected int getMainCasingMax() {
        return 10;
    }

    @Override
    public IStructureDefinition<GTN_TestMultiBlock> getStructureDefinition() {
        return IStructureDefinition.<GTN_TestMultiBlock>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement(
                'A',
                buildHatchAdder(GTN_TestMultiBlock.class)
                    .atLeast(
                        InputHatch,
                        OutputHatch,
                        InputBus,
                        OutputBus,
                        Energy,
                        MultiAmpEnergy,
                        ExoticEnergy,
                        Maintenance,
                        Muffler,
                        Dynamo)
                    .casingIndex(getMainCasings().textureId)
                    .dot(1)
                    .buildAndChain(
                        onElementPass(
                            GTN_TestMultiBlock::mainCasingAdd,
                            ofBlock(getMainCasings().getBlock(), getMainCasings().meta))))
            .build();
    }

    private static final List<ItemStack> removeItems = new ArrayList<>();
    private static final List<ItemStack> addItems = new ArrayList<>();

    private static final List<FluidStack> removeFluids = new ArrayList<>();
    private static final List<FluidStack> addFluids = new ArrayList<>();

    static {
        removeItems.add(createItem(Items.iron_ingot, 5));
        removeItems.add(createItem(Items.coal, 10));
        removeItems.add(createItem(Blocks.dirt, 5));

        addItems.addAll(createItemsWithLong(Items.iron_ingot, 20_000));
        addItems.addAll(createItemsWithLong(GTN_OreDict.getIngot(Materials.Steel), 100));

        removeFluids.add(Materials.Water.getFluid(1_000));
        removeFluids.add(Materials.Lava.getFluid(2_000));

        addFluids.add(Materials.Acetone.getFluid(1_000));
    }

    protected ProcessingLogic createProcessingLogic() {
        return new GTN_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                List<ItemStack> inputItems = getStoredInputs();
                List<FluidStack> inputFluids = getStoredFluids();

                if (removeItems(inputItems, removeItems, true) && removeFluids(inputFluids, removeFluids, true)) {
                    outputItems = itemListToArray(addItems);
                    outputFluids = fluidListToArray(addFluids);

                    if (isOutputItemsFull(outputItems, machine)) {
                        return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
                    }

                    if (isOutputFluidsFull(outputFluids, machine)) {
                        return CheckRecipeResultRegistry.FLUID_OUTPUT_FULL;
                    }

                    removeItems(inputItems, removeItems);
                    removeFluids(inputFluids, removeFluids);

                    setDurationInSeconds(1);

                    return CheckRecipeResultRegistry.SUCCESSFUL;
                }
                return CheckRecipeResultRegistry.NO_RECIPE;
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }
}
