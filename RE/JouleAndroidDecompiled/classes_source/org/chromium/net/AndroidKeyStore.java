package org.chromium.net;

import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

@JNINamespace("net::android")
public interface AndroidKeyStore {
    @CalledByNative
    byte[] getECKeyOrder(AndroidPrivateKey androidPrivateKey);

    @CalledByNative
    Object getOpenSSLEngineForPrivateKey(AndroidPrivateKey androidPrivateKey);

    @CalledByNative
    long getOpenSSLHandleForPrivateKey(AndroidPrivateKey androidPrivateKey);

    @CalledByNative
    int getPrivateKeyType(AndroidPrivateKey androidPrivateKey);

    @CalledByNative
    byte[] getRSAKeyModulus(AndroidPrivateKey androidPrivateKey);

    @CalledByNative
    byte[] rawSignDigestWithPrivateKey(AndroidPrivateKey androidPrivateKey, byte[] bArr);

    @CalledByNative
    void releaseKey(AndroidPrivateKey androidPrivateKey);
}
