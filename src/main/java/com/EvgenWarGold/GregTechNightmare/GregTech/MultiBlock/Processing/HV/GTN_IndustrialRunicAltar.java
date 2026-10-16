package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.HV;

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

public class GTN_IndustrialRunicAltar extends GTN_BotanicMultiBlockBase<GTN_IndustrialRunicAltar> {

    public GTN_IndustrialRunicAltar(int id, String name) {
        super(id, name);
    }

    public GTN_IndustrialRunicAltar(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_IndustrialRunicAltar>> getStructureVariants() {
        return List.of(
            new StructureVariant<>(
                "IndustrialRunicAltar",
                // spotless:off
                new String[][] {
                    {"CCCCC", "CGGGC", "CGGGC", "CGGGC", "CCCCC"},
                    {"CCCCC", "C   C", "C   C", "C   C", "CCCCC"},
                    {"CCCCC", "C   C", "C R C", "C   C", "CCCCC"},
                    {"CC~CC", "CCCCC", "CCCCC", "CCCCC", "CCCCC"}
                },
                // spotless:on
                new MultiblockOffsets(2, 3, 0),
                new MultiblockArea(5, 4, 5),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_IndustrialRunicAltar createNewMetaEntity() {
        return new GTN_IndustrialRunicAltar(this.mName);
    }

    @Override
    protected int getBaseTier() {
        return VoltageIndex.HV;
    }

    @Override
    public IStructureDefinition<GTN_IndustrialRunicAltar> getStructureDefinition() {
        return buildStructureDefinition(builder -> {
            addBotanicCasing(builder, 'C');
            builder.addAllGlasses('G')
                .addBlock('R', BotaniaBlocks.RuneAltar.get(), 0);
        });
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTN_Recipe.RuneAltarRecipes;
    }
}
