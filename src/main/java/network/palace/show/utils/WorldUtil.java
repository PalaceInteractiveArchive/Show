package network.palace.show.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldUtil {

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

    public static Double[] strToDoubleList(String string) {
        if (string.length() == 0) {
            return null;
        }
        String[] tokens = string.split(",");
        Double[] doublelist = new Double[3];
        doublelist[0] = Double.parseDouble(tokens[1]);
        doublelist[1] = Double.parseDouble(tokens[2]);
        doublelist[2] = Double.parseDouble(tokens[3]);
        return doublelist;
        // return new location(cur, Double.parseDouble(tokens[1]),
        // Double.parseDouble(tokens[2]),
        // Double.parseDouble(tokens[3]));
    }
}
