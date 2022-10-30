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
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.MessageHeader;
import org.chromium.mojo.bindings.MessageReceiver;
import org.chromium.mojo.bindings.MessageReceiverWithResponder;
import org.chromium.mojo.bindings.ServiceMessage;
import org.chromium.mojo.bindings.SideEffectFreeCloseable;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;
import org.chromium.mojom.mojo.HttpConnection.SetReceiveBufferSizeResponse;
import org.chromium.mojom.mojo.HttpConnection.SetSendBufferSizeResponse;
import org.xwalk.core.internal.XWalkResourceClientInternal;

class HttpConnection_Internal {
    public static final Manager<HttpConnection, org.chromium.mojom.mojo.HttpConnection.Proxy> MANAGER;
    private static final int SET_RECEIVE_BUFFER_SIZE_ORDINAL = 1;
    private static final int SET_SEND_BUFFER_SIZE_ORDINAL = 0;

    /* renamed from: org.chromium.mojom.mojo.HttpConnection_Internal.1 */
    static class C06211 extends Manager<HttpConnection, org.chromium.mojom.mojo.HttpConnection.Proxy> {
        C06211() {
        }

        public String getName() {
            return "mojo::HttpConnection";
        }

        public int getVersion() {
            return 0;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, HttpConnection impl) {
            return new Stub(core, impl);
        }

        public HttpConnection[] buildArray(int size) {
            return new HttpConnection[size];
        }
    }

