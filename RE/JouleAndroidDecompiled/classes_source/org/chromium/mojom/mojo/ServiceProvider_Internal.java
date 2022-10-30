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
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojo.system.MessagePipeHandle;
import org.xwalk.core.internal.XWalkResourceClientInternal;

class ServiceProvider_Internal {
    private static final int CONNECT_TO_SERVICE_ORDINAL = 0;
    public static final Manager<ServiceProvider, org.chromium.mojom.mojo.ServiceProvider.Proxy> MANAGER;

    /* renamed from: org.chromium.mojom.mojo.ServiceProvider_Internal.1 */
    static class C06271 extends Manager<ServiceProvider, org.chromium.mojom.mojo.ServiceProvider.Proxy> {
        C06271() {
        }

        public String getName() {
            return "mojo::ServiceProvider";
        }

        public int getVersion() {
            return 0;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, ServiceProvider impl) {
            return new Stub(core, impl);
        }

        public ServiceProvider[] buildArray(int size) {
            return new ServiceProvider[size];
        }
    }

    static final class ServiceProviderConnectToServiceParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public String interfaceName;
        public MessagePipeHandle pipe;

        static {
            VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private ServiceProviderConnectToServiceParams(int version) {
            super(STRUCT_SIZE, version);
            this.pipe = InvalidHandle.INSTANCE;
        }

        public ServiceProviderConnectToServiceParams() {
            this(0);
        }

        public static ServiceProviderConnectToServiceParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static ServiceProviderConnectToServiceParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            ServiceProviderConnectToServiceParams result = new ServiceProviderConnectToServiceParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.interfaceName = decoder0.readString(8, false);
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.pipe = decoder0.readMessagePipeHandle(16, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.interfaceName, 8, false);
            encoder0.encode(this.pipe, 16, false);
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
            ServiceProviderConnectToServiceParams other = (ServiceProviderConnectToServiceParams) object;
            if (!BindingsHelper.equals(this.interfaceName, other.interfaceName)) {
                return false;
            }
            if (BindingsHelper.equals(this.pipe, other.pipe)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.interfaceName)) * 31) + BindingsHelper.hashCode(this.pipe);
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<ServiceProvider> {
        Stub(Core core, ServiceProvider impl) {
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
                        return InterfaceControlMessagesHelper.handleRunOrClosePipe(ServiceProvider_Internal.MANAGER, messageWithHeader);
                    case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                        ServiceProviderConnectToServiceParams data = ServiceProviderConnectToServiceParams.deserialize(messageWithHeader.getPayload());
                        ((ServiceProvider) getImpl()).connectToService(data.interfaceName, data.pipe);
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
                if (header.validateHeader(1)) {
                    switch (header.getType()) {
                        case ExploreByTouchHelper.HOST_ID /*-1*/:
                            z = InterfaceControlMessagesHelper.handleRun(getCore(), ServiceProvider_Internal.MANAGER, messageWithHeader, receiver);
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

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.ServiceProvider.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void connectToService(String interfaceName, MessagePipeHandle pipe) {
            ServiceProviderConnectToServiceParams _message = new ServiceProviderConnectToServiceParams();
            _message.interfaceName = interfaceName;
            _message.pipe = pipe;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    ServiceProvider_Internal() {
    }

    static {
        MANAGER = new C06271();
    }
}
