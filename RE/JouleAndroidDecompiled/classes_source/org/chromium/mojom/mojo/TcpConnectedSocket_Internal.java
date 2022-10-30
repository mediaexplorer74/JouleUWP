package org.chromium.mojom.mojo;

import android.support.v4.widget.ExploreByTouchHelper;
import org.chromium.mojo.bindings.DeserializationException;
import org.chromium.mojo.bindings.Interface.AbstractProxy;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.bindings.InterfaceControlMessagesHelper;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.MessageHeader;
import org.chromium.mojo.bindings.MessageReceiver;
import org.chromium.mojo.bindings.MessageReceiverWithResponder;
import org.chromium.mojo.bindings.ServiceMessage;
import org.chromium.mojo.system.Core;
import org.xwalk.core.internal.XWalkResourceClientInternal;

class TcpConnectedSocket_Internal {
    public static final Manager<TcpConnectedSocket, org.chromium.mojom.mojo.TcpConnectedSocket.Proxy> MANAGER;

    /* renamed from: org.chromium.mojom.mojo.TcpConnectedSocket_Internal.1 */
    static class C06301 extends Manager<TcpConnectedSocket, org.chromium.mojom.mojo.TcpConnectedSocket.Proxy> {
        C06301() {
        }

        public String getName() {
            return "mojo::TCPConnectedSocket";
        }

        public int getVersion() {
            return 0;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, TcpConnectedSocket impl) {
            return new Stub(core, impl);
        }

        public TcpConnectedSocket[] buildArray(int size) {
            return new TcpConnectedSocket[size];
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<TcpConnectedSocket> {
        Stub(Core core, TcpConnectedSocket impl) {
            super(core, impl);
        }

        public boolean accept(Message message) {
            boolean z = false;
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                MessageHeader header = messageWithHeader.getHeader();
                if (header.validateHeader(0)) {
                    switch (header.getType()) {
                        case XWalkResourceClientInternal.ERROR_HOST_LOOKUP /*-2*/:
                            z = InterfaceControlMessagesHelper.handleRunOrClosePipe(TcpConnectedSocket_Internal.MANAGER, messageWithHeader);
                            break;
                        default:
                            break;
                    }
                }
            } catch (DeserializationException e) {
            }
            return z;
        }

        public boolean acceptWithResponder(Message message, MessageReceiver receiver) {
            boolean z = false;
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                MessageHeader header = messageWithHeader.getHeader();
                if (header.validateHeader(1)) {
                    switch (header.getType()) {
                        case ExploreByTouchHelper.HOST_ID /*-1*/:
                            z = InterfaceControlMessagesHelper.handleRun(getCore(), TcpConnectedSocket_Internal.MANAGER, messageWithHeader, receiver);
                            break;
                        default:
                            break;
                    }
                }
            } catch (DeserializationException e) {
            }
            return z;
        }
    }

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.mojo.TcpConnectedSocket.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }
    }

    TcpConnectedSocket_Internal() {
    }

    static {
        MANAGER = new C06301();
    }
}
