package network.palace.show.handlers.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class Build {
    @Getter private String name;
    @Getter private BuildBlock[] blocks;

    public Build(String name) {
        this.name = name;
        this.blocks = null;
    }

    public Build(String name, JsonArray array) {
        this.name = name;
        this.blocks = new BuildBlock[array.size()];
        int i = 0;
        for (JsonElement element : array) {
            JsonObject object = element.getAsJsonObject();
            blocks[i++] = new BuildBlock(object.get("x").getAsInt(), object.get("y").getAsInt(),
                    object.get("z").getAsInt(), object.get("typeId").getAsInt(), object.get("data").getAsByte());
        }
    }

    public void load(World world, Vector loc1, Vector loc2) {
        List<BuildBlock> list = new ArrayList<>();
        int lowX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int lowY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int lowZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        for (int x = lowX; x <= Math.max(loc1.getBlockX(), loc2.getBlockX()); x++) {
            for (int y = lowY; y <= Math.max(loc1.getBlockY(), loc2.getBlockY()); y++) {
                for (int z = lowZ; z <= Math.max(loc1.getBlockZ(), loc2.getBlockZ()); z++) {
                    Block b = world.getBlockAt(x, y, z);
                    list.add(new BuildBlock(x - lowX, y - lowY, z - lowZ, b.getTypeId(), b.getData()));
                }
            }
        }
        blocks = new BuildBlock[list.size()];
        for (int i = 0; i < list.size(); i++) {
            blocks[i] = list.get(i);
        }
    }

    public void build(Location loc) throws IllegalStateException {
        if (blocks == null) throw new IllegalStateException("Attempted to build a Build with no block data!");
        Block b = loc.getBlock();
        for (BuildBlock block : blocks) {
            Block rel = b.getRelative(block.getX(), block.getY(), block.getZ());
            rel.setType(Material.getMaterial(block.getTypeId()));
            rel.setData(block.getData());
        }
    }

    @Getter
    @AllArgsConstructor
    public class BuildBlock {
        int x, y, z, typeId;
        byte data;
    }
}
