package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Example;

import static com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HatchElement.ManaHatch;
import static gregtech.api.enums.HatchElement.Dynamo;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticDynamo;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.Muffler;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.CasingData;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_ProcessingBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.TieredElementBuilder;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;

public class GTN_TestMultiBlock extends GTN_MultiBlockBase<GTN_TestMultiBlock> {

    private GTN_ProcessingBuilder<?> processingBuilder = new GTN_ProcessingBuilder<>(processingHelper);

    public GTN_TestMultiBlock(int id, String name) {
        super(id, name);
    }

    public GTN_TestMultiBlock(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_TestMultiBlock>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "Gas",
                // spotless:off
                new String[][]{
                    {"ACA","AAA","AAA"},
                    {"A~A","A A","AAA"},
                    {"AAA","AAA","AAA"}
                },
                //spotless:on
                new MultiblockOffsets(1, 1, 0),
                new MultiblockArea(4, 5, 10),
                1,
                GTN_Casings.FrostProofMachineCasing),
            new StructureVariant<>(
                "Fuel",
                // spotless:off
                new String[][]{
                    {"BDB","BBB","BBB"},
                    {"B~B","B B","BBB"},
                    {"BBB","BBB","BBB"}
                },
                //spotless:on
                new MultiblockOffsets(1, 1, 0),
                new MultiblockArea(3, 3, 3),
                2,
                GTN_Casings.TitaniumGearBoxCasing));
    }

    @Override
    public GTN_TestMultiBlock createNewMetaEntity() {
        return new GTN_TestMultiBlock(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addExtraInfo("CRAZER")
            .addExtraInfoWithSpace("FAOTIK");
    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    private final CasingData casing = createCasingData("casing", true);
    private final CasingData casing1 = createCasingData("casing1", true);
    private final CasingData temp = createCasingData("temp");
    private final CasingData temp1 = createCasingData("temp1");

    @Override
    public IStructureDefinition<GTN_TestMultiBlock> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addElement(
                'B',
                TieredElementBuilder.create(casing1, GTN_TestMultiBlock.class)
                    .casings(GTN_Casings.TitaniumGearBoxCasing, GTN_Casings.SolidifierCasing)
                    .hatches(
                        InputHatch,
                        OutputHatch,
                        InputBus,
                        OutputBus,
                        Energy,
                        ExoticEnergy,
                        Maintenance,
                        Muffler,
                        Dynamo,
                        ManaHatch)
                    .build())
                .addElement(
                    'A',
                    TieredElementBuilder.create(casing, GTN_TestMultiBlock.class)
                        .casings(GTN_Casings.FrostProofMachineCasing, GTN_Casings.Firebricks)
                        .hatches(
                            InputHatch,
                            OutputHatch,
                            InputBus,
                            OutputBus,
                            Energy,
                            ExoticEnergy,
                            ExoticDynamo,
                            Maintenance,
                            Muffler,
                            Dynamo,
                            ManaHatch)
                        .build())
                .addTierBlock('C', temp, Blocks.coal_block, Blocks.tnt)
                .addTierBlock('D', temp1, Blocks.diamond_block, Blocks.end_stone)
                .build());
    }

    @Override
    protected boolean GTN_checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return super.GTN_checkMachine(aBaseMetaTileEntity, aStack);
    }

    @Override
    public OverclockType getOverclockType() {
        return multiBlockTier > 1 ? OverclockType.PerfectOverclock : OverclockType.NormalOverclock;
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        CheckRecipeResult result = processingBuilder.execute();

        if (processingBuilder.executeCheck()) {
            return result;
        }

        return result;
    }

    @Override
    protected void initialize() {
        super.initialize();
        ItemStack input = new ItemStack(Items.stick);
        Map<ItemStack, Integer> output = new HashMap<>();
        Map<FluidStack, Integer> outputFluid = new HashMap<>();

        output.put(new ItemStack(Items.coal), 64);
        output.put(new ItemStack(Items.glass_bottle), 64);
        output.put(new ItemStack(Items.apple), 64);

        outputFluid.put(Materials.Fluorine.getGas(1), 3_000);
        outputFluid.put(Materials.Praseodymium.getMolten(1), 3_000);

        processingBuilder.consumeItem(input, 4)
            .consumeFluid(Materials.Fluorine.getGas(1), 3_000)
            .outputFluid(outputFluid)
            .outputItem(output)
            .setEnergyGenerate(96)
            .setDurationSeconds(1);
    }
}
