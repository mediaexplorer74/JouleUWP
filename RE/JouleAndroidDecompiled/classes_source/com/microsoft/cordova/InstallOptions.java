package com.microsoft.cordova;

public class InstallOptions {
    public InstallMode installMode;
    public int minimumBackgroundDuration;

    public InstallOptions(InstallMode installMode, int minimumBackgroundDuration) {
        this.installMode = installMode;
        this.minimumBackgroundDuration = minimumBackgroundDuration;
    }
}
