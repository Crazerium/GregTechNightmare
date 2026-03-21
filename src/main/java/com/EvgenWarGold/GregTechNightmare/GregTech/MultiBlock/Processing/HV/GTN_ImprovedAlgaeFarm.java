package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.HV;

import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.ElementBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_ProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_StructureUtility;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.TierData;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.TieredElementBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;

public class GTN_ImprovedAlgaeFarm extends GTN_MultiBlockBase<GTN_ImprovedAlgaeFarm> {

    private static final int WATER_PER_RECIPE = 1000;
    private static final int FERTILIZER_MULTIPLIER = 2;
    private final TierData machinecasing = createTierData("machinecasing");
    private final TierData glass = createTierData("glass");

    public GTN_ImprovedAlgaeFarm(int id, String name) {
        super(id, name);
    }

    public GTN_ImprovedAlgaeFarm(String name) {
        super(name);
    }

    @Override
    public GTN_ImprovedAlgaeFarm createNewMetaEntity() {
        return new GTN_ImprovedAlgaeFarm(this.mName);
    }

    @Override
    public Authors getAuthor() {
        return Authors.CRAZER;
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addInputBus()
            .addInputHatch()
            .addOutputBus();
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
    public boolean isEnergyMultiBlock() {
        return false;
    }

    private static final int[] MAX_PARALLELS_BY_TIER = { 16, // LV
        32, // MV
        64, // HV
        128, // EV
        256, // IV
        512, // LuV
        1024, // UV
        2048, // UHV
        4096, // UEV
        8192, // UIV
        16384, // UMV
        32768 // UXV
    };

    @Override
    public int getMaxParallelRecipes() {
        int tier = machinecasing.getCasingTier();
        if (tier < 0) return 1;
        if (tier >= MAX_PARALLELS_BY_TIER.length) {
            return MAX_PARALLELS_BY_TIER[MAX_PARALLELS_BY_TIER.length - 1];
        }
        return MAX_PARALLELS_BY_TIER[tier];
    }

    @Override
    public List<StructureVariant<GTN_ImprovedAlgaeFarm>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "ImprovedAlgaeFarm",
                new String[][] { { "CCCCCCC", "CAAAAAC", "CAAAAAC", "CAAAAAC", "CAAAAAC", "CAAAAAC", "CCCCCCC" },
                    { "CCCCCCC", "C     C", "C     C", "C     C", "C     C", "C     C", "CCCCCCC" },
                    { "CCC~CCC", "CBBBBBC", "CBBBBBC", "CBBBBBC", "CBBBBBC", "CBBBBBC", "CCCCCCC" } },
                new MultiblockOffsets(3, 2, 0),
                new MultiblockArea(7, 3, 7),
                1,
                GTN_Casings.SterileFarmCasing));
    }

    @Override
    public IStructureDefinition<GTN_ImprovedAlgaeFarm> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addElement('A', GTN_StructureUtility.createAllTieredGlass(glass))
                .addElement(
                    'B',
                    TieredElementBuilder.create(machinecasing, GTN_ImprovedAlgaeFarm.class)
                        .casings(
                            GTN_Casings.LVMachineCasing,
                            GTN_Casings.MVMachineCasing,
                            GTN_Casings.HVMachineCasing,
                            GTN_Casings.EVMachineCasing,
                            GTN_Casings.IVMachineCasing,
                            GTN_Casings.LuVMachineCasing,
                            GTN_Casings.UVMachineCasing,
                            GTN_Casings.UHVMachineCasing,
                            GTN_Casings.UEVMachineCasing,
                            GTN_Casings.UIVMachineCasing,
                            GTN_Casings.UMVMachineCasing,
                            GTN_Casings.UXVMachineCasing)
                        .build())
                .addElement(
                    'C',
                    ElementBuilder.create(GTN_ImprovedAlgaeFarm.class, this)
                        .casing(GTN_Casings.SterileFarmCasing)
                        .hatches(InputBus, InputHatch, OutputBus)
                        .build()));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTN_Recipe.ImprovedAlgaeFarmRecipes;
    }

    private boolean hasUVCasing() {
        return machinecasing.getCasingTier() >= 6;
    }

    private static final ItemStack FERTILIZER = GTModHandler.getModItem("IC2", "itemFertilizer", 1, 0);

    private boolean isFertilizer(ItemStack stack) {
        return stack != null && FERTILIZER != null && GTUtility.areStacksEqual(stack, FERTILIZER, false);
    }

    private boolean consumeFertilizer(int amount) {
        if (amount <= 0) return true;

        ArrayList<ItemStack> inputs = getStoredInputs();
        if (inputs == null) return false;

        int available = 0;

        for (ItemStack stack : inputs) {
            if (stack != null && isFertilizer(stack)) {
                available += stack.stackSize;
            }
        }

        if (available < amount) {
            return false;
        }

        int remaining = amount;

        for (ItemStack stack : inputs) {
            if (stack != null && isFertilizer(stack) && remaining > 0) {
                int take = Math.min(stack.stackSize, remaining);
                stack.stackSize -= take;
                remaining -= take;
            }
        }

        updateSlots();
        return true;
    }

    private ItemStack[] multiplyOutputs(ItemStack[] outputs, int multiplier) {
        if (outputs == null || multiplier <= 1) return outputs;

        ItemStack[] result = new ItemStack[outputs.length];
        for (int i = 0; i < outputs.length; i++) {
            if (outputs[i] != null) {
                result[i] = outputs[i].copy();
                result[i].stackSize *= multiplier;
            }
        }
        return result;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new GTN_ProcessingLogic() {

            @Override
            public CheckRecipeResult process() {
                CheckRecipeResult result = super.process();
                if (!result.wasSuccessful()) {
                    return result;
                }

                if (!hasUVCasing()) {
                    int waterNeeded = getMaxParallelRecipes() * WATER_PER_RECIPE;
                    if (!depleteInput(Materials.Water.getFluid(waterNeeded))) {
                        return SimpleCheckRecipeResult.ofFailure("not_enough_water");
                    }
                }

                int fertilizerNeeded = getMaxParallelRecipes();

                if (consumeFertilizer(fertilizerNeeded) && outputItems != null) {
                    outputItems = multiplyOutputs(outputItems, FERTILIZER_MULTIPLIER);
                }

                return result;
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_ALGAE_LOOP;
    }
}
