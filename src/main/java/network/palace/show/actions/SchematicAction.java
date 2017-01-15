package network.palace.show.actions;

import network.palace.show.Show;
import network.palace.show.TerrainManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SchematicAction extends ShowAction {
    public static WorldEditPlugin wep;
    private static TerrainManager tm;
    private Location loc;
    private String fname;
    private boolean noAir;

    public SchematicAction(Show show, long time, Location loc, String fname, boolean noAir) {
        super(show, time);
        this.loc = loc;
        this.fname = fname;
        this.noAir = noAir;
    }

    public static void setWorldEdit(WorldEditPlugin pl) {
        wep = pl;
        tm = new TerrainManager(wep, Bukkit.getWorlds().get(0));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void play() {
        try {
            tm.loadSchematic(wep, fname, loc, noAir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}