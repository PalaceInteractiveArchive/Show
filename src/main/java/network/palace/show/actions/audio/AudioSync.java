package network.palace.show.actions.audio;

import network.palace.audio.Audio;
import network.palace.audio.handlers.AudioArea;
import network.palace.show.Show;
import network.palace.show.actions.ShowAction;
import org.bukkit.Bukkit;

/**
 * Created by Marc on 2/27/16
 */
public class AudioSync extends ShowAction {
    private final float seconds;
    private AudioArea area;

    public AudioSync(Show show, long time, String areaName, float seconds) {
        super(show, time);
        this.seconds = seconds;
        this.area = Audio.getInstance().getByName(areaName);
        if (area == null) {
            Bukkit.broadcast("Error finding Audio Area " + areaName + "!", "arcade.bypass");
        }
    }

    @Override
    public void play() {
        if (area != null) {
            area.sync(seconds);
        }
    }
}