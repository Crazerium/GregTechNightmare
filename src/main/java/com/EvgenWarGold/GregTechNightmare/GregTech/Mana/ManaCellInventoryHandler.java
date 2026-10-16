package com.EvgenWarGold.GregTechNightmare.GregTech.Mana;

import static com.EvgenWarGold.GregTechNightmare.GregTech.Mana.AEManaStackType.MANA_STACK_TYPE;

import javax.annotation.Nonnull;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.data.IAEStackType;
import appeng.me.storage.CellInventoryHandler;
import appeng.tile.inventory.IAEStackInventory;

public class ManaCellInventoryHandler extends CellInventoryHandler<AEManaStack> {

    public ManaCellInventoryHandler(IMEInventory<AEManaStack> inventory) {
        super(inventory, MANA_STACK_TYPE);
    }

    /** AE2 only knows item, fluid and essentia cells; mana is shown together with fluids in the statistics. */
    @Override
    public TYPE getCellType() {
        return TYPE.FLUID;
    }

    @Nonnull
    @Override
    public IAEStackType<?> getStackType() {
        return MANA_STACK_TYPE;
    }

    /** Mana has no variants, so a mana cell cannot be partitioned. */
    @Override
    protected void setPriorityList(boolean hasFuzzy, IAEStackInventory config, FuzzyMode fuzzyMode) {}
}
