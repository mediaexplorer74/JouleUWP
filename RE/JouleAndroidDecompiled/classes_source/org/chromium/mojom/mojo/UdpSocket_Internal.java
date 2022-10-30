package org.chromium.mojom.mojo;

import android.support.v4.widget.ExploreByTouchHelper;
import java.util.Arrays;
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
import org.chromium.mojom.mojo.UdpSocket.AllowAddressReuseResponse;
import org.chromium.mojom.mojo.UdpSocket.BindResponse;
import org.chromium.mojom.mojo.UdpSocket.ConnectResponse;
import org.chromium.mojom.mojo.UdpSocket.NegotiateMaxPendingSendRequestsResponse;
import org.chromium.mojom.mojo.UdpSocket.SendToResponse;
import org.chromium.mojom.mojo.UdpSocket.SetReceiveBufferSizeResponse;
import org.chromium.mojom.mojo.UdpSocket.SetSendBufferSizeResponse;
import org.xwalk.core.internal.XWalkResourceClientInternal;

class UdpSocket_Internal {
    private static final int ALLOW_ADDRESS_REUSE_ORDINAL = 0;
    private static final int BIND_ORDINAL = 1;
    private static final int CONNECT_ORDINAL = 2;
    public static final Manager<UdpSocket, org.chromium.mojom.mojo.UdpSocket.Proxy> MANAGER;
    private static final int NEGOTIATE_MAX_PENDING_SEND_REQUESTS_ORDINAL = 5;
    private static final int RECEIVE_MORE_ORDINAL = 6;
    private static final int SEND_TO_ORDINAL = 7;
    private static final int SET_RECEIVE_BUFFER_SIZE_ORDINAL = 4;
    private static final int SET_SEND_BUFFER_SIZE_ORDINAL = 3;

    /* renamed from: org.chromium.mojom.mojo.UdpSocket_Internal.1 */
    static class C06331 extends Manager<UdpSocket, org.chromium.mojom.mojo.UdpSocket.Proxy> {
        C06331() {
        }

        public String getName() {
            return "mojo::UDPSocket";
        }

        public int getVersion() {
            return UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, UdpSocket impl) {
            return new Stub(core, impl);
        }

        public UdpSocket[] buildArray(int size) {
            return new UdpSocket[size];
        }
    }

    static final class UdpSocketAllowAddressReuseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 8;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UdpSocket_Internal.BIND_ORDINAL];
            dataHeaderArr[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL] = new DataHeader(STRUCT_SIZE, UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL];
        }

        private UdpSocketAllowAddressReuseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UdpSocketAllowAddressReuseParams() {
            this(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
        }

        public static UdpSocketAllowAddressReuseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UdpSocketAllowAddressReuseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            return new UdpSocketAllowAddressReuseParams(decoder0.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
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

    static final class UdpSocketAllowAddressReuseResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public NetworkError result;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UdpSocket_Internal.BIND_ORDINAL];
            dataHeaderArr[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL] = new DataHeader(STRUCT_SIZE, UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL];
        }

        private UdpSocketAllowAddressReuseResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UdpSocketAllowAddressReuseResponseParams() {
            this(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
        }

        public static UdpSocketAllowAddressReuseResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UdpSocketAllowAddressReuseResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UdpSocketAllowAddressReuseResponseParams result = new UdpSocketAllowAddressReuseResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.result = NetworkError.decode(decoder0.readPointer(8, false));
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8, false);
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
            if (BindingsHelper.equals(this.result, ((UdpSocketAllowAddressReuseResponseParams) object).result)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = getClass().hashCode() + 31;
            return (result * 31) + BindingsHelper.hashCode(result);
        }
    }

    static class UdpSocketAllowAddressReuseResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final AllowAddressReuseResponse mCallback;

        UdpSocketAllowAddressReuseResponseParamsForwardToCallback(AllowAddressReuseResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL, UdpSocket_Internal.CONNECT_ORDINAL)) {
                    return false;
                }
                this.mCallback.call(UdpSocketAllowAddressReuseResponseParams.deserialize(messageWithHeader.getPayload()).result);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class UdpSocketBindParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public NetAddress addr;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UdpSocket_Internal.BIND_ORDINAL];
            dataHeaderArr[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL] = new DataHeader(STRUCT_SIZE, UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL];
        }

        private UdpSocketBindParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UdpSocketBindParams() {
            this(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
        }

        public static UdpSocketBindParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UdpSocketBindParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UdpSocketBindParams result = new UdpSocketBindParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.addr = NetAddress.decode(decoder0.readPointer(8, false));
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.addr, 8, false);
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
            if (BindingsHelper.equals(this.addr, ((UdpSocketBindParams) object).addr)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.addr);
        }
    }

    static final class UdpSocketBindResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 32;
        private static final DataHeader[] VERSION_ARRAY;
        public NetAddress boundAddr;
        public InterfaceRequest<UdpSocketReceiver> receiver;
        public NetworkError result;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UdpSocket_Internal.BIND_ORDINAL];
            dataHeaderArr[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL] = new DataHeader(STRUCT_SIZE, UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL];
        }

        private UdpSocketBindResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UdpSocketBindResponseParams() {
            this(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
        }

        public static UdpSocketBindResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UdpSocketBindResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UdpSocketBindResponseParams result = new UdpSocketBindResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.result = NetworkError.decode(decoder0.readPointer(8, false));
            }
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.boundAddr = NetAddress.decode(decoder0.readPointer(16, true));
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.receiver = decoder0.readInterfaceRequest(24, true);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.result, 8, false);
            encoder0.encode(this.boundAddr, 16, true);
            encoder0.encode(this.receiver, 24, true);
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
            UdpSocketBindResponseParams other = (UdpSocketBindResponseParams) object;
            if (!BindingsHelper.equals(this.result, other.result)) {
                return false;
            }
            if (!BindingsHelper.equals(this.boundAddr, other.boundAddr)) {
                return false;
            }
            if (BindingsHelper.equals(this.receiver, other.receiver)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = getClass().hashCode() + 31;
            return (((((result * 31) + BindingsHelper.hashCode(result)) * 31) + BindingsHelper.hashCode(this.boundAddr)) * 31) + BindingsHelper.hashCode(this.receiver);
        }
    }

    static class UdpSocketBindResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final BindResponse mCallback;

        UdpSocketBindResponseParamsForwardToCallback(BindResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(UdpSocket_Internal.BIND_ORDINAL, UdpSocket_Internal.CONNECT_ORDINAL)) {
                    return false;
                }
                UdpSocketBindResponseParams response = UdpSocketBindResponseParams.deserialize(messageWithHeader.getPayload());
                this.mCallback.call(response.result, response.boundAddr, response.receiver);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class UdpSocketConnectParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public NetAddress remoteAddr;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UdpSocket_Internal.BIND_ORDINAL];
            dataHeaderArr[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL] = new DataHeader(STRUCT_SIZE, UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL];
        }

        private UdpSocketConnectParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UdpSocketConnectParams() {
            this(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
        }

        public static UdpSocketConnectParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UdpSocketConnectParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UdpSocketConnectParams result = new UdpSocketConnectParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.remoteAddr = NetAddress.decode(decoder0.readPointer(8, false));
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.remoteAddr, 8, false);
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
            if (BindingsHelper.equals(this.remoteAddr, ((UdpSocketConnectParams) object).remoteAddr)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.remoteAddr);
        }
    }

    static final class UdpSocketConnectResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 32;
        private static final DataHeader[] VERSION_ARRAY;
        public NetAddress localAddr;
        public InterfaceRequest<UdpSocketReceiver> receiver;
        public NetworkError result;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UdpSocket_Internal.BIND_ORDINAL];
            dataHeaderArr[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL] = new DataHeader(STRUCT_SIZE, UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL];
        }

        private UdpSocketConnectResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UdpSocketConnectResponseParams() {
            this(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
        }

        public static UdpSocketConnectResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UdpSocketConnectResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UdpSocketConnectResponseParams result = new UdpSocketConnectResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.result = NetworkError.decode(decoder0.readPointer(8, false));
            }
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.localAddr = NetAddress.decode(decoder0.readPointer(16, true));
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.receiver = decoder0.readInterfaceRequest(24, true);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.result, 8, false);
            encoder0.encode(this.localAddr, 16, true);
            encoder0.encode(this.receiver, 24, true);
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
            UdpSocketConnectResponseParams other = (UdpSocketConnectResponseParams) object;
            if (!BindingsHelper.equals(this.result, other.result)) {
                return false;
            }
            if (!BindingsHelper.equals(this.localAddr, other.localAddr)) {
                return false;
            }
            if (BindingsHelper.equals(this.receiver, other.receiver)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = getClass().hashCode() + 31;
            return (((((result * 31) + BindingsHelper.hashCode(result)) * 31) + BindingsHelper.hashCode(this.localAddr)) * 31) + BindingsHelper.hashCode(this.receiver);
        }
    }

    static class UdpSocketConnectResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final ConnectResponse mCallback;

        UdpSocketConnectResponseParamsForwardToCallback(ConnectResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(UdpSocket_Internal.CONNECT_ORDINAL, UdpSocket_Internal.CONNECT_ORDINAL)) {
                    return false;
                }
                UdpSocketConnectResponseParams response = UdpSocketConnectResponseParams.deserialize(messageWithHeader.getPayload());
                this.mCallback.call(response.result, response.localAddr, response.receiver);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class UdpSocketNegotiateMaxPendingSendRequestsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public int requestedSize;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UdpSocket_Internal.BIND_ORDINAL];
            dataHeaderArr[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL] = new DataHeader(STRUCT_SIZE, UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL];
        }

        private UdpSocketNegotiateMaxPendingSendRequestsParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UdpSocketNegotiateMaxPendingSendRequestsParams() {
            this(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
        }

        public static UdpSocketNegotiateMaxPendingSendRequestsParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UdpSocketNegotiateMaxPendingSendRequestsParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UdpSocketNegotiateMaxPendingSendRequestsParams result = new UdpSocketNegotiateMaxPendingSendRequestsParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.requestedSize = decoder0.readInt(8);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.requestedSize, 8);
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
            if (this.requestedSize != ((UdpSocketNegotiateMaxPendingSendRequestsParams) object).requestedSize) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.requestedSize);
        }
    }

    static final class UdpSocketNegotiateMaxPendingSendRequestsResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public int actualSize;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UdpSocket_Internal.BIND_ORDINAL];
            dataHeaderArr[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL] = new DataHeader(STRUCT_SIZE, UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL];
        }

        private UdpSocketNegotiateMaxPendingSendRequestsResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UdpSocketNegotiateMaxPendingSendRequestsResponseParams() {
            this(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
        }

        public static UdpSocketNegotiateMaxPendingSendRequestsResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UdpSocketNegotiateMaxPendingSendRequestsResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UdpSocketNegotiateMaxPendingSendRequestsResponseParams result = new UdpSocketNegotiateMaxPendingSendRequestsResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.actualSize = decoder0.readInt(8);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.actualSize, 8);
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
            if (this.actualSize != ((UdpSocketNegotiateMaxPendingSendRequestsResponseParams) object).actualSize) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.actualSize);
        }
    }

    /* renamed from: org.chromium.mojom.mojo.UdpSocket_Internal.UdpSocketNegotiateMaxPendingSendRequestsResponseParamsForwardToCallback */
    static class C0634x2e31c735 extends SideEffectFreeCloseable implements MessageReceiver {
        private final NegotiateMaxPendingSendRequestsResponse mCallback;

        C0634x2e31c735(NegotiateMaxPendingSendRequestsResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(UdpSocket_Internal.NEGOTIATE_MAX_PENDING_SEND_REQUESTS_ORDINAL, UdpSocket_Internal.CONNECT_ORDINAL)) {
                    return false;
                }
                this.mCallback.call(Integer.valueOf(UdpSocketNegotiateMaxPendingSendRequestsResponseParams.deserialize(messageWithHeader.getPayload()).actualSize));
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class UdpSocketReceiveMoreParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public int datagramNumber;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UdpSocket_Internal.BIND_ORDINAL];
            dataHeaderArr[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL] = new DataHeader(STRUCT_SIZE, UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL];
        }

        private UdpSocketReceiveMoreParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UdpSocketReceiveMoreParams() {
            this(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
        }

        public static UdpSocketReceiveMoreParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UdpSocketReceiveMoreParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UdpSocketReceiveMoreParams result = new UdpSocketReceiveMoreParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.datagramNumber = decoder0.readInt(8);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.datagramNumber, 8);
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
            if (this.datagramNumber != ((UdpSocketReceiveMoreParams) object).datagramNumber) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.datagramNumber);
        }
    }

    static final class UdpSocketSendToParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 24;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] data;
        public NetAddress destAddr;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UdpSocket_Internal.BIND_ORDINAL];
            dataHeaderArr[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL] = new DataHeader(STRUCT_SIZE, UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL];
        }

        private UdpSocketSendToParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UdpSocketSendToParams() {
            this(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
        }

        public static UdpSocketSendToParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UdpSocketSendToParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UdpSocketSendToParams result = new UdpSocketSendToParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion >= 0) {
                result.destAddr = NetAddress.decode(decoder0.readPointer(8, true));
            }
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.data = decoder0.readBytes(16, UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL, -1);
            return result;
        }

        protected final void encode(Encoder encoder) {
            Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoder0.encode(this.destAddr, 8, true);
            encoder0.encode(this.data, 16, (int) UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL, -1);
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
            UdpSocketSendToParams other = (UdpSocketSendToParams) object;
            if (!BindingsHelper.equals(this.destAddr, other.destAddr)) {
                return false;
            }
            if (Arrays.equals(this.data, other.data)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.destAddr)) * 31) + Arrays.hashCode(this.data);
        }
    }

    static final class UdpSocketSendToResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public NetworkError result;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UdpSocket_Internal.BIND_ORDINAL];
            dataHeaderArr[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL] = new DataHeader(STRUCT_SIZE, UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL];
        }

        private UdpSocketSendToResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UdpSocketSendToResponseParams() {
            this(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
        }

        public static UdpSocketSendToResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UdpSocketSendToResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UdpSocketSendToResponseParams result = new UdpSocketSendToResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.result = NetworkError.decode(decoder0.readPointer(8, false));
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8, false);
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
            if (BindingsHelper.equals(this.result, ((UdpSocketSendToResponseParams) object).result)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = getClass().hashCode() + 31;
            return (result * 31) + BindingsHelper.hashCode(result);
        }
    }

    static class UdpSocketSendToResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final SendToResponse mCallback;

        UdpSocketSendToResponseParamsForwardToCallback(SendToResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(UdpSocket_Internal.SEND_TO_ORDINAL, UdpSocket_Internal.CONNECT_ORDINAL)) {
                    return false;
                }
                this.mCallback.call(UdpSocketSendToResponseParams.deserialize(messageWithHeader.getPayload()).result);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class UdpSocketSetReceiveBufferSizeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public int size;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UdpSocket_Internal.BIND_ORDINAL];
            dataHeaderArr[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL] = new DataHeader(STRUCT_SIZE, UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL];
        }

        private UdpSocketSetReceiveBufferSizeParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UdpSocketSetReceiveBufferSizeParams() {
            this(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
        }

        public static UdpSocketSetReceiveBufferSizeParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UdpSocketSetReceiveBufferSizeParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UdpSocketSetReceiveBufferSizeParams result = new UdpSocketSetReceiveBufferSizeParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.size = decoder0.readInt(8);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.size, 8);
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
            if (this.size != ((UdpSocketSetReceiveBufferSizeParams) object).size) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.size);
        }
    }

    static final class UdpSocketSetReceiveBufferSizeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public NetworkError result;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UdpSocket_Internal.BIND_ORDINAL];
            dataHeaderArr[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL] = new DataHeader(STRUCT_SIZE, UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL];
        }

        private UdpSocketSetReceiveBufferSizeResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UdpSocketSetReceiveBufferSizeResponseParams() {
            this(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
        }

        public static UdpSocketSetReceiveBufferSizeResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UdpSocketSetReceiveBufferSizeResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UdpSocketSetReceiveBufferSizeResponseParams result = new UdpSocketSetReceiveBufferSizeResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.result = NetworkError.decode(decoder0.readPointer(8, false));
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8, false);
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
            if (BindingsHelper.equals(this.result, ((UdpSocketSetReceiveBufferSizeResponseParams) object).result)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = getClass().hashCode() + 31;
            return (result * 31) + BindingsHelper.hashCode(result);
        }
    }

    static class UdpSocketSetReceiveBufferSizeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final SetReceiveBufferSizeResponse mCallback;

        UdpSocketSetReceiveBufferSizeResponseParamsForwardToCallback(SetReceiveBufferSizeResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(UdpSocket_Internal.SET_RECEIVE_BUFFER_SIZE_ORDINAL, UdpSocket_Internal.CONNECT_ORDINAL)) {
                    return false;
                }
                this.mCallback.call(UdpSocketSetReceiveBufferSizeResponseParams.deserialize(messageWithHeader.getPayload()).result);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class UdpSocketSetSendBufferSizeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public int size;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UdpSocket_Internal.BIND_ORDINAL];
            dataHeaderArr[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL] = new DataHeader(STRUCT_SIZE, UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL];
        }

        private UdpSocketSetSendBufferSizeParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UdpSocketSetSendBufferSizeParams() {
            this(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
        }

        public static UdpSocketSetSendBufferSizeParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UdpSocketSetSendBufferSizeParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UdpSocketSetSendBufferSizeParams result = new UdpSocketSetSendBufferSizeParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.size = decoder0.readInt(8);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.size, 8);
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
            if (this.size != ((UdpSocketSetSendBufferSizeParams) object).size) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.size);
        }
    }

    static final class UdpSocketSetSendBufferSizeResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY;
        public NetworkError result;

        static {
            DataHeader[] dataHeaderArr = new DataHeader[UdpSocket_Internal.BIND_ORDINAL];
            dataHeaderArr[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL] = new DataHeader(STRUCT_SIZE, UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = VERSION_ARRAY[UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL];
        }

        private UdpSocketSetSendBufferSizeResponseParams(int version) {
            super(STRUCT_SIZE, version);
        }

        public UdpSocketSetSendBufferSizeResponseParams() {
            this(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL);
        }

        public static UdpSocketSetSendBufferSizeResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static UdpSocketSetSendBufferSizeResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            UdpSocketSetSendBufferSizeResponseParams result = new UdpSocketSetSendBufferSizeResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.result = NetworkError.decode(decoder0.readPointer(8, false));
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.result, 8, false);
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
            if (BindingsHelper.equals(this.result, ((UdpSocketSetSendBufferSizeResponseParams) object).result)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = getClass().hashCode() + 31;
            return (result * 31) + BindingsHelper.hashCode(result);
        }
    }

    static class UdpSocketSetSendBufferSizeResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final SetSendBufferSizeResponse mCallback;

        UdpSocketSetSendBufferSizeResponseParamsForwardToCallback(SetSendBufferSizeResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(UdpSocket_Internal.SET_SEND_BUFFER_SIZE_ORDINAL, UdpSocket_Internal.CONNECT_ORDINAL)) {
                    return false;
                }
                this.mCallback.call(UdpSocketSetSendBufferSizeResponseParams.deserialize(messageWithHeader.getPayload()).result);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static class UdpSocketAllowAddressReuseResponseParamsProxyToResponder implements AllowAddressReuseResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UdpSocketAllowAddressReuseResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(NetworkError result) {
            UdpSocketAllowAddressReuseResponseParams _response = new UdpSocketAllowAddressReuseResponseParams();
            _response.result = result;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL, UdpSocket_Internal.CONNECT_ORDINAL, this.mRequestId)));
        }
    }

    static class UdpSocketBindResponseParamsProxyToResponder implements BindResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UdpSocketBindResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(NetworkError result, NetAddress boundAddr, InterfaceRequest<UdpSocketReceiver> receiver) {
            UdpSocketBindResponseParams _response = new UdpSocketBindResponseParams();
            _response.result = result;
            _response.boundAddr = boundAddr;
            _response.receiver = receiver;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(UdpSocket_Internal.BIND_ORDINAL, UdpSocket_Internal.CONNECT_ORDINAL, this.mRequestId)));
        }
    }

    static class UdpSocketConnectResponseParamsProxyToResponder implements ConnectResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UdpSocketConnectResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(NetworkError result, NetAddress localAddr, InterfaceRequest<UdpSocketReceiver> receiver) {
            UdpSocketConnectResponseParams _response = new UdpSocketConnectResponseParams();
            _response.result = result;
            _response.localAddr = localAddr;
            _response.receiver = receiver;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(UdpSocket_Internal.CONNECT_ORDINAL, UdpSocket_Internal.CONNECT_ORDINAL, this.mRequestId)));
        }
    }

    /* renamed from: org.chromium.mojom.mojo.UdpSocket_Internal.UdpSocketNegotiateMaxPendingSendRequestsResponseParamsProxyToResponder */
    static class C0682x99409bc9 implements NegotiateMaxPendingSendRequestsResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        C0682x99409bc9(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(Integer actualSize) {
            UdpSocketNegotiateMaxPendingSendRequestsResponseParams _response = new UdpSocketNegotiateMaxPendingSendRequestsResponseParams();
            _response.actualSize = actualSize.intValue();
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(UdpSocket_Internal.NEGOTIATE_MAX_PENDING_SEND_REQUESTS_ORDINAL, UdpSocket_Internal.CONNECT_ORDINAL, this.mRequestId)));
        }
    }

    static class UdpSocketSendToResponseParamsProxyToResponder implements SendToResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UdpSocketSendToResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(NetworkError result) {
            UdpSocketSendToResponseParams _response = new UdpSocketSendToResponseParams();
            _response.result = result;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(UdpSocket_Internal.SEND_TO_ORDINAL, UdpSocket_Internal.CONNECT_ORDINAL, this.mRequestId)));
        }
    }

    static class UdpSocketSetReceiveBufferSizeResponseParamsProxyToResponder implements SetReceiveBufferSizeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UdpSocketSetReceiveBufferSizeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(NetworkError result) {
            UdpSocketSetReceiveBufferSizeResponseParams _response = new UdpSocketSetReceiveBufferSizeResponseParams();
            _response.result = result;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(UdpSocket_Internal.SET_RECEIVE_BUFFER_SIZE_ORDINAL, UdpSocket_Internal.CONNECT_ORDINAL, this.mRequestId)));
        }
    }

    static class UdpSocketSetSendBufferSizeResponseParamsProxyToResponder implements SetSendBufferSizeResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        UdpSocketSetSendBufferSizeResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(NetworkError result) {
            UdpSocketSetSendBufferSizeResponseParams _response = new UdpSocketSetSendBufferSizeResponseParams();
            _response.result = result;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(UdpSocket_Internal.SET_SEND_BUFFER_SIZE_ORDINAL, UdpSocket_Internal.CONNECT_ORDINAL, this.mRequestId)));
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<UdpSocket> {
        Stub(Core core, UdpSocket impl) {
            super(core, impl);
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                MessageHeader header = messageWithHeader.getHeader();
                if (!header.validateHeader(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL)) {
                    return false;
                }
                switch (header.getType()) {
                    case XWalkResourceClientInternal.ERROR_HOST_LOOKUP /*-2*/:
                        return InterfaceControlMessagesHelper.handleRunOrClosePipe(UdpSocket_Internal.MANAGER, messageWithHeader);
                    case UdpSocket_Internal.RECEIVE_MORE_ORDINAL /*6*/:
                        ((UdpSocket) getImpl()).receiveMore(UdpSocketReceiveMoreParams.deserialize(messageWithHeader.getPayload()).datagramNumber);
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
                if (!header.validateHeader(UdpSocket_Internal.BIND_ORDINAL)) {
                    return false;
                }
                switch (header.getType()) {
                    case ExploreByTouchHelper.HOST_ID /*-1*/:
                        return InterfaceControlMessagesHelper.handleRun(getCore(), UdpSocket_Internal.MANAGER, messageWithHeader, receiver);
                    case UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL /*0*/:
                        UdpSocketAllowAddressReuseParams.deserialize(messageWithHeader.getPayload());
                        ((UdpSocket) getImpl()).allowAddressReuse(new UdpSocketAllowAddressReuseResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    case UdpSocket_Internal.BIND_ORDINAL /*1*/:
                        ((UdpSocket) getImpl()).bind(UdpSocketBindParams.deserialize(messageWithHeader.getPayload()).addr, new UdpSocketBindResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    case UdpSocket_Internal.CONNECT_ORDINAL /*2*/:
                        ((UdpSocket) getImpl()).connect(UdpSocketConnectParams.deserialize(messageWithHeader.getPayload()).remoteAddr, new UdpSocketConnectResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    case UdpSocket_Internal.SET_SEND_BUFFER_SIZE_ORDINAL /*3*/:
                        ((UdpSocket) getImpl()).setSendBufferSize(UdpSocketSetSendBufferSizeParams.deserialize(messageWithHeader.getPayload()).size, new UdpSocketSetSendBufferSizeResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    case UdpSocket_Internal.SET_RECEIVE_BUFFER_SIZE_ORDINAL /*4*/:
                        ((UdpSocket) getImpl()).setReceiveBufferSize(UdpSocketSetReceiveBufferSizeParams.deserialize(messageWithHeader.getPayload()).size, new UdpSocketSetReceiveBufferSizeResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    case UdpSocket_Internal.NEGOTIATE_MAX_PENDING_SEND_REQUESTS_ORDINAL /*5*/:
                        ((UdpSocket) getImpl()).negotiateMaxPendingSendRequests(UdpSocketNegotiateMaxPendingSendRequestsParams.deserialize(messageWithHeader.getPayload()).requestedSize, new C0682x99409bc9(getCore(), receiver, header.getRequestId()));
                        return true;
                    case UdpSocket_Internal.SEND_TO_ORDINAL /*7*/:
                        UdpSocketSendToParams data = UdpSocketSendToParams.deserialize(messageWithHeader.getPayload());
                        ((UdpSocket) getImpl()).sendTo(data.destAddr, data.data, new UdpSocketSendToResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    default:
                        return false;
                }
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.UdpSocket.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void allowAddressReuse(AllowAddressReuseResponse callback) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new UdpSocketAllowAddressReuseParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(UdpSocket_Internal.ALLOW_ADDRESS_REUSE_ORDINAL, UdpSocket_Internal.BIND_ORDINAL, 0)), new UdpSocketAllowAddressReuseResponseParamsForwardToCallback(callback));
        }

        public void bind(NetAddress addr, BindResponse callback) {
            UdpSocketBindParams _message = new UdpSocketBindParams();
            _message.addr = addr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(UdpSocket_Internal.BIND_ORDINAL, UdpSocket_Internal.BIND_ORDINAL, 0)), new UdpSocketBindResponseParamsForwardToCallback(callback));
        }

        public void connect(NetAddress remoteAddr, ConnectResponse callback) {
            UdpSocketConnectParams _message = new UdpSocketConnectParams();
            _message.remoteAddr = remoteAddr;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(UdpSocket_Internal.CONNECT_ORDINAL, UdpSocket_Internal.BIND_ORDINAL, 0)), new UdpSocketConnectResponseParamsForwardToCallback(callback));
        }

        public void setSendBufferSize(int size, SetSendBufferSizeResponse callback) {
            UdpSocketSetSendBufferSizeParams _message = new UdpSocketSetSendBufferSizeParams();
            _message.size = size;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(UdpSocket_Internal.SET_SEND_BUFFER_SIZE_ORDINAL, UdpSocket_Internal.BIND_ORDINAL, 0)), new UdpSocketSetSendBufferSizeResponseParamsForwardToCallback(callback));
        }

        public void setReceiveBufferSize(int size, SetReceiveBufferSizeResponse callback) {
            UdpSocketSetReceiveBufferSizeParams _message = new UdpSocketSetReceiveBufferSizeParams();
            _message.size = size;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(UdpSocket_Internal.SET_RECEIVE_BUFFER_SIZE_ORDINAL, UdpSocket_Internal.BIND_ORDINAL, 0)), new UdpSocketSetReceiveBufferSizeResponseParamsForwardToCallback(callback));
        }

        public void negotiateMaxPendingSendRequests(int requestedSize, NegotiateMaxPendingSendRequestsResponse callback) {
            UdpSocketNegotiateMaxPendingSendRequestsParams _message = new UdpSocketNegotiateMaxPendingSendRequestsParams();
            _message.requestedSize = requestedSize;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(UdpSocket_Internal.NEGOTIATE_MAX_PENDING_SEND_REQUESTS_ORDINAL, UdpSocket_Internal.BIND_ORDINAL, 0)), new C0634x2e31c735(callback));
        }

        public void receiveMore(int datagramNumber) {
            UdpSocketReceiveMoreParams _message = new UdpSocketReceiveMoreParams();
            _message.datagramNumber = datagramNumber;
            getProxyHandler().getMessageReceiver().accept(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader((int) UdpSocket_Internal.RECEIVE_MORE_ORDINAL)));
        }

        public void sendTo(NetAddress destAddr, byte[] data, SendToResponse callback) {
            UdpSocketSendToParams _message = new UdpSocketSendToParams();
            _message.destAddr = destAddr;
            _message.data = data;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(UdpSocket_Internal.SEND_TO_ORDINAL, UdpSocket_Internal.BIND_ORDINAL, 0)), new UdpSocketSendToResponseParamsForwardToCallback(callback));
        }
    }

    UdpSocket_Internal() {
    }

    static {
        MANAGER = new C06331();
    }
}
