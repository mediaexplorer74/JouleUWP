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
import org.chromium.mojom.mojo.UrlLoader.FollowRedirectResponse;
import org.chromium.mojom.mojo.UrlLoader.QueryStatusResponse;
import org.chromium.mojom.mojo.UrlLoader.StartResponse;
import org.xwalk.core.internal.XWalkResourceClientInternal;

class UrlLoader_Internal {
    private static final int FOLLOW_REDIRECT_ORDINAL = 1;
    public static final Manager<UrlLoader, org.chromium.mojom.mojo.UrlLoader.Proxy> MANAGER;
    private static final int QUERY_STATUS_ORDINAL = 2;
    private static final int START_ORDINAL = 0;

    /* renamed from: org.chromium.mojom.mojo.UrlLoader_Internal.1 */
    static class C06361 extends Manager<UrlLoader, org.chromium.mojom.mojo.UrlLoader.Proxy> {
        C06361() {
        }

        public String getName() {
            return "mojo::URLLoader";
        }

        public int getVersion() {
            return 0;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, UrlLoader impl) {
            return new Stub(core, impl);
        }

        public UrlLoader[] buildArray(int size) {
            return new UrlLoader[size];
        }
    }

    static final class UrlLoaderFollowRedirectParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 8;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UrlLoader_Internal.FOLLOW_REDIRECT_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private UrlLoaderFollowRedirectParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UrlLoaderFollowRedirectParams() {
            this(0);
        }

        public static UrlLoaderFollowRedirectParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UrlLoaderFollowRedirectParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            return new UrlLoaderFollowRedirectParams(decoder0.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
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
            return true;
        }

