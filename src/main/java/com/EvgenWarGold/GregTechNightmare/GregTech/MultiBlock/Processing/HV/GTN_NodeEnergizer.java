package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.HV;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofTileAdder;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.CasingData;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.ElementBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_ProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_StructureUtility;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.TileNode;

public class GTN_NodeEnergizer extends GTN_MultiBlockBase<GTN_NodeEnergizer> {

    protected ArrayList<TileNode> mNode = new ArrayList<>();
    private final CasingData glass = createCasingData("glass");

    public GTN_NodeEnergizer(int id, String name) {
        super(id, name);
    }

    public GTN_NodeEnergizer(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_NodeEnergizer>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "NodeEnergizer",
                // spotless:off
                new String[][]{
                    {"       ","       ","   C   ","       ","       ","       "},
                    {"  AAA  "," A C A "," ACCCA "," A C A ","  AAA  ","       "},
                    {"       ","  CBC  ","A B B A","  CBC  ","       ","   A   "},
                    {"       ","  C C  ","A BDB A","  CBC  ","       ","   A   "},
                    {"       ","  CBC  ","A B B A","  CBC  ","       ","   A   "},
                    {"  C~C  "," CCCCC ","ACCCCCA"," CCCCC ","  CCC  ","   A   "}
                },
                //spotless:on
                new MultiblockOffsets(3, 5, 0),
                new MultiblockArea(7, 6, 6),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_NodeEnergizer createNewMetaEntity() {
        return new GTN_NodeEnergizer(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addInputBus()
            .addEnergyHatch()
            .addMaintenanceHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_NodeEnergizer> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addElement(
                'C',
                ElementBuilder.create(GTN_NodeEnergizer.class, this)
                    .casing(mainCasing)
                    .hatches(InputBus, Energy, Maintenance)
                    .build())
                .addElement('B', GTN_StructureUtility.createAllTieredGlass(glass))
                .addElement(
                    'D',
                    ofChain(ofTileAdder(GTN_NodeEnergizer::addNodeEnergized, Blocks.air, 0), StructureUtility.isAir()))
                .addElement('A', ofFrame(Materials.Silver)));
    }

    @Override
    public OverclockType getOverclockType() {
        return OverclockType.NONE;
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

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.MOB_ENDERMEN_STARE;
    }
}
