package network.palace.show.actions;

import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;

public class DiscordAction extends ShowAction {
    private String title;
    private String channelId;
    private String desc;
    private String startTime;
    private String whereToWatch;


    public DiscordAction(Show show, long time) {
        super(show, time);
    }

    public DiscordAction(Show show, long time, String channelId, String title, String desc, String startTime, String whereToWatch) {
        super(show, time);
        this.title = title;
        this.channelId = channelId;
        this.desc = desc;
        this.startTime = startTime;
        this.whereToWatch = whereToWatch;
    }

    @Override
    public void play(CPlayer[] nearPlayers) {

    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        if (args[0] == null) throw new ShowParseException("Invalid channelId " + line);
        return null;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        // Action should not be repeatable to prevent spamming
        return null;
    }

}
