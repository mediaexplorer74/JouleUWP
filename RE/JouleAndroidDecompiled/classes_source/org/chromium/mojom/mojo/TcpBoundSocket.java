package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Callbacks.Callback1;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.bindings.InterfaceRequest;
import org.chromium.mojo.system.DataPipe.ConsumerHandle;
import org.chromium.mojo.system.DataPipe.ProducerHandle;

public interface TcpBoundSocket extends Interface {
    public static final Manager<TcpBoundSocket, Proxy> MANAGER;

    public interface ConnectResponse extends Callback1<NetworkError> {
    }

    public interface StartListeningResponse extends Callback1<NetworkError> {
    }

    public interface Proxy extends TcpBoundSocket, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void connect(NetAddress netAddress, ConsumerHandle consumerHandle, ProducerHandle producerHandle, InterfaceRequest<TcpConnectedSocket> interfaceRequest, ConnectResponse connectResponse);

    void startListening(InterfaceRequest<TcpServerSocket> interfaceRequest, StartListeningResponse startListeningResponse);

    static {
        MANAGER = TcpBoundSocket_Internal.MANAGER;
    }
}
