package com.EvgenWarGold.GregTechNightmare.GregTech.Mana;

import static com.EvgenWarGold.GregTechNightmare.GregTech.Mana.AEManaStackType.MANA_STACK_TYPE;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.IMEMonitor;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.parts.AEBasePart;
import appeng.parts.automation.PartSharedItemBus;
import vazkii.botania.api.mana.IManaCollector;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.mana.spark.ISparkAttachable;

/**
 * Mana support for the AE2 Fluid Craft fluid buses (wired in by mixins): when a bus faces a Botania mana block, it
 * moves mana instead of fluids.
 * <ul>
 * <li>import bus: takes mana from a mana pool into the network;</li>
 * <li>export bus: sends mana from the network into a pool, a spreader or any receiver that reports free space;</li>
 * <li>storage bus: shows the mana of a pool as network storage (see {@link ManaExternalStorageHandler}).</li>
 * </ul>
 */
public final class GTN_ManaBuses {

    /** Mana moved per mB the fluid bus would move: 1000 mB per operation = 100 000 mana. */
    private static final int MANA_PER_MB = 100;
    /** AE power: 1 AE per 100 000 mana, same as 1 AE per 1000 mB for fluids. */
    private static final double MANA_PER_AE = 100_000;

    private GTN_ManaBuses() {}

    public static TileEntity getFacingTile(AEBasePart part) {
        // parts are asked for their stack type before they are placed
        if (part.getHost() == null || part.getSide() == null) return null;
        TileEntity self = part.getHost()
            .getTile();
        if (self == null || self.getWorldObj() == null) return null;
        ForgeDirection side = part.getSide();
        World world = self.getWorldObj();
        int x = self.xCoord + side.offsetX;
        int y = self.yCoord + side.offsetY;
        int z = self.zCoord + side.offsetZ;
        if (!world.blockExists(x, y, z)) return null;
        return world.getTileEntity(x, y, z);
    }

    /** A Botania pool that can be used as mana storage. The Fluix Mana Pool is the network itself, so it is skipped. */
    public static boolean isManaStorage(TileEntity tile) {
        return tile instanceof IManaPool && !(tile instanceof TileFluxManaPool);
    }

    /** Free space of a mana receiver, -1 if the receiver does not report it. */
    public static long getFreeSpace(TileEntity tile) {
        if (tile instanceof TileFluxManaPool) return -1;
        if (tile instanceof ISparkAttachable attachable) return attachable.getAvailableSpaceForMana();
        if (tile instanceof IManaCollector collector) {
            return Math.max(0, collector.getMaxMana() - collector.getCurrentMana());
        }
        return -1;
    }

    /**
     * True if the receiver wants mana right now. Crafting blocks (terrasteel plate, rune altar) report free space even
     * when idle and lose the mana, Botania itself only sends mana to them while they craft.
     */
    public static boolean isReadyForMana(TileEntity tile) {
        if (!(tile instanceof IManaReceiver receiver) || receiver.isFull() || !receiver.canRecieveManaFromBursts()) {
            return false;
        }
        return !(tile instanceof ISparkAttachable attachable) || !attachable.areIncomingTranfersDone();
    }

    /** True if the export bus has something to send mana to. */
    public static boolean isManaTarget(TileEntity tile) {
        return tile instanceof IManaReceiver && getFreeSpace(tile) >= 0;
    }

    /** @return the tick rate, or null if the bus does not face mana and should work with fluids. */
    public static TickRateModulation importMana(PartSharedItemBus<?> bus) {
        TileEntity target = getFacingTile(bus);
        if (!isManaStorage(target)) return null;
        IManaPool pool = (IManaPool) target;

        AENetworkProxy proxy = bus.getProxy();
        if (!proxy.isActive()) return TickRateModulation.IDLE;
        try {
            IMEMonitor<AEManaStack> monitor = getMonitor(proxy);
            long amount = Math.min(getAmountPerOperation(bus, proxy), pool.getCurrentMana());
            if (amount <= 0) return TickRateModulation.SLOWER;

            AEManaStack notStored = monitor
                .injectItems(new AEManaStack(amount), Actionable.MODULATE, new MachineSource((IActionHost) bus));
            long moved = amount - (notStored == null ? 0 : notStored.getStackSize());
            if (moved <= 0) return TickRateModulation.SLOWER;

            pool.recieveMana((int) -moved);
            usePower(proxy, moved);
            return TickRateModulation.FASTER;
        } catch (GridAccessException e) {
            return TickRateModulation.IDLE;
        }
    }

    /** @return the tick rate, or null if the bus does not face mana and should work with fluids. */
    public static TickRateModulation exportMana(PartSharedItemBus<?> bus) {
        TileEntity target = getFacingTile(bus);
        if (!isManaTarget(target)) return null;
        IManaReceiver receiver = (IManaReceiver) target;

        AENetworkProxy proxy = bus.getProxy();
        if (!proxy.isActive()) return TickRateModulation.IDLE;
        try {
            IMEMonitor<AEManaStack> monitor = getMonitor(proxy);
            long amount = Math.min(getAmountPerOperation(bus, proxy), getFreeSpace(target));
            if (amount <= 0 || !isReadyForMana(target)) return TickRateModulation.SLOWER;

            AEManaStack extracted = monitor
                .extractItems(new AEManaStack(amount), Actionable.MODULATE, new MachineSource((IActionHost) bus));
            if (extracted == null || extracted.getStackSize() <= 0) return TickRateModulation.SLOWER;

            receiver.recieveMana((int) extracted.getStackSize());
            // blocks like the terrasteel plate show their mana (particles) only after a client update
            target.getWorldObj()
                .markBlockForUpdate(target.xCoord, target.yCoord, target.zCoord);
            usePower(proxy, extracted.getStackSize());
            return TickRateModulation.FASTER;
        } catch (GridAccessException e) {
            return TickRateModulation.IDLE;
        }
    }

    @SuppressWarnings("unchecked")
    private static IMEMonitor<AEManaStack> getMonitor(AENetworkProxy proxy) throws GridAccessException {
        return (IMEMonitor<AEManaStack>) proxy.getStorage()
            .getMEMonitor(MANA_STACK_TYPE);
    }

    /** Mana for one operation, limited by the stored AE power. Never more than an int: Botania works with ints. */
    private static long getAmountPerOperation(PartSharedItemBus<?> bus, AENetworkProxy proxy)
        throws GridAccessException {
        long amount = Math.min((long) bus.calculateAmountToSend() * MANA_PER_MB, Integer.MAX_VALUE);
        IEnergyGrid energy = proxy.getEnergy();
        double power = energy.extractAEPower(amount / MANA_PER_AE, Actionable.SIMULATE, PowerMultiplier.CONFIG);
        return Math.min(amount, (long) (power * MANA_PER_AE + 0.01));
    }

    private static void usePower(AENetworkProxy proxy, long mana) throws GridAccessException {
        proxy.getEnergy()
            .extractAEPower(mana / MANA_PER_AE, Actionable.MODULATE, PowerMultiplier.CONFIG);
    }
}
