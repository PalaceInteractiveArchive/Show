package com.palacemc.show.actions;


import com.palacemc.show.Show;
import com.palacemc.show.handlers.ShowNPC;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public class NPCSpawnAction extends ShowAction {
    private static Field _goalSelector;
    private static Field _targetSelector;
    private static Field _bsRestrictionGoal;
    public String Name;
    public Location Location;
    public EntityType Type;
    public Material Item;
    public NPCSpawnAction(Show show, long time, String npc, Location location,
                          EntityType type, Material holding) {
        super(show, time);

        Name = npc;
        Location = location;
        Type = type;
        Item = holding;

        if (Type == EntityType.PLAYER)
            Type = EntityType.SKELETON;
    }

    public static void Vegetate(Entity entity) {
        try {
            if (_goalSelector == null) {
                _goalSelector = EntityInsentient.class
                        .getDeclaredField("goalSelector");
                _goalSelector.setAccessible(true);
            }

            if (_targetSelector == null) {
                _targetSelector = EntityInsentient.class
                        .getDeclaredField("targetSelector");
                _targetSelector.setAccessible(true);
            }

            if (entity instanceof CraftCreature) {
                EntityCreature creature = ((CraftCreature) entity).getHandle();

                if (_bsRestrictionGoal == null) {
                    _bsRestrictionGoal = EntityCreature.class
                            .getDeclaredField("bs");
                    _bsRestrictionGoal.setAccessible(true);
                }

                _bsRestrictionGoal.set(creature,
                        new PathfinderGoalMoveTowardsRestriction(creature, 0D));
            }

            if (((CraftEntity) entity).getHandle() instanceof EntityInsentient) {
                EntityInsentient creature = (EntityInsentient) ((CraftEntity) entity)
                        .getHandle();

                PathfinderGoalSelector goalSelector = new PathfinderGoalSelector(
                        ((CraftWorld) entity.getWorld()).getHandle().methodProfiler);

                goalSelector.a(7, new PathfinderGoalLookAtPlayer(creature,
                        EntityHuman.class, 6.0F));
                goalSelector.a(7, new PathfinderGoalRandomLookaround(creature));

                _goalSelector.set(creature, goalSelector);
                _targetSelector.set(
                        creature,
                        new PathfinderGoalSelector(((CraftWorld) entity
                                .getWorld()).getHandle().methodProfiler));
            }
        } catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void play() {
        // Remove Old
        ShowNPC npc = show.getNPCMap().remove(Name);
        if (npc != null)
            npc.clean();

        // Spawn New
        Entity ent = Location.getWorld().spawnEntity(Location, Type);

        if (ent instanceof Creature) {
            Creature creature = (Creature) ent;

            String name = "";
            for (String cur : Name.split("&"))
                name = cur + " ";
            if (name.length() > 0)
                name = name.substring(0, name.length() - 1);

            creature.setCustomName(name);
            creature.setCustomNameVisible(true);

            creature.setMaxHealth(5000.0);
            creature.setHealth(5000.0);

            if (Item != null) {
                creature.getEquipment().setItemInHand(new ItemStack(Item));
            }
        }

        Vegetate(ent);

        show.getNPCMap().put(Name, new ShowNPC(ent));

        System.out.println("Spawned NPC: " + Name);
    }
}
