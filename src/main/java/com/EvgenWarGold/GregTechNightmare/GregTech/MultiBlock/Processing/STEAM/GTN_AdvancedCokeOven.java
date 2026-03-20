package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.STEAM;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.fluidListToArray;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.itemListToArray;
import static gregtech.api.enums.HatchElement.OutputHatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_ProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.ElementBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.GTN_NewHatchElement;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.GTN_NewMultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.enums.Materials;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;

public class GTN_AdvancedCokeOven extends GTN_NewMultiBlockBase<GTN_AdvancedCokeOven> {

    private static final List<ItemStack> addItems = new ArrayList<>();
    private static final List<FluidStack> addFluids = new ArrayList<>();

    static {
        addItems.add(GTN_OreDict.getGem(Materials.Charcoal));
        addFluids.add(Materials.Creosote.getFluid(250));
    }

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
                    {"AAA","AAA","AAA"},
                    {"A~A","AAA","AAA"},
                    {"AAA","AAA","AAA"}
                },
                //spotless:on
                new MultiblockOffsets(1, 1, 0),
                new MultiblockArea(3, 3, 3),
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
            builder -> builder.addElement(
                'A',
                ElementBuilder.create(GTN_AdvancedCokeOven.class, this)
                    .hatches(GTN_NewHatchElement.SteamInputBus, GTN_NewHatchElement.SteamOutputBus, OutputHatch)
                    .casing(mainCasing)
                    .build()));
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
    protected ProcessingLogic createProcessingLogic() {
        return new GTN_ProcessingLogic() {

            @Override
            public @NotNull CheckRecipeResult process() {
                List<ItemStack> inputItems = new ArrayList<>();

                for (MTEHatchInputBus bus : mSteamInputBusses) {
                    for (ItemStack itemStack : bus.getRealInventory()) {
                        int[] oreIDs = OreDictionary.getOreIDs(itemStack);

                        for (int id : oreIDs) {
                            String name = OreDictionary.getOreName(id);

                            if ("logWood".equals(name)) {
                                inputItems.add(itemStack);
                            }
                        }
                    }
                }

                for (ItemStack itemStack : inputItems) {
                    int stackSize = itemStack.stackSize;

                    if (stackSize > 0) {

                        outputItems = itemListToArray(addItems);
                        outputFluids = fluidListToArray(addFluids);

                        if (isOutputItemsFull(outputItems, machine)) {
                            return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
                        }

                        if (isOutputFluidsFull(outputFluids, machine)) {
                            return CheckRecipeResultRegistry.FLUID_OUTPUT_FULL;
                        }

                        itemStack.stackSize -= 1;

                        setDurationInSeconds(5);

                        return CheckRecipeResultRegistry.SUCCESSFUL;
                    }
                }

                return CheckRecipeResultRegistry.NO_RECIPE;
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }
}
