package org.chromium.base;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window.Callback;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;

public class ApplicationStatusManager {
    private static ObserverList<WindowFocusChangedListener> sWindowFocusListeners;

    /* renamed from: org.chromium.base.ApplicationStatusManager.1 */
    static class C03011 implements ActivityLifecycleCallbacks {
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !ApplicationStatusManager.class.desiredAssertionStatus();
        }

        C03011() {
        }

        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            ApplicationStatusManager.setWindowFocusChangedCallback(activity);
        }

        public void onActivityDestroyed(Activity activity) {
            if (!$assertionsDisabled && !Proxy.isProxyClass(activity.getWindow().getCallback().getClass())) {
                throw new AssertionError();
            }
        }

        public void onActivityPaused(Activity activity) {
            if (!$assertionsDisabled && !Proxy.isProxyClass(activity.getWindow().getCallback().getClass())) {
                throw new AssertionError();
            }
        }

        public void onActivityResumed(Activity activity) {
            if (!$assertionsDisabled && !Proxy.isProxyClass(activity.getWindow().getCallback().getClass())) {
                throw new AssertionError();
            }
        }

        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            if (!$assertionsDisabled && !Proxy.isProxyClass(activity.getWindow().getCallback().getClass())) {
                throw new AssertionError();
            }
        }

        public void onActivityStarted(Activity activity) {
            if (!$assertionsDisabled && !Proxy.isProxyClass(activity.getWindow().getCallback().getClass())) {
                throw new AssertionError();
            }
        }

        public void onActivityStopped(Activity activity) {
            if (!$assertionsDisabled && !Proxy.isProxyClass(activity.getWindow().getCallback().getClass())) {
                throw new AssertionError();
            }
        }
    }

    private static class WindowCallbackProxy implements InvocationHandler {
        private final Activity mActivity;
        private final Callback mCallback;

        public WindowCallbackProxy(Activity activity, Callback callback) {
            this.mCallback = callback;
            this.mActivity = activity;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("onWindowFocusChanged") && args.length == 1 && (args[0] instanceof Boolean)) {
                onWindowFocusChanged(((Boolean) args[0]).booleanValue());
                return null;
            } else if (method.getName().equals("dispatchKeyEvent") && args.length == 1 && (args[0] instanceof KeyEvent)) {
                return Boolean.valueOf(dispatchKeyEvent((KeyEvent) args[0]));
            } else {
                return method.invoke(this.mCallback, args);
            }
        }

        public void onWindowFocusChanged(boolean hasFocus) {
            this.mCallback.onWindowFocusChanged(hasFocus);
            Iterator i$ = ApplicationStatusManager.sWindowFocusListeners.iterator();
            while (i$.hasNext()) {
                ((WindowFocusChangedListener) i$.next()).onWindowFocusChanged(this.mActivity, hasFocus);
            }
        }

        public boolean dispatchKeyEvent(KeyEvent event) {
            if (event.getKeyCode() == 82 && this.mActivity.dispatchKeyEvent(event)) {
                return true;
            }
            return this.mCallback.dispatchKeyEvent(event);
        }
    }

    public interface WindowFocusChangedListener {
        void onWindowFocusChanged(Activity activity, boolean z);
    }

    static {
        sWindowFocusListeners = new ObserverList();
    }

    public static void init(Application app) {
        ApplicationStatus.initialize(app);
        app.registerActivityLifecycleCallbacks(new C03011());
    }

    public static void registerWindowFocusChangedListener(WindowFocusChangedListener listener) {
        sWindowFocusListeners.addObserver(listener);
    }

    public static void unregisterWindowFocusChangedListener(WindowFocusChangedListener listener) {
        sWindowFocusListeners.removeObserver(listener);
    }

    public static void informActivityStarted(Activity activity) {
        setWindowFocusChangedCallback(activity);
        ApplicationStatus.informActivityStarted(activity);
    }

    private static void setWindowFocusChangedCallback(Activity activity) {
        Callback callback = activity.getWindow().getCallback();
        activity.getWindow().setCallback((Callback) Proxy.newProxyInstance(Callback.class.getClassLoader(), new Class[]{Callback.class}, new WindowCallbackProxy(activity, callback)));
    }
}
