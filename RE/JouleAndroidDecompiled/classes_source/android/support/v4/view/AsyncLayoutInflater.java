package android.support.v4.view;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.util.Pools.SynchronizedPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.concurrent.ArrayBlockingQueue;

public final class AsyncLayoutInflater {
    private static final String TAG = "AsyncLayoutInflater";
    private Handler mHandler;
    private Callback mHandlerCallback;
    private InflateThread mInflateThread;
    private LayoutInflater mInflater;

    /* renamed from: android.support.v4.view.AsyncLayoutInflater.1 */
    class C00811 implements Callback {
        C00811() {
        }

        public boolean handleMessage(Message msg) {
            InflateRequest request = msg.obj;
            if (request.view == null) {
                request.view = AsyncLayoutInflater.this.mInflater.inflate(request.resid, request.parent, false);
            }
            request.callback.onInflateFinished(request.view, request.resid, request.parent);
            AsyncLayoutInflater.this.mInflateThread.releaseRequest(request);
            return true;
        }
    }

    private static class BasicInflater extends LayoutInflater {
        private static final String[] sClassPrefixList;

        static {
            sClassPrefixList = new String[]{"android.widget.", "android.webkit.", "android.app."};
        }

        public BasicInflater(Context context) {
            super(context);
        }

        public LayoutInflater cloneInContext(Context newContext) {
            return new BasicInflater(newContext);
        }

        protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
            String[] strArr = sClassPrefixList;
            int length = strArr.length;
            int i = 0;
            while (i < length) {
                try {
                    View view = createView(name, strArr[i], attrs);
                    if (view != null) {
                        return view;
                    }
                    i++;
                } catch (ClassNotFoundException e) {
                }
            }
            return super.onCreateView(name, attrs);
        }
    }

    private static class InflateRequest {
        OnInflateFinishedListener callback;
        AsyncLayoutInflater inflater;
        ViewGroup parent;
        int resid;
        View view;

        private InflateRequest() {
        }
    }

    private static class InflateThread extends Thread {
        private static final InflateThread sInstance;
        private ArrayBlockingQueue<InflateRequest> mQueue;
        private SynchronizedPool<InflateRequest> mRequestPool;

        private InflateThread() {
            this.mQueue = new ArrayBlockingQueue(10);
            this.mRequestPool = new SynchronizedPool(10);
        }

        static {
            sInstance = new InflateThread();
            sInstance.start();
        }

        public static InflateThread getInstance() {
            return sInstance;
        }

        public void run() {
            while (true) {
                try {
                    InflateRequest request = (InflateRequest) this.mQueue.take();
                    try {
                        request.view = request.inflater.mInflater.inflate(request.resid, request.parent, false);
                    } catch (RuntimeException ex) {
                        Log.w(AsyncLayoutInflater.TAG, "Failed to inflate resource in the background! Retrying on the UI thread", ex);
                    }
                    Message.obtain(request.inflater.mHandler, 0, request).sendToTarget();
                } catch (InterruptedException ex2) {
                    Log.w(AsyncLayoutInflater.TAG, ex2);
                }
            }
        }

        public InflateRequest obtainRequest() {
            InflateRequest obj = (InflateRequest) this.mRequestPool.acquire();
            if (obj == null) {
                return new InflateRequest();
            }
            return obj;
        }

        public void releaseRequest(InflateRequest obj) {
            obj.callback = null;
            obj.inflater = null;
            obj.parent = null;
            obj.resid = 0;
            obj.view = null;
            this.mRequestPool.release(obj);
        }

        public void enqueue(InflateRequest request) {
            try {
                this.mQueue.put(request);
            } catch (InterruptedException e) {
                throw new RuntimeException("Failed to enqueue async inflate request", e);
            }
        }
    }

    public interface OnInflateFinishedListener {
        void onInflateFinished(View view, int i, ViewGroup viewGroup);
    }

    public AsyncLayoutInflater(@NonNull Context context) {
        this.mHandlerCallback = new C00811();
        this.mInflater = new BasicInflater(context);
        this.mHandler = new Handler(this.mHandlerCallback);
        this.mInflateThread = InflateThread.getInstance();
    }

    @UiThread
    public void inflate(@LayoutRes int resid, @Nullable ViewGroup parent, @NonNull OnInflateFinishedListener callback) {
        if (callback == null) {
            throw new NullPointerException("callback argument may not be null!");
        }
        InflateRequest request = this.mInflateThread.obtainRequest();
        request.inflater = this;
        request.resid = resid;
        request.parent = parent;
        request.callback = callback;
        this.mInflateThread.enqueue(request);
    }
}
