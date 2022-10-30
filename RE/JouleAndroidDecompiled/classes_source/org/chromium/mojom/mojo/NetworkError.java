package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class NetworkError extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final int STRUCT_SIZE = 24;
    private static final DataHeader[] VERSION_ARRAY;
    public int code;
    public String description;

    static {
        VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
        DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    }

    private NetworkError(int version) {
        super(STRUCT_SIZE, version);
    }

    public NetworkError() {
        this(0);
    }

    public static NetworkError deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static NetworkError decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        NetworkError result = new NetworkError(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.code = decoder0.readInt(8);
        }
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.description = decoder0.readString(16, true);
        return result;
    }

    protected final void encode(Encoder encoder) {
        Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoder0.encode(this.code, 8);
        encoder0.encode(this.description, 16, true);
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
        NetworkError other = (NetworkError) object;
        if (this.code != other.code) {
            return false;
        }
        if (BindingsHelper.equals(this.description, other.description)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.code)) * 31) + BindingsHelper.hashCode(this.description);
    }
}
