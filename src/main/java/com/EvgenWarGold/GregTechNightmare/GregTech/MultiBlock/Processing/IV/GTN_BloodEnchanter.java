package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.IV;

import static WayofTime.alchemicalWizardry.ModBlocks.ritualStone;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Muffler;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.SoundResource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.ElementBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import WayofTime.alchemicalWizardry.ModBlocks;
import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.common.tiles.TileInfusionProvider;

public class GTN_BloodEnchanter extends GTN_MultiBlockBase<GTN_BloodEnchanter> {

    private static final int MODE_APPLY_BOOKS = 1;
    private static final int MODE_UPGRADE_BOOK = 2;
    private static final int MODE_2_PRAECANTATIO_COST = 32;
    private static final String ASPECT_PRAECANTATIO = "praecantatio";
    private static final String XP_FLUID_NAME = "xpjuice";
    // 30 player lvl
    private static final int FIXED_XP_COST_MB = 16500;
    protected final ArrayList<TileInfusionProvider> tileInfusionProviders = new ArrayList<>();
    private int lastMode = 0;
    private int lastNeedLP = 0;
    private int lastNeedXP = 0;
    private int lastNeedPraecantatio = 0;

    public GTN_BloodEnchanter(int id, String name) {
        super(id, name);
    }

    public GTN_BloodEnchanter(String name) {
        super(name);
    }

    @Override
    public List<StructureVariant<GTN_BloodEnchanter>> getStructureVariants() {
        return Arrays.asList(
            new StructureVariant<>(
                "BloodEnchanter",
                // spotless:off
                new String[][]{
                    {"BBBBBBBBBBB","BBBBBBBBBBB","BBBBBBBBBBB","BBBBBBBBBBB","BBBBBBBBBBB","BBBBBBBBBBB","BBBBBBBBBBB","BBBBBBBBBBB","BBBBBBBBBBB","BBBBBBBBBBB","BBBBBBBBBBB"},
                    {"BAAAAAAAAAB","A         A","A         A","A         A","A   C C   A","A         A","A   C C   A","A         A","A         A","A         A","BAAAAAAAAAB"},
                    {"BAAAAAAAAAB","A         A","A         A","A  C   C  A","A         A","A         A","A         A","A  C   C  A","A         A","A         A","BAAAAAAAAAB"},
                    {"BAAAAAAAAAB","A         A","A C C C C A","A         A","A C     C A","A         A","A C     C A","A         A","A C C C C A","A         A","BAAAAAAAAAB"},
                    {"BAAAAAAAAAB","A         A","A CC   CC A","A C     C A","A         A","A         A","A         A","A C     C A","A CC   CC A","A         A","BAAAAAAAAAB"},
                    {"BAAAAAAAAAB","A         A","A C     C A","A         A","A         A","A         A","A         A","A         A","A C     C A","A         A","BAAAAAAAAAB"},
                    {"BAAAAAAAAAB","A         A","A C  D  C A","A         A","A         A","A D  F  D A","A         A","A         A","A C  D  C A","A         A","BAAAAAAAAAB"},
                    {"BAAAAAAAAAB","A    C    A","A C CCC C A","A    C    A","A C     C A","ACCC E CCCA","A C     C A","A    C    A","A C CCC C A","A    C    A","BAAAAAAAAAB"},
                    {"BAAAAAAAAAB","A         A","A C     C A","A         A","A         A","A         A","A         A","A         A","A C     C A","A         A","BAAAAAAAAAB"},
                    {"BBBBB~BBBBB","BBBBBBBBBBB","BBBBBBBBBBB","BBBBBBBBBBB","BBBBBBBBBBB","BBBBBBBBBBB","BBBBBBBBBBB","BBBBBBBBBBB","BBBBBBBBBBB","BBBBBBBBBBB","BBBBBBBBBBB"}
                },
                //spotless:on
                new MultiblockOffsets(5, 9, 0),
                new MultiblockArea(11, 10, 11),
                1,
                GTN_Casings.RobustTungstenSteelMachineCasing));
    }

