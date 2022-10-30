package org.chromium.ui.base;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.media.TransportMediator;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.chromium.base.ApplicationStatus;
import org.chromium.base.ApplicationStatus.ActivityStateListener;
import org.chromium.base.BuildInfo;
import org.chromium.ui.UiUtils;
import org.chromium.ui.base.WindowAndroid.IntentCallback;
import org.chromium.ui.base.WindowAndroid.PermissionCallback;

public class ActivityWindowAndroid extends WindowAndroid implements ActivityStateListener, OnLayoutChangeListener {
    private static final String PERMISSION_QUERIED_KEY_PREFIX = "HasRequestedAndroidPermission::";
    private static final int REQUEST_CODE_PREFIX = 1000;
    private static final int REQUEST_CODE_RANGE_SIZE = 100;
    private static final String TAG = "ActivityWindowAndroid";
    private final WeakReference<Activity> mActivityRef;
    private final Handler mHandler;
    private int mNextRequestCode;
    private final SparseArray<PermissionCallback> mOutstandingPermissionRequests;
    private Method mRequestPermissionsMethod;

    /* renamed from: org.chromium.ui.base.ActivityWindowAndroid.1 */
    class C04131 implements Runnable {
        final /* synthetic */ PermissionCallback val$callback;
        final /* synthetic */ String[] val$permissions;

        C04131(String[] strArr, PermissionCallback permissionCallback) {
            this.val$permissions = strArr;
            this.val$callback = permissionCallback;
        }

        public void run() {
            int[] results = new int[this.val$permissions.length];
            for (int i = 0; i < this.val$permissions.length; i++) {
                results[i] = ActivityWindowAndroid.this.hasPermission(this.val$permissions[i]) ? 0 : -1;
            }
            this.val$callback.onRequestPermissionsResult(this.val$permissions, results);
        }
    }

    public ActivityWindowAndroid(Activity activity) {
        this(activity, true);
    }

    public ActivityWindowAndroid(Activity activity, boolean listenToActivityState) {
        super(activity.getApplicationContext());
        this.mNextRequestCode = 0;
        this.mActivityRef = new WeakReference(activity);
        this.mHandler = new Handler();
        this.mOutstandingPermissionRequests = new SparseArray();
        if (listenToActivityState) {
            ApplicationStatus.registerStateListenerForActivity(this, activity);
        }
    }

    protected void registerKeyboardVisibilityCallbacks() {
        Activity activity = (Activity) this.mActivityRef.get();
        if (activity != null) {
            activity.findViewById(16908290).addOnLayoutChangeListener(this);
        }
    }

    protected void unregisterKeyboardVisibilityCallbacks() {
        Activity activity = (Activity) this.mActivityRef.get();
        if (activity != null) {
            activity.findViewById(16908290).removeOnLayoutChangeListener(this);
        }
    }

    public int showCancelableIntent(PendingIntent intent, IntentCallback callback, Integer errorId) {
        Activity activity = (Activity) this.mActivityRef.get();
        if (activity == null) {
            return -1;
        }
        int requestCode = generateNextRequestCode();
        try {
            activity.startIntentSenderForResult(intent.getIntentSender(), requestCode, new Intent(), 0, 0, 0);
            storeCallbackData(requestCode, callback, errorId);
            return requestCode;
        } catch (SendIntentException e) {
            return -1;
        }
    }

    public int showCancelableIntent(Intent intent, IntentCallback callback, Integer errorId) {
        Activity activity = (Activity) this.mActivityRef.get();
        if (activity == null) {
            return -1;
        }
        int requestCode = generateNextRequestCode();
        try {
            activity.startActivityForResult(intent, requestCode);
            storeCallbackData(requestCode, callback, errorId);
            return requestCode;
        } catch (ActivityNotFoundException e) {
            return -1;
        }
    }

