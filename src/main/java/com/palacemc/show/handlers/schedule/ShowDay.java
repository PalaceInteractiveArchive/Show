package com.palacemc.show.handlers.schedule;

/**
 * Created by Marc on 10/29/15
 */
public enum ShowDay {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    public static ShowDay fromString(String s) {
        switch (s.toLowerCase()) {
            case "monday":
                return MONDAY;
            case "tuesday":
                return TUESDAY;
            case "wednesday":
                return WEDNESDAY;
            case "thursday":
                return THURSDAY;
            case "friday":
                return FRIDAY;
            case "saturday":
                return SATURDAY;
            case "sunday":
                return SUNDAY;
        }
        return null;
    }
}