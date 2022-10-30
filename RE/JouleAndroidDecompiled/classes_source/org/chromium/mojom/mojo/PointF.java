package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class PointF extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final int STRUCT_SIZE = 16;
    private static final DataHeader[] VERSION_ARRAY;
    public float f16x;
    public float f17y;

    static {
        VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
        DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    }

    private PointF(int version) {
        super(STRUCT_SIZE, version);
    }

    public PointF() {
        this(0);
    }

    public static PointF deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static PointF decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        PointF result = new PointF(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.f16x = decoder0.readFloat(8);
        }
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.f17y = decoder0.readFloat(12);
        return result;
    }

    protected final void encode(Encoder encoder) {
        Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoder0.encode(this.f16x, 8);
        encoder0.encode(this.f17y, 12);
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
        PointF other = (PointF) object;
        if (this.f16x != other.f16x) {
            return false;
        }
        if (this.f17y != other.f17y) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.f16x)) * 31) + BindingsHelper.hashCode(this.f17y);
    }
}
