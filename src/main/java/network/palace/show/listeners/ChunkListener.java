package network.palace.show.listeners;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.stream.Stream;

public class ChunkListener implements Listener {

    @EventHandler
    public void onUnload(ChunkUnloadEvent event) {
        Stream.of(event.getChunk().getEntities()).filter(ArmorStand.class::isInstance)
                .filter(entity -> entity.getMetadata("show").stream().anyMatch(MetadataValue::asBoolean)).forEach(Entity::remove);
    }
}
