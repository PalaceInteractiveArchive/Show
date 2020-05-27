package network.palace.show.dashboard.packets.park;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.show.dashboard.packets.PacketID;

@Getter
public class PacketShowStart extends BasePacket {
    private String showName, world;

    public PacketShowStart() {
        this("", "");
    }

    public PacketShowStart(String showName, String world) {
        super(PacketID.Park.SHOW_START.getID());
        this.showName = showName;
        this.world = world;
    }

    public PacketShowStart fromJSON(JsonObject obj) {
        this.id = obj.get("id").getAsInt();
        this.showName = obj.get("showName").getAsString();
        this.world = obj.get("world").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", this.id);
        obj.addProperty("showName", this.showName);
        obj.addProperty("world", this.world);
        return obj;
    }
}
