package network.palace.show;

import lombok.Getter;
import network.palace.audio.Audio;
import network.palace.audio.handlers.AudioArea;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.HeadUtil;
import network.palace.show.actions.*;
import network.palace.show.actions.armor.*;
import network.palace.show.actions.audio.AudioStart;
import network.palace.show.actions.audio.AudioSync;
import network.palace.show.handlers.ArmorData;
import network.palace.show.handlers.TitleType;
import network.palace.show.handlers.armorstand.PositionType;
import network.palace.show.handlers.armorstand.ShowStand;
import network.palace.show.utils.WorldUtil;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Show {
    @Getter private UUID showID = UUID.randomUUID();
    public long startTime;
    public HashSet<ShowAction> actions;
    public long musicTime = 0;
    public String areaName = "none";
    private World world;
    private Location loc;
    private int radius = 75;
    private HashMap<String, FireworkEffect> effectMap;
    private HashMap<String, String> invalidLines;
    private HashMap<String, ShowStand> standmap = new HashMap<>();
    private int npcTick = 0;
    private long lastPlayerListUpdate = System.currentTimeMillis();
    private List<UUID> nearbyPlayers = new ArrayList<>();
    private String showName = "";

    public Show(JavaPlugin plugin, File file) {
        world = Bukkit.getWorlds().get(0);
        effectMap = new HashMap<>();
        invalidLines = new HashMap<>();
        loadActions(file, 0);
        startTime = System.currentTimeMillis();
        nearbyPlayers.addAll(Bukkit.getOnlinePlayers().stream().filter(tp -> tp.getLocation().distance(loc) <= radius)
                .map(Player::getUniqueId).collect(Collectors.toList()));
    }

    public static Location strToLoc(String string) {
        if (string.length() == 0) {
            return null;
        }
        String[] tokens = string.split(",");
        try {
            for (World cur : Bukkit.getWorlds()) {
                if (cur.getName().equalsIgnoreCase(tokens[0])) {
                    return new Location(cur, Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]),
                            Double.parseDouble(tokens[3]));
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private void loadActions(File file, long addTime) {
        actions = new HashSet<>();
        String strLine = "";
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            // Parse Lines
            while ((strLine = br.readLine()) != null) {
                if (strLine.length() == 0 || strLine.startsWith("#"))
                    continue;
                String[] tokens = strLine.split("\\s+");
                if (tokens.length < 2) {
                    System.out.println("Invalid Show Line [" + strLine + "]");
                    continue;
                }
                if (tokens[1].equals("Name")) {
                    StringBuilder name = new StringBuilder();
                    for (int i = 2; i < tokens.length; i++) {
                        name.append(tokens[i]).append(" ");
                    }
                    if (name.length() > 1) {
                        name = new StringBuilder(name.substring(0, name.length() - 1));
                    }
                    showName = name.toString();
                    continue;
                }
                // Set Show Location
                if (tokens[1].equals("Location")) {
                    Location loc = strToLoc(world.getName() + "," + tokens[2]);
                    if (loc == null) {
                        invalidLines.put(strLine, "Invalid Location Line");
                        continue;
                    }
                    this.loc = loc;
                    continue;
                }
                //Load other show
                if (tokens[1].equals("LoadShow")) {
                    String showName = tokens[2];
                    File f = new File("plugins/ParkManager/shows/" + showName);
                    if (!f.exists()) {
                        invalidLines.put(strLine, "Show does not exist!");
                        continue;
                    }
                    if (f.equals(file)) {
                        invalidLines.put(strLine, "You cannot load a file that's already being loaded");
                        continue;
                    }
                    double time = Double.parseDouble(tokens[3]);
                    loadActions(f, (long) (time * 1000));
                    continue;
                }
                // Set Text Radius
                if (tokens[1].equals("TextRadius")) {
                    try {
                        radius = Integer.parseInt(tokens[2]);
                    } catch (Exception e) {
                        invalidLines.put(strLine, "Invalid Text Radius");
                    }
                    continue;
                }
                // Load Firework effects
                if (tokens[0].equals("Effect")) {
                    FireworkEffect effect = parseEffect(tokens[2]);
                    if (effect == null) {
                        invalidLines.put(strLine, "Invalid Effect Line");
                        continue;
                    }
                    effectMap.put(tokens[1], effect);
                    continue;
                }
                // ArmorStand Ids
                if (tokens[0].equals("ArmorStand")) {
                    // ArmorStand id small
                    String id = tokens[1];
                    if (standmap.get(id) != null) {
                        invalidLines.put(strLine, "ArmorStand with the ID " + id + " already exists!");
                        continue;
                    }
                    Boolean small = Boolean.valueOf(tokens[2]);
                    //ArmorStand 0 false skull:myHash;299(234,124,41);300;301
                    ArmorData armorData = parseArmorData(tokens[3]);
                    ShowStand stand = new ShowStand(id, small, armorData);
                    standmap.put(id, stand);
                    continue;
                }
                // Get time
                String[] timeToks = tokens[0].split("_");
                long time = addTime;
                for (String timeStr : timeToks) {
                    time += (long) (Double.parseDouble(timeStr) * 1000);
                }
                // Text
                if (tokens[1].contains("Text")) {
                    StringBuilder text = new StringBuilder();
                    for (int i = 2; i < tokens.length; i++)
                        text.append(tokens[i]).append(" ");
                    if (text.length() > 1)
                        text = new StringBuilder(text.substring(0, text.length() - 1));
                    actions.add(new TextAction(this, time, text.toString()));
                    continue;
                }
                // Music
                if (tokens[1].contains("Music")) {
                    try {
                        int id = Integer.parseInt(tokens[2]);
                        actions.add(new MusicAction(this, time, id));
                    } catch (Exception e) {
                        invalidLines.put(strLine, "Invalid Material");
                    }
                    continue;
                }
                // Pulse
                if (tokens[1].contains("Pulse")) {
                    Location loc = strToLoc(world.getName() + "," + tokens[2]);
                    if (loc == null) {
                        invalidLines.put(strLine, "Invalid Location");
                        continue;
                    }
                    actions.add(new PulseAction(this, time, loc));
                    continue;
                }
                // ArmorStand Movement
                if (tokens[1].equals("ArmorStand")) {
                    // Show ArmorStand id action param
                    String id = tokens[2];
                    ShowStand stand = standmap.get(id);
                    if (stand == null) {
                        invalidLines.put(strLine, "No ArmorStand exists with the ID " + id);
                        continue;
                    }
                    String action = tokens[3];
                    switch (action.toLowerCase()) {
                        case "spawn": {
                            // x,y,z
                            Location loc = strToLoc(world.getName() + "," + tokens[4]);
                            ArmorStandSpawn spawn = new ArmorStandSpawn(this, time, stand, loc);
                            actions.add(spawn);
                            break;
                        }
                        case "move": {
                            // x,y,z speed
                            Location loc = strToLoc(world.getName() + "," + tokens[4]);
                            Double speed = Double.parseDouble(tokens[5]);
                            ArmorStandMove move = new ArmorStandMove(this, time, stand, loc, speed);
                            actions.add(move);
                            break;
                        }
                        case "position": {
                            // PositionType x,y,z time
                            Double speed = Double.parseDouble(tokens[6]);
                            String[] alist = tokens[5].split(",");
                            EulerAngle angle = new EulerAngle(rad(Double.parseDouble(alist[0])),
                                    rad(Double.parseDouble(alist[1])), rad(Double.parseDouble(alist[2])));
                            ArmorStandPosition position = new ArmorStandPosition(this, time, stand,
                                    PositionType.fromString(tokens[4]), angle, speed);
                            actions.add(position);
                            break;
                        }
                        case "rotate": {
                            // yaw speed
                            float yaw = Float.parseFloat(tokens[4]);
                            double speed = Double.parseDouble(tokens[5]);
                            actions.add(new ArmorStandRotate(this, time, stand, yaw, speed));
                            break;
                        }
                        case "despawn": {
                            ArmorStandDespawn despawn = new ArmorStandDespawn(this, time, stand);
                            actions.add(despawn);
                            break;
                        }
                    }
                    continue;
                }
                // Take away GWTS Hats
                if (tokens[1].contains("GlowDone")) {
                    actions.add(new GlowDoneAction(this, time));
                    continue;
                }
                // Glow With The Show
                if (tokens[1].contains("Glow")) {
                    try {
                        Color color;
                        if (tokens[2].contains(",")) {
                            String[] list = tokens[2].split(",");
                            color = Color.fromRGB(getInt(list[0]), getInt(list[1]), getInt(list[2]));
                        } else {
                            color = colorFromString(tokens[2]);
                        }
                        if (color == null) {
                            invalidLines.put(strLine, "Invalid Glow Color " + tokens[2]);
                            continue;
                        }
                        Location loc = strToLoc(world.getName() + "," + tokens[3]);
                        actions.add(new GlowAction(this, time, color, loc, getInt(tokens[4])));
                    } catch (Exception ignored) {
                        invalidLines.put(strLine, "Invalid Glow Line");
                    }
                    continue;
                }
                // Lightning
                if (tokens[1].contains("Lightning")) {
                    Location loc = strToLoc(world.getName() + "," + tokens[2]);
                    if (loc == null) {
                        invalidLines.put(strLine, "Invalid Location");
                        continue;
                    }
                    actions.add(new LightningAction(this, time, loc));
                    continue;
                }
                // Fake Block
                if (tokens[1].contains("FakeBlock")) {
                    Location loc = strToLoc(world.getName() + "," + tokens[3]);
                    if (loc == null) {
                        invalidLines.put(strLine, "Invalid Location");
                        continue;
                    }
                    String[] list;
                    if (tokens[2].contains(":")) {
                        list = tokens[2].split(":");
                    } else {
                        list = null;
                    }
                    try {
                        int id;
                        byte data;
                        if (list != null) {
                            id = Integer.parseInt(list[0]);
                            data = Byte.parseByte(list[1]);
                        } else {
                            id = Integer.parseInt(tokens[2]);
                            data = (byte) 0;
                        }
                        actions.add(new FakeBlockAction(this, time, loc, id, data));
                    } catch (Exception e) {
                        e.printStackTrace();
                        invalidLines.put(strLine,
                                "Invalid Block ID or Block data");
                    }
                    continue;
                }
                // Block
                if (tokens[1].startsWith("Block")) {
                    Location loc = strToLoc(world.getName() + "," + tokens[3]);
                    if (loc == null) {
                        invalidLines.put(strLine, "Invalid Location");
                        continue;
                    }
                    String[] list;
                    if (tokens[2].contains(":")) {
                        list = tokens[2].split(":");
                    } else {
                        list = null;
                    }
                    try {
                        int id;
                        byte data;
                        if (list != null) {
                            id = Integer.parseInt(list[0]);
                            data = Byte.parseByte(list[1]);
                        } else {
                            id = Integer.parseInt(tokens[2]);
                            data = (byte) 0;
                        }
                        actions.add(new BlockAction(this, time, loc, id, data));
                    } catch (Exception e) {
                        invalidLines.put(strLine, "Invalid Block ID or Block data");
                    }
                    continue;
                }
                // Firework
                if (tokens[1].contains("PowerFirework")) {
                    if (tokens.length != 5) {
                        invalidLines.put(strLine, "Invalid PowerFirework Line Length");
                    }
                    Location loc = strToLoc(Bukkit.getWorlds().get(0).getName() + "," + tokens[2]);
                    String[] l = tokens[4].split(",");
                    Vector motion = new Vector(Double.parseDouble(l[0]), Double.parseDouble(l[1]),
                            Double.parseDouble(l[2]));
                    ArrayList<FireworkEffect> effectList = new ArrayList<>();
                    String[] effects = tokens[3].split(",");
                    for (String effect : effects) {
                        if (effectMap.containsKey(effect)) {
                            effectList.add(effectMap.get(effect));
                        }
                    }
                    if (effectList.isEmpty()) {
                        invalidLines.put(strLine, "Invalid effects");
                        continue;
                    }
                    actions.add(new PowerFireworkAction(this, time, loc, motion, effectList));
                    continue;
                }
                if (tokens[1].startsWith("Firework")) {
                    if (tokens.length != 7) {
                        invalidLines.put(strLine, "Invalid Firework Line Length");
                        continue;
                    }
                    // loc
                    Location loc = strToLoc(world.getName() + "," + tokens[2]);
                    if (loc == null) {
                        invalidLines.put(strLine, "Invalid Location");
                        continue;
                    }
                    // Effect List
                    ArrayList<FireworkEffect> effectList = new ArrayList<>();
                    String[] effects = tokens[3].split(",");
                    for (String effect : effects) {
                        if (effectMap.containsKey(effect)) {
                            effectList.add(effectMap.get(effect));
                        }
                    }
                    if (effectList.isEmpty()) {
                        invalidLines.put(strLine, "Invalid effects");
                        continue;
                    }
                    // power
                    int power;
                    try {
                        power = Integer.parseInt(tokens[4]);
                        if (power < 0 || power > 5) {
                            invalidLines.put(strLine, "Power too High/Low");
                            continue;
                        }
                    } catch (Exception e) {
                        invalidLines.put(strLine, "Invalid Power");
                        continue;
                    }
                    // direction
                    Vector dir;
                    try {
                        String[] coords = tokens[5].split(",");
                        dir = new Vector(Double.parseDouble(coords[0]),
                                Double.parseDouble(coords[1]),
                                Double.parseDouble(coords[2]));
                    } catch (Exception e) {
                        invalidLines.put(strLine, "Invalid Direction");
                        continue;
                    }
                    // Dir power
                    double dirPower;
                    try {
                        dirPower = Double.parseDouble(tokens[6]);
                        if (dirPower < 0 || dirPower > 10) {
                            invalidLines.put(strLine, "Direction Power too High/Low");
                            continue;
                        }
                    } catch (Exception e) {
                        invalidLines.put(strLine, "Invalid Direction Power");
                        continue;
                    }
                    actions.add(new FireworkAction(this, time, loc, effectList, power, dir, dirPower));
                    continue;
                }
                // Schematic
                if (tokens[1].contains("Schematic")) {
                    if (isInt(tokens[3]) && isInt(tokens[4]) && isInt(tokens[5])) {
                        try {
                            int x = Integer.parseInt(tokens[3]);
                            int y = Integer.parseInt(tokens[4]);
                            int z = Integer.parseInt(tokens[5]);
                            Location pasteloc = new Location(Bukkit.getWorld(tokens[6]), x, y, z);
                            boolean noAir = !tokens[7].toLowerCase().contains("false");
                            actions.add(new SchematicAction(this, time, pasteloc, tokens[2], noAir));
                        } catch (Exception e) {
                            e.printStackTrace();
                            invalidLines.put(strLine, "Error creating Schematic Action!");
                        }
                    } else {
                        invalidLines.put(strLine, "Invalid X, Y, or Z Coordinates!");
                    }
                    continue;
                }
                if (tokens[1].contains("Fountain")) {
                    Location loc = strToLoc(world.getName() + "," + tokens[4]);
                    Double[] values = WorldUtil.strToDoubleList(world.getName() + "," + tokens[5]);
                    double duration = Double.parseDouble(tokens[3]);
                    String[] list;
                    if (tokens[2].contains(":")) {
                        list = tokens[2].split(":");
                    } else {
                        list = null;
                    }
                    try {
                        int type;
                        byte data;
                        if (list != null) {
                            type = Integer.parseInt(list[0]);
                            data = Byte.parseByte(list[1]);
                        } else {
                            type = Integer.parseInt(tokens[2]);
                            data = (byte) 0;
                        }
                        Vector force = new Vector(values[0], values[1], values[2]);
                        actions.add(new FountainAction(this, time, loc, duration, type, data, force));
                    } catch (NumberFormatException e) {
                        invalidLines.put(strLine, "Invalid Fountain Type");
                        e.printStackTrace();
                    }
                    continue;
                }
                if (tokens[1].contains("Title")) {
                    // 0 Title title fadeIn fadeOut stay title...
                    TitleType type = getTitleType(tokens[2].toUpperCase());
                    int fadeIn = Integer.parseInt(tokens[3]);
                    int fadeOut = Integer.parseInt(tokens[4]);
                    int stay = Integer.parseInt(tokens[5]);
                    StringBuilder text = new StringBuilder();
                    for (int i = 6; i < tokens.length; i++)
                        text.append(tokens[i]).append(" ");
                    if (text.length() > 1)
                        text = new StringBuilder(text.substring(0, text.length() - 1));
                    actions.add(new TitleAction(this, time, type, text.toString(), fadeIn, fadeOut, stay));
                    continue;
                }
                if (tokens[1].contains("Particle")) {
                    // 0 Particle type x,y,z oX oY oZ speed amount
                    Particle effect = getParticle(tokens[2]);
                    Location location = strToLoc(world.getName() + "," + tokens[3]);
                    double offsetX = Float.parseFloat(tokens[4]);
                    double offsetY = Float.parseFloat(tokens[5]);
                    double offsetZ = Float.parseFloat(tokens[6]);
                    float speed = Float.parseFloat(tokens[7]);
                    int amount = Integer.parseInt(tokens[8]);
                    actions.add(new ParticleAction(this, time, effect, location, offsetX, offsetY, offsetZ, speed, amount));
                    continue;
                }
                if (tokens[1].contains("AudioStart")) {
                    actions.add(new AudioStart(this, time, tokens[2]));
                    continue;
                }
                if (tokens[1].contains("AudioSync")) {
                    actions.add(new AudioSync(this, time, tokens[2], Float.valueOf(tokens[3])));
                }
            }
            in.close();
        } catch (Exception e) {
            System.out.println("Error on Line [" + strLine + "]");
            Bukkit.broadcast("Error on Line [" + strLine + "]", "arcade.bypass");
            e.printStackTrace();
        }

        if (loc == null) {
            invalidLines.put("Missing Line", "Show loc x,y,z");
        }

        for (String cur : invalidLines.keySet()) {
            System.out.print(ChatColor.GOLD + invalidLines.get(cur) + " @ " + ChatColor.WHITE + cur.replaceAll("\t", " "));
            Bukkit.broadcast(ChatColor.GOLD + invalidLines.get(cur) + " @ " + ChatColor.WHITE + cur.replaceAll("\t", " "),
                    "arcade.bypass");
        }
    }

    private Particle getParticle(String s) {
        switch (s.toLowerCase()) {
            case "barrier":
                return Particle.BARRIER;
            case "bubble":
                return Particle.WATER_BUBBLE;
            case "cloud":
                return Particle.CLOUD;
            case "crit":
                return Particle.CRIT;
            case "depthsuspend":
                return Particle.SUSPENDED_DEPTH;
            case "dragonbreath":
                return Particle.DRAGON_BREATH;
            case "driplava":
                return Particle.DRIP_LAVA;
            case "dripwater":
                return Particle.DRIP_WATER;
            case "enchantmenttable":
                return Particle.ENCHANTMENT_TABLE;
            case "explode":
                return Particle.EXPLOSION_NORMAL;
            case "fireworksspark":
                return Particle.FIREWORKS_SPARK;
            case "flame":
                return Particle.FLAME;
            case "footstep":
                return Particle.FOOTSTEP;
            case "happyvillager":
                return Particle.VILLAGER_HAPPY;
            case "heart":
                return Particle.HEART;
            case "hugeexplosion":
                return Particle.EXPLOSION_HUGE;
            case "instantspell":
                return Particle.SPELL_INSTANT;
            case "largeexplode":
                return Particle.EXPLOSION_LARGE;
            case "largesmoke":
                return Particle.SMOKE_LARGE;
            case "lava":
                return Particle.LAVA;
            case "magiccrit":
                return Particle.CRIT_MAGIC;
            case "mobspell":
                return Particle.SPELL_MOB;
            case "mobspellambient":
                return Particle.SPELL_MOB_AMBIENT;
            case "note":
                return Particle.NOTE;
            case "portal":
                return Particle.PORTAL;
            case "reddust":
                return Particle.REDSTONE;
            case "slime":
                return Particle.SLIME;
            case "smoke":
                return Particle.SMOKE_NORMAL;
            case "snowballpoof":
                return Particle.SNOWBALL;
            case "snowshovel":
                return Particle.SNOW_SHOVEL;
            case "spell":
                return Particle.SPELL;
            case "spit":
                return Particle.SPIT;
            case "splash":
                return Particle.WATER_SPLASH;
            case "suspend":
                return Particle.SUSPENDED;
            case "totem":
                return Particle.TOTEM;
            case "townaura":
                return Particle.TOWN_AURA;
            case "wake":
                return Particle.WATER_WAKE;
            case "witchmagic":
                return Particle.SPELL_WITCH;
        }
        return Particle.valueOf(s);
    }

    private TitleType getTitleType(String s) {
        switch (s.toLowerCase()) {
            case "subtitle":
                return TitleType.SUBTITLE;
        }
        return TitleType.TITLE;
    }

    private double rad(double v) {
        return (v * Math.PI) / 180;
    }

    @SuppressWarnings("deprecation")
    private ArmorData parseArmorData(String s) throws Exception {
        String[] list = s.split(";");
        ItemStack head = new ItemStack(Material.AIR);
        ItemStack chestplate = new ItemStack(Material.AIR);
        ItemStack leggings = new ItemStack(Material.AIR);
        ItemStack boots = new ItemStack(Material.AIR);
        int i = 0;
        if (list.length == 4) {
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
                }
            }
        }
        return new ArmorData(head, chestplate, leggings, boots);
    }

    private Color colorFromString(String s) {
        switch (s.toLowerCase()) {
            case "red":
                return Color.fromRGB(170, 0, 0);
            case "orange":
                return Color.fromRGB(255, 102, 0);
            case "yellow":
                return Color.fromRGB(255, 222, 0);
            case "green":
                return Color.fromRGB(0, 153, 0);
            case "aqua":
                return Color.fromRGB(0, 255, 255);
            case "blue":
                return Color.fromRGB(51, 51, 255);
            case "purple":
                return Color.fromRGB(39, 31, 155);
            case "pink":
                return Color.fromRGB(255, 0, 255);
            case "white":
                return Color.fromRGB(255, 255, 255);
            case "black":
                return Color.fromRGB(0, 0, 0);
        }
        return null;
    }

    private int getInt(String s) {
        return Integer.parseInt(s);
    }

    public List<UUID> getNearPlayers() {
        if (System.currentTimeMillis() - lastPlayerListUpdate < 10000) {
            return new ArrayList<>(nearbyPlayers);
        }
        List<UUID> list = Bukkit.getOnlinePlayers().stream().filter(tp -> tp.getLocation().distance(loc) <= radius)
                .map(Player::getUniqueId).collect(Collectors.toList());
        lastPlayerListUpdate = System.currentTimeMillis();
        nearbyPlayers = list;
        return list;
    }

    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public boolean update() {
        if (!invalidLines.isEmpty()) {
            return true;
        }
        List<ShowAction> list = new ArrayList<>(actions);
        for (ShowAction action : list) {
            try {
                if (System.currentTimeMillis() - startTime <= action.time) {
                    continue;
                }
                action.play();
                actions.remove(action);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return actions.isEmpty();
    }

    public void displayText(String text) {
        for (UUID uuid : getNearPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            if (offset(player.getLocation(), loc) < radius) {
                player.sendMessage(ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', text));
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void playMusic(int record) {
        for (UUID uuid : getNearPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            player.playEffect(loc, Effect.RECORD_PLAY, record);
        }
    }

    public Location getLocation() {
        return loc.clone();
    }

    public FireworkEffect parseEffect(String effect) {
        String[] tokens = effect.split(",");

        // Shape
        Type shape;
        try {
            shape = Type.valueOf(tokens[0]);
        } catch (Exception e) {
            invalidLines.put(effect, "Invalid type [" + tokens[0] + "]");
            return null;
        }

        // Color
        List<Color> colors = new ArrayList<>();
        for (String color : tokens[1].split("&")) {
            if (color.equalsIgnoreCase("AQUA")) {
                colors.add(Color.AQUA);
            } else if (color.equalsIgnoreCase("BLACK")) {
                colors.add(Color.BLACK);
            } else if (color.equalsIgnoreCase("BLUE")) {
                colors.add(Color.BLUE);
            } else if (color.equalsIgnoreCase("FUCHSIA")) {
                colors.add(Color.FUCHSIA);
            } else if (color.equalsIgnoreCase("GRAY")) {
                colors.add(Color.GRAY);
            } else if (color.equalsIgnoreCase("GREEN")) {
                colors.add(Color.GREEN);
            } else if (color.equalsIgnoreCase("LIME")) {
                colors.add(Color.LIME);
            } else if (color.equalsIgnoreCase("MAROON")) {
                colors.add(Color.MAROON);
            } else if (color.equalsIgnoreCase("NAVY")) {
                colors.add(Color.NAVY);
            } else if (color.equalsIgnoreCase("OLIVE")) {
                colors.add(Color.OLIVE);
            } else if (color.equalsIgnoreCase("ORANGE")) {
                colors.add(Color.ORANGE);
            } else if (color.equalsIgnoreCase("PURPLE")) {
                colors.add(Color.PURPLE);
            } else if (color.equalsIgnoreCase("RED")) {
                colors.add(Color.RED);
            } else if (color.equalsIgnoreCase("SILVER")) {
                colors.add(Color.SILVER);
            } else if (color.equalsIgnoreCase("TEAL")) {
                colors.add(Color.TEAL);
            } else if (color.equalsIgnoreCase("WHITE")) {
                colors.add(Color.WHITE);
            } else if (color.equalsIgnoreCase("YELLOW")) {
                colors.add(Color.YELLOW);
            } else if (color.contains(";")) {
                String[] list = color.split(";");
                colors.add(Color.fromRGB(getInt(list[0]), getInt(list[1]), getInt(list[2])));
            } else {
                invalidLines.put(effect, "Invalid Color [" + color + "]");
                return null;
            }
        }
        if (colors.isEmpty()) {
            invalidLines.put(effect, "No Valid Colors");
            return null;
        }
        // Fade
        List<Color> fades = new ArrayList<>();
        if (tokens.length > 2) {
            for (String color : tokens[2].split("&")) {
                if (color.equalsIgnoreCase("AQUA")) {
                    fades.add(Color.AQUA);
                } else if (color.equalsIgnoreCase("BLACK")) {
                    fades.add(Color.BLACK);
                } else if (color.equalsIgnoreCase("BLUE")) {
                    fades.add(Color.BLUE);
                } else if (color.equalsIgnoreCase("FUCHSIA")) {
                    fades.add(Color.FUCHSIA);
                } else if (color.equalsIgnoreCase("GRAY")) {
                    fades.add(Color.GRAY);
                } else if (color.equalsIgnoreCase("GREEN")) {
                    fades.add(Color.GREEN);
                } else if (color.equalsIgnoreCase("LIME")) {
                    fades.add(Color.LIME);
                } else if (color.equalsIgnoreCase("MAROON")) {
                    fades.add(Color.MAROON);
                } else if (color.equalsIgnoreCase("NAVY")) {
                    fades.add(Color.NAVY);
                } else if (color.equalsIgnoreCase("OLIVE")) {
                    fades.add(Color.OLIVE);
                } else if (color.equalsIgnoreCase("ORANGE")) {
                    fades.add(Color.ORANGE);
                } else if (color.equalsIgnoreCase("PURPLE")) {
                    fades.add(Color.PURPLE);
                } else if (color.equalsIgnoreCase("RED")) {
                    fades.add(Color.RED);
                } else if (color.equalsIgnoreCase("SILVER")) {
                    fades.add(Color.SILVER);
                } else if (color.equalsIgnoreCase("TEAL")) {
                    fades.add(Color.TEAL);
                } else if (color.equalsIgnoreCase("WHITE")) {
                    fades.add(Color.WHITE);
                } else if (color.equalsIgnoreCase("YELLOW")) {
                    fades.add(Color.YELLOW);
                } else if (color.contains(";")) {
                    String[] list = color.split(";");
                    colors.add(Color.fromRGB(getInt(list[0]), getInt(list[1]), getInt(list[2])));
                } else if (color.equalsIgnoreCase("FLICKER") || color.equalsIgnoreCase("TRAIL")) {
                    break;
                } else {
                    invalidLines.put(effect, "Invalid Fade Color [" + color + "]");
                    return null;
                }
            }
        }
        boolean flicker = effect.toLowerCase().contains("flicker");
        boolean trail = effect.toLowerCase().contains("trail");
        // Firework
        return FireworkEffect.builder().with(shape).withColor(colors).withFade(fades).flicker(flicker).trail(trail).build();
    }

    public void syncAudioForPlayer(final CPlayer tp) {
        final AudioArea area = Audio.getInstance().getByName(areaName);
        if (area == null) {
            return;
        }
        area.triggerPlayer(tp);
        Bukkit.getScheduler().runTaskLater(ShowPlugin.getInstance(), () -> area.sync(((System.currentTimeMillis() - musicTime + 300) / 1000), tp), 20L);
    }

    public String getName() {
        return showName;
    }

    public int getRadius() {
        return radius;
    }

    public void stop() {
        for (Entity e : world.getEntities()) {
            if (!e.getType().equals(EntityType.ARMOR_STAND) || !(e.hasMetadata("show") &&
                    e.getMetadata("show").get(0).asString().equals(showID.toString()))) {
                continue;
            }
            e.remove();
        }
    }

    public static double offset(Location a, Location b) {
        return a.toVector().subtract(b.toVector()).length();
    }
}
