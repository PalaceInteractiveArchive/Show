package com.palacemc.show.actions;

import com.palacemc.show.Show;
import com.palacemc.show.handlers.armorstand.ShowStand;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;

/**
 * Created by Marc on 10/11/15
 */
public class ArmorStandDespawn extends ShowAction {
    private ShowStand stand;

    public ArmorStandDespawn(Show show, long time, ShowStand stand) {
        super(show, time);
        this.stand = stand;
    }

    @Override
    public void play() {
        if (!stand.hasSpawned()) {
            Bukkit.broadcast("ArmorStand with ID " + stand.getId() + " has not spawned", "arcade.bypass");
            return;
        }
        ArmorStand armor = stand.getStand();
        armor.remove();
        stand.setStand(null);
        stand.despawn();
    }
}