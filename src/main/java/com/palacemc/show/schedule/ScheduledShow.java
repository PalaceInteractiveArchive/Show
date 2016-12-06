package com.palacemc.show.schedule;

import com.palacemc.show.handlers.schedule.ShowDay;
import com.palacemc.show.handlers.schedule.ShowType;

/**
 * Created by Marc on 10/29/15
 */
public class ScheduledShow {
    private ShowType type;
    private ShowDay day;
    private String time;

    public ScheduledShow(ShowType type, ShowDay day, String time) {
        this.type = type;
        this.day = day;
        this.time = time;
    }

    public ShowType getType() {
        return type;
    }

    public ShowDay getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }
}