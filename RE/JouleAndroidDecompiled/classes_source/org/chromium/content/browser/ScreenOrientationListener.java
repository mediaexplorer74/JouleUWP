package org.chromium.content.browser;

import android.annotation.SuppressLint;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.os.Build.VERSION;
import android.view.WindowManager;
import com.google.android.gms.common.ConnectionResult;
import java.util.Iterator;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.base.Log;
import org.chromium.base.ObserverList;
import org.chromium.base.ThreadUtils;
import org.chromium.base.VisibleForTesting;
import org.chromium.ui.gfx.DeviceDisplayInfo;

@VisibleForTesting
public class ScreenOrientationListener {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String TAG = "cr.ScreenOrientation";
    private static ScreenOrientationListener sInstance;
    private Context mAppContext;
    private ScreenOrientationListenerBackend mBackend;
    private final ObserverList<ScreenOrientationObserver> mObservers;
    private int mOrientation;

    /* renamed from: org.chromium.content.browser.ScreenOrientationListener.1 */
    class C03481 implements Runnable {
        final /* synthetic */ ScreenOrientationObserver val$obs;

        C03481(ScreenOrientationObserver screenOrientationObserver) {
            this.val$obs = screenOrientationObserver;
        }

        public void run() {
            this.val$obs.onScreenOrientationChanged(ScreenOrientationListener.this.mOrientation);
        }
    }

    private interface ScreenOrientationListenerBackend {
        void startAccurateListening();

        void startListening();

        void stopAccurateListening();

        void stopListening();
    }

    public interface ScreenOrientationObserver {
        void onScreenOrientationChanged(int i);
    }

    private class ScreenOrientationConfigurationListener implements ScreenOrientationListenerBackend, ComponentCallbacks {
        static final /* synthetic */ boolean $assertionsDisabled;
        private static final long POLLING_DELAY = 500;
        private int mAccurateCount;

        /* renamed from: org.chromium.content.browser.ScreenOrientationListener.ScreenOrientationConfigurationListener.1 */
        class C03491 implements Runnable {
            final /* synthetic */ ScreenOrientationConfigurationListener val$self;

            C03491(ScreenOrientationConfigurationListener screenOrientationConfigurationListener) {
                this.val$self = screenOrientationConfigurationListener;
            }

            public void run() {
                this.val$self.onConfigurationChanged(null);
                if (this.val$self.mAccurateCount >= 1) {
                    ThreadUtils.postOnUiThreadDelayed(this, ScreenOrientationConfigurationListener.POLLING_DELAY);
                }
            }
        }

        static {
            $assertionsDisabled = !ScreenOrientationListener.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        }

        private ScreenOrientationConfigurationListener() {
            this.mAccurateCount = 0;
        }

        public void startListening() {
            ScreenOrientationListener.this.mAppContext.registerComponentCallbacks(this);
        }

        public void stopListening() {
            ScreenOrientationListener.this.mAppContext.unregisterComponentCallbacks(this);
        }

        public void startAccurateListening() {
            this.mAccurateCount++;
            if (this.mAccurateCount <= 1) {
                ThreadUtils.postOnUiThreadDelayed(new C03491(this), POLLING_DELAY);
            }
        }

        public void stopAccurateListening() {
            this.mAccurateCount--;
            if (!$assertionsDisabled && this.mAccurateCount < 0) {
                throw new AssertionError();
            }
        }

        public void onConfigurationChanged(Configuration newConfig) {
            ScreenOrientationListener.this.notifyObservers();
        }

        public void onLowMemory() {
        }
    }

    @SuppressLint({"NewApi"})
    private class ScreenOrientationDisplayListener implements ScreenOrientationListenerBackend, DisplayListener {
        private ScreenOrientationDisplayListener() {
        }

        public void startListening() {
            ((DisplayManager) ScreenOrientationListener.this.mAppContext.getSystemService("display")).registerDisplayListener(this, null);
        }

        public void stopListening() {
            ((DisplayManager) ScreenOrientationListener.this.mAppContext.getSystemService("display")).unregisterDisplayListener(this);
        }

        public void startAccurateListening() {
        }

        public void stopAccurateListening() {
        }

        public void onDisplayAdded(int displayId) {
        }

        public void onDisplayRemoved(int displayId) {
        }

        public void onDisplayChanged(int displayId) {
            ScreenOrientationListener.this.notifyObservers();
        }
    }

    static {
        $assertionsDisabled = !ScreenOrientationListener.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public static ScreenOrientationListener getInstance() {
        ThreadUtils.assertOnUiThread();
        if (sInstance == null) {
            sInstance = new ScreenOrientationListener();
        }
        return sInstance;
    }

    private ScreenOrientationListener() {
        this.mObservers = new ObserverList();
        this.mBackend = VERSION.SDK_INT >= 17 ? new ScreenOrientationDisplayListener() : new ScreenOrientationConfigurationListener();
    }

    public void addObserver(ScreenOrientationObserver observer, Context context) {
        if (this.mAppContext == null) {
            this.mAppContext = context.getApplicationContext();
        }
        if (!$assertionsDisabled && this.mAppContext != context.getApplicationContext()) {
            throw new AssertionError();
        } else if (!$assertionsDisabled && this.mAppContext == null) {
            throw new AssertionError();
        } else if (this.mObservers.addObserver(observer)) {
            if (this.mObservers.size() == 1) {
                updateOrientation();
                this.mBackend.startListening();
            }
            ScreenOrientationObserver obs = observer;
            ThreadUtils.assertOnUiThread();
            ThreadUtils.postOnUiThread(new C03481(obs));
        } else {
            Log.m42w(TAG, "Adding an observer that is already present!", new Object[0]);
        }
    }

    public void removeObserver(ScreenOrientationObserver observer) {
        if (!this.mObservers.removeObserver(observer)) {
            Log.m42w(TAG, "Removing an inexistent observer!", new Object[0]);
        } else if (this.mObservers.isEmpty()) {
            this.mBackend.stopListening();
        }
    }

    public void startAccurateListening() {
        this.mBackend.startAccurateListening();
    }

    public void stopAccurateListening() {
        this.mBackend.stopAccurateListening();
    }

    private void notifyObservers() {
        int previousOrientation = this.mOrientation;
        updateOrientation();
        if (this.mOrientation != previousOrientation) {
            DeviceDisplayInfo.create(this.mAppContext).updateNativeSharedDisplayInfo();
            Iterator i$ = this.mObservers.iterator();
            while (i$.hasNext()) {
                ((ScreenOrientationObserver) i$.next()).onScreenOrientationChanged(this.mOrientation);
            }
        }
    }

    private void updateOrientation() {
        switch (((WindowManager) this.mAppContext.getSystemService("window")).getDefaultDisplay().getRotation()) {
            case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                this.mOrientation = 0;
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                this.mOrientation = 90;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                this.mOrientation = 180;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                this.mOrientation = -90;
            default:
                throw new IllegalStateException("Display.getRotation() shouldn't return that value");
        }
    }
}
