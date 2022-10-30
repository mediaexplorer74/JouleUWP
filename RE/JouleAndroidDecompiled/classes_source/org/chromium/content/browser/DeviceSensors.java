package org.chromium.content.browser;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import com.google.android.gms.common.ConnectionResult;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.chromium.base.CalledByNative;
import org.chromium.base.CollectionUtil;
import org.chromium.base.JNINamespace;
import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;
import org.chromium.base.VisibleForTesting;

@JNINamespace("content")
class DeviceSensors implements SensorEventListener {
    static final int DEVICE_LIGHT = 2;
    static final Set<Integer> DEVICE_LIGHT_SENSORS;
    static final int DEVICE_MOTION = 1;
    static final Set<Integer> DEVICE_MOTION_SENSORS;
    static final int DEVICE_ORIENTATION = 0;
    static final Set<Integer> DEVICE_ORIENTATION_BACKUP_SENSORS;
    static final Set<Integer> DEVICE_ORIENTATION_DEFAULT_SENSORS;
    private static final String TAG = "cr.DeviceSensors";
    private static DeviceSensors sSingleton;
    private static Object sSingletonLock;
    @VisibleForTesting
    final Set<Integer> mActiveSensors;
    private final Context mAppContext;
    boolean mDeviceLightIsActive;
    boolean mDeviceMotionIsActive;
    boolean mDeviceOrientationIsActive;
    Set<Integer> mDeviceOrientationSensors;
    private float[] mDeviceRotationMatrix;
    private Handler mHandler;
    private final Object mHandlerLock;
    private float[] mMagneticFieldVector;
    private long mNativePtr;
    private final Object mNativePtrLock;
    private double[] mRotationAngles;
    private SensorManagerProxy mSensorManagerProxy;
    private Thread mThread;
    private float[] mTruncatedRotationVector;
    boolean mUseBackupOrientationSensors;

    interface SensorManagerProxy {
        boolean registerListener(SensorEventListener sensorEventListener, int i, int i2, Handler handler);

        void unregisterListener(SensorEventListener sensorEventListener, int i);
    }

    static class SensorManagerProxyImpl implements SensorManagerProxy {
        private final SensorManager mSensorManager;

        SensorManagerProxyImpl(SensorManager sensorManager) {
            this.mSensorManager = sensorManager;
        }

        public boolean registerListener(SensorEventListener listener, int sensorType, int rate, Handler handler) {
            List<Sensor> sensors = this.mSensorManager.getSensorList(sensorType);
            if (sensors.isEmpty()) {
                return false;
            }
            return this.mSensorManager.registerListener(listener, (Sensor) sensors.get(DeviceSensors.DEVICE_ORIENTATION), rate, handler);
        }

        public void unregisterListener(SensorEventListener listener, int sensorType) {
            List<Sensor> sensors = this.mSensorManager.getSensorList(sensorType);
            if (!sensors.isEmpty()) {
                this.mSensorManager.unregisterListener(listener, (Sensor) sensors.get(DeviceSensors.DEVICE_ORIENTATION));
            }
        }
    }

    private native void nativeGotAcceleration(long j, double d, double d2, double d3);

    private native void nativeGotAccelerationIncludingGravity(long j, double d, double d2, double d3);

    private native void nativeGotLight(long j, double d);

    private native void nativeGotOrientation(long j, double d, double d2, double d3);

    private native void nativeGotRotationRate(long j, double d, double d2, double d3);

    static {
        sSingletonLock = new Object();
        Integer[] numArr = new Integer[DEVICE_MOTION];
        numArr[DEVICE_ORIENTATION] = Integer.valueOf(11);
        DEVICE_ORIENTATION_DEFAULT_SENSORS = CollectionUtil.newHashSet(numArr);
        numArr = new Integer[DEVICE_LIGHT];
        numArr[DEVICE_ORIENTATION] = Integer.valueOf(DEVICE_MOTION);
        numArr[DEVICE_MOTION] = Integer.valueOf(DEVICE_LIGHT);
        DEVICE_ORIENTATION_BACKUP_SENSORS = CollectionUtil.newHashSet(numArr);
        DEVICE_MOTION_SENSORS = CollectionUtil.newHashSet(Integer.valueOf(DEVICE_MOTION), Integer.valueOf(10), Integer.valueOf(4));
        numArr = new Integer[DEVICE_MOTION];
        numArr[DEVICE_ORIENTATION] = Integer.valueOf(5);
        DEVICE_LIGHT_SENSORS = CollectionUtil.newHashSet(numArr);
    }

