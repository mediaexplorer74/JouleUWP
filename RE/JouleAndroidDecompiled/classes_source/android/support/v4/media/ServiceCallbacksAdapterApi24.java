package android.support.v4.media;

import java.lang.reflect.Method;

class ServiceCallbacksAdapterApi24 extends ServiceCallbacksAdapterApi21 {
    private Method mOnLoadChildrenMethodWithOptionsMethod;

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    ServiceCallbacksAdapterApi24(java.lang.Object r8) {
        /*
        r7 = this;
        r7.<init>(r8);
        r3 = "android.service.media.IMediaBrowserServiceCallbacks";
        r2 = java.lang.Class.forName(r3);	 Catch:{ ClassNotFoundException -> 0x0028, NoSuchMethodException -> 0x002d }
        r3 = "android.content.pm.ParceledListSlice";
        r1 = java.lang.Class.forName(r3);	 Catch:{ ClassNotFoundException -> 0x0028, NoSuchMethodException -> 0x002d }
        r3 = "onLoadChildrenWithOptions";
        r4 = 3;
        r4 = new java.lang.Class[r4];	 Catch:{ ClassNotFoundException -> 0x0028, NoSuchMethodException -> 0x002d }
        r5 = 0;
        r6 = java.lang.String.class;
        r4[r5] = r6;	 Catch:{ ClassNotFoundException -> 0x0028, NoSuchMethodException -> 0x002d }
        r5 = 1;
        r4[r5] = r1;	 Catch:{ ClassNotFoundException -> 0x0028, NoSuchMethodException -> 0x002d }
        r5 = 2;
        r6 = android.os.Bundle.class;
        r4[r5] = r6;	 Catch:{ ClassNotFoundException -> 0x0028, NoSuchMethodException -> 0x002d }
        r3 = r2.getMethod(r3, r4);	 Catch:{ ClassNotFoundException -> 0x0028, NoSuchMethodException -> 0x002d }
        r7.mOnLoadChildrenMethodWithOptionsMethod = r3;	 Catch:{ ClassNotFoundException -> 0x0028, NoSuchMethodException -> 0x002d }
    L_0x0027:
        return;
    L_0x0028:
        r0 = move-exception;
    L_0x0029:
        r0.printStackTrace();
        goto L_0x0027;
    L_0x002d:
        r0 = move-exception;
        goto L_0x0029;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.media.ServiceCallbacksAdapterApi24.<init>(java.lang.Object):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void onLoadChildrenWithOptions(java.lang.String r6, java.lang.Object r7, android.os.Bundle r8) throws android.os.RemoteException {
        /*
        r5 = this;
        r1 = r5.mOnLoadChildrenMethodWithOptionsMethod;	 Catch:{ IllegalAccessException -> 0x0019, InvocationTargetException -> 0x001b, NullPointerException -> 0x0014 }
        r2 = r5.mCallbackObject;	 Catch:{ IllegalAccessException -> 0x0019, InvocationTargetException -> 0x001b, NullPointerException -> 0x0014 }
        r3 = 3;
        r3 = new java.lang.Object[r3];	 Catch:{ IllegalAccessException -> 0x0019, InvocationTargetException -> 0x001b, NullPointerException -> 0x0014 }
        r4 = 0;
        r3[r4] = r6;	 Catch:{ IllegalAccessException -> 0x0019, InvocationTargetException -> 0x001b, NullPointerException -> 0x0014 }
        r4 = 1;
        r3[r4] = r7;	 Catch:{ IllegalAccessException -> 0x0019, InvocationTargetException -> 0x001b, NullPointerException -> 0x0014 }
        r4 = 2;
        r3[r4] = r8;	 Catch:{ IllegalAccessException -> 0x0019, InvocationTargetException -> 0x001b, NullPointerException -> 0x0014 }
        r1.invoke(r2, r3);	 Catch:{ IllegalAccessException -> 0x0019, InvocationTargetException -> 0x001b, NullPointerException -> 0x0014 }
    L_0x0013:
        return;
    L_0x0014:
        r0 = move-exception;
    L_0x0015:
        r0.printStackTrace();
        goto L_0x0013;
    L_0x0019:
        r0 = move-exception;
        goto L_0x0015;
    L_0x001b:
        r0 = move-exception;
        goto L_0x0015;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.media.ServiceCallbacksAdapterApi24.onLoadChildrenWithOptions(java.lang.String, java.lang.Object, android.os.Bundle):void");
    }
}
