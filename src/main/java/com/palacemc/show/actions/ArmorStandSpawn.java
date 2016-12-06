package com.palacemc.show.actions;

import com.palacemc.show.Show;
import com.palacemc.show.handlers.ArmorData;
import com.palacemc.show.handlers.armorstand.ShowStand;
import net.minecraft.server.v1_11_R1.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;

import java.lang.reflect.Field;

/**
 * Created by Marc on 10/11/15
 */
public class ArmorStandSpawn extends ShowAction {
    private ShowStand stand;
    private Location loc;

    public ArmorStandSpawn(Show show, long time, ShowStand stand, Location loc) {
        super(show, time);
        this.stand = stand;
        this.loc = loc;
    }

    @Override
    public void play() {
        if (stand.hasSpawned()) {
            Bukkit.broadcast("ArmorStand with ID " + stand.getId() + " has spawned already", "arcade.bypass");
            return;
        }
        ArmorStand armor = loc.getWorld().spawn(loc, ArmorStand.class);
        stand.spawn();
        armor.setArms(true);
        armor.setBasePlate(false);
        armor.setGravity(false);
        armor.setSmall(stand.isSmall());

        try {
            EntityArmorStand nmsStand = ((CraftArmorStand) armor).getHandle();
            Field f = nmsStand.getClass().getDeclaredField("bi");
            f.setAccessible(true);
            f.setInt(nmsStand, 2039583);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        ArmorData data = stand.getArmorData();
        if (data != null) {
            if (data.getHead() != null) {
                armor.setHelmet(data.getHead());
            }
            if (data.getChestplate() != null) {
                armor.setChestplate(data.getChestplate());
            }
            if (data.getLeggings() != null) {
                armor.setLeggings(data.getLeggings());
            }
            if (data.getBoots() != null) {
                armor.setBoots(data.getBoots());
            }
        }
        stand.setStand(armor);
    }
}