    protected DeviceSensors(Context context) {
        this.mHandlerLock = new Object();
        this.mNativePtrLock = new Object();
        this.mActiveSensors = new HashSet();
        this.mDeviceOrientationSensors = DEVICE_ORIENTATION_DEFAULT_SENSORS;
        this.mDeviceLightIsActive = false;
        this.mDeviceMotionIsActive = false;
        this.mDeviceOrientationIsActive = false;
        this.mUseBackupOrientationSensors = false;
        this.mAppContext = context.getApplicationContext();
    }

    @CalledByNative
    public boolean start(long nativePtr, int eventType, int rateInMicroseconds) {
        synchronized (this.mNativePtrLock) {
            boolean success;
            switch (eventType) {
                case DEVICE_ORIENTATION /*0*/:
                    success = registerSensors(this.mDeviceOrientationSensors, rateInMicroseconds, true);
                    if (!success) {
                        this.mDeviceOrientationSensors = DEVICE_ORIENTATION_BACKUP_SENSORS;
                        success = registerSensors(this.mDeviceOrientationSensors, rateInMicroseconds, true);
                        this.mUseBackupOrientationSensors = success;
                    }
                    ensureRotationStructuresAllocated();
                    break;
                case DEVICE_MOTION /*1*/:
                    success = registerSensors(DEVICE_MOTION_SENSORS, rateInMicroseconds, false);
                    break;
                case DEVICE_LIGHT /*2*/:
                    success = registerSensors(DEVICE_LIGHT_SENSORS, rateInMicroseconds, true);
                    break;
                default:
                    Object[] objArr = new Object[DEVICE_MOTION];
                    objArr[DEVICE_ORIENTATION] = Integer.valueOf(eventType);
                    Log.m32e(TAG, "Unknown event type: %d", objArr);
                    return false;
            }
            if (success) {
                this.mNativePtr = nativePtr;
                setEventTypeActive(eventType, true);
            }
            return success;
        }
    }

    @CalledByNative
    public int getNumberActiveDeviceMotionSensors() {
        Set<Integer> deviceMotionSensors = new HashSet(DEVICE_MOTION_SENSORS);
        deviceMotionSensors.removeAll(this.mActiveSensors);
        return DEVICE_MOTION_SENSORS.size() - deviceMotionSensors.size();
    }

