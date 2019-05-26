package network.palace.show.commands.showbuild;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.show.ShowPlugin;
import network.palace.show.handlers.block.Build;
import org.bukkit.ChatColor;

import java.io.IOException;

@CommandMeta(description = "Load a build file into the world")
public class LoadCommand extends CoreCommand {

    public LoadCommand() {
        super("load");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/showbuild load [name]");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Loading build " + ChatColor.YELLOW + args[0] + ChatColor.GREEN + "...");
        Build build;
        try {
            build = ShowPlugin.getBuildUtil().loadBuild(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "There was an error loading that build file! " + e.getMessage());
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Build file loaded! Now building it at your location...");
        build.build(player.getLocation());
        player.sendMessage(ChatColor.GREEN + "Finished building!");
    }
}
