package network.palace.show.actions;

import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;
import org.bukkit.Particle;

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

    public ParticleAction(Show show, long time, Particle effect, Location loc, double offsetX, double offsetY, double offsetZ, float speed, int amount) {
        super(show, time);
        this.effect = effect;
        this.loc = loc;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.amount = amount;
    }

    @Override
    public void play(CPlayer[] nearPlayers) {
        if (effect == null) return;
        for (CPlayer tp : nearPlayers) {
            if (tp == null) continue;
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

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new ParticleAction(show, time, effect, loc, offsetX, offsetY, offsetZ, speed, amount);
    }
}
