package network.palace.show;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.plugin.Plugin;
import network.palace.core.plugin.PluginInfo;
import network.palace.show.actions.SchematicAction;
import network.palace.show.commands.Commandshow;
import network.palace.show.listeners.PlayerInteract;
import network.palace.show.listeners.SignChange;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marc on 12/6/16.
 */
@PluginInfo(name = "Show", version = "1.0.3", depend = {"Audio", "Core"}, canReload = true)
public class ShowPlugin extends Plugin {
    @Getter private ArmorStandManager armorStandManager;
    @Getter private FountainManager fountainManager;
    private static ShowPlugin instance;
    @Getter private static HashMap<String, Show> shows = new HashMap<>();
    private int taskid = 0;

    public static ShowPlugin getInstance() {
        return instance;
    }

    @Override
    protected void onPluginEnable() throws Exception {
        instance = this;
        armorStandManager = new ArmorStandManager();
        fountainManager = new FountainManager();
        File file = new File("plugins/Show/");
        if (!file.exists()) {
            file.mkdir();
        }
        File file2 = new File("plugins/Show/shows/");
        if (!file2.exists()) {
            file2.mkdir();
        }
        registerCommand(new Commandshow());
        registerListener(new FountainManager());
        registerListener(new PlayerInteract());
        registerListener(new SignChange());
        // Show Ticker
        org.bukkit.plugin.Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (plugin instanceof WorldEditPlugin) {
            SchematicAction.setWorldEdit((WorldEditPlugin) plugin);
        } else {
            Core.logMessage("Show", "Error finding WorldEdit!");
        }
        taskid = Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Map.Entry<String, Show> entry : new HashMap<>(shows).entrySet()) {
                if (entry.getValue().update()) {
                    entry.getValue().stop();
                    shows.remove(entry.getKey());
                }
            }
        }, 0L, 1L).getTaskId();
    }

    @Override
    protected void onPluginDisable() throws Exception {
        int size = shows.size();
        if (size > 0) {
            for (CPlayer p : Core.getPlayerManager().getOnlinePlayers()) {
                if (p.getRank().getRankId() < Rank.SQUIRE.getRankId()) {
                    continue;
                }
                p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Reloading Show plugin, there are currently " +
                        size + " shows running!");
            }
        }
        Bukkit.getScheduler().cancelTask(taskid);
        for (Show s : shows.values()) {
            s.stop();
        }
    }
}
