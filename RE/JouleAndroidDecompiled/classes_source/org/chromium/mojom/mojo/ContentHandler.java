package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.bindings.InterfaceRequest;

public interface ContentHandler extends Interface {
    public static final Manager<ContentHandler, Proxy> MANAGER;

    public interface Proxy extends ContentHandler, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void startApplication(InterfaceRequest<Application> interfaceRequest, UrlResponse urlResponse);

    static {
        MANAGER = ContentHandler_Internal.MANAGER;
    }
}
