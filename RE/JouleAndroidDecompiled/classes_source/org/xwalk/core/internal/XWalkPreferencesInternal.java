package org.xwalk.core.internal;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

@XWalkAPI(noInstance = true)
public class XWalkPreferencesInternal {
    @XWalkAPI
    public static final String ALLOW_UNIVERSAL_ACCESS_FROM_FILE = "allow-universal-access-from-file";
    @XWalkAPI
    public static final String ANIMATABLE_XWALK_VIEW = "animatable-xwalk-view";
    static final String ENABLE_EXTENSIONS = "enable-extensions";
    static final String ENABLE_JAVASCRIPT = "enable-javascript";
    @XWalkAPI
    public static final String JAVASCRIPT_CAN_OPEN_WINDOW = "javascript-can-open-window";
    @XWalkAPI
    public static final String PROFILE_NAME = "profile-name";
    @XWalkAPI
    public static final String REMOTE_DEBUGGING = "remote-debugging";
    @XWalkAPI
    public static final String SUPPORT_MULTIPLE_WINDOWS = "support-multiple-windows";
    private static ArrayList<WeakReference<KeyValueChangeListener>> sListeners;
    private static HashMap<String, PreferenceValue> sPrefMap;
    private static ReferenceQueue<KeyValueChangeListener> sRefQueue;

    interface KeyValueChangeListener {
        void onKeyValueChanged(String str, PreferenceValue preferenceValue);
    }

    static class PreferenceValue {
        static final int PREFERENCE_TYPE_BOOLEAN = 1;
        static final int PREFERENCE_TYPE_INTEGER = 2;
        static final int PREFERENCE_TYPE_STRING = 3;
        int mType;
        Object mValue;

        PreferenceValue(boolean value) {
            this.mType = PREFERENCE_TYPE_BOOLEAN;
            this.mValue = Boolean.valueOf(value);
        }

        PreferenceValue(int value) {
            this.mType = PREFERENCE_TYPE_INTEGER;
            this.mValue = Integer.valueOf(value);
        }

        PreferenceValue(String value) {
            this.mType = PREFERENCE_TYPE_STRING;
            this.mValue = value;
        }

        int getType() {
            return this.mType;
        }

        boolean getBooleanValue() {
            if (this.mType != PREFERENCE_TYPE_BOOLEAN) {
                return false;
            }
            return ((Boolean) this.mValue).booleanValue();
        }

        int getIntegerValue() {
            if (this.mType != PREFERENCE_TYPE_INTEGER) {
                return -1;
            }
            return ((Integer) this.mValue).intValue();
        }

        String getStringValue() {
            if (this.mType != PREFERENCE_TYPE_STRING) {
                return null;
            }
            return (String) this.mValue;
        }
    }

    static {
        sPrefMap = new HashMap();
        sListeners = new ArrayList();
        sRefQueue = new ReferenceQueue();
        sPrefMap.put(REMOTE_DEBUGGING, new PreferenceValue(false));
        sPrefMap.put(ANIMATABLE_XWALK_VIEW, new PreferenceValue(false));
        sPrefMap.put(ENABLE_JAVASCRIPT, new PreferenceValue(true));
        sPrefMap.put(JAVASCRIPT_CAN_OPEN_WINDOW, new PreferenceValue(true));
        sPrefMap.put(ALLOW_UNIVERSAL_ACCESS_FROM_FILE, new PreferenceValue(false));
        sPrefMap.put(SUPPORT_MULTIPLE_WINDOWS, new PreferenceValue(false));
        sPrefMap.put(ENABLE_EXTENSIONS, new PreferenceValue(true));
        sPrefMap.put(PROFILE_NAME, new PreferenceValue("Default"));
    }

    @XWalkAPI(reservable = true)
    public static synchronized void setValue(String key, boolean enabled) throws RuntimeException {
        synchronized (XWalkPreferencesInternal.class) {
            checkKey(key);
            if (key != ANIMATABLE_XWALK_VIEW || sListeners.isEmpty()) {
                if (((PreferenceValue) sPrefMap.get(key)).getBooleanValue() != enabled) {
                    PreferenceValue v = new PreferenceValue(enabled);
                    sPrefMap.put(key, v);
                    onKeyValueChanged(key, v);
                }
            } else {
                throw new RuntimeException("Warning: the preference key " + key + " can not be set if the preference is already loaded by Crosswalk");
            }
        }
    }

