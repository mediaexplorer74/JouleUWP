package org.chromium.content.browser;

import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

@JNINamespace("content")
public class MotionEventSynthesizer {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final int ACTION_CANCEL = 2;
    private static final int ACTION_END = 3;
    private static final int ACTION_MOVE = 1;
    private static final int ACTION_SCROLL = 4;
    private static final int ACTION_START = 0;
    private static final int MAX_NUM_POINTERS = 16;
    private final ContentViewCore mContentViewCore;
    private long mDownTimeInMs;
    private final PointerCoords[] mPointerCoords;
    private final PointerProperties[] mPointerProperties;

    static {
        $assertionsDisabled = !MotionEventSynthesizer.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    MotionEventSynthesizer(ContentViewCore contentViewCore) {
        this.mContentViewCore = contentViewCore;
        this.mPointerProperties = new PointerProperties[MAX_NUM_POINTERS];
        this.mPointerCoords = new PointerCoords[MAX_NUM_POINTERS];
    }

    @CalledByNative
    void setPointer(int index, int x, int y, int id) {
        if ($assertionsDisabled || (index >= 0 && index < MAX_NUM_POINTERS)) {
            float scaleFactor = this.mContentViewCore.getRenderCoordinates().getDeviceScaleFactor();
            PointerCoords coords = new PointerCoords();
            coords.x = ((float) x) * scaleFactor;
            coords.y = ((float) y) * scaleFactor;
            coords.pressure = 1.0f;
            this.mPointerCoords[index] = coords;
            PointerProperties properties = new PointerProperties();
            properties.id = id;
            this.mPointerProperties[index] = properties;
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    void setScrollDeltas(int x, int y, int dx, int dy) {
        setPointer(ACTION_START, x, y, ACTION_START);
        float scaleFactor = this.mContentViewCore.getRenderCoordinates().getDeviceScaleFactor();
        this.mPointerCoords[ACTION_START].setAxisValue(10, ((float) dx) * scaleFactor);
        this.mPointerCoords[ACTION_START].setAxisValue(9, ((float) dy) * scaleFactor);
    }

    @CalledByNative
    void inject(int action, int pointerCount, long timeInMs) {
        MotionEvent event;
        switch (action) {
            case ACTION_START /*0*/:
                this.mDownTimeInMs = timeInMs;
                event = MotionEvent.obtain(this.mDownTimeInMs, timeInMs, ACTION_START, ACTION_MOVE, this.mPointerProperties, this.mPointerCoords, ACTION_START, ACTION_START, 1.0f, 1.0f, ACTION_START, ACTION_START, ACTION_START, ACTION_START);
                this.mContentViewCore.onTouchEvent(event);
                event.recycle();
                if (pointerCount > ACTION_MOVE) {
                    event = MotionEvent.obtain(this.mDownTimeInMs, timeInMs, 5, pointerCount, this.mPointerProperties, this.mPointerCoords, ACTION_START, ACTION_START, 1.0f, 1.0f, ACTION_START, ACTION_START, ACTION_START, ACTION_START);
                    this.mContentViewCore.onTouchEvent(event);
                    event.recycle();
                }
            case ACTION_MOVE /*1*/:
                event = MotionEvent.obtain(this.mDownTimeInMs, timeInMs, ACTION_CANCEL, pointerCount, this.mPointerProperties, this.mPointerCoords, ACTION_START, ACTION_START, 1.0f, 1.0f, ACTION_START, ACTION_START, ACTION_START, ACTION_START);
                this.mContentViewCore.onTouchEvent(event);
                event.recycle();
            case ACTION_CANCEL /*2*/:
                event = MotionEvent.obtain(this.mDownTimeInMs, timeInMs, ACTION_END, ACTION_MOVE, this.mPointerProperties, this.mPointerCoords, ACTION_START, ACTION_START, 1.0f, 1.0f, ACTION_START, ACTION_START, ACTION_START, ACTION_START);
                this.mContentViewCore.onTouchEvent(event);
                event.recycle();
            case ACTION_END /*3*/:
                if (pointerCount > ACTION_MOVE) {
                    event = MotionEvent.obtain(this.mDownTimeInMs, timeInMs, 6, pointerCount, this.mPointerProperties, this.mPointerCoords, ACTION_START, ACTION_START, 1.0f, 1.0f, ACTION_START, ACTION_START, ACTION_START, ACTION_START);
                    this.mContentViewCore.onTouchEvent(event);
                    event.recycle();
                }
                event = MotionEvent.obtain(this.mDownTimeInMs, timeInMs, ACTION_MOVE, ACTION_MOVE, this.mPointerProperties, this.mPointerCoords, ACTION_START, ACTION_START, 1.0f, 1.0f, ACTION_START, ACTION_START, ACTION_START, ACTION_START);
                this.mContentViewCore.onTouchEvent(event);
                event.recycle();
            case ACTION_SCROLL /*4*/:
                if ($assertionsDisabled || pointerCount == ACTION_MOVE) {
                    event = MotionEvent.obtain(this.mDownTimeInMs, timeInMs, 8, pointerCount, this.mPointerProperties, this.mPointerCoords, ACTION_START, ACTION_START, 1.0f, 1.0f, ACTION_START, ACTION_START, ACTION_CANCEL, ACTION_START);
                    this.mContentViewCore.onGenericMotionEvent(event);
                    event.recycle();
                    return;
                }
                throw new AssertionError();
            default:
                if (!$assertionsDisabled) {
                    throw new AssertionError("Unreached");
                }
        }
    }
}
