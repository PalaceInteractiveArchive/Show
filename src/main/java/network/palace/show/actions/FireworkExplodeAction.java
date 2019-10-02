package network.palace.show.actions;

import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import org.bukkit.entity.Firework;

/**
 * Created by Marc on 7/1/15
 */
public class FireworkExplodeAction extends ShowAction {
    private final Firework fw;

    public FireworkExplodeAction(Show show, long time, Firework fw) {
        super(show, time);
        this.fw = fw;
    }

    @Override
    public void play(CPlayer[] nearPlayers) {
        fw.detonate();
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        return this;
    }
}