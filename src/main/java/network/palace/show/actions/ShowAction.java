package network.palace.show.actions;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;

@Getter
public abstract class ShowAction {
    protected Show show;
    protected long time;

    @Setter ShowAction next = null;

    public ShowAction(Show show, long time) {
        this.show = show;
        this.time = time;
    }

    public abstract void play(CPlayer[] nearPlayers);

    public abstract ShowAction load(String line, String... args) throws ShowParseException;
}
