package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.Magic;

import bartworks.API.BorosilicateGlass;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.ParallelHelper;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.visnet.VisNetHandler;

import javax.annotation.Nonnull;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static thaumcraft.common.config.ConfigBlocks.blockMetalDevice;
import static thaumcraft.common.config.ConfigBlocks.blockStoneDevice;

public class GTN_LargeArcaneAssembler extends GTN_MultiBlockBase<GTN_LargeArcaneAssembler> {
    public GTN_LargeArcaneAssembler(int id, String name) {
        super(id, name);
    }

    public GTN_LargeArcaneAssembler(String name) {
        super(name);
    }

    public int getOffsetHorizontal() {
        return 2;
    }

    public int getOffsetVertical() {
        return 5;
    }

    public int getOffsetDepth() {
        return 0;
    }

    public GTN_Casings getMainCasings() {
        return GTN_Casings.FrostProofMachineCasing;
    }

    public GTN_LargeArcaneAssembler createNewMetaEntity() {
        return new GTN_LargeArcaneAssembler(this.mName);
    }
    public boolean isNoMaintenanceIssue() {return true;}

    public String[][] getShape() {
        return new String[][]{
            {"BBBBB","BAAAB","BAAAB","BAAAB","BBBBB"},
            {"BAAAB","A   A","A   A","A   A","BAAAB"},
            {"BAAAB","A   A","A F A","A   A","BAAAB"},
            {"BAAAB","A   A","A   A","A   A","BAAAB"},
            {"BAAAB","A   A","A E A","A   A","BAAAB"},
            {"BB~BB","BCCCB","BCCCB","BCCCB","BBBBB"}};
    }
    private byte glassTier = 0;
    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setByte("glassTier", glassTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        glassTier = aNBT.getByte("glassTier");
    }

    public void createGtnTooltip(MultiblockTooltipBuilder builder) {
        builder.addInfo(this.tr("tooltip.00"))
            .addInfo(this.tr("tooltip.01"))
            .addInfo(this.tr("tooltip.02"))
            .addInfo(this.tr("tooltip.03"))
            .addInfo(Constants.AUTHOR_CRAZER)
            .beginStructureBlock(5, 6, 5, false)
            .addEnergyHatch(EnumChatFormatting.GOLD + "1", new int[]{1})
            .addInputBus(EnumChatFormatting.GOLD + "1", new int[]{1})
            .addOutputBus(EnumChatFormatting.GOLD + "1", new int[]{1});
    }
    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {

        if (!super.checkMachine(aBaseMetaTileEntity, aStack)) {
            return false;
        }
        for (MTEHatchEnergy hatch : mEnergyHatches) {
            if (hatch.mTier > glassTier) {
                return false;
            }else if (glassTier == 10){
                return true;
            }
        }
        return true;
    }
    @Override
    public IStructureDefinition<GTN_LargeArcaneAssembler> getStructureDefinition() {
        return IStructureDefinition.<GTN_LargeArcaneAssembler>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement('C', ofBlock(blockMetalDevice, 9))
            .addElement('A', withChannel(
                "glass",
                BorosilicateGlass.ofBoroGlass(
                    (byte) 0,
                    (byte) 1,
                    Byte.MAX_VALUE,
                    (te, t) -> te.glassTier = t,
                    te -> te.glassTier)))
            .addElement('E', ofBlock(blockStoneDevice, 10))
            .addElement('F', ofBlock(blockStoneDevice, 11))
            .addElement(
                'B',
                buildHatchAdder(GTN_LargeArcaneAssembler.class).atLeast(InputBus, OutputBus, Energy)
                    .casingIndex(getMainCasings().textureId)
                    .dot(2)
                    .buildAndChain(
                        onElementPass(
                            GTN_LargeArcaneAssembler::mainCasingAdd,
                            ofBlock(getMainCasings().getBlock(), getMainCasings().meta))))
            .build();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTN_Recipe.ARCANE_ASSEMBLER_RECIPES;
    }
    @Override
    public int getMaxParallelRecipes() {
        return 10;
    }
    @Override
    public OverclockType getOverclockType() {
        return OverclockType.NONE;
    }
    public float getSpeedBonus() {
        return 0.10F;
    }
    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {
            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {

                AspectList required = recipe.getMetadata(GTN_Recipe.ASPECT_COST);

                if (required != null && required.size() > 0) {
                    World world = getBaseMetaTileEntity().getWorld();
                    int x = getBaseMetaTileEntity().getXCoord();
                    int y = getBaseMetaTileEntity().getYCoord();
                    int z = getBaseMetaTileEntity().getZCoord();

                    for (Aspect aspect : required.getAspects()) {

                        int amount = required.getAmount(aspect);

                        int drained = VisNetHandler.drainVis(world, x, y, z, aspect, amount);

                        if (drained < amount) { return CheckRecipeResultRegistry.insufficientPower(0); }
                    }
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        };
    }

}
