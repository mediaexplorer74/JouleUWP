package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class NetAddress extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final int STRUCT_SIZE = 32;
    private static final DataHeader[] VERSION_ARRAY;
    public int family;
    public NetAddressIPv4 ipv4;
    public NetAddressIPv6 ipv6;

    static {
        VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
        DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    }

    private NetAddress(int version) {
        super(STRUCT_SIZE, version);
        this.family = 0;
    }

    public NetAddress() {
        this(0);
    }

    public static NetAddress deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static NetAddress decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        NetAddress result = new NetAddress(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.family = decoder0.readInt(8);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.ipv4 = NetAddressIPv4.decode(decoder0.readPointer(16, true));
        }
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.ipv6 = NetAddressIPv6.decode(decoder0.readPointer(24, true));
        return result;
    }

    protected final void encode(Encoder encoder) {
        Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoder0.encode(this.family, 8);
        encoder0.encode(this.ipv4, 16, true);
        encoder0.encode(this.ipv6, 24, true);
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
        NetAddress other = (NetAddress) object;
        if (this.family != other.family) {
            return false;
        }
        if (!BindingsHelper.equals(this.ipv4, other.ipv4)) {
            return false;
        }
        if (BindingsHelper.equals(this.ipv6, other.ipv6)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.family)) * 31) + BindingsHelper.hashCode(this.ipv4)) * 31) + BindingsHelper.hashCode(this.ipv6);
    }
}
