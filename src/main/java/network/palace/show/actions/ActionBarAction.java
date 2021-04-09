package network.palace.show.actions;

import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import org.bukkit.ChatColor;

public class ActionBarAction extends ShowAction {
    private String text;

    public ActionBarAction(Show show, long time) {
        super(show, time);
    }

    @Override
    public boolean play(CPlayer[] nearPlayers) {
        for (CPlayer player : nearPlayers) {
            if (player == null) continue;
            if (Show.offset(player.getLocation(), show.getLocation()) < show.getRadius()) {
                player.getActionBar().show(text);
            }
        }
        return true;
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        // 0 ActionBar text...
        StringBuilder text = new StringBuilder();
        for (int i = 2; i < args.length; i++) text.append(args[i]).append(" ");
        if (text.length() > 1) text = new StringBuilder(text.substring(0, text.length() - 1));
        this.text = ChatColor.translateAlternateColorCodes('&', text.toString());
        return this;
    }
}
