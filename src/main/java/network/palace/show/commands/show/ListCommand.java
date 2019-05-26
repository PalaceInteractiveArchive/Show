package network.palace.show.commands.show;

import network.palace.core.command.CommandException;
import network.palace.core.command.CoreCommand;
import network.palace.show.ShowPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        Set<String> shows = ShowPlugin.getShows().keySet();
        if (shows.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No shows are currently running!");
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "Currently running shows:");
        for (String s : shows) {
            sender.sendMessage(ChatColor.GREEN + "- " + ChatColor.AQUA + s);
        }
    }
}
