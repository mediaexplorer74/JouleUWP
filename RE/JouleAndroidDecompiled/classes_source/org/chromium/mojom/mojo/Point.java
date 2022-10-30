package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class Point extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final int STRUCT_SIZE = 16;
    private static final DataHeader[] VERSION_ARRAY;
    public int f14x;
    public int f15y;

    static {
        VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
        DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    }

    private Point(int version) {
        super(STRUCT_SIZE, version);
    }

    public Point() {
        this(0);
    }

    public static Point deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static Point decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        Point result = new Point(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.f14x = decoder0.readInt(8);
        }
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.f15y = decoder0.readInt(12);
        return result;
    }

    protected final void encode(Encoder encoder) {
        Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoder0.encode(this.f14x, 8);
        encoder0.encode(this.f15y, 12);
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
        Point other = (Point) object;
        if (this.f14x != other.f14x) {
            return false;
        }
        if (this.f15y != other.f15y) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.f14x)) * 31) + BindingsHelper.hashCode(this.f15y);
    }
}
