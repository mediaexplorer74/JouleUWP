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
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;
import org.xwalk.core.internal.XWalkResourceClientInternal;

class Shell_Internal {
    private static final int CONNECT_TO_APPLICATION_ORDINAL = 0;
    public static final Manager<Shell, org.chromium.mojom.mojo.Shell.Proxy> MANAGER;
    private static final int QUIT_APPLICATION_ORDINAL = 1;

    /* renamed from: org.chromium.mojom.mojo.Shell_Internal.1 */
    static class C06281 extends Manager<Shell, org.chromium.mojom.mojo.Shell.Proxy> {
        C06281() {
        }

        public String getName() {
            return "mojo::Shell";
        }

        public int getVersion() {
            return Shell_Internal.CONNECT_TO_APPLICATION_ORDINAL;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, Shell impl) {
            return new Stub(core, impl);
        }

        public Shell[] buildArray(int size) {
            return new Shell[size];
        }
    }

    static final class ShellConnectToApplicationParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 32;
        private static final DataHeader[] VERSION_ARRAY;
        public UrlRequest applicationUrl;
        public ServiceProvider exposedServices;
        public InterfaceRequest<ServiceProvider> services;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[Shell_Internal.QUIT_APPLICATION_ORDINAL];
            dataHeaderArr[Shell_Internal.CONNECT_TO_APPLICATION_ORDINAL] = new DataHeader(STRUCT_SIZE, Shell_Internal.CONNECT_TO_APPLICATION_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[Shell_Internal.CONNECT_TO_APPLICATION_ORDINAL];
        }

        private ShellConnectToApplicationParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public ShellConnectToApplicationParams() {
            this(Shell_Internal.CONNECT_TO_APPLICATION_ORDINAL);
        }

        public static ShellConnectToApplicationParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static ShellConnectToApplicationParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            ShellConnectToApplicationParams result = new ShellConnectToApplicationParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.applicationUrl = UrlRequest.decode(decoder0.readPointer(8, false));
            }
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.services = decoder0.readInterfaceRequest(16, true);
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.exposedServices = (ServiceProvider) decoder0.readServiceInterface(20, true, ServiceProvider.MANAGER);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.applicationUrl, 8, false);
            encoder0.encode(this.services, 16, true);
            encoder0.encode(this.exposedServices, 20, true, ServiceProvider.MANAGER);
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
            ShellConnectToApplicationParams other = (ShellConnectToApplicationParams) object;
            if (!BindingsHelper.equals(this.applicationUrl, other.applicationUrl)) {
                return false;
            }
            if (!BindingsHelper.equals(this.services, other.services)) {
                return false;
            }
            if (BindingsHelper.equals(this.exposedServices, other.exposedServices)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.applicationUrl)) * 31) + BindingsHelper.hashCode(this.services)) * 31) + BindingsHelper.hashCode(this.exposedServices);
        }
    }

    static final class ShellQuitApplicationParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 8;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[Shell_Internal.QUIT_APPLICATION_ORDINAL];
            dataHeaderArr[Shell_Internal.CONNECT_TO_APPLICATION_ORDINAL] = new DataHeader(STRUCT_SIZE, Shell_Internal.CONNECT_TO_APPLICATION_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[Shell_Internal.CONNECT_TO_APPLICATION_ORDINAL];
        }

        private ShellQuitApplicationParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public ShellQuitApplicationParams() {
            this(Shell_Internal.CONNECT_TO_APPLICATION_ORDINAL);
        }

        public static ShellQuitApplicationParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static ShellQuitApplicationParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            return new ShellQuitApplicationParams(decoder0.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
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

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<Shell> {
        Stub(Core core, Shell impl) {
            super(core, impl);
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                MessageHeader header = messageWithHeader.getHeader();
                if (!header.validateHeader(Shell_Internal.CONNECT_TO_APPLICATION_ORDINAL)) {
                    return false;
                }
                switch (header.getType()) {
                    case XWalkResourceClientInternal.ERROR_HOST_LOOKUP /*-2*/:
                        return InterfaceControlMessagesHelper.handleRunOrClosePipe(Shell_Internal.MANAGER, messageWithHeader);
                    case Shell_Internal.CONNECT_TO_APPLICATION_ORDINAL /*0*/:
                        ShellConnectToApplicationParams data = ShellConnectToApplicationParams.deserialize(messageWithHeader.getPayload());
                        ((Shell) getImpl()).connectToApplication(data.applicationUrl, data.services, data.exposedServices);
                        return true;
                    case Shell_Internal.QUIT_APPLICATION_ORDINAL /*1*/:
                        ShellQuitApplicationParams.deserialize(messageWithHeader.getPayload());
                        ((Shell) getImpl()).quitApplication();
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
                if (header.validateHeader(Shell_Internal.QUIT_APPLICATION_ORDINAL)) {
                    switch (header.getType()) {
                        case ExploreByTouchHelper.HOST_ID /*-1*/:
                            z = InterfaceControlMessagesHelper.handleRun(getCore(), Shell_Internal.MANAGER, messageWithHeader, receiver);
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

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.Shell.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void connectToApplication(UrlRequest applicationUrl, InterfaceRequest<ServiceProvider> services, ServiceProvider exposedServices) {
            ShellConnectToApplicationParams _message = new ShellConnectToApplicationParams();
            _message.applicationUrl = applicationUrl;
            _message.services = services;
            _message.exposedServices = exposedServices;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader((int) Shell_Internal.CONNECT_TO_APPLICATION_ORDINAL)));
        }

        public void quitApplication() {
            getProxyHandler().getMessageReceiver().accept(new ShellQuitApplicationParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader((int) Shell_Internal.QUIT_APPLICATION_ORDINAL)));
        }
    }

    Shell_Internal() {
    }

    static {
        MANAGER = new C06281();
    }
}
