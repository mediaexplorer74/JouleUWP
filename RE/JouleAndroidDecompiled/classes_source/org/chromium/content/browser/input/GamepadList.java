package org.chromium.content.browser.input;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;
import android.os.Build.VERSION;
import android.support.v4.view.InputDeviceCompat;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.google.android.gms.common.ConnectionResult;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.ThreadUtils;
import org.chromium.net.ConnectionSubtype;

@JNINamespace("content")
public class GamepadList {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final int MAX_GAMEPADS = 4;
    private int mAttachedToWindowCounter;
    private final GamepadDevice[] mGamepadDevices;
    private InputDeviceListener mInputDeviceListener;
    private InputManager mInputManager;
    private boolean mIsGamepadAccessed;
    private final Object mLock;

    /* renamed from: org.chromium.content.browser.input.GamepadList.1 */
    class C03601 implements InputDeviceListener {
        C03601() {
        }

        public void onInputDeviceChanged(int deviceId) {
            GamepadList.this.onInputDeviceChangedImpl(deviceId);
        }

        public void onInputDeviceRemoved(int deviceId) {
            GamepadList.this.onInputDeviceRemovedImpl(deviceId);
        }

        public void onInputDeviceAdded(int deviceId) {
            GamepadList.this.onInputDeviceAddedImpl(deviceId);
        }
    }

    private static class LazyHolder {
        private static final GamepadList INSTANCE;

        private LazyHolder() {
        }

        static {
            INSTANCE = new GamepadList();
        }
    }

    private native void nativeSetGamepadData(long j, int i, boolean z, boolean z2, String str, long j2, float[] fArr, float[] fArr2);

