package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.LV;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.EvgenWarGold.GregTechNightmare.Utils.VoidMinerUtils;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;

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
    public int getOffsetHorizontal() {
        return 1;
    }

    @Override
    public int getOffsetVertical() {
        return 6;
    }

    @Override
    public int getOffsetDepth() {
        return 0;
    }

    @Override
    public GTN_Casings getMainCasings() {
        return GTN_Casings.SolidSteelMachineCasing;
    }

    @Override
    public GTN_LowPowerVoidMiner createNewMetaEntity() {
        return new GTN_LowPowerVoidMiner(this.mName);
    }

    @Override
    public OverclockType getOverclockType() {
        return OverclockType.NONE;
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return false;
    }

    @Override
    public String[][] getShape() {
        return new String[][] { { "   ", " C ", "   " }, { "   ", " C ", "   " }, { "   ", " C ", "   " },
            { " C ", "CAC", " C " }, { " C ", "CAC", " C " }, { " C ", "CAC", " C " }, { "B~B", "BBB", "BBB" } };
    }

    @Override
    public void createGtnTooltip(MultiblockTooltipBuilder builder) {
        builder.addInfo(tr("tooltip.00"))
            .addInfo(tr("tooltip.01"))
            .addInfo(tr("tooltip.02"))
            .addInfo(Constants.AUTHOR_EVGEN_WAR_GOLD)
            .beginStructureBlock(3, 7, 3, false)
            .addEnergyHatch(EnumChatFormatting.GOLD + "1", 1)
            .addOutputBus(EnumChatFormatting.GOLD + "1", 1);
    }

    @Override
    public IStructureDefinition<GTN_LowPowerVoidMiner> getStructureDefinition() {
        return IStructureDefinition.<GTN_LowPowerVoidMiner>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement('C', ofFrame(Materials.Cobalt))
            .addElement('A', GTN_Casings.BoltedCobaltCasing.asElement())
            .addElement(
                'B',
                buildHatchAdder(GTN_LowPowerVoidMiner.class).atLeast(OutputBus, Energy, Maintenance)
                    .casingIndex(getMainCasings().textureId)
                    .dot(1)
                    .buildAndChain(
                        onElementPass(
                            GTN_LowPowerVoidMiner::mainCasingAdd,
                            ofBlock(getMainCasings().getBlock(), getMainCasings().meta))))
            .build();
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
        lEUt = getEnergyUsageWithoutLoss(128);

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }
}
