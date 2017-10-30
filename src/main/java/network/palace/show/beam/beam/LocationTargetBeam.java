package network.palace.show.beam.beam;

import com.google.common.base.Preconditions;
import network.palace.show.beam.protocol.PacketFactory;
import network.palace.show.beam.protocol.WrappedBeamPacket;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LocationTargetBeam {
    private final WrappedBeamPacket packetSquidSpawn;
    private final WrappedBeamPacket packetSquidMove;
    private final WrappedBeamPacket packetGuardianSpawn;
    private final WrappedBeamPacket packetGuardianMove;
    private final WrappedBeamPacket packetRemoveEntities;

    /**
     * Create a guardian beam. This sets up the packets.
     *
     * @param startingPosition Position to start the beam, or the position which the effect 'moves towards'.
     * @param endingPosition   Position to stop the beam, or the position which the effect 'moves away from'.
     */
    public LocationTargetBeam(Location startingPosition, Location endingPosition) {
        Preconditions.checkNotNull(startingPosition, "startingPosition cannot be null");
        Preconditions.checkNotNull(endingPosition, "endingPosition cannot be null");
        Preconditions.checkState(startingPosition.getWorld().equals(endingPosition.getWorld()), "startingPosition and endingPosition must be in the same world");

        this.packetSquidSpawn = PacketFactory.createPacketSquidSpawn(startingPosition);
        this.packetSquidMove = PacketFactory.createPacketEntityMove(this.packetSquidSpawn);
        this.packetGuardianSpawn = PacketFactory.createPacketGuardianSpawn(endingPosition, this.packetSquidSpawn);
        this.packetGuardianMove = PacketFactory.createPacketEntityMove(this.packetGuardianSpawn);
        this.packetRemoveEntities = PacketFactory.createPacketRemoveEntities(this.packetSquidSpawn, this.packetGuardianSpawn);
    }

    /**
     * Send the packets to create the beam to the player.
     *
     * @param player player to whom the beam will be sent.
     */
    public void start(Player player) {
        this.packetSquidSpawn.send(player);
        this.packetGuardianSpawn.send(player);
    }

    /**
     * Sets the position of the beam which the effect 'moves away from'.
     *
     * @param player   player who should receive the update. They MUST have been showed the beam already.
     * @param location location of the new position.
     */
    public void setStartingPosition(Player player, Location location) {
        PacketFactory.modifyPacket(this.packetSquidSpawn, location);
        PacketFactory.modifyPacket(this.packetSquidMove, location).send(player);
    }

    /**
     * Sets the position of the beam which the effect 'moves towards'.
     *
     * @param player   player who should receive the update. They MUST have been showed the beam already.
     * @param location location of the new position.
     */
    public void setEndingPosition(Player player, Location location) {
        PacketFactory.modifyPacket(this.packetGuardianSpawn, location);
        PacketFactory.modifyPacket(this.packetGuardianMove, location).send(player);
    }

    /**
     * Cleans up the entities on the player's side.
     *
     * @param player player who needs the cleanup.
     */
    public void cleanup(Player player) {
        this.packetRemoveEntities.send(player);
    }
}
