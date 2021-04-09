package network.palace.show.actions;

import com.comphenix.protocol.wrappers.EnumWrappers;
import network.palace.core.packets.AbstractPacket;
import network.palace.core.packets.server.entity.WrapperPlayServerEntityEquipment;
import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Marc on 9/6/15
 */
public class GlowAction extends ShowAction {
    public static String name = ChatColor.LIGHT_PURPLE + "Glow With The Show Ears";
    private int radius;
    private final ItemStack helm;
    private Location loc;

    public GlowAction(Show show, long time) {
        super(show, time);
        helm = new ItemStack(Material.LEATHER_HELMET);
    }

    @Override
    public boolean play(CPlayer[] nearPlayers) {
        List<AbstractPacket> packets = new ArrayList<>();
        WrapperPlayServerEntityEquipment p = new WrapperPlayServerEntityEquipment();
        p.setSlot(EnumWrappers.ItemSlot.HEAD);
        p.setItem(helm);
        Arrays.stream(nearPlayers)
                .filter(tp -> tp.getWorld().getUID().equals(loc.getWorld().getUID()))
                .filter(tp -> tp.getLocation().distance(loc) <= radius)
                .collect(Collectors.toList())
                .forEach(tp -> {
                    p.setEntityID(tp.getEntityId());
                    tp.sendPacket(p);
                });
        return true;
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        try {
            Color color;
            if (args[2].contains(",")) {
                String[] list = args[2].split(",");
                color = Color.fromRGB(ShowUtil.getInt(list[0]), ShowUtil.getInt(list[1]), ShowUtil.getInt(list[2]));
            } else {
                try {
                    color = ShowUtil.colorFromString(args[2]);
                } catch (ShowParseException e) {
                    throw new ShowParseException("Invalid Glow Color " + args[2]);
                }
            }
            if (color == null) {
                throw new ShowParseException("Invalid Glow Color " + args[2]);
            }
            LeatherArmorMeta meta = (LeatherArmorMeta) helm.getItemMeta();
            meta.setColor(color);
            meta.setDisplayName(name);
            meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "This is part of the Show, don't move it!"));
            helm.setItemMeta(meta);
            this.loc = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]);
            this.radius = ShowUtil.getInt(args[4]);
        } catch (Exception e) {
            throw new ShowParseException("Invalid Glow Line");
        }
        return this;
    }
}