package org.chromium.mojo.bindings;

import java.nio.ByteBuffer;

public class MessageHeader {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final int FLAGS_OFFSET = 12;
    public static final int MESSAGE_EXPECTS_RESPONSE_FLAG = 1;
    public static final int MESSAGE_IS_RESPONSE_FLAG = 2;
    private static final int MESSAGE_WITH_REQUEST_ID_SIZE = 24;
    private static final DataHeader MESSAGE_WITH_REQUEST_ID_STRUCT_INFO;
    private static final int MESSAGE_WITH_REQUEST_ID_VERSION = 1;
    public static final int NO_FLAG = 0;
    private static final int REQUEST_ID_OFFSET = 16;
    private static final int SIMPLE_MESSAGE_SIZE = 16;
    private static final DataHeader SIMPLE_MESSAGE_STRUCT_INFO;
    private static final int SIMPLE_MESSAGE_VERSION = 0;
    private static final int TYPE_OFFSET = 8;
    private final DataHeader mDataHeader;
    private final int mFlags;
    private long mRequestId;
    private final int mType;

    static {
        $assertionsDisabled = !MessageHeader.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        SIMPLE_MESSAGE_STRUCT_INFO = new DataHeader(SIMPLE_MESSAGE_SIZE, SIMPLE_MESSAGE_VERSION);
        MESSAGE_WITH_REQUEST_ID_STRUCT_INFO = new DataHeader(MESSAGE_WITH_REQUEST_ID_SIZE, MESSAGE_WITH_REQUEST_ID_VERSION);
    }

    public MessageHeader(int type) {
        this.mDataHeader = SIMPLE_MESSAGE_STRUCT_INFO;
        this.mType = type;
        this.mFlags = SIMPLE_MESSAGE_VERSION;
        this.mRequestId = 0;
    }

    public MessageHeader(int type, int flags, long requestId) {
        if ($assertionsDisabled || mustHaveRequestId(flags)) {
            this.mDataHeader = MESSAGE_WITH_REQUEST_ID_STRUCT_INFO;
            this.mType = type;
            this.mFlags = flags;
            this.mRequestId = requestId;
            return;
        }
        throw new AssertionError();
    }

    MessageHeader(Message message) {
        Decoder decoder = new Decoder(message);
        this.mDataHeader = decoder.readDataHeader();
        validateDataHeader(this.mDataHeader);
        this.mType = decoder.readInt(TYPE_OFFSET);
        this.mFlags = decoder.readInt(FLAGS_OFFSET);
        if (!mustHaveRequestId(this.mFlags)) {
            this.mRequestId = 0;
        } else if (this.mDataHeader.size < MESSAGE_WITH_REQUEST_ID_SIZE) {
            throw new DeserializationException("Incorrect message size, expecting at least 24 for a message with a request identifier, but got: " + this.mDataHeader.size);
        } else {
            this.mRequestId = decoder.readLong(SIMPLE_MESSAGE_SIZE);
        }
    }

    public int getSize() {
        return this.mDataHeader.size;
    }

    public int getType() {
        return this.mType;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public boolean hasFlag(int flag) {
        return (this.mFlags & flag) == flag ? true : $assertionsDisabled;
    }

    public boolean hasRequestId() {
        return mustHaveRequestId(this.mFlags);
    }

    public long getRequestId() {
        if ($assertionsDisabled || hasRequestId()) {
            return this.mRequestId;
        }
        throw new AssertionError();
    }

    public void encode(Encoder encoder) {
        encoder.encode(this.mDataHeader);
        encoder.encode(getType(), (int) TYPE_OFFSET);
        encoder.encode(getFlags(), (int) FLAGS_OFFSET);
        if (hasRequestId()) {
            encoder.encode(getRequestId(), (int) SIMPLE_MESSAGE_SIZE);
        }
    }

    public boolean validateHeader(int expectedFlags) {
        return (getFlags() & 3) == expectedFlags ? true : $assertionsDisabled;
    }

    public boolean validateHeader(int expectedType, int expectedFlags) {
        return (getType() == expectedType && validateHeader(expectedFlags)) ? true : $assertionsDisabled;
    }

    public int hashCode() {
        return (((((((this.mDataHeader == null ? SIMPLE_MESSAGE_VERSION : this.mDataHeader.hashCode()) + 31) * 31) + this.mFlags) * 31) + ((int) (this.mRequestId ^ (this.mRequestId >>> 32)))) * 31) + this.mType;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return $assertionsDisabled;
        }
        if (getClass() != object.getClass()) {
            return $assertionsDisabled;
        }
        MessageHeader other = (MessageHeader) object;
        if (BindingsHelper.equals(this.mDataHeader, other.mDataHeader) && this.mFlags == other.mFlags && this.mRequestId == other.mRequestId && this.mType == other.mType) {
            return true;
        }
        return $assertionsDisabled;
    }

    void setRequestId(ByteBuffer buffer, long requestId) {
        if ($assertionsDisabled || mustHaveRequestId(buffer.getInt(FLAGS_OFFSET))) {
            buffer.putLong(SIMPLE_MESSAGE_SIZE, requestId);
            this.mRequestId = requestId;
            return;
        }
        throw new AssertionError();
    }

    private static boolean mustHaveRequestId(int flags) {
        return (flags & 3) != 0 ? true : $assertionsDisabled;
    }

    private static void validateDataHeader(DataHeader dataHeader) {
        if (dataHeader.elementsOrVersion < 0) {
            throw new DeserializationException("Incorrect number of fields, expecting at least 0, but got: " + dataHeader.elementsOrVersion);
        } else if (dataHeader.size < SIMPLE_MESSAGE_SIZE) {
            throw new DeserializationException("Incorrect message size, expecting at least 16, but got: " + dataHeader.size);
        } else if (dataHeader.elementsOrVersion == 0 && dataHeader.size != SIMPLE_MESSAGE_SIZE) {
            throw new DeserializationException("Incorrect message size for a message with 0 fields, expecting 16, but got: " + dataHeader.size);
        } else if (dataHeader.elementsOrVersion == MESSAGE_WITH_REQUEST_ID_VERSION && dataHeader.size != MESSAGE_WITH_REQUEST_ID_SIZE) {
            throw new DeserializationException("Incorrect message size for a message with 1 fields, expecting 24, but got: " + dataHeader.size);
        }
    }
}
