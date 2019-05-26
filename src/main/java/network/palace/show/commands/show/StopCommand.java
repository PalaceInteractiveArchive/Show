package network.palace.show.commands.show;

import network.palace.core.command.CommandException;
import network.palace.core.command.CoreCommand;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author Marc
 * @since 8/2/17
 */
public class StopCommand extends CoreCommand {

    public StopCommand() {
        super("stop");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/show stop [Show Name]");
            return;
        }
        if (!ShowPlugin.getShows().containsKey(args[0])) {
            sender.sendMessage(ChatColor.RED + "----------------------------------------------");
            sender.sendMessage(ChatColor.GOLD + args[0] + ChatColor.AQUA + " is not running!");
            sender.sendMessage(ChatColor.RED + "----------------------------------------------");
        } else {
            sender.sendMessage(ChatColor.GOLD + args[0] + ChatColor.AQUA + " has been stopped!");
            Show s = ShowPlugin.getShows().get(args[0]);
            if (s == null) {
                sender.sendMessage(ChatColor.RED + "Couldn't find a show with that name!");
                return;
            }
            s.stop();
            ShowPlugin.stopShow(args[0]);
            ShowPlugin.getShows().remove(args[0]);
        }
    }
}
