package network.palace.show.sequence.laser;

import network.palace.show.Show;
import network.palace.show.beam.beam.Beam;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.sequence.handlers.LaserObject;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * @author Marc
 * @since 8/2/17
 */
public class LaserMoveSequence extends ShowSequence {
    private final LaserSequence parent;
    private LaserObject object;
    private Location target;
    private Vector change = null;
    private long startTime = 0;
    private int duration;

    public LaserMoveSequence(Show show, long time, LaserSequence parent) {
        super(show, time);
        this.parent = parent;
    }

    @Override
    public boolean run() {
        if (!parent.isSpawned()) return true;
        Beam beam = parent.getBeam();
        if (beam == null || !beam.isActive()) return true;
        Location loc;
        switch (object) {
            case SOURCE:
                loc = beam.getSource();
                break;
            case TARGET:
                loc = beam.getTarget();
                break;
            default:
                return true;
        }
        if (startTime == 0) {
            switch (parent.state) {
                case RELATIVE:
                    change = target.toVector();
                    break;
                case ACTUAL: {
                    change = new Vector(target.getX() - loc.getX(), target.getY() - loc.getY(), target.getZ() - loc.getZ());
                    break;
                }
                default:
                    return true;
            }
            if (duration != 0) {
                change.divide(new Vector(duration, duration, duration));
            }
            startTime = System.currentTimeMillis();
        }

        if ((startTime) + (duration * 50) <= System.currentTimeMillis()) {
            return true;
        }

        switch (object) {
            case SOURCE:
                beam.setSource(loc.add(change));
                break;
            case TARGET:
                beam.setTarget(loc.add(change));
                break;
        }
        return false;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        this.object = LaserObject.fromString(args[2]);
        this.target = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]);
        this.duration = ShowUtil.getInt(args[4]);
        return this;
    }
}
