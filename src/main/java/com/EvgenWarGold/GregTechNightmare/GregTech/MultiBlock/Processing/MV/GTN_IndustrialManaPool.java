package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.MV;

import java.util.List;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_BotanicMultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.BotaniaBlocks;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.enums.VoltageIndex;
import gregtech.api.recipe.RecipeMap;

public class GTN_IndustrialManaPool extends GTN_BotanicMultiBlockBase<GTN_IndustrialManaPool> {

    public GTN_IndustrialManaPool(int id, String name) {
        super(id, name);
    }

    public GTN_IndustrialManaPool(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_IndustrialManaPool>> getStructureVariants() {
        return List.of(
            new StructureVariant<>(
                "IndustrialManaPool",
                // spotless:off
                new String[][] {
                    {"CCCCC", "CGGGC", "CGGGC", "CGGGC", "CCCCC"},
                    {"CCCCC", "C   C", "C P C", "C   C", "CCCCC"},
                    {"CC~CC", "CCCCC", "CCCCC", "CCCCC", "CCCCC"}
                },
                // spotless:on
                new MultiblockOffsets(2, 2, 0),
                new MultiblockArea(5, 3, 5),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_IndustrialManaPool createNewMetaEntity() {
        return new GTN_IndustrialManaPool(this.mName);
    }

    @Override
    protected int getBaseTier() {
        return VoltageIndex.MV;
    }

    @Override
    public IStructureDefinition<GTN_IndustrialManaPool> getStructureDefinition() {
        return buildStructureDefinition(builder -> {
            addBotanicCasing(builder, 'C');
            builder.addAllGlasses('G')
                .addBlock('P', BotaniaBlocks.ManaPool.get(), 0);
        });
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTN_Recipe.ManaInfusionRecipes;
    }
}
