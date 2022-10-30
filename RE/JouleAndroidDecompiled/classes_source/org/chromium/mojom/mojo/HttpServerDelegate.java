package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.bindings.InterfaceRequest;

public interface HttpServerDelegate extends Interface {
    public static final Manager<HttpServerDelegate, Proxy> MANAGER;

    public interface Proxy extends HttpServerDelegate, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void onConnected(HttpConnection httpConnection, InterfaceRequest<HttpConnectionDelegate> interfaceRequest);

    static {
        MANAGER = HttpServerDelegate_Internal.MANAGER;
    }
}
