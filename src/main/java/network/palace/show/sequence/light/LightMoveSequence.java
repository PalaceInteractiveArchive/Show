package network.palace.show.sequence.light;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.injector.PacketConstructor;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.ShowCrystal;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.sequence.handlers.SequenceState;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;

public class LightMoveSequence extends ShowSequence {

    private boolean hasProtocolErrored;
    private double seconds;
    private Vector movement;
    private boolean first = true;
    private ShowCrystal crystal;

    public LightMoveSequence(Show show, long time, ShowCrystal crystal) {
        super(show, time);
        this.crystal = crystal;
    }

    @Override
    public boolean run() {
        if (!crystal.isSpawned()) {
            ShowUtil.logDebug(show.getName(), "EnderCrystal with ID " + crystal.getId() + " has not spawned.");
            return true;
        }

        EnderCrystal enderCrystal = crystal.getCrystal();
        int ticks = (int) (seconds * 20);
        new BukkitRunnable() {

            int time = 0;

            @Override
            public void run() {
                if (time >= ticks) cancel();
                time++;

                if (first && crystal.getState().equals(SequenceState.ACTUAL)) {
                    first = false;
                    Location enderLoc = enderCrystal.getLocation();
                    Location targetLoc = movement.toLocation(enderCrystal.getWorld());
                    movement = new Vector(targetLoc.getX() - enderLoc.getX(), targetLoc.getY() - enderLoc.getY(), targetLoc.getZ() - enderLoc.getZ());
                }

                enderCrystal.teleport(enderCrystal.getLocation().clone().add(movement.clone().divide(new Vector(ticks, ticks, ticks))));

                ProtocolManager pm = ProtocolLibrary.getProtocolManager();
                PacketConstructor pc = pm.createPacketConstructor(Server.ENTITY_TELEPORT, enderCrystal);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    try {
                        pm.sendServerPacket(player, pc.createPacket(enderCrystal));
                    } catch (InvocationTargetException e) {
                        Bukkit.getLogger().warning("Failed to update " + crystal.getId() + "'s position for 1 or more players.");
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskTimer(ShowPlugin.getInstance(), 1L, 1L);
        return true;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        Double[] doubles = WorldUtil.strToDoubleList(show.getWorld().getName() + "," + args[3]);
        this.movement = new Vector(doubles[0], doubles[1], doubles[2]);
        this.seconds = Double.parseDouble(args[4]);
        return this;
    }
}
