package com.palacemc.show.actions;

import com.palacemc.show.Show;
import com.palacemc.show.handlers.armorstand.Rotation;
import com.palacemc.show.handlers.armorstand.ShowStand;
import com.palacemc.show.handlers.armorstand.StandAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import us.mcmagic.parkmanager.ParkManager;

/**
 * Created by Marc on 3/26/16
 */
public class ArmorStandRotate extends ShowAction {
    private ShowStand stand;
    private float yaw;
    private double speed;

    public ArmorStandRotate(Show show, long time, ShowStand stand, float yaw, double speed) {
        super(show, time);
        this.stand = stand;
        this.yaw = yaw;
        this.speed = speed;
    }

    @Override
    public void play() {
        if (!stand.hasSpawned()) {
            Bukkit.broadcast("ArmorStand with ID " + stand.getId() + " has not spawned", "arcade.bypass");
            return;
        }
        ArmorStand armor = stand.getStand();
        float yaw = (float) (((armor.getLocation().getYaw() + this.yaw) / speed) / 20);
        stand.setRotation(new Rotation(yaw, speed * 20));
        ParkManager.armorStandManager.addStand(stand, StandAction.ROTATION);
    }
}