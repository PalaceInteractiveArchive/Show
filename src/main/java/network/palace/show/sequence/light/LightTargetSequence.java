package network.palace.show.sequence.light;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.ShowCrystal;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;

public class LightTargetSequence extends ShowSequence {

    private Location target;
    private ShowCrystal crystal;

    public LightTargetSequence(Show show, long time, ShowCrystal crystal) {
        super(show, time);
        this.crystal = crystal;
    }

    @Override
    public boolean run() {
        if (!crystal.isSpawned()) {
            ShowUtil.logDebug(show.getName(), "EnderCrystal with ID " + crystal.getId() + " has not spawned.");
        } else {
            crystal.getCrystal().setBeamTarget(target);
        }

        return true;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        target = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]);
        return this;
    }
}
