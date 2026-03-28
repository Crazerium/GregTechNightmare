package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.LV;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.removeFluids;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.Mods.Forestry;
import static gtPlusPlus.xmod.gregtech.common.tileentities.machines.multi.production.MTETreeFarm.treeProductsMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.CasingData;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_ProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;
import gtPlusPlus.xmod.gregtech.common.tileentities.machines.multi.production.MTETreeFarm;

public class GTN_TreeSprouter extends GTN_MultiBlockBase<GTN_TreeSprouter> {

    private static final MTETreeFarm.Mode LOG = MTETreeFarm.Mode.LOG;
    private final CasingData glass = createCasingData("glass");

    public GTN_TreeSprouter(int id, String name) {
        super(id, name);
    }

    public GTN_TreeSprouter(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_TreeSprouter>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "TreeSprouter",
                // spotless:off
                new String[][]{
                    {"     ","  A  "," ABA ","  A  ","     "},
                    {" AAA ","ADDDA","ADDDA","ADDDA"," AAA "},
                    {"ADDDA","D   D","D E D","D   D","ADDDA"},
                    {"ADDDA","D E D","DEFED","D E D","ADDDA"},
                    {"BDDDB","D   D","D F D","D   D","BDDDB"},
                    {"BB~BB","BCCCB","BCCCB","BCCCB","BBBBB"}
                },
                //spotless:on
                new MultiblockOffsets(2, 5, 0),
                new MultiblockArea(5, 6, 5),
                1,
                GTN_Casings.ArborealCasing));
    }

    @Override
    public GTN_TreeSprouter createNewMetaEntity() {
        return new GTN_TreeSprouter(this.mName);
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
    public IStructureDefinition<GTN_TreeSprouter> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addFrame('A', Materials.Wood)
                .addAllGlasses('D', glass)
                .addElement('E', ofBlock(Blocks.leaves, 0))
                .addElement('C', ofChain(ofBlock(Blocks.dirt, 0), ofBlock(Blocks.grass, 0)))
                .addElement('F', ofBlock(Blocks.log, 0))
                .addMainCasing('B', b -> b.hatches(OutputBus, Energy, Maintenance)));
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

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GTCEU_OP_SAW;
    }
}
