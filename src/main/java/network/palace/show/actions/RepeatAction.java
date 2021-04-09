package network.palace.show.actions;

import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.utils.ShowUtil;

import java.util.Arrays;

public class RepeatAction extends ShowAction {
    private ShowAction repeatingAction;
    private int occurrences;
    private int interval;

    public RepeatAction(Show show, long time) {
        super(show, time);
    }

    @Override
    public void play(CPlayer[] nearPlayers) {
        for (int i = 0; i < occurrences; i++) {
            try {
                ShowAction copy = repeatingAction.copy(show, time + ((long) interval * 50 * i));
                show.addLaterAction(copy);
            } catch (ShowParseException e) {
                ShowUtil.logDebug(show.getName(), "Error parsing Repeat action: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        // 0 Repeat occurrences interval ActionText
        this.occurrences = Integer.parseInt(args[2]);
        this.interval = Integer.parseInt(args[3]);
        if (occurrences < 1) throw new ShowParseException("'occurrences' value in Repeat action must be at least 1");
        if (interval < 1) throw new ShowParseException("'interval' value in Repeat action must be at least 1");
        StringBuilder action = new StringBuilder("0\t");
        for (int i = 4; i < args.length; i++) {
            action.append(args[i]).append("\t");
        }
        String[] newArgs = Arrays.copyOfRange(args, 3, args.length);
        newArgs[0] = "0";
        this.repeatingAction = show.parseAction(action.toString(), newArgs, 0, show.getSequences());
        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        throw new ShowParseException("You cannot stack Repeat actions!");
    }
}
