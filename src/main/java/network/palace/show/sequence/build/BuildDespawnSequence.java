package network.palace.show.sequence.build;

import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.BuildObject;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.utils.ShowUtil;

public class BuildDespawnSequence extends ShowSequence {
    private final BuildSequence buildSequence;
    private String buildName;

    public BuildDespawnSequence(BuildSequence buildSequence, long time, String buildName) {
        super(buildSequence.getShow(), time);
        this.buildSequence = buildSequence;
        this.buildName = buildName;
    }

    @Override
    public boolean run() {
        BuildObject buildObject = buildSequence.getBuildObject(buildName);
        if (buildObject == null) {
            ShowUtil.logDebug(show.getName(), "There is no Build with ID " + buildName + ".");
        } else if (!buildObject.isSpawned()) {
            ShowUtil.logDebug(show.getName(), "Build with ID " + buildObject.getId() + " has not spawned.");
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
