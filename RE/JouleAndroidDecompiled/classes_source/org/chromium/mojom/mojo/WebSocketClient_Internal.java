package org.chromium.mojom.mojo;

import android.support.v4.widget.ExploreByTouchHelper;
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

class WebSocketClient_Internal {
    private static final int DID_CLOSE_ORDINAL = 4;
    private static final int DID_CONNECT_ORDINAL = 0;
    private static final int DID_FAIL_ORDINAL = 3;
    private static final int DID_RECEIVE_DATA_ORDINAL = 1;
    private static final int DID_RECEIVE_FLOW_CONTROL_ORDINAL = 2;
    public static final Manager<WebSocketClient, org.chromium.mojom.mojo.WebSocketClient.Proxy> MANAGER;

    /* renamed from: org.chromium.mojom.mojo.WebSocketClient_Internal.1 */
    static class C06371 extends Manager<WebSocketClient, org.chromium.mojom.mojo.WebSocketClient.Proxy> {
        C06371() {
        }

        public String getName() {
            return "mojo::WebSocketClient";
        }

        public int getVersion() {
            return WebSocketClient_Internal.DID_CONNECT_ORDINAL;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, WebSocketClient impl) {
            return new Stub(core, impl);
        }

        public WebSocketClient[] buildArray(int size) {
            return new WebSocketClient[size];
        }
    }

    static final class WebSocketClientDidCloseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public short code;
        public String reason;
        public boolean wasClean;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[WebSocketClient_Internal.DID_RECEIVE_DATA_ORDINAL];
            dataHeaderArr[WebSocketClient_Internal.DID_CONNECT_ORDINAL] = new DataHeader(STRUCT_SIZE, WebSocketClient_Internal.DID_CONNECT_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[WebSocketClient_Internal.DID_CONNECT_ORDINAL];
        }

        private WebSocketClientDidCloseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public WebSocketClientDidCloseParams() {
            this(WebSocketClient_Internal.DID_CONNECT_ORDINAL);
        }

