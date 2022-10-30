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
import org.chromium.mojo.bindings.SideEffectFreeCloseable;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;
import org.chromium.mojom.mojo.CookieStore.GetResponse;
import org.chromium.mojom.mojo.CookieStore.SetResponse;
import org.xwalk.core.internal.XWalkResourceClientInternal;

class CookieStore_Internal {
    private static final int GET_ORDINAL = 0;
    public static final Manager<CookieStore, org.chromium.mojom.mojo.CookieStore.Proxy> MANAGER;
    private static final int SET_ORDINAL = 1;

    /* renamed from: org.chromium.mojom.mojo.CookieStore_Internal.1 */
    static class C06171 extends Manager<CookieStore, org.chromium.mojom.mojo.CookieStore.Proxy> {
        C06171() {
        }

        public String getName() {
            return "mojo::CookieStore";
        }

        public int getVersion() {
            return CookieStore_Internal.GET_ORDINAL;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, CookieStore impl) {
            return new Stub(core, impl);
        }

        public CookieStore[] buildArray(int size) {
            return new CookieStore[size];
        }
    }

    static final class CookieStoreGetParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public String url;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[CookieStore_Internal.SET_ORDINAL];
            dataHeaderArr[CookieStore_Internal.GET_ORDINAL] = new DataHeader(STRUCT_SIZE, CookieStore_Internal.GET_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[CookieStore_Internal.GET_ORDINAL];
        }

        private CookieStoreGetParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public CookieStoreGetParams() {
            this(CookieStore_Internal.GET_ORDINAL);
        }

