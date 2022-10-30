package org.chromium.ui.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener;
import android.widget.Toast;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.ui.VSyncMonitor;
import org.chromium.ui.VSyncMonitor.Listener;

@JNINamespace("ui")
public class WindowAndroid {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final int START_INTENT_FAILURE = -1;
    private static final String TAG = "WindowAndroid";
    static final String WINDOW_CALLBACK_ERRORS = "window_callback_errors";
    private final AccessibilityManager mAccessibilityManager;
    private View mAnimationPlaceholderView;
    private HashSet<Animator> mAnimationsOverContent;
    protected Context mApplicationContext;
    protected HashMap<Integer, String> mIntentErrors;
    private boolean mIsKeyboardShowing;
    private boolean mIsTouchExplorationEnabled;
    private ViewGroup mKeyboardAccessoryView;
    private LinkedList<KeyboardVisibilityListener> mKeyboardVisibilityListeners;
    private long mNativeWindowAndroid;
    protected SparseArray<IntentCallback> mOutstandingIntents;
    private TouchExplorationMonitor mTouchExplorationMonitor;
    private final Listener mVSyncListener;
    private final VSyncMonitor mVSyncMonitor;

    /* renamed from: org.chromium.ui.base.WindowAndroid.2 */
    class C04142 implements Runnable {
        final /* synthetic */ FileAccessCallback val$callback;

        C04142(FileAccessCallback fileAccessCallback) {
            this.val$callback = fileAccessCallback;
        }

        public void run() {
            this.val$callback.onFileAccessResult(WindowAndroid.$assertionsDisabled);
        }
    }

    /* renamed from: org.chromium.ui.base.WindowAndroid.3 */
    class C04153 extends AnimatorListenerAdapter {
        C04153() {
        }

        public void onAnimationEnd(Animator animation) {
            animation.removeListener(this);
            WindowAndroid.this.mAnimationsOverContent.remove(animation);
            WindowAndroid.this.refreshWillNotDraw();
        }
    }

    public interface FileAccessCallback {
        void onFileAccessResult(boolean z);
    }

    public interface IntentCallback {
        void onIntentCompleted(WindowAndroid windowAndroid, int i, ContentResolver contentResolver, Intent intent);
    }

    public interface KeyboardVisibilityListener {
        void keyboardVisibilityChanged(boolean z);
    }

    public interface PermissionCallback {
        void onRequestPermissionsResult(String[] strArr, int[] iArr);
    }

    @TargetApi(19)
    private class TouchExplorationMonitor {
        private TouchExplorationStateChangeListener mTouchExplorationListener;

        /* renamed from: org.chromium.ui.base.WindowAndroid.TouchExplorationMonitor.1 */
        class C04161 implements TouchExplorationStateChangeListener {
            final /* synthetic */ WindowAndroid val$this$0;

            C04161(WindowAndroid windowAndroid) {
                this.val$this$0 = windowAndroid;
            }

            public void onTouchExplorationStateChanged(boolean enabled) {
                WindowAndroid.this.mIsTouchExplorationEnabled = WindowAndroid.this.mAccessibilityManager.isTouchExplorationEnabled();
                WindowAndroid.this.refreshWillNotDraw();
            }
        }

        TouchExplorationMonitor() {
            this.mTouchExplorationListener = new C04161(WindowAndroid.this);
            WindowAndroid.this.mAccessibilityManager.addTouchExplorationStateChangeListener(this.mTouchExplorationListener);
        }

        void destroy() {
            WindowAndroid.this.mAccessibilityManager.removeTouchExplorationStateChangeListener(this.mTouchExplorationListener);
        }
    }

    /* renamed from: org.chromium.ui.base.WindowAndroid.1 */
    class C06401 implements Listener {
        C06401() {
        }

        public void onVSync(VSyncMonitor monitor, long vsyncTimeMicros) {
            if (WindowAndroid.this.mNativeWindowAndroid != 0) {
                WindowAndroid.this.nativeOnVSync(WindowAndroid.this.mNativeWindowAndroid, vsyncTimeMicros, WindowAndroid.this.mVSyncMonitor.getVSyncPeriodInMicroseconds());
            }
        }
    }

    private native void nativeDestroy(long j);

    private native long nativeInit();

    private native void nativeOnActivityStarted(long j);

