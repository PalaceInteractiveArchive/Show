package network.palace.show.commands.showgen;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.show.ShowPlugin;
import network.palace.show.actions.FakeBlockAction;
import network.palace.show.generator.GeneratorSession;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@CommandMeta(description = "Generate blocks of show actions with one command")
public class CommandGenerate extends CoreCommand {

    public CommandGenerate() {
        super("generate");
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        GeneratorSession session = ShowPlugin.getShowGenerator().getOrCreateSession(player.getUniqueId());
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/showgen generate [action] [timestamp]");
            return;
        }
        String action = args[0];
        int time;
        try {
            time = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + args[1] + " is not a number!");
            return;
        }
        if (args[0].equalsIgnoreCase("fakeblock")) {
            if (session.getInitialScene() == null) {
                player.sendMessage(ChatColor.RED + "You must set an initial scene before running this command! Use /showgen setinitialscene");
                return;
            }
            if (session.getCorner() == null) {
                player.sendMessage(ChatColor.RED + "You must set the location of the final north-west-bottom corner to generate proper coordinates!");
                return;
            }
            GeneratorSession.ShowSelection initialScene = session.getInitialScene();
            Location corner = session.getCorner();

            WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
            Selection selection = worldEdit.getSelection(player.getBukkitPlayer());

            if (selection == null) {
                player.sendMessage(ChatColor.RED + "You need to select the blocks for the second scene with your WorldEdit wand before running this command!");
                return;
            }

            Location finalMax = selection.getMaximumPoint();
            Location finalMin = selection.getMinimumPoint();

            GeneratorSession.ShowSelection finalScene = new GeneratorSession.ShowSelection(finalMax, finalMin);

            if (!finalScene.equalSize(initialScene)) {
                player.sendMessage(ChatColor.RED + "Your initial scene is not the same size as your current scene! They must be equal in order to generate the differences in their blocks.");
                return;
            }

            player.sendMessage(ChatColor.GREEN + "Generating a list of " + args[0].toLowerCase() + " changes at time " + time + " between scene 1 at " + initialScene.toString() + " and scene 2 at " + finalScene.toString());
            Core.runTaskAsynchronously(() -> {
                List<FakeBlockAction> actions = new ArrayList<>();

                for (int x = 0; x < initialScene.getXLength(); x++) {
                    for (int y = 0; y < initialScene.getYLength(); y++) {
                        for (int z = 0; z < initialScene.getZLength(); z++) {
                            Block oldBlock = initialScene.getBlock(x, y, z);
                            Block newBlock = finalScene.getBlock(x, y, z);
                            if (newBlock.getType().equals(oldBlock.getType()) && newBlock.getData() == oldBlock.getData()) {
                                continue;
                            }
                            Material material = newBlock.getType();
                            byte data = newBlock.getData();
                            FakeBlockAction act = new FakeBlockAction(null, time);
                            act.setId(material.getId());
                            act.setData(data);
                            act.setLoc(new Location(corner.getWorld(), corner.getBlockX() + x, corner.getBlockY() + y, corner.getBlockZ() + z));
                            actions.add(act);
                        }
                    }
                }

                if (actions.isEmpty()) {
                    player.sendMessage(ChatColor.RED + "There aren't any differences between the two selected regions!");
                    return;
                }

                String date = new SimpleDateFormat("MM_dd_yyyy_HH:mm:ss").format(new Date());

                String url;
                try {
                    url = ShowPlugin.getShowGenerator().postGist(actions, finalMax.getWorld().getName() + "_" + date + "_" + player.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColor.RED + "There was an error creating a GitHub Gist with the list of actions!");
                    return;
                }
                player.sendMessage(ChatColor.GREEN + "Finished with a list of " + actions.size() + " actions! View the actions here: " + url);
            });
            return;
        }
        player.sendMessage(ChatColor.RED + "The show generator doesn't currently support '" + args[0] + "' actions.");
    }
}