    @Override
    public GTN_BloodEnchanter createNewMetaEntity() {
        return new GTN_BloodEnchanter(this.mName);
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {
        builder.addInputBus()
            .addOutputBus()
            .addInputHatch()
            .addMufflerHatch();
    }

    @Override
    public Authors getAuthor() {
        return Authors.CRAZER;
    }

    @Override
    public IStructureDefinition<GTN_BloodEnchanter> getStructureDefinition() {
        return buildStructureDefinition(
            builder -> builder.addElement('A', chainAllGlasses())
                .addElement('C', ofBlock(ritualStone, 0))
                .addElement('D', ofBlock(ModBlocks.blockPedestal, 0))
                .addElement('E', ofBlock(ModBlocks.blockMasterStone, 0))
                .addElement('F', ofBlock(ModBlocks.blockAltar, 0))
                .addElement(
                    'B',
                    ElementBuilder.create(GTN_BloodEnchanter.class, this)
                        .casing(mainCasing)
                        .hatches(InputBus, InputHatch, Muffler, OutputBus)
                        .tileAdder(GTN_BloodEnchanter::addInfusionProvider)
                        .build()));
    }

    @Override
    public OverclockType getOverclockType() {
        return OverclockType.NONE;
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mLastMode", lastMode);
        aNBT.setInteger("mLastNeedLP", lastNeedLP);
        aNBT.setInteger("mLastNeedXP", lastNeedXP);
        aNBT.setInteger("mLastNeedPraecantatio", lastNeedPraecantatio);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        lastMode = aNBT.getInteger("mLastMode");
        lastNeedLP = aNBT.getInteger("mLastNeedLP");
        lastNeedXP = aNBT.getInteger("mLastNeedXP");
        lastNeedPraecantatio = aNBT.getInteger("mLastNeedPraecantatio");
    }

    @Override
    public int getPollutionPerSecond(ItemStack aStack) {
        return 128000;
    }

    public boolean addInfusionProvider(TileEntity aTileEntity) {
        if (aTileEntity instanceof TileInfusionProvider provider) {
            if (!this.tileInfusionProviders.contains(provider)) {
                this.tileInfusionProviders.add(provider);
            }
            return true;
        }
        return false;
    }

    private static class EnchantmentData {

        public int enchant;
        public int level;

        public EnchantmentData(int enchant, int level) {
            this.enchant = enchant;
            this.level = level;
        }
    }

    private static class RecipeData {

        public int mode;
        public ItemStack sourceStack;
        public ItemStack resultStack;
        public List<EnchantmentData> enchants = new ArrayList<>();
        public int lpCost;
        public int xpCost;
        public int praecantatioCost;
    }

    private ArrayList<ItemStack> getSafeStoredInputs() {
        ArrayList<ItemStack> inputs = getStoredInputs();
        return inputs == null ? new ArrayList<>() : inputs;
    }

    private int getModeFromCircuit() {
        for (ItemStack stack : getSafeStoredInputs()) {
            if (isIntegratedCircuit(stack, MODE_APPLY_BOOKS)) {
                return MODE_APPLY_BOOKS;
            }
            if (isIntegratedCircuit(stack, MODE_UPGRADE_BOOK)) {
                return MODE_UPGRADE_BOOK;
            }
        }
        return 0;
    }

    private boolean isIntegratedCircuit(ItemStack stack, int config) {
        if (stack == null || stack.getItem() == null) return false;
        String unlocalizedName = stack.getUnlocalizedName();
        if (unlocalizedName == null) return false;
        if (!unlocalizedName.toLowerCase()
            .contains("circuit")) {
            return false;
        }
        return stack.getItemDamage() == config;
    }

    private ItemStack findTargetItem() {
        for (ItemStack stack : getSafeStoredInputs()) {
            if (stack == null) continue;
            if (stack.getItem() == Items.enchanted_book) continue;
            if (isIntegratedCircuit(stack, MODE_APPLY_BOOKS) || isIntegratedCircuit(stack, MODE_UPGRADE_BOOK)) continue;
            return stack;
        }
        return null;
    }

    private List<ItemStack> findEnchantedBooks() {
        List<ItemStack> books = new ArrayList<>();
        for (ItemStack stack : getSafeStoredInputs()) {
            if (stack != null && stack.getItem() == Items.enchanted_book) {
                books.add(stack);
            }
        }
        return books;
    }

    private ItemStack findSingleEnchantedBook() {
        for (ItemStack stack : getSafeStoredInputs()) {
            if (stack != null && stack.getItem() == Items.enchanted_book) {
                return stack;
            }
        }
        return null;
    }

    private void appendBookEnchantments(ItemStack book, List<EnchantmentData> result) {
        if (book == null || book.getItem() != Items.enchanted_book) {
            return;
        }
        NBTTagList enchants = Items.enchanted_book.func_92110_g(book);
        if (enchants == null) return;
        for (int i = 0; i < enchants.tagCount(); i++) {
            NBTTagCompound enchant = enchants.getCompoundTagAt(i);
            short id = enchant.getShort("id");
            short lvl = enchant.getShort("lvl");
            result.add(new EnchantmentData(id, lvl));
        }
    }

    private List<EnchantmentData> readBookEnchantments(ItemStack book) {
        List<EnchantmentData> result = new ArrayList<>();
        appendBookEnchantments(book, result);
        return result;
    }

    private boolean isEnchantmentValidForItem(short id, ItemStack enchantItem, List<EnchantmentData> pending) {
        if (id < 0 || id >= Enchantment.enchantmentsList.length) return false;
        Enchantment ench = Enchantment.enchantmentsList[id];
        if (ench == null || ench.type == null) return false;
        if (!ench.canApply(enchantItem) || !ench.type.canEnchantItem(enchantItem.getItem())) {
            return false;
        }
        if (EnchantmentHelper.getEnchantmentLevel(id, enchantItem) > 0) {
            return false;
        }
        for (EnchantmentData data : pending) {
            Enchantment otherEnch = Enchantment.enchantmentsList[data.enchant];
            if (otherEnch == null) continue;
            if (!otherEnch.canApplyTogether(ench) || !ench.canApplyTogether(otherEnch)) {
                return false;
            }
        }
        return true;
    }

    private int calculateLpCost(List<EnchantmentData> enchants) {
        int lpRequired = 0;
        for (EnchantmentData data : enchants) {
            Enchantment ench = Enchantment.enchantmentsList[data.enchant];
            if (ench == null) continue;
            double lpDiff = Math.min(
                Integer.MAX_VALUE,
                500D * (15D / (double) ench.getWeight() * 1.05D)
                    * (0.75D + 2.25D * (double) data.level / (double) ench.getMaxLevel())
                    * (0.9D + enchants.size() * 0.05D));
            if (lpDiff + (double) lpRequired > (double) Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            lpRequired += (int) lpDiff;
        }
        return lpRequired;
    }

    private int getFixedXpCost() {
        return FIXED_XP_COST_MB;
    }

    private void applyEnchantments(ItemStack target, List<EnchantmentData> enchants) {
        if (target.stackTagCompound == null) {
            target.setTagCompound(new NBTTagCompound());
        }
        if (!target.stackTagCompound.hasKey("ench", 9)) {
            target.stackTagCompound.setTag("ench", new NBTTagList());
        }
        NBTTagList list = target.stackTagCompound.getTagList("ench", 10);
        for (EnchantmentData data : enchants) {
            if (EnchantmentHelper.getEnchantmentLevel(data.enchant, target) > 0) {
                continue;
            }
            NBTTagCompound ench = new NBTTagCompound();
            ench.setShort("id", (short) data.enchant);
            ench.setShort("lvl", (short) data.level);
            list.appendTag(ench);
        }
    }

    private ItemStack createUpgradedBook(ItemStack sourceBook, List<EnchantmentData> enchants) {
        ItemStack result = sourceBook.copy();
        result.stackSize = 1;
        NBTTagList list = new NBTTagList();
        for (EnchantmentData data : enchants) {
            NBTTagCompound ench = new NBTTagCompound();
            ench.setShort("id", (short) data.enchant);
            ench.setShort("lvl", (short) data.level);
            list.appendTag(ench);
        }
        NBTTagCompound tag = result.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            result.setTagCompound(tag);
        }
        tag.setTag("StoredEnchantments", list);
        return result;
    }

    private RecipeData buildApplyBooksRecipe() {
        ItemStack target = findTargetItem();
        List<ItemStack> books = findEnchantedBooks();
        if (target == null || books.isEmpty()) {
            return null;
        }
        List<EnchantmentData> allBookEnchants = new ArrayList<>();
        for (ItemStack book : books) {
            appendBookEnchantments(book, allBookEnchants);
        }
        if (allBookEnchants.isEmpty()) {
            return null;
        }
        ItemStack resultTarget = target.copy();
        resultTarget.stackSize = 1;
        List<EnchantmentData> validEnchants = new ArrayList<>();
        for (EnchantmentData data : allBookEnchants) {
            if (isEnchantmentValidForItem((short) data.enchant, resultTarget, validEnchants)) {
                validEnchants.add(data);
            }
        }
        if (validEnchants.isEmpty()) {
            return null;
        }
        applyEnchantments(resultTarget, validEnchants);
        RecipeData recipe = new RecipeData();
        recipe.mode = MODE_APPLY_BOOKS;
        recipe.sourceStack = target;
        recipe.resultStack = resultTarget;
        recipe.enchants = validEnchants;
        recipe.lpCost = calculateLpCost(validEnchants);
        recipe.xpCost = getFixedXpCost();
        recipe.praecantatioCost = 0;
        return recipe;
    }

    private RecipeData buildUpgradeBookRecipe() {
        ItemStack sourceBook = findSingleEnchantedBook();
        if (sourceBook == null) {
            return null;
        }
        List<EnchantmentData> enchants = readBookEnchantments(sourceBook);
        if (enchants.isEmpty()) {
            return null;
        }
        List<EnchantmentData> upgraded = new ArrayList<>();
        for (EnchantmentData data : enchants) {
            upgraded.add(new EnchantmentData(data.enchant, data.level + 1));
        }
        RecipeData recipe = new RecipeData();
        recipe.mode = MODE_UPGRADE_BOOK;
        recipe.sourceStack = sourceBook;
        recipe.resultStack = createUpgradedBook(sourceBook, upgraded);
        recipe.enchants = upgraded;
        recipe.lpCost = calculateLpCost(upgraded);
        recipe.xpCost = getFixedXpCost();
        recipe.praecantatioCost = MODE_2_PRAECANTATIO_COST;
        return recipe;
    }

    private FluidStack findXpFluidStack(int amount) {
        ArrayList<FluidStack> storedFluids = getStoredFluids();
        if (storedFluids == null || storedFluids.isEmpty()) return null;
        Fluid xpJuice = FluidRegistry.getFluid(XP_FLUID_NAME);
        if (xpJuice == null) return null;
        for (FluidStack stored : storedFluids) {
            if (stored == null || stored.getFluid() == null) continue;
            if (stored.getFluid() == xpJuice && stored.amount >= amount) {
                return new FluidStack(xpJuice, amount);
            }
        }

        return null;
    }

    private int getAvailableXpFluidAmount() {
        ArrayList<FluidStack> storedFluids = getStoredFluids();
        if (storedFluids == null || storedFluids.isEmpty()) return 0;
        Fluid xpJuice = FluidRegistry.getFluid(XP_FLUID_NAME);
        if (xpJuice == null) return 0;
        int total = 0;
        for (FluidStack stored : storedFluids) {
            if (stored == null || stored.getFluid() == null) continue;
            if (stored.getFluid() == xpJuice) {
                total += stored.amount;
            }
        }
        return total;
    }

    private boolean drainXpFluid(int amount) {
        FluidStack toDrain = findXpFluidStack(amount);
        return toDrain != null && depleteInput(toDrain);
    }

    private long getPraecantatioAmount() {
        Aspect praecantatio = Aspect.getAspect(ASPECT_PRAECANTATIO);
        if (praecantatio == null) {
            return 0L;
        }
        long total = 0L;
        for (TileInfusionProvider provider : tileInfusionProviders) {
            if (provider == null) continue;
            total += provider.getAspectAmountInNetwork(praecantatio);
        }
        return total;
    }

    private boolean drainPraecantatio(int amount) {
        Aspect praecantatio = Aspect.getAspect(ASPECT_PRAECANTATIO);
        if (praecantatio == null) {
            return false;
        }
        long remaining = amount;
        for (TileInfusionProvider provider : tileInfusionProviders) {
            if (provider == null) continue;
            long available = provider.getAspectAmountInNetwork(praecantatio);
            if (available <= 0L) continue;
            long toTake = Math.min(available, remaining);
            if (provider.takeFromContainer(praecantatio, (int) toTake)) {
                remaining -= toTake;
            }
            if (remaining <= 0L) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        lastMode = 0;
        lastNeedLP = 0;
        lastNeedXP = 0;
        lastNeedPraecantatio = 0;
        int mode = getModeFromCircuit();
        lastMode = mode;
        if (mode == 0) {
            return SimpleCheckRecipeResult.ofFailure("no_mode_selected");
        }
        RecipeData recipe;
        if (mode == MODE_APPLY_BOOKS) {
            recipe = buildApplyBooksRecipe();
        } else if (mode == MODE_UPGRADE_BOOK) {
            recipe = buildUpgradeBookRecipe();
        } else {
            return SimpleCheckRecipeResult.ofFailure("no_mode_selected");
        }
        if (recipe == null) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }
        lastNeedLP = recipe.lpCost;
        lastNeedXP = recipe.xpCost;
        lastNeedPraecantatio = recipe.praecantatioCost;
        String owner = getBaseMetaTileEntity() == null ? null : getBaseMetaTileEntity().getOwnerName();
        int currentLP = SoulNetworkHandler.getCurrentEssence(owner);
        if (currentLP < recipe.lpCost) {
            return SimpleCheckRecipeResult.ofFailure("not_enough_lp");
        }
        if (getAvailableXpFluidAmount() < recipe.xpCost) {
            return SimpleCheckRecipeResult.ofFailure("not_enough_xp");
        }
        if (recipe.praecantatioCost > 0) {
            if (tileInfusionProviders.isEmpty()) {
                return SimpleCheckRecipeResult.ofFailure("not_enough_praecantatio");
            }
            if (getPraecantatioAmount() < (long) recipe.praecantatioCost) {
                return SimpleCheckRecipeResult.ofFailure("not_enough_praecantatio");
            }
        }
        SoulNetworkHandler.syphonFromNetwork(owner, recipe.lpCost);
        if (!drainXpFluid(recipe.xpCost)) {
            return SimpleCheckRecipeResult.ofFailure("not_enough_xp");
        }
        if (recipe.praecantatioCost > 0 && !drainPraecantatio(recipe.praecantatioCost)) {
            return SimpleCheckRecipeResult.ofFailure("not_enough_praecantatio");
        }
        recipe.sourceStack.stackSize--;
        mOutputItems = new ItemStack[] { recipe.resultStack };
        mMaxProgresstime = 200;
        updateSlots();
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public String[] getInfoData() {
        String[] origin = super.getInfoData();
        String modeName;
        if (lastMode == MODE_APPLY_BOOKS) {
            modeName = "Apply Books";
        } else if (lastMode == MODE_UPGRADE_BOOK) {
            modeName = "Upgrade Book";
        } else {
            modeName = "None";
        }
        String[] ret = new String[origin.length + 2];
        System.arraycopy(origin, 0, ret, 0, origin.length);
        ret[origin.length] = EnumChatFormatting.AQUA + "Mode: " + EnumChatFormatting.GOLD + modeName;
        ret[origin.length + 1] = EnumChatFormatting.AQUA + "Blood Required: " + EnumChatFormatting.GOLD + lastNeedLP;
        return ret;
    }

    @Override
    public boolean isEnergyMultiBlock() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.RANDOM_ANVIL_USE;
    }
}
