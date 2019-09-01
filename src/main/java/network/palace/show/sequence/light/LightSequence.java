package network.palace.show.sequence.light;

import lombok.Getter;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.ShowCrystal;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.sequence.handlers.SequenceState;
import network.palace.show.utils.ShowUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class LightSequence extends ShowSequence {

    @Getter private long startTime;
    private LinkedList<ShowSequence> sequences;
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

        LinkedList<ShowSequence> sequences = new LinkedList<>();
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
                    SequenceState state;
                    if (args.length > 2) {
                        state = SequenceState.fromString(args[2]);
                        if (state == null) throw new ShowParseException("Unknown Light Sequence State " + args[2]);
                    } else {
                        state = SequenceState.RELATIVE;
                    }
                    ShowCrystal showCrystal = new ShowCrystal(id, state);
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
                        LightMoveSequence move = new LightMoveSequence(show, time, crystal);
                        sequences.add(move.load(strLine, args));
                        break;
                    case "target":
                        LightTargetSequence target = new LightTargetSequence(show, time, crystal);
                        sequences.add(target.load(strLine, args));
                        break;
                    case "spawn":
                        LightSpawnSequence spawn = new LightSpawnSequence(show, time, crystal);
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
