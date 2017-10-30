package network.palace.show.sequence.laser;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * @author Marc
 * @since 8/2/17
 */
public class LaserSpawnSequence extends ShowSequence {
    private final LaserSequence parent;
    private Location loc = null;
    private Location loc2 = null;

    public LaserSpawnSequence(Show show, long time, LaserSequence parent) {
        super(show, time);
        this.parent = parent;
    }

    @Override
    public boolean run() {
        if (parent.isSpawned()) {
            return true;
        }
        try {
            if (loc2 == null) {
                parent.spawn(loc);
            } else {
                parent.spawn(loc, loc2);
            }
        } catch (ShowParseException e) {
            Bukkit.getLogger().warning("Error on Spawn action for Laser Sequence Cause: " + e.getReason());
            Bukkit.broadcast("Error on Spawn action for Laser Sequence Cause: " + e.getReason(), "arcade.bypass");
        }
        return true;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        if (args.length > 2) {
            this.loc = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[2]);
        }
        if (args.length > 3) {
            this.loc2 = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]);
        }
        return this;
    }
}
