package network.palace.show.actions.audio;

import network.palace.audio.Audio;
import network.palace.audio.handlers.AudioArea;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.actions.ShowAction;
import org.bukkit.ChatColor;

import java.util.UUID;

/**
 * Created by Marc on 2/27/16
 */
public class AudioStart extends ShowAction {
    private AudioArea area;

    public AudioStart(Show show, long time, String areaName) {
        super(show, time);
        area = Audio.getInstance().getByName(areaName);
        if (area == null) {
            Core.logMessage(ChatColor.RED + "Show Error", "Error finding Audio Area " + areaName + "!");
        }
    }

    @Override
    public void play() {
        if (area == null) {
            return;
        }
        show.musicTime = System.currentTimeMillis();
        show.areaName = area.getAreaName();
        if (area != null) {
            for (UUID uuid : show.getNearPlayers()) {
                CPlayer tp = Core.getPlayerManager().getPlayer(uuid);
                if (tp != null) {
                    area.triggerPlayer(tp);
                }
            }
        }
    }
}