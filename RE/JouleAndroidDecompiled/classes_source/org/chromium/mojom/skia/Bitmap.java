package org.chromium.mojom.skia;

import java.util.Arrays;
import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class Bitmap extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final int STRUCT_SIZE = 40;
    private static final DataHeader[] VERSION_ARRAY;
    public int alphaType;
    public int colorType;
    public int height;
    public byte[] pixelData;
    public int profileType;
    public int width;

    static {
        VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
        DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    }

    private Bitmap(int version) {
        super(STRUCT_SIZE, version);
    }

    public Bitmap() {
        this(0);
    }

    public static Bitmap deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static Bitmap decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        Bitmap result = new Bitmap(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.colorType = decoder0.readInt(8);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.alphaType = decoder0.readInt(12);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.profileType = decoder0.readInt(16);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.width = decoder0.readInt(20);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.height = decoder0.readInt(24);
        }
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.pixelData = decoder0.readBytes(32, 0, -1);
        return result;
    }

    protected final void encode(Encoder encoder) {
        Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoder0.encode(this.colorType, 8);
        encoder0.encode(this.alphaType, 12);
        encoder0.encode(this.profileType, 16);
        encoder0.encode(this.width, 20);
        encoder0.encode(this.height, 24);
        encoder0.encode(this.pixelData, 32, 0, -1);
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
        Bitmap other = (Bitmap) object;
        if (this.colorType != other.colorType) {
            return false;
        }
        if (this.alphaType != other.alphaType) {
            return false;
        }
        if (this.profileType != other.profileType) {
            return false;
        }
        if (this.width != other.width) {
            return false;
        }
        if (this.height != other.height) {
            return false;
        }
        if (Arrays.equals(this.pixelData, other.pixelData)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.colorType)) * 31) + BindingsHelper.hashCode(this.alphaType)) * 31) + BindingsHelper.hashCode(this.profileType)) * 31) + BindingsHelper.hashCode(this.width)) * 31) + BindingsHelper.hashCode(this.height)) * 31) + Arrays.hashCode(this.pixelData);
    }
}
