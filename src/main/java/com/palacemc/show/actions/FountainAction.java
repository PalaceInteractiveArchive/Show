package com.palacemc.show.actions;

import com.palacemc.show.Show;
import com.palacemc.show.handlers.Fountain;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import us.mcmagic.parkmanager.ParkManager;

public class FountainAction extends ShowAction {
    private double duration;
    private Location loc;
    private int type;
    private byte data;
    private Vector force;

    public FountainAction(Show show, long time, Location loc, double duration, int type, byte data, Vector force) {
        super(show, time);
        this.loc = loc;
        this.duration = duration;
        this.type = type;
        this.data = data;
        this.force = force;
    }

    @Override
    public void play() {
        ParkManager.fountainManager.addFountain(new Fountain(loc, duration, type, data, force));
    }
}