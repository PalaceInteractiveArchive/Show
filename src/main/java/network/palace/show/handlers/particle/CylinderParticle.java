package network.palace.show.handlers.particle;

import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.handlers.ParticleObject;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;

import java.util.UUID;

@Getter
public class CylinderParticle extends ParticleObject {
    protected double radius;
    protected double distance;
    protected final Shape shape;
    protected final BlockFace face;

    public CylinderParticle(String id, Particle particle, Show show, int count, float offsetX, float offsetY, float offsetZ, float speed, int frequency, double radius, double distance, Shape shape, BlockFace face) {
        super(id, particle, show, count, offsetX, offsetY, offsetZ, speed, frequency);
        this.radius = radius;
        this.distance = Math.toRadians(distance);
        this.shape = shape;
        this.face = face;
    }


    public void adjustRadius(double d) {
        radius += d;
    }

    public void adjustDistance(double d) {
        distance += d;
    }

    @Override
    public void render() {
        if (!isSpawned()) return;
        if (ticks++ % (20 / frequency) != 0) return;
        if (shape.equals(Shape.FULL)) {
            renderParticles(0, 2 * Math.PI);
        } else {
            switch (face) {
                case NORTH: {
                    renderParticles(Math.PI / 2, (3 * Math.PI) / 2);
                    break;
                }
                case EAST: {
                    renderParticles(Math.PI, 2 * Math.PI);
                    break;
                }
                case SOUTH: {
                    renderParticles((3 * Math.PI) / 2, (5 * Math.PI) / 2);
                    break;
                }
                case WEST: {
                    renderParticles(2 * Math.PI, 3 * Math.PI);
                    break;
                }
            }
        }
    }

    /**
     * Render a cylinder of particles starting at 'startingDistance' and ending at 'maxDistance' (in radians)
     *
     * @param startingDistance the starting 'angle' in radians
     * @param maxDistance      the ending 'angle' in radians
     */
    private void renderParticles(double startingDistance, double maxDistance) {
        while (startingDistance < maxDistance) {
            double x = (Math.sin(startingDistance) * radius) + loc.getX();
            double z = (Math.cos(startingDistance) * radius) + loc.getZ();

            for (UUID uuid : show.getNearPlayers()) {
                CPlayer tp = Core.getPlayerManager().getPlayer(uuid);
                if (tp == null) continue;
                tp.getParticles().send(new Location(tp.getWorld(), x, loc.getY(), z), particle, count, offsetX, offsetY, offsetZ, speed);
            }

            startingDistance += distance;
        }
    }

    @Override
    public ParticleObject duplicate() {
        ParticleObject obj = new CylinderParticle(id, particle, show, count, offsetX, offsetY, offsetZ, speed, frequency, radius, distance, shape, face);
        obj.teleport(loc);
        obj.setSpawned(isSpawned());
        return obj;
    }

    public enum Shape {
        FULL, HALF
    }
}
