package org.xwalk.core.internal;

public class XWalkHttpAuthHandlerBridge extends XWalkHttpAuthHandlerInternal {
    private ReflectMethod cancelMethod;
    private XWalkCoreBridge coreBridge;
    private ReflectMethod proceedStringStringMethod;
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    public XWalkHttpAuthHandlerBridge(long nativeXWalkHttpAuthHandler, boolean firstAttempt, Object wrapper) {
        super(nativeXWalkHttpAuthHandler, firstAttempt);
        this.proceedStringStringMethod = new ReflectMethod(null, "proceed", new Class[0]);
        this.cancelMethod = new ReflectMethod(null, "cancel", new Class[0]);
        this.wrapper = wrapper;
        reflectionInit();
    }

    public void proceed(String username, String password) {
        if (this.proceedStringStringMethod.isNull()) {
            proceedSuper(username, password);
            return;
        }
        this.proceedStringStringMethod.invoke(username, password);
    }

    public void proceedSuper(String username, String password) {
        super.proceed(username, password);
    }

    public void cancel() {
        if (this.cancelMethod.isNull()) {
            cancelSuper();
        } else {
            this.cancelMethod.invoke(new Object[0]);
        }
    }

    public void cancelSuper() {
        super.cancel();
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            this.proceedStringStringMethod.init(this.wrapper, null, "proceed", String.class, String.class);
            this.cancelMethod.init(this.wrapper, null, "cancel", new Class[0]);
        }
    }
}
