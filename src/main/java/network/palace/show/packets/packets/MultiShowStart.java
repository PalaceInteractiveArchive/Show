package network.palace.show.packets.packets;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.messagequeue.packets.MQPacket;

public class MultiShowStart extends MQPacket {
    @Getter private final String showName, world, server;

    public MultiShowStart(JsonObject object) {
        super(PacketID.Global.MULTI_SHOW_START.getId(), object);
        this.showName = object.get("showName").getAsString();
        this.world = object.get("world").getAsString();
        this.server = object.get("server").getAsString();
    }

    public MultiShowStart(String showName, String world, String server) {
        super(PacketID.Global.MULTI_SHOW_START.getId(), null);
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
