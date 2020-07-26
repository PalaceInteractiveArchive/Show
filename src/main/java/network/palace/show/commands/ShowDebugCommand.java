package network.palace.show.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;

@CommandMeta(description = "Toggle show debug mode", rank = Rank.TRAINEETECH)
public class ShowDebugCommand extends CoreCommand {

    public ShowDebugCommand() {
        super("showdebug");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (!player.getRegistry().hasEntry("show_debug")) {
            player.getRegistry().addEntry("show_debug", true);
            player.sendMessage(ChatColor.AQUA + "[ShowDebug] - " + ChatColor.GREEN + "Enabled");
        } else {
            player.getRegistry().removeEntry("show_debug");
            player.sendMessage(ChatColor.AQUA + "[ShowDebug] - " + ChatColor.RED + "Disabled");
        }
    }
}
