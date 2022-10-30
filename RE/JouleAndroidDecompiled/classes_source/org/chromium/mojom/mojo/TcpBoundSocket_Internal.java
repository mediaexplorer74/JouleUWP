package org.chromium.mojom.mojo;

import android.support.v4.widget.ExploreByTouchHelper;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.DeserializationException;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Interface.AbstractProxy;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.bindings.InterfaceControlMessagesHelper;
import org.chromium.mojo.bindings.InterfaceRequest;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.MessageHeader;
import org.chromium.mojo.bindings.MessageReceiver;
import org.chromium.mojo.bindings.MessageReceiverWithResponder;
import org.chromium.mojo.bindings.ServiceMessage;
import org.chromium.mojo.bindings.SideEffectFreeCloseable;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;
import org.chromium.mojo.system.DataPipe.ConsumerHandle;
import org.chromium.mojo.system.DataPipe.ProducerHandle;
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojom.mojo.TcpBoundSocket.ConnectResponse;
import org.chromium.mojom.mojo.TcpBoundSocket.StartListeningResponse;
import org.xwalk.core.internal.XWalkResourceClientInternal;

class TcpBoundSocket_Internal {
    private static final int CONNECT_ORDINAL = 1;
    public static final Manager<TcpBoundSocket, org.chromium.mojom.mojo.TcpBoundSocket.Proxy> MANAGER;
    private static final int START_LISTENING_ORDINAL = 0;

    /* renamed from: org.chromium.mojom.mojo.TcpBoundSocket_Internal.1 */
    static class C06291 extends Manager<TcpBoundSocket, org.chromium.mojom.mojo.TcpBoundSocket.Proxy> {
        C06291() {
        }

        public String getName() {
            return "mojo::TCPBoundSocket";
        }

        public int getVersion() {
            return 0;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, TcpBoundSocket impl) {
            return new Stub(core, impl);
        }

        public TcpBoundSocket[] buildArray(int size) {
            return new TcpBoundSocket[size];
        }
    }

    static final class TcpBoundSocketConnectParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 32;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<TcpConnectedSocket> clientSocket;
        public ProducerHandle receiveStream;
        public NetAddress remoteAddress;
        public ConsumerHandle sendStream;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[TcpBoundSocket_Internal.CONNECT_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private TcpBoundSocketConnectParams(int version) {
            super(STRUCT_SIZE, version);
            this.sendStream = InvalidHandle.INSTANCE;
            this.receiveStream = InvalidHandle.INSTANCE;
        }

        public TcpBoundSocketConnectParams() {
            this(0);
        }

        public static TcpBoundSocketConnectParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static TcpBoundSocketConnectParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            TcpBoundSocketConnectParams result = new TcpBoundSocketConnectParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.remoteAddress = NetAddress.decode(decoder0.readPointer(8, false));
            }
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.sendStream = decoder0.readConsumerHandle(16, false);
            }
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.receiveStream = decoder0.readProducerHandle(20, false);
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.clientSocket = decoder0.readInterfaceRequest(24, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.remoteAddress, 8, false);
            encoder0.encode(this.sendStream, 16, false);
            encoder0.encode(this.receiveStream, 20, false);
            encoder0.encode(this.clientSocket, 24, false);
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
            TcpBoundSocketConnectParams other = (TcpBoundSocketConnectParams) object;
            if (!BindingsHelper.equals(this.remoteAddress, other.remoteAddress)) {
                return false;
            }
            if (!BindingsHelper.equals(this.sendStream, other.sendStream)) {
                return false;
            }
            if (!BindingsHelper.equals(this.receiveStream, other.receiveStream)) {
                return false;
            }
            if (BindingsHelper.equals(this.clientSocket, other.clientSocket)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.remoteAddress)) * 31) + BindingsHelper.hashCode(this.sendStream)) * 31) + BindingsHelper.hashCode(this.receiveStream)) * 31) + BindingsHelper.hashCode(this.clientSocket);
        }
    }

    static final class TcpBoundSocketConnectResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public NetworkError result;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[TcpBoundSocket_Internal.CONNECT_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private TcpBoundSocketConnectResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public TcpBoundSocketConnectResponseParams() {
            this(0);
        }

        public static TcpBoundSocketConnectResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static TcpBoundSocketConnectResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            TcpBoundSocketConnectResponseParams result = new TcpBoundSocketConnectResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.result = NetworkError.decode(decoder0.readPointer(8, false));
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8, false);
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
            if (BindingsHelper.equals(this.result, ((TcpBoundSocketConnectResponseParams) object).result)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = getClass().hashCode() + 31;
            return (result * 31) + BindingsHelper.hashCode(result);
        }
    }

    static class TcpBoundSocketConnectResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ConnectResponse mCallback;

        TcpBoundSocketConnectResponseParamsForwardToCallback(ConnectResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(TcpBoundSocket_Internal.CONNECT_ORDINAL, 2)) {
                    return false;
                }
                this.mCallback.call(TcpBoundSocketConnectResponseParams.deserialize(messageWithHeader.getPayload()).result);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class TcpBoundSocketStartListeningParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<TcpServerSocket> server;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[TcpBoundSocket_Internal.CONNECT_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private TcpBoundSocketStartListeningParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public TcpBoundSocketStartListeningParams() {
            this(0);
        }

        public static TcpBoundSocketStartListeningParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static TcpBoundSocketStartListeningParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            TcpBoundSocketStartListeningParams result = new TcpBoundSocketStartListeningParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.server = decoder0.readInterfaceRequest(8, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.server, 8, false);
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
            if (BindingsHelper.equals(this.server, ((TcpBoundSocketStartListeningParams) object).server)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.server);
        }
    }

    static final class TcpBoundSocketStartListeningResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public NetworkError result;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[TcpBoundSocket_Internal.CONNECT_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private TcpBoundSocketStartListeningResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public TcpBoundSocketStartListeningResponseParams() {
            this(0);
        }

        public static TcpBoundSocketStartListeningResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static TcpBoundSocketStartListeningResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            TcpBoundSocketStartListeningResponseParams result = new TcpBoundSocketStartListeningResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.result = NetworkError.decode(decoder0.readPointer(8, false));
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8, false);
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
            if (BindingsHelper.equals(this.result, ((TcpBoundSocketStartListeningResponseParams) object).result)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = getClass().hashCode() + 31;
            return (result * 31) + BindingsHelper.hashCode(result);
        }
    }

    static class TcpBoundSocketStartListeningResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final StartListeningResponse mCallback;

        TcpBoundSocketStartListeningResponseParamsForwardToCallback(StartListeningResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(TcpBoundSocketStartListeningResponseParams.deserialize(messageWithHeader.getPayload()).result);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static class TcpBoundSocketConnectResponseParamsProxyToResponder implements ConnectResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        TcpBoundSocketConnectResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(NetworkError result) {
            TcpBoundSocketConnectResponseParams _response = new TcpBoundSocketConnectResponseParams();
            _response.result = result;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(TcpBoundSocket_Internal.CONNECT_ORDINAL, 2, this.mRequestId)));
        }
    }

    static class TcpBoundSocketStartListeningResponseParamsProxyToResponder implements StartListeningResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        TcpBoundSocketStartListeningResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(NetworkError result) {
            TcpBoundSocketStartListeningResponseParams _response = new TcpBoundSocketStartListeningResponseParams();
            _response.result = result;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<TcpBoundSocket> {
        Stub(Core core, TcpBoundSocket impl) {
            super(core, impl);
        }

        public boolean accept(Message message) {
            boolean z = false;
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                MessageHeader header = messageWithHeader.getHeader();
                if (header.validateHeader(0)) {
                    switch (header.getType()) {
                        case XWalkResourceClientInternal.ERROR_HOST_LOOKUP /*-2*/:
                            z = InterfaceControlMessagesHelper.handleRunOrClosePipe(TcpBoundSocket_Internal.MANAGER, messageWithHeader);
                            break;
                        default:
                            break;
                    }
                }
            } catch (DeserializationException e) {
            }
            return z;
        }

        public boolean acceptWithResponder(Message message, MessageReceiver receiver) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                MessageHeader header = messageWithHeader.getHeader();
                if (!header.validateHeader(TcpBoundSocket_Internal.CONNECT_ORDINAL)) {
                    return false;
                }
                switch (header.getType()) {
                    case ExploreByTouchHelper.HOST_ID /*-1*/:
                        return InterfaceControlMessagesHelper.handleRun(getCore(), TcpBoundSocket_Internal.MANAGER, messageWithHeader, receiver);
                    case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                        ((TcpBoundSocket) getImpl()).startListening(TcpBoundSocketStartListeningParams.deserialize(messageWithHeader.getPayload()).server, new TcpBoundSocketStartListeningResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    case TcpBoundSocket_Internal.CONNECT_ORDINAL /*1*/:
                        TcpBoundSocketConnectParams data = TcpBoundSocketConnectParams.deserialize(messageWithHeader.getPayload());
                        ((TcpBoundSocket) getImpl()).connect(data.remoteAddress, data.sendStream, data.receiveStream, data.clientSocket, new TcpBoundSocketConnectResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    default:
                        return false;
                }
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.TcpBoundSocket.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void startListening(InterfaceRequest<TcpServerSocket> server, StartListeningResponse callback) {
            TcpBoundSocketStartListeningParams _message = new TcpBoundSocketStartListeningParams();
            _message.server = server;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, TcpBoundSocket_Internal.CONNECT_ORDINAL, 0)), new TcpBoundSocketStartListeningResponseParamsForwardToCallback(callback));
        }

        public void connect(NetAddress remoteAddress, ConsumerHandle sendStream, ProducerHandle receiveStream, InterfaceRequest<TcpConnectedSocket> clientSocket, ConnectResponse callback) {
            TcpBoundSocketConnectParams _message = new TcpBoundSocketConnectParams();
            _message.remoteAddress = remoteAddress;
            _message.sendStream = sendStream;
            _message.receiveStream = receiveStream;
            _message.clientSocket = clientSocket;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(TcpBoundSocket_Internal.CONNECT_ORDINAL, TcpBoundSocket_Internal.CONNECT_ORDINAL, 0)), new TcpBoundSocketConnectResponseParamsForwardToCallback(callback));
        }
    }

    TcpBoundSocket_Internal() {
    }

    static {
        MANAGER = new C06291();
    }
}
