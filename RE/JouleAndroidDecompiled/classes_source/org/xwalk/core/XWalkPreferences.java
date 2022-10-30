package org.xwalk.core;

public class XWalkPreferences {
    public static final String ALLOW_UNIVERSAL_ACCESS_FROM_FILE = "allow-universal-access-from-file";
    public static final String ANIMATABLE_XWALK_VIEW = "animatable-xwalk-view";
    public static final String JAVASCRIPT_CAN_OPEN_WINDOW = "javascript-can-open-window";
    public static final String PROFILE_NAME = "profile-name";
    public static final String REMOTE_DEBUGGING = "remote-debugging";
    public static final String SUPPORT_MULTIPLE_WINDOWS = "support-multiple-windows";
    private static XWalkCoreWrapper coreWrapper;
    private static ReflectMethod getBooleanValueStringMethod;
    private static ReflectMethod getIntegerValueStringMethod;
    private static ReflectMethod getStringValueStringMethod;
    private static ReflectMethod getValueStringMethod;
    private static ReflectMethod setValueStringStringMethod;
    private static ReflectMethod setValueStringbooleanMethod;
    private static ReflectMethod setValueStringintMethod;

    public static void setValue(String key, boolean enabled) {
        reflectionInit();
        if (setValueStringbooleanMethod.isNull()) {
            setValueStringbooleanMethod.setArguments(key, Boolean.valueOf(enabled));
            XWalkCoreWrapper.reserveReflectMethod(setValueStringbooleanMethod);
            return;
        }
        setValueStringbooleanMethod.invoke(key, Boolean.valueOf(enabled));
    }

    static {
        setValueStringbooleanMethod = new ReflectMethod(null, "setValue", new Class[0]);
        setValueStringintMethod = new ReflectMethod(null, "setValue", new Class[0]);
        setValueStringStringMethod = new ReflectMethod(null, "setValue", new Class[0]);
        getValueStringMethod = new ReflectMethod(null, "getValue", new Class[0]);
        getBooleanValueStringMethod = new ReflectMethod(null, "getBooleanValue", new Class[0]);
        getIntegerValueStringMethod = new ReflectMethod(null, "getIntegerValue", new Class[0]);
        getStringValueStringMethod = new ReflectMethod(null, "getStringValue", new Class[0]);
    }

    public static void setValue(String key, int value) {
        reflectionInit();
        if (setValueStringintMethod.isNull()) {
            setValueStringintMethod.setArguments(key, Integer.valueOf(value));
            XWalkCoreWrapper.reserveReflectMethod(setValueStringintMethod);
            return;
        }
        setValueStringintMethod.invoke(key, Integer.valueOf(value));
    }

    public static void setValue(String key, String value) {
        reflectionInit();
        if (setValueStringStringMethod.isNull()) {
            setValueStringStringMethod.setArguments(key, value);
            XWalkCoreWrapper.reserveReflectMethod(setValueStringStringMethod);
            return;
        }
        setValueStringStringMethod.invoke(key, value);
    }

    public static boolean getValue(String key) {
        reflectionInit();
        return ((Boolean) getValueStringMethod.invoke(key)).booleanValue();
    }

    public static boolean getBooleanValue(String key) {
        reflectionInit();
        return ((Boolean) getBooleanValueStringMethod.invoke(key)).booleanValue();
    }

    public static int getIntegerValue(String key) {
        reflectionInit();
        return ((Integer) getIntegerValueStringMethod.invoke(key)).intValue();
    }

    public static String getStringValue(String key) {
        reflectionInit();
        return (String) getStringValueStringMethod.invoke(key);
    }

    static void reflectionInit() {
        if (coreWrapper == null) {
            XWalkCoreWrapper.initEmbeddedMode();
            coreWrapper = XWalkCoreWrapper.getInstance();
            if (coreWrapper == null) {
                XWalkCoreWrapper.reserveReflectClass(XWalkPreferences.class);
                return;
            }
            Class<?> bridgeClass = coreWrapper.getBridgeClass("XWalkPreferencesBridge");
            setValueStringbooleanMethod.init(null, bridgeClass, "setValue", String.class, Boolean.TYPE);
            setValueStringintMethod.init(null, bridgeClass, "setValue", String.class, Integer.TYPE);
            setValueStringStringMethod.init(null, bridgeClass, "setValue", String.class, String.class);
            getValueStringMethod.init(null, bridgeClass, "getValue", String.class);
            getBooleanValueStringMethod.init(null, bridgeClass, "getBooleanValue", String.class);
            getIntegerValueStringMethod.init(null, bridgeClass, "getIntegerValue", String.class);
            getStringValueStringMethod.init(null, bridgeClass, "getStringValue", String.class);
        }
    }
}
