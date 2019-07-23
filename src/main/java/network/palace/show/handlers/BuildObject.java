package network.palace.show.handlers;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.npc.entity.EntityFallingBlock;
import network.palace.core.npc.mob.MobArmorStand;
import network.palace.core.pathfinding.Point;
import network.palace.show.Show;
import network.palace.show.handlers.block.Build;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class BuildObject {
    @Getter protected String id;
    @Getter @Setter protected Build build;
    protected Location loc;
    @Getter protected Show show;
    @Getter private boolean spawned = false;

    private HashMap<MobArmorStand, EntityFallingBlock> entities = new HashMap<>();

    public BuildObject(String id, Build build, Show show) {
        this.id = id;
        this.build = build;
        this.show = show;
    }

    public void move(Vector change) {
        this.loc.add(change);
        entities.forEach((stand, block) -> stand.move(Point.of(change.getX(), change.getY(), change.getZ(), loc.getWorld()), true));
    }

    /**
     * Move the entities by 'change' amount, and despawn if they cross over 'y'
     *
     * @param change a vector representing the change in the x, y and z dimensions
     * @param y      the y axis that when entities cross it they are removed
     * @param above  true if entities are removed when they're above 'y', false if when below 'y'
     */
    public void move(Vector change, double y, boolean above) {
        this.loc.add(change);
        entities.forEach((stand, block) -> {
            if (!stand.isSpawned()) return;
            stand.move(Point.of(change.getX(), change.getY(), change.getZ(), loc.getWorld()), true);
            double standY = stand.getLocation().getY();
            if ((above && standY > y) || (!above && standY < y)) {
                block.despawn();
                stand.despawn();
            }
        });
    }

    public void teleport(Location loc) {
        if (this.loc == null) {
            this.loc = loc;
            return;
        }
        this.loc = loc;
        entities.forEach((stand, block) -> stand.move(Point.of(loc.getX(), loc.getY(), loc.getZ(), loc.getWorld()), false));
    }

    public Location getLocation() {
        return loc.clone();
    }

    public HashMap<MobArmorStand, EntityFallingBlock> getEntities() {
        return new HashMap<>(entities);
    }

    public BuildObject duplicate() {
        BuildObject object = new BuildObject(id, build, show);
        object.teleport(loc);
        return object;
    }

    public void spawn() {
        spawned = true;
        if (!entities.isEmpty()) {
            entities.forEach((stand, block) -> {
                stand.spawn();
                block.spawn();
                stand.addPassenger(block);
            });
        }
        for (Build.BuildBlock block : build.getBlocks()) {
            if (block.getTypeId() == 0) continue;
            Location rel = loc.clone().add(block.getX(), block.getY(), block.getZ());
            MobArmorStand stand = new MobArmorStand(Point.of(rel), null, "");
            stand.setVisible(false);
            stand.setGravity(false);
            stand.setArms(false);
            stand.setBasePlate(false);
            stand.setMarker(false);
            stand.spawn();
            EntityFallingBlock fallingBlock = new EntityFallingBlock(Point.of(rel), null, "", block.getTypeId(), block.getData());
            fallingBlock.spawn();
            stand.addPassenger(fallingBlock);
            entities.put(stand, fallingBlock);
        }
    }

    public void despawn() {
        spawned = false;
        entities.forEach((stand, block) -> {
            stand.removePassenger(block);
            block.despawn();
            stand.despawn();
        });
    }
}
