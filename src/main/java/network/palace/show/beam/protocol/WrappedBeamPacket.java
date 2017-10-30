package network.palace.show.beam.protocol;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class WrappedBeamPacket {
    private final PacketContainer handle;

    /**
     * Wraps the packet.
     *
     * @param container packet to wrap.
     */
    public WrappedBeamPacket(PacketContainer container) {
        this.handle = container;
    }

    /**
     * Sends the packet to a lucky receiver!
     *
     * @param receiver player to send the packet to.
     */
    public void send(Player receiver) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, this.handle);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException("Failed to send beam packet to player.", ex);
        }
    }

    /**
     * Get the packet container.
     *
     * @return ProtocolLib packet container.
     */
    public PacketContainer getHandle() {
        return this.handle;
    }
}
