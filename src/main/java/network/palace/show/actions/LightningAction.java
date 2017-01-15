package network.palace.show.actions;


import network.palace.show.Show;
import org.bukkit.Location;

public class LightningAction extends ShowAction {
    public Location loc;

    public LightningAction(Show show, long time, Location loc) {
        super(show, time);
        this.loc = loc;
    }

    @Override
    public void play() {
        loc.getWorld().strikeLightningEffect(loc);
    }
}
