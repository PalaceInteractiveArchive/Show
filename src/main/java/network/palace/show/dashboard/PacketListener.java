package network.palace.show.dashboard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import network.palace.core.Core;
import network.palace.core.events.IncomingPacketEvent;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.dashboard.packets.park.PacketShowStart;
import network.palace.show.dashboard.packets.park.PacketShowStop;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;

public class PacketListener implements Listener {

    @EventHandler
    public void onIncomingPacket(IncomingPacketEvent event) {
        String data = event.getPacket();
        JsonObject object = (JsonObject) new JsonParser().parse(data);
        if (!object.has("id")) {
            return;
        }
        int id = object.get("id").getAsInt();
        /* Start Show */
        if (id == 79) {
            PacketShowStart packet = new PacketShowStart().fromJSON(object);
            String showName = packet.getShowName();
            Core.debugLog("Trying to start multi-show: " + showName);
            if (ShowPlugin.getShows().containsKey(showName)) return;
            File f = new File("plugins/Show/shows/" + packet.getWorld() + "/" + showName + ".show");
            World world;
            if (!f.exists() || (world = Bukkit.getWorld(packet.getWorld())) == null) return;
            ShowPlugin.startShow(showName, new Show(f, world));
            Core.debugLog("Started multi-show: " + showName);
        }
        /* Stop Show */
        if (id == 80) {
            PacketShowStop packet = new PacketShowStop().fromJSON(object);
            String showName = packet.getShowName();
            Core.debugLog("Trying to stop multi-show: " + showName);
            if (!ShowPlugin.getShows().containsKey(showName)) return;
            Show s = ShowPlugin.getShows().get(showName);
            if (s == null) return;
            s.stop();
            ShowPlugin.stopShow(showName);
            ShowPlugin.getShows().remove(showName);
            Core.debugLog("Stopped multi-show: " + showName);
        }
    }
}

