package org.chromium.content.browser.input;

import android.os.SystemClock;
import android.view.InputDevice;
import android.view.InputDevice.MotionRange;
import android.view.KeyEvent;
import android.view.MotionEvent;
import java.util.Arrays;
import java.util.List;
import org.chromium.base.VisibleForTesting;

class GamepadDevice {
    static final /* synthetic */ boolean $assertionsDisabled;
    @VisibleForTesting
    static final int MAX_RAW_AXIS_VALUES = 256;
    @VisibleForTesting
    static final int MAX_RAW_BUTTON_VALUES = 256;
    private int[] mAxes;
    private final float[] mAxisValues;
    private final float[] mButtonsValues;
    private int mDeviceId;
    private int mDeviceIndex;
    private String mDeviceName;
    private boolean mIsStandardGamepad;
    private final float[] mRawAxes;
    private final float[] mRawButtons;
    private long mTimestamp;

    static {
        $assertionsDisabled = !GamepadDevice.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    GamepadDevice(int index, InputDevice inputDevice) {
        this.mAxisValues = new float[4];
        this.mButtonsValues = new float[17];
        this.mRawButtons = new float[MAX_RAW_BUTTON_VALUES];
        this.mRawAxes = new float[MAX_RAW_BUTTON_VALUES];
        this.mDeviceIndex = index;
        this.mDeviceId = inputDevice.getId();
        this.mDeviceName = inputDevice.getName();
        this.mTimestamp = SystemClock.uptimeMillis();
        List<MotionRange> ranges = inputDevice.getMotionRanges();
        this.mAxes = new int[ranges.size()];
        int i = 0;
        for (MotionRange range : ranges) {
            if ((range.getSource() & 16) != 0) {
                int axis = range.getAxis();
                if ($assertionsDisabled || axis < MAX_RAW_BUTTON_VALUES) {
                    int i2 = i + 1;
                    this.mAxes[i] = axis;
                    i = i2;
                } else {
                    throw new AssertionError();
                }
            }
        }
    }

    public void updateButtonsAndAxesMapping() {
        this.mIsStandardGamepad = GamepadMappings.mapToStandardGamepad(this.mAxisValues, this.mButtonsValues, this.mRawAxes, this.mRawButtons, this.mDeviceName);
    }

    public int getId() {
        return this.mDeviceId;
    }

    public boolean isStandardGamepad() {
        return this.mIsStandardGamepad;
    }

    public String getName() {
        return this.mDeviceName;
    }

    public int getIndex() {
        return this.mDeviceIndex;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    public float[] getAxes() {
        return this.mAxisValues;
    }

    public float[] getButtons() {
        return this.mButtonsValues;
    }

    public void clearData() {
        Arrays.fill(this.mAxisValues, 0.0f);
        Arrays.fill(this.mRawAxes, 0.0f);
        Arrays.fill(this.mButtonsValues, 0.0f);
        Arrays.fill(this.mRawButtons, 0.0f);
    }

    public boolean handleKeyEvent(KeyEvent event) {
        if (!GamepadList.isGamepadEvent(event)) {
            return $assertionsDisabled;
        }
        int keyCode = event.getKeyCode();
        if ($assertionsDisabled || keyCode < MAX_RAW_BUTTON_VALUES) {
            if (event.getAction() == 0) {
                this.mRawButtons[keyCode] = 1.0f;
            } else if (event.getAction() == 1) {
                this.mRawButtons[keyCode] = 0.0f;
            }
            this.mTimestamp = event.getEventTime();
            return true;
        }
        throw new AssertionError();
    }

    public boolean handleMotionEvent(MotionEvent event) {
        if (!GamepadList.isGamepadEvent(event)) {
            return $assertionsDisabled;
        }
        for (int axis : this.mAxes) {
            this.mRawAxes[axis] = event.getAxisValue(axis);
        }
        this.mTimestamp = event.getEventTime();
        return true;
    }
}
