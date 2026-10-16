package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.LV;

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

public class GTN_IndustrialPureDaisy extends GTN_BotanicMultiBlockBase<GTN_IndustrialPureDaisy> {

    public GTN_IndustrialPureDaisy(int id, String name) {
        super(id, name);
    }

    public GTN_IndustrialPureDaisy(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_IndustrialPureDaisy>> getStructureVariants() {
        return List.of(
            new StructureVariant<>(
                "IndustrialPureDaisy",
                // spotless:off
                new String[][] {
                    {"CCCCC", "CGGGC", "CGGGC", "CGGGC", "CCCCC"},
                    {"CCCCC", "C   C", "C W C", "C   C", "CCCCC"},
                    {"CC~CC", "CLLLC", "CLLLC", "CLLLC", "CCCCC"}
                },
                // spotless:on
                new MultiblockOffsets(2, 2, 0),
                new MultiblockArea(5, 3, 5),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_IndustrialPureDaisy createNewMetaEntity() {
        return new GTN_IndustrialPureDaisy(this.mName);
    }

    @Override
    protected int getBaseTier() {
        return VoltageIndex.LV;
    }

    @Override
    public IStructureDefinition<GTN_IndustrialPureDaisy> getStructureDefinition() {
        return buildStructureDefinition(builder -> {
            addBotanicCasing(builder, 'C');
            builder.addAllGlasses('G')
                .addBlock('W', BotaniaBlocks.LivingWood.get(), 0)
                .addBlock('L', BotaniaBlocks.LivingRock.get(), 0);
        });
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTN_Recipe.PureDaisyRecipes;
    }
}
