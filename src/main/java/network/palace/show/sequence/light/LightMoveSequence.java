package network.palace.show.sequence.light;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.injector.PacketConstructor;
import java.lang.reflect.InvocationTargetException;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.ShowCrystal;
import network.palace.show.sequence.ShowSequence;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class LightMoveSequence extends ShowSequence {

    private boolean hasProtocolErrored;
    private double seconds;
    private Vector movement;
    private ShowCrystal crystal;

    public LightMoveSequence(Show show, long time, ShowCrystal crystal, Vector movement, double seconds) {
        super(show, time);
        this.movement = movement;
        this.crystal = crystal;
        this.seconds = seconds;
    }

    @Override
    public boolean run() {
        if (!crystal.isSpawned()) {
            Bukkit.broadcast("EnderCrystal with ID " + crystal.getId() + " has not spawned.", "palace.core.rank.mod");
            return true;
        }

        EnderCrystal enderCrystal = crystal.getCrystal();
        int ticks = (int) (seconds * 20);
        new BukkitRunnable() {

            int time = 0;

            @Override
            public void run() {
                if (time >= ticks) {
                    cancel();
                }

                time++;
                enderCrystal.teleport(enderCrystal.getLocation().clone().add(movement.clone().divide(new Vector(ticks, ticks, ticks))));
                ProtocolManager pm = ProtocolLibrary.getProtocolManager();
                PacketConstructor pc = pm.createPacketConstructor(Server.ENTITY_TELEPORT, enderCrystal);
                Bukkit.getOnlinePlayers().forEach(player -> {
                    try {
                        pm.sendServerPacket(player, pc.createPacket(enderCrystal));
                    }
                    catch (InvocationTargetException e) {
                        if (hasProtocolErrored) {
                            return;
                        }

                        hasProtocolErrored = true;
                        Bukkit.broadcast("Failed to update " + crystal.getId() + "'s position for 1 or more players.", "palace.core.rank.mod");
                        e.printStackTrace();
                    }
                });
            }
        }.runTaskTimer(ShowPlugin.getInstance(), 1L, 1L);
        return true;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        return this;
    }
}
