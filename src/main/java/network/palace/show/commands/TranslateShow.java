package network.palace.show.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import network.palace.show.ShowPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandMeta(description = "Translate the x/y/z coordinates for a show", rank = Rank.DEVELOPER)
public class TranslateShow extends CoreCommand {

    public TranslateShow() {
        super("translateshow");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length != 6) {
            player.sendMessage(ChatColor.RED + "/translateshow [current world] [target world] [rel x] [rel y] [rel z] [show file]");
            return;
        }
        World currentWorld = Bukkit.getWorld(args[0]);
        World targetWorld = Bukkit.getWorld(args[1]);
        if (currentWorld == null) {
            player.sendMessage(ChatColor.RED + "Unknown world '" + args[0] + "'!");
            return;
        }
        if (targetWorld == null) {
            player.sendMessage(ChatColor.RED + "Unknown world '" + args[1] + "'!");
            return;
        }
        if (!(MiscUtil.checkIfDouble(args[2]) &&
                MiscUtil.checkIfDouble(args[3]) &&
                MiscUtil.checkIfDouble(args[4]))) {
            player.sendMessage(ChatColor.RED + "The coordinates must be numbers!");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Processing " + (currentWorld.getName() + "/" + args[5]) + "...");

        Core.runTaskAsynchronously(ShowPlugin.getInstance(), () -> {
            double x = Double.parseDouble(args[2]);
            double y = Double.parseDouble(args[3]);
            double z = Double.parseDouble(args[4]);

            try {
                File file = new File("plugins/Show/shows/" + currentWorld.getName() + "/" + args[5]);
                if (file == null || !file.exists() || file.isDirectory()) {
                    player.sendMessage(ChatColor.RED + "Unknown show '" + currentWorld.getName() + "/" + args[5] + "'!");
                    return;
                }

                Pattern pattern = Pattern.compile("(-?\\d+(\\.\\d+)?),\\s?(-?\\d+(\\.\\d+)?),\\s?(-?\\d+(\\.\\d+)?)");

                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = "", text = "";
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("#") && line.length() > 0) {
                        if (line.contains("ArmorStand")) {
                            if (line.startsWith("ArmorStand")) {
                                text += line + "\r\n";
                                continue;
                            }
                            if (line.toLowerCase().contains("position")) {
                                text += line + "\r\n";
                                continue;
                            }
                        }
                        if (line.contains("Schematic")) {
                            String[] tokens = line.split("\\s+");
                            try {
                                double prevX = Double.parseDouble(tokens[3]);
                                double prevY = Double.parseDouble(tokens[4]);
                                double prevZ = Double.parseDouble(tokens[5]);
                                line = line.replaceAll(tokens[3] + "\\s" + tokens[4] + "\\s" + tokens[5], format(prevX + x) + "\t" + format(prevY + y) + "\t" + format(prevZ + z));
                            } catch (Exception e) {
                                Core.logMessage("ShowTranslate", "Error reading coordinates on line '" + line + "'!");
                            }
                        } else {
                            Matcher matcher = pattern.matcher(line);
                            if (matcher.find()) {
                                int count = (line.contains("Glow") && matcher.groupCount() > 1) ? 1 : 0;
                                String group = matcher.group(count);
                                line = line.replaceAll(group, increment(group, x, y, z));
                            }
                        }
                    }
                    text += line + "\r\n";
                }
                reader.close();

                FileWriter writer = new FileWriter(file.getAbsolutePath().replaceAll(currentWorld.getName(), targetWorld.getName()));
                writer.write(text);

                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            player.sendMessage(ChatColor.GREEN + "Finished processing the show file!");
        });
    }

    private String increment(String coordinateString, double x, double y, double z) {
        String[] list = coordinateString.replaceAll(" ", "").split(",");
        double prevX = Double.parseDouble(list[0]);
        double prevY = Double.parseDouble(list[1]);
        double prevZ = Double.parseDouble(list[2]);
        return format(prevX + x) + "," + format(prevY + y) + "," + format(prevZ + z);
    }

    private String format(double number) {
        if (number == ((int) number)) {
            return String.valueOf((int) number);
        }
        return String.valueOf(number);
    }
}
