package org.chromium.mojom.mojo;

import org.chromium.mojo.bindings.Callbacks.Callback1;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.bindings.InterfaceRequest;

public interface Application extends Interface {
    public static final Manager<Application, Proxy> MANAGER;

    public interface OnQuitRequestedResponse extends Callback1<Boolean> {
    }

    public interface Proxy extends Application, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void acceptConnection(String str, InterfaceRequest<ServiceProvider> interfaceRequest, ServiceProvider serviceProvider, String str2);

    void initialize(Shell shell, String str);

    void onQuitRequested(OnQuitRequestedResponse onQuitRequestedResponse);

    static {
        MANAGER = Application_Internal.MANAGER;
    }
}
