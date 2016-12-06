package com.palacemc.show.handlers.armorstand;

import org.bukkit.util.Vector;

/**
 * Created by Marc on 10/11/15
 */
public class Movement {
    private Vector motion;
    private long duration;

    public Movement(Vector motion, double speed) {
        this.motion = motion;
        this.duration = (long) speed;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Vector getMotion() {
        return motion;
    }

    public void setMotion(Vector motion) {
        this.motion = motion;
    }
}