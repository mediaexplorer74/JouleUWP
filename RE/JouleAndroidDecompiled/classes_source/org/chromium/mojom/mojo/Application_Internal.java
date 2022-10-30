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
import org.chromium.mojom.mojo.Application.OnQuitRequestedResponse;
import org.xwalk.core.internal.XWalkResourceClientInternal;

class Application_Internal {
    private static final int ACCEPT_CONNECTION_ORDINAL = 1;
    private static final int INITIALIZE_ORDINAL = 0;
    public static final Manager<Application, org.chromium.mojom.mojo.Application.Proxy> MANAGER;
    private static final int ON_QUIT_REQUESTED_ORDINAL = 2;

    /* renamed from: org.chromium.mojom.mojo.Application_Internal.1 */
    static class C06151 extends Manager<Application, org.chromium.mojom.mojo.Application.Proxy> {
        C06151() {
        }

        public String getName() {
            return "mojo::Application";
        }

        public int getVersion() {
            return Application_Internal.INITIALIZE_ORDINAL;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, Application impl) {
            return new Stub(core, impl);
        }

        public Application[] buildArray(int size) {
            return new Application[size];
        }
    }

    static final class ApplicationAcceptConnectionParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 40;
        private static final DataHeader[] VERSION_ARRAY;
        public ServiceProvider exposedServices;
        public String requestorUrl;
        public String resolvedUrl;
        public InterfaceRequest<ServiceProvider> services;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[Application_Internal.ACCEPT_CONNECTION_ORDINAL];
            dataHeaderArr[Application_Internal.INITIALIZE_ORDINAL] = new DataHeader(STRUCT_SIZE, Application_Internal.INITIALIZE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[Application_Internal.INITIALIZE_ORDINAL];
        }

        private ApplicationAcceptConnectionParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public ApplicationAcceptConnectionParams() {
            this(Application_Internal.INITIALIZE_ORDINAL);
        }

        public static ApplicationAcceptConnectionParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static ApplicationAcceptConnectionParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            ApplicationAcceptConnectionParams result = new ApplicationAcceptConnectionParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.requestorUrl = decoder0.readString(8, false);
            }
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.services = decoder0.readInterfaceRequest(16, true);
            }
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.exposedServices = (ServiceProvider) decoder0.readServiceInterface(20, true, ServiceProvider.MANAGER);
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.resolvedUrl = decoder0.readString(32, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.requestorUrl, 8, false);
            encoder0.encode(this.services, 16, true);
            encoder0.encode(this.exposedServices, 20, true, ServiceProvider.MANAGER);
            encoder0.encode(this.resolvedUrl, 32, false);
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
            ApplicationAcceptConnectionParams other = (ApplicationAcceptConnectionParams) object;
            if (!BindingsHelper.equals(this.requestorUrl, other.requestorUrl)) {
                return false;
            }
            if (!BindingsHelper.equals(this.services, other.services)) {
                return false;
            }
            if (!BindingsHelper.equals(this.exposedServices, other.exposedServices)) {
                return false;
            }
            if (BindingsHelper.equals(this.resolvedUrl, other.resolvedUrl)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.requestorUrl)) * 31) + BindingsHelper.hashCode(this.services)) * 31) + BindingsHelper.hashCode(this.exposedServices)) * 31) + BindingsHelper.hashCode(this.resolvedUrl);
        }
    }

    static final class ApplicationInitializeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public Shell shell;
        public String url;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[Application_Internal.ACCEPT_CONNECTION_ORDINAL];
            dataHeaderArr[Application_Internal.INITIALIZE_ORDINAL] = new DataHeader(STRUCT_SIZE, Application_Internal.INITIALIZE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[Application_Internal.INITIALIZE_ORDINAL];
        }

        private ApplicationInitializeParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public ApplicationInitializeParams() {
            this(Application_Internal.INITIALIZE_ORDINAL);
        }

        public static ApplicationInitializeParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static ApplicationInitializeParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            ApplicationInitializeParams result = new ApplicationInitializeParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.shell = (Shell) decoder0.readServiceInterface(8, false, Shell.MANAGER);
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.url = decoder0.readString(16, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.shell, 8, false, Shell.MANAGER);
            encoder0.encode(this.url, 16, false);
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
            ApplicationInitializeParams other = (ApplicationInitializeParams) object;
            if (!BindingsHelper.equals(this.shell, other.shell)) {
                return false;
            }
            if (BindingsHelper.equals(this.url, other.url)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.shell)) * 31) + BindingsHelper.hashCode(this.url);
        }
    }

    static final class ApplicationOnQuitRequestedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 8;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[Application_Internal.ACCEPT_CONNECTION_ORDINAL];
            dataHeaderArr[Application_Internal.INITIALIZE_ORDINAL] = new DataHeader(STRUCT_SIZE, Application_Internal.INITIALIZE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[Application_Internal.INITIALIZE_ORDINAL];
        }

        private ApplicationOnQuitRequestedParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public ApplicationOnQuitRequestedParams() {
            this(Application_Internal.INITIALIZE_ORDINAL);
        }

        public static ApplicationOnQuitRequestedParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static ApplicationOnQuitRequestedParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            return new ApplicationOnQuitRequestedParams(decoder0.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
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

    static final class ApplicationOnQuitRequestedResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean canQuit;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[Application_Internal.ACCEPT_CONNECTION_ORDINAL];
            dataHeaderArr[Application_Internal.INITIALIZE_ORDINAL] = new DataHeader(STRUCT_SIZE, Application_Internal.INITIALIZE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[Application_Internal.INITIALIZE_ORDINAL];
        }

        private ApplicationOnQuitRequestedResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public ApplicationOnQuitRequestedResponseParams() {
            this(Application_Internal.INITIALIZE_ORDINAL);
        }

        public static ApplicationOnQuitRequestedResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static ApplicationOnQuitRequestedResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            ApplicationOnQuitRequestedResponseParams result = new ApplicationOnQuitRequestedResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.canQuit = decoder0.readBoolean(8, Application_Internal.INITIALIZE_ORDINAL);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.canQuit, 8, (int) Application_Internal.INITIALIZE_ORDINAL);
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
            if (this.canQuit != ((ApplicationOnQuitRequestedResponseParams) object).canQuit) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.canQuit);
        }
    }

    static class ApplicationOnQuitRequestedResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final OnQuitRequestedResponse mCallback;

        ApplicationOnQuitRequestedResponseParamsForwardToCallback(OnQuitRequestedResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(Application_Internal.ON_QUIT_REQUESTED_ORDINAL, Application_Internal.ON_QUIT_REQUESTED_ORDINAL)) {
                    return false;
                }
                this.mCallback.call(Boolean.valueOf(ApplicationOnQuitRequestedResponseParams.deserialize(messageWithHeader.getPayload()).canQuit));
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static class ApplicationOnQuitRequestedResponseParamsProxyToResponder implements OnQuitRequestedResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        ApplicationOnQuitRequestedResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(Boolean canQuit) {
            ApplicationOnQuitRequestedResponseParams _response = new ApplicationOnQuitRequestedResponseParams();
            _response.canQuit = canQuit.booleanValue();
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(Application_Internal.ON_QUIT_REQUESTED_ORDINAL, Application_Internal.ON_QUIT_REQUESTED_ORDINAL, this.mRequestId)));
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<Application> {
        Stub(Core core, Application impl) {
            super(core, impl);
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                MessageHeader header = messageWithHeader.getHeader();
                if (!header.validateHeader(Application_Internal.INITIALIZE_ORDINAL)) {
                    return false;
                }
                switch (header.getType()) {
                    case XWalkResourceClientInternal.ERROR_HOST_LOOKUP /*-2*/:
                        return InterfaceControlMessagesHelper.handleRunOrClosePipe(Application_Internal.MANAGER, messageWithHeader);
                    case Application_Internal.INITIALIZE_ORDINAL /*0*/:
                        ApplicationInitializeParams data = ApplicationInitializeParams.deserialize(messageWithHeader.getPayload());
                        ((Application) getImpl()).initialize(data.shell, data.url);
                        return true;
                    case Application_Internal.ACCEPT_CONNECTION_ORDINAL /*1*/:
                        ApplicationAcceptConnectionParams data2 = ApplicationAcceptConnectionParams.deserialize(messageWithHeader.getPayload());
                        ((Application) getImpl()).acceptConnection(data2.requestorUrl, data2.services, data2.exposedServices, data2.resolvedUrl);
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
                if (!header.validateHeader(Application_Internal.ACCEPT_CONNECTION_ORDINAL)) {
                    return false;
                }
                switch (header.getType()) {
                    case ExploreByTouchHelper.HOST_ID /*-1*/:
                        return InterfaceControlMessagesHelper.handleRun(getCore(), Application_Internal.MANAGER, messageWithHeader, receiver);
                    case Application_Internal.ON_QUIT_REQUESTED_ORDINAL /*2*/:
                        ApplicationOnQuitRequestedParams.deserialize(messageWithHeader.getPayload());
                        ((Application) getImpl()).onQuitRequested(new ApplicationOnQuitRequestedResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    default:
                        return false;
                }
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.Application.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void initialize(Shell shell, String url) {
            ApplicationInitializeParams _message = new ApplicationInitializeParams();
            _message.shell = shell;
            _message.url = url;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader((int) Application_Internal.INITIALIZE_ORDINAL)));
        }

        public void acceptConnection(String requestorUrl, InterfaceRequest<ServiceProvider> services, ServiceProvider exposedServices, String resolvedUrl) {
            ApplicationAcceptConnectionParams _message = new ApplicationAcceptConnectionParams();
            _message.requestorUrl = requestorUrl;
            _message.services = services;
            _message.exposedServices = exposedServices;
            _message.resolvedUrl = resolvedUrl;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader((int) Application_Internal.ACCEPT_CONNECTION_ORDINAL)));
        }

        public void onQuitRequested(OnQuitRequestedResponse callback) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new ApplicationOnQuitRequestedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(Application_Internal.ON_QUIT_REQUESTED_ORDINAL, Application_Internal.ACCEPT_CONNECTION_ORDINAL, 0)), new ApplicationOnQuitRequestedResponseParamsForwardToCallback(callback));
        }
    }

    Application_Internal() {
    }

    static {
        MANAGER = new C06151();
    }
}
