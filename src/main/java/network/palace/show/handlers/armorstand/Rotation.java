package network.palace.show.handlers.armorstand;

/**
 * Created by Marc on 3/26/16
 */
public class Rotation {
    private float yaw;
    private long duration;
    private boolean handled = false;

    public Rotation(float yaw, double speed) {
        this.yaw = yaw;
        this.duration = (long) speed;
    }

    public float getYaw() {
        return yaw;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void handle() {
        handled = true;
    }

    public boolean isHandled() {
        return handled;
    }
}
