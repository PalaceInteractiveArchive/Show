package network.palace.show;

import network.palace.show.handlers.armorstand.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 10/11/15
 */
public class ArmorStandManager {
    private List<ShowStand> move = new ArrayList<>();
    private List<ShowStand> pos = new ArrayList<>();
    private List<ShowStand> rot = new ArrayList<>();

    public ArmorStandManager() {
        start();
    }

    private void start() {
        Bukkit.getScheduler().runTaskTimer(ShowPlugin.getInstance(), () -> {
            try {
                for (ShowStand stand : new ArrayList<>(move)) {
                    ArmorStand armor = stand.getStand();
                    if (armor == null) {
                        move.remove(stand);
                        continue;
                    }
                    Movement movement = stand.getMovement();
                    Vector motion = movement.getMotion();
                    Location loc = armor.getLocation();
                    float yaw = loc.getYaw();
                    float pitch = loc.getPitch();
                    if (rot.contains(stand)) {
                        for (ShowStand s2 : new ArrayList<>(rot)) {
                            if (!s2.getId().equals(stand.getId())) {
                                continue;
                            }
                            Rotation r = s2.getRotation();
                            r.handle();
                            yaw += r.getYaw();
                            r.setDuration(r.getDuration() - 1);
                            if (r.getDuration() < 0) {
                                rot.remove(s2);
                            }
                            break;
                        }
                    }
                    armor.teleport(new Location(armor.getWorld(), loc.getX() + motion.getX(), loc.getY() + motion.getY(),
                            loc.getZ() + motion.getZ(), yaw, pitch));
                    movement.setDuration(movement.getDuration() - 1);
                    if (movement.getDuration() < 0) {
                        move.remove(stand);
                    }
                }
                for (ShowStand stand : new ArrayList<>(pos)) {
                    ArmorStand armor = stand.getStand();
                    for (Position position : stand.getPositions()) {
                        Vector motion = position.getMotion();
                        switch (position.getPositionType()) {
                            case HEAD: {
                                EulerAngle cur = armor.getHeadPose();
                                EulerAngle newangle = new EulerAngle(cur.getX() + motion.getX(), cur.getY() + motion.getY(),
                                        cur.getZ() + motion.getZ());
                                armor.setHeadPose(newangle);
                                break;
                            }
                            case BODY: {
                                EulerAngle cur = armor.getBodyPose();
                                armor.setBodyPose(new EulerAngle(cur.getX() + motion.getX(), cur.getY() + motion.getY(),
                                        cur.getZ() + motion.getZ()));
                                break;
                            }
                            case ARM_LEFT: {
                                EulerAngle cur = armor.getLeftArmPose();
                                armor.setLeftArmPose(new EulerAngle(cur.getX() + motion.getX(), cur.getY() + motion.getY(),
                                        cur.getZ() + motion.getZ()));
                                break;
                            }
                            case ARM_RIGHT: {
                                EulerAngle cur = armor.getRightArmPose();
                                armor.setRightArmPose(new EulerAngle(cur.getX() + motion.getX(), cur.getY() + motion.getY(),
                                        cur.getZ() + motion.getZ()));
                                break;
                            }
                            case LEG_LEFT: {
                                EulerAngle cur = armor.getLeftLegPose();
                                armor.setLeftLegPose(new EulerAngle(cur.getX() + motion.getX(), cur.getY() + motion.getY(),
                                        cur.getZ() + motion.getZ()));
                                break;
                            }
                            case LEG_RIGHT: {
                                EulerAngle cur = armor.getRightLegPose();
                                armor.setRightLegPose(new EulerAngle(cur.getX() + motion.getX(), cur.getY() + motion.getY(),
                                        cur.getZ() + motion.getZ()));
                                break;
                            }
                        }
                        position.setDuration(position.getDuration() - 1);
                        if (position.getDuration() < 0) {
                            stand.removePosition(position);
                            if (stand.getPositions().isEmpty()) {
                                pos.remove(stand);
                            }
                        }
                    }
                }
                for (ShowStand stand : new ArrayList<>(rot)) {
                    if (!stand.isHasSpawned()) {
                        continue;
                    }
                    ArmorStand armor = stand.getStand();
                    if (armor == null) {
                        continue;
                    }
                    Rotation r = stand.getRotation();
                    if (r.isHandled()) {
                        continue;
                    }
                    Location loc = armor.getLocation().clone();
                    armor.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw() +
                            r.getYaw(), loc.getPitch()));
                    r.setDuration(r.getDuration() - 1);
                    if (r.getDuration() < 0) {
                        rot.remove(stand);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0L, 1L);
    }

    public void addStand(ShowStand stand, StandAction action) {
        switch (action) {
            case MOVE:
                move.add(stand);
                break;
            case POSITION:
                pos.add(stand);
                break;
            case ROTATION:
                rot.add(stand);
                break;
        }
    }
}