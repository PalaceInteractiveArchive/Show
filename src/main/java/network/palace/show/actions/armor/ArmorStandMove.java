package network.palace.show.actions.armor;

import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.actions.ShowAction;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.armorstand.Movement;
import network.palace.show.handlers.armorstand.ShowStand;
import network.palace.show.handlers.armorstand.StandAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Created by Marc on 10/11/15
 */
public class ArmorStandMove extends ShowAction {
    private final Location loc;
    private ShowStand stand;
    private double speed;

    public ArmorStandMove(Show show, long time, ShowStand stand, Location loc, double speed) {
        super(show, time);
        this.stand = stand;
        this.loc = loc;
        this.speed = speed;
    }

    @Override
    public void play() {
        if (!stand.isHasSpawned()) {
            Bukkit.broadcast("ArmorStand with ID " + stand.getId() + " has not spawned", "palace.core.rank.mod");
            return;
        }
        Location l = stand.getStand().getLocation();
        if (loc == null || l == null) {
            return;
        }
        double x = ((float) (((float) (loc.getX() - l.getX())) / (20 * speed)));
        double y = ((float) (((float) (loc.getY() - l.getY())) / (20 * speed)));
        double z = ((float) (((float) (loc.getZ() - l.getZ())) / (20 * speed)));
        Vector motion = new Vector(x, y, z);
        stand.setMovement(new Movement(motion, speed * 20));
        ShowPlugin.getInstance().getArmorStandManager().addStand(stand, StandAction.MOVE);
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        return this;
    }
}