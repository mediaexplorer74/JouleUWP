package org.chromium.media;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.provider.Settings.System;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.Log;

@JNINamespace("media")
class AudioManagerAndroid {
    private static final boolean DEBUG = false;
    private static final int DEFAULT_FRAME_PER_BUFFER = 256;
    private static final int DEFAULT_SAMPLING_RATE = 44100;
    private static final int DEVICE_BLUETOOTH_HEADSET = 3;
    private static final int DEVICE_COUNT = 4;
    private static final int DEVICE_DEFAULT = -2;
    private static final int DEVICE_EARPIECE = 2;
    private static final int DEVICE_INVALID = -1;
    private static final String[] DEVICE_NAMES;
    private static final int DEVICE_SPEAKERPHONE = 0;
    private static final int DEVICE_WIRED_HEADSET = 1;
    private static final int STATE_BLUETOOTH_SCO_INVALID = -1;
    private static final int STATE_BLUETOOTH_SCO_OFF = 0;
    private static final int STATE_BLUETOOTH_SCO_ON = 1;
    private static final int STATE_BLUETOOTH_SCO_TURNING_OFF = 3;
    private static final int STATE_BLUETOOTH_SCO_TURNING_ON = 2;
    private static final String[] SUPPORTED_AEC_MODELS;
    private static final String TAG = "cr.media";
    private static final Integer[] VALID_DEVICES;
    private boolean[] mAudioDevices;
    private final AudioManager mAudioManager;
    private BroadcastReceiver mBluetoothHeadsetReceiver;
    private BroadcastReceiver mBluetoothScoReceiver;
    private int mBluetoothScoState;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private int mCurrentVolume;
    private boolean mHasBluetoothPermission;
    private boolean mHasModifyAudioSettingsPermission;
    private boolean mIsInitialized;
    private final Object mLock;
    private final long mNativeAudioManagerAndroid;
    private final NonThreadSafe mNonThreadSafe;
    private int mRequestedAudioDevice;
    private int mSavedAudioMode;
    private boolean mSavedIsMicrophoneMute;
    private boolean mSavedIsSpeakerphoneOn;
    private ContentObserver mSettingsObserver;
    private HandlerThread mSettingsObserverThread;
    private BroadcastReceiver mWiredHeadsetReceiver;

    /* renamed from: org.chromium.media.AudioManagerAndroid.1 */
    class C03751 extends BroadcastReceiver {
        private static final int HAS_MIC = 1;
        private static final int HAS_NO_MIC = 0;
        private static final int STATE_PLUGGED = 1;
        private static final int STATE_UNPLUGGED = 0;

