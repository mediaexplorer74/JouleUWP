package org.chromium.content.browser;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import com.google.android.gms.common.ConnectionResult;
import java.util.ArrayList;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.PackageUtils;

@JNINamespace("content")
public class SpeechRecognition {
    private static final int PROVIDER_MIN_VERSION = 300207030;
    private static final String PROVIDER_PACKAGE_NAME = "com.google.android.googlequicksearchbox";
    private static final int STATE_AWAITING_SPEECH = 1;
    private static final int STATE_CAPTURING_SPEECH = 2;
    private static final int STATE_IDLE = 0;
    private static ComponentName sRecognitionProvider;
    private final Context mContext;
    private boolean mContinuous;
    private final Intent mIntent;
    private final RecognitionListener mListener;
    private long mNativeSpeechRecognizerImplAndroid;
    private SpeechRecognizer mRecognizer;
    private int mState;

    class Listener implements RecognitionListener {
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !SpeechRecognition.class.desiredAssertionStatus();
        }

        Listener() {
        }

        public void onBeginningOfSpeech() {
            SpeechRecognition.this.mState = SpeechRecognition.STATE_CAPTURING_SPEECH;
            SpeechRecognition.this.nativeOnSoundStart(SpeechRecognition.this.mNativeSpeechRecognizerImplAndroid);
        }

        public void onBufferReceived(byte[] buffer) {
        }

        public void onEndOfSpeech() {
            if (!SpeechRecognition.this.mContinuous) {
                SpeechRecognition.this.nativeOnSoundEnd(SpeechRecognition.this.mNativeSpeechRecognizerImplAndroid);
                SpeechRecognition.this.nativeOnAudioEnd(SpeechRecognition.this.mNativeSpeechRecognizerImplAndroid);
                SpeechRecognition.this.mState = 0;
            }
        }

        public void onError(int error) {
            int code;
            switch (error) {
                case SpeechRecognition.STATE_AWAITING_SPEECH /*1*/:
                case SpeechRecognition.STATE_CAPTURING_SPEECH /*2*/:
                case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                    code = 4;
                    break;
                case ConnectionResult.SERVICE_DISABLED /*3*/:
                    code = 3;
                    break;
                case ConnectionResult.INVALID_ACCOUNT /*5*/:
                    code = SpeechRecognition.STATE_CAPTURING_SPEECH;
                    break;
                case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                    code = SpeechRecognition.STATE_AWAITING_SPEECH;
                    break;
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    code = 9;
                    break;
                case ConnectionResult.INTERNAL_ERROR /*8*/:
                case ConnectionResult.SERVICE_INVALID /*9*/:
                    code = 5;
                    break;
                default:
                    if (!$assertionsDisabled) {
                        throw new AssertionError();
                    }
                    return;
            }
            SpeechRecognition.this.terminate(code);
        }

        public void onEvent(int event, Bundle bundle) {
        }

        public void onPartialResults(Bundle bundle) {
            handleResults(bundle, true);
        }

        public void onReadyForSpeech(Bundle bundle) {
            SpeechRecognition.this.mState = SpeechRecognition.STATE_AWAITING_SPEECH;
            SpeechRecognition.this.nativeOnAudioStart(SpeechRecognition.this.mNativeSpeechRecognizerImplAndroid);
        }

        public void onResults(Bundle bundle) {
            handleResults(bundle, false);
            SpeechRecognition.this.terminate(0);
        }

        public void onRmsChanged(float rms) {
        }

        private void handleResults(Bundle bundle, boolean provisional) {
            if (SpeechRecognition.this.mContinuous && provisional) {
                provisional = false;
            }
            ArrayList<String> list = bundle.getStringArrayList("results_recognition");
            SpeechRecognition.this.nativeOnRecognitionResults(SpeechRecognition.this.mNativeSpeechRecognizerImplAndroid, (String[]) list.toArray(new String[list.size()]), bundle.getFloatArray("confidence_scores"), provisional);
        }
    }

    private native void nativeOnAudioEnd(long j);

    private native void nativeOnAudioStart(long j);

    private native void nativeOnRecognitionEnd(long j);

    private native void nativeOnRecognitionError(long j, int i);

    private native void nativeOnRecognitionResults(long j, String[] strArr, float[] fArr, boolean z);

    private native void nativeOnSoundEnd(long j);

    private native void nativeOnSoundStart(long j);

    public static boolean initialize(Context context) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            return false;
        }
        for (ResolveInfo resolve : context.getPackageManager().queryIntentServices(new Intent("android.speech.RecognitionService"), 4)) {
            ServiceInfo service = resolve.serviceInfo;
            if (service.packageName.equals(PROVIDER_PACKAGE_NAME) && PackageUtils.getPackageVersion(context, service.packageName) >= PROVIDER_MIN_VERSION) {
                sRecognitionProvider = new ComponentName(service.packageName, service.name);
                return true;
            }
        }
        return false;
    }

    private SpeechRecognition(Context context, long nativeSpeechRecognizerImplAndroid) {
        this.mContext = context;
        this.mContinuous = false;
        this.mNativeSpeechRecognizerImplAndroid = nativeSpeechRecognizerImplAndroid;
        this.mListener = new Listener();
        this.mIntent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        if (sRecognitionProvider != null) {
            this.mRecognizer = SpeechRecognizer.createSpeechRecognizer(this.mContext, sRecognitionProvider);
        } else {
            this.mRecognizer = SpeechRecognizer.createSpeechRecognizer(this.mContext);
        }
        this.mRecognizer.setRecognitionListener(this.mListener);
    }

    private void terminate(int error) {
        if (this.mState != 0) {
            if (this.mState == STATE_CAPTURING_SPEECH) {
                nativeOnSoundEnd(this.mNativeSpeechRecognizerImplAndroid);
            }
            nativeOnAudioEnd(this.mNativeSpeechRecognizerImplAndroid);
            this.mState = 0;
        }
        if (error != 0) {
            nativeOnRecognitionError(this.mNativeSpeechRecognizerImplAndroid, error);
        }
        this.mRecognizer.destroy();
        this.mRecognizer = null;
        nativeOnRecognitionEnd(this.mNativeSpeechRecognizerImplAndroid);
        this.mNativeSpeechRecognizerImplAndroid = 0;
    }

    @CalledByNative
    private static SpeechRecognition createSpeechRecognition(Context context, long nativeSpeechRecognizerImplAndroid) {
        return new SpeechRecognition(context, nativeSpeechRecognizerImplAndroid);
    }

    @CalledByNative
    private void startRecognition(String language, boolean continuous, boolean interimResults) {
        if (this.mRecognizer != null) {
            this.mContinuous = continuous;
            this.mIntent.putExtra("android.speech.extra.DICTATION_MODE", continuous);
            this.mIntent.putExtra("android.speech.extra.LANGUAGE", language);
            this.mIntent.putExtra("android.speech.extra.PARTIAL_RESULTS", interimResults);
            this.mRecognizer.startListening(this.mIntent);
        }
    }

    @CalledByNative
    private void abortRecognition() {
        if (this.mRecognizer != null) {
            this.mRecognizer.cancel();
            terminate(STATE_CAPTURING_SPEECH);
        }
    }

    @CalledByNative
    private void stopRecognition() {
        if (this.mRecognizer != null) {
            this.mContinuous = false;
            this.mRecognizer.stopListening();
        }
    }
}
