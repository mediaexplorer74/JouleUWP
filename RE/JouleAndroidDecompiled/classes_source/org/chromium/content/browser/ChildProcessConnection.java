package org.chromium.content.browser;

import android.os.Bundle;
import org.chromium.content.common.IChildProcessCallback;
import org.chromium.content.common.IChildProcessService;

public interface ChildProcessConnection {
    public static final String EXTRA_COMMAND_LINE = "com.google.android.apps.chrome.extra.command_line";
    public static final String EXTRA_CPU_COUNT = "com.google.android.apps.chrome.extra.cpu_count";
    public static final String EXTRA_CPU_FEATURES = "com.google.android.apps.chrome.extra.cpu_features";
    public static final String EXTRA_FILES = "com.google.android.apps.chrome.extra.extraFiles";

    public interface ConnectionCallback {
        void onConnected(int i);
    }

    public interface DeathCallback {
        void onChildProcessDied(ChildProcessConnection childProcessConnection);
    }

    void addModerateBinding();

    void addStrongBinding();

    void dropOomBindings();

    int getPid();

    IChildProcessService getService();

    int getServiceNumber();

    boolean isInSandbox();

    boolean isInitialBindingBound();

    boolean isModerateBindingBound();

    boolean isOomProtectedOrWasWhenDied();

    boolean isStrongBindingBound();

    void removeInitialBinding();

    void removeModerateBinding();

    void removeStrongBinding();

    void setupConnection(String[] strArr, FileDescriptorInfo[] fileDescriptorInfoArr, IChildProcessCallback iChildProcessCallback, ConnectionCallback connectionCallback, Bundle bundle);

    void start(String[] strArr);

    void stop();
}
