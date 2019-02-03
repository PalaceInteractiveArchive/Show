package network.palace.show.sequence.light;

import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.ShowCrystal;
import network.palace.show.sequence.ShowSequence;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.metadata.FixedMetadataValue;

public class LightSpawnSequence extends ShowSequence {

    private Location spawnLocation;
    private Location targetLocation;
    private ShowCrystal crystal;

    public LightSpawnSequence(Show show, long time, ShowCrystal crystal, Location spawnLocation, Location targetLocation) {
        super(show, time);
        this.crystal = crystal;
        this.spawnLocation = spawnLocation;
        this.targetLocation = targetLocation;
    }

    @Override
    public boolean run() {
        if (crystal.isSpawned()) {
            Bukkit.broadcast("EnderCrystal with ID " + crystal.getId() + " has spawned already", "arcade.bypass");
            return true;
        }

        crystal.setCrystal(spawnLocation.getWorld().spawn(spawnLocation, EnderCrystal.class, ec -> {
            ec.setBeamTarget(targetLocation);
            ec.setCustomName(crystal.getId());
            ec.setMetadata("show", new FixedMetadataValue(ShowPlugin.getInstance(), true));
        }));
        crystal.setSpawned(true);
        return true;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        return this;
    }
}
