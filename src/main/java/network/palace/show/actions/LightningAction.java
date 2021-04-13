package network.palace.show.actions;

import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;

public class LightningAction extends ShowAction {
    private Location loc;

    public LightningAction(Show show, long time) {
        super(show, time);
    }

    public LightningAction(Show show, long time, Location loc) {
        super(show, time);
        this.loc = loc;
    }

    @Override
    public void play(CPlayer[] nearPlayers) {
        loc.getWorld().strikeLightningEffect(loc);
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        this.loc = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[2]);
        if (loc == null) {
            throw new ShowParseException("Invalid Location");
        }
        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new LightningAction(show, time, loc);
    }
}
