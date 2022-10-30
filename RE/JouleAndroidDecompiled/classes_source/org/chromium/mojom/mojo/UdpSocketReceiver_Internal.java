package org.chromium.mojom.mojo;

import android.support.v4.widget.ExploreByTouchHelper;
import java.util.Arrays;
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
import org.xwalk.core.internal.XWalkResourceClientInternal;

class UdpSocketReceiver_Internal {
    public static final Manager<UdpSocketReceiver, org.chromium.mojom.mojo.UdpSocketReceiver.Proxy> MANAGER;
    private static final int ON_RECEIVED_ORDINAL = 0;

    /* renamed from: org.chromium.mojom.mojo.UdpSocketReceiver_Internal.1 */
    static class C06321 extends Manager<UdpSocketReceiver, org.chromium.mojom.mojo.UdpSocketReceiver.Proxy> {
        C06321() {
        }

        public String getName() {
            return "mojo::UDPSocketReceiver";
        }

        public int getVersion() {
            return 0;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, UdpSocketReceiver impl) {
            return new Stub(core, impl);
        }

        public UdpSocketReceiver[] buildArray(int size) {
            return new UdpSocketReceiver[size];
        }
    }

    static final class UdpSocketReceiverOnReceivedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 32;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] data;
        public NetworkError result;
        public NetAddress srcAddr;

        static {
            VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private UdpSocketReceiverOnReceivedParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UdpSocketReceiverOnReceivedParams() {
            this(0);
        }

        public static UdpSocketReceiverOnReceivedParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UdpSocketReceiverOnReceivedParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UdpSocketReceiverOnReceivedParams result = new UdpSocketReceiverOnReceivedParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.result = NetworkError.decode(decoder0.readPointer(8, false));
            }
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.srcAddr = NetAddress.decode(decoder0.readPointer(16, true));
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.data = decoder0.readBytes(24, 1, -1);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.result, 8, false);
            encoder0.encode(this.srcAddr, 16, true);
            encoder0.encode(this.data, 24, 1, -1);
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
            UdpSocketReceiverOnReceivedParams other = (UdpSocketReceiverOnReceivedParams) object;
            if (!BindingsHelper.equals(this.result, other.result)) {
                return false;
            }
            if (!BindingsHelper.equals(this.srcAddr, other.srcAddr)) {
                return false;
            }
            if (Arrays.equals(this.data, other.data)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = getClass().hashCode() + 31;
            return (((((result * 31) + BindingsHelper.hashCode(result)) * 31) + BindingsHelper.hashCode(this.srcAddr)) * 31) + Arrays.hashCode(this.data);
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<UdpSocketReceiver> {
        Stub(Core core, UdpSocketReceiver impl) {
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
                        return InterfaceControlMessagesHelper.handleRunOrClosePipe(UdpSocketReceiver_Internal.MANAGER, messageWithHeader);
                    case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                        UdpSocketReceiverOnReceivedParams data = UdpSocketReceiverOnReceivedParams.deserialize(messageWithHeader.getPayload());
                        ((UdpSocketReceiver) getImpl()).onReceived(data.result, data.srcAddr, data.data);
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
                            z = InterfaceControlMessagesHelper.handleRun(getCore(), UdpSocketReceiver_Internal.MANAGER, messageWithHeader, receiver);
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

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.UdpSocketReceiver.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void onReceived(NetworkError result, NetAddress srcAddr, byte[] data) {
            UdpSocketReceiverOnReceivedParams _message = new UdpSocketReceiverOnReceivedParams();
            _message.result = result;
            _message.srcAddr = srcAddr;
            _message.data = data;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }
    }

    UdpSocketReceiver_Internal() {
    }

    static {
        MANAGER = new C06321();
    }
}
