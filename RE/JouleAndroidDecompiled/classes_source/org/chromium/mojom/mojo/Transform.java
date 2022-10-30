package org.chromium.mojom.mojo;

import java.util.Arrays;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class Transform extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final int STRUCT_SIZE = 16;
    private static final DataHeader[] VERSION_ARRAY;
    public float[] matrix;

    static {
        VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
        DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    }

    private Transform(int version) {
        super(STRUCT_SIZE, version);
    }

    public Transform() {
        this(0);
    }

    public static Transform deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static Transform decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        Transform result = new Transform(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.matrix = decoder0.readFloats(8, 0, STRUCT_SIZE);
        return result;
    }

    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.matrix, 8, 0, (int) STRUCT_SIZE);
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
        if (Arrays.equals(this.matrix, ((Transform) object).matrix)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((getClass().hashCode() + 31) * 31) + Arrays.hashCode(this.matrix);
    }
}
