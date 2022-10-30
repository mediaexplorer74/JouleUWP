package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class HttpHeader extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final int STRUCT_SIZE = 24;
    private static final DataHeader[] VERSION_ARRAY;
    public String name;
    public String value;

    static {
        VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
        DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    }

    private HttpHeader(int version) {
        super(STRUCT_SIZE, version);
    }

    public HttpHeader() {
        this(0);
    }

    public static HttpHeader deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static HttpHeader decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        HttpHeader result = new HttpHeader(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.name = decoder0.readString(8, false);
        }
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.value = decoder0.readString(16, false);
        return result;
    }

    protected final void encode(Encoder encoder) {
        Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoder0.encode(this.name, 8, false);
        encoder0.encode(this.value, 16, false);
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
        HttpHeader other = (HttpHeader) object;
        if (!BindingsHelper.equals(this.name, other.name)) {
            return false;
        }
        if (BindingsHelper.equals(this.value, other.value)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.name)) * 31) + BindingsHelper.hashCode(this.value);
    }
}
