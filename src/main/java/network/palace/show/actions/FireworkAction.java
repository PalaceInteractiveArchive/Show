package network.palace.show.actions;

import network.palace.show.Show;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class FireworkAction extends ShowAction implements Listener {
    public Location loc;
    public ArrayList<FireworkEffect> effects;
    public int power;
    public Vector direction;
    public double dirPower;
    private Show show;
    private long time;

    public FireworkAction(Show show, long time, Location loc, ArrayList<FireworkEffect> effectList, int power, Vector dir,
                          double dirPow) {
        super(show, time);
        this.show = show;
        this.time = time;
        this.loc = loc;
        effects = effectList;
        this.power = power;
        direction = dir;
        dirPower = dirPow;
    }

    @Override
    public void play() {
        try {
            playFirework();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playFirework() throws Exception {
        final Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta data = fw.getFireworkMeta();
        data.clearEffects();
        // Add effects
        for (FireworkEffect effect : effects) {
            data.addEffect(effect);
        }
        // Instant
        boolean instaburst;
        if (power == 0) {
            instaburst = true;
        } else {
            instaburst = false;
            data.setPower(Math.min(1, power));
        }
        // Set data
        fw.setFireworkMeta(data);
        // Velocity
        if (direction.length() > 0) {
            fw.setVelocity(direction.normalize().multiply(dirPower * 0.05));
        }
        if (instaburst) {
            FireworkExplodeAction explode = new FireworkExplodeAction(show, time + 50, fw);
            show.actions.add(explode);
        }
    }
}