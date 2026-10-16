package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import static com.EvgenWarGold.GregTechNightmare.GregTech.Mana.AEManaStackType.MANA_STACK_TYPE;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.EvgenWarGold.GregTechNightmare.GregTech.Mana.GTN_ManaStorageLink;
import com.glodblock.github.common.parts.PartFluidStorageBus;

import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageName;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.parts.misc.PartStorageBus;
import appeng.tile.inventory.IAEStackInventory;

/** A fluid storage bus facing a Botania mana pool also provides the pool as mana storage. */
@Mixin(value = PartStorageBus.class, remap = false)
public abstract class PartStorageBusManaMixin {

    @Unique
    private GTN_ManaStorageLink gtn$manaLink;

    @Unique
    private GTN_ManaStorageLink gtn$getManaLink() {
        if (!((Object) this instanceof PartFluidStorageBus bus)) return null;
        if (this.gtn$manaLink == null) this.gtn$manaLink = new GTN_ManaStorageLink(bus);
        return this.gtn$manaLink;
    }

    /**
     * @param type     requested stack type
     * @param callback the mana pool storage for the mana stack type
     * @author Crazerium
     * @reason Fluid storage buses are reused for mana instead of adding a separate mana storage bus
     */
    @SuppressWarnings("rawtypes")
    @Inject(method = "getCellArray", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void gtn$manaCellArray(IAEStackType<?> type, CallbackInfoReturnable<List<IMEInventoryHandler>> callback) {
        if (type != MANA_STACK_TYPE) return;
        GTN_ManaStorageLink link = this.gtn$getManaLink();
        if (link != null) callback.setReturnValue(link.getCellArray());
    }

    /**
     * @param callback unused
     * @author Crazerium
     * @reason Partitioning a bus in front of a mana pool could put mana into the fluid filter, which breaks the fluid
     *         storage of the bus. Only fluids are kept in the filter.
     */
    @Inject(method = "getInternalHandler", at = @At("HEAD"), remap = false, require = 1)
    private void gtn$keepOnlyFluidFilters(CallbackInfoReturnable<?> callback) {
        if (!((Object) this instanceof PartFluidStorageBus bus)) return;
        IAEStackInventory config = bus.getAEInventoryByName(StorageName.CONFIG);
        for (int i = 0; i < config.getSizeInventory(); i++) {
            IAEStack<?> stack = config.getAEStackInSlot(i);
            if (stack != null && !(stack instanceof IAEFluidStack)) config.putAEStackInSlot(i, null);
        }
    }

    /**
     * @param callback unused
     * @author Crazerium
     * @reason The network has to learn when a mana pool appears in front of the bus or disappears
     */
    @Inject(method = "onNeighborChanged", at = @At("TAIL"), remap = false, require = 1)
    private void gtn$manaNeighborChanged(CallbackInfo callback) {
        GTN_ManaStorageLink link = this.gtn$getManaLink();
        if (link != null) link.onNeighborChanged();
    }
}
