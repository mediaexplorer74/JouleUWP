package org.xwalk.core.internal;

import org.chromium.base.JNINamespace;

@JNINamespace("xwalk")
@XWalkAPI(createExternally = true)
public class XWalkCookieManagerInternal {
    private native boolean nativeAcceptCookie();

    private native boolean nativeAllowFileSchemeCookies();

    private native void nativeFlushCookieStore();

    private native String nativeGetCookie(String str);

    private native boolean nativeHasCookies();

    private native void nativeRemoveAllCookie();

    private native void nativeRemoveExpiredCookie();

    private native void nativeRemoveSessionCookie();

    private native void nativeSetAcceptCookie(boolean z);

    private native void nativeSetAcceptFileSchemeCookies(boolean z);

    private native void nativeSetCookie(String str, String str2);

    @XWalkAPI
    public void setAcceptCookie(boolean accept) {
        nativeSetAcceptCookie(accept);
    }

    @XWalkAPI
    public boolean acceptCookie() {
        return nativeAcceptCookie();
    }

    @XWalkAPI
    public void setCookie(String url, String value) {
        nativeSetCookie(url, value);
    }

    @XWalkAPI
    public String getCookie(String url) {
        String cookie = nativeGetCookie(url.toString());
        return (cookie == null || cookie.trim().isEmpty()) ? null : cookie;
    }

    @XWalkAPI
    public void removeSessionCookie() {
        nativeRemoveSessionCookie();
    }

    @XWalkAPI
    public void removeAllCookie() {
        nativeRemoveAllCookie();
    }

    @XWalkAPI
    public boolean hasCookies() {
        return nativeHasCookies();
    }

    @XWalkAPI
    public void removeExpiredCookie() {
        nativeRemoveExpiredCookie();
    }

    @XWalkAPI
    public void flushCookieStore() {
        nativeFlushCookieStore();
    }

    @XWalkAPI
    public boolean allowFileSchemeCookies() {
        return nativeAllowFileSchemeCookies();
    }

    @XWalkAPI
    public void setAcceptFileSchemeCookies(boolean accept) {
        nativeSetAcceptFileSchemeCookies(accept);
    }
}
