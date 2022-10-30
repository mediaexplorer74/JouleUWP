package org.chromium.mojom.mojo;

import android.support.v4.widget.ExploreByTouchHelper;
import java.util.Arrays;
import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.DeserializationException;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Interface.AbstractProxy;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.bindings.InterfaceControlMessagesHelper;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.MessageHeader;
import org.chromium.mojo.bindings.MessageReceiver;
import org.chromium.mojo.bindings.MessageReceiverWithResponder;
import org.chromium.mojo.bindings.ServiceMessage;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;
import org.chromium.mojo.system.DataPipe.ConsumerHandle;
import org.chromium.mojo.system.InvalidHandle;
import org.xwalk.core.internal.XWalkResourceClientInternal;

class WebSocket_Internal {
    private static final int CLOSE_ORDINAL = 3;
    private static final int CONNECT_ORDINAL = 0;
    private static final int FLOW_CONTROL_ORDINAL = 2;
    public static final Manager<WebSocket, org.chromium.mojom.mojo.WebSocket.Proxy> MANAGER;
    private static final int SEND_ORDINAL = 1;

    /* renamed from: org.chromium.mojom.mojo.WebSocket_Internal.1 */
    static class C06381 extends Manager<WebSocket, org.chromium.mojom.mojo.WebSocket.Proxy> {
        C06381() {
        }

        public String getName() {
            return "mojo::WebSocket";
        }

        public int getVersion() {
            return WebSocket_Internal.CONNECT_ORDINAL;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, WebSocket impl) {
            return new Stub(core, impl);
        }

        public WebSocket[] buildArray(int size) {
            return new WebSocket[size];
        }
    }

    static final class WebSocketCloseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public short code;
        public String reason;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[WebSocket_Internal.SEND_ORDINAL];
            dataHeaderArr[WebSocket_Internal.CONNECT_ORDINAL] = new DataHeader(STRUCT_SIZE, WebSocket_Internal.CONNECT_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[WebSocket_Internal.CONNECT_ORDINAL];
        }

        private WebSocketCloseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public WebSocketCloseParams() {
            this(WebSocket_Internal.CONNECT_ORDINAL);
        }

