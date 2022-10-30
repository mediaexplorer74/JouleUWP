package org.xwalk.core.internal;

import org.xwalk.core.internal.XWalkNavigationHistoryInternal.DirectionInternal;

public class XWalkNavigationHistoryBridge extends XWalkNavigationHistoryInternal {
    private ReflectMethod canGoBackMethod;
    private ReflectMethod canGoForwardMethod;
    private ReflectMethod clearMethod;
    private XWalkCoreBridge coreBridge;
    private ReflectMethod enumDirectionClassValueOfMethod;
    private ReflectMethod getCurrentIndexMethod;
    private ReflectMethod getCurrentItemMethod;
    private ReflectMethod getItemAtintMethod;
    private ReflectMethod hasItemAtintMethod;
    private XWalkNavigationHistoryInternal internal;
    private ReflectMethod navigateDirectionInternalintMethod;
    private ReflectMethod sizeMethod;
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    private Object ConvertDirectionInternal(DirectionInternal type) {
        return this.enumDirectionClassValueOfMethod.invoke(type.toString());
    }

    XWalkNavigationHistoryBridge(XWalkNavigationHistoryInternal internal) {
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
        this.internal = internal;
        reflectionInit();
    }

    public int size() {
        if (this.sizeMethod.isNull()) {
            return sizeSuper();
        }
        return ((Integer) this.sizeMethod.invoke(new Object[0])).intValue();
    }

    public int sizeSuper() {
        if (this.internal == null) {
            return super.size();
        }
        return this.internal.size();
    }

    public boolean hasItemAt(int index) {
        if (this.hasItemAtintMethod.isNull()) {
            return hasItemAtSuper(index);
        }
        return ((Boolean) this.hasItemAtintMethod.invoke(Integer.valueOf(index))).booleanValue();
    }

    public boolean hasItemAtSuper(int index) {
        if (this.internal == null) {
            return super.hasItemAt(index);
        }
        return this.internal.hasItemAt(index);
    }

    public XWalkNavigationItemInternal getItemAt(int index) {
        if (this.getItemAtintMethod.isNull()) {
            return getItemAtSuper(index);
        }
        return (XWalkNavigationItemBridge) this.coreBridge.getBridgeObject(this.getItemAtintMethod.invoke(Integer.valueOf(index)));
    }

    public XWalkNavigationItemBridge getItemAtSuper(int index) {
        XWalkNavigationItemInternal ret;
        if (this.internal == null) {
            ret = super.getItemAt(index);
        } else {
            ret = this.internal.getItemAt(index);
        }
        if (ret == null) {
            return null;
        }
        return ret instanceof XWalkNavigationItemBridge ? (XWalkNavigationItemBridge) ret : new XWalkNavigationItemBridge(ret);
    }

    public XWalkNavigationItemInternal getCurrentItem() {
        if (this.getCurrentItemMethod.isNull()) {
            return getCurrentItemSuper();
        }
        return (XWalkNavigationItemBridge) this.coreBridge.getBridgeObject(this.getCurrentItemMethod.invoke(new Object[0]));
    }

    public XWalkNavigationItemBridge getCurrentItemSuper() {
        XWalkNavigationItemInternal ret;
        if (this.internal == null) {
            ret = super.getCurrentItem();
        } else {
            ret = this.internal.getCurrentItem();
        }
        if (ret == null) {
            return null;
        }
        return ret instanceof XWalkNavigationItemBridge ? (XWalkNavigationItemBridge) ret : new XWalkNavigationItemBridge(ret);
    }

    public boolean canGoBack() {
        if (this.canGoBackMethod.isNull()) {
            return canGoBackSuper();
        }
        return ((Boolean) this.canGoBackMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean canGoBackSuper() {
        if (this.internal == null) {
            return super.canGoBack();
        }
        return this.internal.canGoBack();
    }

    public boolean canGoForward() {
        if (this.canGoForwardMethod.isNull()) {
            return canGoForwardSuper();
        }
        return ((Boolean) this.canGoForwardMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean canGoForwardSuper() {
        if (this.internal == null) {
            return super.canGoForward();
        }
        return this.internal.canGoForward();
    }

    public void navigate(DirectionInternal direction, int steps) {
        if (this.navigateDirectionInternalintMethod.isNull()) {
            navigateSuper(direction, steps);
            return;
        }
        this.navigateDirectionInternalintMethod.invoke(ConvertDirectionInternal(direction), Integer.valueOf(steps));
    }

    public void navigateSuper(DirectionInternal direction, int steps) {
        if (this.internal == null) {
            super.navigate(direction, steps);
        } else {
            this.internal.navigate(direction, steps);
        }
    }

    public int getCurrentIndex() {
        if (this.getCurrentIndexMethod.isNull()) {
            return getCurrentIndexSuper();
        }
        return ((Integer) this.getCurrentIndexMethod.invoke(new Object[0])).intValue();
    }

    public int getCurrentIndexSuper() {
        if (this.internal == null) {
            return super.getCurrentIndex();
        }
        return this.internal.getCurrentIndex();
    }

    public void clear() {
        if (this.clearMethod.isNull()) {
            clearSuper();
        } else {
            this.clearMethod.invoke(new Object[0]);
        }
    }

    public void clearSuper() {
        if (this.internal == null) {
            super.clear();
        } else {
            this.internal.clear();
        }
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            ReflectConstructor constructor = new ReflectConstructor(this.coreBridge.getWrapperClass("XWalkNavigationHistory"), Object.class);
            if (!constructor.isNull()) {
                this.wrapper = constructor.newInstance(this);
                this.enumDirectionClassValueOfMethod.init(null, this.coreBridge.getWrapperClass("XWalkNavigationHistory$Direction"), "valueOf", String.class);
                this.sizeMethod.init(this.wrapper, null, "size", new Class[0]);
                this.hasItemAtintMethod.init(this.wrapper, null, "hasItemAt", Integer.TYPE);
                this.getItemAtintMethod.init(this.wrapper, null, "getItemAt", Integer.TYPE);
                this.getCurrentItemMethod.init(this.wrapper, null, "getCurrentItem", new Class[0]);
                this.canGoBackMethod.init(this.wrapper, null, "canGoBack", new Class[0]);
                this.canGoForwardMethod.init(this.wrapper, null, "canGoForward", new Class[0]);
                this.navigateDirectionInternalintMethod.init(this.wrapper, null, "navigate", this.coreBridge.getWrapperClass("XWalkNavigationHistory$Direction"), Integer.TYPE);
                this.getCurrentIndexMethod.init(this.wrapper, null, "getCurrentIndex", new Class[0]);
                this.clearMethod.init(this.wrapper, null, "clear", new Class[0]);
            }
        }
    }
}
