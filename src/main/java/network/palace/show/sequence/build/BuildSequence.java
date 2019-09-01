package network.palace.show.sequence.build;

import lombok.Getter;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.BuildObject;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.utils.ShowUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BuildSequence extends ShowSequence {
    @Getter private long startTime = 0;
    private LinkedList<ShowSequence> sequences;
    private Map<String, BuildObject> blockMap = new HashMap<>();
    private boolean isBuildSequence = false;

    public BuildSequence(Show show, long time) {
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
        File file = new File(ShowPlugin.getInstance().getDataFolder(), "sequences/builds/" + showArgs[3] + ".sequence");
        if (!file.exists()) {
            throw new ShowParseException("Could not find Build sequence file " + showArgs[3]);
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

                if (!isBuildSequence) {
                    if (!args[0].equalsIgnoreCase("Sequence")) {
                        throw new ShowParseException("First line isn't a Sequence definition");
                    }

                    if (!args[1].equalsIgnoreCase("Build")) {
                        throw new ShowParseException("This isn't a Build file!");
                    }

                    isBuildSequence = true;
                    continue;
                }

                if (args[0].equals("Build")) {
                    String id = args[1];
                    BuildObject block = new BuildObject(id, ShowPlugin.getBuildUtil().loadBuild(args[2]), show);
                    blockMap.put(id, block);
                    continue;
                }

                String[] timeToks = args[0].split("_");
                long time = 0;
                for (String timeStr : timeToks) {
                    time += (long) (Double.parseDouble(timeStr) * 1000);
                }

                switch (args[2].toLowerCase()) {
                    case "spawn":
                        BuildSpawnSequence spawn = new BuildSpawnSequence(this, time, args[1]);
                        sequences.add(spawn.load(strLine, args));
                        break;
                    case "move":
                        BuildMoveSequence move = new BuildMoveSequence(this, time, args[1]);
                        sequences.add(move.load(strLine, args));
                        break;
                    case "moveanddespawn":
                        BuildMoveAndDespawnSequence moveAndDespawn = new BuildMoveAndDespawnSequence(this, time, args[1]);
                        sequences.add(moveAndDespawn.load(strLine, args));
                        break;
                    case "despawn":
                        BuildDespawnSequence despawn = new BuildDespawnSequence(this, time, args[1]);
                        sequences.add(despawn.load(strLine, args));
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

    public BuildObject getBuildObject(String name) {
        return blockMap.get(name);
    }

    public void despawn() {
        blockMap.values().forEach(BuildObject::despawn);
    }
}
