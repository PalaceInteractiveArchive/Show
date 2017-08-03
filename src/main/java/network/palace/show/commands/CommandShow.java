package network.palace.show.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.show.commands.show.CommandStart;
import network.palace.show.commands.show.CommandStop;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by Marc on 12/6/16.
 */
@CommandMeta(description = "Command used to start shows")
@CommandPermission(rank = Rank.KNIGHT)
public class CommandShow extends CoreCommand {

    public CommandShow() {
        super("show");
        registerSubCommand(new CommandStart());
        registerSubCommand(new CommandStop());
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.GREEN + "Show Commands:");
        sender.sendMessage(ChatColor.AQUA + "/show start [Show Name] " + ChatColor.GREEN + "- Start a show");
        sender.sendMessage(ChatColor.AQUA + "/show stop [Show Name] " + ChatColor.GREEN + "- Stop a show");
    }
}
