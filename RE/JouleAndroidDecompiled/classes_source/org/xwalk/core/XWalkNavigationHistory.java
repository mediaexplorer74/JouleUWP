package org.xwalk.core;

import java.util.ArrayList;

public class XWalkNavigationHistory {
    private Object bridge;
    private ReflectMethod canGoBackMethod;
    private ReflectMethod canGoForwardMethod;
    private ReflectMethod clearMethod;
    private ArrayList<Object> constructorParams;
    private ArrayList<Object> constructorTypes;
    private XWalkCoreWrapper coreWrapper;
    private ReflectMethod enumDirectionClassValueOfMethod;
    private ReflectMethod getCurrentIndexMethod;
    private ReflectMethod getCurrentItemMethod;
    private ReflectMethod getItemAtintMethod;
    private ReflectMethod hasItemAtintMethod;
    private ReflectMethod navigateDirectionInternalintMethod;
    private ReflectMethod postWrapperMethod;
    private ReflectMethod sizeMethod;

    public enum Direction {
        BACKWARD,
        FORWARD
    }

    private Object ConvertDirection(Direction type) {
        return this.enumDirectionClassValueOfMethod.invoke(type.toString());
    }

    Object getBridge() {
        return this.bridge;
    }

    public XWalkNavigationHistory(Object bridge) {
        this.enumDirectionClassValueOfMethod = new ReflectMethod();
        this.sizeMethod = new ReflectMethod(null, "size", new Class[0]);
        this.hasItemAtintMethod = new ReflectMethod(null, "hasItemAt", new Class[0]);
        this.getItemAtintMethod = new ReflectMethod(null, "getItemAt", new Class[0]);
        this.getCurrentItemMethod = new ReflectMethod(null, "getCurrentItem", new Class[0]);
        this.canGoBackMethod = new ReflectMethod(null, "canGoBack", new Class[0]);
        this.canGoForwardMethod = new ReflectMethod(null, "canGoForward", new Class[0]);
        this.navigateDirectionInternalintMethod = new ReflectMethod(null, "navigate", new Class[0]);
        this.getCurrentIndexMethod = new ReflectMethod(null, "getCurrentIndex", new Class[0]);
        this.clearMethod = new ReflectMethod(null, "clear", new Class[0]);
        this.bridge = bridge;
        reflectionInit();
    }

    public int size() {
        return ((Integer) this.sizeMethod.invoke(new Object[0])).intValue();
    }

    public boolean hasItemAt(int index) {
        return ((Boolean) this.hasItemAtintMethod.invoke(Integer.valueOf(index))).booleanValue();
    }

    public XWalkNavigationItem getItemAt(int index) {
        return (XWalkNavigationItem) this.coreWrapper.getWrapperObject(this.getItemAtintMethod.invoke(Integer.valueOf(index)));
    }

    public XWalkNavigationItem getCurrentItem() {
        return (XWalkNavigationItem) this.coreWrapper.getWrapperObject(this.getCurrentItemMethod.invoke(new Object[0]));
    }

    public boolean canGoBack() {
        return ((Boolean) this.canGoBackMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean canGoForward() {
        return ((Boolean) this.canGoForwardMethod.invoke(new Object[0])).booleanValue();
    }

    public void navigate(Direction direction, int steps) {
        this.navigateDirectionInternalintMethod.invoke(ConvertDirection(direction), Integer.valueOf(steps));
    }

    public int getCurrentIndex() {
        return ((Integer) this.getCurrentIndexMethod.invoke(new Object[0])).intValue();
    }

    public void clear() {
        this.clearMethod.invoke(new Object[0]);
    }

    void reflectionInit() {
        XWalkCoreWrapper.initEmbeddedMode();
        this.coreWrapper = XWalkCoreWrapper.getInstance();
        if (this.coreWrapper == null) {
            XWalkCoreWrapper.reserveReflectObject(this);
            return;
        }
        this.enumDirectionClassValueOfMethod.init(null, this.coreWrapper.getBridgeClass("XWalkNavigationHistoryInternal$DirectionInternal"), "valueOf", String.class);
        this.sizeMethod.init(this.bridge, null, "sizeSuper", new Class[0]);
        this.hasItemAtintMethod.init(this.bridge, null, "hasItemAtSuper", Integer.TYPE);
        this.getItemAtintMethod.init(this.bridge, null, "getItemAtSuper", Integer.TYPE);
        this.getCurrentItemMethod.init(this.bridge, null, "getCurrentItemSuper", new Class[0]);
        this.canGoBackMethod.init(this.bridge, null, "canGoBackSuper", new Class[0]);
        this.canGoForwardMethod.init(this.bridge, null, "canGoForwardSuper", new Class[0]);
        this.navigateDirectionInternalintMethod.init(this.bridge, null, "navigateSuper", this.coreWrapper.getBridgeClass("XWalkNavigationHistoryInternal$DirectionInternal"), Integer.TYPE);
        this.getCurrentIndexMethod.init(this.bridge, null, "getCurrentIndexSuper", new Class[0]);
        this.clearMethod.init(this.bridge, null, "clearSuper", new Class[0]);
    }
}
