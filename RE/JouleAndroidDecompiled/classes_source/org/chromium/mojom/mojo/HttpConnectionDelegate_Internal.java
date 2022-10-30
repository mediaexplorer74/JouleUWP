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
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojom.mojo.HttpConnectionDelegate.OnReceivedRequestResponse;
import org.chromium.mojom.mojo.HttpConnectionDelegate.OnReceivedWebSocketRequestResponse;
import org.xwalk.core.internal.XWalkResourceClientInternal;

class HttpConnectionDelegate_Internal {
    public static final Manager<HttpConnectionDelegate, org.chromium.mojom.mojo.HttpConnectionDelegate.Proxy> MANAGER;
    private static final int ON_RECEIVED_REQUEST_ORDINAL = 0;
    private static final int ON_RECEIVED_WEB_SOCKET_REQUEST_ORDINAL = 1;

    /* renamed from: org.chromium.mojom.mojo.HttpConnectionDelegate_Internal.1 */
    static class C06181 extends Manager<HttpConnectionDelegate, org.chromium.mojom.mojo.HttpConnectionDelegate.Proxy> {
        C06181() {
        }

        public String getName() {
            return "mojo::HttpConnectionDelegate";
        }

        public int getVersion() {
            return HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, HttpConnectionDelegate impl) {
            return new Stub(core, impl);
        }

        public HttpConnectionDelegate[] buildArray(int size) {
            return new HttpConnectionDelegate[size];
        }
    }

