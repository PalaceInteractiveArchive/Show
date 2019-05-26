package network.palace.show.sequence.light;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.ShowCrystal;
import network.palace.show.sequence.ShowSequence;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LightTargetSequence extends ShowSequence {

    private Location target;
    private ShowCrystal crystal;

    public LightTargetSequence(Show show, long time, ShowCrystal crystal, Location target) {
        super(show, time);
        this.crystal = crystal;
        this.target = target;
    }

    @Override
    public boolean run() {
        if (!crystal.isSpawned()) {
            Bukkit.broadcast("EnderCrystal with ID " + crystal.getId() + " has not spawned.", "palace.core.rank.mod");
        } else {
            crystal.getCrystal().setBeamTarget(target);
        }

        return true;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        return this;
    }
}