    public void cancelIntent(int requestCode) {
        Activity activity = (Activity) this.mActivityRef.get();
        if (activity != null) {
            activity.finishActivity(requestCode);
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentCallback callback = (IntentCallback) this.mOutstandingIntents.get(requestCode);
        this.mOutstandingIntents.delete(requestCode);
        String errorMessage = (String) this.mIntentErrors.remove(Integer.valueOf(requestCode));
        if (callback != null) {
            callback.onIntentCompleted(this, resultCode, this.mApplicationContext.getContentResolver(), data);
            return true;
        } else if (errorMessage == null) {
            return false;
        } else {
            showCallbackNonExistentError(errorMessage);
            return true;
        }
    }

    private String getHasRequestedPermissionKey(String permission) {
        String permissionQueriedKey = permission;
        try {
            PermissionInfo permissionInfo = getApplicationContext().getPackageManager().getPermissionInfo(permission, TransportMediator.FLAG_KEY_MEDIA_NEXT);
            if (!TextUtils.isEmpty(permissionInfo.group)) {
                permissionQueriedKey = permissionInfo.group;
            }
        } catch (NameNotFoundException e) {
        }
        return PERMISSION_QUERIED_KEY_PREFIX + permissionQueriedKey;
    }

    public boolean canRequestPermission(String permission) {
        if (!BuildInfo.isMncOrLater()) {
            return false;
        }
        Activity activity = (Activity) this.mActivityRef.get();
        if (activity == null) {
            return false;
        }
        if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(getHasRequestedPermissionKey(permission), false)) {
            return false;
        }
        return true;
    }

    public void requestPermissions(String[] permissions, PermissionCallback callback) {
        if (requestPermissionsInternal(permissions, callback)) {
            Editor editor = PreferenceManager.getDefaultSharedPreferences((Activity) this.mActivityRef.get()).edit();
            for (String hasRequestedPermissionKey : permissions) {
                editor.putBoolean(getHasRequestedPermissionKey(hasRequestedPermissionKey), true);
            }
            editor.apply();
            return;
        }
        this.mHandler.post(new C04131(permissions, callback));
    }

    private boolean requestPermissionsInternal(String[] permissions, PermissionCallback callback) {
        if (!BuildInfo.isMncOrLater()) {
            return false;
        }
        Activity activity = (Activity) this.mActivityRef.get();
        if (activity == null) {
            return false;
        }
        if (this.mRequestPermissionsMethod == null) {
            try {
                this.mRequestPermissionsMethod = Activity.class.getMethod("requestPermissions", new Class[]{String[].class, Integer.TYPE});
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
        int requestCode = generateNextRequestCode();
        this.mOutstandingPermissionRequests.put(requestCode, callback);
        try {
            this.mRequestPermissionsMethod.invoke(activity, new Object[]{permissions, Integer.valueOf(requestCode)});
            return true;
        } catch (IllegalAccessException e2) {
            this.mOutstandingPermissionRequests.delete(requestCode);
            return false;
        } catch (IllegalArgumentException e3) {
            this.mOutstandingPermissionRequests.delete(requestCode);
            return false;
        } catch (InvocationTargetException e4) {
            this.mOutstandingPermissionRequests.delete(requestCode);
            return false;
        }
    }

    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionCallback callback = (PermissionCallback) this.mOutstandingPermissionRequests.get(requestCode);
        this.mOutstandingPermissionRequests.delete(requestCode);
        if (callback == null) {
            return false;
        }
        callback.onRequestPermissionsResult(permissions, grantResults);
        return true;
    }

    public WeakReference<Activity> getActivity() {
        return new WeakReference(this.mActivityRef.get());
    }

    public void onActivityStateChange(Activity activity, int newState) {
        if (newState == 5) {
            onActivityStopped();
        } else if (newState == 2) {
            onActivityStarted();
        }
    }

    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        keyboardVisibilityPossiblyChanged(UiUtils.isKeyboardShowing((Context) this.mActivityRef.get(), v));
    }

    private int generateNextRequestCode() {
        int requestCode = this.mNextRequestCode + REQUEST_CODE_PREFIX;
        this.mNextRequestCode = (this.mNextRequestCode + 1) % REQUEST_CODE_RANGE_SIZE;
        return requestCode;
    }

    private void storeCallbackData(int requestCode, IntentCallback callback, Integer errorId) {
        this.mOutstandingIntents.put(requestCode, callback);
        this.mIntentErrors.put(Integer.valueOf(requestCode), errorId == null ? null : this.mApplicationContext.getString(errorId.intValue()));
    }
}
