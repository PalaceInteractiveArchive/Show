package network.palace.show.packets.packets;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.messagequeue.packets.MQPacket;

public class BotNotification extends MQPacket {
    @Getter private final String channelId, title, desc, startTime, whereToWatch, color, image;

    public BotNotification(JsonObject object) {
        super(PacketID.Discord.NOTIFICATION.getId(), object);
        this.channelId = object.get("channelId").getAsString();
        this.title = object.get("title").getAsString();
        this.desc = object.get("desc").getAsString();
        this.startTime = object.get("startTime").getAsString();
        this.whereToWatch = object.get("whereToWatch").getAsString();
        this.color = object.get("color").getAsString();
        this.image = object.get("image").getAsString();
    }

    public BotNotification(String channelId, String title, String desc, String startTime, String whereToWatch, String color, String image) {
        super(PacketID.Discord.NOTIFICATION.getId(), null);
        this.channelId = channelId;
        this.title = title;
        this.desc = desc;
        this.startTime = startTime;
        this.whereToWatch = whereToWatch;
        this.color = color;
        this.image = image;
    }

    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("channelId", this.channelId);
        object.addProperty("title", this.title);
        object.addProperty("desc", this.desc);
        object.addProperty("startTime", this.startTime);
        object.addProperty("whereToWatch", this.whereToWatch);
        object.addProperty("color", this.color);
        object.addProperty("image", this.image);
        return object;
    }
}
