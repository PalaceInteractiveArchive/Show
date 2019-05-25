package network.palace.show.sequence.particle;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.ParticleObject;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class ParticleSpawnSequence extends ShowSequence {
    private Location spawnLocation;
    private ParticleObject particleObject;

    public ParticleSpawnSequence(Show show, long time, ParticleObject particleObject) {
        super(show, time);
        this.particleObject = particleObject;
    }

    @Override
    public boolean run() {
        if (particleObject.isSpawned()) {
            Bukkit.broadcast("ParticleObject with ID " + particleObject.getId() + " has spawned already", "palace.core.rank.mod");
            return true;
        }

        particleObject.teleport(spawnLocation);
        particleObject.setSpawned(true);
        return true;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        this.spawnLocation = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]);
        return this;
    }
}
