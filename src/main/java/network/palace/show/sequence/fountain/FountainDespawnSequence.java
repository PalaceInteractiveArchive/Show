package network.palace.show.sequence.fountain;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.sequence.ShowSequence;

/**
 * @author Marc
 * @since 8/2/17
 */
public class FountainDespawnSequence extends ShowSequence {
    private final FountainSequence parent;

    public FountainDespawnSequence(Show show, long time, FountainSequence parent) {
        super(show, time);
        this.parent = parent;
    }

    @Override
    public boolean run() {
        if (!parent.isSpawned()) {
            return true;
        }
        parent.despawn();
        return true;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        return this;
    }
}
