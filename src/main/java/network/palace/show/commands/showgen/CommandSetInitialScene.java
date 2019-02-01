package network.palace.show.commands.showgen;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.show.ShowPlugin;
import network.palace.show.generator.GeneratorSession;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

@CommandMeta(description = "Set the initial scene for a generator session")
public class CommandSetInitialScene extends CoreCommand {

    public CommandSetInitialScene() {
        super("setinitialscene");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        GeneratorSession session = ShowPlugin.getShowGenerator().getOrCreateSession(player.getUniqueId());

        WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        Selection selection = worldEdit.getSelection(player.getBukkitPlayer());

        if (selection == null) {
            player.sendMessage(ChatColor.RED + "Make a selection with a WorldEdit wand, then run this command!");
            return;
        }

        World world = selection.getWorld();
        Location max = selection.getMaximumPoint();
        Location min = selection.getMinimumPoint();
        session.setInitialScene(new GeneratorSession.ShowSelection(max, min));
        player.sendMessage(ChatColor.GREEN + "The initial scene has been set!");
    }
}
