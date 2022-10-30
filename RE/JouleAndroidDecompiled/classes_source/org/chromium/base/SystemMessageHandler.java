package org.chromium.base;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class SystemMessageHandler extends Handler {
    private static final int DELAYED_SCHEDULED_WORK = 2;
    private static final int SCHEDULED_WORK = 1;
    private static final String TAG = "SystemMessageHandler";
    private long mDelayedScheduledTimeTicks;
    private Method mMessageMethodSetAsynchronous;
    private long mMessagePumpDelegateNative;

    private native void nativeDoRunLoopOnce(long j, long j2);

    private SystemMessageHandler(long messagePumpDelegateNative) {
        this.mMessagePumpDelegateNative = 0;
        this.mDelayedScheduledTimeTicks = 0;
        this.mMessagePumpDelegateNative = messagePumpDelegateNative;
        try {
            Class[] clsArr = new Class[SCHEDULED_WORK];
            clsArr[0] = Boolean.TYPE;
            this.mMessageMethodSetAsynchronous = Class.forName("android.os.Message").getMethod("setAsynchronous", clsArr);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Failed to find android.os.Message class:" + e);
        } catch (NoSuchMethodException e2) {
            Log.e(TAG, "Failed to load Message.setAsynchronous method:" + e2);
        } catch (RuntimeException e3) {
            Log.e(TAG, "Exception while loading Message.setAsynchronous method: " + e3);
        }
    }

    public void handleMessage(Message msg) {
        if (msg.what == DELAYED_SCHEDULED_WORK) {
            this.mDelayedScheduledTimeTicks = 0;
        }
        nativeDoRunLoopOnce(this.mMessagePumpDelegateNative, this.mDelayedScheduledTimeTicks);
    }

    @CalledByNative
    private void scheduleWork() {
        sendMessage(obtainAsyncMessage(SCHEDULED_WORK));
    }

    @CalledByNative
    private void scheduleDelayedWork(long delayedTimeTicks, long millis) {
        if (this.mDelayedScheduledTimeTicks != 0) {
            removeMessages(DELAYED_SCHEDULED_WORK);
        }
        this.mDelayedScheduledTimeTicks = delayedTimeTicks;
        sendMessageDelayed(obtainAsyncMessage(DELAYED_SCHEDULED_WORK), millis);
    }

    @CalledByNative
    private void removeAllPendingMessages() {
        removeMessages(SCHEDULED_WORK);
        removeMessages(DELAYED_SCHEDULED_WORK);
    }

    private Message obtainAsyncMessage(int what) {
        Message msg = Message.obtain();
        msg.what = what;
        if (this.mMessageMethodSetAsynchronous != null) {
            try {
                Method method = this.mMessageMethodSetAsynchronous;
                Object[] objArr = new Object[SCHEDULED_WORK];
                objArr[0] = Boolean.valueOf(true);
                method.invoke(msg, objArr);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Illegal access to asynchronous message creation, disabling.");
                this.mMessageMethodSetAsynchronous = null;
            } catch (IllegalArgumentException e2) {
                Log.e(TAG, "Illegal argument for asynchronous message creation, disabling.");
                this.mMessageMethodSetAsynchronous = null;
            } catch (InvocationTargetException e3) {
                Log.e(TAG, "Invocation exception during asynchronous message creation, disabling.");
                this.mMessageMethodSetAsynchronous = null;
            } catch (RuntimeException e4) {
                Log.e(TAG, "Runtime exception during asynchronous message creation, disabling.");
                this.mMessageMethodSetAsynchronous = null;
            }
        }
        return msg;
    }

    @CalledByNative
    private static SystemMessageHandler create(long messagePumpDelegateNative) {
        return new SystemMessageHandler(messagePumpDelegateNative);
    }
}
