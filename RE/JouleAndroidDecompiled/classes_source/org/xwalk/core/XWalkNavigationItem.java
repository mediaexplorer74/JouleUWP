package org.xwalk.core;

import java.util.ArrayList;

public class XWalkNavigationItem {
    private Object bridge;
    private ArrayList<Object> constructorParams;
    private ArrayList<Object> constructorTypes;
    private XWalkCoreWrapper coreWrapper;
    private ReflectMethod getOriginalUrlMethod;
    private ReflectMethod getTitleMethod;
    private ReflectMethod getUrlMethod;
    private ReflectMethod postWrapperMethod;

    Object getBridge() {
        return this.bridge;
    }

    public XWalkNavigationItem(Object bridge) {
        this.getUrlMethod = new ReflectMethod(null, "getUrl", new Class[0]);
        this.getOriginalUrlMethod = new ReflectMethod(null, "getOriginalUrl", new Class[0]);
        this.getTitleMethod = new ReflectMethod(null, "getTitle", new Class[0]);
        this.bridge = bridge;
        reflectionInit();
    }

    public String getUrl() {
        return (String) this.getUrlMethod.invoke(new Object[0]);
    }

    public String getOriginalUrl() {
        return (String) this.getOriginalUrlMethod.invoke(new Object[0]);
    }

    public String getTitle() {
        return (String) this.getTitleMethod.invoke(new Object[0]);
    }

    void reflectionInit() {
        XWalkCoreWrapper.initEmbeddedMode();
        this.coreWrapper = XWalkCoreWrapper.getInstance();
        if (this.coreWrapper == null) {
            XWalkCoreWrapper.reserveReflectObject(this);
            return;
        }
        this.getUrlMethod.init(this.bridge, null, "getUrlSuper", new Class[0]);
        this.getOriginalUrlMethod.init(this.bridge, null, "getOriginalUrlSuper", new Class[0]);
        this.getTitleMethod.init(this.bridge, null, "getTitleSuper", new Class[0]);
    }
}
