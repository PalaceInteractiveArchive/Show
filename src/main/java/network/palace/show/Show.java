package network.palace.show;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import lombok.Setter;
import network.palace.audio.Audio;
import network.palace.audio.handlers.AudioArea;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.HeadUtil;
import network.palace.show.actions.*;
import network.palace.show.actions.armor.*;
import network.palace.show.actions.audio.AudioStart;
import network.palace.show.actions.audio.AudioSync;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.ArmorData;
import network.palace.show.handlers.armorstand.PositionType;
import network.palace.show.handlers.armorstand.ShowStand;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.sequence.build.BuildSequence;
import network.palace.show.sequence.fountain.FountainSequence;
import network.palace.show.sequence.laser.LaserSequence;
import network.palace.show.sequence.light.LightSequence;
import network.palace.show.sequence.particle.ParticleSequence;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.EulerAngle;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class Show {
    @Getter private final UUID showID = UUID.randomUUID();
    @Getter private final World world;
    private Location location;
    @Getter private String name = "";
    private LinkedList<ShowSequence> sequences;
    @Getter private final long startTime;
    @Getter @Setter private long musicTime = 0;
    @Getter @Setter private String areaName = "none";
    @Getter private int radius = 75;
    private final HashMap<String, FireworkEffect> effectMap;
    private final HashMap<String, String> invalidLines;
    private final HashMap<String, ShowStand> standmap = new HashMap<>();
    private long lastPlayerListUpdate = System.currentTimeMillis();
    private List<UUID> nearbyPlayers = new ArrayList<>();

    private ShowAction firstAction = null;
    private ShowAction lastAction = null;
    private final LinkedList<ShowAction> laterActions = new LinkedList<>();

    @Getter private boolean skipDebug = false;
    @Getter private int debug = 0;

    /*
      WorldEdit classes
    */
    @Getter private WorldEditPlugin worldEditPlugin;
    @Getter private TerrainManager terrainManager;

    public Show(File file, World world) {
        this.world = world;
        effectMap = new HashMap<>();
        invalidLines = new HashMap<>();
        loadActions(file, 0);
        startTime = System.currentTimeMillis();
        nearbyPlayers.addAll(Bukkit.getOnlinePlayers().stream().filter(tp -> tp.getWorld().getUID().equals(world.getUID()))
                .filter(tp -> tp.getLocation().distance(location) <= radius).map(Player::getUniqueId).collect(Collectors.toList()));
    }

    private void addAction(ShowAction newAction) {
        if (firstAction == null) {
            firstAction = newAction;
        } else {
            lastAction.setNext(newAction);
        }
        lastAction = newAction;
    }

    private void loadActions(File file, long addTime) {
        List<ShowAction> actions = new ArrayList<>() {
            public boolean add(ShowAction mt) {
                int index = Collections.binarySearch(this, mt, (o1, o2) -> (int) (o1.getTime() - o2.getTime()));
                if (index < 0) index = ~index;
                super.add(index, mt);
                return true;
            }
        };

        LinkedList<ShowSequence> sequences = new LinkedList<>();
        String strLine = "";
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            // Parse Lines
            while ((strLine = br.readLine()) != null) {
                if (strLine.length() == 0 || strLine.startsWith("#"))
                    continue;
                String[] args = strLine.split("\\s+");
                if (args.length < 2) {
                    ShowUtil.logDebug(getName(), "Invalid Show Line [" + strLine + "]");
                    continue;
                }
                if (args[1].equals("Name")) {
                    StringBuilder name = new StringBuilder();
                    for (int i = 2; i < args.length; i++) {
                        name.append(args[i]).append(" ");
                    }
                    if (name.length() > 1) {
                        name = new StringBuilder(name.substring(0, name.length() - 1));
                    }
                    this.name = name.toString();
                    continue;
                }
                // Set Show Location
                if (args[1].equals("Location")) {
                    Location loc = WorldUtil.strToLoc(world.getName() + "," + args[2]);
                    if (loc == null) {
                        invalidLines.put(strLine, "Invalid Location Line");
                        continue;
                    }
                    this.location = loc;
                    continue;
                }
                //Load other show
                if (args[1].equals("LoadShow")) {
                    String showName = args[2];
                    File f = new File("plugins/Show/shows/" + world.getName() + "/" + showName + ".show");
                    if (!f.exists()) {
                        invalidLines.put(strLine, "Show does not exist!");
                        continue;
                    }
                    if (f.equals(file)) {
                        invalidLines.put(strLine, "You cannot load a file that's already being loaded");
                        continue;
                    }
                    double time = Double.parseDouble(args[3]);
                    loadActions(f, (long) (time * 1000));
                    continue;
                }
                // Set Text Radius
                if (args[1].equals("TextRadius")) {
                    try {
                        radius = Integer.parseInt(args[2]);
                    } catch (Exception e) {
                        invalidLines.put(strLine, "Invalid Text Radius");
                    }
                    continue;
                }
                // Load Firework effects
                if (args[0].equals("Effect")) {
                    FireworkEffect effect = ShowUtil.parseEffect(args[2]);
                    if (effect == null) {
                        invalidLines.put(strLine, "Invalid Effect Line");
                        continue;
                    }
                    effectMap.put(args[1], effect);
                    continue;
                }
                // ArmorStand Ids
                if (args[0].equals("ArmorStand")) {
                    // ArmorStand id small
                    String id = args[1];
                    if (standmap.get(id) != null) {
                        invalidLines.put(strLine, "ArmorStand with the ID " + id + " already exists!");
                        continue;
                    }
                    boolean small = Boolean.parseBoolean(args[2]);
                    //ArmorStand 0 false skull:myHash;299(234,124,41);300;301
                    ArmorData armorData = parseArmorData(args[3]);
                    ShowStand stand = new ShowStand(id, small, armorData);
                    standmap.put(id, stand);
                    continue;
                }
                // ShowDebug status
                if (args[0].equals("HideDebug")) {
                    skipDebug = Boolean.parseBoolean(args[1]);
                    continue;
                }
                // Get time
                String[] timeToks = args[0].split("_");
                long time = addTime;
                for (String timeStr : timeToks) {
                    time += (long) (Double.parseDouble(timeStr) * 1000);
                }
                ShowAction action = parseAction(strLine, args, time, 1);
                if (action != null) actions.add(action);
            }
            br.close();
            in.close();
            fstream.close();
        } catch (ShowParseException e) {
            Bukkit.getLogger().warning("Error on Line [" + strLine + "] Cause: " + e.getReason());
            ShowUtil.logDebug(getName(), "Error on Line [" + strLine + "] Cause: " + e.getReason());
        } catch (Exception e) {
            System.out.println("Error on Line [" + strLine + "]");
            ShowUtil.logDebug(getName(), "Error on Line [" + strLine + "]");
            e.printStackTrace();
        }

        if (location == null) {
            invalidLines.put("Missing Line", "Show loc x,y,z");
        }

        for (String cur : invalidLines.keySet()) {
            System.out.print(ChatColor.GOLD + invalidLines.get(cur) + " @ " + ChatColor.WHITE + cur.replaceAll("\t", " "));
            ShowUtil.logDebug(getName(), ChatColor.GOLD + invalidLines.get(cur) + " @ " + ChatColor.WHITE + cur.replaceAll("\t", " "));
        }

        this.sequences = sequences;
        actions.forEach(this::addAction);
        actions.clear();
    }

    private ShowAction parseAction(String strLine, String[] args, long time, int start) throws ShowParseException {
        ShowAction action;
        if (args[start].contains("Text")) {
            // Text
            TextAction ac = new TextAction(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].contains("Music")) {
            // Music
            MusicAction ac = new MusicAction(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].contains("Pulse")) {
            // Pulse
            PulseAction ac = new PulseAction(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].equals("ArmorStand")) {
            // ArmorStand Movement
            // Show ArmorStand id action param
            String id = args[start + 1];
            ShowStand stand = standmap.get(id);
            if (stand == null) {
                invalidLines.put(strLine, "No ArmorStand exists with the ID " + id);
                action = null;
            } else {
                String actionText = args[start + 2];
                switch (actionText.toLowerCase()) {
                    case "spawn": {
                        // x,y,z
                        Location loc = WorldUtil.strToLocWithYaw(world.getName() + "," + args[start + 3]);
                        action = new ArmorStandSpawn(this, time, stand, loc);
                        break;
                    }
                    case "move": {
                        // x,y,z speed
                        Location loc = WorldUtil.strToLoc(world.getName() + "," + args[start + 3]);
                        double speed = Double.parseDouble(args[start + 4]);
                        action = new ArmorStandMove(this, time, stand, loc, speed);
                        break;
                    }
                    case "position": {
                        // PositionType x,y,z time
                        double speed = Double.parseDouble(args[start + 5]);
                        String[] alist = args[start + 4].split(",");
                        EulerAngle angle = new EulerAngle(rad(Double.parseDouble(alist[0])),
                                rad(Double.parseDouble(alist[1])), rad(Double.parseDouble(alist[2])));
                        action = new ArmorStandPosition(this, time, stand,
                                PositionType.fromString(args[start + 3]), angle, speed);
                        break;
                    }
                    case "rotate": {
                        // yaw speed
                        float yaw = Float.parseFloat(args[start + 3]);
                        double speed = Double.parseDouble(args[start + 4]);
                        action = new ArmorStandRotate(this, time, stand, yaw, speed);
                        break;
                    }
                    case "despawn": {
                        action = new ArmorStandDespawn(this, time, stand);
                        break;
                    }
                    default:
                        action = null;
                }
            }
        } else if (args[start].contains("GlowDone")) {
            // Take away GWTS Hats
            action = new GlowDoneAction(this, time);
        } else if (args[start].contains("Glow")) {
            // Glow With The Show
            GlowAction ac = new GlowAction(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].contains("Lightning")) {
            // Lightning
            LightningAction ac = new LightningAction(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].contains("FakeBlock")) {
            // FakeBlock
            FakeBlockAction ac = new FakeBlockAction(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].startsWith("Block")) {
            // Block
            BlockAction ac = new BlockAction(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].contains("PowerFirework")) {
            // PowerFirework
            PowerFireworkAction ac = new PowerFireworkAction(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].startsWith("Firework")) {
            // Firework
            FireworkAction ac = new FireworkAction(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].contains("Schematic")) {
            // Schematic
            if (worldEditPlugin == null) {
                org.bukkit.plugin.Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
                if (plugin instanceof WorldEditPlugin) {
                    worldEditPlugin = (WorldEditPlugin) plugin;
                    terrainManager = new TerrainManager(worldEditPlugin, world);
                } else {
                    throw new ShowParseException("Unable to load SchematicAction - no WorldEdit plugin found!");
                }
            }
            SchematicAction ac = new SchematicAction(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].contains("Fountain")) {
            // Fountain
            FountainAction ac = new FountainAction(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].contains("Title")) {
            // Title
            TitleAction ac = new TitleAction(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].contains("ActionBar")) {
            // ActionBar
            ActionBarAction ac = new ActionBarAction(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].contains("Command")) {
            // Command
            CommandAction ac = new CommandAction(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].contains("SpiralParticle")) {
            // SpiralParticle
            SpiralParticle ac = new SpiralParticle(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].contains("Particle")) {
            // Particle
            ParticleAction ac = new ParticleAction(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].contains("AudioStart")) {
            // AudioStart
            AudioStart ac = new AudioStart(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].contains("AudioSync")) {
            // AudioSync
            AudioSync ac = new AudioSync(this, time);
            action = ac.load(strLine, args);
        } else if (args[start].contains("Sequence")) {
            // Sequences
            ShowSequence sequence;
            switch (args[start + 1].toLowerCase()) {
                case "laser": {
                    sequence = new LaserSequence(this, time);
                    break;
                }
                case "fountain": {
                    sequence = new FountainSequence(this, time);
                    break;
                }
                case "light": {
                    sequence = new LightSequence(this, time);
                    break;
                }
                case "particle": {
                    sequence = new ParticleSequence(this, time);
                    break;
                }
                case "build": {
                    sequence = new BuildSequence(this, time);
                    break;
                }
                default:
                    return null;
            }
            sequences.add(sequence.load(strLine, args));
            action = null;
        } else {
            action = null;
        }
        return action;
    }

    private double rad(double v) {
        return (v * Math.PI) / 180;
    }

    private ArmorData parseArmorData(String s) throws Exception {
        String[] list = s.split(";");
        ItemStack head = new ItemStack(Material.AIR);
        ItemStack chestplate = new ItemStack(Material.AIR);
        ItemStack leggings = new ItemStack(Material.AIR);
        ItemStack boots = new ItemStack(Material.AIR);
        ItemStack itemInMainHand = new ItemStack(Material.AIR);
        int i = 0;
        if (list.length >= 4) {
            for (String st : list) {
                i++;
                if (i == 1) {
                    if (st.startsWith("skull")) {
                        head = HeadUtil.getPlayerHead(st.split(":")[1]);
                        continue;
                    }
                }
                if (st.contains("(")) {
                    String[] color = st.split("\\(");
                    String[] l = color[0].split(":");
                    int id = Integer.parseInt(l[0]);
                    byte dam = l.length > 1 ? Byte.parseByte(l[1]) : 0;
                    Material type = Material.getMaterial(id);
                    if (!type.name().toLowerCase().contains("leather")) {
                        continue;
                    }
                    ItemStack temp = new ItemStack(type, 1, dam);
                    LeatherArmorMeta lam = (LeatherArmorMeta) temp.getItemMeta();
                    String[] cls = color[1].replaceAll("[()]", "").split(",");
                    lam.setColor(Color.fromRGB(Integer.parseInt(cls[0]), Integer.parseInt(cls[1]),
                            Integer.parseInt(cls[2])));
                    temp.setItemMeta(lam);
                    switch (i) {
                        case 1:
                            head = temp;
                            continue;
                        case 2:
                            chestplate = temp;
                            continue;
                        case 3:
                            leggings = temp;
                            continue;
                        case 4:
                            boots = temp;
                            continue;
                        case 5:
                            itemInMainHand = temp;
                            continue;
                    }
                    continue;
                }
                String[] l = st.split(":");
                int id = Integer.parseInt(l[0]);
                byte dam = l.length > 1 ? Byte.parseByte(l[1]) : 0;
                ItemStack temp = new ItemStack(Material.getMaterial(id), 1, dam);
                switch (i) {
                    case 1:
                        head = temp;
                        continue;
                    case 2:
                        chestplate = temp;
                        continue;
                    case 3:
                        leggings = temp;
                        continue;
                    case 4:
                        boots = temp;
                        continue;
                    case 5:
                        itemInMainHand = temp;
                }
            }
        }
        return new ArmorData(head, chestplate, leggings, boots, itemInMainHand);
    }

    public List<UUID> getNearPlayers() {
        if (System.currentTimeMillis() - lastPlayerListUpdate < 10000) {
            return new ArrayList<>(nearbyPlayers);
        }
        List<UUID> list = Bukkit.getOnlinePlayers().stream().filter(tp -> tp.getWorld().getUID().equals(world.getUID()))
                .filter(tp -> tp.getLocation().distance(location) <= radius).map(Player::getUniqueId).collect(Collectors.toList());
        lastPlayerListUpdate = System.currentTimeMillis();
        nearbyPlayers = list;
        return list;
    }

    public boolean update() {
        if (!invalidLines.isEmpty()) return true;

        CPlayer[] nearPlayers;
        List<UUID> nearIDs = getNearPlayers();
        nearPlayers = new CPlayer[nearIDs.size()];
        int i = 0;
        for (UUID uuid : nearIDs) {
            nearPlayers[i++] = Core.getPlayerManager().getPlayer(uuid);
        }

        long timeDiff = System.currentTimeMillis() - startTime;

        for (ShowAction action : new LinkedList<>(laterActions)) {
            if (action == null) continue;
            try {
                if (timeDiff < action.getTime()) continue;
                try {
                    action.play(nearPlayers);
                } catch (Exception e) {
                    Core.logMessage("Show " + action.getShow().getName(), "Error playing action in show " + action.getShow().getName());
                }
                laterActions.remove(action);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ShowAction temp = firstAction;
        while (temp != null) {
            // if action time isn't meant to play yet, exit
            if (temp.getTime() > timeDiff) break;
            // otherwise, play action

            try {
                temp.play(nearPlayers);
            } catch (Exception e) {
                Core.logMessage("Show " + temp.getShow().getName(), "Error playing action in show " + temp.getShow().getName());
            }

            // then move to next action
            ShowAction a = temp;
            temp = temp.getNext();
            a.setNext(null);
        }
        firstAction = temp;
        if (sequences != null) ShowUtil.runSequences(sequences, startTime);
        return firstAction == null && this.sequences.isEmpty() && laterActions.isEmpty();
    }

    public void displayText(String text) {
        for (UUID uuid : getNearPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            if (offset(player.getLocation(), location) < radius) {
                player.sendMessage(ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', text));
            }
        }
    }

    public void playMusic(int record) {
        for (UUID uuid : getNearPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            player.playEffect(location, Effect.RECORD_PLAY, record);
        }
    }

    public Location getLocation() {
        return location.clone();
    }

    public void syncAudioForPlayer(final CPlayer tp) {
        final AudioArea area = Audio.getInstance().getByName(areaName);
        if (area == null) {
            return;
        }
        area.triggerPlayer(tp);
        long ms = (System.currentTimeMillis() - musicTime);
        float seconds = ms / 1000f;
        area.sync(seconds, tp, 0);
    }

    public void stop() {
        for (Entity e : world.getEntities()) {
            if (!e.getType().equals(EntityType.ARMOR_STAND) || !(e.hasMetadata("show") &&
                    e.getMetadata("show").get(0).asString().equals(showID.toString()))) {
                continue;
            }
            e.remove();
        }
        for (ShowSequence s : sequences) {
            if (s instanceof LaserSequence) {
                ((LaserSequence) s).despawn();
            }
            if (s instanceof BuildSequence) {
                ((BuildSequence) s).despawn();
            }
        }
        if (debug > 0) {
            Core.getPlayerManager().getOnlinePlayers().stream()
                    .filter(p -> p.getRank().getRankId() >= Rank.TRAINEETECH.getRankId())
                    .forEach(p -> p.sendMessage(ChatColor.AQUA + "[ShowDebug - " + getName() + "] " +
                            ChatColor.YELLOW + debug + " debug messages were hidden."));
        }
    }

    public static double offset(Location a, Location b) {
        return a.toVector().subtract(b.toVector()).length();
    }

    public HashMap<String, FireworkEffect> getEffectMap() {
        return new HashMap<>(effectMap);
    }

    public void addLaterAction(ShowAction action) {
        laterActions.add(action);
    }

    public void debug() {
        if (skipDebug) return;
        debug++;
    }
}
