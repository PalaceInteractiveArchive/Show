package network.palace.show.sequence;

import lombok.Getter;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;

/**
 * @author Marc
 * @since 8/2/17
 */
public abstract class ShowSequence {
    @Getter protected Show show;
    @Getter protected long time;

    public ShowSequence(Show show, long time) {
        this.show = show;
        this.time = time;
    }

    /**
     * Run the sequence
     *
     * @return true if sequence is finished, false if not
     */
    public abstract boolean run();

    public abstract ShowSequence load(String line, String... args) throws ShowParseException;
}
