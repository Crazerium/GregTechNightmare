package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.LV;

import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.ElementBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.EvgenWarGold.GregTechNightmare.Utils.VoidMinerUtils;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;

public class GTN_LowPowerVoidMiner extends GTN_MultiBlockBase<GTN_LowPowerVoidMiner> {

    private static VoidMinerUtils voidMiner = null;
    private static final List<Integer> ALLOW_DIMENSION = Arrays.asList(-1, 7, 0);
    private static boolean preGenerated = false;

    public GTN_LowPowerVoidMiner(int id, String name) {
        super(id, name);
    }

    public GTN_LowPowerVoidMiner(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_LowPowerVoidMiner>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "LowPowerVoidMiner",
                // spotless:off
                new String[][]{
                    {"   "," B ","   "},
                    {"   "," B ","   "},
                    {"   "," B ","   "},
                    {" B ","BCB"," B "},
                    {" B ","BCB"," B "},
                    {" B ","BCB"," B "},
                    {"A~A","AAA","AAA"}
                },
                //spotless:on
                new MultiblockOffsets(1, 6, 0),
                new MultiblockArea(3, 7, 3),
                1,
                GTN_Casings.SolidSteelMachineCasing));
    }

    @Override
    public GTN_LowPowerVoidMiner createNewMetaEntity() {
        return new GTN_LowPowerVoidMiner(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addOutputBus()
            .addEnergyHatch()
            .addMaintenanceHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_LowPowerVoidMiner> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder
                .addFrame('B', Materials.Cobalt)
                .addCasing('C', GTN_Casings.BoltedCobaltCasing)
                .addMainCasing('A', b -> b
                    .hatches(OutputBus, Energy, Maintenance)));
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);

        if (aBaseMetaTileEntity.isServerSide()) {
            boolean matchFound = ALLOW_DIMENSION.stream()
                .anyMatch(n -> n.equals(aBaseMetaTileEntity.getWorld().provider.dimensionId));

            if (!matchFound) {
                explodeMultiblock();
            }

            if (!preGenerated) {
                voidMiner = new VoidMinerUtils(ALLOW_DIMENSION);
                voidMiner.initDropMap(aBaseMetaTileEntity);
                preGenerated = true;
            }
        }
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        long availableEUt = getMaxInputPower();

        if (availableEUt < 128) {
            return CheckRecipeResultRegistry.insufficientPower(128);
        }

        ItemStack[] result = voidMiner.generateStackOre(8)
            .toArray(new ItemStack[0]);

        if (isItemOutputFull(result)) return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;

        mOutputItems = result;
        mEfficiency = getEfficiency();
        mMaxProgresstime = 20;
        setEnergyUsageWithoutLoss(128);

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GTCEU_LOOP_MINER;
    }
}
