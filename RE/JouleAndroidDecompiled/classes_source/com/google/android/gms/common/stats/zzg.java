package com.google.android.gms.common.stats;

import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.text.TextUtils;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;

public class zzg {
    public static String zza(WakeLock wakeLock, String str) {
        StringBuilder append = new StringBuilder().append(String.valueOf((((long) Process.myPid()) << 32) | ((long) System.identityHashCode(wakeLock))));
        if (TextUtils.isEmpty(str)) {
            str = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        }
        return append.append(str).toString();
    }
}
