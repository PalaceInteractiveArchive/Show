package network.palace.show.commands.showgen;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.show.ShowPlugin;
import network.palace.show.generator.GeneratorSession;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

@CommandMeta(description = "Set the location of the final north-west-bottom corner to help give real coordinate values", rank = Rank.MOD)
public class CommandSetCorner extends CoreCommand {

    public CommandSetCorner() {
        super("setcorner");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/showgen setcorner x,y,z");
            return;
        }
        String[] locData = args[0].split(",");
        if (locData.length < 3) {
            player.sendMessage(ChatColor.RED + "/showgen setcorner x,y,z");
            return;
        }
        try {
            int x = Integer.parseInt(locData[0]);
            int y = Integer.parseInt(locData[1]);
            int z = Integer.parseInt(locData[2]);
            World w = player.getWorld();
            GeneratorSession session = ShowPlugin.getShowGenerator().getOrCreateSession(player.getUniqueId());
            session.setCorner(new Location(w, x, y, z));
            player.sendMessage(ChatColor.GREEN + "Set north-west-bottom corner to " + x + "," + y + "," + z + "!");
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Are you sure those are all numbers?");
        }
    }
}
