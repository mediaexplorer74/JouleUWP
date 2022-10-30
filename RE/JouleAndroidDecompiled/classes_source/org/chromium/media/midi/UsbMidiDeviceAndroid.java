package org.chromium.media.midi;

import android.annotation.TargetApi;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Handler;
import android.support.v4.media.TransportMediator;
import android.util.SparseArray;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.ui.base.PageTransition;

@JNINamespace("media::midi")
class UsbMidiDeviceAndroid {
    static final int MIDI_SUBCLASS = 3;
    static final int REQUEST_GET_DESCRIPTOR = 6;
    static final int STRING_DESCRIPTOR_TYPE = 3;
    private final UsbDeviceConnection mConnection;
    private final SparseArray<UsbEndpoint> mEndpointMap;
    private final Handler mHandler;
    private boolean mHasInputThread;
    private boolean mIsClosed;
    private long mNativePointer;
    private final Map<UsbEndpoint, UsbRequest> mRequestMap;
    private UsbDevice mUsbDevice;

    /* renamed from: org.chromium.media.midi.UsbMidiDeviceAndroid.1 */
    class C03911 extends Thread {
        final /* synthetic */ Map val$bufferForEndpoints;

        C03911(Map map) {
            this.val$bufferForEndpoints = map;
        }

        public void run() {
            while (true) {
                UsbRequest request = UsbMidiDeviceAndroid.this.mConnection.requestWait();
                if (request != null) {
                    UsbEndpoint endpoint = request.getEndpoint();
                    if (endpoint.getDirection() == TransportMediator.FLAG_KEY_MEDIA_NEXT) {
                        ByteBuffer buffer = (ByteBuffer) this.val$bufferForEndpoints.get(endpoint);
                        int length = UsbMidiDeviceAndroid.getInputDataLength(buffer);
                        if (length > 0) {
                            buffer.rewind();
                            byte[] bs = new byte[length];
                            buffer.get(bs, 0, length);
                            UsbMidiDeviceAndroid.this.postOnDataEvent(endpoint.getEndpointNumber(), bs);
                        }
                        buffer.rewind();
                        request.queue(buffer, buffer.capacity());
                    }
                } else {
                    return;
                }
            }
        }
    }

    /* renamed from: org.chromium.media.midi.UsbMidiDeviceAndroid.2 */
    class C03922 implements Runnable {
        final /* synthetic */ byte[] val$bs;
        final /* synthetic */ int val$endpointNumber;

        C03922(int i, byte[] bArr) {
            this.val$endpointNumber = i;
            this.val$bs = bArr;
        }

        public void run() {
            if (!UsbMidiDeviceAndroid.this.mIsClosed) {
                UsbMidiDeviceAndroid.nativeOnData(UsbMidiDeviceAndroid.this.mNativePointer, this.val$endpointNumber, this.val$bs);
            }
        }
    }

    private static native void nativeOnData(long j, int i, byte[] bArr);

    UsbMidiDeviceAndroid(UsbManager manager, UsbDevice device) {
        this.mConnection = manager.openDevice(device);
        this.mEndpointMap = new SparseArray();
        this.mRequestMap = new HashMap();
        this.mHandler = new Handler();
        this.mUsbDevice = device;
        this.mIsClosed = false;
        this.mHasInputThread = false;
        this.mNativePointer = 0;
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface iface = device.getInterface(i);
            if (iface.getInterfaceClass() == 1 && iface.getInterfaceSubclass() == STRING_DESCRIPTOR_TYPE) {
                this.mConnection.claimInterface(iface, true);
                for (int j = 0; j < iface.getEndpointCount(); j++) {
                    UsbEndpoint endpoint = iface.getEndpoint(j);
                    if (endpoint.getDirection() == 0) {
                        this.mEndpointMap.put(endpoint.getEndpointNumber(), endpoint);
                    }
                }
            }
        }
        startListen(device);
    }

    private void startListen(UsbDevice device) {
        Map<UsbEndpoint, ByteBuffer> bufferForEndpoints = new HashMap();
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface iface = device.getInterface(i);
            if (iface.getInterfaceClass() == 1 && iface.getInterfaceSubclass() == STRING_DESCRIPTOR_TYPE) {
                for (int j = 0; j < iface.getEndpointCount(); j++) {
                    UsbEndpoint endpoint = iface.getEndpoint(j);
                    if (endpoint.getDirection() == TransportMediator.FLAG_KEY_MEDIA_NEXT) {
                        ByteBuffer buffer = ByteBuffer.allocate(endpoint.getMaxPacketSize());
                        UsbRequest request = new UsbRequest();
                        request.initialize(this.mConnection, endpoint);
                        request.queue(buffer, buffer.remaining());
                        bufferForEndpoints.put(endpoint, buffer);
                    }
                }
            }
        }
        if (!bufferForEndpoints.isEmpty()) {
            this.mHasInputThread = true;
            new C03911(bufferForEndpoints).start();
        }
    }

    private void postOnDataEvent(int endpointNumber, byte[] bs) {
        this.mHandler.post(new C03922(endpointNumber, bs));
    }

    UsbDevice getUsbDevice() {
        return this.mUsbDevice;
    }

    boolean isClosed() {
        return this.mIsClosed;
    }

    @CalledByNative
    void registerSelf(long nativePointer) {
        this.mNativePointer = nativePointer;
    }

    @TargetApi(18)
    @CalledByNative
    void send(int endpointNumber, byte[] bs) {
        if (!this.mIsClosed) {
            UsbEndpoint endpoint = (UsbEndpoint) this.mEndpointMap.get(endpointNumber);
            if (endpoint == null) {
                return;
            }
            if (shouldUseBulkTransfer()) {
                this.mConnection.bulkTransfer(endpoint, bs, bs.length, 100);
                return;
            }
            UsbRequest request = (UsbRequest) this.mRequestMap.get(endpoint);
            if (request == null) {
                request = new UsbRequest();
                request.initialize(this.mConnection, endpoint);
                this.mRequestMap.put(endpoint, request);
            }
            request.queue(ByteBuffer.wrap(bs), bs.length);
        }
    }

    private boolean shouldUseBulkTransfer() {
        return this.mHasInputThread;
    }

    @CalledByNative
    byte[] getDescriptors() {
        if (this.mConnection == null) {
            return new byte[0];
        }
        return this.mConnection.getRawDescriptors();
    }

    @CalledByNative
    byte[] getStringDescriptor(int index) {
        if (this.mConnection == null) {
            return new byte[0];
        }
        byte[] buffer = new byte[PageTransition.CORE_MASK];
        int read = this.mConnection.controlTransfer(TransportMediator.FLAG_KEY_MEDIA_NEXT, REQUEST_GET_DESCRIPTOR, index | 768, 0, buffer, buffer.length, 0);
        if (read < 0) {
            return new byte[0];
        }
        return Arrays.copyOf(buffer, read);
    }

    @CalledByNative
    void close() {
        this.mEndpointMap.clear();
        for (UsbRequest request : this.mRequestMap.values()) {
            request.close();
        }
        this.mRequestMap.clear();
        this.mConnection.close();
        this.mNativePointer = 0;
        this.mIsClosed = true;
    }

    private static int getInputDataLength(ByteBuffer buffer) {
        int position = buffer.position();
        for (int i = 0; i < position; i += 4) {
            if (buffer.get(i) == null) {
                return i;
            }
        }
        return position;
    }
}
