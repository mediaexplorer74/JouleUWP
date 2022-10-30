package org.chromium.content.browser;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.VisibleForTesting;

@JNINamespace("content")
public class BackgroundSyncLauncher {
    static final /* synthetic */ boolean $assertionsDisabled;
    static final String PREF_BACKGROUND_SYNC_LAUNCH_NEXT_ONLINE = "bgsync_launch_next_online";
    private static BackgroundSyncLauncher sInstance;

    /* renamed from: org.chromium.content.browser.BackgroundSyncLauncher.1 */
    static class C03201 extends AsyncTask<Void, Void, Boolean> {
        final /* synthetic */ ShouldLaunchCallback val$callback;
        final /* synthetic */ Context val$context;

        C03201(Context context, ShouldLaunchCallback shouldLaunchCallback) {
            this.val$context = context;
            this.val$callback = shouldLaunchCallback;
        }

        protected Boolean doInBackground(Void... params) {
            return Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(this.val$context).getBoolean(BackgroundSyncLauncher.PREF_BACKGROUND_SYNC_LAUNCH_NEXT_ONLINE, BackgroundSyncLauncher.$assertionsDisabled));
        }

        protected void onPostExecute(Boolean shouldLaunch) {
            this.val$callback.run(shouldLaunch);
        }
    }

    /* renamed from: org.chromium.content.browser.BackgroundSyncLauncher.2 */
    class C03212 extends AsyncTask<Void, Void, Void> {
        final /* synthetic */ Context val$context;
        final /* synthetic */ boolean val$shouldLaunch;

        C03212(Context context, boolean z) {
            this.val$context = context;
            this.val$shouldLaunch = z;
        }

        protected Void doInBackground(Void... params) {
            PreferenceManager.getDefaultSharedPreferences(this.val$context).edit().putBoolean(BackgroundSyncLauncher.PREF_BACKGROUND_SYNC_LAUNCH_NEXT_ONLINE, this.val$shouldLaunch).apply();
            return null;
        }
    }

    public interface ShouldLaunchCallback {
        void run(Boolean bool);
    }

    static {
        $assertionsDisabled = !BackgroundSyncLauncher.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    @CalledByNative
    @VisibleForTesting
    protected static BackgroundSyncLauncher create(Context context) {
        if (sInstance != null) {
            throw new IllegalStateException("Already instantiated");
        }
        sInstance = new BackgroundSyncLauncher(context);
        return sInstance;
    }

    @CalledByNative
    @VisibleForTesting
    protected void destroy() {
        if ($assertionsDisabled || sInstance == this) {
            sInstance = null;
            return;
        }
        throw new AssertionError();
    }

    protected static void shouldLaunchWhenNextOnline(Context context, ShouldLaunchCallback callback) {
        new C03201(context, callback).execute(new Void[0]);
    }

    @CalledByNative
    @VisibleForTesting
    protected void setLaunchWhenNextOnline(Context context, boolean shouldLaunch) {
        new C03212(context, shouldLaunch).execute(new Void[0]);
    }

    protected static boolean hasInstance() {
        return sInstance != null ? true : $assertionsDisabled;
    }

    private BackgroundSyncLauncher(Context context) {
        setLaunchWhenNextOnline(context, $assertionsDisabled);
    }
}
