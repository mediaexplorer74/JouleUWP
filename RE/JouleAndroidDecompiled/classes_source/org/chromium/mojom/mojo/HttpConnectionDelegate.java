package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Callbacks.Callback1;
import org.chromium.mojo.bindings.Callbacks.Callback3;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.bindings.InterfaceRequest;
import org.chromium.mojo.system.DataPipe.ConsumerHandle;

public interface HttpConnectionDelegate extends Interface {
    public static final Manager<HttpConnectionDelegate, Proxy> MANAGER;

    public interface OnReceivedRequestResponse extends Callback1<HttpResponse> {
    }

    public interface OnReceivedWebSocketRequestResponse extends Callback3<InterfaceRequest<WebSocket>, ConsumerHandle, WebSocketClient> {
    }

    public interface Proxy extends HttpConnectionDelegate, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void onReceivedRequest(HttpRequest httpRequest, OnReceivedRequestResponse onReceivedRequestResponse);

    void onReceivedWebSocketRequest(HttpRequest httpRequest, OnReceivedWebSocketRequestResponse onReceivedWebSocketRequestResponse);

    static {
        MANAGER = HttpConnectionDelegate_Internal.MANAGER;
    }
}
