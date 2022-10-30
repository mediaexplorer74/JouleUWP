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

public final class UrlResponse extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final int STRUCT_SIZE = 96;
    private static final DataHeader[] VERSION_ARRAY;
    public ConsumerHandle body;
    public String charset;
    public NetworkError error;
    public HttpHeader[] headers;
    public String mimeType;
    public String redirectMethod;
    public String redirectReferrer;
    public String redirectUrl;
    public String site;
    public int statusCode;
    public String statusLine;
    public String url;

    static {
        VERSION_ARRAY = new DataHeader[]{new DataHeader(STRUCT_SIZE, 0)};
        DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    }

    private UrlResponse(int version) {
        super(STRUCT_SIZE, version);
        this.body = InvalidHandle.INSTANCE;
    }

    public UrlResponse() {
        this(0);
    }

    public static UrlResponse deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static UrlResponse decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        UrlResponse result = new UrlResponse(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.error = NetworkError.decode(decoder0.readPointer(8, true));
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.body = decoder0.readConsumerHandle(16, true);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.statusCode = decoder0.readInt(20);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.url = decoder0.readString(24, true);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.site = decoder0.readString(32, true);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.statusLine = decoder0.readString(40, true);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            Decoder decoder1 = decoder0.readPointer(48, true);
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
            result.mimeType = decoder0.readString(56, true);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.charset = decoder0.readString(64, true);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.redirectMethod = decoder0.readString(72, true);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.redirectUrl = decoder0.readString(80, true);
        }
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.redirectReferrer = decoder0.readString(88, true);
        return result;
    }

    protected final void encode(Encoder encoder) {
        Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoder0.encode(this.error, 8, true);
        encoder0.encode(this.body, 16, true);
        encoder0.encode(this.statusCode, 20);
        encoder0.encode(this.url, 24, true);
        encoder0.encode(this.site, 32, true);
        encoder0.encode(this.statusLine, 40, true);
        if (this.headers == null) {
            encoder0.encodeNullPointer(48, true);
        } else {
            Encoder encoder1 = encoder0.encodePointerArray(this.headers.length, 48, -1);
            for (int i0 = 0; i0 < this.headers.length; i0++) {
                encoder1.encode(this.headers[i0], (i0 * 8) + 8, false);
            }
        }
        encoder0.encode(this.mimeType, 56, true);
        encoder0.encode(this.charset, 64, true);
        encoder0.encode(this.redirectMethod, 72, true);
        encoder0.encode(this.redirectUrl, 80, true);
        encoder0.encode(this.redirectReferrer, 88, true);
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
        UrlResponse other = (UrlResponse) object;
        if (!BindingsHelper.equals(this.error, other.error)) {
            return false;
        }
        if (!BindingsHelper.equals(this.body, other.body)) {
            return false;
        }
        if (!BindingsHelper.equals(this.url, other.url)) {
            return false;
        }
        if (!BindingsHelper.equals(this.site, other.site)) {
            return false;
        }
        if (this.statusCode != other.statusCode) {
            return false;
        }
        if (!BindingsHelper.equals(this.statusLine, other.statusLine)) {
            return false;
        }
        if (!Arrays.deepEquals(this.headers, other.headers)) {
            return false;
        }
        if (!BindingsHelper.equals(this.mimeType, other.mimeType)) {
            return false;
        }
        if (!BindingsHelper.equals(this.charset, other.charset)) {
            return false;
        }
        if (!BindingsHelper.equals(this.redirectMethod, other.redirectMethod)) {
            return false;
        }
        if (!BindingsHelper.equals(this.redirectUrl, other.redirectUrl)) {
            return false;
        }
        if (BindingsHelper.equals(this.redirectReferrer, other.redirectReferrer)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((((((((((((((((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.error)) * 31) + BindingsHelper.hashCode(this.body)) * 31) + BindingsHelper.hashCode(this.url)) * 31) + BindingsHelper.hashCode(this.site)) * 31) + BindingsHelper.hashCode(this.statusCode)) * 31) + BindingsHelper.hashCode(this.statusLine)) * 31) + Arrays.deepHashCode(this.headers)) * 31) + BindingsHelper.hashCode(this.mimeType)) * 31) + BindingsHelper.hashCode(this.charset)) * 31) + BindingsHelper.hashCode(this.redirectMethod)) * 31) + BindingsHelper.hashCode(this.redirectUrl)) * 31) + BindingsHelper.hashCode(this.redirectReferrer);
    }
}
