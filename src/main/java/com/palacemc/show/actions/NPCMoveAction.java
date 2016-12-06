package com.palacemc.show.actions;

import com.palacemc.show.Show;
import com.palacemc.show.handlers.ShowNPC;
import org.bukkit.Location;

public class NPCMoveAction extends ShowAction {
    public String Name;
    public Location Location;
    public float Speed;

    public NPCMoveAction(Show show, long time, String npc, Location location, float speed) {
        super(show, time);

        Name = npc;
        Location = location;
        Speed = speed;
    }

    @Override
    public void play() {
        ShowNPC npc = show.getNPCMap().get(Name);
        if (npc != null) {
            npc.SetTarget(Location, Speed);
        }
    }
}
