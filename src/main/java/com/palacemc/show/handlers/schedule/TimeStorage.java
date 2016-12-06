package com.palacemc.show.handlers.schedule;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Marc on 11/1/15
 */
public class TimeStorage {
    private HashMap<UUID, Integer> rows = new HashMap<>();
    private HashMap<UUID, String> times = new HashMap<>();

    public void add(Integer row, String time) {
        UUID uuid = UUID.randomUUID();
        rows.put(uuid, row);
        times.put(uuid, time);
    }

    public String getTime(Integer row) {
        UUID uuid = null;
        for (Map.Entry<UUID, Integer> entry : rows.entrySet()) {
            if (entry.getValue().equals(row)) {
                uuid = entry.getKey();
                break;
            }
        }
        return times.get(uuid);
    }

    public Integer getColumn(String time) {
        UUID uuid = null;
        for (Map.Entry<UUID, String> entry : times.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(time)) {
                uuid = entry.getKey();
                break;
            }
        }
        return rows.get(uuid);
    }
}