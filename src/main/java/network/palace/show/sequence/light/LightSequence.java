package network.palace.show.sequence.light;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import lombok.Getter;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.ShowCrystal;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.util.Vector;

public class LightSequence extends ShowSequence {

    @Getter
    private long startTime;
    private HashSet<ShowSequence> sequences;
    private Map<String, ShowCrystal> crystalMap = new HashMap<>();
    private boolean isLightSequence = false;

    public LightSequence(Show show, long time) {
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

    @Override
    public ShowSequence load(String line, String... showArgs) throws ShowParseException {
        File file = new File(ShowPlugin.getInstance().getDataFolder(), "sequences/lights/" + showArgs[3] + ".sequence");
        if (!file.exists()) {
            throw new ShowParseException("Could not find Light sequence file " + showArgs[3]);
        }

        HashSet<ShowSequence> sequences = new HashSet<>();
        String strLine = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((strLine = br.readLine()) != null) {
                if (strLine.length() == 0 || strLine.startsWith("#")) continue;

                String[] args = strLine.split("\\s+");
                if (args.length < 2) {
                    ShowPlugin.getInstance().getLogger().warning("Invalid Sequence Line:" + showArgs[3] + " [" + strLine + "]");
                    continue;
                }

                if (!isLightSequence) {
                    if (!args[0].equalsIgnoreCase("Sequence")) {
                        throw new ShowParseException("First line isn't a Sequence definition");
                    }

                    if (!args[1].equalsIgnoreCase("Light")) {
                        throw new ShowParseException("This isn't a Light file!");
                    }

                    isLightSequence = true;
                    continue;
                }

                if (args[0].equals("EnderCrystal")) {
                    String id = args[1];
                    ShowCrystal showCrystal = new ShowCrystal(id);
                    crystalMap.put(id, showCrystal);
                    continue;
                }

                String[] timeToks = args[0].split("_");
                long time = 0;
                for (String timeStr : timeToks) {
                    time += (long) (Double.parseDouble(timeStr) * 1000);
                }

                ShowCrystal crystal = crystalMap.get(args[1]);
                switch (args[2].toLowerCase()) {
                    case "despawn":
                        LightDespawnSequence despawn = new LightDespawnSequence(show, time, crystal);
                        sequences.add(despawn.load(strLine, args));
                        break;
                    case "move":
                        Double[] doubles = WorldUtil.strToDoubleList(show.getWorld().getName() + "," + args[3]);
                        LightMoveSequence move = new LightMoveSequence(show, time, crystal, new Vector(doubles[0], doubles[1], doubles[2]), Double.parseDouble(args[4]));
                        sequences.add(move.load(strLine, args));
                        break;
                    case "target":
                        LightTargetSequence target = new LightTargetSequence(show, time, crystal, WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]));
                        sequences.add(target.load(strLine, args));
                        break;
                    case "spawn":
                        LightSpawnSequence spawn = new LightSpawnSequence(show, time, crystal, WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]), WorldUtil.strToLoc(show.getWorld().getName() + "," + args[4]));
                        sequences.add(spawn.load(strLine, args));
                        break;
                }
            }
            br.close();
        } catch (ShowParseException e) {
            throw new ShowParseException("Error while parsing Sequence " + showArgs[3] + ": " + e.getReason());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShowParseException("Error while parsing Sequence " + showArgs[3] + " on Line [" + strLine + "]");
        }

        this.sequences = sequences;
        return this;
    }
}
