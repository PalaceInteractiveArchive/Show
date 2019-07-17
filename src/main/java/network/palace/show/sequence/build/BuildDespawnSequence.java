package network.palace.show.sequence.build;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.BuildObject;
import network.palace.show.sequence.ShowSequence;
import org.bukkit.Bukkit;

public class BuildDespawnSequence extends ShowSequence {
    private BuildObject buildObject;

    public BuildDespawnSequence(Show show, long time, BuildObject buildObject) {
        super(show, time);
        this.buildObject = buildObject;
    }

    @Override
    public boolean run() {
        if (!buildObject.isSpawned()) {
            Bukkit.broadcast("Build with ID " + buildObject.getId() + " has not spawned.", "palace.core.rank.mod");
        } else {
            buildObject.despawn();
        }

        return true;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        return this;
    }
}