    @CalledByNative
    public boolean isUsingBackupSensorsForOrientation() {
        return this.mUseBackupOrientationSensors;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @org.chromium.base.CalledByNative
    public void stop(int r9) {
        /*
        r8 = this;
        r1 = new java.util.HashSet;
        r1.<init>();
        r3 = r8.mNativePtrLock;
        monitor-enter(r3);
        switch(r9) {
            case 0: goto L_0x001e;
            case 1: goto L_0x0052;
            case 2: goto L_0x0065;
            default: goto L_0x000b;
        };
    L_0x000b:
        r2 = "cr.DeviceSensors";
        r4 = "Unknown event type: %d";
        r5 = 1;
        r5 = new java.lang.Object[r5];	 Catch:{ all -> 0x004f }
        r6 = 0;
        r7 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x004f }
        r5[r6] = r7;	 Catch:{ all -> 0x004f }
        org.chromium.base.Log.m32e(r2, r4, r5);	 Catch:{ all -> 0x004f }
        monitor-exit(r3);	 Catch:{ all -> 0x004f }
    L_0x001d:
        return;
    L_0x001e:
        r2 = r8.mDeviceMotionIsActive;	 Catch:{ all -> 0x004f }
        if (r2 == 0) goto L_0x0027;
    L_0x0022:
        r2 = DEVICE_MOTION_SENSORS;	 Catch:{ all -> 0x004f }
        r1.addAll(r2);	 Catch:{ all -> 0x004f }
    L_0x0027:
        r2 = r8.mDeviceLightIsActive;	 Catch:{ all -> 0x004f }
        if (r2 == 0) goto L_0x0030;
    L_0x002b:
        r2 = DEVICE_LIGHT_SENSORS;	 Catch:{ all -> 0x004f }
        r1.addAll(r2);	 Catch:{ all -> 0x004f }
    L_0x0030:
        r0 = new java.util.HashSet;	 Catch:{ all -> 0x004f }
        r2 = r8.mActiveSensors;	 Catch:{ all -> 0x004f }
        r0.<init>(r2);	 Catch:{ all -> 0x004f }
        r0.removeAll(r1);	 Catch:{ all -> 0x004f }
        r8.unregisterSensors(r0);	 Catch:{ all -> 0x004f }
        r2 = 0;
        r8.setEventTypeActive(r9, r2);	 Catch:{ all -> 0x004f }
        r2 = r8.mActiveSensors;	 Catch:{ all -> 0x004f }
        r2 = r2.isEmpty();	 Catch:{ all -> 0x004f }
        if (r2 == 0) goto L_0x004d;
    L_0x0049:
        r4 = 0;
        r8.mNativePtr = r4;	 Catch:{ all -> 0x004f }
    L_0x004d:
        monitor-exit(r3);	 Catch:{ all -> 0x004f }
        goto L_0x001d;
    L_0x004f:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x004f }
        throw r2;
    L_0x0052:
        r2 = r8.mDeviceOrientationIsActive;	 Catch:{ all -> 0x004f }
        if (r2 == 0) goto L_0x005b;
    L_0x0056:
        r2 = r8.mDeviceOrientationSensors;	 Catch:{ all -> 0x004f }
        r1.addAll(r2);	 Catch:{ all -> 0x004f }
    L_0x005b:
        r2 = r8.mDeviceLightIsActive;	 Catch:{ all -> 0x004f }
        if (r2 == 0) goto L_0x0030;
    L_0x005f:
        r2 = DEVICE_LIGHT_SENSORS;	 Catch:{ all -> 0x004f }
        r1.addAll(r2);	 Catch:{ all -> 0x004f }
        goto L_0x0030;
    L_0x0065:
        r2 = r8.mDeviceMotionIsActive;	 Catch:{ all -> 0x004f }
        if (r2 == 0) goto L_0x006e;
    L_0x0069:
        r2 = DEVICE_MOTION_SENSORS;	 Catch:{ all -> 0x004f }
        r1.addAll(r2);	 Catch:{ all -> 0x004f }
    L_0x006e:
        r2 = r8.mDeviceOrientationIsActive;	 Catch:{ all -> 0x004f }
        if (r2 == 0) goto L_0x0030;
    L_0x0072:
        r2 = r8.mDeviceOrientationSensors;	 Catch:{ all -> 0x004f }
        r1.addAll(r2);	 Catch:{ all -> 0x004f }
        goto L_0x0030;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.content.browser.DeviceSensors.stop(int):void");
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        sensorChanged(event.sensor.getType(), event.values);
    }

    @VisibleForTesting
    void sensorChanged(int type, float[] values) {
        switch (type) {
            case DEVICE_MOTION /*1*/:
                if (this.mDeviceMotionIsActive) {
                    gotAccelerationIncludingGravity((double) values[DEVICE_ORIENTATION], (double) values[DEVICE_MOTION], (double) values[DEVICE_LIGHT]);
                }
                if (this.mDeviceOrientationIsActive && this.mUseBackupOrientationSensors) {
                    getOrientationFromGeomagneticVectors(values, this.mMagneticFieldVector);
                }
            case DEVICE_LIGHT /*2*/:
                if (this.mDeviceOrientationIsActive && this.mUseBackupOrientationSensors) {
                    if (this.mMagneticFieldVector == null) {
                        this.mMagneticFieldVector = new float[3];
                    }
                    System.arraycopy(values, DEVICE_ORIENTATION, this.mMagneticFieldVector, DEVICE_ORIENTATION, this.mMagneticFieldVector.length);
                }
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                if (this.mDeviceMotionIsActive) {
                    gotRotationRate((double) values[DEVICE_ORIENTATION], (double) values[DEVICE_MOTION], (double) values[DEVICE_LIGHT]);
                }
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                if (this.mDeviceLightIsActive) {
                    gotLight((double) values[DEVICE_ORIENTATION]);
                }
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                if (this.mDeviceMotionIsActive) {
                    gotAcceleration((double) values[DEVICE_ORIENTATION], (double) values[DEVICE_MOTION], (double) values[DEVICE_LIGHT]);
                }
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                if (!this.mDeviceOrientationIsActive) {
                    return;
                }
                if (values.length > 4) {
                    if (this.mTruncatedRotationVector == null) {
                        this.mTruncatedRotationVector = new float[4];
                    }
                    System.arraycopy(values, DEVICE_ORIENTATION, this.mTruncatedRotationVector, DEVICE_ORIENTATION, 4);
                    getOrientationFromRotationVector(this.mTruncatedRotationVector);
                    return;
                }
                getOrientationFromRotationVector(values);
            default:
        }
    }

