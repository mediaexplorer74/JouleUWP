package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class RectF extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final int STRUCT_SIZE = 24;
    private static final DataHeader[] VERSION_ARRAY;
    public float height;
    public float width;
    public float f20x;
    public float f21y;

    static {
        VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
        DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    }

    private RectF(int version) {
        super(STRUCT_SIZE, version);
    }

    public RectF() {
        this(0);
    }

    public static RectF deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static RectF decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        RectF result = new RectF(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.f20x = decoder0.readFloat(8);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.f21y = decoder0.readFloat(12);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.width = decoder0.readFloat(16);
        }
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.height = decoder0.readFloat(20);
        return result;
    }

    protected final void encode(Encoder encoder) {
        Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoder0.encode(this.f20x, 8);
        encoder0.encode(this.f21y, 12);
        encoder0.encode(this.width, 16);
        encoder0.encode(this.height, 20);
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
        RectF other = (RectF) object;
        if (this.f20x != other.f20x) {
            return false;
        }
        if (this.f21y != other.f21y) {
            return false;
        }
        if (this.width != other.width) {
            return false;
        }
        if (this.height != other.height) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.f20x)) * 31) + BindingsHelper.hashCode(this.f21y)) * 31) + BindingsHelper.hashCode(this.width)) * 31) + BindingsHelper.hashCode(this.height);
    }
}
