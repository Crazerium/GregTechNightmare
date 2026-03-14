package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import galaxyspace.core.capes.GSCapeLoader;

@Mixin(value = GSCapeLoader.class, remap = false)
public class FixGalaxySpaceCapeCrushMixins {

    @Inject(method = "run", at = @At("HEAD"), cancellable = true)
    private void onRun(CallbackInfo ci) {
        ci.cancel();
    }
}
