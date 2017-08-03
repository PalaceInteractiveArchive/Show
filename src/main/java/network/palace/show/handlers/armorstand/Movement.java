package network.palace.show.handlers.armorstand;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.util.Vector;

/**
 * Created by Marc on 10/11/15
 */
@Getter
@Setter
public class Movement {
    private Vector motion;
    private long duration;

    public Movement(Vector motion, double speed) {
        this.motion = motion;
        this.duration = (long) speed;
    }
}