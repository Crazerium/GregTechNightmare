package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.STEAM;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.fluidListToArray;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.itemListToArray;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HatchElement;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_ProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.enums.Materials;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;

public class GTN_AdvancedCokeOven extends GTN_MultiBlockBase<GTN_AdvancedCokeOven> {

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
    public GTN_Casings getMainCasings() {
        return GTN_Casings.CokeOvenCasing;
    }

    @Override
    public GTN_AdvancedCokeOven createNewMetaEntity() {
        return new GTN_AdvancedCokeOven(this.mName);
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
        return new String[][] { { "AAA", "AAA", "AAA" }, { "A~A", "AAA", "AAA" }, { "AAA", "AAA", "AAA" } };
    }

    @Override
    public void createGtnTooltip(MultiblockTooltipBuilder builder) {
        builder.addInfo(tr("tooltip.00"))
            .addInfo(tr("tooltip.01"))
            .addInfo(Constants.AUTHOR_EVGEN_WAR_GOLD)
            .beginStructureBlock(3, 3, 3, false)
            .addSteamInputBus(EnumChatFormatting.GOLD + "1", 1)
            .addSteamOutputBus(EnumChatFormatting.GOLD + "1", 1);
    }

    @Override
    public IStructureDefinition<GTN_AdvancedCokeOven> getStructureDefinition() {
        return IStructureDefinition.<GTN_AdvancedCokeOven>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement(
                'A',
                buildHatchAdder(GTN_AdvancedCokeOven.class)
                    .atLeast(GTN_HatchElement.SteamInputBus, GTN_HatchElement.SteamOutputBus, OutputHatch)
                    .casingIndex(getMainCasings().textureId)
                    .dot(1)
                    .buildAndChain(
                        onElementPass(
                            GTN_AdvancedCokeOven::mainCasingAdd,
                            ofBlock(getMainCasings().getBlock(), getMainCasings().meta))))
            .build();
    }

    @Override
    public boolean isEnergyMultiBlock() {
        return false;
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

                        setDurationInSeconds(1);

                        return CheckRecipeResultRegistry.SUCCESSFUL;
                    }
                }

                return CheckRecipeResultRegistry.NO_RECIPE;
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }
}
