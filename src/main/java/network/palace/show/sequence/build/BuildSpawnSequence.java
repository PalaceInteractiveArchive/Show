package network.palace.show.sequence.build;

import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.BuildObject;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BuildSpawnSequence extends ShowSequence {
    private final BuildSequence buildSequence;
    private Location spawnLocation;
    private String buildName;

    public BuildSpawnSequence(BuildSequence buildSequence, long time, String buildName) {
        super(buildSequence.getShow(), time);
        this.buildSequence = buildSequence;
        this.buildName = buildName;
    }

    @Override
    public boolean run() {
        BuildObject buildObject = buildSequence.getBuildObject(buildName);
        if (buildObject == null) {
            Bukkit.broadcast("There is no Build with ID " + buildName + ".", "palace.core.rank.mod");
        } else if (buildObject.isSpawned()) {
            Bukkit.broadcast("Build with ID " + buildObject.getId() + " has spawned already", "palace.core.rank.mod");
        } else {
            buildObject.teleport(spawnLocation);
            buildObject.spawn();
        }
        return true;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        this.spawnLocation = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]);
        return this;
    }
}
