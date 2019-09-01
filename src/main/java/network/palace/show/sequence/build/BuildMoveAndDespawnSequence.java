package network.palace.show.sequence.build;

import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.BuildObject;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class BuildMoveAndDespawnSequence extends ShowSequence {
    private final BuildSequence buildSequence;
    private BuildObject buildObject = null;

    private Vector newLocation;
    private Vector change = null;
    private String buildName;
    private double yLocation;
    private boolean above;
    private int totalTicks;
    private int ticks = 0;

    public BuildMoveAndDespawnSequence(BuildSequence buildSequence, long time, String buildName) {
        super(buildSequence.getShow(), time);
        this.buildSequence = buildSequence;
        this.buildName = buildName;
    }

    @Override
    public boolean run() {
        if (buildObject == null) {
            this.buildObject = buildSequence.getBuildObject(buildName);
            if (buildObject == null) {
                Bukkit.broadcast("There is no Build with ID " + buildName + ".", "palace.core.rank.mod");
                return true;
            }
        }
        if (!buildObject.isSpawned()) {
            Bukkit.broadcast("Build with ID " + buildObject.getId() + " has not spawned.", "palace.core.rank.mod");
            return true;
        }
        if (change == null) {
            Location current = buildObject.getLocation();
            this.change = new Vector(newLocation.getX() - current.getX(), newLocation.getY() - current.getY(), newLocation.getZ() - current.getZ()).divide(new Vector(totalTicks, totalTicks, totalTicks));
        }
        buildObject.move(change, yLocation, above);
        boolean finished = ticks++ >= totalTicks;
        if (finished) buildObject.despawn();
        return finished;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        Double[] doubles = WorldUtil.strToDoubleList(show.getWorld().getName() + "," + args[3]);
        this.newLocation = new Vector(doubles[0], doubles[1], doubles[2]);
        this.yLocation = Double.parseDouble(args[4]);
        this.above = Boolean.parseBoolean(args[5]);
        this.totalTicks = Integer.parseInt(args[6]);
        return this;
    }
}
