package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.EV;

import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.EvgenWarGold.GregTechNightmare.ModItems.EnderIOItems;
import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;

public class GTN_ImprovedSliceNSplice extends GTN_MultiBlockBase<GTN_ImprovedSliceNSplice> {

    private static final Map<ItemStack, Integer> CAPACITOR_PARALLELS = new HashMap<>();

    public GTN_ImprovedSliceNSplice(int id, String name) {
        super(id, name);
    }

    public GTN_ImprovedSliceNSplice(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_ImprovedSliceNSplice>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "",
                // spotless:off
                new String[][]{
                    {"CCCCCCC","CAAAAAC","CAAAAAC","CAAAAAC","CCCCCCC"},
                    {"CBCBCBC","B     B","B     B","B     B","CCCCCCC"},
                    {"CCC~CCC","B     B","C     C","B     B","CCCCCCC"},
                    {"CBCBCBC","B     B","B     B","B     B","CCCCCCC"},
                    {"CCCCCCC","CCCCCCC","CCCCCCC","CCCCCCC","CCCCCCC"}
                },
                //spotless:on
                new MultiblockOffsets(3, 2, 0),
                new MultiblockArea(7, 5, 5),
                1,
                GTN_Casings.SoulCasing));
    }

    @Override
    public GTN_ImprovedSliceNSplice createNewMetaEntity() {
        return new GTN_ImprovedSliceNSplice(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addInputBus()
            .addOutputBus()
            .addEnergyHatch()
            .addMaintenanceHatch()
            .addInputHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.CRAZER;
    }

    @Override
    public IStructureDefinition<GTN_ImprovedSliceNSplice> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addAllGlasses('A')
                .addCasing('B', GTN_Casings.SteelGearBoxCasing)
                .addMainCasing('C', b -> b.hatches(InputBus, Energy, OutputBus, Maintenance, InputHatch)));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTN_Recipe.ImprovedSliceNSpliceRecipes;
    }

    private int getParallelFromCapacitor() {
        ItemStack stack = getControllerSlot();

        if (stack == null) return 2;

        for (Map.Entry<ItemStack, Integer> entry : CAPACITOR_PARALLELS.entrySet()) {
            if (GTUtility.areStacksEqual(stack, entry.getKey(), true)) {
                return entry.getValue();
            }
        }

        return 2;
    }

    @Override
    public int getMaxParallelRecipes() {
        return getParallelFromCapacitor();
    }

    @Override
    protected void initialize() {
        super.initialize();
        CAPACITOR_PARALLELS.put(EnderIOItems.BasicCapacitor.get(), 4);
        CAPACITOR_PARALLELS.put(EnderIOItems.DoubleLayerCapacitor.get(), 8);
        CAPACITOR_PARALLELS.put(EnderIOItems.OctadicCapacitor.get(), 16);
        CAPACITOR_PARALLELS.put(EnderIOItems.CrystallineCapacitor.get(), 32);
        CAPACITOR_PARALLELS.put(EnderIOItems.MelodicCapacitor.get(), 64);
        CAPACITOR_PARALLELS.put(EnderIOItems.StellarCapacitor.get(), 128);
        CAPACITOR_PARALLELS.put(EnderIOItems.TotemicCapacitor.get(), 512);
    }
}
