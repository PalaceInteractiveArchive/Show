package network.palace.show.actions.armor;

import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.actions.ShowAction;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.armorstand.Movement;
import network.palace.show.handlers.armorstand.ShowStand;
import network.palace.show.handlers.armorstand.StandAction;
import network.palace.show.utils.ShowUtil;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Created by Marc on 10/11/15
 */
public class ArmorStandMove extends ShowAction {
    private final Location loc;
    private final ShowStand stand;
    private final double speed;

    public ArmorStandMove(Show show, long time, ShowStand stand, Location loc, double speed) {
        super(show, time);
        this.stand = stand;
        this.loc = loc;
        this.speed = speed;
    }

    @Override
    public boolean play(CPlayer[] nearPlayers) {
        if (!stand.isHasSpawned()) {
            ShowUtil.logDebug(show.getName(), "ArmorStand with ID " + stand.getId() + " has not spawned");
            return true;
        }
        Location l = stand.getStand().getLocation();
        if (loc == null || l == null) {
            return true;
        }
        double x = ((float) (((float) (loc.getX() - l.getX())) / (20 * speed)));
        double y = ((float) (((float) (loc.getY() - l.getY())) / (20 * speed)));
        double z = ((float) (((float) (loc.getZ() - l.getZ())) / (20 * speed)));
        Vector motion = new Vector(x, y, z);
        stand.setMovement(new Movement(motion, speed * 20));
        ShowPlugin.getInstance().getArmorStandManager().addStand(stand, StandAction.MOVE);
        return true;
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        return this;
    }
}