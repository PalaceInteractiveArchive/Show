package network.palace.show.actions;

import network.palace.show.Show;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class BlockAction extends ShowAction {
    public Location location;
    public int type;
    public byte data;

    public BlockAction(Show show, long time, Location location, int type, byte data) {
        super(show, time);
        this.location = location;
        this.type = type;
        this.data = data;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void play() {
        Block block = location.getBlock();
        block.setTypeId(type);
        block.setData(data);
    }
}
