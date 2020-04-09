package network.palace.show.commands.show;

import network.palace.core.command.CommandException;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

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
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        handle(player.getBukkitPlayer(), args, player.getWorld());
    }

    @Override
    protected void handleCommand(BlockCommandSender commandSender, String[] args) throws CommandException {
        handle(commandSender, args, commandSender.getBlock().getWorld());
    }

    @Override
    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {
        if (Bukkit.getWorlds().size() > 1) {
            commandSender.sendMessage(ChatColor.RED + "You can't start shows from console with multiple worlds!");
            return;
        }
        handle(commandSender, args, Bukkit.getWorlds().get(0));
    }

    private void handle(CommandSender sender, String[] args, World world) {
        if (world == null || world.getName() == null) {
            sender.sendMessage(ChatColor.RED + "Invalid world!");
            return;
        }
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
        File f = new File("plugins/Show/shows/" + world.getName() + "/" + args[0] + ".show");
        if (!f.exists()) {
            sender.sendMessage(ChatColor.RED + "----------------------------------------------");
            sender.sendMessage(ChatColor.RED + "That show doesn't exist! Looking at: " + f.getPath());
            sender.sendMessage(ChatColor.RED + "----------------------------------------------");
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "Starting... ");
        ShowPlugin.startShow(args[0], new Show(f, world));
        sender.sendMessage(ChatColor.GREEN + args[0] + ChatColor.AQUA + " has started on world " + ChatColor.YELLOW + world.getName());
    }
}
