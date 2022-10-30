package org.chromium.mojo.system.impl;

import com.google.android.gms.common.ConnectionResult;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.mojo.system.AsyncWaiter;
import org.chromium.mojo.system.AsyncWaiter.Callback;
import org.chromium.mojo.system.AsyncWaiter.Cancellable;
import org.chromium.mojo.system.Core;
import org.chromium.mojo.system.Core.HandleSignals;
import org.chromium.mojo.system.Core.HandleSignalsState;
import org.chromium.mojo.system.Core.WaitManyResult;
import org.chromium.mojo.system.Core.WaitResult;
import org.chromium.mojo.system.DataPipe;
import org.chromium.mojo.system.DataPipe.ConsumerHandle;
import org.chromium.mojo.system.DataPipe.ProducerHandle;
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.MessagePipeHandle;
import org.chromium.mojo.system.MessagePipeHandle.CreateOptions;
import org.chromium.mojo.system.MessagePipeHandle.ReadFlags;
import org.chromium.mojo.system.MessagePipeHandle.ReadMessageResult;
import org.chromium.mojo.system.MessagePipeHandle.WriteFlags;
import org.chromium.mojo.system.MojoException;
import org.chromium.mojo.system.Pair;
import org.chromium.mojo.system.ResultAnd;
import org.chromium.mojo.system.RunLoop;
import org.chromium.mojo.system.SharedBufferHandle;
import org.chromium.mojo.system.SharedBufferHandle.DuplicateOptions;
import org.chromium.mojo.system.SharedBufferHandle.MapFlags;
import org.chromium.mojo.system.UntypedHandle;

@JNINamespace("mojo::android")
public class CoreImpl implements Core, AsyncWaiter {
    private static final int FLAG_SIZE = 4;
    private static final int HANDLE_SIZE = 4;
    static final int INVALID_HANDLE = 0;
    private static final int MOJO_READ_DATA_FLAG_DISCARD = 2;
    private final int mByteBufferOffset;
    private final ThreadLocal<BaseRunLoop> mCurrentRunLoop;

    private static class LazyHolder {
        private static final Core INSTANCE;

        static {
            INSTANCE = new CoreImpl();
        }

        private LazyHolder() {
        }
    }

    private class AsyncWaiterCancellableImpl implements Cancellable {
        private boolean mActive;
        private final long mDataPtr;
        private final long mId;

        private AsyncWaiterCancellableImpl(long id, long dataPtr) {
            this.mActive = true;
            this.mId = id;
            this.mDataPtr = dataPtr;
        }

        public void cancel() {
            if (this.mActive) {
                this.mActive = false;
                CoreImpl.this.nativeCancelAsyncWait(this.mId, this.mDataPtr);
            }
        }

        private boolean isActive() {
            return this.mActive;
        }

        private void deactivate() {
            this.mActive = false;
        }
    }

    private static final class IntegerPair extends Pair<Integer, Integer> {
        public IntegerPair(Integer first, Integer second) {
            super(first, second);
        }
    }

    private native AsyncWaiterCancellableImpl nativeAsyncWait(int i, int i2, long j, Callback callback);

    private native ResultAnd<ByteBuffer> nativeBeginReadData(int i, int i2, int i3);

    private native ResultAnd<ByteBuffer> nativeBeginWriteData(int i, int i2, int i3);

    private native void nativeCancelAsyncWait(long j, long j2);

    private native int nativeClose(int i);

    private native ResultAnd<IntegerPair> nativeCreateDataPipe(ByteBuffer byteBuffer);

    private native ResultAnd<IntegerPair> nativeCreateMessagePipe(ByteBuffer byteBuffer);

    private native ResultAnd<Integer> nativeCreateSharedBuffer(ByteBuffer byteBuffer, long j);

    private native ResultAnd<Integer> nativeDuplicate(int i, ByteBuffer byteBuffer);

    private native int nativeEndReadData(int i, int i2);

    private native int nativeEndWriteData(int i, int i2);

    private native int nativeGetNativeBufferOffset(ByteBuffer byteBuffer, int i);

