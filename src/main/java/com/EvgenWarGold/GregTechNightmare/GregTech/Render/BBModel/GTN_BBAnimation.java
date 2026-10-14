package com.EvgenWarGold.GregTechNightmare.GregTech.Render.BBModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Animation taken straight from a Blockbench project (.bbmodel).
 * Keyframe values are kept in Blockbench space, so they are applied to bones as-is
 * (no Bedrock/GeckoLib axis flipping).
 */
public final class GTN_BBAnimation {

    public enum Channel {
        POSITION,
        ROTATION,
        SCALE
    }

    private enum Interpolation {
        LINEAR,
        CATMULLROM,
        STEP
    }

    private static final class Keyframe {

        final float time;
        final float[] pre;
        final float[] post;
        final Interpolation interpolation;

        Keyframe(float time, float[] pre, float[] post, Interpolation interpolation) {
            this.time = time;
            this.pre = pre;
            this.post = post;
            this.interpolation = interpolation;
        }
    }

    public final String name;
    public final float length;
    public final boolean loop;
    /** bone uuid -> channel index -> sorted keyframes */
    private final Map<String, Keyframe[][]> tracks = new HashMap<>();

    private GTN_BBAnimation(String name, float length, boolean loop) {
        this.name = name;
        this.length = length;
        this.loop = loop;
    }

    static GTN_BBAnimation parse(JsonObject json) {
        GTN_BBAnimation animation = new GTN_BBAnimation(
            json.get("name")
                .getAsString(),
            json.has("length") ? json.get("length")
                .getAsFloat() : 0,
            json.has("loop") && "loop".equals(
                json.get("loop")
                    .getAsString()));

        if (!json.has("animators")) return animation;
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("animators")
            .entrySet()) {
            JsonObject animator = entry.getValue()
                .getAsJsonObject();
            if (!animator.has("keyframes")) continue;

            List<List<Keyframe>> channels = new ArrayList<>();
            for (int i = 0; i < Channel.values().length; i++) channels.add(new ArrayList<>());

            for (JsonElement element : animator.getAsJsonArray("keyframes")) {
                JsonObject keyframe = element.getAsJsonObject();
                Channel channel = parseChannel(
                    keyframe.get("channel")
                        .getAsString());
                if (channel == null) continue;

                JsonArray points = keyframe.getAsJsonArray("data_points");
                if (points == null || points.size() == 0) continue;
                float[] pre = parsePoint(
                    points.get(0)
                        .getAsJsonObject(),
                    channel);
                float[] post = points.size() > 1 ? parsePoint(
                    points.get(1)
                        .getAsJsonObject(),
                    channel) : pre;

                channels.get(channel.ordinal())
                    .add(
                        new Keyframe(
                            keyframe.get("time")
                                .getAsFloat(),
                            pre,
                            post,
                            parseInterpolation(keyframe)));
            }

            Keyframe[][] track = new Keyframe[Channel.values().length][];
            boolean any = false;
            for (int i = 0; i < track.length; i++) {
                List<Keyframe> list = channels.get(i);
                if (list.isEmpty()) continue;
                list.sort((a, b) -> Float.compare(a.time, b.time));
                track[i] = list.toArray(new Keyframe[0]);
                any = true;
            }
            if (any) animation.tracks.put(entry.getKey(), track);
        }
        return animation;
    }

    /**
     * Writes the channel value of the bone at the given time into {@code out}.
     *
     * @return false if the bone has no keyframes for this channel ({@code out} is untouched)
     */
    public boolean sample(String boneUuid, Channel channel, float time, float[] out) {
        Keyframe[][] track = tracks.get(boneUuid);
        if (track == null) return false;
        Keyframe[] frames = track[channel.ordinal()];
        if (frames == null) return false;

        if (time <= frames[0].time || frames.length == 1) {
            copy(frames[0].pre, out);
            return true;
        }
        Keyframe last = frames[frames.length - 1];
        if (time >= last.time) {
            copy(last.post, out);
            return true;
        }

        int after = 1;
        while (frames[after].time < time) after++;
        int before = after - 1;
        Keyframe a = frames[before];
        Keyframe b = frames[after];

        if (a.interpolation == Interpolation.STEP) {
            copy(a.post, out);
            return true;
        }

        float alpha = (time - a.time) / (b.time - a.time);
        if (a.interpolation == Interpolation.CATMULLROM || b.interpolation == Interpolation.CATMULLROM) {
            float[] p0 = before > 0 ? frames[before - 1].post : a.post;
            float[] p3 = after < frames.length - 1 ? frames[after + 1].pre : b.pre;
            for (int i = 0; i < 3; i++) out[i] = catmullRom(alpha, p0[i], a.post[i], b.pre[i], p3[i]);
        } else {
            for (int i = 0; i < 3; i++) out[i] = a.post[i] + (b.pre[i] - a.post[i]) * alpha;
        }
        return true;
    }

    private static float catmullRom(float t, float p0, float p1, float p2, float p3) {
        float v0 = (p2 - p0) * 0.5f;
        float v1 = (p3 - p1) * 0.5f;
        float t2 = t * t;
        float t3 = t * t2;
        return (2 * p1 - 2 * p2 + v0 + v1) * t3 + (-3 * p1 + 3 * p2 - 2 * v0 - v1) * t2 + v0 * t + p1;
    }

    private static void copy(float[] from, float[] to) {
        to[0] = from[0];
        to[1] = from[1];
        to[2] = from[2];
    }

    private static Channel parseChannel(String channel) {
        switch (channel) {
            case "position":
                return Channel.POSITION;
            case "rotation":
                return Channel.ROTATION;
            case "scale":
                return Channel.SCALE;
            default:
                return null; // particle / sound / timeline effects are not supported
        }
    }

    private static Interpolation parseInterpolation(JsonObject keyframe) {
        if (!keyframe.has("interpolation")) return Interpolation.LINEAR;
        switch (keyframe.get("interpolation")
            .getAsString()) {
            case "catmullrom":
                return Interpolation.CATMULLROM;
            case "step":
                return Interpolation.STEP;
            default:
                return Interpolation.LINEAR; // bezier falls back to linear
        }
    }

    private static float[] parsePoint(JsonObject point, Channel channel) {
        float fallback = channel == Channel.SCALE ? 1 : 0;
        return new float[] { parseValue(point, "x", fallback), parseValue(point, "y", fallback),
            parseValue(point, "z", fallback) };
    }

    /**
     * Blockbench stores values as Molang strings (often with trailing spaces/newlines); only constants are supported.
     */
    private static float parseValue(JsonObject point, String key, float fallback) {
        if (!point.has(key)) return fallback;
        String raw = point.get(key)
            .getAsString()
            .trim();
        if (raw.isEmpty()) return fallback;
        try {
            return Float.parseFloat(raw);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
