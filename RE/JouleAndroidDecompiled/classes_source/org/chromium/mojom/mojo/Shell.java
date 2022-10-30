package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.bindings.InterfaceRequest;

public interface Shell extends Interface {
    public static final Manager<Shell, Proxy> MANAGER;

    public interface Proxy extends Shell, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void connectToApplication(UrlRequest urlRequest, InterfaceRequest<ServiceProvider> interfaceRequest, ServiceProvider serviceProvider);

    void quitApplication();

    static {
        MANAGER = Shell_Internal.MANAGER;
    }
}
