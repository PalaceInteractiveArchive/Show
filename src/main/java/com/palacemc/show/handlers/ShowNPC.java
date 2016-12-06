package com.palacemc.show.handlers;

import net.minecraft.server.v1_11_R1.EntityCreature;
import net.minecraft.server.v1_11_R1.NavigationAbstract;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftCreature;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import us.mcmagic.parkmanager.utils.AlgUtil;
import us.mcmagic.parkmanager.utils.MathUtil;

public class ShowNPC {
    private Entity entity;
    private Location loc;
    private float speed;

    public ShowNPC(Entity ent) {
        entity = ent;
        loc = ent.getLocation();
    }

    public void SetTarget(Location target, float speed) {
        loc = target;
        this.speed = speed;
    }

    public void move() {
        if (entity == null)
            return;
        if (!(entity instanceof Creature))
            return;
        if (MathUtil.offset(entity.getLocation(), loc) < 0.25)
            return;
        EntityCreature ec = ((CraftCreature) entity).getHandle();
        //Path Finding
        NavigationAbstract nav = ec.getNavigation();
        if (MathUtil.offset(entity.getLocation(), loc) > 12) {
            Location newTarget = entity.getLocation();
            newTarget.add(AlgUtil.getTrajectory(entity.getLocation(), loc).multiply(12));
            nav.a(newTarget.getX(), newTarget.getY(), newTarget.getZ(), speed);
        } else {
            nav.a(loc.getX(), loc.getY(), loc.getZ(), speed);
        }
        //FAST
        //ec.getControllerMove().a(loc.getX(), loc.getY(), loc.getZ(), speed);
    }

    public void clean() {
        if (entity != null)
            entity.remove();
    }

    public Entity getEntity() {
        return entity;
    }
}
