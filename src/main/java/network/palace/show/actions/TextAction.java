package network.palace.show.actions;


import network.palace.show.Show;

public class TextAction extends ShowAction {
    public String text;

    public TextAction(Show show, long time, String text) {
        super(show, time);
        this.text = text;
    }

    @Override
    public void play() {
        show.displayText(text);
    }
}
