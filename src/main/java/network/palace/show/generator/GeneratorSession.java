package network.palace.show.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.UUID;

@Getter
public class GeneratorSession {
    private UUID uuid;
    @Setter private Location corner;
    @Setter private ShowSelection initialScene;

    public GeneratorSession(UUID uuid) {
        this.uuid = uuid;
    }

    @Getter
    @AllArgsConstructor
    public static class ShowSelection {
        private Location max;
        private Location min;

        public Block getBlock(int dx, int dy, int dz) {
            return min.clone().add(dx, dy, dz).getBlock();
        }

        public int getXLength() {
            return max.getBlockX() - min.getBlockX() + 1;
        }

        public int getYLength() {
            return max.getBlockY() - min.getBlockY() + 1;
        }

        public int getZLength() {
            return max.getBlockZ() - min.getBlockZ() + 1;
        }

        public boolean equalSize(ShowSelection selection) {
            return getXLength() == selection.getXLength() && getYLength() == selection.getYLength() && getZLength() == selection.getZLength();
        }

        @Override
        public String toString() {
            return max.getBlockX() + "," + max.getBlockY() + "," + max.getBlockZ() + " -> " + min.getBlockX() + "," + min.getBlockY() + "," + min.getBlockZ();
        }
    }
}
