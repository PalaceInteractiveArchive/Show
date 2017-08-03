package network.palace.show.actions;

import lombok.Getter;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;

public abstract class ShowAction {
    @Getter protected Show show;
    @Getter protected long time;

    public ShowAction(Show show, long time) {
        this.show = show;
        this.time = time;
    }

    public abstract void play();

    public abstract ShowAction load(String line, String... args) throws ShowParseException;
}
