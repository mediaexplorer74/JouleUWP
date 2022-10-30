package org.chromium.mojo.bindings;

import java.io.Closeable;
import org.chromium.mojo.system.Handle;

public interface HandleOwner<H extends Handle> extends Closeable {
    void close();

    H passHandle();
}