        C03751() {
        }

        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra("state", HAS_NO_MIC)) {
                case HAS_NO_MIC /*0*/:
                    synchronized (AudioManagerAndroid.this.mLock) {
                        AudioManagerAndroid.this.mAudioDevices[STATE_PLUGGED] = AudioManagerAndroid.DEBUG;
                        if (AudioManagerAndroid.this.hasEarpiece()) {
                            AudioManagerAndroid.this.mAudioDevices[AudioManagerAndroid.STATE_BLUETOOTH_SCO_TURNING_ON] = true;
                        }
                        break;
                    }
                    break;
                case STATE_PLUGGED /*1*/:
                    synchronized (AudioManagerAndroid.this.mLock) {
                        AudioManagerAndroid.this.mAudioDevices[STATE_PLUGGED] = true;
                        AudioManagerAndroid.this.mAudioDevices[AudioManagerAndroid.STATE_BLUETOOTH_SCO_TURNING_ON] = AudioManagerAndroid.DEBUG;
                        break;
                    }
                    break;
                default:
                    AudioManagerAndroid.loge("Invalid state");
                    break;
            }
            if (AudioManagerAndroid.this.deviceHasBeenRequested()) {
                AudioManagerAndroid.this.updateDeviceActivation();
            }
        }
    }

    /* renamed from: org.chromium.media.AudioManagerAndroid.2 */
    class C03762 extends BroadcastReceiver {
        C03762() {
        }

        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra("android.bluetooth.profile.extra.STATE", AudioManagerAndroid.STATE_BLUETOOTH_SCO_OFF)) {
                case AudioManagerAndroid.STATE_BLUETOOTH_SCO_OFF /*0*/:
                    synchronized (AudioManagerAndroid.this.mLock) {
                        AudioManagerAndroid.this.mAudioDevices[AudioManagerAndroid.STATE_BLUETOOTH_SCO_TURNING_OFF] = AudioManagerAndroid.DEBUG;
                        break;
                    }
                    break;
                case AudioManagerAndroid.STATE_BLUETOOTH_SCO_ON /*1*/:
                case AudioManagerAndroid.STATE_BLUETOOTH_SCO_TURNING_OFF /*3*/:
                    break;
                case AudioManagerAndroid.STATE_BLUETOOTH_SCO_TURNING_ON /*2*/:
                    synchronized (AudioManagerAndroid.this.mLock) {
                        AudioManagerAndroid.this.mAudioDevices[AudioManagerAndroid.STATE_BLUETOOTH_SCO_TURNING_OFF] = true;
                        break;
                    }
                    break;
                default:
                    AudioManagerAndroid.loge("Invalid state");
                    break;
            }
            if (AudioManagerAndroid.this.deviceHasBeenRequested()) {
                AudioManagerAndroid.this.updateDeviceActivation();
            }
        }
    }

    /* renamed from: org.chromium.media.AudioManagerAndroid.3 */
    class C03773 extends BroadcastReceiver {
        C03773() {
        }

        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra("android.media.extra.SCO_AUDIO_STATE", AudioManagerAndroid.STATE_BLUETOOTH_SCO_OFF)) {
                case AudioManagerAndroid.STATE_BLUETOOTH_SCO_OFF /*0*/:
                    AudioManagerAndroid.this.mBluetoothScoState = AudioManagerAndroid.STATE_BLUETOOTH_SCO_OFF;
                case AudioManagerAndroid.STATE_BLUETOOTH_SCO_ON /*1*/:
                    AudioManagerAndroid.this.mBluetoothScoState = AudioManagerAndroid.STATE_BLUETOOTH_SCO_ON;
                case AudioManagerAndroid.STATE_BLUETOOTH_SCO_TURNING_ON /*2*/:
                default:
                    AudioManagerAndroid.loge("Invalid state");
            }
        }
    }

    /* renamed from: org.chromium.media.AudioManagerAndroid.4 */
    class C03784 extends ContentObserver {
        C03784(Handler x0) {
            super(x0);
        }

        public void onChange(boolean selfChange) {
            boolean z = AudioManagerAndroid.DEBUG;
            super.onChange(selfChange);
            if (AudioManagerAndroid.this.mAudioManager.getMode() != AudioManagerAndroid.STATE_BLUETOOTH_SCO_TURNING_OFF) {
                throw new IllegalStateException("Only enable SettingsObserver in COMM mode");
            }
            int volume = AudioManagerAndroid.this.mAudioManager.getStreamVolume(AudioManagerAndroid.STATE_BLUETOOTH_SCO_OFF);
            AudioManagerAndroid audioManagerAndroid = AudioManagerAndroid.this;
            long access$900 = AudioManagerAndroid.this.mNativeAudioManagerAndroid;
            if (volume == 0) {
                z = true;
            }
            audioManagerAndroid.nativeSetMute(access$900, z);
        }
    }

    private static class AudioDeviceName {
        private final int mId;
        private final String mName;

        private AudioDeviceName(int id, String name) {
            this.mId = id;
            this.mName = name;
        }

        @CalledByNative("AudioDeviceName")
        private String id() {
            return String.valueOf(this.mId);
        }

        @CalledByNative("AudioDeviceName")
        private String name() {
            return this.mName;
        }
    }

    private static class NonThreadSafe {
        private final Long mThreadId;

        public NonThreadSafe() {
            this.mThreadId = Long.valueOf(0);
        }

        public boolean calledOnValidThread() {
            return true;
        }
    }

    private native void nativeSetMute(long j, boolean z);

    static {
        SUPPORTED_AEC_MODELS = new String[]{"GT-I9300", "GT-I9500", "GT-N7105", "Nexus 4", "Nexus 5", "Nexus 7", "SM-N9005", "SM-T310"};
        String[] strArr = new String[DEVICE_COUNT];
        strArr[STATE_BLUETOOTH_SCO_OFF] = "Speakerphone";
        strArr[STATE_BLUETOOTH_SCO_ON] = "Wired headset";
        strArr[STATE_BLUETOOTH_SCO_TURNING_ON] = "Headset earpiece";
        strArr[STATE_BLUETOOTH_SCO_TURNING_OFF] = "Bluetooth headset";
        DEVICE_NAMES = strArr;
        Integer[] numArr = new Integer[DEVICE_COUNT];
        numArr[STATE_BLUETOOTH_SCO_OFF] = Integer.valueOf(STATE_BLUETOOTH_SCO_OFF);
        numArr[STATE_BLUETOOTH_SCO_ON] = Integer.valueOf(STATE_BLUETOOTH_SCO_ON);
        numArr[STATE_BLUETOOTH_SCO_TURNING_ON] = Integer.valueOf(STATE_BLUETOOTH_SCO_TURNING_ON);
        numArr[STATE_BLUETOOTH_SCO_TURNING_OFF] = Integer.valueOf(STATE_BLUETOOTH_SCO_TURNING_OFF);
        VALID_DEVICES = numArr;
    }

    @CalledByNative
    private static AudioManagerAndroid createAudioManagerAndroid(Context context, long nativeAudioManagerAndroid) {
        return new AudioManagerAndroid(context, nativeAudioManagerAndroid);
    }

    private AudioManagerAndroid(Context context, long nativeAudioManagerAndroid) {
        this.mHasModifyAudioSettingsPermission = DEBUG;
        this.mHasBluetoothPermission = DEBUG;
        this.mSavedAudioMode = DEVICE_DEFAULT;
        this.mBluetoothScoState = STATE_BLUETOOTH_SCO_INVALID;
        this.mIsInitialized = DEBUG;
        this.mRequestedAudioDevice = STATE_BLUETOOTH_SCO_INVALID;
        this.mNonThreadSafe = new NonThreadSafe();
        this.mLock = new Object();
        this.mAudioDevices = new boolean[DEVICE_COUNT];
        this.mSettingsObserver = null;
        this.mSettingsObserverThread = null;
        this.mContext = context;
        this.mNativeAudioManagerAndroid = nativeAudioManagerAndroid;
        this.mAudioManager = (AudioManager) this.mContext.getSystemService("audio");
        this.mContentResolver = this.mContext.getContentResolver();
    }

    @CalledByNative
    private void init() {
        checkIfCalledOnValidThread();
        if (!this.mIsInitialized) {
            this.mHasModifyAudioSettingsPermission = hasPermission("android.permission.MODIFY_AUDIO_SETTINGS");
            this.mAudioDevices[STATE_BLUETOOTH_SCO_TURNING_ON] = hasEarpiece();
            this.mAudioDevices[STATE_BLUETOOTH_SCO_ON] = hasWiredHeadset();
            this.mAudioDevices[STATE_BLUETOOTH_SCO_OFF] = true;
            registerBluetoothIntentsIfNeeded();
            registerForWiredHeadsetIntentBroadcast();
            this.mIsInitialized = true;
        }
    }

    @CalledByNative
    private void close() {
        checkIfCalledOnValidThread();
        if (this.mIsInitialized) {
            stopObservingVolumeChanges();
            unregisterForWiredHeadsetIntentBroadcast();
            unregisterBluetoothIntentsIfNeeded();
            this.mIsInitialized = DEBUG;
        }
    }

    @CalledByNative
    private void setCommunicationAudioModeOn(boolean on) {
        if (!this.mHasModifyAudioSettingsPermission) {
            Log.m42w(TAG, "MODIFY_AUDIO_SETTINGS is missing => client will run with reduced functionality", new Object[STATE_BLUETOOTH_SCO_OFF]);
        } else if (on) {
            if (this.mSavedAudioMode != DEVICE_DEFAULT) {
                throw new IllegalStateException("Audio mode has already been set");
            }
            try {
                this.mSavedAudioMode = this.mAudioManager.getMode();
                this.mSavedIsSpeakerphoneOn = this.mAudioManager.isSpeakerphoneOn();
                this.mSavedIsMicrophoneMute = this.mAudioManager.isMicrophoneMute();
                try {
                    this.mAudioManager.setMode(STATE_BLUETOOTH_SCO_TURNING_OFF);
                    startObservingVolumeChanges();
                } catch (SecurityException e) {
                    logDeviceInfo();
                    throw e;
                }
            } catch (SecurityException e2) {
                logDeviceInfo();
                throw e2;
            }
        } else if (this.mSavedAudioMode == DEVICE_DEFAULT) {
            throw new IllegalStateException("Audio mode has not yet been set");
        } else {
            stopObservingVolumeChanges();
            setMicrophoneMute(this.mSavedIsMicrophoneMute);
            setSpeakerphoneOn(this.mSavedIsSpeakerphoneOn);
            try {
                this.mAudioManager.setMode(this.mSavedAudioMode);
                this.mSavedAudioMode = DEVICE_DEFAULT;
            } catch (SecurityException e22) {
                logDeviceInfo();
                throw e22;
            }
        }
    }

    @CalledByNative
    private boolean setDevice(String deviceId) {
        if (!this.mIsInitialized) {
            return DEBUG;
        }
        boolean hasRecordAudioPermission = hasPermission("android.permission.RECORD_AUDIO");
        if (this.mHasModifyAudioSettingsPermission && hasRecordAudioPermission) {
            int intDeviceId = deviceId.isEmpty() ? DEVICE_DEFAULT : Integer.parseInt(deviceId);
            if (intDeviceId == DEVICE_DEFAULT) {
                boolean[] devices;
                synchronized (this.mLock) {
                    devices = (boolean[]) this.mAudioDevices.clone();
                    this.mRequestedAudioDevice = DEVICE_DEFAULT;
                }
                setAudioDevice(selectDefaultDevice(devices));
                return true;
            } else if (!Arrays.asList(VALID_DEVICES).contains(Integer.valueOf(intDeviceId)) || !this.mAudioDevices[intDeviceId]) {
                return DEBUG;
            } else {
                synchronized (this.mLock) {
                    this.mRequestedAudioDevice = intDeviceId;
                }
                setAudioDevice(intDeviceId);
                return true;
            }
        }
        Log.m42w(TAG, "Requires MODIFY_AUDIO_SETTINGS and RECORD_AUDIO. Selected device will not be available for recording", new Object[STATE_BLUETOOTH_SCO_OFF]);
        return DEBUG;
    }

    @CalledByNative
    private AudioDeviceName[] getAudioInputDeviceNames() {
        if (!this.mIsInitialized) {
            return null;
        }
        boolean hasRecordAudioPermission = hasPermission("android.permission.RECORD_AUDIO");
        if (this.mHasModifyAudioSettingsPermission && hasRecordAudioPermission) {
            boolean[] devices;
            synchronized (this.mLock) {
                devices = (boolean[]) this.mAudioDevices.clone();
            }
            List<String> list = new ArrayList();
            AudioDeviceName[] array = new AudioDeviceName[getNumOfAudioDevices(devices)];
            int i = STATE_BLUETOOTH_SCO_OFF;
            for (int id = STATE_BLUETOOTH_SCO_OFF; id < DEVICE_COUNT; id += STATE_BLUETOOTH_SCO_ON) {
                if (devices[id]) {
                    array[i] = new AudioDeviceName(DEVICE_NAMES[id], null);
                    list.add(DEVICE_NAMES[id]);
                    i += STATE_BLUETOOTH_SCO_ON;
                }
            }
            return array;
        }
        Log.m42w(TAG, "Requires MODIFY_AUDIO_SETTINGS and RECORD_AUDIO. No audio device will be available for recording", new Object[STATE_BLUETOOTH_SCO_OFF]);
        return null;
    }

    @TargetApi(17)
    @CalledByNative
    private int getNativeOutputSampleRate() {
        if (VERSION.SDK_INT < 17) {
            return DEFAULT_SAMPLING_RATE;
        }
        String sampleRateString = this.mAudioManager.getProperty("android.media.property.OUTPUT_SAMPLE_RATE");
        if (sampleRateString == null) {
            return DEFAULT_SAMPLING_RATE;
        }
        return Integer.parseInt(sampleRateString);
    }

    @CalledByNative
    private static int getMinInputFrameSize(int sampleRate, int channels) {
        int channelConfig;
        if (channels == STATE_BLUETOOTH_SCO_ON) {
            channelConfig = 16;
        } else if (channels != STATE_BLUETOOTH_SCO_TURNING_ON) {
            return STATE_BLUETOOTH_SCO_INVALID;
        } else {
            channelConfig = 12;
        }
        return (AudioRecord.getMinBufferSize(sampleRate, channelConfig, STATE_BLUETOOTH_SCO_TURNING_ON) / STATE_BLUETOOTH_SCO_TURNING_ON) / channels;
    }

    @CalledByNative
    private static int getMinOutputFrameSize(int sampleRate, int channels) {
        int channelConfig;
        if (channels == STATE_BLUETOOTH_SCO_ON) {
            channelConfig = DEVICE_COUNT;
        } else if (channels != STATE_BLUETOOTH_SCO_TURNING_ON) {
            return STATE_BLUETOOTH_SCO_INVALID;
        } else {
            channelConfig = 12;
        }
        return (AudioTrack.getMinBufferSize(sampleRate, channelConfig, STATE_BLUETOOTH_SCO_TURNING_ON) / STATE_BLUETOOTH_SCO_TURNING_ON) / channels;
    }

    @CalledByNative
    private boolean isAudioLowLatencySupported() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.audio.low_latency");
    }

    @TargetApi(17)
    @CalledByNative
    private int getAudioLowLatencyOutputFrameSize() {
        if (VERSION.SDK_INT < 17) {
            return DEFAULT_FRAME_PER_BUFFER;
        }
        String framesPerBuffer = this.mAudioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER");
        if (framesPerBuffer != null) {
            return Integer.parseInt(framesPerBuffer);
        }
        return DEFAULT_FRAME_PER_BUFFER;
    }

    @TargetApi(16)
    @CalledByNative
    private static boolean shouldUseAcousticEchoCanceler() {
        if (VERSION.SDK_INT >= 16 && Arrays.asList(SUPPORTED_AEC_MODELS).contains(Build.MODEL)) {
            return AcousticEchoCanceler.isAvailable();
        }
        return DEBUG;
    }

    private void checkIfCalledOnValidThread() {
    }

    private void registerBluetoothIntentsIfNeeded() {
        this.mHasBluetoothPermission = hasPermission("android.permission.BLUETOOTH");
        if (this.mHasBluetoothPermission) {
            this.mAudioDevices[STATE_BLUETOOTH_SCO_TURNING_OFF] = hasBluetoothHeadset();
            registerForBluetoothHeadsetIntentBroadcast();
            registerForBluetoothScoIntentBroadcast();
            return;
        }
        Log.m42w(TAG, "Requires BLUETOOTH permission", new Object[STATE_BLUETOOTH_SCO_OFF]);
    }

    private void unregisterBluetoothIntentsIfNeeded() {
        if (this.mHasBluetoothPermission) {
            this.mAudioManager.stopBluetoothSco();
            unregisterForBluetoothHeadsetIntentBroadcast();
            unregisterForBluetoothScoIntentBroadcast();
        }
    }

    private void setSpeakerphoneOn(boolean on) {
        if (this.mAudioManager.isSpeakerphoneOn() != on) {
            this.mAudioManager.setSpeakerphoneOn(on);
        }
    }

    private void setMicrophoneMute(boolean on) {
        if (this.mAudioManager.isMicrophoneMute() != on) {
            this.mAudioManager.setMicrophoneMute(on);
        }
    }

    private boolean isMicrophoneMute() {
        return this.mAudioManager.isMicrophoneMute();
    }

    private boolean hasEarpiece() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.telephony");
    }

    @Deprecated
    private boolean hasWiredHeadset() {
        return this.mAudioManager.isWiredHeadsetOn();
    }

    private boolean hasPermission(String permission) {
        return this.mContext.checkPermission(permission, Process.myPid(), Process.myUid()) == 0 ? true : DEBUG;
    }

    @TargetApi(18)
    private boolean hasBluetoothHeadset() {
        boolean z = true;
        if (this.mHasBluetoothPermission) {
            BluetoothAdapter btAdapter;
            if (VERSION.SDK_INT >= 18) {
                btAdapter = ((BluetoothManager) this.mContext.getSystemService("bluetooth")).getAdapter();
            } else {
                btAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            if (btAdapter == null) {
                return DEBUG;
            }
            int profileConnectionState = btAdapter.getProfileConnectionState(STATE_BLUETOOTH_SCO_ON);
            if (!(btAdapter.isEnabled() && profileConnectionState == STATE_BLUETOOTH_SCO_TURNING_ON)) {
                z = STATE_BLUETOOTH_SCO_OFF;
            }
            return z;
        }
        Log.m42w(TAG, "hasBluetoothHeadset() requires BLUETOOTH permission", new Object[STATE_BLUETOOTH_SCO_OFF]);
        return DEBUG;
    }

    private void registerForWiredHeadsetIntentBroadcast() {
        IntentFilter filter = new IntentFilter("android.intent.action.HEADSET_PLUG");
        this.mWiredHeadsetReceiver = new C03751();
        this.mContext.registerReceiver(this.mWiredHeadsetReceiver, filter);
    }

    private void unregisterForWiredHeadsetIntentBroadcast() {
        this.mContext.unregisterReceiver(this.mWiredHeadsetReceiver);
        this.mWiredHeadsetReceiver = null;
    }

    private void registerForBluetoothHeadsetIntentBroadcast() {
        IntentFilter filter = new IntentFilter("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
        this.mBluetoothHeadsetReceiver = new C03762();
        this.mContext.registerReceiver(this.mBluetoothHeadsetReceiver, filter);
    }

    private void unregisterForBluetoothHeadsetIntentBroadcast() {
        this.mContext.unregisterReceiver(this.mBluetoothHeadsetReceiver);
        this.mBluetoothHeadsetReceiver = null;
    }

    private void registerForBluetoothScoIntentBroadcast() {
        IntentFilter filter = new IntentFilter("android.media.ACTION_SCO_AUDIO_STATE_UPDATED");
        this.mBluetoothScoReceiver = new C03773();
        this.mContext.registerReceiver(this.mBluetoothScoReceiver, filter);
    }

    private void unregisterForBluetoothScoIntentBroadcast() {
        this.mContext.unregisterReceiver(this.mBluetoothScoReceiver);
        this.mBluetoothScoReceiver = null;
    }

    private void startBluetoothSco() {
        if (this.mHasBluetoothPermission && this.mBluetoothScoState != STATE_BLUETOOTH_SCO_ON && this.mBluetoothScoState != STATE_BLUETOOTH_SCO_TURNING_ON) {
            if (this.mAudioManager.isBluetoothScoOn()) {
                this.mBluetoothScoState = STATE_BLUETOOTH_SCO_ON;
                return;
            }
            this.mBluetoothScoState = STATE_BLUETOOTH_SCO_TURNING_ON;
            this.mAudioManager.startBluetoothSco();
        }
    }

    private void stopBluetoothSco() {
        if (!this.mHasBluetoothPermission) {
            return;
        }
        if (this.mBluetoothScoState != STATE_BLUETOOTH_SCO_ON && this.mBluetoothScoState != STATE_BLUETOOTH_SCO_TURNING_ON) {
            return;
        }
        if (this.mAudioManager.isBluetoothScoOn()) {
            this.mBluetoothScoState = STATE_BLUETOOTH_SCO_TURNING_OFF;
            this.mAudioManager.stopBluetoothSco();
            return;
        }
        loge("Unable to stop BT SCO since it is already disabled");
    }

    private void setAudioDevice(int device) {
        if (device == STATE_BLUETOOTH_SCO_TURNING_OFF) {
            startBluetoothSco();
        } else {
            stopBluetoothSco();
        }
        switch (device) {
            case STATE_BLUETOOTH_SCO_OFF /*0*/:
                setSpeakerphoneOn(true);
                break;
            case STATE_BLUETOOTH_SCO_ON /*1*/:
                setSpeakerphoneOn(DEBUG);
                break;
            case STATE_BLUETOOTH_SCO_TURNING_ON /*2*/:
                setSpeakerphoneOn(DEBUG);
                break;
            case STATE_BLUETOOTH_SCO_TURNING_OFF /*3*/:
                break;
            default:
                loge("Invalid audio device selection");
                break;
        }
        reportUpdate();
    }

    private static int selectDefaultDevice(boolean[] devices) {
        if (devices[STATE_BLUETOOTH_SCO_ON]) {
            return STATE_BLUETOOTH_SCO_ON;
        }
        if (devices[STATE_BLUETOOTH_SCO_TURNING_OFF]) {
            return STATE_BLUETOOTH_SCO_TURNING_OFF;
        }
        return STATE_BLUETOOTH_SCO_OFF;
    }

    private boolean deviceHasBeenRequested() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mRequestedAudioDevice != STATE_BLUETOOTH_SCO_INVALID ? true : DEBUG;
        }
        return z;
    }

    private void updateDeviceActivation() {
        synchronized (this.mLock) {
            int requested = this.mRequestedAudioDevice;
            boolean[] devices = (boolean[]) this.mAudioDevices.clone();
        }
        if (requested == STATE_BLUETOOTH_SCO_INVALID) {
            loge("Unable to activate device since no device is selected");
        } else if (requested == DEVICE_DEFAULT || !devices[requested]) {
            setAudioDevice(selectDefaultDevice(devices));
        } else {
            setAudioDevice(requested);
        }
    }

    private static int getNumOfAudioDevices(boolean[] devices) {
        int count = STATE_BLUETOOTH_SCO_OFF;
        for (int i = STATE_BLUETOOTH_SCO_OFF; i < DEVICE_COUNT; i += STATE_BLUETOOTH_SCO_ON) {
            if (devices[i]) {
                count += STATE_BLUETOOTH_SCO_ON;
            }
        }
        return count;
    }

    private void reportUpdate() {
        synchronized (this.mLock) {
            List<String> devices = new ArrayList();
            for (int i = STATE_BLUETOOTH_SCO_OFF; i < DEVICE_COUNT; i += STATE_BLUETOOTH_SCO_ON) {
                if (this.mAudioDevices[i]) {
                    devices.add(DEVICE_NAMES[i]);
                }
            }
        }
    }

    private void logDeviceInfo() {
        logd("Android SDK: " + VERSION.SDK_INT + ", " + "Release: " + VERSION.RELEASE + ", " + "Brand: " + Build.BRAND + ", " + "Device: " + Build.DEVICE + ", " + "Id: " + Build.ID + ", " + "Hardware: " + Build.HARDWARE + ", " + "Manufacturer: " + Build.MANUFACTURER + ", " + "Model: " + Build.MODEL + ", " + "Product: " + Build.PRODUCT);
    }

    private static void logd(String msg) {
        Log.m24d(TAG, msg);
    }

    private static void loge(String msg) {
        Log.m32e(TAG, msg, new Object[STATE_BLUETOOTH_SCO_OFF]);
    }

    private void startObservingVolumeChanges() {
        if (this.mSettingsObserverThread == null) {
            this.mSettingsObserverThread = new HandlerThread("SettingsObserver");
            this.mSettingsObserverThread.start();
            this.mSettingsObserver = new C03784(new Handler(this.mSettingsObserverThread.getLooper()));
            this.mContentResolver.registerContentObserver(System.CONTENT_URI, true, this.mSettingsObserver);
        }
    }

    private void stopObservingVolumeChanges() {
        if (this.mSettingsObserverThread != null) {
            this.mContentResolver.unregisterContentObserver(this.mSettingsObserver);
            this.mSettingsObserver = null;
            this.mSettingsObserverThread.quit();
            try {
                this.mSettingsObserverThread.join();
            } catch (InterruptedException e) {
                Object[] objArr = new Object[STATE_BLUETOOTH_SCO_ON];
                objArr[STATE_BLUETOOTH_SCO_OFF] = e;
                Log.m32e(TAG, "Thread.join() exception: ", objArr);
            }
            this.mSettingsObserverThread = null;
        }
    }
}
