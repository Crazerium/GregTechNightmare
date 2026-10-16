package com.EvgenWarGold.GregTechNightmare.GregTech.Mana;

import static com.EvgenWarGold.GregTechNightmare.GregTech.Mana.AEManaStackType.MANA_STACK_TYPE;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import appeng.api.exceptions.AppEngException;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.data.IAEStackType;
import appeng.me.storage.CellInventory;

/** Contents of a mana cell: a single mana stack. AE2 does the byte accounting. */
public class ManaCellInventory extends CellInventory<AEManaStack> {

    private static final String NBT_MANA = "Mana";

    public ManaCellInventory(ItemStack cell, ISaveProvider provider) throws AppEngException {
        super(cell, provider);
    }

    @Override
    protected AEManaStack readStack(NBTTagCompound tag) {
        return AEManaStack.loadStackFromNBT(tag);
    }

    @Override
    protected String getStackTypeTag() {
        return "mt";
    }

    @Override
    protected String getStackCountTag() {
        return "mc";
    }

    @Override
    protected void saveChanges() {
        long count = 0;
        AEManaStack stored = null;
        for (AEManaStack stack : this.cellStacks) {
            count += stack.getStackSize();
            stored = stack;
        }

        if (stored == null || count <= 0) {
            this.tagCompound.removeTag(NBT_MANA);
            this.tagCompound.removeTag(this.getStackTypeTag());
            this.tagCompound.removeTag(this.getStackCountTag());
            this.storedTypes = 0;
        } else {
            NBTTagCompound stackTag = new NBTTagCompound();
            stored.writeToNBT(stackTag);
            this.tagCompound.setTag(NBT_MANA, stackTag);
            this.storedTypes = 1;
            this.tagCompound.setShort(this.getStackTypeTag(), this.storedTypes);
            this.tagCompound.setLong(this.getStackCountTag(), count);
        }
        this.storedCount = count;

        if (this.container != null) this.container.saveChanges(this);
    }

    @Override
    protected void loadCellStacks() {
        long count = 0;
        if (this.tagCompound.hasKey(NBT_MANA)) {
            AEManaStack stack = AEManaStack.loadStackFromNBT(this.tagCompound.getCompoundTag(NBT_MANA));
            if (stack != null && stack.getStackSize() > 0) {
                this.cellStacks.add(stack);
                count = stack.getStackSize();
            }
        }
        this.storedTypes = (short) (count > 0 ? 1 : 0);
        this.storedCount = count;
        this.tagCompound.setShort(this.getStackTypeTag(), this.storedTypes);
        this.tagCompound.setLong(this.getStackCountTag(), count);
    }

    @Nonnull
    @Override
    public IAEStackType<?> getStackType() {
        return MANA_STACK_TYPE;
    }
}
