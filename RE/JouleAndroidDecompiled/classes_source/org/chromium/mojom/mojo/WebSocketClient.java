package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.system.DataPipe.ConsumerHandle;

public interface WebSocketClient extends Interface {
    public static final Manager<WebSocketClient, Proxy> MANAGER;

    public interface Proxy extends WebSocketClient, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void didClose(boolean z, short s, String str);

    void didConnect(String str, String str2, ConsumerHandle consumerHandle);

    void didFail(String str);

    void didReceiveData(boolean z, int i, int i2);

    void didReceiveFlowControl(long j);

    static {
        MANAGER = WebSocketClient_Internal.MANAGER;
    }
}