    private native long nativeGetTimeTicksNow();

    private native ResultAnd<ByteBuffer> nativeMap(int i, long j, long j2, int i2);

    private native ResultAnd<Integer> nativeReadData(int i, ByteBuffer byteBuffer, int i2, int i3);

    private native ResultAnd<ReadMessageResult> nativeReadMessage(int i, ByteBuffer byteBuffer, ByteBuffer byteBuffer2, int i2);

    private native int nativeUnmap(ByteBuffer byteBuffer);

    private native int nativeWait(ByteBuffer byteBuffer, int i, int i2, long j);

    private native int nativeWaitMany(ByteBuffer byteBuffer, long j);

    private native ResultAnd<Integer> nativeWriteData(int i, ByteBuffer byteBuffer, int i2, int i3);

    private native int nativeWriteMessage(int i, ByteBuffer byteBuffer, int i2, ByteBuffer byteBuffer2, int i3);

    public static Core getInstance() {
        return LazyHolder.INSTANCE;
    }

    private CoreImpl() {
        this.mCurrentRunLoop = new ThreadLocal();
        this.mByteBufferOffset = nativeGetNativeBufferOffset(ByteBuffer.allocateDirect(8), 8);
    }

    public long getTimeTicksNow() {
        return nativeGetTimeTicksNow();
    }

    public WaitManyResult waitMany(List<Pair<Handle, HandleSignals>> handles, long deadline) {
        ByteBuffer buffer = allocateDirectBuffer((handles.size() * 16) + HANDLE_SIZE);
        int index = INVALID_HANDLE;
        for (Pair<Handle, HandleSignals> handle : handles) {
            buffer.putInt(index * HANDLE_SIZE, getMojoHandle((Handle) handle.first));
            buffer.putInt((index * HANDLE_SIZE) + (handles.size() * HANDLE_SIZE), ((HandleSignals) handle.second).getFlags());
            index++;
        }
        int code = nativeWaitMany(buffer, deadline);
        WaitManyResult result = new WaitManyResult();
        result.setMojoResult(filterMojoResultForWait(code));
        result.setHandleIndex(buffer.getInt(handles.size() * 16));
        if (!(result.getMojoResult() == 3 || result.getMojoResult() == 8)) {
            HandleSignalsState[] states = new HandleSignalsState[handles.size()];
            for (int i = INVALID_HANDLE; i < handles.size(); i++) {
                states[i] = new HandleSignalsState(new HandleSignals(buffer.getInt((handles.size() + i) * 8)), new HandleSignals(buffer.getInt(((handles.size() + i) * 8) + HANDLE_SIZE)));
            }
            result.setSignalStates(Arrays.asList(states));
        }
        return result;
    }

    public WaitResult wait(Handle handle, HandleSignals signals, long deadline) {
        ByteBuffer buffer = allocateDirectBuffer(8);
        WaitResult result = new WaitResult();
        result.setMojoResult(filterMojoResultForWait(nativeWait(buffer, getMojoHandle(handle), signals.getFlags(), deadline)));
        result.setHandleSignalsState(new HandleSignalsState(new HandleSignals(buffer.getInt(INVALID_HANDLE)), new HandleSignals(buffer.getInt(HANDLE_SIZE))));
        return result;
    }

    public Pair<MessagePipeHandle, MessagePipeHandle> createMessagePipe(CreateOptions options) {
        ByteBuffer optionsBuffer = null;
        if (options != null) {
            optionsBuffer = allocateDirectBuffer(8);
            optionsBuffer.putInt(INVALID_HANDLE, 8);
            optionsBuffer.putInt(HANDLE_SIZE, options.getFlags().getFlags());
        }
        ResultAnd<IntegerPair> result = nativeCreateMessagePipe(optionsBuffer);
        if (result.getMojoResult() == 0) {
            return Pair.create(new MessagePipeHandleImpl(this, ((Integer) ((IntegerPair) result.getValue()).first).intValue()), new MessagePipeHandleImpl(this, ((Integer) ((IntegerPair) result.getValue()).second).intValue()));
        }
        throw new MojoException(result.getMojoResult());
    }

