package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Generators.EV.Magen;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.removeFluids;
import static gregtech.api.enums.HatchElement.InputHatch;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.CoordMultiBlock;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class GTN_MagicGenerator extends GTN_MultiBlockBase<GTN_MagicGenerator> {

    private static final List<Class<? extends GTN_MultiBlockBase<?>>> ALLOWED_LINK_MULTIBLOCK = Arrays.asList(
        GTN_AquaModuleMagicGenerator.class
    );

    public GTN_MagicGenerator(int id, String name) {
        super(id, name);
    }

    public GTN_MagicGenerator(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_MagicGenerator>> getStructureVariants() {
        return List.of(
            new StructureVariant<>(
                "",
                // spotless:off
                new String[][]{
                    {"A"},
                    {"B"},
                    {"B"},
                    {"~"}
                },
                //spotless:on
                new MultiblockOffsets(0, 3, 0),
                new MultiblockArea(1, 4, 1),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_MagicGenerator createNewMetaEntity() {
        return new GTN_MagicGenerator(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {

    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_MagicGenerator> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addMainCasing('B', b -> b.hatches(InputHatch))
                .addCasing('A', GTN_Casings.VacuumCasing));
    }

    @Override
    public boolean tryLink(CoordMultiBlock coord) {
        if (getCoord().equals(coord)) return false;

        IGregTechTileEntity gte = multiBlocks.get(coord);

        if (gte == null) {
            IGregTechTileEntity newGte = coord.getMTEMultiBlockBase();
            if (newGte != null) {
                IMetaTileEntity mte = newGte.getMetaTileEntity();
                if (mte instanceof GTN_MultiBlockBase && isClassAllowed(mte.getClass())) {
                    removeExistingLinkOfSameType(mte.getClass(), coord);

                    multiBlocks.put(coord, newGte);
                    return true;
                }
            }
            return false;
        }

        IMetaTileEntity mte = gte.getMetaTileEntity();
        if (mte != null) return false;

        multiBlocks.remove(coord);

        IGregTechTileEntity newGte = coord.getMTEMultiBlockBase();
        if (newGte != null) {
            IMetaTileEntity newMte = newGte.getMetaTileEntity();
            if (newMte instanceof GTN_MultiBlockBase && isClassAllowed(newMte.getClass())) {
                removeExistingLinkOfSameType(newMte.getClass(), coord);

                multiBlocks.put(coord, newGte);
                return true;
            }
        }

        return false;
    }

    private void removeExistingLinkOfSameType(Class<?> mteClass, CoordMultiBlock exceptCoord) {
        multiBlocks.entrySet().removeIf(entry -> {
            if (entry.getKey().equals(exceptCoord)) return false;
            if (entry.getValue() == null) return false;

            IMetaTileEntity existingMte = entry.getValue().getMetaTileEntity();
            return mteClass.isInstance(existingMte);
        });
    }

    private boolean isClassAllowed(Class<?> clazz) {
        return ALLOWED_LINK_MULTIBLOCK.stream()
            .anyMatch(allowedClass -> allowedClass.isAssignableFrom(clazz));
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return true;
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        if (multiBlocks.isEmpty()) return CheckRecipeResultRegistry.NO_RECIPE;

        int generate = 0;

        for (CoordMultiBlock coord : multiBlocks.keySet()) {
            IGregTechTileEntity gte = multiBlocks.get(coord);
            IMetaTileEntity mte = gte.getMetaTileEntity();

            if (mte instanceof IMagicGeneratorModule module) {
                generate += module.generate();
            }
        }

        if (generate == 0) return CheckRecipeResultRegistry.NO_RECIPE;



//        for (FluidStack fluid : fluids) {
//            if (fluid.isFluidEqual(CREOSOTE)) {
//                switch (DYNAMO_TIER) {
//                    case 1 -> {
//                        CREOSOTE.amount = Math.toIntExact(CREOSOTE_USAGE_PER_SEC * DYNAMO_AMP);
//                        setEnergyGenerate(32 * DYNAMO_AMP);
//                    }
//                    case 2 -> {
//                        CREOSOTE.amount = Math.toIntExact(CREOSOTE_USAGE_PER_SEC * DYNAMO_AMP) * 4;
//                        setEnergyGenerate(128 * DYNAMO_AMP);
//                    }
//                }
//
//                if (getAllMaxDynamoBuffer() != getAllDynamoBuffer()) {
//                    if (removeFluids(fluids, fluidUsage, true)) {
//                        removeFluids(fluids, fluidUsage);
//                        mEfficiency = getEfficiency();
//                        setDurationInSeconds(1);
//                        return CheckRecipeResultRegistry.GENERATING;
//                    }
//                }
//            }
//        }

        setEnergyGenerate(generate);
        super.mEfficiency = getEfficiency();
        setDurationInSeconds(5);
        return CheckRecipeResultRegistry.GENERATING;
    }
}
