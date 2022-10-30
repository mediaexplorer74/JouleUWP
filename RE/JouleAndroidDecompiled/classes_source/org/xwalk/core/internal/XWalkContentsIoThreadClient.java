package org.xwalk.core.internal;

import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

@JNINamespace("xwalk")
interface XWalkContentsIoThreadClient {
    @CalledByNative
    int getCacheMode();

    @CalledByNative
    void newLoginRequest(String str, String str2, String str3);

    @CalledByNative
    void onDownloadStart(String str, String str2, String str3, String str4, long j);

    @CalledByNative
    boolean shouldBlockContentUrls();

    @CalledByNative
    boolean shouldBlockFileUrls();

    @CalledByNative
    boolean shouldBlockNetworkLoads();

    @CalledByNative
    InterceptedRequestData shouldInterceptRequest(String str, boolean z);
}