    @VisibleForTesting
    public static double[] computeDeviceOrientationFromRotationMatrix(float[] matrixR, double[] values) {
        if (matrixR.length == 9) {
            if (matrixR[8] > 0.0f) {
                values[DEVICE_ORIENTATION] = Math.atan2((double) (-matrixR[DEVICE_MOTION]), (double) matrixR[4]);
                values[DEVICE_MOTION] = Math.asin((double) matrixR[7]);
                values[DEVICE_LIGHT] = Math.atan2((double) (-matrixR[6]), (double) matrixR[8]);
            } else if (matrixR[8] < 0.0f) {
                values[DEVICE_ORIENTATION] = Math.atan2((double) matrixR[DEVICE_MOTION], (double) (-matrixR[4]));
                values[DEVICE_MOTION] = -Math.asin((double) matrixR[7]);
                values[DEVICE_MOTION] = (values[DEVICE_MOTION] >= 0.0d ? -3.141592653589793d : 3.141592653589793d) + values[DEVICE_MOTION];
                values[DEVICE_LIGHT] = Math.atan2((double) matrixR[6], (double) (-matrixR[8]));
            } else if (matrixR[6] > 0.0f) {
                values[DEVICE_ORIENTATION] = Math.atan2((double) (-matrixR[DEVICE_MOTION]), (double) matrixR[4]);
                values[DEVICE_MOTION] = Math.asin((double) matrixR[7]);
                values[DEVICE_LIGHT] = -1.5707963267948966d;
            } else if (matrixR[6] < 0.0f) {
                values[DEVICE_ORIENTATION] = Math.atan2((double) matrixR[DEVICE_MOTION], (double) (-matrixR[4]));
                values[DEVICE_MOTION] = -Math.asin((double) matrixR[7]);
                values[DEVICE_MOTION] = (values[DEVICE_MOTION] >= 0.0d ? -3.141592653589793d : 3.141592653589793d) + values[DEVICE_MOTION];
                values[DEVICE_LIGHT] = -1.5707963267948966d;
            } else {
                values[DEVICE_ORIENTATION] = Math.atan2((double) matrixR[3], (double) matrixR[DEVICE_ORIENTATION]);
                values[DEVICE_MOTION] = matrixR[7] > 0.0f ? 1.5707963267948966d : -1.5707963267948966d;
                values[DEVICE_LIGHT] = 0.0d;
            }
            if (values[DEVICE_ORIENTATION] < 0.0d) {
                values[DEVICE_ORIENTATION] = values[DEVICE_ORIENTATION] + 6.283185307179586d;
            }
        }
        return values;
    }

    private void getOrientationFromRotationVector(float[] rotationVector) {
        SensorManager.getRotationMatrixFromVector(this.mDeviceRotationMatrix, rotationVector);
        computeDeviceOrientationFromRotationMatrix(this.mDeviceRotationMatrix, this.mRotationAngles);
        gotOrientation(Math.toDegrees(this.mRotationAngles[DEVICE_ORIENTATION]), Math.toDegrees(this.mRotationAngles[DEVICE_MOTION]), Math.toDegrees(this.mRotationAngles[DEVICE_LIGHT]));
    }

    private void getOrientationFromGeomagneticVectors(float[] acceleration, float[] magnetic) {
        if (acceleration != null && magnetic != null && SensorManager.getRotationMatrix(this.mDeviceRotationMatrix, null, acceleration, magnetic)) {
            computeDeviceOrientationFromRotationMatrix(this.mDeviceRotationMatrix, this.mRotationAngles);
            gotOrientation(Math.toDegrees(this.mRotationAngles[DEVICE_ORIENTATION]), Math.toDegrees(this.mRotationAngles[DEVICE_MOTION]), Math.toDegrees(this.mRotationAngles[DEVICE_LIGHT]));
        }
    }

