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
import org.chromium.mojo.bindings.SideEffectFreeCloseable;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;
import org.chromium.mojo.system.DataPipe.ConsumerHandle;
import org.chromium.mojo.system.DataPipe.ProducerHandle;
import org.chromium.mojo.system.InvalidHandle;
import org.chromium.mojom.mojo.TcpServerSocket.AcceptResponse;
import org.xwalk.core.internal.XWalkResourceClientInternal;

class TcpServerSocket_Internal {
    private static final int ACCEPT_ORDINAL = 0;
    public static final Manager<TcpServerSocket, org.chromium.mojom.mojo.TcpServerSocket.Proxy> MANAGER;

    /* renamed from: org.chromium.mojom.mojo.TcpServerSocket_Internal.1 */
    static class C06311 extends Manager<TcpServerSocket, org.chromium.mojom.mojo.TcpServerSocket.Proxy> {
        C06311() {
        }

        public String getName() {
            return "mojo::TCPServerSocket";
        }

        public int getVersion() {
            return 0;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, TcpServerSocket impl) {
            return new Stub(core, impl);
        }

        public TcpServerSocket[] buildArray(int size) {
            return new TcpServerSocket[size];
        }
    }

    static final class TcpServerSocketAcceptParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public InterfaceRequest<TcpConnectedSocket> clientSocket;
        public ProducerHandle receiveStream;
        public ConsumerHandle sendStream;

        static {
            VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private TcpServerSocketAcceptParams(int version) {
            super(STRUCT_SIZE, version);
            this.sendStream = InvalidHandle.INSTANCE;
            this.receiveStream = InvalidHandle.INSTANCE;
        }

        public TcpServerSocketAcceptParams() {
            this(0);
        }

        public static TcpServerSocketAcceptParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static TcpServerSocketAcceptParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            TcpServerSocketAcceptParams result = new TcpServerSocketAcceptParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.sendStream = decoder0.readConsumerHandle(8, false);
            }
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.receiveStream = decoder0.readProducerHandle(12, false);
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.clientSocket = decoder0.readInterfaceRequest(16, false);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.sendStream, 8, false);
            encoder0.encode(this.receiveStream, 12, false);
            encoder0.encode(this.clientSocket, 16, false);
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
            TcpServerSocketAcceptParams other = (TcpServerSocketAcceptParams) object;
            if (!BindingsHelper.equals(this.sendStream, other.sendStream)) {
                return false;
            }
            if (!BindingsHelper.equals(this.receiveStream, other.receiveStream)) {
                return false;
            }
            if (BindingsHelper.equals(this.clientSocket, other.clientSocket)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.sendStream)) * 31) + BindingsHelper.hashCode(this.receiveStream)) * 31) + BindingsHelper.hashCode(this.clientSocket);
        }
    }

    static final class TcpServerSocketAcceptResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public NetAddress remoteAddress;
        public NetworkError result;

        static {
            VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        }

        private TcpServerSocketAcceptResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public TcpServerSocketAcceptResponseParams() {
            this(0);
        }

        public static TcpServerSocketAcceptResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static TcpServerSocketAcceptResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            TcpServerSocketAcceptResponseParams result = new TcpServerSocketAcceptResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.result = NetworkError.decode(decoder0.readPointer(8, false));
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.remoteAddress = NetAddress.decode(decoder0.readPointer(16, true));
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.result, 8, false);
            encoder0.encode(this.remoteAddress, 16, true);
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
            TcpServerSocketAcceptResponseParams other = (TcpServerSocketAcceptResponseParams) object;
            if (!BindingsHelper.equals(this.result, other.result)) {
                return false;
            }
            if (BindingsHelper.equals(this.remoteAddress, other.remoteAddress)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = getClass().hashCode() + 31;
            return (((result * 31) + BindingsHelper.hashCode(result)) * 31) + BindingsHelper.hashCode(this.remoteAddress);
        }
    }

    static class TcpServerSocketAcceptResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AcceptResponse mCallback;

        TcpServerSocketAcceptResponseParamsForwardToCallback(AcceptResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                TcpServerSocketAcceptResponseParams response = TcpServerSocketAcceptResponseParams.deserialize(messageWithHeader.getPayload());
                this.mCallback.call(response.result, response.remoteAddress);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static class TcpServerSocketAcceptResponseParamsProxyToResponder implements AcceptResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        TcpServerSocketAcceptResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(NetworkError result, NetAddress remoteAddress) {
            TcpServerSocketAcceptResponseParams _response = new TcpServerSocketAcceptResponseParams();
            _response.result = result;
            _response.remoteAddress = remoteAddress;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<TcpServerSocket> {
        Stub(Core core, TcpServerSocket impl) {
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
                            z = InterfaceControlMessagesHelper.handleRunOrClosePipe(TcpServerSocket_Internal.MANAGER, messageWithHeader);
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
                if (!header.validateHeader(1)) {
                    return false;
                }
                switch (header.getType()) {
                    case ExploreByTouchHelper.HOST_ID /*-1*/:
                        return InterfaceControlMessagesHelper.handleRun(getCore(), TcpServerSocket_Internal.MANAGER, messageWithHeader, receiver);
                    case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                        TcpServerSocketAcceptParams data = TcpServerSocketAcceptParams.deserialize(messageWithHeader.getPayload());
                        ((TcpServerSocket) getImpl()).accept(data.sendStream, data.receiveStream, data.clientSocket, new TcpServerSocketAcceptResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    default:
                        return false;
                }
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.TcpServerSocket.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void accept(ConsumerHandle sendStream, ProducerHandle receiveStream, InterfaceRequest<TcpConnectedSocket> clientSocket, AcceptResponse callback) {
            TcpServerSocketAcceptParams _message = new TcpServerSocketAcceptParams();
            _message.sendStream = sendStream;
            _message.receiveStream = receiveStream;
            _message.clientSocket = clientSocket;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0)), new TcpServerSocketAcceptResponseParamsForwardToCallback(callback));
        }
    }

    TcpServerSocket_Internal() {
    }

    static {
        MANAGER = new C06311();
    }
}
