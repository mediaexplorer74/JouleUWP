package org.chromium.mojom.mojo;

import java.util.Arrays;
import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class NetAddressIPv6 extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final int STRUCT_SIZE = 24;
    private static final DataHeader[] VERSION_ARRAY;
    public byte[] addr;
    public short port;

    static {
        VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
        DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    }

    private NetAddressIPv6(int version) {
        super(STRUCT_SIZE, version);
    }

    public NetAddressIPv6() {
        this(0);
    }

    public static NetAddressIPv6 deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static NetAddressIPv6 decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        NetAddressIPv6 result = new NetAddressIPv6(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.port = decoder0.readShort(8);
        }
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.addr = decoder0.readBytes(16, 0, 16);
        return result;
    }

    protected final void encode(Encoder encoder) {
        Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoder0.encode(this.port, 8);
        encoder0.encode(this.addr, 16, 0, 16);
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
        NetAddressIPv6 other = (NetAddressIPv6) object;
        if (this.port != other.port) {
            return false;
        }
        if (Arrays.equals(this.addr, other.addr)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.port)) * 31) + Arrays.hashCode(this.addr);
    }
}
