package network.palace.show.actions;

import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class PulseAction extends ShowAction {
    private Location loc;

    public PulseAction(Show show, long time) {
        super(show, time);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void play(CPlayer[] nearPlayers) {
        Block pre = loc.getBlock();
        final int id = pre.getTypeId();
        final byte data = pre.getData();
        loc.getBlock().setType(Material.REDSTONE_BLOCK);
        show.addLaterAction(new BlockAction(show, time + 100, loc, id, data));
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        this.loc = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[2]);
        if (loc == null) {
            throw new ShowParseException("Invalid Location");
        }
        return this;
    }
}
