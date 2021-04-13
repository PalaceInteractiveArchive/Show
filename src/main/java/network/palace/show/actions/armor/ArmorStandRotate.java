package network.palace.show.actions.armor;

import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.actions.ShowAction;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.armorstand.Rotation;
import network.palace.show.handlers.armorstand.ShowStand;
import network.palace.show.handlers.armorstand.StandAction;
import network.palace.show.utils.ShowUtil;

/**
 * Created by Marc on 3/26/16
 */
public class ArmorStandRotate extends ShowAction {
    private final ShowStand stand;
    private final float yaw;
    private final double speed;

    public ArmorStandRotate(Show show, long time, ShowStand stand, float yaw, double speed) {
        super(show, time);
        this.stand = stand;
        this.yaw = yaw;
        this.speed = speed;
    }

    @Override
    public void play(CPlayer[] nearPlayers) {
        if (!stand.isHasSpawned()) {
            ShowUtil.logDebug(show.getName(), "ArmorStand with ID " + stand.getId() + " has not spawned");
            return;
        }

        double ticks = speed * 20;
        float interval = (float) (this.yaw / ticks);
        stand.setRotation(new Rotation(interval, speed * 20));
        ShowPlugin.getInstance().getArmorStandManager().addStand(stand, StandAction.ROTATION);
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new ArmorStandRotate(show, time, stand, yaw, speed);
    }
}
