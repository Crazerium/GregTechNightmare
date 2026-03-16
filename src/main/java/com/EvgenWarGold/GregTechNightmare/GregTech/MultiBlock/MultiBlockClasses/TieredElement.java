package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import static com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_StructureUtility.createTierBlocks;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureUtility;

import gregtech.api.interfaces.IHatchElement;
import gregtech.api.util.HatchElementBuilder;

public class TieredElement<T> implements IStructureElement<T> {

    private final GTN_StructureUtility.TierData tierData;
    private GTN_Casings[] casings = new GTN_Casings[0];
    private IHatchElement<? super T>[] hatches = null;
    private int dot = 1;

    public TieredElement(GTN_StructureUtility.TierData tierData) {
        this.tierData = tierData;
    }

    public TieredElement<T> withCasings(GTN_Casings... casings) {
        this.casings = casings;
        return this;
    }

    @SafeVarargs
    public final TieredElement<T> withHatches(int dot, IHatchElement<? super T>... hatches) {
        this.dot = dot;
        this.hatches = hatches;
        return this;
    }

    private IStructureElement<T> buildInternal() {
        return StructureUtility.ofChain(
            createTierBlocks(tierData, casings),
            HatchElementBuilder.<T>builder()
                .atLeast(hatches)
                .dot(dot)
                .casingIndex(tierData.getCasingTextureId())
                .buildAndChain());
    }

    @Override
    public boolean check(T t, World world, int x, int y, int z) {
        return buildInternal().check(t, world, x, y, z);
    }

    @Override
    public boolean spawnHint(T t, World world, int x, int y, int z, ItemStack trigger) {
        return buildInternal().spawnHint(t, world, x, y, z, trigger);
    }

    @Override
    public boolean placeBlock(T t, World world, int x, int y, int z, ItemStack trigger) {
        return buildInternal().placeBlock(t, world, x, y, z, trigger);
    }

    public static <T> TieredElement<T> build(GTN_StructureUtility.TierData tierData) {
        return new TieredElement<>(tierData);
    }
}
