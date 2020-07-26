package network.palace.show.sequence.particle.cylinder;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.particle.CylinderParticle;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.utils.ShowUtil;

public class ParticleCylinderSequence extends ShowSequence {
    private CylinderParticle particleObject;
    private double radius, distance;
    private int totalTicks;

    private double dRadius, dDistance;
    private int ticks = -1;

    public ParticleCylinderSequence(Show show, long time, CylinderParticle particleObject) {
        super(show, time);
        this.particleObject = particleObject;
    }

    @Override
    public boolean run() {
        if (!particleObject.isSpawned()) {
            ShowUtil.logDebug(show.getName(), "ParticleObject with ID " + particleObject.getId() + " has not spawned.");
            return true;
        }
        if (ticks == -1) {
            ticks = 0;
            dRadius = (radius - particleObject.getRadius()) / totalTicks;
            dDistance = (distance - particleObject.getDistance()) / totalTicks;
        }
        particleObject.adjustRadius(dRadius);
        particleObject.adjustDistance(dDistance);
        return ticks++ >= totalTicks;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        this.radius = Double.parseDouble(args[3]);
        this.distance = Math.toRadians(Double.parseDouble(args[4]));
        this.totalTicks = Integer.parseInt(args[5]);
        return this;
    }
}
