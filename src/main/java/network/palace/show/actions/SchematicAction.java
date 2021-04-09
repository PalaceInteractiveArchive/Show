package network.palace.show.actions;

import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SchematicAction extends ShowAction {
    private Location loc;
    private String fname;
    private boolean noAir;

    public SchematicAction(Show show, long time) {
        super(show, time);
    }

    public SchematicAction(Show show, long time, Location loc, String fname, boolean noAir) {
        super(show, time);
        this.loc = loc;
        this.fname = fname;
        this.noAir = noAir;
    }

    @Override
    public void play(CPlayer[] nearPlayers) {
        try {
            show.getTerrainManager().loadSchematic(show.getWorldEditPlugin(), fname, loc, noAir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        if (MiscUtil.checkIfInt(args[3]) && MiscUtil.checkIfInt(args[4]) && MiscUtil.checkIfInt(args[5])) {
            try {
                int x = Integer.parseInt(args[3]);
                int y = Integer.parseInt(args[4]);
                int z = Integer.parseInt(args[5]);
                Location pasteLoc = new Location(Bukkit.getWorld(args[6]), x, y, z);
                boolean noAir = !args[7].toLowerCase().contains("false");
                this.loc = pasteLoc;
                this.fname = args[2];
                this.noAir = noAir;
            } catch (Exception e) {
                e.printStackTrace();
                throw new ShowParseException("Error creating Schematic Action!");
            }
        } else {
            throw new ShowParseException("Invalid X, Y, or Z Coordinates!");
        }
        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new SchematicAction(show, time, loc, fname, noAir);
    }
}