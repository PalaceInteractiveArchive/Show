package network.palace.show.actions;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.UUID;

/**
 * Created by Marc on 1/10/15
 */
public class ParticleAction extends ShowAction {
    public Particle effect;
    public Location loc;
    public double offsetX;
    public double offsetY;
    public double offsetZ;
    public float speed;
    public int amount;
    private Show show;

    public ParticleAction(Show show, long time, Particle effect, Location loc, double offsetX, double offsetY,
                          double offsetZ, float speed, int amount) {
        super(show, time);
        this.show = show;
        this.effect = effect;
        this.loc = loc;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.amount = amount;
    }

    @Override
    public void play() {
        if (effect == null) {
            return;
        }
        for (UUID uuid : show.getNearPlayers()) {
            CPlayer tp = Core.getPlayerManager().getPlayer(uuid);
            if (tp == null) {
                continue;
            }
            if (tp.getLocation().distance(loc) > 50) {
                continue;
            }
            tp.getParticles().send(loc, effect, amount, (float) offsetX, (float) offsetY, (float) offsetZ, speed);
        }
    }
}
