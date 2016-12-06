package com.palacemc.show.actions;

import com.palacemc.show.Show;
import net.minecraft.server.v1_11_R1.Block;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.PacketPlayOutBlockChange;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Marc on 7/1/15
 */
public class FakeBlockAction extends ShowAction {
    private final Show show;
    private PacketPlayOutBlockChange packet;

    public FakeBlockAction(Show show, long time, Location loc, int id, byte data) {
        super(show, time);
        this.show = show;
        BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        packet = new PacketPlayOutBlockChange(((CraftWorld) loc.getWorld()).getHandle(), pos);
        packet.block = Block.getById(id).fromLegacyData(data);
    }

    @Override
    public void play() {
        for (UUID uuid : show.getNearPlayers()) {
            Player tp = Bukkit.getPlayer(uuid);
            if (tp == null) {
                continue;
            }
            ((CraftPlayer) tp).getHandle().playerConnection.sendPacket(packet);
        }
    }
}