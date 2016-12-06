package com.palacemc.show.actions;

import com.palacemc.show.Show;
import org.bukkit.Bukkit;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.audioserver.AudioArea;

/**
 * Created by Marc on 2/27/16
 */
public class AudioSync extends ShowAction {
    private final float seconds;
    private AudioArea area;

    public AudioSync(Show show, long time, String areaName, float seconds) {
        super(show, time);
        this.seconds = seconds;
        this.area = MCMagicCore.audioServer.getByName(areaName);
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