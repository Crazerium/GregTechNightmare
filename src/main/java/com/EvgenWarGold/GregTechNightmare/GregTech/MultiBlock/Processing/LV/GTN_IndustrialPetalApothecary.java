package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.LV;

import java.util.List;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_BotanicMultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.BotaniaBlocks;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.enums.VoltageIndex;
import gregtech.api.recipe.RecipeMap;

public class GTN_IndustrialPetalApothecary extends GTN_BotanicMultiBlockBase<GTN_IndustrialPetalApothecary> {

    public GTN_IndustrialPetalApothecary(int id, String name) {
        super(id, name);
    }

    public GTN_IndustrialPetalApothecary(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_IndustrialPetalApothecary>> getStructureVariants() {
        return List.of(
            new StructureVariant<>(
                "IndustrialPetalApothecary",
                // spotless:off
                new String[][] {
                    {"CCCCC", "CGGGC", "CGGGC", "CGGGC", "CCCCC"},
                    {"CCCCC", "C   C", "C A C", "C   C", "CCCCC"},
                    {"CC~CC", "CCCCC", "CCCCC", "CCCCC", "CCCCC"}
                },
                // spotless:on
                new MultiblockOffsets(2, 2, 0),
                new MultiblockArea(5, 3, 5),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_IndustrialPetalApothecary createNewMetaEntity() {
        return new GTN_IndustrialPetalApothecary(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        super.createGtnTooltip(builder);
        builder.addInputHatch();
    }

    @Override
    protected int getBaseTier() {
        return VoltageIndex.LV;
    }

    @Override
    public IStructureDefinition<GTN_IndustrialPetalApothecary> getStructureDefinition() {
        return buildStructureDefinition(builder -> {
            addBotanicCasing(builder, 'C');
            builder.addAllGlasses('G')
                .addBlock('A', BotaniaBlocks.PetalApothecary.get(), 0);
        });
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTN_Recipe.PetalApothecaryRecipes;
    }
}
