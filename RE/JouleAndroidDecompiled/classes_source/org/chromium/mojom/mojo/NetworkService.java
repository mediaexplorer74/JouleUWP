package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Callbacks.Callback2;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.bindings.InterfaceRequest;
import org.chromium.mojo.system.DataPipe.ConsumerHandle;
import org.chromium.mojo.system.DataPipe.ProducerHandle;

public interface NetworkService extends Interface {
    public static final Manager<NetworkService, Proxy> MANAGER;

    public interface CreateHttpServerResponse extends Callback2<NetworkError, NetAddress> {
    }

    public interface CreateTcpBoundSocketResponse extends Callback2<NetworkError, NetAddress> {
    }

    public interface CreateTcpConnectedSocketResponse extends Callback2<NetworkError, NetAddress> {
    }

    public interface Proxy extends NetworkService, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void createHttpServer(NetAddress netAddress, HttpServerDelegate httpServerDelegate, CreateHttpServerResponse createHttpServerResponse);

    void createTcpBoundSocket(NetAddress netAddress, InterfaceRequest<TcpBoundSocket> interfaceRequest, CreateTcpBoundSocketResponse createTcpBoundSocketResponse);

    void createTcpConnectedSocket(NetAddress netAddress, ConsumerHandle consumerHandle, ProducerHandle producerHandle, InterfaceRequest<TcpConnectedSocket> interfaceRequest, CreateTcpConnectedSocketResponse createTcpConnectedSocketResponse);

    void createUdpSocket(InterfaceRequest<UdpSocket> interfaceRequest);

    void createWebSocket(InterfaceRequest<WebSocket> interfaceRequest);

    void getCookieStore(InterfaceRequest<CookieStore> interfaceRequest);

    static {
        MANAGER = NetworkService_Internal.MANAGER;
    }
}
