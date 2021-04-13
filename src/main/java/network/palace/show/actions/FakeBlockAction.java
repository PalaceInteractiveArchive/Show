package network.palace.show.actions;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.packets.server.block.WrapperPlayServerBlockChange;
import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.BlockData;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Created by Marc on 7/1/15
 */
@Getter
@Setter
@SuppressWarnings("deprecation")
public class FakeBlockAction extends ShowAction {
    private Location loc;
    private int id;
    private byte data;

    public FakeBlockAction(Show show, long time) {
        super(show, time);
    }

    public FakeBlockAction(Show show, long time, Location loc, int id, byte data) {
        super(show, time);
        this.loc = loc;
        this.id = id;
        this.data = data;
    }

    @Override
    public void play(CPlayer[] nearPlayers) {
        try {
            WrapperPlayServerBlockChange p = new WrapperPlayServerBlockChange();
            p.setLocation(new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            p.setBlockData(WrappedBlockData.createData(Material.getMaterial(id), data));
            for (CPlayer tp : nearPlayers) {
                if (tp != null) tp.sendPacket(p);
            }
        } catch (Exception e) {
            Core.logMessage("FakeBlockAction", ChatColor.RED + "Error sending FakeBlockAction for type (" +
                    id + ":" + data + ") at location " + loc.getX() + "," + loc.getY() + "," + loc.getZ() + " at time " +
                    time + " for show " + show.getName());
            e.printStackTrace();
        }
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        Location loc = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]);
        if (loc == null) {
            throw new ShowParseException("Invalid Location " + line);
        }
        try {
            BlockData data = ShowUtil.getBlockData(args[2]);
            this.loc = loc;
            this.id = data.getId();
            this.data = data.getData();
        } catch (ShowParseException e) {
            throw new ShowParseException(e.getReason());
        }
        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new FakeBlockAction(show, time, loc, id, data);
    }
}