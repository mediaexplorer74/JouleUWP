package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Callbacks.Callback1;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;

public interface HttpConnection extends Interface {
    public static final Manager<HttpConnection, Proxy> MANAGER;

    public interface SetReceiveBufferSizeResponse extends Callback1<NetworkError> {
    }

    public interface SetSendBufferSizeResponse extends Callback1<NetworkError> {
    }

    public interface Proxy extends HttpConnection, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void setReceiveBufferSize(int i, SetReceiveBufferSizeResponse setReceiveBufferSizeResponse);

    void setSendBufferSize(int i, SetSendBufferSizeResponse setSendBufferSizeResponse);

    static {
        MANAGER = HttpConnection_Internal.MANAGER;
    }
}
