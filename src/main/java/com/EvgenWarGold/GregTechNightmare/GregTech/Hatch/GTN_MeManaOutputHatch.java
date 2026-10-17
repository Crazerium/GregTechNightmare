package com.EvgenWarGold.GregTechNightmare.GregTech.Hatch;

import static com.EvgenWarGold.GregTechNightmare.GregTech.Mana.AEManaStackType.MANA_STACK_TYPE;
import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.EvgenWarGold.GregTechNightmare.GregTech.Mana.AEManaStack;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;

import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.util.AECableType;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.render.TextureFactory;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

/**
 * Sends the mana made by a machine into the ME network. The mana is kept in a buffer first and moved into the network
 * once per second; while the buffer is full, the machine does not start new recipes.
 */
@IMetaTileEntity.SkipGenerateDescription
public class GTN_MeManaOutputHatch extends MTEHatch {

    /** Buffer kept for a network that cannot take the mana right now. */
    public static final long BUFFER_SIZE = 10_000_000;
    private static final String TAG_BUFFER = "gtnManaBuffer";
    private static final String TAG_PROXY = "networkProxy";

    private AENetworkProxy proxy;
    private MachineSource source;
    private long buffer;

    public GTN_MeManaOutputHatch(int id, String name) {
        super(id, name, name, 3, 0, "");
    }

    public GTN_MeManaOutputHatch(String name, int tier, String[] description, ITexture[][][] textures) {
        super(name, tier, 0, description, textures);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tile) {
        return new GTN_MeManaOutputHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public boolean isValidSlot(int index) {
        return false;
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return true;
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity base, int index, ForgeDirection side, ItemStack stack) {
        return false;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity base, int index, ForgeDirection side, ItemStack stack) {
        return false;
    }

    // region Mana
    public long getBufferedMana() {
        return buffer;
    }

    public long getFreeSpace() {
        return BUFFER_SIZE - buffer;
    }

    /** Adds up to {@code amount} mana to the buffer, returns how much was added. */
    public long addMana(long amount) {
        long added = Math.max(0, Math.min(amount, getFreeSpace()));
        if (added <= 0) return 0;
        buffer += added;
        markDirtyBase();
        return added;
    }

    private void flush() {
        if (buffer <= 0 || proxy == null || !proxy.isActive()) return;
        try {
            @SuppressWarnings("unchecked")
            IMEMonitor<AEManaStack> monitor = (IMEMonitor<AEManaStack>) proxy.getStorage()
                .getMEMonitor(MANA_STACK_TYPE);
            AEManaStack left = monitor.injectItems(new AEManaStack(buffer), Actionable.MODULATE, getSource());
            long stored = buffer - (left == null ? 0 : left.getStackSize());
            if (stored <= 0) return;
            buffer -= stored;
            markDirtyBase();
        } catch (GridAccessException ignored) {}
    }
    // endregion

    // region AE2
    private MachineSource getSource() {
        if (source == null) source = new MachineSource((IActionHost) getBaseMetaTileEntity());
        return source;
    }

    @Override
    public AENetworkProxy getProxy() {
        if (proxy == null) {
            IGregTechTileEntity base = getBaseMetaTileEntity();
            if (base == null) return null;
            proxy = new AENetworkProxy((IGridProxyable) base, "proxy", getStackForm(1), true);
            proxy.setFlags(GridFlags.REQUIRE_CHANNEL);
            proxy.setIdlePowerUsage(1.0);
            proxy.setValidSides(EnumSet.of(base.getFrontFacing()));
        }
        return proxy;
    }

    @Override
    public void onFacingChange() {
        super.onFacingChange();
        AENetworkProxy networkProxy = getProxy();
        if (networkProxy != null) networkProxy.setValidSides(EnumSet.of(getBaseMetaTileEntity().getFrontFacing()));
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection dir) {
        return dir == getBaseMetaTileEntity().getFrontFacing() ? AECableType.SMART : AECableType.NONE;
    }
    // endregion

    @Override
    public void onFirstTick(IGregTechTileEntity base) {
        super.onFirstTick(base);
        AENetworkProxy networkProxy = getProxy();
        if (networkProxy != null) networkProxy.onReady();
    }

    @Override
    public void onPostTick(IGregTechTileEntity base, long tick) {
        super.onPostTick(base, tick);
        if (base.isServerSide() && tick % 20 == 0) flush();
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        if (proxy != null) proxy.invalidate();
    }

    private void markDirtyBase() {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base != null) base.markDirty();
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        nbt.setLong(TAG_BUFFER, buffer);
        if (proxy != null) {
            NBTTagCompound proxyTag = new NBTTagCompound();
            proxy.writeToNBT(proxyTag);
            nbt.setTag(TAG_PROXY, proxyTag);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        buffer = nbt.getLong(TAG_BUFFER);
        AENetworkProxy networkProxy = getProxy();
        if (networkProxy != null && nbt.hasKey(TAG_PROXY)) networkProxy.readFromNBT(nbt.getCompoundTag(TAG_PROXY));
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setLong(TAG_BUFFER, buffer);
        tag.setBoolean("online", proxy != null && proxy.isActive());
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> info, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, info, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        info.add(
            GTN_Utils.tr(
                tag.getBoolean("online") ? "GTN.Hatch.MeAspectHatch.waila.online"
                    : "GTN.Hatch.MeAspectHatch.waila.offline"));
        info.add(GTN_Utils.tr("GTN.Hatch.MeManaOutputHatch.waila.buffer", formatNumber(tag.getLong(TAG_BUFFER))));
    }

    @Override
    public ITexture[] getTexturesActive(ITexture background) {
        return new ITexture[] { background, TextureFactory.of(Textures.BlockIcons.OVERLAY_ME_HATCH_ACTIVE) };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture background) {
        return new ITexture[] { background, TextureFactory.of(Textures.BlockIcons.OVERLAY_ME_HATCH) };
    }

    @Override
    public String[] getDescription() {
        return new String[] { GTN_Utils.tr("GTN.Hatch.MeManaOutputHatch.tooltip.00"),
            GTN_Utils.tr("GTN.Hatch.MeManaOutputHatch.tooltip.01", formatNumber(BUFFER_SIZE)),
            GTN_Utils.tr("GTN.Wildcard.tooltip.author", "§aCrazer"),
            GTN_Utils.tr("GTN.Hatch.common.addedBy", Constants.MOD_NAME) };
    }
}
