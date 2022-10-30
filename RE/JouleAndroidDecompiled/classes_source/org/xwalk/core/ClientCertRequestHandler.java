package org.xwalk.core;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class ClientCertRequestHandler implements ClientCertRequest {
    private Object bridge;
    private ReflectMethod cancelMethod;
    private ArrayList<Object> constructorParams;
    private ArrayList<Object> constructorTypes;
    private XWalkCoreWrapper coreWrapper;
    private ReflectMethod getHostMethod;
    private ReflectMethod getPortMethod;
    private ReflectMethod ignoreMethod;
    private ReflectMethod postWrapperMethod;
    private ReflectMethod proceedPrivateKeyListMethod;

    Object getBridge() {
        return this.bridge;
    }

    public ClientCertRequestHandler(Object bridge) {
        this.proceedPrivateKeyListMethod = new ReflectMethod(null, "proceed", new Class[0]);
        this.ignoreMethod = new ReflectMethod(null, "ignore", new Class[0]);
        this.cancelMethod = new ReflectMethod(null, "cancel", new Class[0]);
        this.getHostMethod = new ReflectMethod(null, "getHost", new Class[0]);
        this.getPortMethod = new ReflectMethod(null, "getPort", new Class[0]);
        this.bridge = bridge;
        reflectionInit();
    }

    public void proceed(PrivateKey privateKey, List<X509Certificate> chain) {
        this.proceedPrivateKeyListMethod.invoke(privateKey, chain);
    }

    public void ignore() {
        this.ignoreMethod.invoke(new Object[0]);
    }

    public void cancel() {
        this.cancelMethod.invoke(new Object[0]);
    }

    public String getHost() {
        return (String) this.getHostMethod.invoke(new Object[0]);
    }

    public int getPort() {
        return ((Integer) this.getPortMethod.invoke(new Object[0])).intValue();
    }

    void reflectionInit() {
        XWalkCoreWrapper.initEmbeddedMode();
        this.coreWrapper = XWalkCoreWrapper.getInstance();
        if (this.coreWrapper == null) {
            XWalkCoreWrapper.reserveReflectObject(this);
            return;
        }
        this.proceedPrivateKeyListMethod.init(this.bridge, null, "proceedSuper", PrivateKey.class, List.class);
        this.ignoreMethod.init(this.bridge, null, "ignoreSuper", new Class[0]);
        this.cancelMethod.init(this.bridge, null, "cancelSuper", new Class[0]);
        this.getHostMethod.init(this.bridge, null, "getHostSuper", new Class[0]);
        this.getPortMethod.init(this.bridge, null, "getPortSuper", new Class[0]);
    }
}