    public Pair<ProducerHandle, ConsumerHandle> createDataPipe(DataPipe.CreateOptions options) {
        ByteBuffer optionsBuffer = null;
        if (options != null) {
            optionsBuffer = allocateDirectBuffer(16);
            optionsBuffer.putInt(INVALID_HANDLE, 16);
            optionsBuffer.putInt(HANDLE_SIZE, options.getFlags().getFlags());
            optionsBuffer.putInt(8, options.getElementNumBytes());
            optionsBuffer.putInt(12, options.getCapacityNumBytes());
        }
        ResultAnd<IntegerPair> result = nativeCreateDataPipe(optionsBuffer);
        if (result.getMojoResult() == 0) {
            return Pair.create(new DataPipeProducerHandleImpl(this, ((Integer) ((IntegerPair) result.getValue()).first).intValue()), new DataPipeConsumerHandleImpl(this, ((Integer) ((IntegerPair) result.getValue()).second).intValue()));
        }
        throw new MojoException(result.getMojoResult());
    }

    public SharedBufferHandle createSharedBuffer(SharedBufferHandle.CreateOptions options, long numBytes) {
        ByteBuffer optionsBuffer = null;
        if (options != null) {
            optionsBuffer = allocateDirectBuffer(8);
            optionsBuffer.putInt(INVALID_HANDLE, 8);
            optionsBuffer.putInt(HANDLE_SIZE, options.getFlags().getFlags());
        }
        ResultAnd<Integer> result = nativeCreateSharedBuffer(optionsBuffer, numBytes);
        if (result.getMojoResult() == 0) {
            return new SharedBufferHandleImpl(this, ((Integer) result.getValue()).intValue());
        }
        throw new MojoException(result.getMojoResult());
    }

    public UntypedHandle acquireNativeHandle(int handle) {
        return new UntypedHandleImpl(this, handle);
    }

    public AsyncWaiter getDefaultAsyncWaiter() {
        return this;
    }

    public RunLoop createDefaultRunLoop() {
        if (this.mCurrentRunLoop.get() != null) {
            throw new MojoException(9);
        }
        BaseRunLoop runLoop = new BaseRunLoop(this);
        this.mCurrentRunLoop.set(runLoop);
        return runLoop;
    }

    public RunLoop getCurrentRunLoop() {
        return (RunLoop) this.mCurrentRunLoop.get();
    }

    void clearCurrentRunLoop() {
        this.mCurrentRunLoop.remove();
    }

    public Cancellable asyncWait(Handle handle, HandleSignals signals, long deadline, Callback callback) {
        return nativeAsyncWait(getMojoHandle(handle), signals.getFlags(), deadline, callback);
    }

    int closeWithResult(int mojoHandle) {
        return nativeClose(mojoHandle);
    }

    void close(int mojoHandle) {
        int mojoResult = nativeClose(mojoHandle);
        if (mojoResult != 0) {
            throw new MojoException(mojoResult);
        }
    }

    void writeMessage(MessagePipeHandleImpl pipeHandle, ByteBuffer bytes, List<? extends Handle> handles, WriteFlags flags) {
        int i = INVALID_HANDLE;
        ByteBuffer handlesBuffer = null;
        if (!(handles == null || handles.isEmpty())) {
            handlesBuffer = allocateDirectBuffer(handles.size() * HANDLE_SIZE);
            for (Handle handle : handles) {
                handlesBuffer.putInt(getMojoHandle(handle));
            }
            handlesBuffer.position(INVALID_HANDLE);
        }
        int mojoHandle = pipeHandle.getMojoHandle();
        if (bytes != null) {
            i = bytes.limit();
        }
        int mojoResult = nativeWriteMessage(mojoHandle, bytes, i, handlesBuffer, flags.getFlags());
        if (mojoResult != 0) {
            throw new MojoException(mojoResult);
        } else if (handles != null) {
            for (Handle handle2 : handles) {
                if (handle2.isValid()) {
                    ((HandleBase) handle2).invalidateHandle();
                }
            }
        }
    }

