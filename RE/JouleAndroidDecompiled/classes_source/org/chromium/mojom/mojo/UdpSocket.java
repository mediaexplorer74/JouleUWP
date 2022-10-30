package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Callbacks.Callback1;
import org.chromium.mojo.bindings.Callbacks.Callback3;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.bindings.InterfaceRequest;

public interface UdpSocket extends Interface {
    public static final Manager<UdpSocket, Proxy> MANAGER;

    public interface AllowAddressReuseResponse extends Callback1<NetworkError> {
    }

    public interface BindResponse extends Callback3<NetworkError, NetAddress, InterfaceRequest<UdpSocketReceiver>> {
    }

    public interface ConnectResponse extends Callback3<NetworkError, NetAddress, InterfaceRequest<UdpSocketReceiver>> {
    }

    public interface NegotiateMaxPendingSendRequestsResponse extends Callback1<Integer> {
    }

    public interface SendToResponse extends Callback1<NetworkError> {
    }

    public interface SetReceiveBufferSizeResponse extends Callback1<NetworkError> {
    }

    public interface SetSendBufferSizeResponse extends Callback1<NetworkError> {
    }

    public interface Proxy extends UdpSocket, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void allowAddressReuse(AllowAddressReuseResponse allowAddressReuseResponse);

    void bind(NetAddress netAddress, BindResponse bindResponse);

    void connect(NetAddress netAddress, ConnectResponse connectResponse);

    void negotiateMaxPendingSendRequests(int i, NegotiateMaxPendingSendRequestsResponse negotiateMaxPendingSendRequestsResponse);

    void receiveMore(int i);

    void sendTo(NetAddress netAddress, byte[] bArr, SendToResponse sendToResponse);

    void setReceiveBufferSize(int i, SetReceiveBufferSizeResponse setReceiveBufferSizeResponse);

    void setSendBufferSize(int i, SetSendBufferSizeResponse setSendBufferSizeResponse);

    static {
        MANAGER = UdpSocket_Internal.MANAGER;
    }
}
