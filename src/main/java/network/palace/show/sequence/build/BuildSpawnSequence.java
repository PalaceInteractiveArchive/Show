package network.palace.show.sequence.build;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.BuildObject;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BuildSpawnSequence extends ShowSequence {
    private Location spawnLocation;
    private BuildObject buildObject;

    public BuildSpawnSequence(Show show, long time, BuildObject buildObject) {
        super(show, time);
        this.buildObject = buildObject;
    }

    @Override
    public boolean run() {
        if (buildObject.isSpawned()) {
            Bukkit.broadcast("ParticleObject with ID " + buildObject.getId() + " has spawned already", "palace.core.rank.mod");
            return true;
        }
        buildObject.teleport(spawnLocation);
        buildObject.spawn();
        return true;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        this.spawnLocation = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]);
        return this;
    }
}
