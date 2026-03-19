package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.google.common.collect.SetMultimap;

import gregtech.api.util.MultiblockTooltipBuilder;

@Mixin(value = MultiblockTooltipBuilder.class, remap = false)
public interface MultiblockTooltipBuilderAccessor {

    @Accessor
    List<String> getILines();

    @Accessor
    List<String> getSLines();

    @Accessor
    List<String> getHLines();

    @Accessor
    SetMultimap<Integer, String> getHBlocks();

    @Accessor
    String[] getIArray();

    @Accessor
    String[] getSArray();

    @Accessor
    String[] getHArray();

    @Accessor
    void setIArray(String[] value);

    @Accessor
    void setSArray(String[] value);

    @Accessor
    void setHArray(String[] value);
}
