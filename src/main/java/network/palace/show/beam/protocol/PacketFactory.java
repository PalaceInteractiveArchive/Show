package network.palace.show.beam.protocol;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.UUID;

import static com.comphenix.protocol.PacketType.Play.Server.*;

public class PacketFactory {

    /**
     * Creates a packet to spawn a squid at the location.
     *
     * @param location location to spawn the squid.
     * @return beam packet used to spawn for players.
     */
    public static WrappedBeamPacket createPacketSquidSpawn(Location location) {
        PacketContainer container = new PacketContainer(SPAWN_ENTITY_LIVING);
        container.getIntegers().write(0, EIDGen.generateEID());
        container.getUUIDs().write(0, UUID.randomUUID());
        container.getIntegers().write(1, 94);
        container.getDoubles().write(0, location.getX());
        container.getDoubles().write(1, location.getY());
        container.getDoubles().write(2, location.getZ());
        container.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
        container.getBytes().write(1, (byte) (location.getPitch() * 256.0F / 360.0F));
        Entity fakeSquid = (Entity) Accessors.getConstructorAccessor(MinecraftReflection.getCraftBukkitClass(
                "entity.CraftSquid"), MinecraftReflection.getCraftBukkitClass("CraftServer"),
                MinecraftReflection.getMinecraftClass("EntitySquid")).invoke(null,
                Accessors.getConstructorAccessor(MinecraftReflection.getMinecraftClass("EntitySquid"),
                        MinecraftReflection.getNmsWorldClass()).invoke(new Object[]{null}));
        WrappedDataWatcher wrapper = WrappedDataWatcher.getEntityWatcher(fakeSquid);
        wrapper.setObject(0, (byte) 32);
        container.getDataWatcherModifier().write(0, wrapper);
        return new WrappedBeamPacket(container);
    }

    /**
     * Creates a packet to spawn a guardian at the location.
     *
     * @param location    location to spawn the guardian.
     * @param squidPacket squid the guardian will target.
     * @return beam packet used to spawn for players.
     */
    public static WrappedBeamPacket createPacketGuardianSpawn(Location location, WrappedBeamPacket squidPacket) {
        PacketContainer container = new PacketContainer(SPAWN_ENTITY_LIVING);
        container.getIntegers().write(0, EIDGen.generateEID());
        container.getUUIDs().write(0, UUID.randomUUID());
        container.getIntegers().write(1, 68);
        container.getDoubles().write(0, location.getX());
        container.getDoubles().write(1, location.getY());
        container.getDoubles().write(2, location.getZ());
        container.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
        container.getBytes().write(1, (byte) (location.getPitch() * 256.0F / 360.0F));
        Entity fakeGuardian = (Entity) Accessors.getConstructorAccessor(MinecraftReflection.getCraftBukkitClass(
                "entity.CraftGuardian"), MinecraftReflection.getCraftBukkitClass("CraftServer"),
                MinecraftReflection.getMinecraftClass("EntityGuardian")).invoke(null,
                Accessors.getConstructorAccessor(MinecraftReflection.getMinecraftClass("EntityGuardian"),
                        MinecraftReflection.getNmsWorldClass()).invoke(new Object[]{null}));
        WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(fakeGuardian);
        watcher.setObject(0, (byte) 32);
        watcher.setObject(12, false);
        watcher.setObject(13, squidPacket.getHandle().getIntegers().read(0));
        container.getDataWatcherModifier().write(0, watcher);
        return new WrappedBeamPacket(container);
    }

    /**
     * Modifies location information of the given Spawn Packet.
     *
     * @param entitySpawnPacket SquidSpawn or GuardianSpawn packet for the entity.
     * @param location          location the entity should be spawned at.
     * @return beam packet used to spawn for players.
     */
    public static WrappedBeamPacket modifyPacket(WrappedBeamPacket entitySpawnPacket, Location location) {
        PacketContainer container = entitySpawnPacket.getHandle();
        container.getDoubles().write(0, location.getX());
        container.getDoubles().write(1, location.getY());
        container.getDoubles().write(2, location.getZ());
        container.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
        container.getBytes().write(1, (byte) (location.getPitch() * 256.0F / 360.0F));
        return entitySpawnPacket;
    }

    /**
     * Creates a packet to move an entity. Doesn't include where to move it to.
     *
     * @param entityPacket SquidSpawn or GuardianSpawn packet for the entity.
     * @return Skeleton packet for the given entity.
     */
    public static WrappedBeamPacket createPacketEntityMove(WrappedBeamPacket entityPacket) {
        PacketContainer container = new PacketContainer(ENTITY_TELEPORT);
        container.getIntegers().write(0, entityPacket.getHandle().getIntegers().read(0));
        return new WrappedBeamPacket(container);
    }
/*
    /**
     * Adds location information to a packet to move an entity.
     *
     * @param entityMovePacket EntityMove packet to add location information to.
     * @param location         location to move the entity to.
     * @return Finished packet to teleport the given entity.
     * /
    public static WrappedBeamPacket modifyPacketEntityMove(WrappedBeamPacket entityMovePacket, Location location) {
        PacketContainer container = entityMovePacket.getHandle();
        container.getDoubles().write(0, location.getX());
        container.getDoubles().write(1, location.getY());
        container.getDoubles().write(2, location.getZ());
        container.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
        container.getBytes().write(1, (byte) (location.getPitch() * 256.0F / 360.0F));
        return entityMovePacket;
    }*/

    /**
     * Creates a packet to remove the guardian and squid entities.
     *
     * @param squidPacket    SquidSpawn of the entity to remove
     * @param guardianPacket GuardianSpawn of the entity to remove
     * @return Packet to remove the guardian and squid when sent to a player.
     */
    public static WrappedBeamPacket createPacketRemoveEntities(WrappedBeamPacket squidPacket, WrappedBeamPacket guardianPacket) {
        PacketContainer container = new PacketContainer(ENTITY_DESTROY);
        container.getIntegerArrays().write(0, new int[]{
                squidPacket.getHandle().getIntegers().read(0),
                guardianPacket.getHandle().getIntegers().read(0)
        });
        return new WrappedBeamPacket(container);
    }
}
