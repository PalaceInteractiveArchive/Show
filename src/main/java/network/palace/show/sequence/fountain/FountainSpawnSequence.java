package network.palace.show.sequence.fountain;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class FountainSpawnSequence extends ShowSequence {
    private final FountainSequence parent;
    private Vector dir = null;

    public FountainSpawnSequence(Show show, long time, FountainSequence parent) {
        super(show, time);
        this.parent = parent;
    }

    @Override
    public boolean run() {
        if (parent.isSpawned()) {
            return true;
        }
        if (dir != null) {
            parent.direction = dir;
        }
        parent.spawn();
        return true;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        if (args.length > 2) {
            Location loc = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[2]);
            if (loc != null) this.dir = loc.toVector();
        }
        return this;
    }
}
