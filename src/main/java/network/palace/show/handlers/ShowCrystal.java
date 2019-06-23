package network.palace.show.handlers;

import lombok.Getter;
import lombok.Setter;
import network.palace.show.sequence.handlers.SequenceState;
import org.bukkit.entity.EnderCrystal;

@Getter
public class ShowCrystal {
    private String id;
    private SequenceState state;
    @Setter private boolean spawned = false;
    @Setter private EnderCrystal crystal;

    public ShowCrystal(String id, SequenceState state) {
        this.id = id;
        this.state = state;
    }
}
