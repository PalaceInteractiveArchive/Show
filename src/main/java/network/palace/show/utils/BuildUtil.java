package network.palace.show.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import network.palace.show.handlers.block.Build;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class BuildUtil {

    public WorldEditPlugin getWorldEditPlugin() {
        org.bukkit.plugin.Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (plugin instanceof WorldEditPlugin) {
            return (WorldEditPlugin) plugin;
        }
        return null;
    }

    public Build loadBuild(String name) throws IOException {
        File file = new File("plugins/Show/builds/" + name + ".build");
        StringBuilder json = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            json.append(line);
        }
        JsonElement element = new Gson().fromJson(json.toString(), JsonElement.class);
        if (element == null || !element.isJsonArray()) throw new IOException("Error parsing build JSON!");
        return new Build(name, element.getAsJsonArray());
    }

    public void saveBuild(Build build) throws IOException {
        Build.BuildBlock[] blocks = build.getBlocks();
        JsonArray array = new JsonArray();
        for (Build.BuildBlock block : blocks) {
            JsonObject object = new JsonObject();
            object.addProperty("x", block.getX());
            object.addProperty("y", block.getY());
            object.addProperty("z", block.getZ());
            object.addProperty("typeId", block.getTypeId());
            object.addProperty("data", block.getData());
            array.add(object);
        }
        Files.write(Paths.get(new File("plugins/Show/builds/" + build.getName() + ".build").toURI()), Collections.singletonList(array.toString()), Charset.forName("UTF-8"));
    }
}
