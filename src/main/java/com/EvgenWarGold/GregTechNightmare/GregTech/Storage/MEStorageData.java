package com.EvgenWarGold.GregTechNightmare.GregTech.Storage;

import static appeng.util.item.AEFluidStackType.FLUID_STACK_TYPE;
import static appeng.util.item.AEItemStackType.ITEM_STACK_TYPE;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_MEDataAccessHatch;

import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.api.storage.data.IItemList;

/** One disk record per storage identity, independent of chunks and player login state. */
public final class MEStorageData extends WorldSavedData {

    public final Contents items;
    public final Contents fluids;
    private BigInteger usedBytes = BigInteger.ZERO;
    public final Map<IStorageGrid, GTN_MEDataAccessHatch> connections = new HashMap<>();

    public MEStorageData(String name) {
        this(name, ITEM_STACK_TYPE, FLUID_STACK_TYPE);
    }

    MEStorageData(String name, IAEStackType<?> itemType, IAEStackType<?> fluidType) {
        super(name);
        items = new Contents(itemType);
        fluids = new Contents(fluidType);
    }

    public static MEStorageData get(UUID id) {
        String key = "gtn_me_storage_" + id;
        var world = DimensionManager.getWorld(0);
        var storage = world.mapStorage;
        MEStorageData data = (MEStorageData) storage.loadData(MEStorageData.class, key);
        if (data == null) {
            var file = world.getSaveHandler()
                .getMapFileFromName(key);
            if (file != null && file.exists())
                throw new IllegalStateException("Cannot load existing ME storage: " + key);
            data = new MEStorageData(key);
            storage.setData(key, data);
            data.markDirty();
        }
        return data;
    }

    public BigInteger getUsedBytes() {
        return usedBytes;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag.getInteger("version") != 1) throw new IllegalStateException("Unsupported ME storage format");
        usedBytes = BigInteger.ZERO;
        items.read(tag.getTagList("items", 10));
        fluids.read(tag.getTagList("fluids", 10));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("version", 1);
        tag.setTag("items", items.write());
        tag.setTag("fluids", fluids.write());
    }

    /** BigInteger quantities keep infinite mode lossless; each AE operation still uses a long. */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final class Contents {

        private final IAEStackType type;
        private final Map<IAEStack, BigInteger> amounts = new HashMap<>();
        private NBTTagList unavailable = new NBTTagList();

        private Contents(IAEStackType type) {
            this.type = type;
        }

        public int types() {
            return amounts.size();
        }

        public long count(IAEStack key) {
            return amounts.getOrDefault(key, BigInteger.ZERO)
                .min(BigInteger.valueOf(Long.MAX_VALUE))
                .longValue();
        }

        private BigInteger bytes(BigInteger amount) {
            BigInteger density = BigInteger.valueOf(type.getAmountPerByte());
            return amount.add(density)
                .subtract(BigInteger.ONE)
                .divide(density);
        }

        public long insertable(IAEStack key, long capacity, boolean infinite) {
            if (items.unavailable.tagCount() > 0 || fluids.unavailable.tagCount() > 0) return 0;
            if (infinite) return Long.MAX_VALUE;
            BigInteger old = amounts.getOrDefault(key, BigInteger.ZERO);
            BigInteger free = BigInteger.valueOf(capacity)
                .subtract(usedBytes)
                .add(bytes(old));
            return free.multiply(BigInteger.valueOf(type.getAmountPerByte()))
                .subtract(old)
                .max(BigInteger.ZERO)
                .min(BigInteger.valueOf(Long.MAX_VALUE))
                .longValue();
        }

        public void change(IAEStack key, long delta) {
            BigInteger old = amounts.getOrDefault(key, BigInteger.ZERO);
            BigInteger next = old.add(BigInteger.valueOf(delta));
            if (next.signum() < 0) throw new IllegalArgumentException("Negative ME storage quantity");
            usedBytes = usedBytes.subtract(bytes(old))
                .add(bytes(next));
            if (next.signum() == 0) {
                amounts.remove(key);
            } else {
                amounts.put(
                    (IAEStack) key.copy()
                        .setStackSize(1),
                    next);
            }
            markDirty();
        }

        public IItemList available(IItemList out) {
            amounts.forEach(
                (key, amount) -> out.add(
                    key.copy()
                        .setStackSize(
                            amount.min(BigInteger.valueOf(Long.MAX_VALUE))
                                .longValue())));
            return out;
        }

        private void read(NBTTagList entries) {
            amounts.clear();
            unavailable = new NBTTagList();
            for (int i = 0; i < entries.tagCount(); i++) {
                NBTTagCompound entry = entries.getCompoundTagAt(i);
                IAEStack key = type.loadStackFromNBT(entry.getCompoundTag("key"));
                BigInteger amount = new BigInteger(entry.getString("amount"));
                if (amount.signum() <= 0) throw new IllegalStateException("Invalid saved ME storage quantity");
                if (key == null) {
                    // Preserve temporarily missing mod items/fluids across subsequent saves.
                    unavailable.appendTag(entry.copy());
                    continue;
                }
                key.setStackSize(1);
                amounts.merge(key, amount, BigInteger::add);
            }
            for (BigInteger amount : amounts.values()) usedBytes = usedBytes.add(bytes(amount));
        }

        private NBTTagList write() {
            NBTTagList result = (NBTTagList) unavailable.copy();
            amounts.forEach((key, amount) -> {
                NBTTagCompound entry = new NBTTagCompound();
                NBTTagCompound stack = new NBTTagCompound();
                key.writeToNBT(stack);
                entry.setTag("key", stack);
                entry.setString("amount", amount.toString());
                result.appendTag(entry);
            });
            return result;
        }
    }
}
