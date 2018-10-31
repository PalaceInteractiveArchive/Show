package network.palace.show;

import com.comphenix.protocol.ProtocolLibrary;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.plugin.Plugin;
import network.palace.core.plugin.PluginInfo;
import network.palace.show.actions.SchematicAction;
import network.palace.show.commands.CommandShow;
import network.palace.show.listeners.PlayerInteract;
import network.palace.show.listeners.SignChange;
import network.palace.show.utils.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marc on 12/6/16.
 */
@PluginInfo(name = "Show", version = "1.2.2", depend = {"Audio", "Core"}, canReload = true)
public class ShowPlugin extends Plugin {
    @Getter private ArmorStandManager armorStandManager;
    @Getter private FountainManager fountainManager;
    private static ShowPlugin instance;
    private static HashMap<String, Show> shows = new HashMap<>();
    private int taskid = 0;

    public static ShowPlugin getInstance() {
        return instance;
    }

    @Override
    protected void onPluginEnable() throws Exception {
        instance = this;
        armorStandManager = new ArmorStandManager();
        fountainManager = new FountainManager();
        FileUtil.setupFiles();
        registerCommand(new CommandShow());
        registerListener(fountainManager);
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
                Show show = entry.getValue();
                if (show.update()) {
                    show.stop();
                    shows.remove(entry.getKey());
                }
            }
        }, 0L, 1L).getTaskId();
    }

    @Override
    protected void onPluginDisable() throws Exception {
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
        int size = shows.size();
        if (size > 0) {
            for (CPlayer p : Core.getPlayerManager().getOnlinePlayers()) {
                if (p.getRank().getRankId() >= Rank.TRAINEE.getRankId()) {
                    p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Reloading Show plugin, there are currently " +
                            size + " shows running!");
                }
            }
        }
        Bukkit.getScheduler().cancelTask(taskid);
        for (Show s : shows.values()) {
            s.stop();
        }
        shows.clear();
    }

    public static HashMap<String, Show> getShows() {
        return new HashMap<>(shows);
    }

    public static void startShow(String name, Show show) {
        shows.put(name, show);
    }

    public static void stopShow(String name) {
        shows.remove(name);
    }
}
