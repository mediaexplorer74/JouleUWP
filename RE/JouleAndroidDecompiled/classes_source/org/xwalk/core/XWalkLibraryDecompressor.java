package org.xwalk.core;

import SevenZip.Compression.LZMA.Decoder;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.Log;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class XWalkLibraryDecompressor {
    private static final String[] MANDATORY_LIBRARIES;
    private static final String TAG = "XWalkLib";

    XWalkLibraryDecompressor() {
    }

    static {
        MANDATORY_LIBRARIES = new String[]{"libxwalkcore.so"};
    }

    public static boolean isCompressed(Context context) {
        for (String library : MANDATORY_LIBRARIES) {
            try {
                openRawResource(context, library).close();
            } catch (IOException e) {
                try {
                    Log.e(TAG, "Closing " + library + "has failed: " + e.getMessage());
                } catch (NotFoundException e2) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isDecompressed(Context context) {
        int version = getLocalVersion(context);
        return version > 0 && version == 5;
    }

    public static boolean decompressLibrary(Context context) {
        String libDir = context.getDir(XWalkLibraryInterface.PRIVATE_DATA_DIRECTORY_SUFFIX, 0).toString();
        long start = System.currentTimeMillis();
        boolean success = decompress(context, libDir);
        Log.d(TAG, "Decompress library cost: " + (System.currentTimeMillis() - start) + " milliseconds.");
        if (success) {
            setLocalVersion(context, 5);
        }
        return success;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean decompress(android.content.Context r17, java.lang.String r18) {
        /*
        r3 = new java.io.File;
        r0 = r18;
        r3.<init>(r0);
        r14 = r3.exists();
        if (r14 == 0) goto L_0x0016;
    L_0x000d:
        r14 = r3.isFile();
        if (r14 == 0) goto L_0x0016;
    L_0x0013:
        r3.delete();
    L_0x0016:
        r14 = r3.exists();
        if (r14 != 0) goto L_0x0024;
    L_0x001c:
        r14 = r3.mkdirs();
        if (r14 != 0) goto L_0x0024;
    L_0x0022:
        r14 = 0;
    L_0x0023:
        return r14;
    L_0x0024:
        r1 = MANDATORY_LIBRARIES;
        r7 = r1.length;
        r4 = 0;
    L_0x0028:
        if (r4 >= r7) goto L_0x00f0;
    L_0x002a:
        r8 = r1[r4];
        r12 = 0;
        r5 = 0;
        r10 = 0;
        r9 = new java.io.File;	 Catch:{ NotFoundException -> 0x007e, Exception -> 0x00ae }
        r0 = r18;
        r9.<init>(r0, r8);	 Catch:{ NotFoundException -> 0x007e, Exception -> 0x00ae }
        r13 = new java.io.File;	 Catch:{ NotFoundException -> 0x007e, Exception -> 0x00ae }
        r14 = new java.lang.StringBuilder;	 Catch:{ NotFoundException -> 0x007e, Exception -> 0x00ae }
        r14.<init>();	 Catch:{ NotFoundException -> 0x007e, Exception -> 0x00ae }
        r14 = r14.append(r8);	 Catch:{ NotFoundException -> 0x007e, Exception -> 0x00ae }
        r15 = ".tmp";
        r14 = r14.append(r15);	 Catch:{ NotFoundException -> 0x007e, Exception -> 0x00ae }
        r14 = r14.toString();	 Catch:{ NotFoundException -> 0x007e, Exception -> 0x00ae }
        r0 = r18;
        r13.<init>(r0, r14);	 Catch:{ NotFoundException -> 0x007e, Exception -> 0x00ae }
        r6 = new java.io.BufferedInputStream;	 Catch:{ NotFoundException -> 0x0126, Exception -> 0x011a, all -> 0x010e }
        r0 = r17;
        r14 = openRawResource(r0, r8);	 Catch:{ NotFoundException -> 0x0126, Exception -> 0x011a, all -> 0x010e }
        r6.<init>(r14);	 Catch:{ NotFoundException -> 0x0126, Exception -> 0x011a, all -> 0x010e }
        r11 = new java.io.BufferedOutputStream;	 Catch:{ NotFoundException -> 0x012a, Exception -> 0x011d, all -> 0x0111 }
        r14 = new java.io.FileOutputStream;	 Catch:{ NotFoundException -> 0x012a, Exception -> 0x011d, all -> 0x0111 }
        r14.<init>(r13);	 Catch:{ NotFoundException -> 0x012a, Exception -> 0x011d, all -> 0x0111 }
        r11.<init>(r14);	 Catch:{ NotFoundException -> 0x012a, Exception -> 0x011d, all -> 0x0111 }
        decodeWithLzma(r6, r11);	 Catch:{ NotFoundException -> 0x012f, Exception -> 0x0121, all -> 0x0115 }
        r13.renameTo(r9);	 Catch:{ NotFoundException -> 0x012f, Exception -> 0x0121, all -> 0x0115 }
        if (r11 == 0) goto L_0x0073;
    L_0x006d:
        r11.flush();	 Catch:{ IOException -> 0x00f3 }
    L_0x0070:
        r11.close();	 Catch:{ IOException -> 0x00f6 }
    L_0x0073:
        if (r6 == 0) goto L_0x0078;
    L_0x0075:
        r6.close();	 Catch:{ IOException -> 0x00f9 }
    L_0x0078:
        r13.delete();
        r4 = r4 + 1;
        goto L_0x0028;
    L_0x007e:
        r2 = move-exception;
    L_0x007f:
        r14 = "XWalkLib";
        r15 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00de }
        r15.<init>();	 Catch:{ all -> 0x00de }
        r16 = "Could not find resource: ";
        r15 = r15.append(r16);	 Catch:{ all -> 0x00de }
        r16 = r2.getMessage();	 Catch:{ all -> 0x00de }
        r15 = r15.append(r16);	 Catch:{ all -> 0x00de }
        r15 = r15.toString();	 Catch:{ all -> 0x00de }
        android.util.Log.d(r14, r15);	 Catch:{ all -> 0x00de }
        r14 = 0;
        if (r10 == 0) goto L_0x00a4;
    L_0x009e:
        r10.flush();	 Catch:{ IOException -> 0x00fc }
    L_0x00a1:
        r10.close();	 Catch:{ IOException -> 0x00fe }
    L_0x00a4:
        if (r5 == 0) goto L_0x00a9;
    L_0x00a6:
        r5.close();	 Catch:{ IOException -> 0x0100 }
    L_0x00a9:
        r12.delete();
        goto L_0x0023;
    L_0x00ae:
        r2 = move-exception;
    L_0x00af:
        r14 = "XWalkLib";
        r15 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00de }
        r15.<init>();	 Catch:{ all -> 0x00de }
        r16 = "Decompress failed: ";
        r15 = r15.append(r16);	 Catch:{ all -> 0x00de }
        r16 = r2.getMessage();	 Catch:{ all -> 0x00de }
        r15 = r15.append(r16);	 Catch:{ all -> 0x00de }
        r15 = r15.toString();	 Catch:{ all -> 0x00de }
        android.util.Log.d(r14, r15);	 Catch:{ all -> 0x00de }
        r14 = 0;
        if (r10 == 0) goto L_0x00d4;
    L_0x00ce:
        r10.flush();	 Catch:{ IOException -> 0x0102 }
    L_0x00d1:
        r10.close();	 Catch:{ IOException -> 0x0104 }
    L_0x00d4:
        if (r5 == 0) goto L_0x00d9;
    L_0x00d6:
        r5.close();	 Catch:{ IOException -> 0x0106 }
    L_0x00d9:
        r12.delete();
        goto L_0x0023;
    L_0x00de:
        r14 = move-exception;
    L_0x00df:
        if (r10 == 0) goto L_0x00e7;
    L_0x00e1:
        r10.flush();	 Catch:{ IOException -> 0x0108 }
    L_0x00e4:
        r10.close();	 Catch:{ IOException -> 0x010a }
    L_0x00e7:
        if (r5 == 0) goto L_0x00ec;
    L_0x00e9:
        r5.close();	 Catch:{ IOException -> 0x010c }
    L_0x00ec:
        r12.delete();
        throw r14;
    L_0x00f0:
        r14 = 1;
        goto L_0x0023;
    L_0x00f3:
        r14 = move-exception;
        goto L_0x0070;
    L_0x00f6:
        r14 = move-exception;
        goto L_0x0073;
    L_0x00f9:
        r14 = move-exception;
        goto L_0x0078;
    L_0x00fc:
        r15 = move-exception;
        goto L_0x00a1;
    L_0x00fe:
        r15 = move-exception;
        goto L_0x00a4;
    L_0x0100:
        r15 = move-exception;
        goto L_0x00a9;
    L_0x0102:
        r15 = move-exception;
        goto L_0x00d1;
    L_0x0104:
        r15 = move-exception;
        goto L_0x00d4;
    L_0x0106:
        r15 = move-exception;
        goto L_0x00d9;
    L_0x0108:
        r15 = move-exception;
        goto L_0x00e4;
    L_0x010a:
        r15 = move-exception;
        goto L_0x00e7;
    L_0x010c:
        r15 = move-exception;
        goto L_0x00ec;
    L_0x010e:
        r14 = move-exception;
        r12 = r13;
        goto L_0x00df;
    L_0x0111:
        r14 = move-exception;
        r5 = r6;
        r12 = r13;
        goto L_0x00df;
    L_0x0115:
        r14 = move-exception;
        r10 = r11;
        r5 = r6;
        r12 = r13;
        goto L_0x00df;
    L_0x011a:
        r2 = move-exception;
        r12 = r13;
        goto L_0x00af;
    L_0x011d:
        r2 = move-exception;
        r5 = r6;
        r12 = r13;
        goto L_0x00af;
    L_0x0121:
        r2 = move-exception;
        r10 = r11;
        r5 = r6;
        r12 = r13;
        goto L_0x00af;
    L_0x0126:
        r2 = move-exception;
        r12 = r13;
        goto L_0x007f;
    L_0x012a:
        r2 = move-exception;
        r5 = r6;
        r12 = r13;
        goto L_0x007f;
    L_0x012f:
        r2 = move-exception;
        r10 = r11;
        r5 = r6;
        r12 = r13;
        goto L_0x007f;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xwalk.core.XWalkLibraryDecompressor.decompress(android.content.Context, java.lang.String):boolean");
    }

    private static void decodeWithLzma(InputStream input, OutputStream output) throws IOException {
        byte[] properties = new byte[5];
        if (input.read(properties, 0, 5) != 5) {
            throw new EOFException("Input .lzma file is too short");
        }
        Decoder decoder = new Decoder();
        if (!decoder.SetDecoderProperties(properties)) {
            Log.w(TAG, "Incorrect stream properties");
        }
        long outSize = 0;
        for (int i = 0; i < 8; i++) {
            int v = input.read();
            if (v < 0) {
                Log.w(TAG, "Can't read stream size");
            }
            outSize |= ((long) v) << (i * 8);
        }
        if (!decoder.Code(input, output, outSize)) {
            Log.w(TAG, "Error in data stream");
        }
    }

    private static InputStream openRawResource(Context context, String library) throws NotFoundException {
        Resources res = context.getResources();
        return res.openRawResource(res.getIdentifier(library.split("\\.")[0], "raw", context.getPackageName()));
    }

    private static int getLocalVersion(Context context) {
        return context.getSharedPreferences("libxwalkcore", 0).getInt("version", 0);
    }

    private static void setLocalVersion(Context context, int version) {
        context.getSharedPreferences("libxwalkcore", 0).edit().putInt("version", version).apply();
    }
}
