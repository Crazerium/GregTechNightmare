package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.EV;

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

public class GTN_IndustrialTerraPlate extends GTN_BotanicMultiBlockBase<GTN_IndustrialTerraPlate> {

    public GTN_IndustrialTerraPlate(int id, String name) {
        super(id, name);
    }

    public GTN_IndustrialTerraPlate(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_IndustrialTerraPlate>> getStructureVariants() {
        return List.of(
            new StructureVariant<>(
                "IndustrialTerraPlate",
                // spotless:off
                new String[][] {
                    {"CCCCC", "CGGGC", "CGGGC", "CGGGC", "CCCCC"},
                    {"CCCCC", "C   C", "C   C", "C   C", "CCCCC"},
                    {"CCCCC", "C   C", "C T C", "C   C", "CCCCC"},
                    {"CC~CC", "CLLLC", "CLLLC", "CLLLC", "CCCCC"}
                },
                // spotless:on
                new MultiblockOffsets(2, 3, 0),
                new MultiblockArea(5, 4, 5),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_IndustrialTerraPlate createNewMetaEntity() {
        return new GTN_IndustrialTerraPlate(this.mName);
    }

    @Override
    protected int getBaseTier() {
        return VoltageIndex.EV;
    }

    @Override
    public IStructureDefinition<GTN_IndustrialTerraPlate> getStructureDefinition() {
        return buildStructureDefinition(builder -> {
            addBotanicCasing(builder, 'C');
            builder.addAllGlasses('G')
                .addBlock('T', BotaniaBlocks.TerraPlate.get(), 0)
                .addBlock('L', BotaniaBlocks.LivingRock.get(), 0);
        });
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTN_Recipe.TerraPlateRecipes;
    }
}
