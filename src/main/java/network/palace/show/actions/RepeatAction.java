package network.palace.show.actions;

import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;

public class RepeatAction extends ShowAction {
    private ShowAction repeatingAction;
    private int actionCounter; // decreases to 0 until it has been run the number of times it needs to
    private int intervalCounter; // increases from 0 until it is equal to 'interval'
    private int interval; // the number of ticks between each run
    private boolean first = true;

    public RepeatAction(Show show, long time) {
        super(show, time);
    }

    @Override
    public boolean play(CPlayer[] nearPlayers) {
        if (actionCounter > 0) {
            // still have a number of runs left, run it
            if (!first && intervalCounter < interval) {
                intervalCounter++;
                return false;
            }
            first = false;
            intervalCounter = 0;
            repeatingAction.play(nearPlayers);
            return --actionCounter <= 0; // return true if actionCounter <= 0 after being decreased, else return false
        }
        return true;
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        // 0 Repeat actionCounter interval ActionText
        this.actionCounter = Integer.parseInt(args[2]);
        this.interval = Integer.parseInt(args[3]);
        this.repeatingAction = show.parseAction(line, args, 0, 4);
        return this;
    }
}
