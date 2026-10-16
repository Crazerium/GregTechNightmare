package com.EvgenWarGold.GregTechNightmare.GregTech.Hatch;

import static appeng.util.item.AEFluidStackType.FLUID_STACK_TYPE;
import static appeng.util.item.AEItemStackType.ITEM_STACK_TYPE;
import static com.EvgenWarGold.GregTechNightmare.GregTech.Mana.AEManaStackType.MANA_STACK_TYPE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_ME_CRAFTING_INPUT_BUFFER;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.EvgenWarGold.GregTechNightmare.GregTech.Mana.AEManaStack;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;

import appeng.api.config.Actionable;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.util.IterationCounter;
import appeng.util.inv.MEInventoryCrafting;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.render.TextureFactory;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumicenergistics.common.storage.AEEssentiaStack;
import thaumicenergistics.common.storage.AEEssentiaStackType;

/**
 * Crafting Input Buffer for magic machines (Thaumcraft and Botania).
 * <p>
 * Essentia (Thaumic Energistics) and mana written in a pattern are pushed by AE2 together with the items and kept in
 * internal buffers of the hatch; the machine takes essentia and mana from these buffers first and from the connected
 * ME network after that.
 */
@IMetaTileEntity.SkipGenerateDescription
public final class GTN_MagicCraftingInputHatch extends MTEHatchCraftingInputME {

    private static final String NBT_STORED_ESSENTIA = "gtnStoredEssentia";
    private static final String NBT_STORED_MANA = "gtnStoredMana";

    private final AspectList storedEssentia = new AspectList();
    private long storedMana;

    private MachineSource source;

    public GTN_MagicCraftingInputHatch(int id, String name) {
        super(id, name, name, true);
    }

    public GTN_MagicCraftingInputHatch(String name, int tier, String[] description, ITexture[][][] textures) {
        super(name, tier, description, textures, true);
    }

    @Override
    public GTN_MagicCraftingInputHatch newMetaEntity(IGregTechTileEntity tile) {
        return new GTN_MagicCraftingInputHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    /** Monitor of the given stack type of the ME network this hatch is connected to, null if offline. */
    @SuppressWarnings("unchecked")
    private <T extends IAEStack<T>> IMEMonitor<T> getMonitor(IAEStackType<T> type) {
        AENetworkProxy proxy = getProxy();
        if (proxy == null || !proxy.isActive()) return null;
        try {
            return (IMEMonitor<T>) proxy.getStorage()
                .getMEMonitor(type);
        } catch (GridAccessException e) {
            return null;
        }
    }

    private MachineSource getSource() {
        if (source == null) source = new MachineSource((IActionHost) getBaseMetaTileEntity());
        return source;
    }

    private void markDirtyBase() {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base != null) base.markDirty();
    }

    // region Essentia
    /** Essentia storage of the ME network this hatch is connected to, null if offline. */
    public IMEMonitor<AEEssentiaStack> getEssentiaMonitor() {
        return getMonitor(AEEssentiaStackType.ESSENTIA_STACK_TYPE);
    }

    public static long getEssentiaAmount(IMEMonitor<AEEssentiaStack> monitor, Aspect aspect) {
        if (monitor == null || aspect == null) return 0;
        AEEssentiaStack stack = monitor.getAvailableItem(new AEEssentiaStack(aspect), IterationCounter.fetchNewId());
        return stack != null ? stack.getStackSize() : 0;
    }

    public long extractEssentia(IMEMonitor<AEEssentiaStack> monitor, Aspect aspect, long amount) {
        if (monitor == null || aspect == null || amount <= 0) return 0;
        AEEssentiaStack extracted = monitor
            .extractItems(new AEEssentiaStack(aspect, amount), Actionable.MODULATE, getSource());
        return extracted != null ? extracted.getStackSize() : 0;
    }

