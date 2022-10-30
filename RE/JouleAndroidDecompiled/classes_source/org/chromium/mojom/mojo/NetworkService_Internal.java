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
import org.chromium.mojom.mojo.NetworkService.CreateHttpServerResponse;
import org.chromium.mojom.mojo.NetworkService.CreateTcpBoundSocketResponse;
import org.chromium.mojom.mojo.NetworkService.CreateTcpConnectedSocketResponse;
import org.xwalk.core.internal.XWalkResourceClientInternal;

class NetworkService_Internal {
    private static final int CREATE_HTTP_SERVER_ORDINAL = 5;
    private static final int CREATE_TCP_BOUND_SOCKET_ORDINAL = 2;
    private static final int CREATE_TCP_CONNECTED_SOCKET_ORDINAL = 3;
    private static final int CREATE_UDP_SOCKET_ORDINAL = 4;
    private static final int CREATE_WEB_SOCKET_ORDINAL = 1;
    private static final int GET_COOKIE_STORE_ORDINAL = 0;
    public static final Manager<NetworkService, org.chromium.mojom.mojo.NetworkService.Proxy> MANAGER;

    /* renamed from: org.chromium.mojom.mojo.NetworkService_Internal.1 */
    static class C06241 extends Manager<NetworkService, org.chromium.mojom.mojo.NetworkService.Proxy> {
        C06241() {
        }

        public String getName() {
            return "mojo::NetworkService";
        }

        public int getVersion() {
            return 0;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, NetworkService impl) {
            return new Stub(core, impl);
        }

        public NetworkService[] buildArray(int size) {
            return new NetworkService[size];
        }
    }

    static final class NetworkServiceCreateHttpServerParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public HttpServerDelegate delegate;
        public NetAddress localAddress;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[NetworkService_Internal.CREATE_WEB_SOCKET_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private NetworkServiceCreateHttpServerParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public NetworkServiceCreateHttpServerParams() {
            this(0);
        }

        public static NetworkServiceCreateHttpServerParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static NetworkServiceCreateHttpServerParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            NetworkServiceCreateHttpServerParams result = new NetworkServiceCreateHttpServerParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.localAddress = NetAddress.decode(decoder0.readPointer(8, false));
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.delegate = (HttpServerDelegate) decoder0.readServiceInterface(16, false, HttpServerDelegate.MANAGER);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.localAddress, 8, false);
            encoder0.encode(this.delegate, 16, false, HttpServerDelegate.MANAGER);
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
            NetworkServiceCreateHttpServerParams other = (NetworkServiceCreateHttpServerParams) object;
            if (!BindingsHelper.equals(this.localAddress, other.localAddress)) {
                return false;
            }
            if (BindingsHelper.equals(this.delegate, other.delegate)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.localAddress)) * 31) + BindingsHelper.hashCode(this.delegate);
        }
    }

    static final class NetworkServiceCreateHttpServerResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public NetAddress boundTo;
        public NetworkError result;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[NetworkService_Internal.CREATE_WEB_SOCKET_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private NetworkServiceCreateHttpServerResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public NetworkServiceCreateHttpServerResponseParams() {
            this(0);
        }

        public static NetworkServiceCreateHttpServerResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static NetworkServiceCreateHttpServerResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            NetworkServiceCreateHttpServerResponseParams result = new NetworkServiceCreateHttpServerResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.result = NetworkError.decode(decoder0.readPointer(8, false));
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.boundTo = NetAddress.decode(decoder0.readPointer(16, true));
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.result, 8, false);
            encoder0.encode(this.boundTo, 16, true);
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
            NetworkServiceCreateHttpServerResponseParams other = (NetworkServiceCreateHttpServerResponseParams) object;
            if (!BindingsHelper.equals(this.result, other.result)) {
                return false;
            }
            if (BindingsHelper.equals(this.boundTo, other.boundTo)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = getClass().hashCode() + 31;
            return (((result * 31) + BindingsHelper.hashCode(result)) * 31) + BindingsHelper.hashCode(this.boundTo);
        }
    }

    static class NetworkServiceCreateHttpServerResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CreateHttpServerResponse mCallback;

        NetworkServiceCreateHttpServerResponseParamsForwardToCallback(CreateHttpServerResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(NetworkService_Internal.CREATE_HTTP_SERVER_ORDINAL, NetworkService_Internal.CREATE_TCP_BOUND_SOCKET_ORDINAL)) {
                    return false;
                }
                NetworkServiceCreateHttpServerResponseParams response = NetworkServiceCreateHttpServerResponseParams.deserialize(messageWithHeader.getPayload());
                this.mCallback.call(response.result, response.boundTo);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class NetworkServiceCreateTcpBoundSocketParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<TcpBoundSocket> boundSocket;
        public NetAddress localAddress;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[NetworkService_Internal.CREATE_WEB_SOCKET_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private NetworkServiceCreateTcpBoundSocketParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public NetworkServiceCreateTcpBoundSocketParams() {
            this(0);
        }

        public static NetworkServiceCreateTcpBoundSocketParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static NetworkServiceCreateTcpBoundSocketParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            NetworkServiceCreateTcpBoundSocketParams result = new NetworkServiceCreateTcpBoundSocketParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.localAddress = NetAddress.decode(decoder0.readPointer(8, true));
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.boundSocket = decoder0.readInterfaceRequest(16, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.localAddress, 8, true);
            encoder0.encode(this.boundSocket, 16, false);
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
            NetworkServiceCreateTcpBoundSocketParams other = (NetworkServiceCreateTcpBoundSocketParams) object;
            if (!BindingsHelper.equals(this.localAddress, other.localAddress)) {
                return false;
            }
            if (BindingsHelper.equals(this.boundSocket, other.boundSocket)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.localAddress)) * 31) + BindingsHelper.hashCode(this.boundSocket);
        }
    }

    static final class NetworkServiceCreateTcpBoundSocketResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public NetAddress boundTo;
        public NetworkError result;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[NetworkService_Internal.CREATE_WEB_SOCKET_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private NetworkServiceCreateTcpBoundSocketResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public NetworkServiceCreateTcpBoundSocketResponseParams() {
            this(0);
        }

        public static NetworkServiceCreateTcpBoundSocketResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static NetworkServiceCreateTcpBoundSocketResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            NetworkServiceCreateTcpBoundSocketResponseParams result = new NetworkServiceCreateTcpBoundSocketResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.result = NetworkError.decode(decoder0.readPointer(8, false));
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.boundTo = NetAddress.decode(decoder0.readPointer(16, true));
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.result, 8, false);
            encoder0.encode(this.boundTo, 16, true);
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
            NetworkServiceCreateTcpBoundSocketResponseParams other = (NetworkServiceCreateTcpBoundSocketResponseParams) object;
            if (!BindingsHelper.equals(this.result, other.result)) {
                return false;
            }
            if (BindingsHelper.equals(this.boundTo, other.boundTo)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = getClass().hashCode() + 31;
            return (((result * 31) + BindingsHelper.hashCode(result)) * 31) + BindingsHelper.hashCode(this.boundTo);
        }
    }

    /* renamed from: org.chromium.mojom.mojo.NetworkService_Internal.NetworkServiceCreateTcpBoundSocketResponseParamsForwardToCallback */
    static class C0625x34c43c2b extends SideEffectFreeCloseable implements MessageReceiver {
        private final CreateTcpBoundSocketResponse mCallback;

        C0625x34c43c2b(CreateTcpBoundSocketResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(NetworkService_Internal.CREATE_TCP_BOUND_SOCKET_ORDINAL, NetworkService_Internal.CREATE_TCP_BOUND_SOCKET_ORDINAL)) {
                    return false;
                }
                NetworkServiceCreateTcpBoundSocketResponseParams response = NetworkServiceCreateTcpBoundSocketResponseParams.deserialize(messageWithHeader.getPayload());
                this.mCallback.call(response.result, response.boundTo);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class NetworkServiceCreateTcpConnectedSocketParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 32;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<TcpConnectedSocket> clientSocket;
        public ProducerHandle receiveStream;
        public NetAddress remoteAddress;
        public ConsumerHandle sendStream;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[NetworkService_Internal.CREATE_WEB_SOCKET_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private NetworkServiceCreateTcpConnectedSocketParams(int version) {
            super(STRUCT_SIZE, version);
            this.sendStream = InvalidHandle.INSTANCE;
            this.receiveStream = InvalidHandle.INSTANCE;
        }

        public NetworkServiceCreateTcpConnectedSocketParams() {
            this(0);
        }

        public static NetworkServiceCreateTcpConnectedSocketParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static NetworkServiceCreateTcpConnectedSocketParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            NetworkServiceCreateTcpConnectedSocketParams result = new NetworkServiceCreateTcpConnectedSocketParams(mainDataHeader.elementsOrVersion);
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
            NetworkServiceCreateTcpConnectedSocketParams other = (NetworkServiceCreateTcpConnectedSocketParams) object;
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

    static final class NetworkServiceCreateTcpConnectedSocketResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public NetAddress localAddress;
        public NetworkError result;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[NetworkService_Internal.CREATE_WEB_SOCKET_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private NetworkServiceCreateTcpConnectedSocketResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public NetworkServiceCreateTcpConnectedSocketResponseParams() {
            this(0);
        }

        public static NetworkServiceCreateTcpConnectedSocketResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static NetworkServiceCreateTcpConnectedSocketResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            NetworkServiceCreateTcpConnectedSocketResponseParams result = new NetworkServiceCreateTcpConnectedSocketResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.result = NetworkError.decode(decoder0.readPointer(8, false));
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.localAddress = NetAddress.decode(decoder0.readPointer(16, true));
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.result, 8, false);
            encoder0.encode(this.localAddress, 16, true);
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
            NetworkServiceCreateTcpConnectedSocketResponseParams other = (NetworkServiceCreateTcpConnectedSocketResponseParams) object;
            if (!BindingsHelper.equals(this.result, other.result)) {
                return false;
            }
            if (BindingsHelper.equals(this.localAddress, other.localAddress)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = getClass().hashCode() + 31;
            return (((result * 31) + BindingsHelper.hashCode(result)) * 31) + BindingsHelper.hashCode(this.localAddress);
        }
    }

    /* renamed from: org.chromium.mojom.mojo.NetworkService_Internal.NetworkServiceCreateTcpConnectedSocketResponseParamsForwardToCallback */
    static class C0626x40ed8e80 extends SideEffectFreeCloseable implements MessageReceiver {
        private final CreateTcpConnectedSocketResponse mCallback;

        C0626x40ed8e80(CreateTcpConnectedSocketResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(NetworkService_Internal.CREATE_TCP_CONNECTED_SOCKET_ORDINAL, NetworkService_Internal.CREATE_TCP_BOUND_SOCKET_ORDINAL)) {
                    return false;
                }
                NetworkServiceCreateTcpConnectedSocketResponseParams response = NetworkServiceCreateTcpConnectedSocketResponseParams.deserialize(messageWithHeader.getPayload());
                this.mCallback.call(response.result, response.localAddress);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class NetworkServiceCreateUdpSocketParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<UdpSocket> socket;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[NetworkService_Internal.CREATE_WEB_SOCKET_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private NetworkServiceCreateUdpSocketParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public NetworkServiceCreateUdpSocketParams() {
            this(0);
        }

        public static NetworkServiceCreateUdpSocketParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static NetworkServiceCreateUdpSocketParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            NetworkServiceCreateUdpSocketParams result = new NetworkServiceCreateUdpSocketParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.socket = decoder0.readInterfaceRequest(8, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.socket, 8, false);
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
            if (BindingsHelper.equals(this.socket, ((NetworkServiceCreateUdpSocketParams) object).socket)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.socket);
        }
    }

    static final class NetworkServiceCreateWebSocketParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<WebSocket> socket;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[NetworkService_Internal.CREATE_WEB_SOCKET_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private NetworkServiceCreateWebSocketParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public NetworkServiceCreateWebSocketParams() {
            this(0);
        }

        public static NetworkServiceCreateWebSocketParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static NetworkServiceCreateWebSocketParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            NetworkServiceCreateWebSocketParams result = new NetworkServiceCreateWebSocketParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.socket = decoder0.readInterfaceRequest(8, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.socket, 8, false);
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
            if (BindingsHelper.equals(this.socket, ((NetworkServiceCreateWebSocketParams) object).socket)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.socket);
        }
    }

    static final class NetworkServiceGetCookieStoreParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<CookieStore> cookieStore;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[NetworkService_Internal.CREATE_WEB_SOCKET_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private NetworkServiceGetCookieStoreParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public NetworkServiceGetCookieStoreParams() {
            this(0);
        }

        public static NetworkServiceGetCookieStoreParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static NetworkServiceGetCookieStoreParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            NetworkServiceGetCookieStoreParams result = new NetworkServiceGetCookieStoreParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.cookieStore = decoder0.readInterfaceRequest(8, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.cookieStore, 8, false);
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
            if (BindingsHelper.equals(this.cookieStore, ((NetworkServiceGetCookieStoreParams) object).cookieStore)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.cookieStore);
        }
    }

    static class NetworkServiceCreateHttpServerResponseParamsProxyToResponder implements CreateHttpServerResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NetworkServiceCreateHttpServerResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(NetworkError result, NetAddress boundTo) {
            NetworkServiceCreateHttpServerResponseParams _response = new NetworkServiceCreateHttpServerResponseParams();
            _response.result = result;
            _response.boundTo = boundTo;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(NetworkService_Internal.CREATE_HTTP_SERVER_ORDINAL, NetworkService_Internal.CREATE_TCP_BOUND_SOCKET_ORDINAL, this.mRequestId)));
        }
    }

    static class NetworkServiceCreateTcpBoundSocketResponseParamsProxyToResponder implements CreateTcpBoundSocketResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        NetworkServiceCreateTcpBoundSocketResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(NetworkError result, NetAddress boundTo) {
            NetworkServiceCreateTcpBoundSocketResponseParams _response = new NetworkServiceCreateTcpBoundSocketResponseParams();
            _response.result = result;
            _response.boundTo = boundTo;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(NetworkService_Internal.CREATE_TCP_BOUND_SOCKET_ORDINAL, NetworkService_Internal.CREATE_TCP_BOUND_SOCKET_ORDINAL, this.mRequestId)));
        }
    }

    /* renamed from: org.chromium.mojom.mojo.NetworkService_Internal.NetworkServiceCreateTcpConnectedSocketResponseParamsProxyToResponder */
    static class C0681x91993f1e implements CreateTcpConnectedSocketResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        C0681x91993f1e(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(NetworkError result, NetAddress localAddress) {
            NetworkServiceCreateTcpConnectedSocketResponseParams _response = new NetworkServiceCreateTcpConnectedSocketResponseParams();
            _response.result = result;
            _response.localAddress = localAddress;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(NetworkService_Internal.CREATE_TCP_CONNECTED_SOCKET_ORDINAL, NetworkService_Internal.CREATE_TCP_BOUND_SOCKET_ORDINAL, this.mRequestId)));
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<NetworkService> {
        Stub(Core core, NetworkService impl) {
            super(core, impl);
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                MessageHeader header = messageWithHeader.getHeader();
                if (!header.validateHeader(0)) {
                    return false;
                }
                switch (header.getType()) {
                    case XWalkResourceClientInternal.ERROR_HOST_LOOKUP /*-2*/:
                        return InterfaceControlMessagesHelper.handleRunOrClosePipe(NetworkService_Internal.MANAGER, messageWithHeader);
                    case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                        ((NetworkService) getImpl()).getCookieStore(NetworkServiceGetCookieStoreParams.deserialize(messageWithHeader.getPayload()).cookieStore);
                        return true;
                    case NetworkService_Internal.CREATE_WEB_SOCKET_ORDINAL /*1*/:
                        ((NetworkService) getImpl()).createWebSocket(NetworkServiceCreateWebSocketParams.deserialize(messageWithHeader.getPayload()).socket);
                        return true;
                    case NetworkService_Internal.CREATE_UDP_SOCKET_ORDINAL /*4*/:
                        ((NetworkService) getImpl()).createUdpSocket(NetworkServiceCreateUdpSocketParams.deserialize(messageWithHeader.getPayload()).socket);
                        return true;
                    default:
                        return false;
                }
            } catch (DeserializationException e) {
                return false;
            }
        }

        public boolean acceptWithResponder(Message message, MessageReceiver receiver) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                MessageHeader header = messageWithHeader.getHeader();
                if (!header.validateHeader(NetworkService_Internal.CREATE_WEB_SOCKET_ORDINAL)) {
                    return false;
                }
                switch (header.getType()) {
                    case ExploreByTouchHelper.HOST_ID /*-1*/:
                        return InterfaceControlMessagesHelper.handleRun(getCore(), NetworkService_Internal.MANAGER, messageWithHeader, receiver);
                    case NetworkService_Internal.CREATE_TCP_BOUND_SOCKET_ORDINAL /*2*/:
                        NetworkServiceCreateTcpBoundSocketParams data = NetworkServiceCreateTcpBoundSocketParams.deserialize(messageWithHeader.getPayload());
                        ((NetworkService) getImpl()).createTcpBoundSocket(data.localAddress, data.boundSocket, new NetworkServiceCreateTcpBoundSocketResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    case NetworkService_Internal.CREATE_TCP_CONNECTED_SOCKET_ORDINAL /*3*/:
                        NetworkServiceCreateTcpConnectedSocketParams data2 = NetworkServiceCreateTcpConnectedSocketParams.deserialize(messageWithHeader.getPayload());
                        ((NetworkService) getImpl()).createTcpConnectedSocket(data2.remoteAddress, data2.sendStream, data2.receiveStream, data2.clientSocket, new C0681x91993f1e(getCore(), receiver, header.getRequestId()));
                        return true;
                    case NetworkService_Internal.CREATE_HTTP_SERVER_ORDINAL /*5*/:
                        NetworkServiceCreateHttpServerParams data3 = NetworkServiceCreateHttpServerParams.deserialize(messageWithHeader.getPayload());
                        ((NetworkService) getImpl()).createHttpServer(data3.localAddress, data3.delegate, new NetworkServiceCreateHttpServerResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    default:
                        return false;
                }
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.NetworkService.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void getCookieStore(InterfaceRequest<CookieStore> cookieStore) {
            NetworkServiceGetCookieStoreParams _message = new NetworkServiceGetCookieStoreParams();
            _message.cookieStore = cookieStore;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        public void createWebSocket(InterfaceRequest<WebSocket> socket) {
            NetworkServiceCreateWebSocketParams _message = new NetworkServiceCreateWebSocketParams();
            _message.socket = socket;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader((int) NetworkService_Internal.CREATE_WEB_SOCKET_ORDINAL)));
        }

        public void createTcpBoundSocket(NetAddress localAddress, InterfaceRequest<TcpBoundSocket> boundSocket, CreateTcpBoundSocketResponse callback) {
            NetworkServiceCreateTcpBoundSocketParams _message = new NetworkServiceCreateTcpBoundSocketParams();
            _message.localAddress = localAddress;
            _message.boundSocket = boundSocket;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(NetworkService_Internal.CREATE_TCP_BOUND_SOCKET_ORDINAL, NetworkService_Internal.CREATE_WEB_SOCKET_ORDINAL, 0)), new C0625x34c43c2b(callback));
        }

        public void createTcpConnectedSocket(NetAddress remoteAddress, ConsumerHandle sendStream, ProducerHandle receiveStream, InterfaceRequest<TcpConnectedSocket> clientSocket, CreateTcpConnectedSocketResponse callback) {
            NetworkServiceCreateTcpConnectedSocketParams _message = new NetworkServiceCreateTcpConnectedSocketParams();
            _message.remoteAddress = remoteAddress;
            _message.sendStream = sendStream;
            _message.receiveStream = receiveStream;
            _message.clientSocket = clientSocket;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(NetworkService_Internal.CREATE_TCP_CONNECTED_SOCKET_ORDINAL, NetworkService_Internal.CREATE_WEB_SOCKET_ORDINAL, 0)), new C0626x40ed8e80(callback));
        }

        public void createUdpSocket(InterfaceRequest<UdpSocket> socket) {
            NetworkServiceCreateUdpSocketParams _message = new NetworkServiceCreateUdpSocketParams();
            _message.socket = socket;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader((int) NetworkService_Internal.CREATE_UDP_SOCKET_ORDINAL)));
        }

        public void createHttpServer(NetAddress localAddress, HttpServerDelegate delegate, CreateHttpServerResponse callback) {
            NetworkServiceCreateHttpServerParams _message = new NetworkServiceCreateHttpServerParams();
            _message.localAddress = localAddress;
            _message.delegate = delegate;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(NetworkService_Internal.CREATE_HTTP_SERVER_ORDINAL, NetworkService_Internal.CREATE_WEB_SOCKET_ORDINAL, 0)), new NetworkServiceCreateHttpServerResponseParamsForwardToCallback(callback));
        }
    }

    NetworkService_Internal() {
    }

    static {
        MANAGER = new C06241();
    }
}
