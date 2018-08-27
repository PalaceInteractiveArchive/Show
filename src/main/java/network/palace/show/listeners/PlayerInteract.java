package network.palace.show.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.show.ShowPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Marc on 1/15/17.
 */
public class PlayerInteract implements Listener {
    public static String show = ChatColor.BLUE + "[Show]";

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) return;
        Action action = event.getAction();
        Rank rank = player.getRank();
        final ItemStack hand = player.getInventory().getItemInMainHand();
        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Material type = event.getClickedBlock().getType();
            if (type.equals(Material.SIGN) || type.equals(Material.SIGN_POST) || type.equals(Material.WALL_SIGN)) {
                Sign s = (Sign) event.getClickedBlock().getState();
                if (s.getLine(0).equals(show)) {
                    String show = ChatColor.stripColor(s.getLine(3));
                    ShowPlugin.getShows().values().stream().filter(sh -> sh.getName().equals(show)).forEach(sh -> {
                        sh.syncAudioForPlayer(player);
                        player.sendMessage(ChatColor.GREEN + "Syncing your audio for " + show + "!");
                    });
                }
            }
        }
    }
}
