package network.palace.show.sequence.laser;

import lombok.Getter;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.sequence.handlers.SequenceState;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Squid;

import java.io.*;
import java.util.HashSet;

/**
 * @author Marc
 * @since 8/2/17
 */
public class LaserSequence extends ShowSequence {
    private SequenceState state;
    @Getter private long startTime;
    private HashSet<ShowSequence> sequences;
    @Getter private boolean spawned = false;
    private Location sourceSpawn;
    private Location targetSpawn;
    @Getter private Guardian source;
    @Getter private Squid target;

    public LaserSequence(Show show, long time) {
        super(show, time);
    }

    @Override
    public boolean run() {
        if (sequences != null) {
            ShowUtil.runSequences(sequences, startTime);
            return this.sequences.isEmpty();
        }
        return false;
    }

    private void spawn() {
        if (spawned) return;
        World world = show.getWorld();
        source = world.spawn(sourceSpawn, Guardian.class);
        source.addPotionEffect(ShowUtil.getInvisibility());
        target = world.spawn(targetSpawn, Squid.class);
        target.addPotionEffect(ShowUtil.getInvisibility());
        source.setTarget(target);
        source.setAI(false);
        spawned = true;
    }

    private void despawn() {
        if (!spawned || source == null || target == null) return;
        source.remove();
        target.remove();
        spawned = false;
    }

    @Override
    public ShowSequence load(String line, String... showArgs) throws ShowParseException {
        sourceSpawn = WorldUtil.strToLoc(show.getWorld().getName() + "," + showArgs[4]);
        File file = new File("plugins/Show/shows/sequences/lasers/" + showArgs[3] + ".sequence");
        if (!file.exists()) {
            throw new ShowParseException("Could not find Laser sequence file " + showArgs[3]);
        }
        HashSet<ShowSequence> sequences = new HashSet<>();
        String strLine = "";
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            // Parse Lines
            while ((strLine = br.readLine()) != null) {
                if (strLine.length() == 0 || strLine.startsWith("#")) continue;
                String[] args = strLine.split("\\s+");
                if (args.length < 2) {
                    System.out.println("Invalid Show Line [" + strLine + "]");
                    continue;
                }
                // Make sure first line is the Sequence line
                if (!args[0].equalsIgnoreCase("Sequence") && state == null) {
                    throw new ShowParseException("First line isn't Sequence definition");
                }
                if (args[0].equalsIgnoreCase("Sequence") && state != null) {
                    if (!args[1].equalsIgnoreCase("Laser")) {
                        throw new ShowParseException("This isn't a Laser file!");
                    }
                    state = SequenceState.fromString(args[2]);
                    if (state == null) {
                        throw new ShowParseException("Unknown Sequence State " + args[2]);
                    }
                    continue;
                }
                String[] timeToks = args[0].split("_");
                long time = 0;
                for (String timeStr : timeToks) {
                    time += (long) (Double.parseDouble(timeStr) * 1000);
                }
                if (args[1].equalsIgnoreCase("Move")) {
                    LaserMoveSequence sq = new LaserMoveSequence(show, time, this);
                    sequences.add(sq.load(strLine, args));
                    continue;
                }
            }
        } catch (ShowParseException e) {
            throw new ShowParseException("Error while parsing Sequence " + showArgs[3] + ": " + e.getReason());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShowParseException("Error while parsing Sequence " + showArgs[3] + " on Line [" + strLine + "]");
        }
        this.sequences = sequences;
        startTime = System.currentTimeMillis();
        return this;
    }
}
