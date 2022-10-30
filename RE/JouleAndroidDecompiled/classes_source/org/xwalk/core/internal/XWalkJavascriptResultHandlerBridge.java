package org.xwalk.core.internal;

public class XWalkJavascriptResultHandlerBridge extends XWalkJavascriptResultHandlerInternal {
    private ReflectMethod cancelMethod;
    private ReflectMethod confirmMethod;
    private ReflectMethod confirmWithResultStringMethod;
    private XWalkCoreBridge coreBridge;
    private XWalkJavascriptResultHandlerInternal internal;
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    XWalkJavascriptResultHandlerBridge(XWalkJavascriptResultHandlerInternal internal) {
        this.confirmMethod = new ReflectMethod(null, "confirm", new Class[0]);
        this.confirmWithResultStringMethod = new ReflectMethod(null, "confirmWithResult", new Class[0]);
        this.cancelMethod = new ReflectMethod(null, "cancel", new Class[0]);
        this.internal = internal;
        reflectionInit();
    }

    public void confirm() {
        if (this.confirmMethod.isNull()) {
            confirmSuper();
        } else {
            this.confirmMethod.invoke(new Object[0]);
        }
    }

    public void confirmSuper() {
        if (this.internal == null) {
            super.confirm();
        } else {
            this.internal.confirm();
        }
    }

    public void confirmWithResult(String promptResult) {
        if (this.confirmWithResultStringMethod.isNull()) {
            confirmWithResultSuper(promptResult);
            return;
        }
        this.confirmWithResultStringMethod.invoke(promptResult);
    }

    public void confirmWithResultSuper(String promptResult) {
        if (this.internal == null) {
            super.confirmWithResult(promptResult);
        } else {
            this.internal.confirmWithResult(promptResult);
        }
    }

    public void cancel() {
        if (this.cancelMethod.isNull()) {
            cancelSuper();
        } else {
            this.cancelMethod.invoke(new Object[0]);
        }
    }

    public void cancelSuper() {
        if (this.internal == null) {
            super.cancel();
        } else {
            this.internal.cancel();
        }
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            ReflectConstructor constructor = new ReflectConstructor(this.coreBridge.getWrapperClass("XWalkJavascriptResultHandler"), Object.class);
            if (!constructor.isNull()) {
                this.wrapper = constructor.newInstance(this);
                this.confirmMethod.init(this.wrapper, null, "confirm", new Class[0]);
                this.confirmWithResultStringMethod.init(this.wrapper, null, "confirmWithResult", String.class);
                this.cancelMethod.init(this.wrapper, null, "cancel", new Class[0]);
            }
        }
    }
}
