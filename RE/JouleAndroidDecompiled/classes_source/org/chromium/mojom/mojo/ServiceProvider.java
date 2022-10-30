package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.system.MessagePipeHandle;

public interface ServiceProvider extends Interface {
    public static final Manager<ServiceProvider, Proxy> MANAGER;

    public interface Proxy extends ServiceProvider, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void connectToService(String str, MessagePipeHandle messagePipeHandle);

    static {
        MANAGER = ServiceProvider_Internal.MANAGER;
    }
}
