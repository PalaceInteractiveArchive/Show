package network.palace.show.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.show.packets.packets.MultiShowStart;
import network.palace.show.packets.packets.MultiShowStop;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@CommandMeta(description = "Run a show across multiple park servers", rank = Rank.MOD)
public class MultiShowCommand extends CoreCommand {

    public MultiShowCommand() {
        super("multishow");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 4) {
            List<String> servers = new ArrayList<>();
            String showName = args[1];
            String world = args[2];
            String server = args[3];
            switch (args[0].toLowerCase()) {
                case "start": {
                    try {
                        Core.getMessageHandler().sendMessage(new MultiShowStart(showName, world, server), Core.getMessageHandler().permanentClients.get("all_parks"));
                        Core.getMessageHandler().sendStaffMessage(ChatColor.translateAlternateColorCodes('&', "&aAttempting to start &b" + showName + " &aon all " + server + " &aservers."));
                    } catch (Exception e) {
                        e.printStackTrace();
                        sender.sendMessage(ChatColor.RED + "An error occurred while attempting to multi-start a show. Check console for errors.");
                    }
                    return;
                }
                case "stop": {
                    try {
                        Core.getMessageHandler().sendMessage(new MultiShowStop(showName, world, server), Core.getMessageHandler().permanentClients.get("all_parks"));
                        Core.getMessageHandler().sendStaffMessage(ChatColor.translateAlternateColorCodes('&', "&aAttempting to stop &b" + showName + " &aon all " + server + " &aservers."));
                    } catch (Exception e) {
                        e.printStackTrace();
                        sender.sendMessage(ChatColor.RED + "An error occurred while attempting to multi-stop a show. Check console for errors.");
                    }
                    return;
                }
            }
        }
        sender.sendMessage(ChatColor.GREEN + "MultiShow Commands:\n" + ChatColor.AQUA +
                "- /multishow start [show] [world] [server] " + ChatColor.GREEN +
                "- Starts the specified show on all instances of the specified server\n" + ChatColor.AQUA +
                "- /multishow stop [show] [world] [server] " + ChatColor.GREEN +
                "- Stops the specified show on all instances of the specified server\n" + ChatColor.AQUA +
                "- /multishow help " + ChatColor.GREEN + "- View this help menu");
    }
}