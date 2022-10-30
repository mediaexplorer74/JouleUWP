package org.chromium.mojom.mojo;

import java.util.Arrays;
import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.DataPipe.ConsumerHandle;

public final class UrlRequest extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final int STRUCT_SIZE = 48;
    private static final DataHeader[] VERSION_ARRAY;
    public boolean autoFollowRedirects;
    public ConsumerHandle[] body;
    public boolean bypassCache;
    public HttpHeader[] headers;
    public String method;
    public int responseBodyBufferSize;
    public String url;

    static {
        VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
        DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    }

    private UrlRequest(int version) {
        super(STRUCT_SIZE, version);
        this.method = "GET";
        this.responseBodyBufferSize = 0;
        this.autoFollowRedirects = false;
        this.bypassCache = false;
    }

    public UrlRequest() {
        this(0);
    }

    public static UrlRequest deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static UrlRequest decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        UrlRequest result = new UrlRequest(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.url = decoder0.readString(8, false);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.method = decoder0.readString(16, false);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            Decoder decoder1 = decoder0.readPointer(24, true);
            if (decoder1 == null) {
                result.headers = null;
            } else {
                DataHeader si1 = decoder1.readDataHeaderForPointerArray(-1);
                result.headers = new HttpHeader[si1.elementsOrVersion];
                for (int i1 = 0; i1 < si1.elementsOrVersion; i1++) {
                    result.headers[i1] = HttpHeader.decode(decoder1.readPointer((i1 * 8) + 8, false));
                }
            }
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.body = decoder0.readConsumerHandles(32, 1, -1);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.responseBodyBufferSize = decoder0.readInt(40);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.autoFollowRedirects = decoder0.readBoolean(44, 0);
        }
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.bypassCache = decoder0.readBoolean(44, 1);
        return result;
    }

    protected final void encode(Encoder encoder) {
        Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoder0.encode(this.url, 8, false);
        encoder0.encode(this.method, 16, false);
        if (this.headers == null) {
            encoder0.encodeNullPointer(24, true);
        } else {
            Encoder encoder1 = encoder0.encodePointerArray(this.headers.length, 24, -1);
            for (int i0 = 0; i0 < this.headers.length; i0++) {
                encoder1.encode(this.headers[i0], (i0 * 8) + 8, false);
            }
        }
        encoder0.encode(this.body, 32, 1, -1);
        encoder0.encode(this.responseBodyBufferSize, 40);
        encoder0.encode(this.autoFollowRedirects, 44, 0);
        encoder0.encode(this.bypassCache, 44, 1);
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
        UrlRequest other = (UrlRequest) object;
        if (!BindingsHelper.equals(this.url, other.url)) {
            return false;
        }
        if (!BindingsHelper.equals(this.method, other.method)) {
            return false;
        }
        if (!Arrays.deepEquals(this.headers, other.headers)) {
            return false;
        }
        if (!Arrays.deepEquals(this.body, other.body)) {
            return false;
        }
        if (this.responseBodyBufferSize != other.responseBodyBufferSize) {
            return false;
        }
        if (this.autoFollowRedirects != other.autoFollowRedirects) {
            return false;
        }
        if (this.bypassCache != other.bypassCache) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((((((((((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.url)) * 31) + BindingsHelper.hashCode(this.method)) * 31) + Arrays.deepHashCode(this.headers)) * 31) + Arrays.deepHashCode(this.body)) * 31) + BindingsHelper.hashCode(this.responseBodyBufferSize)) * 31) + BindingsHelper.hashCode(this.autoFollowRedirects)) * 31) + BindingsHelper.hashCode(this.bypassCache);
    }
}
