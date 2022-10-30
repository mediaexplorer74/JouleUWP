package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Callbacks.Callback2;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.bindings.InterfaceRequest;
import org.chromium.mojo.system.DataPipe.ConsumerHandle;
import org.chromium.mojo.system.DataPipe.ProducerHandle;

public interface TcpServerSocket extends Interface {
    public static final Manager<TcpServerSocket, Proxy> MANAGER;

    public interface AcceptResponse extends Callback2<NetworkError, NetAddress> {
    }

    public interface Proxy extends TcpServerSocket, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void accept(ConsumerHandle consumerHandle, ProducerHandle producerHandle, InterfaceRequest<TcpConnectedSocket> interfaceRequest, AcceptResponse acceptResponse);

    static {
        MANAGER = TcpServerSocket_Internal.MANAGER;
    }
}
