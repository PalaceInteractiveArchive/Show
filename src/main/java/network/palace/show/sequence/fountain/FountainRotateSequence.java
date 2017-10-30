package network.palace.show.sequence.fountain;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.sequence.ShowSequence;
import org.bukkit.util.Vector;

public class FountainRotateSequence extends ShowSequence {
    private final FountainSequence parent;
    private int degrees;
    private int currentDegrees;
    private double speed;
//    private int change;
    private double radius;
    private long startTime = 0;

    public FountainRotateSequence(Show show, long time, FountainSequence parent) {
        super(show, time);
        this.parent = parent;
    }

    @Override
    public boolean run() {
        if (!parent.isSpawned()) {
            return true;
        }
        Vector dir = parent.direction;
        if (startTime == 0) {
            radius = Math.sqrt(Math.pow(dir.getX(), 2) + Math.pow(dir.getZ(), 2));

            startTime = System.currentTimeMillis();
        }
        currentDegrees += speed;

        double rad = Math.toRadians(currentDegrees);

        double x = radius * Math.cos(rad);
        double z = radius * Math.sin(rad);

        dir.setX(x);
        dir.setZ(z);

        return currentDegrees >= degrees;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        this.degrees = Integer.parseInt(args[2]);
        this.speed = Double.parseDouble(args[3]);
        return this;
    }
}
