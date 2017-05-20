package network.palace.show.actions;

import network.palace.show.Show;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Marc on 7/1/15
 */
public class FakeBlockAction extends ShowAction {
    private final Show show;
    private final Location loc;
    private final int id;
    private final byte data;
//    private PacketContainer packet;

    @SuppressWarnings("deprecation")
    public FakeBlockAction(Show show, long time, Location loc, int id, byte data) {
        super(show, time);
        this.show = show;
        this.loc = loc;
        this.id = id;
        this.data = data;
//        BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
//        packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
//        StructureModifier<WrappedBlockData> m = packet.getBlockData();
//        packet.getBlockPositionModifier().write(0, pos);
//        m.getValues().get(0).setTypeAndData(Material.getMaterial(id), data);
    }

    @Override
    public void play() {
//        if (packet == null) {
//            return;
//        }
//        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        for (UUID uuid : show.getNearPlayers()) {
            Player tp = Bukkit.getPlayer(uuid);
            if (tp == null) {
                continue;
            }
            tp.sendBlockChange(loc, id, data);
//            try {
//                pm.sendServerPacket(tp, packet);
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
        }
    }
}