    static {
        $assertionsDisabled = !GamepadList.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    @TargetApi(16)
    private GamepadList() {
        this.mLock = new Object();
        this.mGamepadDevices = new GamepadDevice[MAX_GAMEPADS];
        this.mInputDeviceListener = new C03601();
    }

    @TargetApi(16)
    private void initializeDevices() {
        int[] deviceIds = this.mInputManager.getInputDeviceIds();
        for (int device : deviceIds) {
            InputDevice inputDevice = InputDevice.getDevice(device);
            if (isGamepadDevice(inputDevice)) {
                registerGamepad(inputDevice);
            }
        }
    }

    public static void onAttachedToWindow(Context context) {
        if (!$assertionsDisabled && !ThreadUtils.runningOnUiThread()) {
            throw new AssertionError();
        } else if (isGamepadSupported()) {
            getInstance().attachedToWindow(context);
        }
    }

    @TargetApi(16)
    private void attachedToWindow(Context context) {
        int i = this.mAttachedToWindowCounter;
        this.mAttachedToWindowCounter = i + 1;
        if (i == 0) {
            this.mInputManager = (InputManager) context.getSystemService("input");
            synchronized (this.mLock) {
                initializeDevices();
            }
            this.mInputManager.registerInputDeviceListener(this.mInputDeviceListener, null);
        }
    }

    @SuppressLint({"MissingSuperCall"})
    public static void onDetachedFromWindow() {
        if (!$assertionsDisabled && !ThreadUtils.runningOnUiThread()) {
            throw new AssertionError();
        } else if (isGamepadSupported()) {
            getInstance().detachedFromWindow();
        }
    }

    @TargetApi(16)
    private void detachedFromWindow() {
        int i = this.mAttachedToWindowCounter - 1;
        this.mAttachedToWindowCounter = i;
        if (i == 0) {
            synchronized (this.mLock) {
                for (int i2 = 0; i2 < MAX_GAMEPADS; i2++) {
                    this.mGamepadDevices[i2] = null;
                }
            }
            this.mInputManager.unregisterInputDeviceListener(this.mInputDeviceListener);
            this.mInputManager = null;
        }
    }

    private void onInputDeviceChangedImpl(int deviceId) {
    }

    private void onInputDeviceRemovedImpl(int deviceId) {
        synchronized (this.mLock) {
            unregisterGamepad(deviceId);
        }
    }

    private void onInputDeviceAddedImpl(int deviceId) {
        InputDevice inputDevice = InputDevice.getDevice(deviceId);
        if (isGamepadDevice(inputDevice)) {
            synchronized (this.mLock) {
                registerGamepad(inputDevice);
            }
        }
    }

    private static GamepadList getInstance() {
        if ($assertionsDisabled || isGamepadSupported()) {
            return LazyHolder.INSTANCE;
        }
        throw new AssertionError();
    }

    private int getDeviceCount() {
        int count = 0;
        for (int i = 0; i < MAX_GAMEPADS; i++) {
            if (getDevice(i) != null) {
                count++;
            }
        }
        return count;
    }

    private boolean isDeviceConnected(int index) {
        if (index >= MAX_GAMEPADS || getDevice(index) == null) {
            return $assertionsDisabled;
        }
        return true;
    }

    private GamepadDevice getDeviceById(int deviceId) {
        for (int i = 0; i < MAX_GAMEPADS; i++) {
            GamepadDevice gamepad = this.mGamepadDevices[i];
            if (gamepad != null && gamepad.getId() == deviceId) {
                return gamepad;
            }
        }
        return null;
    }

    private GamepadDevice getDevice(int index) {
        if ($assertionsDisabled || (index >= 0 && index < MAX_GAMEPADS)) {
            return this.mGamepadDevices[index];
        }
        throw new AssertionError();
    }

    public static boolean dispatchKeyEvent(KeyEvent event) {
        if (isGamepadSupported() && isGamepadEvent(event)) {
            return getInstance().handleKeyEvent(event);
        }
        return $assertionsDisabled;
    }

    private boolean handleKeyEvent(KeyEvent event) {
        boolean z = $assertionsDisabled;
        synchronized (this.mLock) {
            if (this.mIsGamepadAccessed) {
                GamepadDevice gamepad = getGamepadForEvent(event);
                if (gamepad == null) {
                } else {
                    z = gamepad.handleKeyEvent(event);
                }
            }
        }
        return z;
    }

    public static boolean onGenericMotionEvent(MotionEvent event) {
        if (isGamepadSupported() && isGamepadEvent(event)) {
            return getInstance().handleMotionEvent(event);
        }
        return $assertionsDisabled;
    }

    private boolean handleMotionEvent(MotionEvent event) {
        boolean z = $assertionsDisabled;
        synchronized (this.mLock) {
            if (this.mIsGamepadAccessed) {
                GamepadDevice gamepad = getGamepadForEvent(event);
                if (gamepad == null) {
                } else {
                    z = gamepad.handleMotionEvent(event);
                }
            }
        }
        return z;
    }

    private int getNextAvailableIndex() {
        for (int i = 0; i < MAX_GAMEPADS; i++) {
            if (getDevice(i) == null) {
                return i;
            }
        }
        return -1;
    }

    private boolean registerGamepad(InputDevice inputDevice) {
        int index = getNextAvailableIndex();
        if (index == -1) {
            return $assertionsDisabled;
        }
        this.mGamepadDevices[index] = new GamepadDevice(index, inputDevice);
        return true;
    }

    private void unregisterGamepad(int deviceId) {
        GamepadDevice gamepadDevice = getDeviceById(deviceId);
        if (gamepadDevice != null) {
            this.mGamepadDevices[gamepadDevice.getIndex()] = null;
        }
    }

    private static boolean isGamepadDevice(InputDevice inputDevice) {
        if (inputDevice != null && (inputDevice.getSources() & InputDeviceCompat.SOURCE_JOYSTICK) == InputDeviceCompat.SOURCE_JOYSTICK) {
            return true;
        }
        return $assertionsDisabled;
    }

    private GamepadDevice getGamepadForEvent(InputEvent event) {
        return getDeviceById(event.getDeviceId());
    }

    public static boolean isGamepadEvent(MotionEvent event) {
        return (event.getSource() & InputDeviceCompat.SOURCE_JOYSTICK) == InputDeviceCompat.SOURCE_JOYSTICK ? true : $assertionsDisabled;
    }

    public static boolean isGamepadEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case ConnectionResult.SERVICE_MISSING_PERMISSION /*19*/:
            case CameraLauncher.PERMISSION_DENIED_ERROR /*20*/:
            case ConnectionSubtype.SUBTYPE_ETHERNET /*21*/:
            case ConnectionSubtype.SUBTYPE_FAST_ETHERNET /*22*/:
                return true;
            default:
                return KeyEvent.isGamepadButton(keyCode);
        }
    }

    private static boolean isGamepadSupported() {
        return VERSION.SDK_INT >= 16 ? true : $assertionsDisabled;
    }

    @CalledByNative
    static void updateGamepadData(long webGamepadsPtr) {
        if (isGamepadSupported()) {
            getInstance().grabGamepadData(webGamepadsPtr);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void grabGamepadData(long r14) {
        /*
        r13 = this;
        r12 = r13.mLock;
        monitor-enter(r12);
        r4 = 0;
    L_0x0004:
        r1 = 4;
        if (r4 >= r1) goto L_0x003d;
    L_0x0007:
        r0 = r13.getDevice(r4);	 Catch:{ all -> 0x003a }
        if (r0 == 0) goto L_0x002d;
    L_0x000d:
        r0.updateButtonsAndAxesMapping();	 Catch:{ all -> 0x003a }
        r5 = r0.isStandardGamepad();	 Catch:{ all -> 0x003a }
        r6 = 1;
        r7 = r0.getName();	 Catch:{ all -> 0x003a }
        r8 = r0.getTimestamp();	 Catch:{ all -> 0x003a }
        r10 = r0.getAxes();	 Catch:{ all -> 0x003a }
        r11 = r0.getButtons();	 Catch:{ all -> 0x003a }
        r1 = r13;
        r2 = r14;
        r1.nativeSetGamepadData(r2, r4, r5, r6, r7, r8, r10, r11);	 Catch:{ all -> 0x003a }
    L_0x002a:
        r4 = r4 + 1;
        goto L_0x0004;
    L_0x002d:
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r10 = 0;
        r11 = 0;
        r1 = r13;
        r2 = r14;
        r1.nativeSetGamepadData(r2, r4, r5, r6, r7, r8, r10, r11);	 Catch:{ all -> 0x003a }
        goto L_0x002a;
    L_0x003a:
        r1 = move-exception;
        monitor-exit(r12);	 Catch:{ all -> 0x003a }
        throw r1;
    L_0x003d:
        monitor-exit(r12);	 Catch:{ all -> 0x003a }
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.content.browser.input.GamepadList.grabGamepadData(long):void");
    }

    @CalledByNative
    static void notifyForGamepadsAccess(boolean isAccessPaused) {
        if (isGamepadSupported()) {
            getInstance().setIsGamepadAccessed(!isAccessPaused ? true : $assertionsDisabled);
        }
    }

    private void setIsGamepadAccessed(boolean isGamepadAccessed) {
        synchronized (this.mLock) {
            this.mIsGamepadAccessed = isGamepadAccessed;
            if (isGamepadAccessed) {
                for (int i = 0; i < MAX_GAMEPADS; i++) {
                    GamepadDevice gamepadDevice = getDevice(i);
                    if (gamepadDevice != null) {
                        gamepadDevice.clearData();
                    }
                }
            }
        }
    }
}
