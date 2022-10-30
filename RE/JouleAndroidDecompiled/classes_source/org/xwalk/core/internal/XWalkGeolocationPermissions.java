package org.xwalk.core.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.webkit.ValueCallback;
import java.util.HashSet;
import java.util.Set;
import org.chromium.base.ThreadUtils;
import org.chromium.net.GURLUtils;

public final class XWalkGeolocationPermissions {
    private static final String PREF_PREFIX;
    private final SharedPreferences mSharedPreferences;

    /* renamed from: org.xwalk.core.internal.XWalkGeolocationPermissions.1 */
    class C04541 implements Runnable {
        final /* synthetic */ ValueCallback val$callback;
        final /* synthetic */ boolean val$finalAllowed;

        C04541(ValueCallback valueCallback, boolean z) {
            this.val$callback = valueCallback;
            this.val$finalAllowed = z;
        }

        public void run() {
            this.val$callback.onReceiveValue(Boolean.valueOf(this.val$finalAllowed));
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkGeolocationPermissions.2 */
    class C04552 implements Runnable {
        final /* synthetic */ ValueCallback val$callback;
        final /* synthetic */ Set val$origins;

        C04552(ValueCallback valueCallback, Set set) {
            this.val$callback = valueCallback;
            this.val$origins = set;
        }

        public void run() {
            this.val$callback.onReceiveValue(this.val$origins);
        }
    }

    public interface Callback {
        void invoke(String str, boolean z, boolean z2);
    }

    static {
        PREF_PREFIX = XWalkGeolocationPermissions.class.getCanonicalName() + "%";
    }

    public XWalkGeolocationPermissions(SharedPreferences sharedPreferences) {
        this.mSharedPreferences = sharedPreferences;
    }

    public void allow(String origin) {
        String key = getOriginKey(origin);
        if (key != null) {
            this.mSharedPreferences.edit().putBoolean(key, true).apply();
        }
    }

    public void deny(String origin) {
        String key = getOriginKey(origin);
        if (key != null) {
            this.mSharedPreferences.edit().putBoolean(key, false).apply();
        }
    }

    public void clear(String origin) {
        String key = getOriginKey(origin);
        if (key != null) {
            this.mSharedPreferences.edit().remove(key).apply();
        }
    }

    public void clearAll() {
        Editor editor = null;
        for (String name : this.mSharedPreferences.getAll().keySet()) {
            if (name.startsWith(PREF_PREFIX)) {
                if (editor == null) {
                    editor = this.mSharedPreferences.edit();
                }
                editor.remove(name);
            }
        }
        if (editor != null) {
            editor.apply();
        }
    }

    public boolean isOriginAllowed(String origin) {
        return this.mSharedPreferences.getBoolean(getOriginKey(origin), false);
    }

    public boolean hasOrigin(String origin) {
        return this.mSharedPreferences.contains(getOriginKey(origin));
    }

    public void getAllowed(String origin, ValueCallback<Boolean> callback) {
        ThreadUtils.postOnUiThread(new C04541(callback, isOriginAllowed(origin)));
    }

    public void getOrigins(ValueCallback<Set<String>> callback) {
        Set<String> origins = new HashSet();
        for (String name : this.mSharedPreferences.getAll().keySet()) {
            if (name.startsWith(PREF_PREFIX)) {
                origins.add(name.substring(PREF_PREFIX.length()));
            }
        }
        ThreadUtils.postOnUiThread(new C04552(callback, origins));
    }

    private String getOriginKey(String url) {
        String origin = GURLUtils.getOrigin(url);
        if (origin.isEmpty()) {
            return null;
        }
        return PREF_PREFIX + origin;
    }
}