    ResultAnd<ReadMessageResult> readMessage(MessagePipeHandleImpl handle, ByteBuffer bytes, int maxNumberOfHandles, ReadFlags flags) {
        ByteBuffer handlesBuffer = null;
        if (maxNumberOfHandles > 0) {
            handlesBuffer = allocateDirectBuffer(maxNumberOfHandles * HANDLE_SIZE);
        }
        ResultAnd<ReadMessageResult> result = nativeReadMessage(handle.getMojoHandle(), bytes, handlesBuffer, flags.getFlags());
        if (result.getMojoResult() == 0 || result.getMojoResult() == 8 || result.getMojoResult() == 17) {
            if (result.getMojoResult() == 0) {
                ReadMessageResult readResult = (ReadMessageResult) result.getValue();
                if (bytes != null) {
                    bytes.position(INVALID_HANDLE);
                    bytes.limit(readResult.getMessageSize());
                }
                List<UntypedHandle> handles = new ArrayList(readResult.getHandlesCount());
                for (int i = INVALID_HANDLE; i < readResult.getHandlesCount(); i++) {
                    handles.add(new UntypedHandleImpl(this, handlesBuffer.getInt(i * HANDLE_SIZE)));
                }
                readResult.setHandles(handles);
            }
            return result;
        }
        throw new MojoException(result.getMojoResult());
    }

    int discardData(DataPipeConsumerHandleImpl handle, int numBytes, DataPipe.ReadFlags flags) {
        ResultAnd<Integer> result = nativeReadData(handle.getMojoHandle(), null, numBytes, flags.getFlags() | MOJO_READ_DATA_FLAG_DISCARD);
        if (result.getMojoResult() == 0) {
            return ((Integer) result.getValue()).intValue();
        }
        throw new MojoException(result.getMojoResult());
    }

    ResultAnd<Integer> readData(DataPipeConsumerHandleImpl handle, ByteBuffer elements, DataPipe.ReadFlags flags) {
        ResultAnd<Integer> result = nativeReadData(handle.getMojoHandle(), elements, elements == null ? INVALID_HANDLE : elements.capacity(), flags.getFlags());
        if (result.getMojoResult() == 0 || result.getMojoResult() == 17) {
            if (result.getMojoResult() == 0 && elements != null) {
                elements.limit(((Integer) result.getValue()).intValue());
            }
            return result;
        }
        throw new MojoException(result.getMojoResult());
    }

    ByteBuffer beginReadData(DataPipeConsumerHandleImpl handle, int numBytes, DataPipe.ReadFlags flags) {
        ResultAnd<ByteBuffer> result = nativeBeginReadData(handle.getMojoHandle(), numBytes, flags.getFlags());
        if (result.getMojoResult() == 0) {
            return ((ByteBuffer) result.getValue()).asReadOnlyBuffer();
        }
        throw new MojoException(result.getMojoResult());
    }

    void endReadData(DataPipeConsumerHandleImpl handle, int numBytesRead) {
        int result = nativeEndReadData(handle.getMojoHandle(), numBytesRead);
        if (result != 0) {
            throw new MojoException(result);
        }
    }

    ResultAnd<Integer> writeData(DataPipeProducerHandleImpl handle, ByteBuffer elements, DataPipe.WriteFlags flags) {
        return nativeWriteData(handle.getMojoHandle(), elements, elements.limit(), flags.getFlags());
    }

    ByteBuffer beginWriteData(DataPipeProducerHandleImpl handle, int numBytes, DataPipe.WriteFlags flags) {
        ResultAnd<ByteBuffer> result = nativeBeginWriteData(handle.getMojoHandle(), numBytes, flags.getFlags());
        if (result.getMojoResult() == 0) {
            return (ByteBuffer) result.getValue();
        }
        throw new MojoException(result.getMojoResult());
    }

    void endWriteData(DataPipeProducerHandleImpl handle, int numBytesWritten) {
        int result = nativeEndWriteData(handle.getMojoHandle(), numBytesWritten);
        if (result != 0) {
            throw new MojoException(result);
        }
    }

