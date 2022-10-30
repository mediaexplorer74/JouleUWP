package org.chromium.mojo.bindings;

import java.nio.ByteBuffer;
import java.util.List;
import org.chromium.mojo.system.Handle;

public class Message {
    static final /* synthetic */ boolean $assertionsDisabled;
    private final ByteBuffer mBuffer;
    private final List<? extends Handle> mHandle;
    private ServiceMessage mWithHeader;

    static {
        $assertionsDisabled = !Message.class.desiredAssertionStatus();
    }

    public Message(ByteBuffer buffer, List<? extends Handle> handles) {
        this.mWithHeader = null;
        if ($assertionsDisabled || buffer.isDirect()) {
            this.mBuffer = buffer;
            this.mHandle = handles;
            return;
        }
        throw new AssertionError();
    }

    public ByteBuffer getData() {
        return this.mBuffer;
    }

    public List<? extends Handle> getHandles() {
        return this.mHandle;
    }

    public ServiceMessage asServiceMessage() {
        if (this.mWithHeader == null) {
            this.mWithHeader = new ServiceMessage(this);
        }
        return this.mWithHeader;
    }
}
