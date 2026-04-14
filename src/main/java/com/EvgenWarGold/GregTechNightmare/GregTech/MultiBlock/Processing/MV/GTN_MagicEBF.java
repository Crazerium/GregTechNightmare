package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.MV;

import static com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_HatchElement.ManaHatch;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_ManaHatch;
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
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;

public class GTN_MagicEBF extends GTN_MultiBlockBase<GTN_MagicEBF> {

    private final CasingData coil = createCasingData("coil");
    private int heatingCapacity = 0;
    private final static int MANA_CONSUME = 210;

    public GTN_MagicEBF(int id, String name) {
        super(id, name);
    }

    public GTN_MagicEBF(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_MagicEBF>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "MagicEBF",
                // spotless:off
                new String[][]{
                    {"BBB","BBB","BBB"},
                    {"AAA","A A","AAA"},
                    {"AAA","A A","AAA"},
                    {"B~B","BBB","BBB"}
                },
                //spotless:on
                new MultiblockOffsets(1, 3, 0),
                new MultiblockArea(3, 4, 3),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_MagicEBF createNewMetaEntity() {
        return new GTN_MagicEBF(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {

    }

    @Override
    public Authors getAuthor() {
        return Authors.EVGEN_WAR_GOLD;
    }

    @Override
    public IStructureDefinition<GTN_MagicEBF> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addAllCoil('A', coil)
                .addMainCasing(
                    'B',
                    b -> b.hatches(ManaHatch, Maintenance, Energy, InputBus, OutputBus, InputHatch, OutputHatch)));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.blastFurnaceRecipes;
    }

    @Override
    public float getSpeedBonus() {
        return 1.50F;
    }

    @Override
    public float getEuModifier() {
        return 0.85F;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_EBF_LOOP;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new GTN_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {

                setEuModifier(getEuModifier());
                setSpeedBonus(1F / getSpeedBonus());
                setOverclockType(getOverclockType());
                return super.process();
            }

            @Nonnull
            @Override
            protected OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setRecipeHeat(recipe.mSpecialValue)
                    .setMachineHeat(heatingCapacity)
                    .setHeatOC(true)
                    .setHeatDiscount(true);
            }

            @Override
            protected @Nonnull CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                return recipe.mSpecialValue <= heatingCapacity ? CheckRecipeResultRegistry.SUCCESSFUL
                    : CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
            }
        };
    }

    @Override
    protected boolean GTN_checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        HeatingCoilLevel coilLevel = coil.getCoilLevel();
        if (coilLevel != null) {
            heatingCapacity = (int) coilLevel.getHeat() + 100 * (GTUtility.getTier(getMaxInputVoltage()) - 2);
        }
        return super.GTN_checkMachine(aBaseMetaTileEntity, aStack) && mManaHatch.size() <= 1;
    }

    @Override
    protected void runMachine(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.runMachine(aBaseMetaTileEntity, aTick);

        if (mMaxProgresstime > 0 && aTick % 20 == 0) {
            if (mManaHatch.isEmpty()) {
                stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                causeMaintenanceIssue();
            }

            GTN_ManaHatch manaHatch = mManaHatch.get(0);

            if (manaHatch.extractMana(MANA_CONSUME, true)) {
                manaHatch.extractMana(MANA_CONSUME);
            } else {
                manaHatch.extractMana(MANA_CONSUME);
                stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                causeMaintenanceIssue();
            }
        }
    }
}
