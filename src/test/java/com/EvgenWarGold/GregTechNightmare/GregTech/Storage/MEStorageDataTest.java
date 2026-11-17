package com.EvgenWarGold.GregTechNightmare.GregTech.Storage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.nbt.NBTTagCompound;

import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;

/** Runs without a Minecraft client. The ledger and NBT code are real; AE item registration is replaced by fixtures. */
public final class MEStorageDataTest {

    public static void main(String[] args) {
        MEStorageData data = empty();
        IAEStack<?> iron = stack("item:iron");
        IAEStack<?> water = stack("fluid:water");
        equal(8, data.items.insertable(iron, 1, false), "eight items per byte");
        data.items.change(iron, 7);
        equal(1, data.items.insertable(iron, 1, false), "partial byte can be filled");
        equal(0, data.fluids.insertable(water, 1, false), "shared capacity excludes fluids when full");
        data.fluids.change(water, 1000);
        equal(
            2,
            data.getUsedBytes()
                .longValueExact(),
            "item and fluid usage combined");
        equal(0, data.items.insertable(iron, 1, false), "capacity reduction is extract only");
        data.fluids.change(water, -1000);
        equal(0, data.fluids.types(), "empty types removed");
        equal(1, data.items.insertable(iron, 1, false), "capacity restored after extraction");

        for (int i = 0; i < 1000; i++) data.items.change(stack("item:variant:" + i), 1);
        equal(1001, data.items.types(), "no 63 type limit");
        NBTTagCompound before = save(data);
        data.items.insertable(iron, Long.MAX_VALUE, false);
        data.items.count(iron);
        check(before.equals(save(data)), "queries must not mutate storage");

        data.items.change(iron, Long.MAX_VALUE);
        equal(Long.MAX_VALUE, data.items.count(iron), "AE visible count saturates");
        equal(0, data.items.insertable(iron, 1, false), "removing infinite upgrade does not permit insertion");
        equal(Long.MAX_VALUE, data.items.insertable(iron, 1, true), "infinite mode has no byte limit");
        MEStorageData restored = empty();
        restored.readFromNBT(save(data));
        check(
            entries(save(data)).equals(entries(save(restored))),
            "NBT round trip preserves all keys and unbounded counts");
        check(
            data.getUsedBytes()
                .equals(restored.getUsedBytes()),
            "usage rebuilt on load");
        restored.items.change(stack("item:iron"), -Long.MAX_VALUE);
        equal(7, restored.items.count(iron), "amount beyond Long.MAX_VALUE survives restart and extraction");
        restored.items.change(iron, -7);
        equal(0, restored.items.count(iron), "full extraction");
        equal(1000, restored.items.types(), "removes only extracted type");
        check(
            restored.getUsedBytes()
                .equals(BigInteger.valueOf(1000)),
            "exact byte accounting after extraction");
        restored.readFromNBT(save(data));
        restored.readFromNBT(save(data));
        check(
            restored.getUsedBytes()
                .equals(data.getUsedBytes()),
            "reload does not accumulate usage");

        MEStorageData missing = empty();
        missing.items.change(stack("missing:temporarily_disabled_mod"), 5);
        NBTTagCompound missingTag = save(missing);
        MEStorageData missingReloaded = empty();
        missingReloaded.readFromNBT(missingTag);
        check(save(missingReloaded).equals(missingTag), "unknown item entries preserved");
        equal(0, missingReloaded.items.insertable(iron, Long.MAX_VALUE, true), "unknown entries block new writes");
        System.out
            .println("ME storage tests passed: shared capacity, >63 types, overflow, downgrade, NBT, missing items.");
    }

    private static MEStorageData empty() {
        return new MEStorageData("test", type(8), type(1000));
    }

    private static NBTTagCompound save(MEStorageData data) {
        NBTTagCompound tag = new NBTTagCompound();
        data.writeToNBT(tag);
        return tag;
    }

    private static Map<String, String> entries(NBTTagCompound tag) {
        Map<String, String> entries = new TreeMap<>();
        for (String channel : new String[] { "items", "fluids" }) {
            var list = tag.getTagList(channel, 10);
            for (int i = 0; i < list.tagCount(); i++) {
                var entry = list.getCompoundTagAt(i);
                entries.put(
                    channel + entry.getCompoundTag("key")
                        .getString("fixtureKey"),
                    entry.getString("amount"));
            }
        }
        return entries;
    }

    private static void equal(long expected, long actual, String message) {
        check(expected == actual, message + ": expected " + expected + ", got " + actual);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static IAEStackType<?> type(int density) {
        return (IAEStackType<?>) Proxy.newProxyInstance(
            MEStorageDataTest.class.getClassLoader(),
            new Class<?>[] { IAEStackType.class },
            (proxy, method, args) -> {
                if (method.getName()
                    .equals("getAmountPerByte")) return density;
                if (method.getName()
                    .equals("loadStackFromNBT")) {
                    String key = ((NBTTagCompound) args[0]).getString("fixtureKey");
                    return key.startsWith("missing:") ? null : stack(key);
                }
                throw new UnsupportedOperationException(method.getName());
            });
    }

    private static IAEStack<?> stack(String key) {
        return (IAEStack<?>) Proxy.newProxyInstance(
            MEStorageDataTest.class.getClassLoader(),
            new Class<?>[] { IAEStack.class },
            new StackFixture(key));
    }

    private static final class StackFixture implements InvocationHandler {

        private final String key;
        private long count = 1;

        private StackFixture(String key) {
            this.key = key;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "copy" -> stack(key).setStackSize(count);
                case "setStackSize" -> {
                    count = (Long) args[0];
                    yield proxy;
                }
                case "getStackSize" -> count;
                case "hashCode" -> key.hashCode();
                case "equals" -> args[0] != null && Proxy.isProxyClass(args[0].getClass())
                    && Proxy.getInvocationHandler(args[0]) instanceof StackFixture other
                    && key.equals(other.key);
                case "writeToNBT" -> {
                    NBTTagCompound tag = (NBTTagCompound) args[0];
                    tag.setString("fixtureKey", key);
                    yield null;
                }
                case "toString" -> key;
                default -> throw new UnsupportedOperationException(method.getName());
            };
        }
    }
}
