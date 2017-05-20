package network.palace.show.actions;

import com.comphenix.protocol.wrappers.EnumWrappers;
import network.palace.core.Core;
import network.palace.core.packets.AbstractPacket;
import network.palace.core.packets.server.entity.WrapperPlayServerEntityEquipment;
import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Marc on 9/6/15
 */
public class GlowAction extends ShowAction {
    private int radius;
    private ItemStack helm;
    private Location loc;

    public GlowAction(Show show, long time, Color color, Location loc, int radius) {
        super(show, time);
        helm = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helm.getItemMeta();
        meta.setColor(color);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Glow With The Show Ears");
        meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "This is part of the Show, don't move it!"));
        helm.setItemMeta(meta);
        this.loc = loc;
        this.radius = radius;
    }

    @Override
    public void play() {
        List<AbstractPacket> packets = new ArrayList<>();
        Collection<CPlayer> collection = Core.getPlayerManager().getOnlinePlayers().stream().filter(tp -> tp.getLocation().distance(loc) <= radius).collect(Collectors.toList());
        collection.forEach(tp -> {
            WrapperPlayServerEntityEquipment p = new WrapperPlayServerEntityEquipment();
            p.setEntityID(tp.getEntityId());
            p.setSlot(EnumWrappers.ItemSlot.HEAD);
            p.setItem(helm);
            packets.add(p);
        });
        collection.forEach(tp -> packets.stream().forEach(tp::sendPacket));
    }
}