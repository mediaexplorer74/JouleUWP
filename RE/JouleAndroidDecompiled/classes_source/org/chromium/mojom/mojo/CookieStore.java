package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Callbacks.Callback1;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;

public interface CookieStore extends Interface {
    public static final Manager<CookieStore, Proxy> MANAGER;

    public interface GetResponse extends Callback1<String> {
    }

    public interface SetResponse extends Callback1<Boolean> {
    }

    public interface Proxy extends CookieStore, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void get(String str, GetResponse getResponse);

    void set(String str, String str2, SetResponse setResponse);

    static {
        MANAGER = CookieStore_Internal.MANAGER;
    }
}
