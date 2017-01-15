package network.palace.show.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;

/**
 * Created by Marc on 12/6/16.
 */
@CommandMeta(description = "Command used to start shows")
@CommandPermission(rank = Rank.KNIGHT)
public class Commandshow extends CoreCommand {

    public Commandshow() {
        super("show");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.GREEN + "Show Commands:");
            sender.sendMessage(ChatColor.AQUA + "/show start [Show Name] " + ChatColor.GREEN + "- Start a show");
            sender.sendMessage(ChatColor.AQUA + "/show stop [Show Name] " + ChatColor.GREEN + "- Stop a show");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "start": {
                if (ShowPlugin.getShows().containsKey(args[1])) {
                    sender.sendMessage(ChatColor.RED + "----------------------------------------------");
                    sender.sendMessage(ChatColor.RED + "That show is already running!");
                    sender.sendMessage(ChatColor.RED + "----------------------------------------------");
                    return;
                }
                File f = new File("plugins/Show/shows/" + args[1] + ".show");
                if (!f.exists()) {
                    sender.sendMessage(ChatColor.RED + "----------------------------------------------");
                    sender.sendMessage(ChatColor.RED + "That show doesn't exist!");
                    sender.sendMessage(ChatColor.RED + "----------------------------------------------");
                    return;
                }
                ShowPlugin.getShows().put(args[1], new Show(ShowPlugin.getInstance(), f));
                sender.sendMessage(ChatColor.GREEN + args[1] + ChatColor.AQUA + " has started.");
                break;
            }
            case "stop": {
                if (!ShowPlugin.getShows().containsKey(args[1])) {
                    sender.sendMessage(ChatColor.RED + "----------------------------------------------");
                    sender.sendMessage(ChatColor.GOLD + args[1] + ChatColor.AQUA + " is not running!");
                    sender.sendMessage(ChatColor.RED + "----------------------------------------------");
                } else {
                    sender.sendMessage(ChatColor.GOLD + args[1] + ChatColor.AQUA + " has been stopped!");
                    Show s = ShowPlugin.getShows().get(args[1]);
                    s.stop();
                    ShowPlugin.getShows().remove(args[1]);
                }
                break;
            }
        }
    }
}