    static final class HttpConnectionSetReceiveBufferSizeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public int size;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[HttpConnection_Internal.SET_RECEIVE_BUFFER_SIZE_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private HttpConnectionSetReceiveBufferSizeParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public HttpConnectionSetReceiveBufferSizeParams() {
            this(0);
        }

        public static HttpConnectionSetReceiveBufferSizeParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static HttpConnectionSetReceiveBufferSizeParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            HttpConnectionSetReceiveBufferSizeParams result = new HttpConnectionSetReceiveBufferSizeParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.size = decoder0.readInt(8);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.size, 8);
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
            if (this.size != ((HttpConnectionSetReceiveBufferSizeParams) object).size) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.size);
        }
    }

    static final class HttpConnectionSetReceiveBufferSizeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public NetworkError result;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[HttpConnection_Internal.SET_RECEIVE_BUFFER_SIZE_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private HttpConnectionSetReceiveBufferSizeResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public HttpConnectionSetReceiveBufferSizeResponseParams() {
            this(0);
        }

        public static HttpConnectionSetReceiveBufferSizeResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static HttpConnectionSetReceiveBufferSizeResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            HttpConnectionSetReceiveBufferSizeResponseParams result = new HttpConnectionSetReceiveBufferSizeResponseParams(mainDataHeader.elementsOrVersion);
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
            if (BindingsHelper.equals(this.result, ((HttpConnectionSetReceiveBufferSizeResponseParams) object).result)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = getClass().hashCode() + 31;
            return (result * 31) + BindingsHelper.hashCode(result);
        }
    }

    /* renamed from: org.chromium.mojom.mojo.HttpConnection_Internal.HttpConnectionSetReceiveBufferSizeResponseParamsForwardToCallback */
    static class C0622xe17f2ed6 extends SideEffectFreeCloseable implements MessageReceiver {
        private final SetReceiveBufferSizeResponse mCallback;

        C0622xe17f2ed6(SetReceiveBufferSizeResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(HttpConnection_Internal.SET_RECEIVE_BUFFER_SIZE_ORDINAL, 2)) {
                    return false;
                }
                this.mCallback.call(HttpConnectionSetReceiveBufferSizeResponseParams.deserialize(messageWithHeader.getPayload()).result);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class HttpConnectionSetSendBufferSizeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public int size;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[HttpConnection_Internal.SET_RECEIVE_BUFFER_SIZE_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private HttpConnectionSetSendBufferSizeParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public HttpConnectionSetSendBufferSizeParams() {
            this(0);
        }

        public static HttpConnectionSetSendBufferSizeParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static HttpConnectionSetSendBufferSizeParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            HttpConnectionSetSendBufferSizeParams result = new HttpConnectionSetSendBufferSizeParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.size = decoder0.readInt(8);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.size, 8);
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
            if (this.size != ((HttpConnectionSetSendBufferSizeParams) object).size) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.size);
        }
    }

    static final class HttpConnectionSetSendBufferSizeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public NetworkError result;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[HttpConnection_Internal.SET_RECEIVE_BUFFER_SIZE_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private HttpConnectionSetSendBufferSizeResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public HttpConnectionSetSendBufferSizeResponseParams() {
            this(0);
        }

        public static HttpConnectionSetSendBufferSizeResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static HttpConnectionSetSendBufferSizeResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            HttpConnectionSetSendBufferSizeResponseParams result = new HttpConnectionSetSendBufferSizeResponseParams(mainDataHeader.elementsOrVersion);
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
            if (BindingsHelper.equals(this.result, ((HttpConnectionSetSendBufferSizeResponseParams) object).result)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = getClass().hashCode() + 31;
            return (result * 31) + BindingsHelper.hashCode(result);
        }
    }

    static class HttpConnectionSetSendBufferSizeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final SetSendBufferSizeResponse mCallback;

        HttpConnectionSetSendBufferSizeResponseParamsForwardToCallback(SetSendBufferSizeResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(HttpConnectionSetSendBufferSizeResponseParams.deserialize(messageWithHeader.getPayload()).result);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static class HttpConnectionSetReceiveBufferSizeResponseParamsProxyToResponder implements SetReceiveBufferSizeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        HttpConnectionSetReceiveBufferSizeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(NetworkError result) {
            HttpConnectionSetReceiveBufferSizeResponseParams _response = new HttpConnectionSetReceiveBufferSizeResponseParams();
            _response.result = result;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(HttpConnection_Internal.SET_RECEIVE_BUFFER_SIZE_ORDINAL, 2, this.mRequestId)));
        }
    }

    static class HttpConnectionSetSendBufferSizeResponseParamsProxyToResponder implements SetSendBufferSizeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        HttpConnectionSetSendBufferSizeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(NetworkError result) {
            HttpConnectionSetSendBufferSizeResponseParams _response = new HttpConnectionSetSendBufferSizeResponseParams();
            _response.result = result;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<HttpConnection> {
        Stub(Core core, HttpConnection impl) {
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
                            z = InterfaceControlMessagesHelper.handleRunOrClosePipe(HttpConnection_Internal.MANAGER, messageWithHeader);
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
                if (!header.validateHeader(HttpConnection_Internal.SET_RECEIVE_BUFFER_SIZE_ORDINAL)) {
                    return false;
                }
                switch (header.getType()) {
                    case ExploreByTouchHelper.HOST_ID /*-1*/:
                        return InterfaceControlMessagesHelper.handleRun(getCore(), HttpConnection_Internal.MANAGER, messageWithHeader, receiver);
                    case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                        ((HttpConnection) getImpl()).setSendBufferSize(HttpConnectionSetSendBufferSizeParams.deserialize(messageWithHeader.getPayload()).size, new HttpConnectionSetSendBufferSizeResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    case HttpConnection_Internal.SET_RECEIVE_BUFFER_SIZE_ORDINAL /*1*/:
                        ((HttpConnection) getImpl()).setReceiveBufferSize(HttpConnectionSetReceiveBufferSizeParams.deserialize(messageWithHeader.getPayload()).size, new HttpConnectionSetReceiveBufferSizeResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    default:
                        return false;
                }
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.HttpConnection.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void setSendBufferSize(int size, SetSendBufferSizeResponse callback) {
            HttpConnectionSetSendBufferSizeParams _message = new HttpConnectionSetSendBufferSizeParams();
            _message.size = size;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, HttpConnection_Internal.SET_RECEIVE_BUFFER_SIZE_ORDINAL, 0)), new HttpConnectionSetSendBufferSizeResponseParamsForwardToCallback(callback));
        }

        public void setReceiveBufferSize(int size, SetReceiveBufferSizeResponse callback) {
            HttpConnectionSetReceiveBufferSizeParams _message = new HttpConnectionSetReceiveBufferSizeParams();
            _message.size = size;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(HttpConnection_Internal.SET_RECEIVE_BUFFER_SIZE_ORDINAL, HttpConnection_Internal.SET_RECEIVE_BUFFER_SIZE_ORDINAL, 0)), new C0622xe17f2ed6(callback));
        }
    }

    HttpConnection_Internal() {
    }

    static {
        MANAGER = new C06211();
    }
}
