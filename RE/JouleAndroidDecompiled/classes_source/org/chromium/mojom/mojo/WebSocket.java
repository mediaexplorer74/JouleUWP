package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.system.DataPipe.ConsumerHandle;

public interface WebSocket extends Interface {
    public static final short ABNORMAL_CLOSE_CODE = (short) 1006;
    public static final Manager<WebSocket, Proxy> MANAGER;

    public static final class MessageType {
        public static final int BINARY = 2;
        public static final int CONTINUATION = 0;
        public static final int TEXT = 1;

        private MessageType() {
        }
    }

    public interface Proxy extends WebSocket, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void close(short s, String str);

    void connect(String str, String[] strArr, String str2, ConsumerHandle consumerHandle, WebSocketClient webSocketClient);

    void flowControl(long j);

    void send(boolean z, int i, int i2);

    static {
        MANAGER = WebSocket_Internal.MANAGER;
    }
}
