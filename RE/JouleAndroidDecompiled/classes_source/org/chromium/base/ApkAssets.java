package org.chromium.base;

@JNINamespace("base::android")
public class ApkAssets {
    private static final String LOGTAG = "ApkAssets";

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @org.chromium.base.CalledByNative
    public static long[] open(android.content.Context r8, java.lang.String r9) {
        /*
        r0 = 0;
        r3 = r8.getAssets();	 Catch:{ IOException -> 0x0035 }
        r0 = r3.openNonAssetFd(r9);	 Catch:{ IOException -> 0x0035 }
        r4 = 3;
        r4 = new long[r4];	 Catch:{ IOException -> 0x0035 }
        r5 = 0;
        r6 = r0.getParcelFileDescriptor();	 Catch:{ IOException -> 0x0035 }
        r6 = r6.detachFd();	 Catch:{ IOException -> 0x0035 }
        r6 = (long) r6;	 Catch:{ IOException -> 0x0035 }
        r4[r5] = r6;	 Catch:{ IOException -> 0x0035 }
        r5 = 1;
        r6 = r0.getStartOffset();	 Catch:{ IOException -> 0x0035 }
        r4[r5] = r6;	 Catch:{ IOException -> 0x0035 }
        r5 = 2;
        r6 = r0.getLength();	 Catch:{ IOException -> 0x0035 }
        r4[r5] = r6;	 Catch:{ IOException -> 0x0035 }
        if (r0 == 0) goto L_0x002b;
    L_0x0028:
        r0.close();	 Catch:{ IOException -> 0x002c }
    L_0x002b:
        return r4;
    L_0x002c:
        r2 = move-exception;
        r5 = "ApkAssets";
        r6 = "Unable to close AssetFileDescriptor";
        android.util.Log.e(r5, r6, r2);
        goto L_0x002b;
    L_0x0035:
        r1 = move-exception;
        r4 = "ApkAssets";
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x006d }
        r5.<init>();	 Catch:{ all -> 0x006d }
        r6 = "Error while loading asset ";
        r5 = r5.append(r6);	 Catch:{ all -> 0x006d }
        r5 = r5.append(r9);	 Catch:{ all -> 0x006d }
        r6 = ": ";
        r5 = r5.append(r6);	 Catch:{ all -> 0x006d }
        r5 = r5.append(r1);	 Catch:{ all -> 0x006d }
        r5 = r5.toString();	 Catch:{ all -> 0x006d }
        android.util.Log.e(r4, r5);	 Catch:{ all -> 0x006d }
        r4 = 3;
        r4 = new long[r4];	 Catch:{ all -> 0x006d }
        r4 = {-1, -1, -1};
        if (r0 == 0) goto L_0x002b;
    L_0x0060:
        r0.close();	 Catch:{ IOException -> 0x0064 }
        goto L_0x002b;
    L_0x0064:
        r2 = move-exception;
        r5 = "ApkAssets";
        r6 = "Unable to close AssetFileDescriptor";
        android.util.Log.e(r5, r6, r2);
        goto L_0x002b;
    L_0x006d:
        r4 = move-exception;
        if (r0 == 0) goto L_0x0073;
    L_0x0070:
        r0.close();	 Catch:{ IOException -> 0x0074 }
    L_0x0073:
        throw r4;
    L_0x0074:
        r2 = move-exception;
        r5 = "ApkAssets";
        r6 = "Unable to close AssetFileDescriptor";
        android.util.Log.e(r5, r6, r2);
        goto L_0x0073;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.base.ApkAssets.open(android.content.Context, java.lang.String):long[]");
    }
}
