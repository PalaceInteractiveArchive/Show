package network.palace.show.sequence.fountain;

import lombok.Getter;
import network.palace.show.FountainManager;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.HashSet;

/**
 * @author Marc
 * @since 8/2/17
 */
public class FountainSequence extends ShowSequence {
    @Getter private long startTime;
    private HashSet<ShowSequence> sequences;
    protected boolean running = false;
    protected MaterialData data = new MaterialData(Material.STAINED_GLASS, (byte) 3);
    protected Vector direction;
    protected Location spawn = null;
    private int ticks = 0;

    public FountainSequence(Show show, long time) {
        super(show, time);
    }

    @Override
    public boolean run() {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        if (running && ticks % 2 == 0) launch();
        ticks++;
        if (sequences != null) {
            ShowUtil.runSequences(sequences, startTime);
            return sequences.isEmpty();
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    private void launch() {
        FallingBlock b = spawn.getWorld().spawnFallingBlock(spawn, data);
        b.setVelocity(direction);
        FountainManager.blocks.add(b.getUniqueId());
    }

    protected void spawn() {
        running = true;
    }

    public void despawn() {
        running = false;
    }

    public boolean isSpawned() {
        return running;
    }

    @Override
    public ShowSequence load(String line, String... showArgs) throws ShowParseException {
        File file = new File("plugins/Show/sequences/fountains/" + showArgs[3] + ".sequence");
        if (!file.exists()) {
            throw new ShowParseException("Could not find Fountain sequence file " + showArgs[3]);
        }
        HashSet<ShowSequence> sequences = new HashSet<>();
        String strLine = "";
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            boolean first = true;
            // Parse Lines
            while ((strLine = br.readLine()) != null) {
                if (strLine.length() == 0 || strLine.startsWith("#")) continue;
                String[] args = strLine.split("\\s+");
                if (args.length < 2) {
                    System.out.println("Invalid Show Line [" + strLine + "]");
                    continue;
                }
                // Make sure first line is the Sequence line
                if (!args[0].equalsIgnoreCase("Sequence") && first) {
                    throw new ShowParseException("First line isn't Sequence definition");
                }
                if (args[0].equalsIgnoreCase("Sequence") && first) {
                    if (!args[1].equalsIgnoreCase("Fountain")) {
                        throw new ShowParseException("This isn't a Fountain file!");
                    }
                    first = false;
                    continue;
                }
                String[] timeToks = args[0].split("_");
                long time = 0;
                for (String timeStr : timeToks) {
                    time += (long) (Double.parseDouble(timeStr) * 1000);
                }
                //TODO make durations doubles
                if (args[1].equalsIgnoreCase("Spawn")) {
                    FountainSpawnSequence sq = new FountainSpawnSequence(show, time, this);
                    sequences.add(sq.load(strLine, args));
                    continue;
                }
                if (args[1].equalsIgnoreCase("Move")) {
                    FountainMoveSequence sq = new FountainMoveSequence(show, time, this);
                    sequences.add(sq.load(strLine, args));
                    continue;
                }
                if (args[1].equalsIgnoreCase("Block")) {
                    FountainBlockSequence sq = new FountainBlockSequence(show, time, this);
                    sequences.add(sq.load(strLine, args));
                    continue;
                }
                if (args[1].equalsIgnoreCase("Rotate")) {
                    FountainRotateSequence sq = new FountainRotateSequence(show, time, this);
                    sequences.add(sq.load(strLine, args));
                    continue;
                }
                if (args[1].equalsIgnoreCase("Despawn")) {
                    FountainDespawnSequence sq = new FountainDespawnSequence(show, time, this);
                    sequences.add(sq.load(strLine, args));
                }
            }
        } catch (ShowParseException e) {
            throw new ShowParseException("Error while parsing Sequence " + showArgs[3] + ": " + e.getReason());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShowParseException("Error while parsing Sequence " + showArgs[3] + " on Line [" + strLine + "]");
        }
        if (showArgs.length > 4) {
            spawn = WorldUtil.strToLoc(show.getWorld().getName() + "," + showArgs[4]);
        }
        this.sequences = sequences;
        return this;
    }
}
