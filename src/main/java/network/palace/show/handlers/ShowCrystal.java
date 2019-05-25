package network.palace.show.handlers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.EnderCrystal;

@Getter
public class ShowCrystal {

    private String id;
    @Setter private boolean spawned = false;
    @Setter private EnderCrystal crystal;

    public ShowCrystal(String id) {
        this.id = id;
    }
}
