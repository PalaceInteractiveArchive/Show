package network.palace.show.commands.show;

import network.palace.core.command.CommandException;
import network.palace.core.command.CoreCommand;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        List<String> shows = new ArrayList<>();
        for (Map.Entry<String, Show> entry : ShowPlugin.getShows().entrySet()) {
            shows.add(entry.getValue().getWorld().getName() + ":" + entry.getKey());
        }
        if (shows.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No shows are currently running!");
            return;
        }
        shows.sort(String::compareTo);
        sender.sendMessage(ChatColor.GREEN + "Currently running shows:");
        for (String s : shows) {
            String[] split = s.split(":");
            sender.sendMessage(ChatColor.GREEN + "- " + ChatColor.AQUA + split[1] + ChatColor.GREEN + " on " + ChatColor.YELLOW + split[0]);
        }
    }
}
