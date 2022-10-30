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

class ContentHandler_Internal {
    public static final Manager<ContentHandler, org.chromium.mojom.mojo.ContentHandler.Proxy> MANAGER;
    private static final int START_APPLICATION_ORDINAL = 0;

    /* renamed from: org.chromium.mojom.mojo.ContentHandler_Internal.1 */
    static class C06161 extends Manager<ContentHandler, org.chromium.mojom.mojo.ContentHandler.Proxy> {
        C06161() {
        }

        public String getName() {
            return "mojo::ContentHandler";
        }

        public int getVersion() {
            return 0;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, ContentHandler impl) {
            return new Stub(core, impl);
        }

        public ContentHandler[] buildArray(int size) {
            return new ContentHandler[size];
        }
    }

    static final class ContentHandlerStartApplicationParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<Application> application;
        public UrlResponse response;

        static {
            VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private ContentHandlerStartApplicationParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public ContentHandlerStartApplicationParams() {
            this(0);
        }

        public static ContentHandlerStartApplicationParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static ContentHandlerStartApplicationParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            ContentHandlerStartApplicationParams result = new ContentHandlerStartApplicationParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.application = decoder0.readInterfaceRequest(8, false);
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.response = UrlResponse.decode(decoder0.readPointer(16, false));
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.application, 8, false);
            encoder0.encode(this.response, 16, false);
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
            ContentHandlerStartApplicationParams other = (ContentHandlerStartApplicationParams) object;
            if (!BindingsHelper.equals(this.application, other.application)) {
                return false;
            }
            if (BindingsHelper.equals(this.response, other.response)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.application)) * 31) + BindingsHelper.hashCode(this.response);
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<ContentHandler> {
        Stub(Core core, ContentHandler impl) {
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
                        return InterfaceControlMessagesHelper.handleRunOrClosePipe(ContentHandler_Internal.MANAGER, messageWithHeader);
                    case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                        ContentHandlerStartApplicationParams data = ContentHandlerStartApplicationParams.deserialize(messageWithHeader.getPayload());
                        ((ContentHandler) getImpl()).startApplication(data.application, data.response);
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
                            z = InterfaceControlMessagesHelper.handleRun(getCore(), ContentHandler_Internal.MANAGER, messageWithHeader, receiver);
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

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.ContentHandler.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void startApplication(InterfaceRequest<Application> application, UrlResponse response) {
            ContentHandlerStartApplicationParams _message = new ContentHandlerStartApplicationParams();
            _message.application = application;
            _message.response = response;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    ContentHandler_Internal() {
    }

    static {
        MANAGER = new C06161();
    }
}