        public static CookieStoreGetParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static CookieStoreGetParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            CookieStoreGetParams result = new CookieStoreGetParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.url = decoder0.readString(8, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.url, 8, false);
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
            if (BindingsHelper.equals(this.url, ((CookieStoreGetParams) object).url)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.url);
        }
    }

    static final class CookieStoreGetResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public String cookies;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[CookieStore_Internal.SET_ORDINAL];
            dataHeaderArr[CookieStore_Internal.GET_ORDINAL] = new DataHeader(STRUCT_SIZE, CookieStore_Internal.GET_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[CookieStore_Internal.GET_ORDINAL];
        }

        private CookieStoreGetResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public CookieStoreGetResponseParams() {
            this(CookieStore_Internal.GET_ORDINAL);
        }

        public static CookieStoreGetResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static CookieStoreGetResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            CookieStoreGetResponseParams result = new CookieStoreGetResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.cookies = decoder0.readString(8, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.cookies, 8, false);
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
            if (BindingsHelper.equals(this.cookies, ((CookieStoreGetResponseParams) object).cookies)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.cookies);
        }
    }

    static class CookieStoreGetResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final GetResponse mCallback;

        CookieStoreGetResponseParamsForwardToCallback(GetResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(CookieStore_Internal.GET_ORDINAL, 2)) {
                    return false;
                }
                this.mCallback.call(CookieStoreGetResponseParams.deserialize(messageWithHeader.getPayload()).cookies);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class CookieStoreSetParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public String cookie;
        public String url;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[CookieStore_Internal.SET_ORDINAL];
            dataHeaderArr[CookieStore_Internal.GET_ORDINAL] = new DataHeader(STRUCT_SIZE, CookieStore_Internal.GET_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[CookieStore_Internal.GET_ORDINAL];
        }

        private CookieStoreSetParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public CookieStoreSetParams() {
            this(CookieStore_Internal.GET_ORDINAL);
        }

        public static CookieStoreSetParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static CookieStoreSetParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            CookieStoreSetParams result = new CookieStoreSetParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.url = decoder0.readString(8, false);
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.cookie = decoder0.readString(16, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.url, 8, false);
            encoder0.encode(this.cookie, 16, false);
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
            CookieStoreSetParams other = (CookieStoreSetParams) object;
            if (!BindingsHelper.equals(this.url, other.url)) {
                return false;
            }
            if (BindingsHelper.equals(this.cookie, other.cookie)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.url)) * 31) + BindingsHelper.hashCode(this.cookie);
        }
    }

    static final class CookieStoreSetResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[CookieStore_Internal.SET_ORDINAL];
            dataHeaderArr[CookieStore_Internal.GET_ORDINAL] = new DataHeader(STRUCT_SIZE, CookieStore_Internal.GET_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[CookieStore_Internal.GET_ORDINAL];
        }

        private CookieStoreSetResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public CookieStoreSetResponseParams() {
            this(CookieStore_Internal.GET_ORDINAL);
        }

        public static CookieStoreSetResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static CookieStoreSetResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            CookieStoreSetResponseParams result = new CookieStoreSetResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.success = decoder0.readBoolean(8, CookieStore_Internal.GET_ORDINAL);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.success, 8, (int) CookieStore_Internal.GET_ORDINAL);
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
            if (this.success != ((CookieStoreSetResponseParams) object).success) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.success);
        }
    }

    static class CookieStoreSetResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final SetResponse mCallback;

        CookieStoreSetResponseParamsForwardToCallback(SetResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(CookieStore_Internal.SET_ORDINAL, 2)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(CookieStoreSetResponseParams.deserialize(messageWithHeader.getPayload()).success));
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static class CookieStoreGetResponseParamsProxyToResponder implements GetResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CookieStoreGetResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(String cookies) {
            CookieStoreGetResponseParams _response = new CookieStoreGetResponseParams();
            _response.cookies = cookies;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(CookieStore_Internal.GET_ORDINAL, 2, this.mRequestId)));
        }
    }

    static class CookieStoreSetResponseParamsProxyToResponder implements SetResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        CookieStoreSetResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(Boolean success) {
            CookieStoreSetResponseParams _response = new CookieStoreSetResponseParams();
            _response.success = success.booleanValue();
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(CookieStore_Internal.SET_ORDINAL, 2, this.mRequestId)));
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<CookieStore> {
        Stub(Core core, CookieStore impl) {
            super(core, impl);
        }

        public boolean accept(Message message) {
            boolean z = false;
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                MessageHeader header = messageWithHeader.getHeader();
                if (header.validateHeader(CookieStore_Internal.GET_ORDINAL)) {
                    switch (header.getType()) {
                        case XWalkResourceClientInternal.ERROR_HOST_LOOKUP /*-2*/:
                            z = InterfaceControlMessagesHelper.handleRunOrClosePipe(CookieStore_Internal.MANAGER, messageWithHeader);
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
                if (!header.validateHeader(CookieStore_Internal.SET_ORDINAL)) {
                    return false;
                }
                switch (header.getType()) {
                    case ExploreByTouchHelper.HOST_ID /*-1*/:
                        return InterfaceControlMessagesHelper.handleRun(getCore(), CookieStore_Internal.MANAGER, messageWithHeader, receiver);
                    case CookieStore_Internal.GET_ORDINAL /*0*/:
                        ((CookieStore) getImpl()).get(CookieStoreGetParams.deserialize(messageWithHeader.getPayload()).url, new CookieStoreGetResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    case CookieStore_Internal.SET_ORDINAL /*1*/:
                        CookieStoreSetParams data = CookieStoreSetParams.deserialize(messageWithHeader.getPayload());
                        ((CookieStore) getImpl()).set(data.url, data.cookie, new CookieStoreSetResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    default:
                        return false;
                }
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.CookieStore.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void get(String url, GetResponse callback) {
            CookieStoreGetParams _message = new CookieStoreGetParams();
            _message.url = url;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(CookieStore_Internal.GET_ORDINAL, CookieStore_Internal.SET_ORDINAL, 0)), new CookieStoreGetResponseParamsForwardToCallback(callback));
        }

        public void set(String url, String cookie, SetResponse callback) {
            CookieStoreSetParams _message = new CookieStoreSetParams();
            _message.url = url;
            _message.cookie = cookie;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(CookieStore_Internal.SET_ORDINAL, CookieStore_Internal.SET_ORDINAL, 0)), new CookieStoreSetResponseParamsForwardToCallback(callback));
        }
    }

    CookieStore_Internal() {
    }

    static {
        MANAGER = new C06171();
    }
}
