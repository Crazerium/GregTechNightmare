package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.EV;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import com.EvgenWarGold.GregTechNightmare.Api.Dimensions;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.Mana.GTN_BotanicProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.Mana.GTN_ManaSources;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_BotanicMultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.BotaniaBlocks;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.shutdown.SimpleShutDownReason;
import gregtech.nei.RecipeDisplayInfo;

/**
 * Automated Alfheim portal. Like the Botania portal, opening it costs mana once and keeping it open drains mana every
 * second, also while it waits for items. The portal closes when the mana runs out or the machine is disabled.
 */
public class GTN_ElvenGateway extends GTN_BotanicMultiBlockBase<GTN_ElvenGateway> {

    /** 75 000 mana from each of the two pylons of the Botania portal. */
    public static final int OPEN_COST = 150_000;
    /** 2 mana per tick from each of the two pylons. */
    public static final int MANA_PER_SECOND = 80;
    private boolean portalOpen;

    public GTN_ElvenGateway(int id, String name) {
        super(id, name);
    }

    public GTN_ElvenGateway(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_ElvenGateway>> getStructureVariants() {
        return List.of(
            new StructureVariant<>(
                "ElvenGateway",
                // spotless:off
                new String[][] {
                    {"CCCCC", "CGGGC", "CGGGC", "CGGGC", "CCCCC"},
                    {"CCCCC", "C   C", "Y   Y", "C   C", "CCCCC"},
                    {"CCCCC", "C   C", "C P C", "C   C", "CCCCC"},
                    {"CC~CC", "CWWWC", "CWWWC", "CWWWC", "CCCCC"}
                },
                // spotless:on
                new MultiblockOffsets(2, 3, 0),
                new MultiblockArea(5, 4, 5),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_ElvenGateway createNewMetaEntity() {
        return new GTN_ElvenGateway(this.mName);
    }

    @Override
    protected int getBaseTier() {
        return VoltageIndex.EV;
    }

    @Override
    public IStructureDefinition<GTN_ElvenGateway> getStructureDefinition() {
        return buildStructureDefinition(builder -> {
            addBotanicCasing(builder, 'C');
            builder.addAllGlasses('G')
                .addBlock('P', BotaniaBlocks.AlfPortal.get(), 0)
                .addBlock('Y', BotaniaBlocks.NaturaPylon.get(), BotaniaBlocks.NaturaPylon.getMeta())
                .addBlock('W', BotaniaBlocks.LivingWood.get(), 0);
        });
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTN_Recipe.ElvenTradeRecipes;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new GTN_BotanicProcessingLogic(this, getBaseTier()) {

            @Nonnull
            @Override
            protected CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                if (!portalOpen && GTN_ManaSources.getTotalAvailable(GTN_ElvenGateway.this) < OPEN_COST) {
                    return SimpleCheckRecipeResult.ofFailure("not_enough_mana");
                }
                return super.validateRecipe(recipe);
            }

            @Nonnull
            @Override
            protected CheckRecipeResult onRecipeStart(@Nonnull GTRecipe recipe) {
                if (!portalOpen) {
                    if (!GTN_ManaSources.consumeTotal(GTN_ElvenGateway.this, OPEN_COST)) {
                        return SimpleCheckRecipeResult.ofFailure("not_enough_mana");
                    }
                    portalOpen = true;
                }
                return super.onRecipeStart(recipe);
            }
        };
    }

    @Override
    protected void runMachine(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.runMachine(aBaseMetaTileEntity, aTick);

        if (!portalOpen || aTick % 20 != 0) return;
        // a disabled machine does not keep the portal open
        if (!aBaseMetaTileEntity.isAllowedToWork()) {
            portalOpen = false;
            return;
        }
        // an open portal drains mana all the time, also while it waits for items
        if (GTN_ManaSources.consumeTotal(this, MANA_PER_SECOND)) return;
        portalOpen = false;
        if (mMaxProgresstime > 0) {
            stopMachine(SimpleShutDownReason.ofCritical("not_enough_mana"));
        } else {
            setCheckRecipeResult(SimpleCheckRecipeResult.ofFailure("not_enough_mana"));
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        nbt.setBoolean("portalOpen", portalOpen);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        portalOpen = nbt.getBoolean("portalOpen");
    }

    @Override
    public void GTN_WailaNBT(TileEntity tile, Dimensions dimensions, NBTTagCompound tag) {
        tag.setBoolean("portalOpen", portalOpen);
        tag.setLong("availableMana", GTN_ManaSources.getTotalAvailable(this));
    }

    @Override
    public void GTN_WailaBody(ItemStack itemStack, List<String> info, NBTTagCompound tag) {
        if (tag.getBoolean("portalOpen")) {
            info.add(GTN_Utils.tr("multiblock.ElvenGateway.waila.open"));
            info.add(GTN_Utils.tr("multiblock.ElvenGateway.waila.drain", formatNumber(MANA_PER_SECOND)));
        } else {
            info.add(GTN_Utils.tr("multiblock.ElvenGateway.waila.closed"));
            info.add(GTN_Utils.tr("multiblock.ElvenGateway.waila.open_cost", formatNumber(OPEN_COST)));
        }
        info.add(GTN_Utils.tr("multiblock.ElvenGateway.waila.available", formatNumber(tag.getLong("availableMana"))));
    }

    public static List<String> getRecipeInfo(RecipeDisplayInfo recipeInfo) {
        return List.of(
            GTN_Utils.tr("multiblock.ElvenGateway.recipe_info.open_cost", formatNumber(OPEN_COST)),
            GTN_Utils.tr("multiblock.ElvenGateway.recipe_info.drain", formatNumber(MANA_PER_SECOND)));
    }
}
