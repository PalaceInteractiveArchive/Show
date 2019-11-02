package network.palace.show.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.show.commands.show.ListCommand;
import network.palace.show.commands.show.StartCommand;
import network.palace.show.commands.show.StopCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by Marc on 12/6/16.
 */
@CommandMeta(description = "Command used to start shows", rank = Rank.TRAINEETECH)
public class ShowCommand extends CoreCommand {

    public ShowCommand() {
        super("show");
        registerSubCommand(new ListCommand());
        registerSubCommand(new StartCommand());
        registerSubCommand(new StopCommand());
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.GREEN + "Show Commands:");
        sender.sendMessage(ChatColor.AQUA + "/show list " + ChatColor.GREEN + "- List all running shows");
        sender.sendMessage(ChatColor.AQUA + "/show start [Show Name] " + ChatColor.GREEN + "- Start a show");
        sender.sendMessage(ChatColor.AQUA + "/show stop [Show Name] " + ChatColor.GREEN + "- Stop a show");
    }
}
