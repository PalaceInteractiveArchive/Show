package network.palace.show.sequence.light;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.ShowCrystal;
import network.palace.show.sequence.ShowSequence;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderCrystal;

public class LightDespawnSequence extends ShowSequence {

    private ShowCrystal crystal;

    public LightDespawnSequence(Show show, long time, ShowCrystal crystal) {
        super(show, time);
        this.crystal = crystal;
    }

    @Override
    public boolean run() {
        if (!crystal.isSpawned()) {
            Bukkit.broadcast("EnderCrystal with ID " + crystal.getId() + " has not spawned.", "arcade.bypass");
        }
        else {
            EnderCrystal enderCrystal = crystal.getCrystal();
            enderCrystal.remove();
            crystal.setCrystal(null);
            crystal.setSpawned(false);
        }

        return true;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        return this;
    }
}