        public static WebSocketClientDidCloseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static WebSocketClientDidCloseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            WebSocketClientDidCloseParams result = new WebSocketClientDidCloseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.wasClean = decoder0.readBoolean(8, WebSocketClient_Internal.DID_CONNECT_ORDINAL);
            }
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.code = decoder0.readShort(10);
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.reason = decoder0.readString(16, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.wasClean, 8, (int) WebSocketClient_Internal.DID_CONNECT_ORDINAL);
            encoder0.encode(this.code, 10);
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
            WebSocketClientDidCloseParams other = (WebSocketClientDidCloseParams) object;
            if (this.wasClean != other.wasClean) {
                return false;
            }
            if (this.code != other.code) {
                return false;
            }
            if (BindingsHelper.equals(this.reason, other.reason)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.wasClean)) * 31) + BindingsHelper.hashCode(this.code)) * 31) + BindingsHelper.hashCode(this.reason);
        }
    }

    static final class WebSocketClientDidConnectParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 32;
        private static final DataHeader[] VERSION_ARRAY;
        public String extensions;
        public ConsumerHandle receiveStream;
        public String selectedSubprotocol;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[WebSocketClient_Internal.DID_RECEIVE_DATA_ORDINAL];
            dataHeaderArr[WebSocketClient_Internal.DID_CONNECT_ORDINAL] = new DataHeader(STRUCT_SIZE, WebSocketClient_Internal.DID_CONNECT_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[WebSocketClient_Internal.DID_CONNECT_ORDINAL];
        }

        private WebSocketClientDidConnectParams(int version) {
            super(STRUCT_SIZE, version);
            this.receiveStream = InvalidHandle.INSTANCE;
        }

        public WebSocketClientDidConnectParams() {
            this(WebSocketClient_Internal.DID_CONNECT_ORDINAL);
        }

        public static WebSocketClientDidConnectParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static WebSocketClientDidConnectParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            WebSocketClientDidConnectParams result = new WebSocketClientDidConnectParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.selectedSubprotocol = decoder0.readString(8, false);
            }
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.extensions = decoder0.readString(16, false);
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.receiveStream = decoder0.readConsumerHandle(24, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.selectedSubprotocol, 8, false);
            encoder0.encode(this.extensions, 16, false);
            encoder0.encode(this.receiveStream, 24, false);
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
            WebSocketClientDidConnectParams other = (WebSocketClientDidConnectParams) object;
            if (!BindingsHelper.equals(this.selectedSubprotocol, other.selectedSubprotocol)) {
                return false;
            }
            if (!BindingsHelper.equals(this.extensions, other.extensions)) {
                return false;
            }
            if (BindingsHelper.equals(this.receiveStream, other.receiveStream)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.selectedSubprotocol)) * 31) + BindingsHelper.hashCode(this.extensions)) * 31) + BindingsHelper.hashCode(this.receiveStream);
        }
    }

    static final class WebSocketClientDidFailParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public String message;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[WebSocketClient_Internal.DID_RECEIVE_DATA_ORDINAL];
            dataHeaderArr[WebSocketClient_Internal.DID_CONNECT_ORDINAL] = new DataHeader(STRUCT_SIZE, WebSocketClient_Internal.DID_CONNECT_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[WebSocketClient_Internal.DID_CONNECT_ORDINAL];
        }

        private WebSocketClientDidFailParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public WebSocketClientDidFailParams() {
            this(WebSocketClient_Internal.DID_CONNECT_ORDINAL);
        }

        public static WebSocketClientDidFailParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static WebSocketClientDidFailParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            WebSocketClientDidFailParams result = new WebSocketClientDidFailParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.message = decoder0.readString(8, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.message, 8, false);
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
            if (BindingsHelper.equals(this.message, ((WebSocketClientDidFailParams) object).message)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.message);
        }
    }

    static final class WebSocketClientDidReceiveDataParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean fin;
        public int numBytes;
        public int type;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[WebSocketClient_Internal.DID_RECEIVE_DATA_ORDINAL];
            dataHeaderArr[WebSocketClient_Internal.DID_CONNECT_ORDINAL] = new DataHeader(STRUCT_SIZE, WebSocketClient_Internal.DID_CONNECT_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[WebSocketClient_Internal.DID_CONNECT_ORDINAL];
        }

        private WebSocketClientDidReceiveDataParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public WebSocketClientDidReceiveDataParams() {
            this(WebSocketClient_Internal.DID_CONNECT_ORDINAL);
        }

        public static WebSocketClientDidReceiveDataParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static WebSocketClientDidReceiveDataParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            WebSocketClientDidReceiveDataParams result = new WebSocketClientDidReceiveDataParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.fin = decoder0.readBoolean(8, WebSocketClient_Internal.DID_CONNECT_ORDINAL);
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
            encoder0.encode(this.fin, 8, (int) WebSocketClient_Internal.DID_CONNECT_ORDINAL);
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
            WebSocketClientDidReceiveDataParams other = (WebSocketClientDidReceiveDataParams) object;
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

    static final class WebSocketClientDidReceiveFlowControlParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public long quota;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[WebSocketClient_Internal.DID_RECEIVE_DATA_ORDINAL];
            dataHeaderArr[WebSocketClient_Internal.DID_CONNECT_ORDINAL] = new DataHeader(STRUCT_SIZE, WebSocketClient_Internal.DID_CONNECT_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[WebSocketClient_Internal.DID_CONNECT_ORDINAL];
        }

        private WebSocketClientDidReceiveFlowControlParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public WebSocketClientDidReceiveFlowControlParams() {
            this(WebSocketClient_Internal.DID_CONNECT_ORDINAL);
        }

        public static WebSocketClientDidReceiveFlowControlParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static WebSocketClientDidReceiveFlowControlParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            WebSocketClientDidReceiveFlowControlParams result = new WebSocketClientDidReceiveFlowControlParams(mainDataHeader.elementsOrVersion);
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
            if (this.quota != ((WebSocketClientDidReceiveFlowControlParams) object).quota) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.quota);
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<WebSocketClient> {
        Stub(Core core, WebSocketClient impl) {
            super(core, impl);
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                MessageHeader header = messageWithHeader.getHeader();
                if (!header.validateHeader(WebSocketClient_Internal.DID_CONNECT_ORDINAL)) {
                    return false;
                }
                switch (header.getType()) {
                    case XWalkResourceClientInternal.ERROR_HOST_LOOKUP /*-2*/:
                        return InterfaceControlMessagesHelper.handleRunOrClosePipe(WebSocketClient_Internal.MANAGER, messageWithHeader);
                    case WebSocketClient_Internal.DID_CONNECT_ORDINAL /*0*/:
                        WebSocketClientDidConnectParams data = WebSocketClientDidConnectParams.deserialize(messageWithHeader.getPayload());
                        ((WebSocketClient) getImpl()).didConnect(data.selectedSubprotocol, data.extensions, data.receiveStream);
                        return true;
                    case WebSocketClient_Internal.DID_RECEIVE_DATA_ORDINAL /*1*/:
                        WebSocketClientDidReceiveDataParams data2 = WebSocketClientDidReceiveDataParams.deserialize(messageWithHeader.getPayload());
                        ((WebSocketClient) getImpl()).didReceiveData(data2.fin, data2.type, data2.numBytes);
                        return true;
                    case WebSocketClient_Internal.DID_RECEIVE_FLOW_CONTROL_ORDINAL /*2*/:
                        ((WebSocketClient) getImpl()).didReceiveFlowControl(WebSocketClientDidReceiveFlowControlParams.deserialize(messageWithHeader.getPayload()).quota);
                        return true;
                    case WebSocketClient_Internal.DID_FAIL_ORDINAL /*3*/:
                        ((WebSocketClient) getImpl()).didFail(WebSocketClientDidFailParams.deserialize(messageWithHeader.getPayload()).message);
                        return true;
                    case WebSocketClient_Internal.DID_CLOSE_ORDINAL /*4*/:
                        WebSocketClientDidCloseParams data3 = WebSocketClientDidCloseParams.deserialize(messageWithHeader.getPayload());
                        ((WebSocketClient) getImpl()).didClose(data3.wasClean, data3.code, data3.reason);
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
                if (header.validateHeader(WebSocketClient_Internal.DID_RECEIVE_DATA_ORDINAL)) {
                    switch (header.getType()) {
                        case ExploreByTouchHelper.HOST_ID /*-1*/:
                            z = InterfaceControlMessagesHelper.handleRun(getCore(), WebSocketClient_Internal.MANAGER, messageWithHeader, receiver);
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

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.WebSocketClient.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void didConnect(String selectedSubprotocol, String extensions, ConsumerHandle receiveStream) {
            WebSocketClientDidConnectParams _message = new WebSocketClientDidConnectParams();
            _message.selectedSubprotocol = selectedSubprotocol;
            _message.extensions = extensions;
            _message.receiveStream = receiveStream;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader((int) WebSocketClient_Internal.DID_CONNECT_ORDINAL)));
        }

        public void didReceiveData(boolean fin, int type, int numBytes) {
            WebSocketClientDidReceiveDataParams _message = new WebSocketClientDidReceiveDataParams();
            _message.fin = fin;
            _message.type = type;
            _message.numBytes = numBytes;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader((int) WebSocketClient_Internal.DID_RECEIVE_DATA_ORDINAL)));
        }

        public void didReceiveFlowControl(long quota) {
            WebSocketClientDidReceiveFlowControlParams _message = new WebSocketClientDidReceiveFlowControlParams();
            _message.quota = quota;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader((int) WebSocketClient_Internal.DID_RECEIVE_FLOW_CONTROL_ORDINAL)));
        }

        public void didFail(String message) {
            WebSocketClientDidFailParams _message = new WebSocketClientDidFailParams();
            _message.message = message;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader((int) WebSocketClient_Internal.DID_FAIL_ORDINAL)));
        }

        public void didClose(boolean wasClean, short code, String reason) {
            WebSocketClientDidCloseParams _message = new WebSocketClientDidCloseParams();
            _message.wasClean = wasClean;
            _message.code = code;
            _message.reason = reason;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader((int) WebSocketClient_Internal.DID_CLOSE_ORDINAL)));
        }
    }

    WebSocketClient_Internal() {
    }

    static {
        MANAGER = new C06371();
    }
}
