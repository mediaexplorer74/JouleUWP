package org.xwalk.core;

import java.util.ArrayList;

public class XWalkJavascriptResultHandler implements XWalkJavascriptResult {
    private Object bridge;
    private ReflectMethod cancelMethod;
    private ReflectMethod confirmMethod;
    private ReflectMethod confirmWithResultStringMethod;
    private ArrayList<Object> constructorParams;
    private ArrayList<Object> constructorTypes;
    private XWalkCoreWrapper coreWrapper;
    private ReflectMethod postWrapperMethod;

    Object getBridge() {
        return this.bridge;
    }

    public XWalkJavascriptResultHandler(Object bridge) {
        this.confirmMethod = new ReflectMethod(null, "confirm", new Class[0]);
        this.confirmWithResultStringMethod = new ReflectMethod(null, "confirmWithResult", new Class[0]);
        this.cancelMethod = new ReflectMethod(null, "cancel", new Class[0]);
        this.bridge = bridge;
        reflectionInit();
    }

    public void confirm() {
        this.confirmMethod.invoke(new Object[0]);
    }

    public void confirmWithResult(String promptResult) {
        this.confirmWithResultStringMethod.invoke(promptResult);
    }

    public void cancel() {
        this.cancelMethod.invoke(new Object[0]);
    }

    void reflectionInit() {
        XWalkCoreWrapper.initEmbeddedMode();
        this.coreWrapper = XWalkCoreWrapper.getInstance();
        if (this.coreWrapper == null) {
            XWalkCoreWrapper.reserveReflectObject(this);
            return;
        }
        this.confirmMethod.init(this.bridge, null, "confirmSuper", new Class[0]);
        this.confirmWithResultStringMethod.init(this.bridge, null, "confirmWithResultSuper", String.class);
        this.cancelMethod.init(this.bridge, null, "cancelSuper", new Class[0]);
    }
}
