package network.palace.show.commands.show;

import network.palace.core.command.CommandException;
import network.palace.core.command.CoreCommand;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;

/**
 * @author Marc
 * @since 8/2/17
 */
public class StartCommand extends CoreCommand {

    public StartCommand() {
        super("start");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/show start [Show Name]");
            return;
        }
        if (ShowPlugin.getShows().containsKey(args[0])) {
            sender.sendMessage(ChatColor.RED + "----------------------------------------------");
            sender.sendMessage(ChatColor.RED + "That show is already running!");
            sender.sendMessage(ChatColor.RED + "----------------------------------------------");
            return;
        }
        File f = new File("plugins/Show/shows/" + args[0] + ".show");
        if (!f.exists()) {
            sender.sendMessage(ChatColor.RED + "----------------------------------------------");
            sender.sendMessage(ChatColor.RED + "That show doesn't exist!");
            sender.sendMessage(ChatColor.RED + "----------------------------------------------");
            return;
        }
        ShowPlugin.startShow(args[0], new Show(ShowPlugin.getInstance(), f));
        sender.sendMessage(ChatColor.GREEN + args[0] + ChatColor.AQUA + " has started.");
    }
}
