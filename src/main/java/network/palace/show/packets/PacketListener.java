package network.palace.show.packets;

import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.events.IncomingMessageEvent;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.packets.packets.MultiShowStart;
import network.palace.show.packets.packets.MultiShowStop;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;

public class PacketListener implements Listener {

    @EventHandler
    public void onIncomingMessage(IncomingMessageEvent event) {
        JsonObject object = event.getPacket();
        if (!object.has("id")) return;
        int id = object.get("id").getAsInt();
        /* Start Show */
        if (id == 26) {
            MultiShowStart packet = new MultiShowStart(object);
            if (!Core.getServerType().equals(packet.getServer())) return;
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
        if (id == 27) {
            MultiShowStop packet = new MultiShowStop(object);
            if (!Core.getServerType().equals(packet.getServer())) return;
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
