package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.LUV;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static thaumcraft.common.config.ConfigBlocks.blockMetalDevice;
import static thaumcraft.common.config.ConfigBlocks.blockStoneDevice;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.ArcaneAssemblerCraftingFX;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_ProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData.SimpleMetaData;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import bartworks.API.BorosilicateGlass;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.lib.research.ResearchManager;

public class GTN_LargeArcaneAssembler extends GTN_MultiBlockBase<GTN_LargeArcaneAssembler> {

    protected ArrayList<String> Research = new ArrayList<>();

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
        return GTN_Casings.MagicCasing;
    }

    public GTN_LargeArcaneAssembler createNewMetaEntity() {
        return new GTN_LargeArcaneAssembler(this.mName);
    }

    public boolean isNoMaintenanceIssue() {
        return true;
    }

    public String[][] getShape() {
        // spotless:off
        return new String[][]{
            {"BBBBB","BAAAB","BAAAB","BAAAB","BBBBB"},
            {"BAAAB","A   A","A   A","A   A","BAAAB"},
            {"BAAAB","A   A","A E A","A   A","BAAAB"},
            {"BAAAB","A   A","A   A","A   A","BAAAB"},
            {"BAAAB","A   A","A D A","A   A","BAAAB"},
            {"BB~BB","BCCCB","BCCCB","BCCCB","BBBBB"}
        };
        //spotless:on
    }

    private byte glassTier = 0;

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setByte("glassTier", glassTier);
        NBTTagList list = new NBTTagList();
        for (String key : Research) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("ResearchKey", key);
            list.appendTag(tag);
        }
        aNBT.setTag("ResearchList", list);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        glassTier = aNBT.getByte("glassTier");
        Research.clear();
        if (aNBT.hasKey("ResearchList")) {
            NBTTagList list = aNBT.getTagList("ResearchList", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                Research.add(
                    list.getCompoundTagAt(i)
                        .getString("ResearchKey"));
            }
        }
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);

        if (!aBaseMetaTileEntity.isServerSide()) return;

        String owner = aBaseMetaTileEntity.getOwnerName();
        if (owner == null || owner.isEmpty()) return;

        Research.clear();

        for (ResearchCategoryList category : ResearchCategories.researchCategories.values()) {

            for (ResearchItem item : category.research.values()) {

                if (ResearchManager.isResearchComplete(owner, item.key)) {
                    Research.add(item.key);
                }
            }
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity tile, long tick) {
        super.onPostTick(tile, tick);
        if (!tile.isClientSide()) return;
        if (!tile.isActive()) return;
        if (tick % 2 == 0) {
            spawnLAAStyleFX(tile);
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnLAAStyleFX(IGregTechTileEntity tile) {
        World world = tile.getWorld();
        double baseX = tile.getXCoord() + 0.5;
        double baseZ = tile.getZCoord() + 0.5;
        for (int i = 0; i < 12; i++) {
            double x = baseX + (world.rand.nextDouble() - 0.5D) * 4.0D;
            double y = tile.getYCoord() + world.rand.nextDouble() * 6.0D;
            double z = baseZ + (world.rand.nextDouble() - 0.5D) * 4.0D;
            Minecraft.getMinecraft().effectRenderer.addEffect(new ArcaneAssemblerCraftingFX(world, x, y, z));
        }
    }

    public void createGtnTooltip(MultiblockTooltipBuilder builder) {
        builder.addInfo(this.tr("tooltip.00"))
            .addInfo(this.tr("tooltip.01"))
            .addInfo(this.tr("tooltip.02"))
            .addInfo(this.tr("tooltip.03"))
            .addInfo(this.tr("tooltip.04"))
            .addInfo(Constants.AUTHOR_CRAZER)
            .beginStructureBlock(5, 6, 5, false)
            .addEnergyHatch(EnumChatFormatting.GOLD + "1", 1)
            .addInputBus(EnumChatFormatting.GOLD + "1", 1)
            .addOutputBus(EnumChatFormatting.GOLD + "1", 1);
    }

    @Override
    public boolean GTN_checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        for (MTEHatchEnergy hatch : mEnergyHatches) {
            if (hatch.mTier > glassTier) {
                return false;
            }
        }
        return true;
    }

    @Override
    public IStructureDefinition<GTN_LargeArcaneAssembler> getStructureDefinition() {
        return IStructureDefinition.<GTN_LargeArcaneAssembler>builder()
            .addShape(getStructurePieceMain(), transpose(getShape()))
            .addElement('C', ofBlock(blockMetalDevice, 9))
            .addElement(
                'A',
                withChannel(
                    "glass",
                    BorosilicateGlass.ofBoroGlass(
                        (byte) 0,
                        (byte) 1,
                        Byte.MAX_VALUE,
                        (te, t) -> te.glassTier = t,
                        te -> te.glassTier)))
            .addElement('D', ofBlock(blockStoneDevice, 10))
            .addElement('E', ofBlock(blockStoneDevice, 11))
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
        return GTN_Recipe.ArcaneAssemblerRecipes;
    }

    @Override
    public OverclockType getOverclockType() {
        return OverclockType.NONE;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new GTN_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {

                CheckRecipeResult base = super.validateRecipe(recipe);
                if (!base.wasSuccessful()) {
                    return base;
                }
                String researchKey = recipe.getMetadata(SimpleMetaData.RESEARCH_KEY);
                String owner = getBaseMetaTileEntity().getOwnerName();
                if (researchKey != null && !ResearchManager.isResearchComplete(owner, researchKey)) {
                    return SimpleCheckRecipeResult.ofFailure("research_not_completed");
                }
                AspectList required = recipe.getMetadata(SimpleMetaData.ASPECT_COST);
                if (required != null && required.size() > 0) {
                    World world = getBaseMetaTileEntity().getWorld();
                    int x = getBaseMetaTileEntity().getXCoord();
                    int y = getBaseMetaTileEntity().getYCoord();
                    int z = getBaseMetaTileEntity().getZCoord();
                    int minAspectAmount = Integer.MAX_VALUE;
                    for (Aspect aspect : required.getAspects()) {
                        int available = VisNetHandler.drainVis(world, x, y, z, aspect, Integer.MAX_VALUE);
                        if (available <= 0) {
                            return SimpleCheckRecipeResult.ofFailure("not_enough_aspects");
                        }
                        minAspectAmount = Math.min(minAspectAmount, available);
                    }
                    setMaxParallel(minAspectAmount);
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }.setOverclockType(getOverclockType())
            .setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

}
