package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.LUV;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.OutputBus;
import static thaumcraft.common.config.ConfigBlocks.blockMetalDevice;
import static thaumcraft.common.config.ConfigBlocks.blockStoneDevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gregtech.api.enums.SoundResource;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.ArcaneAssemblerCraftingFX;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.ElementBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_ProcessingLogic;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_StructureUtility;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.TierData;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData.SimpleMetaData;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

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
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.lib.research.ResearchManager;

public class GTN_LargeArcaneAssembler extends GTN_MultiBlockBase<GTN_LargeArcaneAssembler> {

    TierData glass = createTierData("glass");
    protected ArrayList<String> research = new ArrayList<>();

    public GTN_LargeArcaneAssembler(int id, String name) {
        super(id, name);
    }

    public GTN_LargeArcaneAssembler(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_LargeArcaneAssembler>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "LargeArcaneAssembler",
                // spotless:off
                new String[][]{
                    {"BBBBB","BAAAB","BAAAB","BAAAB","BBBBB"},
                    {"BAAAB","A   A","A   A","A   A","BAAAB"},
                    {"BAAAB","A   A","A E A","A   A","BAAAB"},
                    {"BAAAB","A   A","A   A","A   A","BAAAB"},
                    {"BAAAB","A   A","A D A","A   A","BAAAB"},
                    {"BB~BB","BCCCB","BCCCB","BCCCB","BBBBB"}
                },
                //spotless:on
                new MultiblockOffsets(2, 5, 0),
                new MultiblockArea(5, 6, 5),
                1,
                GTN_Casings.MagicCasing));
    }

    @Override
    public GTN_LargeArcaneAssembler createNewMetaEntity() {
        return new GTN_LargeArcaneAssembler(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addInputBus()
            .addOutputBus()
            .addEnergyHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.CRAZER;
    }

    @Override
    public IStructureDefinition<GTN_LargeArcaneAssembler> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addElement('C', ofBlock(blockMetalDevice, 9))
                .addElement('A', GTN_StructureUtility.createAllTieredGlass(glass))
                .addElement('D', ofBlock(blockStoneDevice, 10))
                .addElement('E', ofBlock(blockStoneDevice, 11))
                .addElement(
                    'B',
                    ElementBuilder.create(GTN_LargeArcaneAssembler.class, this)
                        .casing(mainCasing)
                        .hatches(InputBus, OutputBus, Energy)
                        .build()));
    }

    public boolean isNoMaintenanceIssue() {
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        NBTTagList list = new NBTTagList();
        for (String key : research) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("ResearchKey", key);
            list.appendTag(tag);
        }
        aNBT.setTag("ResearchList", list);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        research.clear();
        if (aNBT.hasKey("ResearchList")) {
            NBTTagList list = aNBT.getTagList("ResearchList", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                research.add(
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

        research.clear();

        for (ResearchCategoryList category : ResearchCategories.researchCategories.values()) {

            for (ResearchItem item : category.research.values()) {

                if (ResearchManager.isResearchComplete(owner, item.key)) {
                    research.add(item.key);
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

    @Override
    public boolean GTN_checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        for (MTEHatchEnergy hatch : mEnergyHatches) {
            if (hatch.mTier > glass.getCasingTier()) {
                return false;
            }
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GTCEU_LOOP_ASSEMBLER;
    }
}
