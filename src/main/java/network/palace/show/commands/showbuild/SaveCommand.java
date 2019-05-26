package network.palace.show.commands.showbuild;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.show.ShowPlugin;
import network.palace.show.handlers.block.Build;
import org.bukkit.ChatColor;

import java.io.IOException;

@CommandMeta(description = "Save your WorldEdit selection to a build file")
public class SaveCommand extends CoreCommand {

    public SaveCommand() {
        super("save");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/showbuild save [name]");
            return;
        }
        WorldEditPlugin plugin = ShowPlugin.getBuildUtil().getWorldEditPlugin();
        LocalSession session = plugin.getSession(player.getBukkitPlayer());
        Region region;
        try {
            region = session.getSelection(session.getSelectionWorld());
        } catch (IncompleteRegionException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "There was an error loading your WorldEdit selection!");
            return;
        }
        Build build = new Build(args[0]);
        Vector v1 = region.getMinimumPoint();
        Vector v2 = region.getMaximumPoint();
        build.load(player.getWorld(), new org.bukkit.util.Vector(v1.getX(), v1.getY(), v1.getZ()), new org.bukkit.util.Vector(v2.getX(), v2.getY(), v2.getZ()));
        try {
            ShowPlugin.getBuildUtil().saveBuild(build);
            player.sendMessage(ChatColor.GREEN + "Successfully saved your build as " + ChatColor.YELLOW + build.getName() + ChatColor.GREEN + "!");
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "There was an error saving the build to the file system!");
        }
    }
}
