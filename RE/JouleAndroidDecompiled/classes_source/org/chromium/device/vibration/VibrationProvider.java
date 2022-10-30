package org.chromium.device.vibration;

import android.content.Context;
import android.media.AudioManager;
import android.os.Vibrator;
import android.util.Log;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

@JNINamespace("device")
class VibrationProvider {
    private static final String TAG = "VibrationProvider";
    private final AudioManager mAudioManager;
    private final boolean mHasVibratePermission;
    private final Vibrator mVibrator;

    @CalledByNative
    private static VibrationProvider create(Context context) {
        return new VibrationProvider(context);
    }

    @CalledByNative
    private void vibrate(long milliseconds) {
        if (this.mAudioManager.getRingerMode() != 0 && this.mHasVibratePermission) {
            this.mVibrator.vibrate(milliseconds);
        }
    }

    @CalledByNative
    private void cancelVibration() {
        if (this.mHasVibratePermission) {
            this.mVibrator.cancel();
        }
    }

    private VibrationProvider(Context context) {
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
        this.mVibrator = (Vibrator) context.getSystemService("vibrator");
        this.mHasVibratePermission = context.checkCallingOrSelfPermission("android.permission.VIBRATE") == 0;
        if (!this.mHasVibratePermission) {
            Log.w(TAG, "Failed to use vibrate API, requires VIBRATE permission.");
        }
    }
}
