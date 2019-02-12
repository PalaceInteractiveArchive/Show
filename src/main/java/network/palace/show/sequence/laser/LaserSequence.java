package network.palace.show.sequence.laser;

import lombok.Getter;
import network.palace.show.Show;
import network.palace.show.beam.beam.Beam;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.sequence.handlers.SequenceState;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;

import java.io.*;
import java.util.HashSet;

/**
 * @author Marc
 * @since 8/2/17
 */
public class LaserSequence extends ShowSequence {
    protected SequenceState state = null;
    @Getter private long startTime;
    private HashSet<ShowSequence> sequences;
    @Getter private Beam beam = null;
    private Location relativeBaseSpawn = null;
    private Location relativeTargetSpawn = null;

    public LaserSequence(Show show, long time) {
        super(show, time);
    }

    @Override
    public boolean run() {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        if (sequences != null) {
            ShowUtil.runSequences(sequences, startTime);
            return sequences.isEmpty();
        }
        return false;
    }

    protected void spawn(Location loc) throws ShowParseException {
        spawn(loc, loc);
    }

    protected void spawn(Location source, Location target) throws ShowParseException {
        if (isSpawned()) return;
        if (state.equals(SequenceState.RELATIVE)) {
            if (relativeBaseSpawn == null) {
                beam = new Beam(target, source);
            } else if (source != null) {
                beam = new Beam(relativeBaseSpawn, source);
            } else if (relativeTargetSpawn == null) {
                beam = new Beam(relativeBaseSpawn, relativeBaseSpawn);
            } else {
                beam = new Beam(relativeBaseSpawn, relativeTargetSpawn);
            }
        } else {
            if (source == null && target == null) {
                throw new ShowParseException("Sequence is ACTUAL type and no spawn locations were provided!");
            }
            beam = new Beam(target, source);
        }
        beam.start();
    }

    public void despawn() {
        if (!isSpawned() || beam == null) return;
        beam.stop();
    }

    public boolean isSpawned() {
        return beam != null && beam.isActive();
    }

    @Override
    public ShowSequence load(String line, String... showArgs) throws ShowParseException {
        File file = new File("plugins/Show/sequences/lasers/" + showArgs[3] + ".sequence");
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
                if (args[0].equalsIgnoreCase("Sequence") && state == null) {
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
                if (args[1].equalsIgnoreCase("Spawn")) {
                    LaserSpawnSequence sq = new LaserSpawnSequence(show, time, this);
                    sequences.add(sq.load(strLine, args));
                    continue;
                }
                if (args[1].equalsIgnoreCase("Move")) {
                    LaserMoveSequence sq = new LaserMoveSequence(show, time, this);
                    sequences.add(sq.load(strLine, args));
                    continue;
                }
                if (args[1].equalsIgnoreCase("Despawn")) {
                    LaserDespawnSequence sq = new LaserDespawnSequence(show, time, this);
                    sequences.add(sq.load(strLine, args));
                }
            }
            br.close();
            in.close();
            fstream.close();
        } catch (ShowParseException e) {
            throw new ShowParseException("Error while parsing Sequence " + showArgs[3] + ": " + e.getReason());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShowParseException("Error while parsing Sequence " + showArgs[3] + " on Line [" + strLine + "]");
        }
        if (showArgs.length > 4) {
            relativeBaseSpawn = WorldUtil.strToLoc(show.getWorld().getName() + "," + showArgs[4]);
            if (showArgs.length > 5) {
                relativeTargetSpawn = WorldUtil.strToLoc(show.getWorld().getName() + "," + showArgs[5]);
            }
        }
        this.sequences = sequences;
        return this;
    }
}
