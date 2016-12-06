package com.palacemc.show.handlers;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Fountain {
    public double duration;
    public Location loc;
    public int type;
    public byte data;
    public Vector force;

    public Fountain(Location loc, double duration, int type, byte data,
                    Vector force) {
        this.loc = loc;
        this.duration = duration;
        this.type = type;
        this.data = data;
        this.force = force;
    }

    public Location getLocation() {
        return loc;
    }

    public void setLocation(Location loc) {
        this.loc = loc;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte getData() {
        return data;
    }

    public void setData(byte data) {
        this.data = data;
    }

    public Vector getForce() {
        return force;
    }

    public void setForce(Vector force) {
        this.force = force;
    }
}