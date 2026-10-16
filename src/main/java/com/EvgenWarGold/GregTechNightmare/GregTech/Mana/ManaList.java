package com.EvgenWarGold.GregTechNightmare.GregTech.Mana;

import static com.EvgenWarGold.GregTechNightmare.GregTech.Mana.AEManaStackType.MANA_STACK_TYPE;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.annotation.Nonnull;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.data.IAEStackType;
import appeng.api.storage.data.IItemList;

/** Item list for mana: there is only one kind of mana, so the list holds at most one record. */
public class ManaList implements IItemList<AEManaStack> {

    private AEManaStack record;

    @Override
    public void add(AEManaStack stack) {
        if (stack == null) return;
        if (this.record != null) this.record.add(stack);
        else this.record = stack.copy();
    }

    @Override
    public AEManaStack findPrecise(AEManaStack stack) {
        return stack == null ? null : this.record;
    }

    @Override
    public Collection<AEManaStack> findFuzzy(AEManaStack filter, FuzzyMode fuzzy) {
        return filter == null || this.record == null ? Collections.emptyList() : Collections.singletonList(this.record);
    }

    @Override
    public boolean isEmpty() {
        return !this.iterator()
            .hasNext();
    }

    @Override
    public void addStorage(AEManaStack stack) {
        if (stack == null) return;
        if (this.record != null) this.record.incStackSize(stack.getStackSize());
        else this.record = stack.copy();
    }

    @Override
    public void addCrafting(AEManaStack stack) {
        if (stack == null) return;
        if (this.record != null) {
            this.record.setCraftable(true);
            return;
        }
        this.record = stack.copy();
        this.record.setStackSize(0);
        this.record.setCraftable(true);
    }

    @Override
    public void addRequestable(AEManaStack stack) {
        if (stack == null) return;
        if (this.record != null) {
            this.record.setCountRequestable(this.record.getCountRequestable() + stack.getCountRequestable());
            return;
        }
        this.record = stack.copy();
        this.record.setStackSize(0);
        this.record.setCraftable(false);
        this.record.setCountRequestable(stack.getCountRequestable());
    }

    @Override
    public AEManaStack getFirstItem() {
        Iterator<AEManaStack> iterator = this.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    @Override
    public int size() {
        return this.record == null ? 0 : 1;
    }

    @Override
    @Nonnull
    public Iterator<AEManaStack> iterator() {
        if (this.record == null || !this.record.isMeaningful()) return Collections.emptyIterator();
        return new Iterator<>() {

            private boolean done;

            @Override
            public boolean hasNext() {
                return !this.done && ManaList.this.record != null;
            }

            @Override
            public AEManaStack next() {
                this.done = true;
                return ManaList.this.record;
            }

            @Override
            public void remove() {
                ManaList.this.record = null;
            }
        };
    }

    @Override
    public void resetStatus() {
        if (this.record != null) this.record.reset();
    }

    @Override
    public IAEStackType<AEManaStack> getStackType() {
        return MANA_STACK_TYPE;
    }
}
