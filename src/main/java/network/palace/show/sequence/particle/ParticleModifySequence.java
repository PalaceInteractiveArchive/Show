package network.palace.show.sequence.particle;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.ParticleObject;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.utils.ShowUtil;
import org.bukkit.Particle;

public class ParticleModifySequence extends ShowSequence {
    private ParticleObject particleObject;
    private Particle particle;
    private float offsetX, offsetY, offsetZ, speed;
    private int count, frequency, totalTicks;

    private float dX, dY, dZ, dSpeed;
    private int dCount, dFrequency, ticks = -1;

    private int countTick, frequencyTick;

    public ParticleModifySequence(Show show, long time, ParticleObject particleObject) {
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
            dX = (offsetX - particleObject.getOffsetX()) / totalTicks;
            dY = (offsetY - particleObject.getOffsetY()) / totalTicks;
            dZ = (offsetZ - particleObject.getOffsetZ()) / totalTicks;
            dSpeed = (speed - particleObject.getSpeed()) / totalTicks;
            if (!particle.equals(particleObject.getParticle())) {
                particleObject.setParticle(particle);
            }

            dCount = (count - particleObject.getCount()) / totalTicks;
            dFrequency = (frequency - particleObject.getFrequency()) / totalTicks;

            double decimalDCount = (count - particleObject.getCount()) / (double) totalTicks;
            double decimalDFrequency = (frequency - particleObject.getFrequency()) / (double) totalTicks;

            if ((double) dCount != decimalDCount) {
                countTick = (int) Math.floor(1 / decimalDCount);
            } else {
                countTick = 0;
            }

            if ((double) dFrequency != decimalDFrequency) {
                frequencyTick = (int) Math.floor(1 / decimalDFrequency);
            } else {
                frequencyTick = 0;
            }
        }
        particleObject.adjustOffset(dX, dY, dZ);
        particleObject.adjustCount(dCount);
        particleObject.adjustSpeed(dSpeed);

        if (countTick != 0) {
            if (particleObject.getCount() != count && (ticks % countTick == 0)) {
                particleObject.adjustCount(particleObject.getCount() < count ? 1 : -1);
            }
        } else {
            particleObject.adjustCount(dCount);
        }

        if (frequencyTick != 0) {
            if (particleObject.getFrequency() != frequency && (ticks % frequencyTick == 0)) {
                particleObject.adjustFrequency(particleObject.getFrequency() < frequency ? 1 : -1);
            }
        } else {
            particleObject.adjustFrequency(dFrequency);
        }

        return ticks++ >= totalTicks;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        String[] offset = args[4].split(",");
        this.particle = ShowUtil.getParticle(args[3]);
        this.offsetX = Float.parseFloat(offset[0]);
        this.offsetY = Float.parseFloat(offset[1]);
        this.offsetZ = Float.parseFloat(offset[2]);
        this.count = Integer.parseInt(args[5]);
        this.speed = Float.parseFloat(args[6]);
        this.frequency = Integer.parseInt(args[7]);
        this.totalTicks = Integer.parseInt(args[8]);
        return this;
    }
}
