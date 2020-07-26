package network.palace.show.sequence.particle;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.ParticleObject;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.utils.ShowUtil;

public class ParticleDespawnSequence extends ShowSequence {
    private ParticleObject particleObject;

    public ParticleDespawnSequence(Show show, long time, ParticleObject particleObject) {
        super(show, time);
        this.particleObject = particleObject;
    }

    @Override
    public boolean run() {
        if (!particleObject.isSpawned()) {
            ShowUtil.logDebug(show.getName(), "ParticleObject with ID " + particleObject.getId() + " has not spawned.");
        } else {
            particleObject.setSpawned(false);
        }

        return true;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        return this;
    }
}
