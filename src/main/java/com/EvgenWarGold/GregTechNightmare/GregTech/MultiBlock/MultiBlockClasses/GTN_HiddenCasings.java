package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.EvgenWarGold.GregTechNightmare.GregTech.Blocks.GTN_HideableCasing;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureElement;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

/**
 * Hideable casings of a multiblock that draws a model in their place: they turn invisible while the structure is formed
 * and come back when it breaks. The volume is given relative to the controller (right, up, back).
 */
public final class GTN_HiddenCasings {

    private static final String NBT_HIDDEN_CASINGS = "gtnHiddenCasings";

    private final int rightMin, rightMax, upMin, upMax, backMin, backMax;
    /** World coordinates (x, y, z triples) of the casings hidden by the controller. */
    private int[] hidden = new int[0];
    /** Set after the first structure check since loading. */
    private boolean structureChecked;
    /** Last structure state sent to clients, so the model appears and disappears together with the structure. */
    private boolean syncedFormed;

    public GTN_HiddenCasings(int rightMin, int rightMax, int upMin, int upMax, int backMin, int backMax) {
        this.rightMin = rightMin;
        this.rightMax = rightMax;
        this.upMin = upMin;
        this.upMax = upMax;
        this.backMin = backMin;
        this.backMax = backMax;
    }

    /** Structure element of a hideable casing variant, in its normal or hidden state. */
    public static <T> IStructureElement<T> ofHideable(GTN_Casings casing) {
        return ofChain(
            ofBlock(casing.getBlock(), casing.getBlockMeta()),
            ofBlock(casing.getBlock(), casing.getBlockMeta() | GTN_HideableCasing.HIDDEN_FLAG));
    }

    public void onStructureChecked() {
        structureChecked = true;
    }

    /** Server tick: hides or shows the casings together with the structure and tells clients when it changes. */
    public void update(IGregTechTileEntity gte, ExtendedFacing facing, boolean formed) {
        if (!gte.isServerSide()) return;
        // wait for the first structure check after loading, otherwise the casings would blink on chunk load
        if (structureChecked && (hidden.length > 0) != formed) {
            if (formed) hide(gte, facing);
            else show(gte);
        }
        if (syncedFormed != formed) {
            syncedFormed = formed;
            gte.issueTileUpdate();
        }
    }

    private void hide(IGregTechTileEntity gte, ExtendedFacing facing) {
        ForgeDirection right = facing.getRelativeRightInWorld();
        ForgeDirection up = facing.getRelativeUpInWorld();
        ForgeDirection back = facing.getRelativeBackInWorld();
        World world = gte.getWorld();

        List<Integer> positions = new ArrayList<>();
        for (int r = rightMin; r <= rightMax; r++) for (int u = upMin; u <= upMax; u++) {
            for (int b = backMin; b <= backMax; b++) {
                int x = gte.getXCoord() + right.offsetX * r + up.offsetX * u + back.offsetX * b;
                int y = gte.getYCoord() + right.offsetY * r + up.offsetY * u + back.offsetY * b;
                int z = gte.getZCoord() + right.offsetZ * r + up.offsetZ * u + back.offsetZ * b;
                if (GTN_HideableCasing.setHidden(world, x, y, z, true)) {
                    positions.add(x);
                    positions.add(y);
                    positions.add(z);
                }
            }
        }
        hidden = positions.stream()
            .mapToInt(Integer::intValue)
            .toArray();
        gte.markDirty();
    }

    /** Makes the hidden casings visible again, also when the controller is broken. */
    public void show(IGregTechTileEntity gte) {
        World world = gte.getWorld();
        for (int i = 0; i + 2 < hidden.length; i += 3) {
            if (world.blockExists(hidden[i], hidden[i + 1], hidden[i + 2])) {
                GTN_HideableCasing.setHidden(world, hidden[i], hidden[i + 1], hidden[i + 2], false);
            }
        }
        hidden = new int[0];
        gte.markDirty();
    }

    public void save(NBTTagCompound nbt) {
        nbt.setIntArray(NBT_HIDDEN_CASINGS, hidden);
    }

    public void load(NBTTagCompound nbt) {
        hidden = nbt.getIntArray(NBT_HIDDEN_CASINGS);
    }
}
