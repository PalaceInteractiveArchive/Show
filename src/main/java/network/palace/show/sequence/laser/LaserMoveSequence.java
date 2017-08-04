package network.palace.show.sequence.laser;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.sequence.handlers.LaserObject;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * @author Marc
 * @since 8/2/17
 */
public class LaserMoveSequence extends ShowSequence {
    private final LaserSequence parent;
    private LaserObject object;
    private Location target;
    private Vector change;
    private int duration;

    public LaserMoveSequence(Show show, long time, LaserSequence parent) {
        super(show, time);
        this.parent = parent;
    }

    @Override
    public boolean run() {
        if (!parent.isSpawned()) {
            return true;
        }
        Entity e;
        switch (object) {
            case SOURCE:
                e = parent.getSource();
                break;
            case TARGET:
                e = parent.getTarget();
                break;
            default:
                return true;
        }
        e.teleport(e.getLocation().add(change));
        return ShowUtil.areLocationsEqual(e.getLocation(), target, 1);
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        this.object = LaserObject.fromString(args[2]);
        this.target = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]);
        this.duration = ShowUtil.getInt(args[4]);
        return this;
    }
}