    /** Essentia pushed by AE2 with the patterns and not used yet. */
    public int getStoredEssentia(Aspect aspect) {
        return storedEssentia.getAmount(aspect);
    }

    /** Takes up to {@code amount} essentia from the internal buffer, returns how much was taken. */
    public int takeStoredEssentia(Aspect aspect, int amount) {
        int taken = Math.min(amount, storedEssentia.getAmount(aspect));
        if (taken <= 0) return 0;
        storedEssentia.reduce(aspect, taken);
        if (storedEssentia.getAmount(aspect) <= 0) storedEssentia.remove(aspect);
        markDirtyBase();
        return taken;
    }
    // endregion

    // region Mana
    /** Mana storage of the ME network this hatch is connected to, null if offline. */
    public IMEMonitor<AEManaStack> getManaMonitor() {
        return getMonitor(MANA_STACK_TYPE);
    }

    public static long getManaAmount(IMEMonitor<AEManaStack> monitor) {
        if (monitor == null) return 0;
        AEManaStack stack = monitor.getAvailableItem(new AEManaStack(), IterationCounter.fetchNewId());
        return stack != null ? stack.getStackSize() : 0;
    }

    public long extractMana(IMEMonitor<AEManaStack> monitor, long amount) {
        if (monitor == null || amount <= 0) return 0;
        AEManaStack extracted = monitor.extractItems(new AEManaStack(amount), Actionable.MODULATE, getSource());
        return extracted != null ? extracted.getStackSize() : 0;
    }

    /** Mana pushed by AE2 with the patterns and not used yet. */
    public long getStoredMana() {
        return storedMana;
    }

    /** Takes up to {@code amount} mana from the internal buffer, returns how much was taken. */
    public long takeStoredMana(long amount) {
        long taken = Math.min(amount, storedMana);
        if (taken <= 0) return 0;
        storedMana -= taken;
        markDirtyBase();
        return taken;
    }
    // endregion

    // region AE2
    @Override
    public IAEStackType<?>[] getSupportedStackTypes() {
        return new IAEStackType<?>[] { ITEM_STACK_TYPE, FLUID_STACK_TYPE, AEEssentiaStackType.ESSENTIA_STACK_TYPE,
            MANA_STACK_TYPE };
    }

    /**
     * GT only knows items and fluids: essentia and mana stacks are taken out of the crafting table into the buffers,
     * the rest of the pattern is pushed by the regular Crafting Input Buffer logic.
     */
    @Override
    public boolean pushPattern(ICraftingPatternDetails details, InventoryCrafting table) {
        if (!(table instanceof MEInventoryCrafting crafting)) return super.pushPattern(details, table);

        IAEStack<?>[] removed = new IAEStack<?>[crafting.getSizeInventory()];
        List<AEEssentiaStack> essentia = new ArrayList<>();
        long mana = 0;
        for (int i = 0; i < removed.length; i++) {
            IAEStack<?> stack = crafting.getAEStackInSlot(i);
            if (stack instanceof AEEssentiaStack essentiaStack) essentia.add(essentiaStack);
            else if (stack instanceof AEManaStack manaStack) mana += manaStack.getStackSize();
            else continue;
            removed[i] = stack;
            crafting.setInventorySlotContents(i, (IAEStack<?>) null);
        }
        if (essentia.isEmpty() && mana == 0) return super.pushPattern(details, table);

        boolean pushed = super.pushPattern(details, table);
        if (pushed) {
            for (AEEssentiaStack stack : essentia) {
                storedEssentia.add(stack.getAspect(), (int) Math.min(Integer.MAX_VALUE, stack.getStackSize()));
            }
            storedMana += mana;
            markDirtyBase();
        } else {
            for (int i = 0; i < removed.length; i++) {
                if (removed[i] != null) crafting.setInventorySlotContents(i, removed[i]);
            }
        }
        return pushed;
    }

