package network.palace.show.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.events.CoreEvent;
import network.palace.show.Show;

/**
 * Created by Marc on 1/15/17.
 */
@AllArgsConstructor
public class GlowDoneEvent extends CoreEvent {
    @Getter private Show show;
}
