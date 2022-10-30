package org.xwalk.core.internal;

import android.content.Context;

class XWalkInternalResources {
    private static final String GENERATED_RESOURCE_CLASS = "org.xwalk.core.R";
    private static final String[] INTERNAL_RESOURCE_CLASSES;
    private static final String TAG = "XWalkInternalResources";
    private static boolean loaded;

    XWalkInternalResources() {
    }

    static {
        loaded = false;
        INTERNAL_RESOURCE_CLASSES = new String[]{"org.chromium.components.web_contents_delegate_android.R", "org.chromium.content.R", "org.chromium.ui.R", "org.xwalk.core.internal.R"};
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void doResetIds(android.content.Context r27) {
        /*
        r7 = r27.getClassLoader();
        r24 = r27.getApplicationContext();
        r3 = r24.getClassLoader();
        r4 = INTERNAL_RESOURCE_CLASSES;
        r0 = r4.length;
        r19 = r0;
        r13 = 0;
        r15 = r13;
    L_0x0013:
        r0 = r19;
        if (r15 >= r0) goto L_0x0159;
    L_0x0017:
        r22 = r4[r15];
        r0 = r22;
        r18 = r7.loadClass(r0);	 Catch:{ ClassNotFoundException -> 0x00dd }
        r17 = r18.getClasses();	 Catch:{ ClassNotFoundException -> 0x00dd }
        r5 = r17;
        r0 = r5.length;	 Catch:{ ClassNotFoundException -> 0x00dd }
        r20 = r0;
        r13 = 0;
        r14 = r13;
    L_0x002a:
        r0 = r20;
        if (r14 >= r0) goto L_0x00fa;
    L_0x002e:
        r16 = r5[r14];	 Catch:{ ClassNotFoundException -> 0x00dd }
        r24 = r16.getName();	 Catch:{ ClassNotFoundException -> 0x00dd }
        r25 = "org.xwalk.core.R";
        r0 = r24;
        r1 = r22;
        r2 = r25;
        r11 = r0.replace(r1, r2);	 Catch:{ ClassNotFoundException -> 0x00dd }
        r12 = r3.loadClass(r11);	 Catch:{ ClassNotFoundException -> 0x0091 }
        r10 = r16.getFields();	 Catch:{ ClassNotFoundException -> 0x00dd }
        r6 = r10;
        r0 = r6.length;	 Catch:{ ClassNotFoundException -> 0x00dd }
        r21 = r0;
        r13 = 0;
    L_0x004d:
        r0 = r21;
        if (r13 >= r0) goto L_0x00ac;
    L_0x0051:
        r9 = r6[r13];	 Catch:{ ClassNotFoundException -> 0x00dd }
        r24 = r9.getModifiers();	 Catch:{ ClassNotFoundException -> 0x00dd }
        r24 = java.lang.reflect.Modifier.isFinal(r24);	 Catch:{ ClassNotFoundException -> 0x00dd }
        if (r24 == 0) goto L_0x0064;
    L_0x005d:
        r24 = 1;
        r0 = r24;
        r9.setAccessible(r0);	 Catch:{ ClassNotFoundException -> 0x00dd }
    L_0x0064:
        r24 = r9.getName();	 Catch:{ IllegalAccessException -> 0x00b1, IllegalArgumentException -> 0x00ff, NoSuchFieldException -> 0x012c }
        r0 = r24;
        r24 = r12.getField(r0);	 Catch:{ IllegalAccessException -> 0x00b1, IllegalArgumentException -> 0x00ff, NoSuchFieldException -> 0x012c }
        r25 = 0;
        r23 = r24.getInt(r25);	 Catch:{ IllegalAccessException -> 0x00b1, IllegalArgumentException -> 0x00ff, NoSuchFieldException -> 0x012c }
        r24 = 0;
        r0 = r24;
        r1 = r23;
        r9.setInt(r0, r1);	 Catch:{ IllegalAccessException -> 0x00b1, IllegalArgumentException -> 0x00ff, NoSuchFieldException -> 0x012c }
    L_0x007d:
        r24 = r9.getModifiers();	 Catch:{ ClassNotFoundException -> 0x00dd }
        r24 = java.lang.reflect.Modifier.isFinal(r24);	 Catch:{ ClassNotFoundException -> 0x00dd }
        if (r24 == 0) goto L_0x008e;
    L_0x0087:
        r24 = 0;
        r0 = r24;
        r9.setAccessible(r0);	 Catch:{ ClassNotFoundException -> 0x00dd }
    L_0x008e:
        r13 = r13 + 1;
        goto L_0x004d;
    L_0x0091:
        r8 = move-exception;
        r24 = "XWalkInternalResources";
        r25 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x00dd }
        r25.<init>();	 Catch:{ ClassNotFoundException -> 0x00dd }
        r0 = r25;
        r25 = r0.append(r11);	 Catch:{ ClassNotFoundException -> 0x00dd }
        r26 = "is not found.";
        r25 = r25.append(r26);	 Catch:{ ClassNotFoundException -> 0x00dd }
        r25 = r25.toString();	 Catch:{ ClassNotFoundException -> 0x00dd }
        android.util.Log.w(r24, r25);	 Catch:{ ClassNotFoundException -> 0x00dd }
    L_0x00ac:
        r13 = r14 + 1;
        r14 = r13;
        goto L_0x002a;
    L_0x00b1:
        r8 = move-exception;
        r24 = "XWalkInternalResources";
        r25 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x00dd }
        r25.<init>();	 Catch:{ ClassNotFoundException -> 0x00dd }
        r26 = r12.getName();	 Catch:{ ClassNotFoundException -> 0x00dd }
        r25 = r25.append(r26);	 Catch:{ ClassNotFoundException -> 0x00dd }
        r26 = ".";
        r25 = r25.append(r26);	 Catch:{ ClassNotFoundException -> 0x00dd }
        r26 = r9.getName();	 Catch:{ ClassNotFoundException -> 0x00dd }
        r25 = r25.append(r26);	 Catch:{ ClassNotFoundException -> 0x00dd }
        r26 = " is not accessable.";
        r25 = r25.append(r26);	 Catch:{ ClassNotFoundException -> 0x00dd }
        r25 = r25.toString();	 Catch:{ ClassNotFoundException -> 0x00dd }
        android.util.Log.w(r24, r25);	 Catch:{ ClassNotFoundException -> 0x00dd }
        goto L_0x007d;
    L_0x00dd:
        r8 = move-exception;
        r24 = "XWalkInternalResources";
        r25 = new java.lang.StringBuilder;
        r25.<init>();
        r0 = r25;
        r1 = r22;
        r25 = r0.append(r1);
        r26 = "is not found.";
        r25 = r25.append(r26);
        r25 = r25.toString();
        android.util.Log.w(r24, r25);
    L_0x00fa:
        r13 = r15 + 1;
        r15 = r13;
        goto L_0x0013;
    L_0x00ff:
        r8 = move-exception;
        r24 = "XWalkInternalResources";
        r25 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x00dd }
        r25.<init>();	 Catch:{ ClassNotFoundException -> 0x00dd }
        r26 = r12.getName();	 Catch:{ ClassNotFoundException -> 0x00dd }
        r25 = r25.append(r26);	 Catch:{ ClassNotFoundException -> 0x00dd }
        r26 = ".";
        r25 = r25.append(r26);	 Catch:{ ClassNotFoundException -> 0x00dd }
        r26 = r9.getName();	 Catch:{ ClassNotFoundException -> 0x00dd }
        r25 = r25.append(r26);	 Catch:{ ClassNotFoundException -> 0x00dd }
        r26 = " is not int.";
        r25 = r25.append(r26);	 Catch:{ ClassNotFoundException -> 0x00dd }
        r25 = r25.toString();	 Catch:{ ClassNotFoundException -> 0x00dd }
        android.util.Log.w(r24, r25);	 Catch:{ ClassNotFoundException -> 0x00dd }
        goto L_0x007d;
    L_0x012c:
        r8 = move-exception;
        r24 = "XWalkInternalResources";
        r25 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x00dd }
        r25.<init>();	 Catch:{ ClassNotFoundException -> 0x00dd }
        r26 = r12.getName();	 Catch:{ ClassNotFoundException -> 0x00dd }
        r25 = r25.append(r26);	 Catch:{ ClassNotFoundException -> 0x00dd }
        r26 = ".";
        r25 = r25.append(r26);	 Catch:{ ClassNotFoundException -> 0x00dd }
        r26 = r9.getName();	 Catch:{ ClassNotFoundException -> 0x00dd }
        r25 = r25.append(r26);	 Catch:{ ClassNotFoundException -> 0x00dd }
        r26 = " is not found.";
        r25 = r25.append(r26);	 Catch:{ ClassNotFoundException -> 0x00dd }
        r25 = r25.toString();	 Catch:{ ClassNotFoundException -> 0x00dd }
        android.util.Log.w(r24, r25);	 Catch:{ ClassNotFoundException -> 0x00dd }
        goto L_0x007d;
    L_0x0159:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xwalk.core.internal.XWalkInternalResources.doResetIds(android.content.Context):void");
    }

    static void resetIds(Context context) {
        if (!loaded) {
            doResetIds(context);
            loaded = true;
        }
    }
}
