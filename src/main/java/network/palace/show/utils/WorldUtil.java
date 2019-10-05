package network.palace.show.utils;

import com.comphenix.protocol.utility.MinecraftReflection;
import network.palace.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WorldUtil {

    public static Location strToLoc(String string) {
        if (string.length() == 0) {
            return null;
        }
        String[] tokens = string.split(",");
        try {
            for (World cur : Bukkit.getWorlds()) {
                if (cur.getName().equalsIgnoreCase(tokens[0])) {
                    return new Location(cur, Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]),
                            Double.parseDouble(tokens[3]));
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static Location strToLocWithYaw(String string) {
        if (string.length() == 0) {
            return null;
        }

        String[] tokens = string.split(",");
        try {
            for (World world : Bukkit.getWorlds()) {
                if (world.getName().equalsIgnoreCase(tokens[0])) {
                    if (tokens.length > 4) {
                        return new Location(world, Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]),
                                Double.parseDouble(tokens[3]), Float.parseFloat(tokens[4]), 0);
                    } else {
                        return new Location(world, Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]),
                                Double.parseDouble(tokens[3]));
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public static Double[] strToDoubleList(String string) {
        if (string.length() == 0) {
            return null;
        }
        String[] tokens = string.split(",");
        Double[] doublelist = new Double[3];
        doublelist[0] = Double.parseDouble(tokens[1]);
        doublelist[1] = Double.parseDouble(tokens[2]);
        doublelist[2] = Double.parseDouble(tokens[3]);
        return doublelist;
    }

    public static ArmorStand lock(ArmorStand stand) {
        try {
            String lockField;
            switch (Core.getMinecraftVersion()) {
                case "v1_13_R1":
                case "v1_13_R2":
                    lockField = "bH";
                    break;
                case "v1_12_R1":
                    lockField = "bB";
                    break;
                default:
                    lockField = "bA";
                    break;
            }
            Field f = Class.forName("net.minecraft.server." + Core.getMinecraftVersion() + ".EntityArmorStand")
                    .getDeclaredField(lockField);
            if (f != null) {
                f.setAccessible(true);
                Object craftStand = Class.forName("org.bukkit.craftbukkit." + Core.getMinecraftVersion() +
                        ".entity.CraftArmorStand").cast(stand);
                Object handle = craftStand.getClass().getDeclaredMethod("getHandle").invoke(craftStand);
                f.set(handle, 2096896);
            }
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return stand;
    }

    public static void teleport(Entity entity, Location loc) {
        try {
            Method getHandle = entity.getClass().getMethod("getHandle");
            Object minecraftEntity = getHandle.invoke(entity);

            Method setLocation = minecraftEntity.getClass().getMethod("setLocation",
                    Double.TYPE, Double.TYPE, Double.TYPE, Float.TYPE, Float.TYPE);
            Method setHeadRotation = minecraftEntity.getClass().getMethod("setHeadRotation", Float.TYPE);

            Object worldServer = minecraftEntity.getClass().getField("world").get(minecraftEntity);
            Method entityJoinedWorld = worldServer.getClass().getMethod("entityJoinedWorld", MinecraftReflection.getEntityClass(), Boolean.TYPE);

            setLocation.invoke(minecraftEntity, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            setHeadRotation.invoke(minecraftEntity, loc.getYaw());
//            entityJoinedWorld.invoke(worldServer, minecraftEntity, false);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
