package network.palace.show.actions.armor;

import network.palace.show.Show;
import network.palace.show.actions.ShowAction;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.armorstand.ShowStand;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;

/**
 * Created by Marc on 10/11/15
 */
public class ArmorStandDespawn extends ShowAction {
    private ShowStand stand;

    public ArmorStandDespawn(Show show, long time, ShowStand stand) {
        super(show, time);
        this.stand = stand;
    }

    @Override
    public void play() {
        if (!stand.isHasSpawned()) {
            Bukkit.broadcast("ArmorStand with ID " + stand.getId() + " has not spawned", "palace.core.rank.mod");
            return;
        }
        ArmorStand armor = stand.getStand();
        armor.remove();
        stand.setStand(null);
        stand.despawn();
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        return this;
    }
}