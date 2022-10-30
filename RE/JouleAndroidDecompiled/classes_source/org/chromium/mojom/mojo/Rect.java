package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class Rect extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final int STRUCT_SIZE = 24;
    private static final DataHeader[] VERSION_ARRAY;
    public int height;
    public int width;
    public int f18x;
    public int f19y;

    static {
        VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
        DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    }

    private Rect(int version) {
        super(STRUCT_SIZE, version);
    }

    public Rect() {
        this(0);
    }

    public static Rect deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static Rect decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        Rect result = new Rect(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.f18x = decoder0.readInt(8);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.f19y = decoder0.readInt(12);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.width = decoder0.readInt(16);
        }
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.height = decoder0.readInt(20);
        return result;
    }

    protected final void encode(Encoder encoder) {
        Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoder0.encode(this.f18x, 8);
        encoder0.encode(this.f19y, 12);
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
        Rect other = (Rect) object;
        if (this.f18x != other.f18x) {
            return false;
        }
        if (this.f19y != other.f19y) {
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
        return ((((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.f18x)) * 31) + BindingsHelper.hashCode(this.f19y)) * 31) + BindingsHelper.hashCode(this.width)) * 31) + BindingsHelper.hashCode(this.height);
    }
}
