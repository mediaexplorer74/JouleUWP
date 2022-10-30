package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;

public interface TcpConnectedSocket extends Interface {
    public static final Manager<TcpConnectedSocket, Proxy> MANAGER;

    public interface Proxy extends TcpConnectedSocket, org.chromium.mojo.bindings.Interface.Proxy {
    }

    static {
        MANAGER = TcpConnectedSocket_Internal.MANAGER;
    }
}
