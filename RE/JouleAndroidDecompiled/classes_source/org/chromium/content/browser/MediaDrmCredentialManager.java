package org.chromium.content.browser;

import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

@JNINamespace("content")
public class MediaDrmCredentialManager {

    public interface MediaDrmCredentialManagerCallback {
        @CalledByNative("MediaDrmCredentialManagerCallback")
        void onCredentialResetFinished(boolean z);
    }

    private static native void nativeResetCredentials(MediaDrmCredentialManagerCallback mediaDrmCredentialManagerCallback);

    public static void resetCredentials(MediaDrmCredentialManagerCallback callback) {
        nativeResetCredentials(callback);
    }
}
