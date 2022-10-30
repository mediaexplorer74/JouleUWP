package org.chromium.content.browser;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.WakefulBroadcastReceiver;
import org.chromium.base.BaseChromiumApplication;
import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.SuppressFBWarnings;
import org.chromium.base.library_loader.ProcessInitException;
import org.chromium.content.browser.BackgroundSyncLauncher.ShouldLaunchCallback;

public class BackgroundSyncLauncherService extends IntentService {
    private static final String TAG = "cr.BgSyncLauncher";

    /* renamed from: org.chromium.content.browser.BackgroundSyncLauncherService.1 */
    class C03221 implements Runnable {
        C03221() {
        }

        public void run() {
            BackgroundSyncLauncherService.this.onOnline(BackgroundSyncLauncherService.this.getApplicationContext());
        }
    }

    /* renamed from: org.chromium.content.browser.BackgroundSyncLauncherService.2 */
    class C05962 implements ShouldLaunchCallback {
        final /* synthetic */ Context val$context;

        C05962(Context context) {
            this.val$context = context;
        }

        public void run(Boolean shouldLaunch) {
            if (shouldLaunch.booleanValue()) {
                Log.m34v(BackgroundSyncLauncherService.TAG, "Starting Browser after coming online");
                BackgroundSyncLauncherService.this.launchBrowser(this.val$context);
            }
        }
    }

    public static class Receiver extends WakefulBroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction()) && isOnline(context) && !BackgroundSyncLauncher.hasInstance()) {
                startService(context);
            }
        }

        @VisibleForTesting
        protected void startService(Context context) {
            WakefulBroadcastReceiver.startWakefulService(context, new Intent(context, BackgroundSyncLauncherService.class));
        }

        @VisibleForTesting
        protected boolean isOnline(Context context) {
            NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }

    public BackgroundSyncLauncherService() {
        super("BackgroundSyncLauncherService");
    }

    public void onHandleIntent(Intent intent) {
        try {
            ThreadUtils.runOnUiThread(new C03221());
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void onOnline(Context context) {
        ThreadUtils.assertOnUiThread();
        BackgroundSyncLauncher.shouldLaunchWhenNextOnline(context, new C05962(context));
    }

    @SuppressFBWarnings({"DM_EXIT"})
    private void launchBrowser(Context context) {
        BaseChromiumApplication.initCommandLine(context);
        try {
            BrowserStartupController.get(context, 1).startBrowserProcessesSync(false);
        } catch (ProcessInitException e) {
            Log.m32e(TAG, "ProcessInitException while starting the browser process", new Object[0]);
            System.exit(-1);
        }
    }
}
