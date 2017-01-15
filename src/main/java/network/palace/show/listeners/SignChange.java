package network.palace.show.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Created by Marc on 1/15/17.
 */
public class SignChange implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block b = event.getBlock();
        if (b.getType().equals(Material.SIGN) || b.getType().equals(Material.SIGN_POST) ||
                b.getType().equals(Material.WALL_SIGN)) {
            String l1 = event.getLine(0);
            if (l1.equalsIgnoreCase("[show]")) {
                event.setLine(0, PlayerInteract.show);
                event.setLine(3, event.getLine(1));
                event.setLine(1, "Click to sync");
                event.setLine(2, "your music to");
            }
        }
    }
}
