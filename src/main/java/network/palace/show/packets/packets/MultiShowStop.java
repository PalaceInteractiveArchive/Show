package network.palace.show.packets.packets;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.messagequeue.packets.MQPacket;

public class MultiShowStop extends MQPacket {
    @Getter private final String showName, world, server;

    public MultiShowStop(JsonObject object) {
        super(PacketID.Global.MULTI_SHOW_STOP.getId(), object);
        this.showName = object.get("showName").getAsString();
        this.world = object.get("world").getAsString();
        this.server = object.get("server").getAsString();
    }

    public MultiShowStop(String showName, String world, String server) {
        super(PacketID.Global.MULTI_SHOW_STOP.getId(), null);
        this.showName = showName;
        this.world = world;
        this.server = server;
    }

    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("showName", this.showName);
        object.addProperty("world", this.world);
        object.addProperty("server", this.server);
        return object;
    }
}
