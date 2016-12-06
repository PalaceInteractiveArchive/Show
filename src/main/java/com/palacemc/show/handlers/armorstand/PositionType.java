package com.palacemc.show.handlers.armorstand;

/**
 * Created by Marc on 2/7/16
 */
public enum PositionType {
    HEAD, BODY, ARM_LEFT, ARM_RIGHT, LEG_LEFT, LEG_RIGHT;

    public static PositionType fromString(String s) {
        switch (s.toLowerCase()) {
            case "head":
                return HEAD;
            case "body":
                return BODY;
            case "arm_left":
                return ARM_LEFT;
            case "arm_right":
                return ARM_RIGHT;
            case "leg_left":
                return LEG_LEFT;
            case "leg_right":
                return LEG_RIGHT;
        }
        return null;
    }
}