package network.palace.show.dashboard.packets.park;


import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.show.dashboard.packets.PacketID;

@Getter
public class PacketShowStop extends BasePacket {
    private String showName;

    public PacketShowStop() {
        this("");
    }

    public PacketShowStop(String showName) {
        super(PacketID.Park.SHOW_STOP.getID());
        this.showName = showName;
    }

    public PacketShowStop fromJSON(JsonObject obj) {
        this.id = obj.get("id").getAsInt();
        this.showName = obj.get("showName").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", this.id);
        obj.addProperty("showName", this.showName);
        return obj;
    }
}
