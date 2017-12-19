package network.palace.show.sequence.fountain;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class FountainMoveSequence extends ShowSequence {
    private final FountainSequence parent;
    private Vector target = null;
    private Vector change = null;
    private int duration;
    private long startTime = 0;

    public FountainMoveSequence(Show show, long time, FountainSequence parent) {
        super(show, time);
        this.parent = parent;
    }

    @Override
    public boolean run() {
        if (!parent.isSpawned()) {
            return true;
        }
        if (startTime == 0) {
            Vector current = parent.direction.clone();
            change = target.subtract(current);
            if (duration != 0) {
                change.divide(new Vector(duration, duration, duration));
            }
            startTime = System.currentTimeMillis();
        }
        parent.direction.add(change);
        return (startTime) + (duration * 50) <= System.currentTimeMillis();
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        Location loc = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[2]);
        if (loc == null) {
            throw new ShowParseException("Error parsing direction values!");
        }
        this.target = loc.toVector();
        this.duration = Integer.parseInt(args[3]);
        return this;
    }
}
