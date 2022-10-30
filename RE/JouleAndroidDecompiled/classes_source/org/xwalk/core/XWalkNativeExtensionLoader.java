package org.xwalk.core;

import java.util.ArrayList;

public class XWalkNativeExtensionLoader {
    static final /* synthetic */ boolean $assertionsDisabled;
    private Object bridge;
    private ArrayList<Object> constructorParams;
    private ArrayList<Object> constructorTypes;
    private XWalkCoreWrapper coreWrapper;
    private ReflectMethod postWrapperMethod;
    private ReflectMethod registerNativeExtensionsInPathStringMethod;

    static {
        $assertionsDisabled = !XWalkNativeExtensionLoader.class.desiredAssertionStatus();
    }

    Object getBridge() {
        return this.bridge;
    }

    public XWalkNativeExtensionLoader() {
        this.registerNativeExtensionsInPathStringMethod = new ReflectMethod(null, "registerNativeExtensionsInPath", new Class[0]);
        this.constructorTypes = new ArrayList();
        this.constructorParams = new ArrayList();
        reflectionInit();
    }

    public void registerNativeExtensionsInPath(String path) {
        this.registerNativeExtensionsInPathStringMethod.invoke(path);
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
        this.bridge = new ReflectConstructor(this.coreWrapper.getBridgeClass("XWalkNativeExtensionLoaderBridge"), paramTypes).newInstance(this.constructorParams.toArray());
        if (this.postWrapperMethod != null) {
            this.postWrapperMethod.invoke(new Object[0]);
        }
        this.registerNativeExtensionsInPathStringMethod.init(this.bridge, null, "registerNativeExtensionsInPathSuper", String.class);
    }
}
