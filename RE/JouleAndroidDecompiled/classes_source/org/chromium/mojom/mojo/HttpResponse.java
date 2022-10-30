package org.chromium.mojom.mojo;

import java.util.Arrays;
import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.DataPipe.ConsumerHandle;
import org.chromium.mojo.system.InvalidHandle;

public final class HttpResponse extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final int STRUCT_SIZE = 24;
    private static final DataHeader[] VERSION_ARRAY;
    public ConsumerHandle body;
    public HttpHeader[] headers;
    public int statusCode;

    static {
        VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
        DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    }

    private HttpResponse(int version) {
        super(STRUCT_SIZE, version);
        this.statusCode = 200;
        this.body = InvalidHandle.INSTANCE;
    }

    public HttpResponse() {
        this(0);
    }

    public static HttpResponse deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static HttpResponse decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        HttpResponse result = new HttpResponse(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.statusCode = decoder0.readInt(8);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.body = decoder0.readConsumerHandle(12, true);
        }
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        Decoder decoder1 = decoder0.readPointer(16, true);
        if (decoder1 == null) {
            result.headers = null;
            return result;
        }
        DataHeader si1 = decoder1.readDataHeaderForPointerArray(-1);
        result.headers = new HttpHeader[si1.elementsOrVersion];
        for (int i1 = 0; i1 < si1.elementsOrVersion; i1++) {
            result.headers[i1] = HttpHeader.decode(decoder1.readPointer((i1 * 8) + 8, false));
        }
        return result;
    }

    protected final void encode(Encoder encoder) {
        Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoder0.encode(this.statusCode, 8);
        encoder0.encode(this.body, 12, true);
        if (this.headers == null) {
            encoder0.encodeNullPointer(16, true);
            return;
        }
        Encoder encoder1 = encoder0.encodePointerArray(this.headers.length, 16, -1);
        for (int i0 = 0; i0 < this.headers.length; i0++) {
            encoder1.encode(this.headers[i0], (i0 * 8) + 8, false);
        }
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
        HttpResponse other = (HttpResponse) object;
        if (this.statusCode != other.statusCode) {
            return false;
        }
        if (!Arrays.deepEquals(this.headers, other.headers)) {
            return false;
        }
        if (BindingsHelper.equals(this.body, other.body)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.statusCode)) * 31) + Arrays.deepHashCode(this.headers)) * 31) + BindingsHelper.hashCode(this.body);
    }
}
