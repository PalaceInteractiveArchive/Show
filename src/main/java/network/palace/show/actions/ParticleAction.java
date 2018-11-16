package network.palace.show.actions;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.UUID;

/**
 * Created by Marc on 1/10/15
 */
public class ParticleAction extends ShowAction {
    private Particle effect;
    private Location loc;
    private double offsetX;
    private double offsetY;
    private double offsetZ;
    private float speed;
    private int amount;

    public ParticleAction(Show show, long time) {
        super(show, time);
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
            tp.getParticles().send(loc, effect, amount, (float) offsetX, (float) offsetY, (float) offsetZ, speed);
        }
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        // 0 Particle type x,y,z oX oY oZ speed amount
        this.effect = ShowUtil.getParticle(args[2]);
        this.loc = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]);
        this.offsetX = Float.parseFloat(args[4]);
        this.offsetY = Float.parseFloat(args[5]);
        this.offsetZ = Float.parseFloat(args[6]);
        this.speed = Float.parseFloat(args[7]);
        this.amount = Integer.parseInt(args[8]);
        return this;
    }
}
