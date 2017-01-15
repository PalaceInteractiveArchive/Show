package network.palace.show.actions;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.handlers.TitleType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Marc on 1/10/15
 */
public class TitleAction extends ShowAction {
    public TitleType type;
    public String title;
    public int fadeIn;
    public int fadeOut;
    public int stay;

    public TitleAction(Show show, long time, TitleType type, String title, int fadeIn, int fadeOut, int stay) {
        super(show, time);
        this.type = type;
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.stay = stay;
    }

    @Override
    public void play() {
        for (UUID uuid : show.getNearPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            if (Show.offset(player.getLocation(), show.getLocation()) < show.getRadius()) {
                CPlayer p = Core.getPlayerManager().getPlayer(player.getUniqueId());
                if (type.equals(TitleType.TITLE)) {
                    p.getTitle().show(title, "", fadeIn, stay, fadeOut);
                } else {
                    p.getTitle().show("", title, fadeIn, stay, fadeOut);
                }
            }
        }
    }
}
