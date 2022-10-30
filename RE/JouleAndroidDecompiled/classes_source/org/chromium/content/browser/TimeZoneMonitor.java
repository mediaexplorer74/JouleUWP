package org.chromium.content.browser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.Log;

@JNINamespace("content")
class TimeZoneMonitor {
    private static final String TAG = "cr.TimeZoneMonitor";
    private final Context mAppContext;
    private final BroadcastReceiver mBroadcastReceiver;
    private final IntentFilter mFilter;
    private long mNativePtr;

    /* renamed from: org.chromium.content.browser.TimeZoneMonitor.1 */
    class C03521 extends BroadcastReceiver {
        C03521() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.TIMEZONE_CHANGED")) {
                TimeZoneMonitor.this.nativeTimeZoneChangedFromJava(TimeZoneMonitor.this.mNativePtr);
            } else {
                Log.m32e(TimeZoneMonitor.TAG, "unexpected intent", new Object[0]);
            }
        }
    }

    private native void nativeTimeZoneChangedFromJava(long j);

    private TimeZoneMonitor(Context context, long nativePtr) {
        this.mFilter = new IntentFilter("android.intent.action.TIMEZONE_CHANGED");
        this.mBroadcastReceiver = new C03521();
        this.mAppContext = context.getApplicationContext();
        this.mNativePtr = nativePtr;
        this.mAppContext.registerReceiver(this.mBroadcastReceiver, this.mFilter);
    }

    @CalledByNative
    static TimeZoneMonitor getInstance(Context context, long nativePtr) {
        return new TimeZoneMonitor(context, nativePtr);
    }

    @CalledByNative
    void stop() {
        this.mAppContext.unregisterReceiver(this.mBroadcastReceiver);
        this.mNativePtr = 0;
    }
}
