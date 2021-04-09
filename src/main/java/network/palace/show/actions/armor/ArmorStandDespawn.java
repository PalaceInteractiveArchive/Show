package network.palace.show.actions.armor;

import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.actions.ShowAction;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.armorstand.ShowStand;
import network.palace.show.utils.ShowUtil;
import org.bukkit.entity.ArmorStand;

/**
 * Created by Marc on 10/11/15
 */
public class ArmorStandDespawn extends ShowAction {
    private final ShowStand stand;

    public ArmorStandDespawn(Show show, long time, ShowStand stand) {
        super(show, time);
        this.stand = stand;
    }

    @Override
    public boolean play(CPlayer[] nearPlayers) {
        if (!stand.isHasSpawned()) {
            ShowUtil.logDebug(show.getName(), "ArmorStand with ID " + stand.getId() + " has not spawned");
            return true;
        }
        ArmorStand armor = stand.getStand();
        armor.remove();
        stand.setStand(null);
        stand.despawn();
        return true;
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        return this;
    }
}