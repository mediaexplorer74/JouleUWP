package com.microsoft.cordova;

public enum InstallMode {
    IMMEDIATE(0),
    ON_NEXT_RESTART(1),
    ON_NEXT_RESUME(2);
    
    private int value;

    private InstallMode(int i) {
        this.value = i;
    }

    public static InstallMode fromValue(int i) {
        for (InstallMode mode : values()) {
            if (i == mode.value) {
                return mode;
            }
        }
        return null;
    }

    public int getValue() {
        return this.value;
    }
}
