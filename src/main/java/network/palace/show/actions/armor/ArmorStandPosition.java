package network.palace.show.actions.armor;

import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.actions.ShowAction;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.armorstand.Position;
import network.palace.show.handlers.armorstand.PositionType;
import network.palace.show.handlers.armorstand.ShowStand;
import network.palace.show.handlers.armorstand.StandAction;
import network.palace.show.utils.ShowUtil;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

/**
 * Created by Marc on 10/24/15
 */
public class ArmorStandPosition extends ShowAction {
    private final ShowStand stand;
    private final PositionType positionType;
    private final EulerAngle angle;
    private final double speed;

    public ArmorStandPosition(Show show, long time, ShowStand stand, PositionType positionType, EulerAngle angle, double speed) {
        super(show, time);
        this.stand = stand;
        this.positionType = positionType;
        this.angle = angle;
        this.speed = speed;
    }

    @Override
    public boolean play(CPlayer[] nearPlayers) {
        if (!stand.isHasSpawned()) {
            ShowUtil.logDebug(show.getName(), "ArmorStand with ID " + stand.getId() + " has not spawned");
            return true;
        }
        EulerAngle a = null;
        switch (positionType) {
            case HEAD:
                a = stand.getStand().getHeadPose();
                break;
            case BODY:
                a = stand.getStand().getBodyPose();
                break;
            case ARM_LEFT:
                a = stand.getStand().getLeftArmPose();
                break;
            case ARM_RIGHT:
                a = stand.getStand().getRightArmPose();
                break;
            case LEG_LEFT:
                a = stand.getStand().getLeftLegPose();
                break;
            case LEG_RIGHT:
                a = stand.getStand().getRightLegPose();
                break;
        }
        double x = ((float) (((float) (angle.getX() - a.getX())) / (20 * speed)));
        double y = ((float) (((float) (angle.getY() - a.getY())) / (20 * speed)));
        double z = ((float) (((float) (angle.getZ() - a.getZ())) / (20 * speed)));
        Vector motion = new Vector(x, y, z);
        stand.addPosition(new Position(motion, speed * 20, positionType));
        ShowPlugin.getInstance().getArmorStandManager().addStand(stand, StandAction.POSITION);
        return true;
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        return this;
    }
}