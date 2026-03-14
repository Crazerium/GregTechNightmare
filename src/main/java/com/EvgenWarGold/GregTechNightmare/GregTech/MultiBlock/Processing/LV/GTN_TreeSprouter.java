package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.LV;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.removeFluids;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.Mods.Forestry;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gtPlusPlus.xmod.gregtech.common.tileentities.machines.multi.production.MTETreeFarm.treeProductsMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_ProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;
import gtPlusPlus.xmod.gregtech.common.tileentities.machines.multi.production.MTETreeFarm;

public class GTN_TreeSprouter extends GTN_MultiBlockBase<GTN_TreeSprouter> {

    private static final MTETreeFarm.Mode LOG = MTETreeFarm.Mode.LOG;

    public GTN_TreeSprouter(int id, String name) {
        super(id, name);
    }

    public GTN_TreeSprouter(String name) {
        super(name);
    }

    @Override
    public int getOffsetHorizontal() {
        return 2;
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
        return GTN_Casings.AborealCasing;
    }

    @Override
    public GTN_TreeSprouter createNewMetaEntity() {
        return new GTN_TreeSprouter(this.mName);
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
        // spotless:off
        return new String[][]{
            {"     ","  A  "," ABA ","  A  ","     "},
            {" AAA ","ADDDA","ADDDA","ADDDA"," AAA "},
            {"ADDDA","D   D","D E D","D   D","ADDDA"},
            {"ADDDA","D E D","DEFED","D E D","ADDDA"},
            {"BDDDB","D   D","D F D","D   D","BDDDB"},
            {"BB~BB","BCCCB","BCCCB","BCCCB","BBBBB"}
        };
        //spotless:on
    }

    @Override
    public void createGtnTooltip(MultiblockTooltipBuilder builder) {
        builder.addInfo(tr("tooltip.00"))
            .addInfo(tr("tooltip.01"))
            .addInfo(tr("tooltip.02"))
            .addInfo(tr("tooltip.03"))
            .addInfo(Constants.AUTHOR_EVGEN_WAR_GOLD)
            .beginStructureBlock(5, 6, 5, true)
            .addInputHatch(EnumChatFormatting.GOLD + "1", 1)
            .addMaintenanceHatch(EnumChatFormatting.GOLD + "1", 1)
            .addOutputBus(EnumChatFormatting.GOLD + "1", 1);
    }

    @Override
    public IStructureDefinition<GTN_TreeSprouter> getStructureDefinition() {
        return IStructureDefinition.<GTN_TreeSprouter>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement('A', ofFrame(Materials.Wood))
            .addElement('D', ofBlock(Blocks.glass, 0))
            .addElement('E', ofBlock(Blocks.leaves, 0))
            .addElement('C', ofChain(ofBlock(Blocks.dirt, 0), ofBlock(Blocks.grass, 0)))
            .addElement('F', ofBlock(Blocks.log, 0))
            .addElement(
                'B',
                buildHatchAdder(GTN_TreeSprouter.class).atLeast(OutputBus, Maintenance, InputHatch)
                    .casingIndex(getMainCasings().textureId)
                    .dot(1)
                    .buildAndChain(
                        onElementPass(
                            GTN_TreeSprouter::mainCasingAdd,
                            ofBlock(getMainCasings().getBlock(), getMainCasings().meta))))
            .build();
    }

    @Override
    public boolean isEnergyMultiBlock() {
        return false;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new GTN_ProcessingLogic() {

            @Override
            public @NotNull CheckRecipeResult process() {
                ItemStack controllerSlot = getControllerSlot();
                ArrayList<FluidStack> inputs = getStoredFluids();

                if (controllerSlot == null) return CheckRecipeResultRegistry.NO_RECIPE;

                if (!isValidSapling(controllerSlot)) {
                    return SimpleCheckRecipeResult.ofFailure("sapling_enough");
                }

                ItemStack output = Objects.requireNonNull(getOutputsForSapling(controllerSlot))
                    .copy();

                output.stackSize = 64;

                if (isItemOutputFull(new ItemStack[] { output })) return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;

                outputItems = new ItemStack[] { output };

                if (removeFluids(inputs, Collections.singletonList(Materials.Steam.getGas(16_000)), true)) {
                    removeFluids(inputs, Collections.singletonList(Materials.Steam.getGas(16_000)));
                    setDurationInMinutes(2 + (100 - Math.min((getEfficiency() / 100), 100)) / 2);
                } else {
                    setDurationInMinutes(5 + (100 - Math.min((getEfficiency() / 100), 100)) / 2);
                }

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    private boolean isValidSapling(ItemStack stack) {
        if (stack == null) return false;
        String registryName = Item.itemRegistry.getNameForObject(stack.getItem());
        return treeProductsMap.containsKey(registryName + ":" + stack.getItemDamage())
            || "Forestry:sapling".equals(registryName);
    }

    private static ItemStack getOutputsForSapling(ItemStack sapling) {
        String registryName = Item.itemRegistry.getNameForObject(sapling.getItem());
        if (Forestry.isModLoaded() && "Forestry:sapling".equals(registryName)) {
            ITree tree = TreeManager.treeRoot.getMember(sapling);
            if (tree == null) return null;

            String speciesUUID = tree.getIdent();

            return treeProductsMap.get("Forestry:sapling:" + speciesUUID)
                .get(LOG);
        } else {
            return treeProductsMap.get(registryName + ":" + sapling.getItemDamage())
                .get(LOG);
        }
    }

    @Override
    public IIconContainer getMainOverlayActive() {
        return TexturesGtBlock.oMCATreeFarmActive;
    }

    @Override
    public IIconContainer getMainOverlay() {
        return TexturesGtBlock.oMCATreeFarm;
    }

    @Override
    public IIconContainer getMainOverlayActiveGlow() {
        return Textures.BlockIcons.VOID;
    }

    @Override
    public IIconContainer getMainOverlayGlow() {
        return Textures.BlockIcons.VOID;
    }
}
