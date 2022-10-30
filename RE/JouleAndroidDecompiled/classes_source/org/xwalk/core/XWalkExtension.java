package org.xwalk.core;

import java.util.ArrayList;

public abstract class XWalkExtension {
    static final /* synthetic */ boolean $assertionsDisabled;
    private Object bridge;
    private ReflectMethod broadcastMessageStringMethod;
    private ArrayList<Object> constructorParams;
    private ArrayList<Object> constructorTypes;
    private XWalkCoreWrapper coreWrapper;
    private ReflectMethod onBinaryMessageintbyteArrayMethod;
    private ReflectMethod onInstanceCreatedintMethod;
    private ReflectMethod onInstanceDestroyedintMethod;
    private ReflectMethod postBinaryMessageintbyteArrayMethod;
    private ReflectMethod postMessageintStringMethod;
    private ReflectMethod postWrapperMethod;

    public abstract void onMessage(int i, String str);

    public abstract String onSyncMessage(int i, String str);

    static {
        $assertionsDisabled = !XWalkExtension.class.desiredAssertionStatus();
    }

    Object getBridge() {
        return this.bridge;
    }

    public XWalkExtension(String name, String jsApi) {
        this.postMessageintStringMethod = new ReflectMethod(null, "postMessage", new Class[0]);
        this.postBinaryMessageintbyteArrayMethod = new ReflectMethod(null, "postBinaryMessage", new Class[0]);
        this.broadcastMessageStringMethod = new ReflectMethod(null, "broadcastMessage", new Class[0]);
        this.onInstanceCreatedintMethod = new ReflectMethod(null, "onInstanceCreated", new Class[0]);
        this.onInstanceDestroyedintMethod = new ReflectMethod(null, "onInstanceDestroyed", new Class[0]);
        this.onBinaryMessageintbyteArrayMethod = new ReflectMethod(null, "onBinaryMessage", new Class[0]);
        this.constructorTypes = new ArrayList();
        this.constructorTypes.add(String.class);
        this.constructorTypes.add(String.class);
        this.constructorParams = new ArrayList();
        this.constructorParams.add(name);
        this.constructorParams.add(jsApi);
        reflectionInit();
    }

    public XWalkExtension(String name, String jsApi, String[] entryPoints) {
        this.postMessageintStringMethod = new ReflectMethod(null, "postMessage", new Class[0]);
        this.postBinaryMessageintbyteArrayMethod = new ReflectMethod(null, "postBinaryMessage", new Class[0]);
        this.broadcastMessageStringMethod = new ReflectMethod(null, "broadcastMessage", new Class[0]);
        this.onInstanceCreatedintMethod = new ReflectMethod(null, "onInstanceCreated", new Class[0]);
        this.onInstanceDestroyedintMethod = new ReflectMethod(null, "onInstanceDestroyed", new Class[0]);
        this.onBinaryMessageintbyteArrayMethod = new ReflectMethod(null, "onBinaryMessage", new Class[0]);
        this.constructorTypes = new ArrayList();
        this.constructorTypes.add(String.class);
        this.constructorTypes.add(String.class);
        this.constructorTypes.add(String[].class);
        this.constructorParams = new ArrayList();
        this.constructorParams.add(name);
        this.constructorParams.add(jsApi);
        this.constructorParams.add(entryPoints);
        reflectionInit();
    }

    public void postMessage(int instanceID, String message) {
        this.postMessageintStringMethod.invoke(Integer.valueOf(instanceID), message);
    }

    public void postBinaryMessage(int instanceID, byte[] message) {
        this.postBinaryMessageintbyteArrayMethod.invoke(Integer.valueOf(instanceID), message);
    }

    public void broadcastMessage(String message) {
        this.broadcastMessageStringMethod.invoke(message);
    }

    public void onInstanceCreated(int instanceID) {
        this.onInstanceCreatedintMethod.invoke(Integer.valueOf(instanceID));
    }

    public void onInstanceDestroyed(int instanceID) {
        this.onInstanceDestroyedintMethod.invoke(Integer.valueOf(instanceID));
    }

    public void onBinaryMessage(int instanceID, byte[] message) {
        this.onBinaryMessageintbyteArrayMethod.invoke(Integer.valueOf(instanceID), message);
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
        this.bridge = new ReflectConstructor(this.coreWrapper.getBridgeClass("XWalkExtensionBridge"), paramTypes).newInstance(this.constructorParams.toArray());
        if (this.postWrapperMethod != null) {
            this.postWrapperMethod.invoke(new Object[0]);
        }
        this.postMessageintStringMethod.init(this.bridge, null, "postMessageSuper", Integer.TYPE, String.class);
        this.postBinaryMessageintbyteArrayMethod.init(this.bridge, null, "postBinaryMessageSuper", Integer.TYPE, byte[].class);
        this.broadcastMessageStringMethod.init(this.bridge, null, "broadcastMessageSuper", String.class);
        this.onInstanceCreatedintMethod.init(this.bridge, null, "onInstanceCreatedSuper", Integer.TYPE);
        this.onInstanceDestroyedintMethod.init(this.bridge, null, "onInstanceDestroyedSuper", Integer.TYPE);
        this.onBinaryMessageintbyteArrayMethod.init(this.bridge, null, "onBinaryMessageSuper", Integer.TYPE, byte[].class);
    }
}
