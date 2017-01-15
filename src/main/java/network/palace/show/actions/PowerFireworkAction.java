package network.palace.show.actions;

import network.palace.show.Show;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by Marc on 4/29/15
 */
public class PowerFireworkAction extends ShowAction {
    private Location loc;
    private int lifetime;
    private Vector motion;
    private List<FireworkEffect> effects;

    public PowerFireworkAction(Show show, long time, Location loc, Vector motion, List<FireworkEffect> effects) {
        super(show, time);
        this.loc = loc;
        this.motion = motion;
        this.effects = effects;
    }

    @Override
    public void play() {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        for (FireworkEffect effect : effects) {
            meta.addEffect(effect);
        }
        fw.setFireworkMeta(meta);
        fw.setVelocity(motion);
        FireworkExplodeAction action = new FireworkExplodeAction(show, time + 50, fw);
        show.actions.add(action);
    }
}
