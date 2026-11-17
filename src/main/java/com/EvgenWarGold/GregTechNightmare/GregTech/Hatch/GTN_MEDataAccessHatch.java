package com.EvgenWarGold.GregTechNightmare.GregTech.Hatch;

import static appeng.util.item.AEFluidStackType.FLUID_STACK_TYPE;
import static appeng.util.item.AEItemStackType.ITEM_STACK_TYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.EvgenWarGold.GregTechNightmare.GregTech.Storage.GTN_MEStorage;
import com.EvgenWarGold.GregTechNightmare.GregTech.Storage.MEStorageData;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;

import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.ICellProvider;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.api.storage.data.IItemList;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.me.storage.MEInventoryHandler;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GTUtility;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

/** Uses the existing ME hatch connection lifecycle, exposing world-backed inventories instead of a buffer. */
public class GTN_MEDataAccessHatch extends GTN_MeManaOutputHatch implements ICellProvider {

    private GTN_MEStorage controller;
    private MEStorageData data;
    private IStorageGrid mountedGrid;
    private IMEInventoryHandler<?> items;
    private IMEInventoryHandler<?> fluids;
    private int priority;
    private AENetworkProxy storageProxy;
    private IGrid mountedNetwork;

    @Override
    public AENetworkProxy getProxy() {
        if (storageProxy == null && getBaseMetaTileEntity() != null) {
            storageProxy = new AENetworkProxy(
                (IGridProxyable) getBaseMetaTileEntity(),
                "storage",
                getStackForm(1),
                true) {

                @Override
                public void invalidate() {
                    detach();
                    super.invalidate();
                }
            };
            storageProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
            storageProxy.setIdlePowerUsage(32);
            storageProxy.setValidSides(EnumSet.of(getBaseMetaTileEntity().getFrontFacing()));
        }
        return storageProxy;
    }

    public GTN_MEDataAccessHatch(int id, String name) {
        super(id, name);
    }