        public static WebSocketCloseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static WebSocketCloseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            WebSocketCloseParams result = new WebSocketCloseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.code = decoder0.readShort(8);
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.reason = decoder0.readString(16, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.code, 8);
            encoder0.encode(this.reason, 16, false);
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (getClass() != object.getClass()) {
                return false;
            }
            WebSocketCloseParams other = (WebSocketCloseParams) object;
            if (this.code != other.code) {
                return false;
            }
            if (BindingsHelper.equals(this.reason, other.reason)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.code)) * 31) + BindingsHelper.hashCode(this.reason);
        }
    }

    static final class WebSocketConnectParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 48;
        private static final DataHeader[] VERSION_ARRAY;
        public WebSocketClient client;
        public String origin;
        public String[] protocols;
        public ConsumerHandle sendStream;
        public String url;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[WebSocket_Internal.SEND_ORDINAL];
            dataHeaderArr[WebSocket_Internal.CONNECT_ORDINAL] = new DataHeader(STRUCT_SIZE, WebSocket_Internal.CONNECT_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[WebSocket_Internal.CONNECT_ORDINAL];
        }

        private WebSocketConnectParams(int version) {
            super(STRUCT_SIZE, version);
            this.sendStream = InvalidHandle.INSTANCE;
        }

        public WebSocketConnectParams() {
            this(WebSocket_Internal.CONNECT_ORDINAL);
        }

        public static WebSocketConnectParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static WebSocketConnectParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            WebSocketConnectParams result = new WebSocketConnectParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.url = decoder0.readString(8, false);
            }
            if (mainDataHeader.elementsOrVersion >= 0) {
                Decoder decoder1 = decoder0.readPointer(16, false);
                DataHeader si1 = decoder1.readDataHeaderForPointerArray(-1);
                result.protocols = new String[si1.elementsOrVersion];
                for (int i1 = WebSocket_Internal.CONNECT_ORDINAL; i1 < si1.elementsOrVersion; i1 += WebSocket_Internal.SEND_ORDINAL) {
                    result.protocols[i1] = decoder1.readString((i1 * 8) + 8, false);
                }
            }
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.origin = decoder0.readString(24, false);
            }
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.sendStream = decoder0.readConsumerHandle(32, false);
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.client = (WebSocketClient) decoder0.readServiceInterface(36, false, WebSocketClient.MANAGER);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.url, 8, false);
            if (this.protocols == null) {
                encoder0.encodeNullPointer(16, false);
            } else {
                Encoder encoder1 = encoder0.encodePointerArray(this.protocols.length, 16, -1);
                for (int i0 = WebSocket_Internal.CONNECT_ORDINAL; i0 < this.protocols.length; i0 += WebSocket_Internal.SEND_ORDINAL) {
                    encoder1.encode(this.protocols[i0], (i0 * 8) + 8, false);
                }
            }
            encoder0.encode(this.origin, 24, false);
            encoder0.encode(this.sendStream, 32, false);
            encoder0.encode(this.client, 36, false, WebSocketClient.MANAGER);
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (getClass() != object.getClass()) {
                return false;
            }
            WebSocketConnectParams other = (WebSocketConnectParams) object;
            if (!BindingsHelper.equals(this.url, other.url)) {
                return false;
            }
            if (!Arrays.deepEquals(this.protocols, other.protocols)) {
                return false;
            }
            if (!BindingsHelper.equals(this.origin, other.origin)) {
                return false;
            }
            if (!BindingsHelper.equals(this.sendStream, other.sendStream)) {
                return false;
            }
            if (BindingsHelper.equals(this.client, other.client)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((((((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.url)) * 31) + Arrays.deepHashCode(this.protocols)) * 31) + BindingsHelper.hashCode(this.origin)) * 31) + BindingsHelper.hashCode(this.sendStream)) * 31) + BindingsHelper.hashCode(this.client);
        }
    }

    static final class WebSocketFlowControlParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public long quota;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[WebSocket_Internal.SEND_ORDINAL];
            dataHeaderArr[WebSocket_Internal.CONNECT_ORDINAL] = new DataHeader(STRUCT_SIZE, WebSocket_Internal.CONNECT_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[WebSocket_Internal.CONNECT_ORDINAL];
        }

        private WebSocketFlowControlParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public WebSocketFlowControlParams() {
            this(WebSocket_Internal.CONNECT_ORDINAL);
        }

        public static WebSocketFlowControlParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static WebSocketFlowControlParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            WebSocketFlowControlParams result = new WebSocketFlowControlParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.quota = decoder0.readLong(8);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.quota, 8);
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (getClass() != object.getClass()) {
                return false;
            }
            if (this.quota != ((WebSocketFlowControlParams) object).quota) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.quota);
        }
    }

    static final class WebSocketSendParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean fin;
        public int numBytes;
        public int type;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[WebSocket_Internal.SEND_ORDINAL];
            dataHeaderArr[WebSocket_Internal.CONNECT_ORDINAL] = new DataHeader(STRUCT_SIZE, WebSocket_Internal.CONNECT_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[WebSocket_Internal.CONNECT_ORDINAL];
        }

        private WebSocketSendParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public WebSocketSendParams() {
            this(WebSocket_Internal.CONNECT_ORDINAL);
        }

        public static WebSocketSendParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static WebSocketSendParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            WebSocketSendParams result = new WebSocketSendParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.fin = decoder0.readBoolean(8, WebSocket_Internal.CONNECT_ORDINAL);
            }
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.type = decoder0.readInt(12);
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.numBytes = decoder0.readInt(16);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.fin, 8, (int) WebSocket_Internal.CONNECT_ORDINAL);
            encoder0.encode(this.type, 12);
            encoder0.encode(this.numBytes, 16);
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (getClass() != object.getClass()) {
                return false;
            }
            WebSocketSendParams other = (WebSocketSendParams) object;
            if (this.fin != other.fin) {
                return false;
            }
            if (this.type != other.type) {
                return false;
            }
            if (this.numBytes != other.numBytes) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return ((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.fin)) * 31) + BindingsHelper.hashCode(this.type)) * 31) + BindingsHelper.hashCode(this.numBytes);
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<WebSocket> {
        Stub(Core core, WebSocket impl) {
            super(core, impl);
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                MessageHeader header = messageWithHeader.getHeader();
                if (!header.validateHeader(WebSocket_Internal.CONNECT_ORDINAL)) {
                    return false;
                }
                switch (header.getType()) {
                    case XWalkResourceClientInternal.ERROR_HOST_LOOKUP /*-2*/:
                        return InterfaceControlMessagesHelper.handleRunOrClosePipe(WebSocket_Internal.MANAGER, messageWithHeader);
                    case WebSocket_Internal.CONNECT_ORDINAL /*0*/:
                        WebSocketConnectParams data = WebSocketConnectParams.deserialize(messageWithHeader.getPayload());
                        ((WebSocket) getImpl()).connect(data.url, data.protocols, data.origin, data.sendStream, data.client);
                        return true;
                    case WebSocket_Internal.SEND_ORDINAL /*1*/:
                        WebSocketSendParams data2 = WebSocketSendParams.deserialize(messageWithHeader.getPayload());
                        ((WebSocket) getImpl()).send(data2.fin, data2.type, data2.numBytes);
                        return true;
                    case WebSocket_Internal.FLOW_CONTROL_ORDINAL /*2*/:
                        ((WebSocket) getImpl()).flowControl(WebSocketFlowControlParams.deserialize(messageWithHeader.getPayload()).quota);
                        return true;
                    case WebSocket_Internal.CLOSE_ORDINAL /*3*/:
                        WebSocketCloseParams data3 = WebSocketCloseParams.deserialize(messageWithHeader.getPayload());
                        ((WebSocket) getImpl()).close(data3.code, data3.reason);
                        return true;
                    default:
                        return false;
                }
            } catch (DeserializationException e) {
                return false;
            }
        }

        public boolean acceptWithResponder(Message message, MessageReceiver receiver) {
            boolean z = false;
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                MessageHeader header = messageWithHeader.getHeader();
                if (header.validateHeader(WebSocket_Internal.SEND_ORDINAL)) {
                    switch (header.getType()) {
                        case ExploreByTouchHelper.HOST_ID /*-1*/:
                            z = InterfaceControlMessagesHelper.handleRun(getCore(), WebSocket_Internal.MANAGER, messageWithHeader, receiver);
                            break;
                        default:
                            break;
                    }
                }
            } catch (DeserializationException e) {
            }
            return z;
        }
    }

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.WebSocket.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void connect(String url, String[] protocols, String origin, ConsumerHandle sendStream, WebSocketClient client) {
            WebSocketConnectParams _message = new WebSocketConnectParams();
            _message.url = url;
            _message.protocols = protocols;
            _message.origin = origin;
            _message.sendStream = sendStream;
            _message.client = client;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader((int) WebSocket_Internal.CONNECT_ORDINAL)));
        }

        public void send(boolean fin, int type, int numBytes) {
            WebSocketSendParams _message = new WebSocketSendParams();
            _message.fin = fin;
            _message.type = type;
            _message.numBytes = numBytes;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader((int) WebSocket_Internal.SEND_ORDINAL)));
        }

        public void flowControl(long quota) {
            WebSocketFlowControlParams _message = new WebSocketFlowControlParams();
            _message.quota = quota;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader((int) WebSocket_Internal.FLOW_CONTROL_ORDINAL)));
        }

        public void close(short code, String reason) {
            WebSocketCloseParams _message = new WebSocketCloseParams();
            _message.code = code;
            _message.reason = reason;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader((int) WebSocket_Internal.CLOSE_ORDINAL)));
        }
    }

    WebSocket_Internal() {
    }

    static {
        MANAGER = new C06381();
    }
}