    SharedBufferHandle duplicate(SharedBufferHandleImpl handle, DuplicateOptions options) {
        ByteBuffer optionsBuffer = null;
        if (options != null) {
            optionsBuffer = allocateDirectBuffer(8);
            optionsBuffer.putInt(INVALID_HANDLE, 8);
            optionsBuffer.putInt(HANDLE_SIZE, options.getFlags().getFlags());
        }
        ResultAnd<Integer> result = nativeDuplicate(handle.getMojoHandle(), optionsBuffer);
        if (result.getMojoResult() == 0) {
            return new SharedBufferHandleImpl(this, ((Integer) result.getValue()).intValue());
        }
        throw new MojoException(result.getMojoResult());
    }

    ByteBuffer map(SharedBufferHandleImpl handle, long offset, long numBytes, MapFlags flags) {
        ResultAnd<ByteBuffer> result = nativeMap(handle.getMojoHandle(), offset, numBytes, flags.getFlags());
        if (result.getMojoResult() == 0) {
            return (ByteBuffer) result.getValue();
        }
        throw new MojoException(result.getMojoResult());
    }

    void unmap(ByteBuffer buffer) {
        int result = nativeUnmap(buffer);
        if (result != 0) {
            throw new MojoException(result);
        }
    }

    private int getMojoHandle(Handle handle) {
        if (handle.isValid()) {
            return ((HandleBase) handle).getMojoHandle();
        }
        return INVALID_HANDLE;
    }

    private static boolean isUnrecoverableError(int code) {
        switch (code) {
            case INVALID_HANDLE /*0*/:
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
            case HANDLE_SIZE /*4*/:
            case ConnectionResult.SERVICE_INVALID /*9*/:
                return false;
            default:
                return true;
        }
    }

    private static int filterMojoResultForWait(int code) {
        if (!isUnrecoverableError(code)) {
            return code;
        }
        throw new MojoException(code);
    }

    private ByteBuffer allocateDirectBuffer(int capacity) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(this.mByteBufferOffset + capacity);
        if (this.mByteBufferOffset != 0) {
            buffer.position(this.mByteBufferOffset);
            buffer = buffer.slice();
        }
        return buffer.order(ByteOrder.nativeOrder());
    }

    @CalledByNative
    private AsyncWaiterCancellableImpl newAsyncWaiterCancellableImpl(long id, long dataPtr) {
        return new AsyncWaiterCancellableImpl(id, dataPtr, null);
    }

    @CalledByNative
    private void onAsyncWaitResult(int mojoResult, Callback callback, AsyncWaiterCancellableImpl cancellable) {
        if (cancellable.isActive()) {
            cancellable.deactivate();
            if (isUnrecoverableError(mojoResult)) {
                callback.onError(new MojoException(mojoResult));
            } else {
                callback.onResult(mojoResult);
            }
        }
    }

    @CalledByNative
    private static ResultAnd<ByteBuffer> newResultAndBuffer(int mojoResult, ByteBuffer buffer) {
        return new ResultAnd(mojoResult, buffer);
    }

    @CalledByNative
    private static ResultAnd<ReadMessageResult> newReadMessageResult(int mojoResult, int messageSize, int handlesCount) {
        ReadMessageResult result = new ReadMessageResult();
        result.setMessageSize(messageSize);
        result.setHandlesCount(handlesCount);
        return new ResultAnd(mojoResult, result);
    }

    @CalledByNative
    private static ResultAnd<Integer> newResultAndInteger(int mojoResult, int numBytesRead) {
        return new ResultAnd(mojoResult, Integer.valueOf(numBytesRead));
    }

    @CalledByNative
    private static ResultAnd<IntegerPair> newNativeCreationResult(int mojoResult, int mojoHandle1, int mojoHandle2) {
        return new ResultAnd(mojoResult, new IntegerPair(Integer.valueOf(mojoHandle1), Integer.valueOf(mojoHandle2)));
    }
}
