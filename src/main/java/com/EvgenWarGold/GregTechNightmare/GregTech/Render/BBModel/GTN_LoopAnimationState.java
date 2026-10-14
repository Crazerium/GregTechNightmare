package com.EvgenWarGold.GregTechNightmare.GregTech.Render.BBModel;

/**
 * Client-side playback state of a looping machine animation.
 * <p>
 * While the machine works the animation speeds up from rest to full speed; when it stops, the current cycle is played
 * to the end so the model always comes back to its rest pose (time 0) instead of freezing mid-motion.
 */
public final class GTN_LoopAnimationState {

    private static final float SPIN_UP_SECONDS = 0.75F;
    /** Large frame gaps (model was off-screen, game paused) are clamped to avoid jumps. */
    private static final float MAX_FRAME_SECONDS = 0.1F;

    private float time;
    private float speed;
    private long lastUpdateNanos = -1;

    /** Advances the animation and returns the time to sample, in seconds. */
    public float update(boolean active, float length) {
        long now = System.nanoTime();
        float delta = lastUpdateNanos < 0 ? 0 : Math.min((now - lastUpdateNanos) / 1.0E9F, MAX_FRAME_SECONDS);
        lastUpdateNanos = now;
        if (length <= 0) return 0;

        if (active) speed = Math.min(1, speed + delta / SPIN_UP_SECONDS);
        if (speed <= 0) return time;

        time += delta * speed;
        if (time >= length) {
            if (active) {
                time %= length;
            } else {
                time = 0;
                speed = 0;
            }
        }
        return time;
    }

    /** 0 when the model is at rest, 1 when it plays at full speed. */
    public float getSpeed() {
        return speed;
    }
}
