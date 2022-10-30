package com.adobe.phonegap.push;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PermissionUtils {
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";

    public static boolean hasPermission(Context appContext, String appOpsServiceId) throws UnknownError {
        ApplicationInfo appInfo = appContext.getApplicationInfo();
        String pkg = appContext.getPackageName();
        int uid = appInfo.uid;
        Object appOps = appContext.getSystemService("appops");
        try {
            Class appOpsClass = Class.forName("android.app.AppOpsManager");
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, new Class[]{Integer.TYPE, Integer.TYPE, String.class});
            int value = appOpsClass.getDeclaredField(appOpsServiceId).getInt(Integer.class);
            return Integer.parseInt(checkOpNoThrowMethod.invoke(appOps, new Object[]{Integer.valueOf(value), Integer.valueOf(uid), pkg}).toString()) == 0;
        } catch (ClassNotFoundException e) {
            throw new UnknownError("class not found");
        } catch (NoSuchMethodException e2) {
            throw new UnknownError("no such method");
        } catch (NoSuchFieldException e3) {
            throw new UnknownError("no such field");
        } catch (InvocationTargetException e4) {
            throw new UnknownError("invocation target");
        } catch (IllegalAccessException e5) {
            throw new UnknownError("illegal access");
        }
    }
}
