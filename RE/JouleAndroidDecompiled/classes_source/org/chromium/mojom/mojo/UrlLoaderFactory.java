package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.bindings.InterfaceRequest;

public interface UrlLoaderFactory extends Interface {
    public static final Manager<UrlLoaderFactory, Proxy> MANAGER;

    public interface Proxy extends UrlLoaderFactory, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void createUrlLoader(InterfaceRequest<UrlLoader> interfaceRequest);

    static {
        MANAGER = UrlLoaderFactory_Internal.MANAGER;
    }
}
