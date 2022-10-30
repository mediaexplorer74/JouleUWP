package org.xwalk.core.internal;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

public class ClientCertRequestHandlerBridge extends ClientCertRequestHandlerInternal {
    private ReflectMethod cancelMethod;
    private XWalkCoreBridge coreBridge;
    private ReflectMethod getHostMethod;
    private ReflectMethod getPortMethod;
    private ReflectMethod ignoreMethod;
    private ClientCertRequestHandlerInternal internal;
    private ReflectMethod proceedPrivateKeyListMethod;
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    ClientCertRequestHandlerBridge(ClientCertRequestHandlerInternal internal) {
        this.proceedPrivateKeyListMethod = new ReflectMethod(null, "proceed", new Class[0]);
        this.ignoreMethod = new ReflectMethod(null, "ignore", new Class[0]);
        this.cancelMethod = new ReflectMethod(null, "cancel", new Class[0]);
        this.getHostMethod = new ReflectMethod(null, "getHost", new Class[0]);
        this.getPortMethod = new ReflectMethod(null, "getPort", new Class[0]);
        this.internal = internal;
        reflectionInit();
    }

    public void proceed(PrivateKey privateKey, List<X509Certificate> chain) {
        if (this.proceedPrivateKeyListMethod.isNull()) {
            proceedSuper(privateKey, chain);
            return;
        }
        this.proceedPrivateKeyListMethod.invoke(privateKey, chain);
    }

    public void proceedSuper(PrivateKey privateKey, List<X509Certificate> chain) {
        if (this.internal == null) {
            super.proceed(privateKey, chain);
        } else {
            this.internal.proceed(privateKey, chain);
        }
    }

    public void ignore() {
        if (this.ignoreMethod.isNull()) {
            ignoreSuper();
        } else {
            this.ignoreMethod.invoke(new Object[0]);
        }
    }

    public void ignoreSuper() {
        if (this.internal == null) {
            super.ignore();
        } else {
            this.internal.ignore();
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

    public String getHost() {
        if (this.getHostMethod.isNull()) {
            return getHostSuper();
        }
        return (String) this.getHostMethod.invoke(new Object[0]);
    }

    public String getHostSuper() {
        String ret;
        if (this.internal == null) {
            ret = super.getHost();
        } else {
            ret = this.internal.getHost();
        }
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public int getPort() {
        if (this.getPortMethod.isNull()) {
            return getPortSuper();
        }
        return ((Integer) this.getPortMethod.invoke(new Object[0])).intValue();
    }

    public int getPortSuper() {
        if (this.internal == null) {
            return super.getPort();
        }
        return this.internal.getPort();
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            ReflectConstructor constructor = new ReflectConstructor(this.coreBridge.getWrapperClass("ClientCertRequestHandler"), Object.class);
            if (!constructor.isNull()) {
                this.wrapper = constructor.newInstance(this);
                this.proceedPrivateKeyListMethod.init(this.wrapper, null, "proceed", PrivateKey.class, List.class);
                this.ignoreMethod.init(this.wrapper, null, "ignore", new Class[0]);
                this.cancelMethod.init(this.wrapper, null, "cancel", new Class[0]);
                this.getHostMethod.init(this.wrapper, null, "getHost", new Class[0]);
                this.getPortMethod.init(this.wrapper, null, "getPort", new Class[0]);
            }
        }
    }
}
