package network.palace.show;

import com.comphenix.protocol.ProtocolLibrary;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.plugin.Plugin;
import network.palace.core.plugin.PluginInfo;
import network.palace.show.commands.ShowBuildCommand;
import network.palace.show.commands.ShowCommand;
import network.palace.show.commands.ShowGenCommand;
import network.palace.show.generator.ShowGenerator;
import network.palace.show.listeners.ChunkListener;
import network.palace.show.listeners.PlayerInteract;
import network.palace.show.listeners.SignChange;
import network.palace.show.utils.BuildUtil;
import network.palace.show.utils.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marc on 12/6/16.
 */
@PluginInfo(name = "Show", version = "1.4.6", depend = {"Audio", "Core"}, softdepend = "WorldEdit", canReload = true)
public class ShowPlugin extends Plugin {
    @Getter private ArmorStandManager armorStandManager;
    @Getter private FountainManager fountainManager;
    @Getter private static ShowGenerator showGenerator;
    @Getter private static BuildUtil buildUtil;
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
        showGenerator = new ShowGenerator();
        buildUtil = new BuildUtil();
        FileUtil.setupFiles();
        registerCommand(new ShowCommand());
        registerCommand(new ShowBuildCommand());
        registerCommand(new ShowGenCommand());
        registerListener(fountainManager);
        registerListener(new PlayerInteract());
        registerListener(new SignChange());
        registerListener(new ChunkListener());
        // Show Ticker
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
