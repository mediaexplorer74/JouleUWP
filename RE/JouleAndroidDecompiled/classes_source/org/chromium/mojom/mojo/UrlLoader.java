package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Callbacks.Callback1;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;

public interface UrlLoader extends Interface {
    public static final Manager<UrlLoader, Proxy> MANAGER;

    public interface FollowRedirectResponse extends Callback1<UrlResponse> {
    }

    public interface QueryStatusResponse extends Callback1<UrlLoaderStatus> {
    }

    public interface StartResponse extends Callback1<UrlResponse> {
    }

    public interface Proxy extends UrlLoader, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void followRedirect(FollowRedirectResponse followRedirectResponse);

    void queryStatus(QueryStatusResponse queryStatusResponse);

    void start(UrlRequest urlRequest, StartResponse startResponse);

    static {
        MANAGER = UrlLoader_Internal.MANAGER;
    }
}
