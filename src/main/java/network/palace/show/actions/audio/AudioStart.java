package network.palace.show.actions.audio;

import network.palace.audio.Audio;
import network.palace.audio.handlers.AudioArea;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.actions.ShowAction;
import network.palace.show.exceptions.ShowParseException;
import org.bukkit.ChatColor;

/**
 * Created by Marc on 2/27/16
 */
public class AudioStart extends ShowAction {
    private AudioArea area;

    public AudioStart(Show show, long time) {
        super(show, time);
    }

    public AudioStart(Show show, long time, AudioArea area) {
        super(show, time);
        this.area = area;
    }

    @Override
    public void play(CPlayer[] nearPlayers) {
        if (area == null) return;
        show.setMusicTime(System.currentTimeMillis());
        show.setAreaName(area.getAreaName());
        for (CPlayer tp : nearPlayers) {
            if (tp != null) area.triggerPlayer(tp);
        }
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        area = Audio.getInstance().getByName(args[2]);
        if (area == null) Core.logMessage(ChatColor.RED + "Show Error", "Error finding Audio Area " + args[2] + "!");
        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new AudioStart(show, time, area);
    }
}