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

class UrlLoaderFactory_Internal {
    private static final int CREATE_URL_LOADER_ORDINAL = 0;
    public static final Manager<UrlLoaderFactory, org.chromium.mojom.mojo.UrlLoaderFactory.Proxy> MANAGER;

    /* renamed from: org.chromium.mojom.mojo.UrlLoaderFactory_Internal.1 */
    static class C06351 extends Manager<UrlLoaderFactory, org.chromium.mojom.mojo.UrlLoaderFactory.Proxy> {
        C06351() {
        }

        public String getName() {
            return "mojo::URLLoaderFactory";
        }

        public int getVersion() {
            return 0;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, UrlLoaderFactory impl) {
            return new Stub(core, impl);
        }

        public UrlLoaderFactory[] buildArray(int size) {
            return new UrlLoaderFactory[size];
        }
    }

    static final class UrlLoaderFactoryCreateUrlLoaderParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<UrlLoader> loader;

        static {
            VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private UrlLoaderFactoryCreateUrlLoaderParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UrlLoaderFactoryCreateUrlLoaderParams() {
            this(0);
        }

        public static UrlLoaderFactoryCreateUrlLoaderParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UrlLoaderFactoryCreateUrlLoaderParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UrlLoaderFactoryCreateUrlLoaderParams result = new UrlLoaderFactoryCreateUrlLoaderParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.loader = decoder0.readInterfaceRequest(8, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.loader, 8, false);
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
            if (BindingsHelper.equals(this.loader, ((UrlLoaderFactoryCreateUrlLoaderParams) object).loader)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.loader);
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<UrlLoaderFactory> {
        Stub(Core core, UrlLoaderFactory impl) {
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
                        return InterfaceControlMessagesHelper.handleRunOrClosePipe(UrlLoaderFactory_Internal.MANAGER, messageWithHeader);
                    case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                        ((UrlLoaderFactory) getImpl()).createUrlLoader(UrlLoaderFactoryCreateUrlLoaderParams.deserialize(messageWithHeader.getPayload()).loader);
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
                            z = InterfaceControlMessagesHelper.handleRun(getCore(), UrlLoaderFactory_Internal.MANAGER, messageWithHeader, receiver);
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

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.UrlLoaderFactory.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void createUrlLoader(InterfaceRequest<UrlLoader> loader) {
            UrlLoaderFactoryCreateUrlLoaderParams _message = new UrlLoaderFactoryCreateUrlLoaderParams();
            _message.loader = loader;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    UrlLoaderFactory_Internal() {
    }

    static {
        MANAGER = new C06351();
    }
}
