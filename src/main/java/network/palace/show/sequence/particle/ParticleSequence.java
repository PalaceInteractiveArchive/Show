package network.palace.show.sequence.particle;

import lombok.Getter;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.ParticleObject;
import network.palace.show.handlers.particle.CylinderParticle;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.sequence.particle.cylinder.ParticleCylinderSequence;
import network.palace.show.utils.ShowUtil;
import org.bukkit.block.BlockFace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class ParticleSequence extends ShowSequence {
    @Getter private long startTime = 0;
    private LinkedList<ShowSequence> sequences;
    private Map<String, ParticleObject> particleMap = new HashMap<>();
    private boolean isParticleSequence = false;

    public ParticleSequence(Show show, long time) {
        super(show, time);
    }

    @Override
    public boolean run() {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }

        particleMap.values().stream().filter(ParticleObject::isSpawned).forEach(ParticleObject::render);

        if (sequences != null) {
            ShowUtil.runSequences(sequences, startTime);
            return sequences.isEmpty();
        }

        return false;
    }

    @Override
    public ShowSequence load(String line, String... showArgs) throws ShowParseException {
        File file = new File(ShowPlugin.getInstance().getDataFolder(), "sequences/particles/" + showArgs[3] + ".sequence");
        if (!file.exists()) {
            throw new ShowParseException("Could not find Particle sequence file " + showArgs[3]);
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

                if (!isParticleSequence) {
                    if (!args[0].equalsIgnoreCase("Sequence")) {
                        throw new ShowParseException("First line isn't a Sequence definition");
                    }

                    if (!args[1].equalsIgnoreCase("Particle")) {
                        throw new ShowParseException("This isn't a Particle file!");
                    }

                    isParticleSequence = true;
                    continue;
                }

                if (args[0].equals("Cylinder")) {
                    String id = args[1];
                    String[] offset = args[3].split(",");
                    ParticleObject particle = new CylinderParticle(args[1], ShowUtil.getParticle(args[2]), show, Integer.parseInt(args[4]),
                            Float.parseFloat(offset[0]), Float.parseFloat(offset[1]), Float.parseFloat(offset[2]),
                            Float.parseFloat(args[5]), Integer.parseInt(args[6]), Double.parseDouble(args[7]), Double.parseDouble(args[8]),
                            CylinderParticle.Shape.valueOf(args[9].toUpperCase()), BlockFace.valueOf(args[10].toUpperCase()));
                    particleMap.put(id, particle);
                    continue;
                }

                String[] timeToks = args[0].split("_");
                long time = 0;
                for (String timeStr : timeToks) {
                    time += (long) (Double.parseDouble(timeStr) * 1000);
                }

                ParticleObject particleObject = particleMap.get(args[1]);
                switch (args[2].toLowerCase()) {
                    case "spawn":
                        ParticleSpawnSequence spawn = new ParticleSpawnSequence(show, time, particleObject);
                        sequences.add(spawn.load(strLine, args));
                        break;
                    case "move":
                        ParticleMoveSequence move = new ParticleMoveSequence(show, time, particleObject);
                        sequences.add(move.load(strLine, args));
                        break;
                    case "modify":
                        ParticleModifySequence target = new ParticleModifySequence(show, time, particleObject);
                        sequences.add(target.load(strLine, args));
                        break;
                    case "cylinder":
                        if (particleObject instanceof CylinderParticle) {
                            ParticleCylinderSequence cylinder = new ParticleCylinderSequence(show, time, (CylinderParticle) particleObject);
                            sequences.add(cylinder.load(strLine, args));
                        } else {
                            throw new ShowParseException("ParticleObject " + particleObject.getId() + " is not a CylinderParticle!");
                        }
                        break;
                    case "despawn":
                        ParticleDespawnSequence despawn = new ParticleDespawnSequence(show, time, particleObject);
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
}
