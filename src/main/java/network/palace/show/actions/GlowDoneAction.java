package network.palace.show.actions;

import network.palace.show.Show;
import network.palace.show.events.GlowDoneEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Marc on 9/7/15
 */
public class GlowDoneAction extends ShowAction {

    public GlowDoneAction(Show show, long time) {
        super(show, time);
    }

    @Override
    public void play() {
        ItemStack air = new ItemStack(Material.AIR);
        for (Player tp : Bukkit.getOnlinePlayers()) {
            tp.getInventory().setHelmet(air);
        }
        new GlowDoneEvent(show).call();
    }
}