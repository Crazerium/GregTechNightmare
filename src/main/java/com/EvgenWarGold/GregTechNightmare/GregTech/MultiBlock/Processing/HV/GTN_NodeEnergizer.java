package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.HV;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofTileAdder;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_ProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.MultiblockTooltipBuilder;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.TileNode;

public class GTN_NodeEnergizer extends GTN_MultiBlockBase<GTN_NodeEnergizer> {

    protected ArrayList<TileNode> mNode = new ArrayList<>();

    public GTN_NodeEnergizer(int id, String name) {
        super(id, name);
    }

    public GTN_NodeEnergizer(String name) {
        super(name);
    }

    @Override
    public int getOffsetHorizontal() {
        return 3;
    }

    @Override
    public int getOffsetVertical() {
        return 5;
    }

    @Override
    public int getOffsetDepth() {
        return 0;
    }

    @Override
    public GTN_Casings getMainCasings() {
        return GTN_Casings.MagicCasing;
    }

    @Override
    public GTN_NodeEnergizer createNewMetaEntity() {
        return new GTN_NodeEnergizer(this.mName);
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
        return new String[][] { { "       ", "       ", "   C   ", "       ", "       ", "       " },
            { "  BBB  ", " B C B ", " BCCCB ", " B C B ", "  BBB  ", "       " },
            { "       ", "  CAC  ", "B A A B", "  CAC  ", "       ", "   B   " },
            { "       ", "  C C  ", "B ADA B", "  CAC  ", "       ", "   B   " },
            { "       ", "  CAC  ", "B A A B", "  CAC  ", "       ", "   B   " },
            { "  C~C  ", " CCCCC ", "BCCCCCB", " CCCCC ", "  CCC  ", "   B   " } };
    }

    @Override
    public void createGtnTooltip(MultiblockTooltipBuilder builder) {
        builder.addInfo(tr("tooltip.00"))
            .addInfo(tr("tooltip.01"))
            .addInfo(tr("tooltip.02"))
            .addInfo(tr("tooltip.03"))
            .addInfo(tr("tooltip.04"))
            .addInfo(tr("tooltip.05"))
            .addInfo(GTN_Utils.tr("Author_Structure", "Magma_Block"))
            .addInfo(Constants.AUTHOR_EVGEN_WAR_GOLD)
            .beginStructureBlock(7, 6, 6, true)
            .addInputBus(EnumChatFormatting.GOLD + "1", 1)
            .addEnergyHatch(EnumChatFormatting.GOLD + "1", 1)
            .addMaintenanceHatch(EnumChatFormatting.GOLD + "1", 1);
    }

    @Override
    public IStructureDefinition<GTN_NodeEnergizer> getStructureDefinition() {
        return IStructureDefinition.<GTN_NodeEnergizer>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement('A', ofBlockAnyMeta(GregTechAPI.sBlockTintedGlass, 0))
            .addElement(
                'D',
                ofChain(ofTileAdder(GTN_NodeEnergizer::addNodeEnergized, Blocks.air, 0), StructureUtility.isAir()))
            .addElement('B', ofFrame(Materials.Silver))
            .addElement(
                'C',
                buildHatchAdder(GTN_NodeEnergizer.class).atLeast(InputBus, Energy, Maintenance)
                    .casingIndex(getMainCasings().textureId)
                    .dot(1)
                    .buildAndChain(
                        onElementPass(
                            GTN_NodeEnergizer::mainCasingAdd,
                            ofBlock(getMainCasings().getBlock(), getMainCasings().meta))))
            .build();
    }

    public final boolean addNodeEnergized(TileEntity aTileEntity) {
        if (aTileEntity instanceof TileNode nodeEnergized) {
            if (!(mNode.size() == 1)) {
                return this.mNode.add(nodeEnergized);
            } else return true;
        }
        return false;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mNode.clear();
    }

    protected ProcessingLogic createProcessingLogic() {
        return new GTN_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                List<ItemStack> inputItems = getStoredInputs();
                long availableEUt = getMaxInputPower();
                int consumeEUt;
                MTEHatchInputBus bus = mInputBusses.get(0);
                TileNode node = mNode.size() == 1 ? mNode.get(0) : null;
                boolean bonus = false;

                if (node == null) return SimpleCheckRecipeResult.ofFailure("node_enough");

                if (bus == null || inputItems.isEmpty()) return CheckRecipeResultRegistry.NO_RECIPE;

                switch ((int) bus.getInputTier()) {
                    case 0 -> {
                        if (availableEUt < 2_048) {
                            return CheckRecipeResultRegistry.insufficientPower(2_048);
                        }
                        consumeEUt = 2_048;
                    }
                    case 1 -> {
                        if (availableEUt < 8_192) {
                            return CheckRecipeResultRegistry.insufficientPower(8_192);
                        }
                        consumeEUt = 8_192;
                    }
                    case 2 -> {
                        if (availableEUt < 32_768) {
                            return CheckRecipeResultRegistry.insufficientPower(32_768);
                        }
                        consumeEUt = 32_768;
                    }
                    case 3 -> {
                        if (availableEUt < 131_072) {
                            return CheckRecipeResultRegistry.insufficientPower(131_072);
                        }
                        bonus = true;
                        consumeEUt = 131_072;
                    }
                    default -> {
                        return CheckRecipeResultRegistry.NO_RECIPE;
                    }
                }

                AspectList oldList = node.getAspects();
                AspectList newList;

                for (ItemStack item : inputItems) {
                    int modifier = bonus ? item.stackSize : 1;

                    for (int i = 0; i < modifier; i++) {
                        newList = ThaumcraftCraftingManager.getObjectTags(item);
                        newList = ThaumcraftCraftingManager.getBonusTags(item, newList);
                        oldList.add(newList);
                    }
                }

                setDurationInSeconds(1);
                setEnergyUsage(consumeEUt);

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    protected boolean GTN_checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return mInputBusses.size() <= 1 && mNode.size() <= 1 && mEnergyHatches.size() <= 2;
    }
}
