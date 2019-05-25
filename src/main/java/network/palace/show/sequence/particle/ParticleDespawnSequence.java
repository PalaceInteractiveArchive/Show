package network.palace.show.sequence.particle;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.ParticleObject;
import network.palace.show.sequence.ShowSequence;
import org.bukkit.Bukkit;

public class ParticleDespawnSequence extends ShowSequence {
    private ParticleObject particleObject;

    public ParticleDespawnSequence(Show show, long time, ParticleObject particleObject) {
        super(show, time);
        this.particleObject = particleObject;
    }

    @Override
    public boolean run() {
        if (!particleObject.isSpawned()) {
            Bukkit.broadcast("ParticleObject with ID " + particleObject.getId() + " has not spawned.", "palace.core.rank.mod");
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
