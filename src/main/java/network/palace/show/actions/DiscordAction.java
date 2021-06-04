package network.palace.show.actions;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.packets.packets.BotNotification;
import org.bukkit.ChatColor;

import java.io.IOException;

public class DiscordAction extends ShowAction {
    private String title;
    private String channelId;
    private String desc;
    private String startTime;
    private String whereToWatch;
    private String color;
    private String image;
    private Boolean ping;


    public DiscordAction(Show show, long time) {
        super(show, time);
    }

    @Override
    public void play(CPlayer[] nearPlayers) {
        try {
            if (!Core.getInstanceName().contains("Build")) {
                Core.getMessageHandler().sendMessage(new BotNotification(channelId, title, desc, startTime, whereToWatch, color, image, ping), Core.getMessageHandler().BOT);
            }
        } catch (IOException e) {
            try {
                Core.getMessageHandler().sendStaffMessage(ChatColor.RED + Core.getInstanceName() + " Failed on sending BotNotification packet via Core " + Core.getVersion() + " - Please alert the development team ASAP");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        Core.logMessage("Show", String.valueOf(args.length));
        if (args.length != 10) {
            throw new ShowParseException("Invalid Discord Line Length");
        }
        if (args[2] == null) throw new ShowParseException("Invalid channelId " + line);
        if (args[3] == null) throw new ShowParseException("Invalid title " + line);
        if (args[4] == null) throw new ShowParseException("Invalid desc " + line);
        if (args[5] == null) throw new ShowParseException("Invalid startTime " + line);
        if (args[6] == null) throw new ShowParseException("Invalid whereToWatch " + line);
        if (args[7] == null) throw new ShowParseException("Invalid color" + line);
        if (args[8] == null) throw new ShowParseException("Invalid image" + line);
        if (args[9] == null) throw new ShowParseException("Invalid ping choice" + line);
        this.channelId = args[2];
        this.title = args[3];
        this.desc = args[4];
        this.startTime = args[5];
        this.whereToWatch = args[6];
        this.color = args[7];
        this.image = args[8];
        this.ping = Boolean.valueOf(args[9]);
        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        // Action should not be repeatable to prevent spamming
        return null;
    }

}
