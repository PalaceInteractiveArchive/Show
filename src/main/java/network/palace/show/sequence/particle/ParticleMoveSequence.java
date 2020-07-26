package network.palace.show.sequence.particle;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.ParticleObject;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class ParticleMoveSequence extends ShowSequence {
    private Vector newLocation;
    private Vector change = null;
    private ParticleObject particleObject;
    private int totalTicks;
    private int ticks = 0;

    public ParticleMoveSequence(Show show, long time, ParticleObject particleObject) {
        super(show, time);
        this.particleObject = particleObject;
    }

    @Override
    public boolean run() {
        if (!particleObject.isSpawned()) {
            ShowUtil.logDebug(show.getName(), "ParticleObject with ID " + particleObject.getId() + " has not spawned.");
            return true;
        }
        if (change == null) {
            Location current = particleObject.getLoc();
            this.change = new Vector(newLocation.getX() - current.getX(), newLocation.getY() - current.getY(), newLocation.getZ() - current.getZ()).divide(new Vector(totalTicks, totalTicks, totalTicks));
        }
        particleObject.move(change);
        return ticks++ >= totalTicks;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        Double[] doubles = WorldUtil.strToDoubleList(show.getWorld().getName() + "," + args[3]);
        this.newLocation = new Vector(doubles[0], doubles[1], doubles[2]);
        this.totalTicks = Integer.parseInt(args[4]);
        return this;
    }
}
