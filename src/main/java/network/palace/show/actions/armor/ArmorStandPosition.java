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
import org.bukkit.Bukkit;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

/**
 * Created by Marc on 10/24/15
 */
public class ArmorStandPosition extends ShowAction {
    private ShowStand stand;
    private PositionType positionType;
    private EulerAngle angle;
    private double speed;

    public ArmorStandPosition(Show show, long time, ShowStand stand, PositionType positionType, EulerAngle angle, double speed) {
        super(show, time);
        this.stand = stand;
        this.positionType = positionType;
        this.angle = angle;
        this.speed = speed;
    }

    @Override
    public void play(CPlayer[] nearPlayers) {
        if (!stand.isHasSpawned()) {
            Bukkit.broadcast("ArmorStand with ID " + stand.getId() + " has not spawned", "palace.core.rank.mod");
            return;
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
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        return this;
    }
}