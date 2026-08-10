package com.EvgenWarGold.GregTechNightmare.GregTech.Hatch;

import java.util.EnumSet;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;
import com.EvgenWarGold.GregTechNightmare.GregTech.Wireless.GTN_WirelessMEDataStick;
import com.EvgenWarGold.GregTechNightmare.GregTech.Wireless.GTN_WirelessMENetworkRegistry;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.render.TextureFactory;

public class GTN_WirelessMEIOHub extends MTEHatch implements IActionHost, IGridProxyable {

    private static final String TAG_NETWORK_ID = "GTNWirelessNetworkId";
    private static final String TAG_NETWORK_NAME = "GTNWirelessNetworkName";

    private AENetworkProxy networkProxy;
    private UUID networkId;
    private String networkName = "";

    public GTN_WirelessMEIOHub(int id, String name) {
        super(id, name, name, 4, 0, "");
    }

    public GTN_WirelessMEIOHub(String name, int tier, String[] description, ITexture[][][] textures) {
        super(name, tier, 0, description, textures);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new GTN_WirelessMEIOHub(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public String[] getDescription() {
        return new String[] { "Wireless access point for GregTech ME hatches", "Connect this hub to an ME cable",
            "Copy the link to a Data Stick, then use it on an existing GT ME Input/Output Bus",
            "Each linked wireless device consumes one ME channel",
            "Linked devices can extend the remote ME network through their normal cable sides",
            "The wireless branch supports up to 32 channels when the hub path has enough capacity",
            "Added by: " + Constants.MOD_NAME };
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return true;
    }

    @Override
    public boolean isValidSlot(int index) {
        return false;
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity tileEntity, int index, ForgeDirection side, ItemStack stack) {
        return false;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity tileEntity, int index, ForgeDirection side, ItemStack stack) {
        return false;
    }

    @Override
    public ITexture[] getTexturesActive(ITexture baseTexture) {
        return new ITexture[] { baseTexture, TextureFactory.of(Textures.BlockIcons.OVERLAY_ME_HATCH_ACTIVE) };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture baseTexture) {
        return new ITexture[] { baseTexture, TextureFactory.of(Textures.BlockIcons.OVERLAY_ME_HATCH) };
    }

    @Override
    public void onFirstTick(IGregTechTileEntity tileEntity) {
        super.onFirstTick(tileEntity);
        if (!tileEntity.isServerSide()) return;

        ensureNetworkIdentity();
        AENetworkProxy proxy = getProxy();
        if (!proxy.isReady()) proxy.onReady();
        GTN_WirelessMENetworkRegistry.register(this);
    }

    @Override
    public void onPostTick(IGregTechTileEntity tileEntity, long tick) {
        super.onPostTick(tileEntity, tick);
        if (!tileEntity.isServerSide()) return;

        ensureNetworkIdentity();
        AENetworkProxy proxy = getProxy();
        if (!proxy.isReady()) proxy.onReady();

        if (tick % 20 == 0) {
            GTN_WirelessMENetworkRegistry.register(this);
            tileEntity.setActive(proxy.isActive());
        }
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity tileEntity, EntityPlayer player, ForgeDirection side, float x,
        float y, float z) {
        if (tileEntity.isServerSide()) {
            ensureNetworkIdentity();
            ItemStack held = player.inventory.getCurrentItem();
            if (GTN_WirelessMEDataStick.isDataStick(held)) {
                GTN_WirelessMEDataStick.write(held, this);
                player.addChatMessage(new ChatComponentText("Wireless ME link saved: " + getNetworkName()));
                return true;
            }

            player.addChatMessage(
                new ChatComponentText(
                    "Wireless ME: " + getNetworkName() + " - " + (getProxy().isActive() ? "Online" : "Offline")));
            player.addChatMessage(
                new ChatComponentText("Use a Data Stick on this hub, then on an existing GT ME Input/Output Bus."));
        }
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound tag) {
        super.saveNBTData(tag);
        ensureNetworkIdentity();
        tag.setString(TAG_NETWORK_ID, networkId.toString());
        tag.setString(TAG_NETWORK_NAME, networkName);
        if (networkProxy != null) networkProxy.writeToNBT(tag);
    }

    @Override
    public void loadNBTData(NBTTagCompound tag) {
        super.loadNBTData(tag);
        if (tag.hasKey(TAG_NETWORK_ID)) {
            try {
                networkId = UUID.fromString(tag.getString(TAG_NETWORK_ID));
            } catch (IllegalArgumentException ignored) {
                networkId = null;
            }
        }
        networkName = tag.getString(TAG_NETWORK_NAME);
        ensureNetworkIdentity();
        if (tag.hasKey("proxy")) getProxy().readFromNBT(tag);
    }

    @Override
    public void inValidate() {
        unregisterNetwork();
        if (networkProxy != null) networkProxy.onChunkUnload();
        super.inValidate();
    }

    @Override
    public void onRemoval() {
        unregisterNetwork();
        if (networkProxy != null) networkProxy.invalidate();
        super.onRemoval();
    }

    @Override
    public AENetworkProxy getProxy() {
        if (networkProxy == null) {
            networkProxy = new AENetworkProxy(this, "proxy", GTN_ItemList.WirelessMEIOHub.get(1), true);
            networkProxy.setFlags(GridFlags.DENSE_CAPACITY);
            networkProxy.setIdlePowerUsage(1.0);
            networkProxy.setValidSides(EnumSet.complementOf(EnumSet.of(ForgeDirection.UNKNOWN)));
        }
        return networkProxy;
    }

    @Override
    public IGridNode getActionableNode() {
        return getProxy().getNode();
    }

    @Override
    public IGridNode getGridNode(ForgeDirection direction) {
        return getProxy().getNode();
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection direction) {
        return AECableType.SMART;
    }

    @Override
    public void securityBreak() {}

    @Override
    public DimensionalCoord getLocation() {
        IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity == null) return new DimensionalCoord(0, 0, 0, 0);
        return new DimensionalCoord(
            tileEntity.getWorld(),
            tileEntity.getXCoord(),
            tileEntity.getYCoord(),
            tileEntity.getZCoord());
    }

    @Override
    public void gridChanged() {
        IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null && tileEntity.isServerSide()) {
            tileEntity.setActive(getProxy().isActive());
            GTN_WirelessMENetworkRegistry.register(this);
            tileEntity.markDirty();
        }
    }

    public UUID getNetworkId() {
        return networkId;
    }

    public String getNetworkName() {
        ensureNetworkIdentity();
        return networkName;
    }

    private void ensureNetworkIdentity() {
        if (networkId == null) networkId = UUID.randomUUID();
        if (networkName == null || networkName.isEmpty()) {
            networkName = "Wireless ME " + networkId.toString().substring(0, 8);
        }
    }

    private void unregisterNetwork() {
        IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null && tileEntity.isServerSide()) {
            GTN_WirelessMENetworkRegistry.unregister(this);
        }
    }
}
