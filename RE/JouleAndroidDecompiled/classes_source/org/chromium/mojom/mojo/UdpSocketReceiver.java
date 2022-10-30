package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;

public interface UdpSocketReceiver extends Interface {
    public static final Manager<UdpSocketReceiver, Proxy> MANAGER;

    public interface Proxy extends UdpSocketReceiver, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void onReceived(NetworkError networkError, NetAddress netAddress, byte[] bArr);

    static {
        MANAGER = UdpSocketReceiver_Internal.MANAGER;
    }
}