    /** Also returns the buffered essentia and mana to the ME network. */
    @Override
    public void refundAll(boolean shouldDrop) {
        super.refundAll(shouldDrop);
        IMEMonitor<AEEssentiaStack> essentiaMonitor = getEssentiaMonitor();
        if (essentiaMonitor != null && storedEssentia.size() > 0) {
            for (Aspect aspect : storedEssentia.getAspects()) {
                int amount = storedEssentia.getAmount(aspect);
                AEEssentiaStack left = essentiaMonitor
                    .injectItems(new AEEssentiaStack(aspect, amount), Actionable.MODULATE, getSource());
                takeStoredEssentia(aspect, amount - (left == null ? 0 : (int) left.getStackSize()));
            }
        }
        IMEMonitor<AEManaStack> manaMonitor = getManaMonitor();
        if (manaMonitor != null && storedMana > 0) {
            AEManaStack left = manaMonitor.injectItems(new AEManaStack(storedMana), Actionable.MODULATE, getSource());
            takeStoredMana(storedMana - (left == null ? 0 : left.getStackSize()));
        }
    }
    // endregion

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        storedEssentia.writeToNBT(nbt, NBT_STORED_ESSENTIA);
        nbt.setLong(NBT_STORED_MANA, storedMana);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        storedEssentia.readFromNBT(nbt, NBT_STORED_ESSENTIA);
        storedMana = nbt.getLong(NBT_STORED_MANA);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        storedEssentia.writeToNBT(tag, NBT_STORED_ESSENTIA);
        tag.setLong(NBT_STORED_MANA, storedMana);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        AspectList essentia = new AspectList();
        essentia.readFromNBT(tag, NBT_STORED_ESSENTIA);
        long mana = tag.getLong(NBT_STORED_MANA);
        if (essentia.size() == 0 && mana == 0) {
            currentTip.add(GTN_Utils.tr("GTN.Hatch.MagicCraftingInput.waila.empty"));
            return;
        }
        if (mana > 0) {
            currentTip.add(
                GTN_Utils.tr(
                    "GTN.Hatch.MagicCraftingInput.waila.mana",
                    EnumChatFormatting.AQUA + NumberFormat.getInstance(Locale.ENGLISH)
                        .format(mana)));
        }
        if (essentia.size() == 0) return;
        currentTip.add(GTN_Utils.tr("GTN.Hatch.MagicCraftingInput.waila.essentia"));
        for (Aspect aspect : essentia.getAspectsSortedAmount()) {
            currentTip.add(
                "  " + EnumChatFormatting.LIGHT_PURPLE
                    + aspect.getName()
                    + EnumChatFormatting.GRAY
                    + ": "
                    + EnumChatFormatting.GOLD
                    + essentia.getAmount(aspect));
        }
    }

    @Override
    public String[] getInfoData() {
        List<String> info = new ArrayList<>(Arrays.asList(super.getInfoData()));
        if (storedMana > 0) info.add(GTN_Utils.tr("GTN.Hatch.MagicCraftingInput.waila.mana", storedMana));
        for (Aspect aspect : storedEssentia.getAspects()) {
            info.add(aspect.getName() + ": " + storedEssentia.getAmount(aspect));
        }
        return info.toArray(new String[0]);
    }

    @Override
    public ITexture[] getTexturesActive(ITexture background) {
        return getTexturesInactive(background);
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture background) {
        return new ITexture[] { background, TextureFactory.of(OVERLAY_ME_CRAFTING_INPUT_BUFFER) };
    }

    @Override
    public String[] getDescription() {
        return new String[] { GTN_Utils.tr("GTN.Hatch.MagicCraftingInput.tooltip.00"),
            GTN_Utils.tr("GTN.Hatch.MagicCraftingInput.tooltip.01"),
            GTN_Utils.tr("GTN.Wildcard.tooltip.author", "§aCrazer"),
            GTN_Utils.tr("GTN.Hatch.common.addedBy", Constants.MOD_NAME) };
    }
}