    @XWalkAPI(reservable = true)
    public static synchronized void setValue(String key, int value) throws RuntimeException {
        synchronized (XWalkPreferencesInternal.class) {
            checkKey(key);
            if (key != ANIMATABLE_XWALK_VIEW || sListeners.isEmpty()) {
                if (((PreferenceValue) sPrefMap.get(key)).getIntegerValue() != value) {
                    PreferenceValue v = new PreferenceValue(value);
                    sPrefMap.put(key, v);
                    onKeyValueChanged(key, v);
                }
            } else {
                throw new RuntimeException("Warning: the preference key " + key + " can not be set if the preference is already loaded by Crosswalk");
            }
        }
    }

    @XWalkAPI(reservable = true)
    public static synchronized void setValue(String key, String value) throws RuntimeException {
        synchronized (XWalkPreferencesInternal.class) {
            checkKey(key);
            if (key != ANIMATABLE_XWALK_VIEW || sListeners.isEmpty()) {
                if (value != null) {
                    if (!value.equals(((PreferenceValue) sPrefMap.get(key)).getStringValue())) {
                        PreferenceValue v = new PreferenceValue(value);
                        sPrefMap.put(key, v);
                        onKeyValueChanged(key, v);
                    }
                }
            } else {
                throw new RuntimeException("Warning: the preference key " + key + " can not be set if the preference is already loaded by Crosswalk");
            }
        }
    }

    @XWalkAPI
    public static synchronized boolean getValue(String key) throws RuntimeException {
        boolean booleanValue;
        synchronized (XWalkPreferencesInternal.class) {
            checkKey(key);
            booleanValue = ((PreferenceValue) sPrefMap.get(key)).getBooleanValue();
        }
        return booleanValue;
    }

    @XWalkAPI
    public static synchronized boolean getBooleanValue(String key) throws RuntimeException {
        boolean booleanValue;
        synchronized (XWalkPreferencesInternal.class) {
            checkKey(key);
            booleanValue = ((PreferenceValue) sPrefMap.get(key)).getBooleanValue();
        }
        return booleanValue;
    }

    @XWalkAPI
    public static synchronized int getIntegerValue(String key) throws RuntimeException {
        int integerValue;
        synchronized (XWalkPreferencesInternal.class) {
            checkKey(key);
            integerValue = ((PreferenceValue) sPrefMap.get(key)).getIntegerValue();
        }
        return integerValue;
    }

    @XWalkAPI
    public static synchronized String getStringValue(String key) throws RuntimeException {
        String stringValue;
        synchronized (XWalkPreferencesInternal.class) {
            checkKey(key);
            stringValue = ((PreferenceValue) sPrefMap.get(key)).getStringValue();
        }
        return stringValue;
    }

    static synchronized void load(KeyValueChangeListener listener) {
        synchronized (XWalkPreferencesInternal.class) {
            for (Entry<String, PreferenceValue> entry : sPrefMap.entrySet()) {
                listener.onKeyValueChanged((String) entry.getKey(), (PreferenceValue) entry.getValue());
            }
            registerListener(listener);
        }
    }

    static synchronized void unload(KeyValueChangeListener listener) {
        synchronized (XWalkPreferencesInternal.class) {
            unregisterListener(listener);
        }
    }

    private static synchronized void registerListener(KeyValueChangeListener listener) {
        synchronized (XWalkPreferencesInternal.class) {
            removeEnqueuedReference();
            sListeners.add(new WeakReference(listener, sRefQueue));
        }
    }

    private static synchronized void unregisterListener(KeyValueChangeListener listener) {
        synchronized (XWalkPreferencesInternal.class) {
            removeEnqueuedReference();
            Iterator i$ = sListeners.iterator();
            while (i$.hasNext()) {
                WeakReference<KeyValueChangeListener> weakListener = (WeakReference) i$.next();
                if (weakListener.get() == listener) {
                    sListeners.remove(weakListener);
                    break;
                }
            }
        }
    }

    private static void onKeyValueChanged(String key, PreferenceValue value) {
        Iterator i$ = sListeners.iterator();
        while (i$.hasNext()) {
            KeyValueChangeListener listener = (KeyValueChangeListener) ((WeakReference) i$.next()).get();
            if (listener != null) {
                listener.onKeyValueChanged(key, value);
            }
        }
    }

    private static void checkKey(String key) throws RuntimeException {
        removeEnqueuedReference();
        if (!sPrefMap.containsKey(key)) {
            throw new RuntimeException("Warning: the preference key " + key + " is not supported by Crosswalk.");
        }
    }

    private static void removeEnqueuedReference() {
        while (true) {
            WeakReference<KeyValueChangeListener> toRemove = (WeakReference) sRefQueue.poll();
            if (toRemove != null) {
                sListeners.remove(toRemove);
            } else {
                return;
            }
        }
    }
}
