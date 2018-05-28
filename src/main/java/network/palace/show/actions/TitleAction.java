package network.palace.show.actions;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.TitleType;
import network.palace.show.utils.ShowUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Marc on 1/10/15
 */
public class TitleAction extends ShowAction {
    private TitleType type;
    private String title;
    private int fadeIn;
    private int fadeOut;
    private int stay;

    public TitleAction(Show show, long time) {
        super(show, time);
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

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        // 0 Title title fadeIn fadeOut stay title...
        TitleType type = ShowUtil.getTitleType(args[2].toUpperCase());
        int fadeIn = Integer.parseInt(args[3]);
        int fadeOut = Integer.parseInt(args[4]);
        int stay = Integer.parseInt(args[5]);
        StringBuilder text = new StringBuilder();
        for (int i = 6; i < args.length; i++) text.append(args[i]).append(" ");
        if (text.length() > 1) text = new StringBuilder(text.substring(0, text.length() - 1));
        this.type = type;
        this.title = ChatColor.translateAlternateColorCodes('&', text.toString());
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.stay = stay;
        return this;
    }
}
