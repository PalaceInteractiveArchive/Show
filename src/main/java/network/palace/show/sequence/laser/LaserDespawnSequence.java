package network.palace.show.sequence.laser;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.sequence.ShowSequence;

/**
 * @author Marc
 * @since 8/2/17
 */
public class LaserDespawnSequence extends ShowSequence {
    private final LaserSequence parent;

    public LaserDespawnSequence(Show show, long time, LaserSequence parent) {
        super(show, time);
        this.parent = parent;
    }

    @Override
    public boolean run() {
        if (parent.isSpawned()) parent.despawn();
        return true;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        return this;
    }
}