    private SensorManagerProxy getSensorManagerProxy() {
        if (this.mSensorManagerProxy != null) {
            return this.mSensorManagerProxy;
        }
        ThreadUtils.assertOnUiThread();
        SensorManager sensorManager = (SensorManager) this.mAppContext.getSystemService("sensor");
        if (sensorManager != null) {
            this.mSensorManagerProxy = new SensorManagerProxyImpl(sensorManager);
        }
        return this.mSensorManagerProxy;
    }

    @VisibleForTesting
    void setSensorManagerProxy(SensorManagerProxy sensorManagerProxy) {
        this.mSensorManagerProxy = sensorManagerProxy;
    }

    private void setEventTypeActive(int eventType, boolean value) {
        switch (eventType) {
            case DEVICE_ORIENTATION /*0*/:
                this.mDeviceOrientationIsActive = value;
            case DEVICE_MOTION /*1*/:
                this.mDeviceMotionIsActive = value;
            case DEVICE_LIGHT /*2*/:
                this.mDeviceLightIsActive = value;
            default:
        }
    }

    private void ensureRotationStructuresAllocated() {
        if (this.mDeviceRotationMatrix == null) {
            this.mDeviceRotationMatrix = new float[9];
        }
        if (this.mRotationAngles == null) {
            this.mRotationAngles = new double[3];
        }
    }

    private boolean registerSensors(Set<Integer> sensorTypes, int rateInMicroseconds, boolean failOnMissingSensor) {
        Set<Integer> sensorsToActivate = new HashSet(sensorTypes);
        sensorsToActivate.removeAll(this.mActiveSensors);
        boolean success = false;
        for (Integer sensorType : sensorsToActivate) {
            boolean result = registerForSensorType(sensorType.intValue(), rateInMicroseconds);
            if (!result && failOnMissingSensor) {
                unregisterSensors(sensorsToActivate);
                return false;
            } else if (result) {
                this.mActiveSensors.add(sensorType);
                success = true;
            }
        }
        return success;
    }

    private void unregisterSensors(Iterable<Integer> sensorTypes) {
        for (Integer sensorType : sensorTypes) {
            if (this.mActiveSensors.contains(sensorType)) {
                getSensorManagerProxy().unregisterListener(this, sensorType.intValue());
                this.mActiveSensors.remove(sensorType);
            }
        }
    }

    private boolean registerForSensorType(int type, int rateInMicroseconds) {
        SensorManagerProxy sensorManager = getSensorManagerProxy();
        if (sensorManager == null) {
            return false;
        }
        return sensorManager.registerListener(this, type, rateInMicroseconds, getHandler());
    }

    protected void gotOrientation(double alpha, double beta, double gamma) {
        synchronized (this.mNativePtrLock) {
            if (this.mNativePtr != 0) {
                nativeGotOrientation(this.mNativePtr, alpha, beta, gamma);
            }
        }
    }

    protected void gotAcceleration(double x, double y, double z) {
        synchronized (this.mNativePtrLock) {
            if (this.mNativePtr != 0) {
                nativeGotAcceleration(this.mNativePtr, x, y, z);
            }
        }
    }

    protected void gotAccelerationIncludingGravity(double x, double y, double z) {
        synchronized (this.mNativePtrLock) {
            if (this.mNativePtr != 0) {
                nativeGotAccelerationIncludingGravity(this.mNativePtr, x, y, z);
            }
        }
    }

    protected void gotRotationRate(double alpha, double beta, double gamma) {
        synchronized (this.mNativePtrLock) {
            if (this.mNativePtr != 0) {
                nativeGotRotationRate(this.mNativePtr, alpha, beta, gamma);
            }
        }
    }

    protected void gotLight(double value) {
        synchronized (this.mNativePtrLock) {
            if (this.mNativePtr != 0) {
                nativeGotLight(this.mNativePtr, value);
            }
        }
    }

    private Handler getHandler() {
        Handler handler;
        synchronized (this.mHandlerLock) {
            if (this.mHandler == null) {
                HandlerThread thread = new HandlerThread("DeviceMotionAndOrientation");
                thread.start();
                this.mHandler = new Handler(thread.getLooper());
            }
            handler = this.mHandler;
        }
        return handler;
    }

    @CalledByNative
    static DeviceSensors getInstance(Context appContext) {
        DeviceSensors deviceSensors;
        synchronized (sSingletonLock) {
            if (sSingleton == null) {
                sSingleton = new DeviceSensors(appContext);
            }
            deviceSensors = sSingleton;
        }
        return deviceSensors;
    }
}
