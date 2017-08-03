package network.palace.show.actions;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;

public class MusicAction extends ShowAction {
    private int record;

    public MusicAction(Show show, long time) {
        super(show, time);
    }

    @Override
    public void play() {
        show.playMusic(record);
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        try {
            this.record = Integer.parseInt(args[2]);
        } catch (Exception e) {
            throw new ShowParseException("Invalid Material");
        }
        return this;
    }
}
