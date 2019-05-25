package network.palace.show.handlers;

import lombok.Getter;
import lombok.Setter;
import network.palace.show.Show;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

@Getter
public abstract class ParticleObject {
    protected String id;
    @Setter protected Particle particle;
    protected Location loc;
    protected Show show;
    protected int count, frequency, ticks = 0;
    protected float offsetX, offsetY, offsetZ, speed;
    @Setter private boolean spawned = false;

    public ParticleObject(String id, Particle particle, Show show, int count, float offsetX, float offsetY, float offsetZ, float speed, int frequency) {
        this.id = id;
        this.particle = particle;
        this.show = show;
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.frequency = frequency;
    }

    public void move(Vector vector) {
        loc.add(vector);
    }

    public void teleport(Location loc) {
        this.loc = loc;
    }

    public void adjustOffset(float dx, float dy, float dz) {
        this.offsetX += dx;
        this.offsetY += dy;
        this.offsetZ += dz;
    }

    public void adjustCount(int d) {
        this.count += d;
    }

    public void adjustFrequency(int d) {
        this.frequency += d;
    }

    public void adjustSpeed(float d) {
        this.speed += d;
    }

    public abstract void render();

    public Location getLocation() {
        return loc.clone();
    }

    public abstract ParticleObject duplicate();
}
