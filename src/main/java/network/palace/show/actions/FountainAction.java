package network.palace.show.actions;

import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.BlockData;
import network.palace.show.handlers.Fountain;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class FountainAction extends ShowAction {
    private double duration;
    private Location loc;
    private int type;
    private byte data;
    private Vector force;

    public FountainAction(Show show, long time) {
        super(show, time);
    }

    @Override
    public void play(CPlayer[] nearPlayers) {
        ShowPlugin.getInstance().getFountainManager().addFountain(new Fountain(loc, duration, type, data, force));
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        Location loc = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[4]);
        Double[] values = WorldUtil.strToDoubleList(show.getWorld().getName() + "," + args[5]);
        double duration = Double.parseDouble(args[3]);
        Vector force = new Vector(values[0], values[1], values[2]);
        try {
            BlockData data = ShowUtil.getBlockData(args[2]);
            this.duration = duration;
            this.loc = loc;
            this.type = data.getId();
            this.data = data.getData();
            this.force = force;
        } catch (ShowParseException e) {
            throw new ShowParseException(e.getReason());
        }
        return this;
    }
}