        public int hashCode() {
            return getClass().hashCode() + 31;
        }
    }

    static final class UrlLoaderFollowRedirectResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public UrlResponse response;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UrlLoader_Internal.FOLLOW_REDIRECT_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private UrlLoaderFollowRedirectResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UrlLoaderFollowRedirectResponseParams() {
            this(0);
        }

        public static UrlLoaderFollowRedirectResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UrlLoaderFollowRedirectResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UrlLoaderFollowRedirectResponseParams result = new UrlLoaderFollowRedirectResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.response = UrlResponse.decode(decoder0.readPointer(8, false));
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
            if (BindingsHelper.equals(this.response, ((UrlLoaderFollowRedirectResponseParams) object).response)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.response);
        }
    }

    static class UrlLoaderFollowRedirectResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final FollowRedirectResponse mCallback;

        UrlLoaderFollowRedirectResponseParamsForwardToCallback(FollowRedirectResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(UrlLoader_Internal.FOLLOW_REDIRECT_ORDINAL, UrlLoader_Internal.QUERY_STATUS_ORDINAL)) {
                    return false;
                }
                this.mCallback.call(UrlLoaderFollowRedirectResponseParams.deserialize(messageWithHeader.getPayload()).response);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class UrlLoaderQueryStatusParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 8;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UrlLoader_Internal.FOLLOW_REDIRECT_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private UrlLoaderQueryStatusParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UrlLoaderQueryStatusParams() {
            this(0);
        }

        public static UrlLoaderQueryStatusParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UrlLoaderQueryStatusParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            return new UrlLoaderQueryStatusParams(decoder0.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
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
            return true;
        }

        public int hashCode() {
            return getClass().hashCode() + 31;
        }
    }

    static final class UrlLoaderQueryStatusResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public UrlLoaderStatus status;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UrlLoader_Internal.FOLLOW_REDIRECT_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private UrlLoaderQueryStatusResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UrlLoaderQueryStatusResponseParams() {
            this(0);
        }

        public static UrlLoaderQueryStatusResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UrlLoaderQueryStatusResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UrlLoaderQueryStatusResponseParams result = new UrlLoaderQueryStatusResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.status = UrlLoaderStatus.decode(decoder0.readPointer(8, false));
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8, false);
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
            if (BindingsHelper.equals(this.status, ((UrlLoaderQueryStatusResponseParams) object).status)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.status);
        }
    }

    static class UrlLoaderQueryStatusResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final QueryStatusResponse mCallback;

        UrlLoaderQueryStatusResponseParamsForwardToCallback(QueryStatusResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(UrlLoader_Internal.QUERY_STATUS_ORDINAL, UrlLoader_Internal.QUERY_STATUS_ORDINAL)) {
                    return false;
                }
                this.mCallback.call(UrlLoaderQueryStatusResponseParams.deserialize(messageWithHeader.getPayload()).status);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class UrlLoaderStartParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public UrlRequest request;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UrlLoader_Internal.FOLLOW_REDIRECT_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private UrlLoaderStartParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UrlLoaderStartParams() {
            this(0);
        }

        public static UrlLoaderStartParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UrlLoaderStartParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UrlLoaderStartParams result = new UrlLoaderStartParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.request = UrlRequest.decode(decoder0.readPointer(8, false));
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
            if (BindingsHelper.equals(this.request, ((UrlLoaderStartParams) object).request)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.request);
        }
    }

    static final class UrlLoaderStartResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public UrlResponse response;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UrlLoader_Internal.FOLLOW_REDIRECT_ORDINAL];
            dataHeaderArr[0] = new DataHeader(STRUCT_SIZE, 0);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private UrlLoaderStartResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UrlLoaderStartResponseParams() {
            this(0);
        }

        public static UrlLoaderStartResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UrlLoaderStartResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UrlLoaderStartResponseParams result = new UrlLoaderStartResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.response = UrlResponse.decode(decoder0.readPointer(8, false));
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
            if (BindingsHelper.equals(this.response, ((UrlLoaderStartResponseParams) object).response)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.response);
        }
    }

    static class UrlLoaderStartResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final StartResponse mCallback;

        UrlLoaderStartResponseParamsForwardToCallback(StartResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(0, UrlLoader_Internal.QUERY_STATUS_ORDINAL)) {
                    return false;
                }
                this.mCallback.call(UrlLoaderStartResponseParams.deserialize(messageWithHeader.getPayload()).response);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static class UrlLoaderFollowRedirectResponseParamsProxyToResponder implements FollowRedirectResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UrlLoaderFollowRedirectResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(UrlResponse response) {
            UrlLoaderFollowRedirectResponseParams _response = new UrlLoaderFollowRedirectResponseParams();
            _response.response = response;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(UrlLoader_Internal.FOLLOW_REDIRECT_ORDINAL, UrlLoader_Internal.QUERY_STATUS_ORDINAL, this.mRequestId)));
        }
    }

    static class UrlLoaderQueryStatusResponseParamsProxyToResponder implements QueryStatusResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UrlLoaderQueryStatusResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(UrlLoaderStatus status) {
            UrlLoaderQueryStatusResponseParams _response = new UrlLoaderQueryStatusResponseParams();
            _response.status = status;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(UrlLoader_Internal.QUERY_STATUS_ORDINAL, UrlLoader_Internal.QUERY_STATUS_ORDINAL, this.mRequestId)));
        }
    }

    static class UrlLoaderStartResponseParamsProxyToResponder implements StartResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UrlLoaderStartResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(UrlResponse response) {
            UrlLoaderStartResponseParams _response = new UrlLoaderStartResponseParams();
            _response.response = response;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(0, UrlLoader_Internal.QUERY_STATUS_ORDINAL, this.mRequestId)));
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<UrlLoader> {
        Stub(Core core, UrlLoader impl) {
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
                            z = InterfaceControlMessagesHelper.handleRunOrClosePipe(UrlLoader_Internal.MANAGER, messageWithHeader);
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
                if (!header.validateHeader(UrlLoader_Internal.FOLLOW_REDIRECT_ORDINAL)) {
                    return false;
                }
                switch (header.getType()) {
                    case ExploreByTouchHelper.HOST_ID /*-1*/:
                        return InterfaceControlMessagesHelper.handleRun(getCore(), UrlLoader_Internal.MANAGER, messageWithHeader, receiver);
                    case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                        ((UrlLoader) getImpl()).start(UrlLoaderStartParams.deserialize(messageWithHeader.getPayload()).request, new UrlLoaderStartResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    case UrlLoader_Internal.FOLLOW_REDIRECT_ORDINAL /*1*/:
                        UrlLoaderFollowRedirectParams.deserialize(messageWithHeader.getPayload());
                        ((UrlLoader) getImpl()).followRedirect(new UrlLoaderFollowRedirectResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    case UrlLoader_Internal.QUERY_STATUS_ORDINAL /*2*/:
                        UrlLoaderQueryStatusParams.deserialize(messageWithHeader.getPayload());
                        ((UrlLoader) getImpl()).queryStatus(new UrlLoaderQueryStatusResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    default:
                        return false;
                }
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.UrlLoader.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void start(UrlRequest request, StartResponse callback) {
            UrlLoaderStartParams _message = new UrlLoaderStartParams();
            _message.request = request;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, UrlLoader_Internal.FOLLOW_REDIRECT_ORDINAL, 0)), new UrlLoaderStartResponseParamsForwardToCallback(callback));
        }

        public void followRedirect(FollowRedirectResponse callback) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new UrlLoaderFollowRedirectParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(UrlLoader_Internal.FOLLOW_REDIRECT_ORDINAL, UrlLoader_Internal.FOLLOW_REDIRECT_ORDINAL, 0)), new UrlLoaderFollowRedirectResponseParamsForwardToCallback(callback));
        }

        public void queryStatus(QueryStatusResponse callback) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new UrlLoaderQueryStatusParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(UrlLoader_Internal.QUERY_STATUS_ORDINAL, UrlLoader_Internal.FOLLOW_REDIRECT_ORDINAL, 0)), new UrlLoaderQueryStatusResponseParamsForwardToCallback(callback));
        }
    }

    UrlLoader_Internal() {
    }

    static {
        MANAGER = new C06361();
    }
}
