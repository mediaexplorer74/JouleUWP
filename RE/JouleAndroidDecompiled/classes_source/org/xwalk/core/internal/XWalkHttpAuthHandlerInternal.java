package org.xwalk.core.internal;

import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

@JNINamespace("xwalk")
@XWalkAPI(createExternally = true)
public class XWalkHttpAuthHandlerInternal {
    private final boolean mFirstAttempt;
    private long mNativeXWalkHttpAuthHandler;

    private native void nativeCancel(long j);

    private native void nativeProceed(long j, String str, String str2);

    @XWalkAPI
    public void proceed(String username, String password) {
        if (this.mNativeXWalkHttpAuthHandler != 0) {
            nativeProceed(this.mNativeXWalkHttpAuthHandler, username, password);
            this.mNativeXWalkHttpAuthHandler = 0;
        }
    }

    @XWalkAPI
    public void cancel() {
        if (this.mNativeXWalkHttpAuthHandler != 0) {
            nativeCancel(this.mNativeXWalkHttpAuthHandler);
            this.mNativeXWalkHttpAuthHandler = 0;
        }
    }

    public boolean isFirstAttempt() {
        return this.mFirstAttempt;
    }

    @CalledByNative
    public static XWalkHttpAuthHandlerInternal create(long nativeXWalkAuthHandler, boolean firstAttempt) {
        return new XWalkHttpAuthHandlerInternal(nativeXWalkAuthHandler, firstAttempt);
    }

    @XWalkAPI
    public XWalkHttpAuthHandlerInternal(long nativeXWalkHttpAuthHandler, boolean firstAttempt) {
        this.mNativeXWalkHttpAuthHandler = nativeXWalkHttpAuthHandler;
        this.mFirstAttempt = firstAttempt;
    }

    @CalledByNative
    void handlerDestroyed() {
        this.mNativeXWalkHttpAuthHandler = 0;
    }
}
