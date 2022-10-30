package org.chromium.base;

import android.app.Application;
import android.content.Context;

public class BaseChromiumApplication extends Application {
    public void onCreate() {
        super.onCreate();
        ApplicationStatusManager.init(this);
    }

    public void initCommandLine() {
    }

    @VisibleForTesting
    public static void initCommandLine(Context context) {
        ((BaseChromiumApplication) context.getApplicationContext()).initCommandLine();
    }
}
