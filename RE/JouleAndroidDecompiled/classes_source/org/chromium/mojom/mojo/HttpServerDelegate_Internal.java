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
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;
import org.xwalk.core.internal.XWalkResourceClientInternal;

class HttpServerDelegate_Internal {
    public static final Manager<HttpServerDelegate, org.chromium.mojom.mojo.HttpServerDelegate.Proxy> MANAGER;
    private static final int ON_CONNECTED_ORDINAL = 0;

    /* renamed from: org.chromium.mojom.mojo.HttpServerDelegate_Internal.1 */
    static class C06231 extends Manager<HttpServerDelegate, org.chromium.mojom.mojo.HttpServerDelegate.Proxy> {
        C06231() {
        }

        public String getName() {
            return "mojo::HttpServerDelegate";
        }

        public int getVersion() {
            return 0;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, HttpServerDelegate impl) {
            return new Stub(core, impl);
        }

        public HttpServerDelegate[] buildArray(int size) {
            return new HttpServerDelegate[size];
        }
    }

    static final class HttpServerDelegateOnConnectedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public HttpConnection connection;
        public InterfaceRequest<HttpConnectionDelegate> delegate;

        static {
            VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private HttpServerDelegateOnConnectedParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public HttpServerDelegateOnConnectedParams() {
            this(0);
        }

        public static HttpServerDelegateOnConnectedParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static HttpServerDelegateOnConnectedParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            HttpServerDelegateOnConnectedParams result = new HttpServerDelegateOnConnectedParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.connection = (HttpConnection) decoder0.readServiceInterface(8, false, HttpConnection.MANAGER);
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.delegate = decoder0.readInterfaceRequest(16, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.connection, 8, false, HttpConnection.MANAGER);
            encoder0.encode(this.delegate, 16, false);
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
            HttpServerDelegateOnConnectedParams other = (HttpServerDelegateOnConnectedParams) object;
            if (!BindingsHelper.equals(this.connection, other.connection)) {
                return false;
            }
            if (BindingsHelper.equals(this.delegate, other.delegate)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.connection)) * 31) + BindingsHelper.hashCode(this.delegate);
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<HttpServerDelegate> {
        Stub(Core core, HttpServerDelegate impl) {
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
                        return InterfaceControlMessagesHelper.handleRunOrClosePipe(HttpServerDelegate_Internal.MANAGER, messageWithHeader);
                    case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                        HttpServerDelegateOnConnectedParams data = HttpServerDelegateOnConnectedParams.deserialize(messageWithHeader.getPayload());
                        ((HttpServerDelegate) getImpl()).onConnected(data.connection, data.delegate);
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
                            z = InterfaceControlMessagesHelper.handleRun(getCore(), HttpServerDelegate_Internal.MANAGER, messageWithHeader, receiver);
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

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.HttpServerDelegate.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void onConnected(HttpConnection connection, InterfaceRequest<HttpConnectionDelegate> delegate) {
            HttpServerDelegateOnConnectedParams _message = new HttpServerDelegateOnConnectedParams();
            _message.connection = connection;
            _message.delegate = delegate;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    HttpServerDelegate_Internal() {
    }

    static {
        MANAGER = new C06231();
    }
}
