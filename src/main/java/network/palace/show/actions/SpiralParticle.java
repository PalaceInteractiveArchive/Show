package network.palace.show.actions;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;
import org.bukkit.Particle;

public class SpiralParticle extends ShowAction {
    private Particle effect;
    private Location loc;
    private float yaw;
    private int duration;
    private double radius;
    private int strands;
    private int particles;
    private double curveRatio;
    private int step = 0;
    private double speed = 1;

    public SpiralParticle(Show show, long time) {
        super(show, time);
    }

    @Override
    public boolean play(CPlayer[] nearPlayers) {
        int taskID = Core.runTaskTimer(() -> {
            for (int i = 1; i <= strands; i++) {
                for (int j = 1; j <= particles; j++) {
                    float ratio = (float) j / particles;
                    double angle = curveRatio * ratio * 2 * Math.PI / strands + (2 * Math.PI * i / strands) + (Math.PI / 4) - (step / 40.0);

                    double horizontal = Math.cos(angle) * ratio * radius;
                    double vertical = Math.sin(angle) * ratio * radius;

                    double yawToRadius = Math.toRadians(yaw);

                    double x = Math.sin(yawToRadius) * horizontal;
                    double z = Math.cos(yawToRadius) * horizontal;

                    loc.add(x, vertical, z);

                    for (CPlayer tp : nearPlayers) {
                        if (tp == null || !tp.getWorld().getUID().equals(loc.getWorld().getUID()) || tp.getLocation().distance(loc) > 50)
                            continue;

                        tp.getParticles().send(loc, effect, 1, 0, 0, 0, 0);
                    }

                    loc.subtract(x, vertical, z);
                }
            }
            step += speed;
        }, 0L, 1L);
        Core.runTaskLater(() -> Core.cancelTask(taskID), duration * 20L);
        return true;
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        // 0 SpiralParticle fireworksSpark x,y,z duration yaw radius strands particles curveRatio speed
        this.effect = ShowUtil.getParticle(args[2]);
        this.loc = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]);
        this.duration = Integer.parseInt(args[4]);
        this.yaw = Float.parseFloat(args[5]);
        this.radius = Double.parseDouble(args[6]);
        this.strands = Integer.parseInt(args[7]);
        this.particles = Integer.parseInt(args[8]);
        this.curveRatio = Double.parseDouble(args[9]);
        if (args.length > 10) {
            this.speed = Double.parseDouble(args[10]);
        }
        return this;
    }
}