    private native void nativeOnActivityStopped(long j);

    private native void nativeOnVSync(long j, long j2, long j3);

    private native void nativeOnVisibilityChanged(long j, boolean z);

    static {
        $assertionsDisabled = !WindowAndroid.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public boolean isInsideVSync() {
        return this.mVSyncMonitor.isInsideVSync();
    }

    @SuppressLint({"UseSparseArrays"})
    public WindowAndroid(Context context) {
        this.mNativeWindowAndroid = 0;
        this.mAnimationsOverContent = new HashSet();
        this.mIsKeyboardShowing = $assertionsDisabled;
        this.mKeyboardVisibilityListeners = new LinkedList();
        this.mVSyncListener = new C06401();
        if ($assertionsDisabled || context == context.getApplicationContext()) {
            this.mApplicationContext = context;
            this.mOutstandingIntents = new SparseArray();
            this.mIntentErrors = new HashMap();
            this.mVSyncMonitor = new VSyncMonitor(context, this.mVSyncListener);
            this.mAccessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
            return;
        }
        throw new AssertionError();
    }

    public boolean showIntent(PendingIntent intent, IntentCallback callback, Integer errorId) {
        return showCancelableIntent(intent, callback, errorId) >= 0 ? true : $assertionsDisabled;
    }

    public boolean showIntent(Intent intent, IntentCallback callback, Integer errorId) {
        return showCancelableIntent(intent, callback, errorId) >= 0 ? true : $assertionsDisabled;
    }

    public int showCancelableIntent(PendingIntent intent, IntentCallback callback, Integer errorId) {
        Log.d(TAG, "Can't show intent as context is not an Activity: " + intent);
        return START_INTENT_FAILURE;
    }

    public int showCancelableIntent(Intent intent, IntentCallback callback, Integer errorId) {
        Log.d(TAG, "Can't show intent as context is not an Activity: " + intent);
        return START_INTENT_FAILURE;
    }

    public void cancelIntent(int requestCode) {
        Log.d(TAG, "Can't cancel intent as context is not an Activity: " + requestCode);
    }

    public boolean removeIntentCallback(IntentCallback callback) {
        int requestCode = this.mOutstandingIntents.indexOfValue(callback);
        if (requestCode < 0) {
            return $assertionsDisabled;
        }
        this.mOutstandingIntents.remove(requestCode);
        this.mIntentErrors.remove(Integer.valueOf(requestCode));
        return true;
    }

    @CalledByNative
    public boolean hasPermission(String permission) {
        return this.mApplicationContext.checkPermission(permission, Process.myPid(), Process.myUid()) == 0 ? true : $assertionsDisabled;
    }

    @CalledByNative
    public boolean canRequestPermission(String permission) {
        Log.w(TAG, "Cannot determine the request permission state as the context is not an Activity");
        if ($assertionsDisabled) {
            return $assertionsDisabled;
        }
        throw new AssertionError("Failed to determine the request permission state using a WindowAndroid without an Activity");
    }

    public void requestPermissions(String[] permissions, PermissionCallback callback) {
        Log.w(TAG, "Cannot request permissions as the context is not an Activity");
        if (!$assertionsDisabled) {
            throw new AssertionError("Failed to request permissions using a WindowAndroid without an Activity");
        }
    }

    public boolean hasFileAccess() {
        return true;
    }

    public void requestFileAccess(FileAccessCallback callback) {
        new Handler().post(new C04142(callback));
    }

    public void showError(String error) {
        if (error != null) {
            Toast.makeText(this.mApplicationContext, error, 0).show();
        }
    }

    public void showError(int resId) {
        showError(this.mApplicationContext.getString(resId));
    }

    protected void showCallbackNonExistentError(String error) {
        showError(error);
    }

    public void sendBroadcast(Intent intent) {
        this.mApplicationContext.sendBroadcast(intent);
    }

    public WeakReference<Activity> getActivity() {
        return new WeakReference(null);
    }

    public Context getApplicationContext() {
        return this.mApplicationContext;
    }

    public void saveInstanceState(Bundle bundle) {
        bundle.putSerializable(WINDOW_CALLBACK_ERRORS, this.mIntentErrors);
    }

    public void restoreInstanceState(Bundle bundle) {
        if (bundle != null) {
            Serializable errors = bundle.getSerializable(WINDOW_CALLBACK_ERRORS);
            if (errors instanceof HashMap) {
                this.mIntentErrors = (HashMap) errors;
            }
        }
    }

    public void onVisibilityChanged(boolean visible) {
        if (this.mNativeWindowAndroid != 0) {
            nativeOnVisibilityChanged(this.mNativeWindowAndroid, visible);
        }
    }

    protected void onActivityStopped() {
        if (this.mNativeWindowAndroid != 0) {
            nativeOnActivityStopped(this.mNativeWindowAndroid);
        }
    }

    protected void onActivityStarted() {
        if (this.mNativeWindowAndroid != 0) {
            nativeOnActivityStarted(this.mNativeWindowAndroid);
        }
    }

    @CalledByNative
    private void requestVSyncUpdate() {
        this.mVSyncMonitor.requestUpdate();
    }

    public boolean canResolveActivity(Intent intent) {
        return this.mApplicationContext.getPackageManager().resolveActivity(intent, 0) != null ? true : $assertionsDisabled;
    }

    public void destroy() {
        if (this.mNativeWindowAndroid != 0) {
            nativeDestroy(this.mNativeWindowAndroid);
            this.mNativeWindowAndroid = 0;
        }
        if (VERSION.SDK_INT >= 19 && this.mTouchExplorationMonitor != null) {
            this.mTouchExplorationMonitor.destroy();
        }
    }

    public long getNativePointer() {
        if (this.mNativeWindowAndroid == 0) {
            this.mNativeWindowAndroid = nativeInit();
        }
        return this.mNativeWindowAndroid;
    }

    public void setAnimationPlaceholderView(View view) {
        this.mAnimationPlaceholderView = view;
        this.mIsTouchExplorationEnabled = this.mAccessibilityManager.isTouchExplorationEnabled();
        refreshWillNotDraw();
        if (VERSION.SDK_INT >= 19) {
            this.mTouchExplorationMonitor = new TouchExplorationMonitor();
        }
    }

    public void setKeyboardAccessoryView(ViewGroup view) {
        this.mKeyboardAccessoryView = view;
    }

    public ViewGroup getKeyboardAccessoryView() {
        return this.mKeyboardAccessoryView;
    }

    protected void registerKeyboardVisibilityCallbacks() {
    }

    protected void unregisterKeyboardVisibilityCallbacks() {
    }

    public void addKeyboardVisibilityListener(KeyboardVisibilityListener listener) {
        if (this.mKeyboardVisibilityListeners.isEmpty()) {
            registerKeyboardVisibilityCallbacks();
        }
        this.mKeyboardVisibilityListeners.add(listener);
    }

    public void removeKeyboardVisibilityListener(KeyboardVisibilityListener listener) {
        this.mKeyboardVisibilityListeners.remove(listener);
        if (this.mKeyboardVisibilityListeners.isEmpty()) {
            unregisterKeyboardVisibilityCallbacks();
        }
    }

    protected void keyboardVisibilityPossiblyChanged(boolean isShowing) {
        if (this.mIsKeyboardShowing != isShowing) {
            this.mIsKeyboardShowing = isShowing;
            Iterator i$ = new LinkedList(this.mKeyboardVisibilityListeners).iterator();
            while (i$.hasNext()) {
                ((KeyboardVisibilityListener) i$.next()).keyboardVisibilityChanged(isShowing);
            }
        }
    }

    public void startAnimationOverContent(Animator animation) {
        if (this.mAnimationPlaceholderView != null) {
            if (animation.isStarted()) {
                throw new IllegalArgumentException("Already started.");
            } else if (this.mAnimationsOverContent.add(animation)) {
                animation.start();
                refreshWillNotDraw();
                animation.addListener(new C04153());
            } else {
                throw new IllegalArgumentException("Already Added.");
            }
        }
    }

    private void refreshWillNotDraw() {
        boolean willNotDraw = (this.mIsTouchExplorationEnabled || !this.mAnimationsOverContent.isEmpty()) ? $assertionsDisabled : true;
        if (this.mAnimationPlaceholderView.willNotDraw() != willNotDraw) {
            this.mAnimationPlaceholderView.setWillNotDraw(willNotDraw);
        }
    }
}
