package network.palace.show.handlers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.EnderCrystal;

public class ShowCrystal {

    @Getter
    private String id;
    @Getter
    @Setter
    private boolean spawned = false;
    @Getter
    @Setter
    private EnderCrystal crystal;

    public ShowCrystal(String id) {
        this.id = id;
    }
}
