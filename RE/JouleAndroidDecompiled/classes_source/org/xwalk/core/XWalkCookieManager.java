package org.xwalk.core;

import java.util.ArrayList;

public class XWalkCookieManager {
    static final /* synthetic */ boolean $assertionsDisabled;
    private ReflectMethod acceptCookieMethod;
    private ReflectMethod allowFileSchemeCookiesMethod;
    private Object bridge;
    private ArrayList<Object> constructorParams;
    private ArrayList<Object> constructorTypes;
    private XWalkCoreWrapper coreWrapper;
    private ReflectMethod flushCookieStoreMethod;
    private ReflectMethod getCookieStringMethod;
    private ReflectMethod hasCookiesMethod;
    private ReflectMethod postWrapperMethod;
    private ReflectMethod removeAllCookieMethod;
    private ReflectMethod removeExpiredCookieMethod;
    private ReflectMethod removeSessionCookieMethod;
    private ReflectMethod setAcceptCookiebooleanMethod;
    private ReflectMethod setAcceptFileSchemeCookiesbooleanMethod;
    private ReflectMethod setCookieStringStringMethod;

    static {
        $assertionsDisabled = !XWalkCookieManager.class.desiredAssertionStatus();
    }

    Object getBridge() {
        return this.bridge;
    }

    public XWalkCookieManager() {
        this.setAcceptCookiebooleanMethod = new ReflectMethod(null, "setAcceptCookie", new Class[0]);
        this.acceptCookieMethod = new ReflectMethod(null, "acceptCookie", new Class[0]);
        this.setCookieStringStringMethod = new ReflectMethod(null, "setCookie", new Class[0]);
        this.getCookieStringMethod = new ReflectMethod(null, "getCookie", new Class[0]);
        this.removeSessionCookieMethod = new ReflectMethod(null, "removeSessionCookie", new Class[0]);
        this.removeAllCookieMethod = new ReflectMethod(null, "removeAllCookie", new Class[0]);
        this.hasCookiesMethod = new ReflectMethod(null, "hasCookies", new Class[0]);
        this.removeExpiredCookieMethod = new ReflectMethod(null, "removeExpiredCookie", new Class[0]);
        this.flushCookieStoreMethod = new ReflectMethod(null, "flushCookieStore", new Class[0]);
        this.allowFileSchemeCookiesMethod = new ReflectMethod(null, "allowFileSchemeCookies", new Class[0]);
        this.setAcceptFileSchemeCookiesbooleanMethod = new ReflectMethod(null, "setAcceptFileSchemeCookies", new Class[0]);
        this.constructorTypes = new ArrayList();
        this.constructorParams = new ArrayList();
        reflectionInit();
    }

    public void setAcceptCookie(boolean accept) {
        this.setAcceptCookiebooleanMethod.invoke(Boolean.valueOf(accept));
    }

    public boolean acceptCookie() {
        return ((Boolean) this.acceptCookieMethod.invoke(new Object[0])).booleanValue();
    }

    public void setCookie(String url, String value) {
        this.setCookieStringStringMethod.invoke(url, value);
    }

    public String getCookie(String url) {
        return (String) this.getCookieStringMethod.invoke(url);
    }

    public void removeSessionCookie() {
        this.removeSessionCookieMethod.invoke(new Object[0]);
    }

    public void removeAllCookie() {
        this.removeAllCookieMethod.invoke(new Object[0]);
    }

    public boolean hasCookies() {
        return ((Boolean) this.hasCookiesMethod.invoke(new Object[0])).booleanValue();
    }

    public void removeExpiredCookie() {
        this.removeExpiredCookieMethod.invoke(new Object[0]);
    }

    public void flushCookieStore() {
        this.flushCookieStoreMethod.invoke(new Object[0]);
    }

    public boolean allowFileSchemeCookies() {
        return ((Boolean) this.allowFileSchemeCookiesMethod.invoke(new Object[0])).booleanValue();
    }

    public void setAcceptFileSchemeCookies(boolean accept) {
        this.setAcceptFileSchemeCookiesbooleanMethod.invoke(Boolean.valueOf(accept));
    }

    void reflectionInit() {
        XWalkCoreWrapper.initEmbeddedMode();
        this.coreWrapper = XWalkCoreWrapper.getInstance();
        if (this.coreWrapper == null) {
            XWalkCoreWrapper.reserveReflectObject(this);
            return;
        }
        int length = this.constructorTypes.size();
        Class<?>[] paramTypes = new Class[(length + 1)];
        for (int i = 0; i < length; i++) {
            Object type = this.constructorTypes.get(i);
            if (type instanceof String) {
                paramTypes[i] = this.coreWrapper.getBridgeClass((String) type);
                this.constructorParams.set(i, this.coreWrapper.getBridgeObject(this.constructorParams.get(i)));
            } else if (type instanceof Class) {
                paramTypes[i] = (Class) type;
            } else if (!$assertionsDisabled) {
                throw new AssertionError();
            }
        }
        paramTypes[length] = Object.class;
        this.constructorParams.add(this);
        this.bridge = new ReflectConstructor(this.coreWrapper.getBridgeClass("XWalkCookieManagerBridge"), paramTypes).newInstance(this.constructorParams.toArray());
        if (this.postWrapperMethod != null) {
            this.postWrapperMethod.invoke(new Object[0]);
        }
        this.setAcceptCookiebooleanMethod.init(this.bridge, null, "setAcceptCookieSuper", Boolean.TYPE);
        this.acceptCookieMethod.init(this.bridge, null, "acceptCookieSuper", new Class[0]);
        this.setCookieStringStringMethod.init(this.bridge, null, "setCookieSuper", String.class, String.class);
        this.getCookieStringMethod.init(this.bridge, null, "getCookieSuper", String.class);
        this.removeSessionCookieMethod.init(this.bridge, null, "removeSessionCookieSuper", new Class[0]);
        this.removeAllCookieMethod.init(this.bridge, null, "removeAllCookieSuper", new Class[0]);
        this.hasCookiesMethod.init(this.bridge, null, "hasCookiesSuper", new Class[0]);
        this.removeExpiredCookieMethod.init(this.bridge, null, "removeExpiredCookieSuper", new Class[0]);
        this.flushCookieStoreMethod.init(this.bridge, null, "flushCookieStoreSuper", new Class[0]);
        this.allowFileSchemeCookiesMethod.init(this.bridge, null, "allowFileSchemeCookiesSuper", new Class[0]);
        this.setAcceptFileSchemeCookiesbooleanMethod.init(this.bridge, null, "setAcceptFileSchemeCookiesSuper", Boolean.TYPE);
    }
}