    static final class HttpConnectionDelegateOnReceivedRequestParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public HttpRequest request;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[HttpConnectionDelegate_Internal.ON_RECEIVED_WEB_SOCKET_REQUEST_ORDINAL];
            dataHeaderArr[HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL] = new DataHeader(STRUCT_SIZE, HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL];
        }

        private HttpConnectionDelegateOnReceivedRequestParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public HttpConnectionDelegateOnReceivedRequestParams() {
            this(HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL);
        }

        public static HttpConnectionDelegateOnReceivedRequestParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static HttpConnectionDelegateOnReceivedRequestParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            HttpConnectionDelegateOnReceivedRequestParams result = new HttpConnectionDelegateOnReceivedRequestParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.request = HttpRequest.decode(decoder0.readPointer(8, false));
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.request, 8, false);
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
            if (BindingsHelper.equals(this.request, ((HttpConnectionDelegateOnReceivedRequestParams) object).request)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.request);
        }
    }

    static final class HttpConnectionDelegateOnReceivedRequestResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public HttpResponse response;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[HttpConnectionDelegate_Internal.ON_RECEIVED_WEB_SOCKET_REQUEST_ORDINAL];
            dataHeaderArr[HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL] = new DataHeader(STRUCT_SIZE, HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL];
        }

        private HttpConnectionDelegateOnReceivedRequestResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public HttpConnectionDelegateOnReceivedRequestResponseParams() {
            this(HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL);
        }

        public static HttpConnectionDelegateOnReceivedRequestResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static HttpConnectionDelegateOnReceivedRequestResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            HttpConnectionDelegateOnReceivedRequestResponseParams result = new HttpConnectionDelegateOnReceivedRequestResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.response = HttpResponse.decode(decoder0.readPointer(8, false));
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.response, 8, false);
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
            if (BindingsHelper.equals(this.response, ((HttpConnectionDelegateOnReceivedRequestResponseParams) object).response)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.response);
        }
    }

    /* renamed from: org.chromium.mojom.mojo.HttpConnectionDelegate_Internal.HttpConnectionDelegateOnReceivedRequestResponseParamsForwardToCallback */
    static class C0619xdb9d39fa extends SideEffectFreeCloseable implements MessageReceiver {
        private final OnReceivedRequestResponse mCallback;

        C0619xdb9d39fa(OnReceivedRequestResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL, 2)) {
                    return false;
                }
                this.mCallback.call(HttpConnectionDelegateOnReceivedRequestResponseParams.deserialize(messageWithHeader.getPayload()).response);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class HttpConnectionDelegateOnReceivedWebSocketRequestParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public HttpRequest request;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[HttpConnectionDelegate_Internal.ON_RECEIVED_WEB_SOCKET_REQUEST_ORDINAL];
            dataHeaderArr[HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL] = new DataHeader(STRUCT_SIZE, HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL];
        }

        private HttpConnectionDelegateOnReceivedWebSocketRequestParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public HttpConnectionDelegateOnReceivedWebSocketRequestParams() {
            this(HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL);
        }

        public static HttpConnectionDelegateOnReceivedWebSocketRequestParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static HttpConnectionDelegateOnReceivedWebSocketRequestParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            HttpConnectionDelegateOnReceivedWebSocketRequestParams result = new HttpConnectionDelegateOnReceivedWebSocketRequestParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.request = HttpRequest.decode(decoder0.readPointer(8, false));
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.request, 8, false);
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
            if (BindingsHelper.equals(this.request, ((HttpConnectionDelegateOnReceivedWebSocketRequestParams) object).request)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.request);
        }
    }

    static final class HttpConnectionDelegateOnReceivedWebSocketRequestResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public WebSocketClient client;
        public ConsumerHandle sendStream;
        public InterfaceRequest<WebSocket> webSocket;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[HttpConnectionDelegate_Internal.ON_RECEIVED_WEB_SOCKET_REQUEST_ORDINAL];
            dataHeaderArr[HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL] = new DataHeader(STRUCT_SIZE, HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL];
        }

        private HttpConnectionDelegateOnReceivedWebSocketRequestResponseParams(int version) {
            super(STRUCT_SIZE, version);
            this.sendStream = InvalidHandle.INSTANCE;
        }

        public HttpConnectionDelegateOnReceivedWebSocketRequestResponseParams() {
            this(HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL);
        }

        public static HttpConnectionDelegateOnReceivedWebSocketRequestResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static HttpConnectionDelegateOnReceivedWebSocketRequestResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            HttpConnectionDelegateOnReceivedWebSocketRequestResponseParams result = new HttpConnectionDelegateOnReceivedWebSocketRequestResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.webSocket = decoder0.readInterfaceRequest(8, true);
            }
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.sendStream = decoder0.readConsumerHandle(12, true);
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.client = (WebSocketClient) decoder0.readServiceInterface(16, true, WebSocketClient.MANAGER);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.webSocket, 8, true);
            encoder0.encode(this.sendStream, 12, true);
            encoder0.encode(this.client, 16, true, WebSocketClient.MANAGER);
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
            HttpConnectionDelegateOnReceivedWebSocketRequestResponseParams other = (HttpConnectionDelegateOnReceivedWebSocketRequestResponseParams) object;
            if (!BindingsHelper.equals(this.webSocket, other.webSocket)) {
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
            return ((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.webSocket)) * 31) + BindingsHelper.hashCode(this.sendStream)) * 31) + BindingsHelper.hashCode(this.client);
        }
    }

    /* renamed from: org.chromium.mojom.mojo.HttpConnectionDelegate_Internal.HttpConnectionDelegateOnReceivedWebSocketRequestResponseParamsForwardToCallback */
    static class C0620xcdbe284b extends SideEffectFreeCloseable implements MessageReceiver {
        private final OnReceivedWebSocketRequestResponse mCallback;

        C0620xcdbe284b(OnReceivedWebSocketRequestResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(HttpConnectionDelegate_Internal.ON_RECEIVED_WEB_SOCKET_REQUEST_ORDINAL, 2)) {
                    return false;
                }
                HttpConnectionDelegateOnReceivedWebSocketRequestResponseParams response = HttpConnectionDelegateOnReceivedWebSocketRequestResponseParams.deserialize(messageWithHeader.getPayload());
                this.mCallback.call(response.webSocket, response.sendStream, response.client);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    /* renamed from: org.chromium.mojom.mojo.HttpConnectionDelegate_Internal.HttpConnectionDelegateOnReceivedRequestResponseParamsProxyToResponder */
    static class C0679xe0e93c64 implements OnReceivedRequestResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        C0679xe0e93c64(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(HttpResponse response) {
            HttpConnectionDelegateOnReceivedRequestResponseParams _response = new HttpConnectionDelegateOnReceivedRequestResponseParams();
            _response.response = response;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL, 2, this.mRequestId)));
        }
    }

    /* renamed from: org.chromium.mojom.mojo.HttpConnectionDelegate_Internal.HttpConnectionDelegateOnReceivedWebSocketRequestResponseParamsProxyToResponder */
    static class C0680xe8b8bff3 implements OnReceivedWebSocketRequestResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        C0680xe8b8bff3(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(InterfaceRequest<WebSocket> webSocket, ConsumerHandle sendStream, WebSocketClient client) {
            HttpConnectionDelegateOnReceivedWebSocketRequestResponseParams _response = new HttpConnectionDelegateOnReceivedWebSocketRequestResponseParams();
            _response.webSocket = webSocket;
            _response.sendStream = sendStream;
            _response.client = client;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(HttpConnectionDelegate_Internal.ON_RECEIVED_WEB_SOCKET_REQUEST_ORDINAL, 2, this.mRequestId)));
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<HttpConnectionDelegate> {
        Stub(Core core, HttpConnectionDelegate impl) {
            super(core, impl);
        }

        public boolean accept(Message message) {
            boolean z = false;
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                MessageHeader header = messageWithHeader.getHeader();
                if (header.validateHeader(HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL)) {
                    switch (header.getType()) {
                        case XWalkResourceClientInternal.ERROR_HOST_LOOKUP /*-2*/:
                            z = InterfaceControlMessagesHelper.handleRunOrClosePipe(HttpConnectionDelegate_Internal.MANAGER, messageWithHeader);
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
                if (!header.validateHeader(HttpConnectionDelegate_Internal.ON_RECEIVED_WEB_SOCKET_REQUEST_ORDINAL)) {
                    return false;
                }
                switch (header.getType()) {
                    case ExploreByTouchHelper.HOST_ID /*-1*/:
                        return InterfaceControlMessagesHelper.handleRun(getCore(), HttpConnectionDelegate_Internal.MANAGER, messageWithHeader, receiver);
                    case HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL /*0*/:
                        ((HttpConnectionDelegate) getImpl()).onReceivedRequest(HttpConnectionDelegateOnReceivedRequestParams.deserialize(messageWithHeader.getPayload()).request, new C0679xe0e93c64(getCore(), receiver, header.getRequestId()));
                        return true;
                    case HttpConnectionDelegate_Internal.ON_RECEIVED_WEB_SOCKET_REQUEST_ORDINAL /*1*/:
                        ((HttpConnectionDelegate) getImpl()).onReceivedWebSocketRequest(HttpConnectionDelegateOnReceivedWebSocketRequestParams.deserialize(messageWithHeader.getPayload()).request, new C0680xe8b8bff3(getCore(), receiver, header.getRequestId()));
                        return true;
                    default:
                        return false;
                }
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.HttpConnectionDelegate.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void onReceivedRequest(HttpRequest request, OnReceivedRequestResponse callback) {
            HttpConnectionDelegateOnReceivedRequestParams _message = new HttpConnectionDelegateOnReceivedRequestParams();
            _message.request = request;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(HttpConnectionDelegate_Internal.ON_RECEIVED_REQUEST_ORDINAL, HttpConnectionDelegate_Internal.ON_RECEIVED_WEB_SOCKET_REQUEST_ORDINAL, 0)), new C0619xdb9d39fa(callback));
        }

        public void onReceivedWebSocketRequest(HttpRequest request, OnReceivedWebSocketRequestResponse callback) {
            HttpConnectionDelegateOnReceivedWebSocketRequestParams _message = new HttpConnectionDelegateOnReceivedWebSocketRequestParams();
            _message.request = request;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(HttpConnectionDelegate_Internal.ON_RECEIVED_WEB_SOCKET_REQUEST_ORDINAL, HttpConnectionDelegate_Internal.ON_RECEIVED_WEB_SOCKET_REQUEST_ORDINAL, 0)), new C0620xcdbe284b(callback));
        }
    }

    HttpConnectionDelegate_Internal() {
    }

    static {
        MANAGER = new C06181();
    }
}
