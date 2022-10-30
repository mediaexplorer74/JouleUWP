package org.xwalk.core.internal;

import java.io.InputStream;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

@JNINamespace("xwalk")
class InterceptedRequestData {
    private String mCharset;
    private InputStream mData;
    private String mMimeType;

    public InterceptedRequestData(String mimeType, String encoding, InputStream data) {
        this.mMimeType = mimeType;
        this.mCharset = encoding;
        this.mData = data;
    }

    @CalledByNative
    public String getMimeType() {
        return this.mMimeType;
    }

    @CalledByNative
    public String getCharset() {
        return this.mCharset;
    }

    @CalledByNative
    public InputStream getData() {
        return this.mData;
    }
}
