package network.palace.show.actions;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 4/29/15
 */
public class PowerFireworkAction extends ShowAction {
    private Location loc;
    private Vector motion;
    private List<FireworkEffect> effects;

    public PowerFireworkAction(Show show, long time) {
        super(show, time);
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
        FireworkExplodeAction action = new FireworkExplodeAction(show, time + 1, fw);
        show.addAction(action);
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        //Location loc, Vector motion, List<FireworkEffect> effects
        if (args.length != 5) {
            throw new ShowParseException("Invalid PowerFirework Line Length");
        }
        Location loc = WorldUtil.strToLoc(Bukkit.getWorlds().get(0).getName() + "," + args[2]);
        String[] l = args[4].split(",");
        Vector motion = new Vector(Double.parseDouble(l[0]), Double.parseDouble(l[1]), Double.parseDouble(l[2]));
        ArrayList<FireworkEffect> effectList = new ArrayList<>();
        String[] effects = args[3].split(",");
        for (String effect : effects) {
            if (show.getEffectMap().containsKey(effect)) {
                effectList.add(show.getEffectMap().get(effect));
            }
        }
        if (effectList.isEmpty()) {
            throw new ShowParseException("Invalid effects");
        }
        this.loc = loc;
        this.motion = motion;
        this.effects = effectList;
        return this;
    }
}
