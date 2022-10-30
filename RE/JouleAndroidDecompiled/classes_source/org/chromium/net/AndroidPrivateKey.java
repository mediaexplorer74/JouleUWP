package org.chromium.net;

import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

@JNINamespace("net::android")
public interface AndroidPrivateKey {
    @CalledByNative
    AndroidKeyStore getKeyStore();
}
