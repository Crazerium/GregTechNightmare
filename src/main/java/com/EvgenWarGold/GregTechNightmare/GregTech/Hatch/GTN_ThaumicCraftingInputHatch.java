package com.EvgenWarGold.GregTechNightmare.GregTech.Hatch;

import static appeng.util.item.AEFluidStackType.FLUID_STACK_TYPE;
import static appeng.util.item.AEItemStackType.ITEM_STACK_TYPE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_ME_CRAFTING_INPUT_BUFFER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

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
 * Crafting Input Buffer for Thaumcraft machines.
 * <p>
 * Essentia written in a pattern (Thaumic Energistics essentia stacks) is pushed by AE2 together with the items and kept
 * in an internal buffer of the hatch; the machine takes the recipe essentia from this buffer first and from the
 * connected ME network after that.
 */
@IMetaTileEntity.SkipGenerateDescription
public final class GTN_ThaumicCraftingInputHatch extends MTEHatchCraftingInputME {

    private static final String NBT_STORED_ESSENTIA = "gtnStoredEssentia";

    private final AspectList storedEssentia = new AspectList();

    private MachineSource essentiaSource;

    public GTN_ThaumicCraftingInputHatch(int id, String name) {
        super(id, name, name, true);
    }

    public GTN_ThaumicCraftingInputHatch(String name, int tier, String[] description, ITexture[][][] textures) {
        super(name, tier, description, textures, true);
    }

    @Override
    public GTN_ThaumicCraftingInputHatch newMetaEntity(IGregTechTileEntity tile) {
        return new GTN_ThaumicCraftingInputHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    // region Essentia
    /** Essentia storage of the ME network this hatch is connected to, null if offline. */
    @SuppressWarnings("unchecked")
    public IMEMonitor<AEEssentiaStack> getEssentiaMonitor() {
        AENetworkProxy proxy = getProxy();
        if (proxy == null || !proxy.isActive()) return null;
        try {
            return (IMEMonitor<AEEssentiaStack>) proxy.getStorage()
                .getMEMonitor(AEEssentiaStackType.ESSENTIA_STACK_TYPE);
        } catch (GridAccessException e) {
            return null;
        }
    }

    public static long getEssentiaAmount(IMEMonitor<AEEssentiaStack> monitor, Aspect aspect) {
        if (monitor == null || aspect == null) return 0;
        AEEssentiaStack stack = monitor.getAvailableItem(new AEEssentiaStack(aspect), IterationCounter.fetchNewId());
        return stack != null ? stack.getStackSize() : 0;
    }

    public long extractEssentia(IMEMonitor<AEEssentiaStack> monitor, Aspect aspect, long amount) {
        if (monitor == null || aspect == null || amount <= 0) return 0;
        AEEssentiaStack extracted = monitor
            .extractItems(new AEEssentiaStack(aspect, amount), Actionable.MODULATE, getEssentiaSource());
        return extracted != null ? extracted.getStackSize() : 0;
    }

    private MachineSource getEssentiaSource() {
        if (essentiaSource == null) essentiaSource = new MachineSource((IActionHost) getBaseMetaTileEntity());
        return essentiaSource;
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

    private void markDirtyBase() {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base != null) base.markDirty();
    }
    // endregion

    // region AE2
    @Override
    public IAEStackType<?>[] getSupportedStackTypes() {
        return new IAEStackType<?>[] { ITEM_STACK_TYPE, FLUID_STACK_TYPE, AEEssentiaStackType.ESSENTIA_STACK_TYPE };
    }

    /**
     * GT only knows items and fluids: essentia stacks are taken out of the crafting table into the buffer, the rest of
     * the pattern is pushed by the regular Crafting Input Buffer logic.
     */
    @Override
    public boolean pushPattern(ICraftingPatternDetails details, InventoryCrafting table) {
        if (!(table instanceof MEInventoryCrafting crafting)) return super.pushPattern(details, table);

        IAEStack<?>[] removed = new IAEStack<?>[crafting.getSizeInventory()];
        List<AEEssentiaStack> essentia = new ArrayList<>();
        for (int i = 0; i < removed.length; i++) {
            if (crafting.getAEStackInSlot(i) instanceof AEEssentiaStack stack) {
                removed[i] = stack;
                essentia.add(stack);
                crafting.setInventorySlotContents(i, (IAEStack<?>) null);
            }
        }
        if (essentia.isEmpty()) return super.pushPattern(details, table);

        boolean pushed = super.pushPattern(details, table);
        if (pushed) {
            for (AEEssentiaStack stack : essentia) {
                storedEssentia.add(stack.getAspect(), (int) Math.min(Integer.MAX_VALUE, stack.getStackSize()));
            }
            markDirtyBase();
        } else {
            for (int i = 0; i < removed.length; i++) {
                if (removed[i] != null) crafting.setInventorySlotContents(i, removed[i]);
            }
        }
        return pushed;
    }

    /** Also returns the buffered essentia to the ME network. */
    @Override
    public void refundAll(boolean shouldDrop) {
        super.refundAll(shouldDrop);
        IMEMonitor<AEEssentiaStack> monitor = getEssentiaMonitor();
        if (monitor == null || storedEssentia.size() == 0) return;
        for (Aspect aspect : storedEssentia.getAspects()) {
            int amount = storedEssentia.getAmount(aspect);
            AEEssentiaStack left = monitor
                .injectItems(new AEEssentiaStack(aspect, amount), Actionable.MODULATE, getEssentiaSource());
            takeStoredEssentia(aspect, amount - (left == null ? 0 : (int) left.getStackSize()));
        }
    }
    // endregion

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        storedEssentia.writeToNBT(nbt, NBT_STORED_ESSENTIA);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        storedEssentia.readFromNBT(nbt, NBT_STORED_ESSENTIA);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        storedEssentia.writeToNBT(tag, NBT_STORED_ESSENTIA);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        AspectList essentia = new AspectList();
        essentia.readFromNBT(accessor.getNBTData(), NBT_STORED_ESSENTIA);
        if (essentia.size() == 0) {
            currentTip.add(GTN_Utils.tr("GTN.Hatch.ThaumicCraftingInput.waila.empty"));
            return;
        }
        currentTip.add(GTN_Utils.tr("GTN.Hatch.ThaumicCraftingInput.waila.essentia"));
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
        return new String[] { GTN_Utils.tr("GTN.Hatch.ThaumicCraftingInput.tooltip.00"),
            GTN_Utils.tr("GTN.Hatch.ThaumicCraftingInput.tooltip.01"),
            GTN_Utils.tr("GTN.Wildcard.tooltip.author", "§aCrazer"),
            GTN_Utils.tr("GTN.Hatch.common.addedBy", Constants.MOD_NAME) };
    }
}
