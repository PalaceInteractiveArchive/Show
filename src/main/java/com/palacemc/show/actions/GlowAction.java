package com.palacemc.show.actions;

import com.palacemc.show.Show;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Collections;

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
        for (Player tp : Bukkit.getOnlinePlayers()) {
//            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
//            new PacketPlayOutEntityEquipment(((CraftPlayer)tp).getHandle().getId(),1,new net.minecraft.server.v1_11_R1.ItemStack(Item.getById(298),));
            if (tp.getLocation().distance(loc) <= radius) {
                tp.getInventory().setHelmet(helm);
            }
        }
    }
}