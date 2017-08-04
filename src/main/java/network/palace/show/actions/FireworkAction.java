package network.palace.show.actions;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.utils.WorldUtil;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class FireworkAction extends ShowAction implements Listener {
    private Location loc;
    private ArrayList<FireworkEffect> effects;
    private int power;
    private Vector direction;
    private double dirPower;
    private Show show;
    private long time;

    public FireworkAction(Show show, long time) {
        super(show, time);
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
            show.addAction(explode);
        }
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        //Location loc, ArrayList<FireworkEffect> effectList, int power, Vector dir, double dirPow
        if (args.length != 7) {
            throw new ShowParseException("Invalid Firework Line Length");
        }
        // Location
        Location loc = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[2]);
        if (loc == null) {
            throw new ShowParseException("Invalid Location");
        }
        // Effect List
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
        // Power
        int power;
        try {
            power = Integer.parseInt(args[4]);
            if (power < 0 || power > 5) {
                throw new ShowParseException("Power too High/Low");
            }
        } catch (Exception e) {
            throw new ShowParseException("Invalid Power");
        }
        // Direction
        Vector dir;
        try {
            String[] coords = args[5].split(",");
            dir = new Vector(Double.parseDouble(coords[0]),
                    Double.parseDouble(coords[1]),
                    Double.parseDouble(coords[2]));
        } catch (Exception e) {
            throw new ShowParseException("Invalid Direction");
        }
        // Directional Power
        double dirPower;
        try {
            dirPower = Double.parseDouble(args[6]);
            if (dirPower < 0 || dirPower > 10) {
                throw new ShowParseException("Direction Power too High/Low");
            }
        } catch (Exception e) {
            throw new ShowParseException("Invalid Direction Power");
        }
        this.loc = loc;
        this.effects = effectList;
        this.power = power;
        this.direction = dir;
        this.dirPower = dirPower;
        return this;
    }
}