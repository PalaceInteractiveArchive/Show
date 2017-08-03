package network.palace.show.actions;


import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;

public class LightningAction extends ShowAction {
    private Location loc;

    public LightningAction(Show show, long time) {
        super(show, time);
    }

    @Override
    public void play() {
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
}