    private GTN_MEDataAccessHatch(String name, int tier, String[] description, ITexture[][][] textures) {
        super(name, tier, description, textures);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tile) {
        return new GTN_MEDataAccessHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    public boolean bind(GTN_MEStorage machine) {
        if (controller != null && controller != machine && controller.storageOnline()) return false;
        if (controller != machine) detach();
        controller = machine;
        return true;
    }

    public void unbind(GTN_MEStorage machine) {
        if (controller != machine) return;
        detach();
        controller = null;
    }

    public boolean available() {
        return controller != null && controller.storageOnline()
            && getBaseMetaTileEntity() != null
            && !getBaseMetaTileEntity().isDead()
            && getProxy() != null
            && getProxy().isActive();
    }

    public void detach() {
        if (mountedGrid != null) {
            // Keep the old inventories available while AE subtracts their contents from its monitor.
            mountedGrid.unregisterCellProvider(this);
            data.connections.remove(mountedGrid, this);
            refreshNetwork();
        }
        mountedGrid = null;
        mountedNetwork = null;
        data = null;
        items = null;
        fluids = null;
    }

    @Override
    public void onPostTick(IGregTechTileEntity base, long tick) {
        super.onPostTick(base, tick);
        if (!base.isServerSide()) return;
        if (!available()) {
            detach();
            return;
        }
        try {
            IStorageGrid grid = getProxy().getStorage();
            MEStorageData next = controller.storageData();
            if (next == null) {
                detach();
                return;
            }
            if (grid == mountedGrid && next == data) return;
            detach();
            GTN_MEDataAccessHatch existing = next.connections.get(grid);
            if (existing != null) {
                if (existing.available()) return;
                existing.detach();
            }
            data = next;
            mountedGrid = grid;
            mountedNetwork = getProxy().getGrid();
            items = handler(ITEM_STACK_TYPE, data.items);
            fluids = handler(FLUID_STACK_TYPE, data.fluids);
            data.connections.put(grid, this);
            grid.registerCellProvider(this);
            refreshNetwork();
        } catch (GridAccessException ignored) {
            detach();
        }
    }

    private void refreshNetwork() {
        if (mountedNetwork != null) mountedNetwork.postEvent(new MENetworkCellArrayUpdate());
    }

    private <T extends IAEStack<T>> MEInventoryHandler<T> handler(IAEStackType<T> type,
        MEStorageData.Contents contents) {
        MEInventoryHandler<T> result = new MEInventoryHandler<>(new Inventory<>(type, contents), type);
        result.setPriority(priority);
        return result;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List<IMEInventoryHandler> getCellArray(IAEStackType<?> type) {
        IMEInventoryHandler<?> inventory = type == ITEM_STACK_TYPE ? items : type == FLUID_STACK_TYPE ? fluids : null;
        return inventory == null ? Collections.emptyList() : Collections.singletonList(inventory);
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public boolean connected() {
        return mountedGrid != null && available();
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        tag.setBoolean("storageOnline", connected());
        tag.setInteger("storagePriority", priority);
    }

    @Override
    public void getWailaBody(ItemStack stack, List<String> info, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        NBTTagCompound tag = accessor.getNBTData();
        info.add(
            GTN_Utils.tr(tag.getBoolean("storageOnline") ? "GTN.MEStorage.connected" : "GTN.MEStorage.disconnected"));
        info.add(GTN_Utils.tr("GTN.MEStorage.priority", tag.getInteger("storagePriority")));
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer player, float x, float y, float z,
        ItemStack tool) {
        if (!getBaseMetaTileEntity().isServerSide()) return;
        detach();
        priority = Math.max(-1000000, Math.min(1000000, priority + (player.isSneaking() ? -100 : 100)));
        getBaseMetaTileEntity().markDirty();
        GTUtility.sendChatToPlayer(player, GTN_Utils.tr("GTN.MEStorage.priority", priority));
    }

    @Override
    public void onRemoval() {
        detach();
        if (storageProxy != null) storageProxy.invalidate();
        super.onRemoval();
    }

    @Override
    public void saveNBTData(NBTTagCompound tag) {
        super.saveNBTData(tag);
        tag.setInteger("storagePriority", priority);
        if (storageProxy != null) {
            NBTTagCompound proxyTag = new NBTTagCompound();
            storageProxy.writeToNBT(proxyTag);
            tag.setTag("storageProxy", proxyTag);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound tag) {
        super.loadNBTData(tag);
        priority = tag.getInteger("storagePriority");
        if (tag.hasKey("storageProxy") && getProxy() != null)
            getProxy().readFromNBT(tag.getCompoundTag("storageProxy"));
    }

    @Override
    public String[] getDescription() {
        return new String[] { GTN_Utils.tr("GTN.MEStorage.hatch.0"), GTN_Utils.tr("GTN.MEStorage.hatch.1") };
    }

    private final class Inventory<T extends IAEStack<T>> implements IMEInventory<T> {

        private final IAEStackType<T> type;
        private final MEStorageData.Contents contents;

        private Inventory(IAEStackType<T> type, MEStorageData.Contents contents) {
            this.type = type;
            this.contents = contents;
        }

        @Override
        public T injectItems(T input, Actionable mode, BaseActionSource source) {
            if (input == null || input.getStackSize() <= 0 || !available()) return input;
            long count = Math
                .min(input.getStackSize(), contents.insertable(input, controller.capacity(), controller.infinite()));
            if (count > 0 && mode == Actionable.MODULATE) change(input, count, source);
            return count == input.getStackSize() ? null
                : input.copy()
                    .setStackSize(input.getStackSize() - count);
        }

        @Override
        public T extractItems(T request, Actionable mode, BaseActionSource source) {
            if (request == null || request.getStackSize() <= 0 || !available()) return null;
            long count = Math.min(request.getStackSize(), contents.count(request));
            if (count <= 0) return null;
            if (mode == Actionable.MODULATE) change(request, -count, source);
            return request.copy()
                .setStackSize(count);
        }

        private void change(T stack, long delta, BaseActionSource source) {
            contents.change(stack, delta);
            // The initiating network accounts for its own operation. Notify only the other networks.
            for (IStorageGrid other : new ArrayList<>(data.connections.keySet())) {
                if (other != mountedGrid) other.postAlterationOfStoredItems(
                    type,
                    Collections.singletonList(
                        stack.copy()
                            .setStackSize(delta)),
                    source);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public IItemList<T> getAvailableItems(IItemList<T> out, int iteration) {
            return contents.available(out);
        }

        @Override
        public T getAvailableItem(T request, int iteration) {
            long count = contents.count(request);
            return count == 0 ? null
                : request.copy()
                    .setStackSize(count);
        }

        @Override
        public IAEStackType<T> getStackType() {
            return type;
        }
    }
}
