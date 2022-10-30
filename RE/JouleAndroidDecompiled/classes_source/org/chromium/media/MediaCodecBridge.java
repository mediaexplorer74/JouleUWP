package org.chromium.media;

import android.annotation.TargetApi;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodec.CryptoException;
import android.media.MediaCodec.CryptoInfo;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.view.PointerIconCompat;
import android.view.Surface;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.Log;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;

@JNINamespace("media")
@TargetApi(16)
class MediaCodecBridge {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String KEY_CROP_BOTTOM = "crop-bottom";
    private static final String KEY_CROP_LEFT = "crop-left";
    private static final String KEY_CROP_RIGHT = "crop-right";
    private static final String KEY_CROP_TOP = "crop-top";
    private static final int MAX_ADAPTIVE_PLAYBACK_HEIGHT = 1080;
    private static final int MAX_ADAPTIVE_PLAYBACK_WIDTH = 1920;
    private static final long MAX_PRESENTATION_TIMESTAMP_SHIFT_US = 100000;
    private static final int MEDIA_CODEC_ABORT = 8;
    private static final int MEDIA_CODEC_DECODER = 0;
    private static final int MEDIA_CODEC_DEQUEUE_INPUT_AGAIN_LATER = 1;
    private static final int MEDIA_CODEC_DEQUEUE_OUTPUT_AGAIN_LATER = 2;
    private static final int MEDIA_CODEC_ENCODER = 1;
    private static final int MEDIA_CODEC_ERROR = 9;
    private static final int MEDIA_CODEC_INPUT_END_OF_STREAM = 5;
    private static final int MEDIA_CODEC_NO_KEY = 7;
    private static final int MEDIA_CODEC_OK = 0;
    private static final int MEDIA_CODEC_OUTPUT_BUFFERS_CHANGED = 3;
    private static final int MEDIA_CODEC_OUTPUT_END_OF_STREAM = 6;
    private static final int MEDIA_CODEC_OUTPUT_FORMAT_CHANGED = 4;
    private static final String TAG = "cr.media";
    private boolean mAdaptivePlaybackSupported;
    private AudioTrack mAudioTrack;
    private boolean mFlushed;
    private ByteBuffer[] mInputBuffers;
    private long mLastPresentationTimeUs;
    private MediaCodec mMediaCodec;
    private String mMime;
    private ByteBuffer[] mOutputBuffers;

    private static class CodecInfo {
        private final String mCodecName;
        private final String mCodecType;
        private final int mDirection;

        private CodecInfo(String codecType, String codecName, int direction) {
            this.mCodecType = codecType;
            this.mCodecName = codecName;
            this.mDirection = direction;
        }

        @CalledByNative("CodecInfo")
        private String codecType() {
            return this.mCodecType;
        }

        @CalledByNative("CodecInfo")
        private String codecName() {
            return this.mCodecName;
        }

        @CalledByNative("CodecInfo")
        private int direction() {
            return this.mDirection;
        }
    }

    private static class DequeueInputResult {
        private final int mIndex;
        private final int mStatus;

        private DequeueInputResult(int status, int index) {
            this.mStatus = status;
            this.mIndex = index;
        }

        @CalledByNative("DequeueInputResult")
        private int status() {
            return this.mStatus;
        }

        @CalledByNative("DequeueInputResult")
        private int index() {
            return this.mIndex;
        }
    }

    private static class DequeueOutputResult {
        private final int mFlags;
        private final int mIndex;
        private final int mNumBytes;
        private final int mOffset;
        private final long mPresentationTimeMicroseconds;
        private final int mStatus;

        private DequeueOutputResult(int status, int index, int flags, int offset, long presentationTimeMicroseconds, int numBytes) {
            this.mStatus = status;
            this.mIndex = index;
            this.mFlags = flags;
            this.mOffset = offset;
            this.mPresentationTimeMicroseconds = presentationTimeMicroseconds;
            this.mNumBytes = numBytes;
        }

        @CalledByNative("DequeueOutputResult")
        private int status() {
            return this.mStatus;
        }

        @CalledByNative("DequeueOutputResult")
        private int index() {
            return this.mIndex;
        }

        @CalledByNative("DequeueOutputResult")
        private int flags() {
            return this.mFlags;
        }

        @CalledByNative("DequeueOutputResult")
        private int offset() {
            return this.mOffset;
        }

        @CalledByNative("DequeueOutputResult")
        private long presentationTimeMicroseconds() {
            return this.mPresentationTimeMicroseconds;
        }

        @CalledByNative("DequeueOutputResult")
        private int numBytes() {
            return this.mNumBytes;
        }
    }

    static {
        $assertionsDisabled = !MediaCodecBridge.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    @CalledByNative
    private static CodecInfo[] getCodecsInfo() {
        Map<String, CodecInfo> encoderInfoMap = new HashMap();
        Map<String, CodecInfo> decoderInfoMap = new HashMap();
        int count = MediaCodecList.getCodecCount();
        for (int i = MEDIA_CODEC_OK; i < count; i += MEDIA_CODEC_ENCODER) {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            int direction = info.isEncoder() ? MEDIA_CODEC_ENCODER : MEDIA_CODEC_OK;
            String codecString = info.getName();
            String[] supportedTypes = info.getSupportedTypes();
            for (int j = MEDIA_CODEC_OK; j < supportedTypes.length; j += MEDIA_CODEC_ENCODER) {
                Map<String, CodecInfo> map;
                if (info.isEncoder()) {
                    map = encoderInfoMap;
                } else {
                    map = decoderInfoMap;
                }
                if (!map.containsKey(supportedTypes[j])) {
                    map.put(supportedTypes[j], new CodecInfo(codecString, direction, null));
                }
            }
        }
        ArrayList<CodecInfo> codecInfos = new ArrayList(decoderInfoMap.size() + encoderInfoMap.size());
        codecInfos.addAll(encoderInfoMap.values());
        codecInfos.addAll(decoderInfoMap.values());
        return (CodecInfo[]) codecInfos.toArray(new CodecInfo[codecInfos.size()]);
    }

    @TargetApi(18)
    @CalledByNative
    private static String getDefaultCodecName(String mime, int direction) {
        String codecName = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        if (VERSION.SDK_INT < 18) {
            return codecName;
        }
        MediaCodec mediaCodec;
        if (direction == MEDIA_CODEC_ENCODER) {
            try {
                mediaCodec = MediaCodec.createEncoderByType(mime);
            } catch (Exception e) {
                Object[] objArr = new Object[MEDIA_CODEC_OUTPUT_BUFFERS_CHANGED];
                objArr[MEDIA_CODEC_OK] = mime;
                objArr[MEDIA_CODEC_ENCODER] = Integer.valueOf(direction);
                objArr[MEDIA_CODEC_DEQUEUE_OUTPUT_AGAIN_LATER] = e;
                Log.m42w(TAG, "getDefaultCodecName: Failed to create MediaCodec: %s, direction: %d", objArr);
                return codecName;
            }
        }
        mediaCodec = MediaCodec.createDecoderByType(mime);
        codecName = mediaCodec.getName();
        mediaCodec.release();
        return codecName;
    }

    @TargetApi(21)
    @CalledByNative
    private static int[] getEncoderColorFormatsForMime(String mime) {
        MediaCodecInfo[] codecs;
        int i;
        if (VERSION.SDK_INT >= 21) {
            codecs = new MediaCodecList(MEDIA_CODEC_ENCODER).getCodecInfos();
        } else {
            int count = MediaCodecList.getCodecCount();
            if (count <= 0) {
                return null;
            }
            codecs = new MediaCodecInfo[count];
            for (i = MEDIA_CODEC_OK; i < count; i += MEDIA_CODEC_ENCODER) {
                codecs[i] = MediaCodecList.getCodecInfoAt(i);
            }
        }
        for (i = MEDIA_CODEC_OK; i < codecs.length; i += MEDIA_CODEC_ENCODER) {
            if (codecs[i].isEncoder()) {
                String[] supportedTypes = codecs[i].getSupportedTypes();
                for (int j = MEDIA_CODEC_OK; j < supportedTypes.length; j += MEDIA_CODEC_ENCODER) {
                    if (supportedTypes[j].equalsIgnoreCase(mime)) {
                        return codecs[i].getCapabilitiesForType(mime).colorFormats;
                    }
                }
                continue;
            }
        }
        return null;
    }

    private static String getDecoderNameForMime(String mime) {
        int count = MediaCodecList.getCodecCount();
        for (int i = MEDIA_CODEC_OK; i < count; i += MEDIA_CODEC_ENCODER) {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            if (!info.isEncoder()) {
                String[] supportedTypes = info.getSupportedTypes();
                for (int j = MEDIA_CODEC_OK; j < supportedTypes.length; j += MEDIA_CODEC_ENCODER) {
                    if (supportedTypes[j].equalsIgnoreCase(mime)) {
                        return info.getName();
                    }
                }
                continue;
            }
        }
        return null;
    }

    private MediaCodecBridge(MediaCodec mediaCodec, String mime, boolean adaptivePlaybackSupported) {
        if ($assertionsDisabled || mediaCodec != null) {
            this.mMediaCodec = mediaCodec;
            this.mMime = mime;
            this.mLastPresentationTimeUs = 0;
            this.mFlushed = true;
            this.mAdaptivePlaybackSupported = adaptivePlaybackSupported;
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private static MediaCodecBridge create(String mime, boolean isSecure, int direction) {
        if (isSecure && VERSION.SDK_INT < 18) {
            return null;
        }
        MediaCodec mediaCodec = null;
        boolean adaptivePlaybackSupported = $assertionsDisabled;
        try {
            if (mime.startsWith("video") && isSecure && direction == 0) {
                String decoderName = getDecoderNameForMime(mime);
                if (decoderName == null) {
                    return null;
                }
                if (VERSION.SDK_INT >= 19) {
                    MediaCodec insecureCodec = MediaCodec.createByCodecName(decoderName);
                    adaptivePlaybackSupported = codecSupportsAdaptivePlayback(insecureCodec, mime);
                    insecureCodec.release();
                }
                mediaCodec = MediaCodec.createByCodecName(decoderName + ".secure");
                if (mediaCodec == null) {
                    return null;
                }
                return new MediaCodecBridge(mediaCodec, mime, adaptivePlaybackSupported);
            }
            if (direction == MEDIA_CODEC_ENCODER) {
                mediaCodec = MediaCodec.createEncoderByType(mime);
            } else {
                mediaCodec = MediaCodec.createDecoderByType(mime);
                adaptivePlaybackSupported = codecSupportsAdaptivePlayback(mediaCodec, mime);
            }
            if (mediaCodec == null) {
                return new MediaCodecBridge(mediaCodec, mime, adaptivePlaybackSupported);
            }
            return null;
        } catch (Exception e) {
            Object[] objArr = new Object[MEDIA_CODEC_OUTPUT_FORMAT_CHANGED];
            objArr[MEDIA_CODEC_OK] = mime;
            objArr[MEDIA_CODEC_ENCODER] = Boolean.valueOf(isSecure);
            objArr[MEDIA_CODEC_DEQUEUE_OUTPUT_AGAIN_LATER] = Integer.valueOf(direction);
            objArr[MEDIA_CODEC_OUTPUT_BUFFERS_CHANGED] = e;
            Log.m32e(TAG, "Failed to create MediaCodec: %s, isSecure: %s, direction: %d", objArr);
        }
    }

    @CalledByNative
    private void release() {
        try {
            this.mMediaCodec.release();
        } catch (IllegalStateException e) {
            Object[] objArr = new Object[MEDIA_CODEC_ENCODER];
            objArr[MEDIA_CODEC_OK] = e;
            Log.m32e(TAG, "Cannot release media codec", objArr);
        }
        this.mMediaCodec = null;
        if (this.mAudioTrack != null) {
            this.mAudioTrack.release();
        }
    }

    @CalledByNative
    private boolean start() {
        try {
            this.mMediaCodec.start();
            if (VERSION.SDK_INT > 19) {
                return true;
            }
            this.mInputBuffers = this.mMediaCodec.getInputBuffers();
            this.mOutputBuffers = this.mMediaCodec.getOutputBuffers();
            return true;
        } catch (IllegalStateException e) {
            Object[] objArr = new Object[MEDIA_CODEC_ENCODER];
            objArr[MEDIA_CODEC_OK] = e;
            Log.m32e(TAG, "Cannot start the media codec", objArr);
            return $assertionsDisabled;
        }
    }

    @CalledByNative
    private DequeueInputResult dequeueInputBuffer(long timeoutUs) {
        int status = MEDIA_CODEC_ERROR;
        int index = -1;
        try {
            int indexOrStatus = this.mMediaCodec.dequeueInputBuffer(timeoutUs);
            if (indexOrStatus >= 0) {
                status = MEDIA_CODEC_OK;
                index = indexOrStatus;
            } else if (indexOrStatus == -1) {
                Log.m32e(TAG, "dequeueInputBuffer: MediaCodec.INFO_TRY_AGAIN_LATER", new Object[MEDIA_CODEC_OK]);
                status = MEDIA_CODEC_ENCODER;
            } else {
                Log.m32e(TAG, "Unexpected index_or_status: " + indexOrStatus, new Object[MEDIA_CODEC_OK]);
                if (!$assertionsDisabled) {
                    throw new AssertionError();
                }
            }
        } catch (Exception e) {
            Object[] objArr = new Object[MEDIA_CODEC_ENCODER];
            objArr[MEDIA_CODEC_OK] = e;
            Log.m32e(TAG, "Failed to dequeue input buffer", objArr);
        }
        return new DequeueInputResult(index, null);
    }

    @CalledByNative
    private int flush() {
        try {
            this.mFlushed = true;
            if (this.mAudioTrack != null) {
                this.mAudioTrack.pause();
                this.mAudioTrack.flush();
            }
            this.mMediaCodec.flush();
            return MEDIA_CODEC_OK;
        } catch (IllegalStateException e) {
            Object[] objArr = new Object[MEDIA_CODEC_ENCODER];
            objArr[MEDIA_CODEC_OK] = e;
            Log.m32e(TAG, "Failed to flush MediaCodec", objArr);
            return MEDIA_CODEC_ERROR;
        }
    }

    @CalledByNative
    private void stop() {
        this.mMediaCodec.stop();
        if (this.mAudioTrack != null) {
            this.mAudioTrack.pause();
        }
    }

    private boolean outputFormatHasCropValues(MediaFormat format) {
        return (format.containsKey(KEY_CROP_RIGHT) && format.containsKey(KEY_CROP_LEFT) && format.containsKey(KEY_CROP_BOTTOM) && format.containsKey(KEY_CROP_TOP)) ? true : $assertionsDisabled;
    }

    @CalledByNative
    private int getOutputHeight() {
        MediaFormat format = this.mMediaCodec.getOutputFormat();
        return outputFormatHasCropValues(format) ? (format.getInteger(KEY_CROP_BOTTOM) - format.getInteger(KEY_CROP_TOP)) + MEDIA_CODEC_ENCODER : format.getInteger("height");
    }

    @CalledByNative
    private int getOutputWidth() {
        MediaFormat format = this.mMediaCodec.getOutputFormat();
        return outputFormatHasCropValues(format) ? (format.getInteger(KEY_CROP_RIGHT) - format.getInteger(KEY_CROP_LEFT)) + MEDIA_CODEC_ENCODER : format.getInteger("width");
    }

    @CalledByNative
    private int getOutputSamplingRate() {
        return this.mMediaCodec.getOutputFormat().getInteger("sample-rate");
    }

    @CalledByNative
    private ByteBuffer getInputBuffer(int index) {
        if (VERSION.SDK_INT > 19) {
            return this.mMediaCodec.getInputBuffer(index);
        }
        return this.mInputBuffers[index];
    }

    @CalledByNative
    private ByteBuffer getOutputBuffer(int index) {
        if (VERSION.SDK_INT > 19) {
            return this.mMediaCodec.getOutputBuffer(index);
        }
        return this.mOutputBuffers[index];
    }

    @CalledByNative
    private int getOutputBuffersCount() {
        return this.mOutputBuffers != null ? this.mOutputBuffers.length : -1;
    }

    @CalledByNative
    private int getOutputBuffersCapacity() {
        return this.mOutputBuffers != null ? this.mOutputBuffers[MEDIA_CODEC_OK].capacity() : -1;
    }

    @CalledByNative
    private int queueInputBuffer(int index, int offset, int size, long presentationTimeUs, int flags) {
        resetLastPresentationTimeIfNeeded(presentationTimeUs);
        try {
            if (VERSION.SDK_INT > 19) {
                this.mMediaCodec.getInputBuffer(index);
            }
            this.mMediaCodec.queueInputBuffer(index, offset, size, presentationTimeUs, flags);
            return MEDIA_CODEC_OK;
        } catch (Exception e) {
            Object[] objArr = new Object[MEDIA_CODEC_ENCODER];
            objArr[MEDIA_CODEC_OK] = e;
            Log.m32e(TAG, "Failed to queue input buffer", objArr);
            return MEDIA_CODEC_ERROR;
        }
    }

    @TargetApi(19)
    @CalledByNative
    private void setVideoBitrate(int bps) {
        Bundle b = new Bundle();
        b.putInt("video-bitrate", bps);
        this.mMediaCodec.setParameters(b);
    }

    @TargetApi(19)
    @CalledByNative
    private void requestKeyFrameSoon() {
        Bundle b = new Bundle();
        b.putInt("request-sync", MEDIA_CODEC_OK);
        this.mMediaCodec.setParameters(b);
    }

    @CalledByNative
    private int queueSecureInputBuffer(int index, int offset, byte[] iv, byte[] keyId, int[] numBytesOfClearData, int[] numBytesOfEncryptedData, int numSubSamples, long presentationTimeUs) {
        Object[] objArr;
        resetLastPresentationTimeIfNeeded(presentationTimeUs);
        try {
            CryptoInfo cryptoInfo = new CryptoInfo();
            cryptoInfo.set(numSubSamples, numBytesOfClearData, numBytesOfEncryptedData, keyId, iv, MEDIA_CODEC_ENCODER);
            this.mMediaCodec.queueSecureInputBuffer(index, offset, cryptoInfo, presentationTimeUs, MEDIA_CODEC_OK);
            return MEDIA_CODEC_OK;
        } catch (CryptoException e) {
            objArr = new Object[MEDIA_CODEC_ENCODER];
            objArr[MEDIA_CODEC_OK] = e;
            Log.m32e(TAG, "Failed to queue secure input buffer", objArr);
            if (e.getErrorCode() == MEDIA_CODEC_ENCODER) {
                Log.m32e(TAG, "MediaCodec.CryptoException.ERROR_NO_KEY", new Object[MEDIA_CODEC_OK]);
                return MEDIA_CODEC_NO_KEY;
            }
            Log.m32e(TAG, "MediaCodec.CryptoException with error code " + e.getErrorCode(), new Object[MEDIA_CODEC_OK]);
            return MEDIA_CODEC_ERROR;
        } catch (IllegalStateException e2) {
            objArr = new Object[MEDIA_CODEC_ENCODER];
            objArr[MEDIA_CODEC_OK] = e2;
            Log.m32e(TAG, "Failed to queue secure input buffer", objArr);
            return MEDIA_CODEC_ERROR;
        }
    }

    @CalledByNative
    private void releaseOutputBuffer(int index, boolean render) {
        try {
            if (VERSION.SDK_INT > 19) {
                this.mMediaCodec.getOutputBuffer(index);
            }
            this.mMediaCodec.releaseOutputBuffer(index, render);
        } catch (IllegalStateException e) {
            Object[] objArr = new Object[MEDIA_CODEC_ENCODER];
            objArr[MEDIA_CODEC_OK] = e;
            Log.m32e(TAG, "Failed to release output buffer", objArr);
        }
    }

    @CalledByNative
    private DequeueOutputResult dequeueOutputBuffer(long timeoutUs) {
        BufferInfo info = new BufferInfo();
        int status = MEDIA_CODEC_ERROR;
        int index = -1;
        try {
            int indexOrStatus = this.mMediaCodec.dequeueOutputBuffer(info, timeoutUs);
            if (info.presentationTimeUs < this.mLastPresentationTimeUs) {
                info.presentationTimeUs = this.mLastPresentationTimeUs;
            }
            this.mLastPresentationTimeUs = info.presentationTimeUs;
            if (indexOrStatus >= 0) {
                status = MEDIA_CODEC_OK;
                index = indexOrStatus;
            } else if (indexOrStatus == -3) {
                this.mOutputBuffers = this.mMediaCodec.getOutputBuffers();
                status = MEDIA_CODEC_OUTPUT_BUFFERS_CHANGED;
            } else if (indexOrStatus == -2) {
                status = MEDIA_CODEC_OUTPUT_FORMAT_CHANGED;
                MediaFormat newFormat = this.mMediaCodec.getOutputFormat();
                if (this.mAudioTrack != null && newFormat.containsKey("sample-rate")) {
                    if (this.mAudioTrack.setPlaybackRate(newFormat.getInteger("sample-rate")) != 0) {
                        status = MEDIA_CODEC_ERROR;
                    }
                }
            } else if (indexOrStatus == -1) {
                status = MEDIA_CODEC_DEQUEUE_OUTPUT_AGAIN_LATER;
            } else {
                Log.m32e(TAG, "Unexpected index_or_status: " + indexOrStatus, new Object[MEDIA_CODEC_OK]);
                if (!$assertionsDisabled) {
                    throw new AssertionError();
                }
            }
        } catch (IllegalStateException e) {
            Object[] objArr = new Object[MEDIA_CODEC_ENCODER];
            objArr[MEDIA_CODEC_OK] = e;
            Log.m32e(TAG, "Failed to dequeue output buffer", objArr);
        }
        return new DequeueOutputResult(index, info.flags, info.offset, info.presentationTimeUs, info.size, null);
    }

    @CalledByNative
    private boolean configureVideo(MediaFormat format, Surface surface, MediaCrypto crypto, int flags) {
        try {
            if (this.mAdaptivePlaybackSupported) {
                format.setInteger("max-width", MAX_ADAPTIVE_PLAYBACK_WIDTH);
                format.setInteger("max-height", MAX_ADAPTIVE_PLAYBACK_HEIGHT);
            }
            this.mMediaCodec.configure(format, surface, crypto, flags);
            return true;
        } catch (IllegalStateException e) {
            Object[] objArr = new Object[MEDIA_CODEC_ENCODER];
            objArr[MEDIA_CODEC_OK] = e;
            Log.m32e(TAG, "Cannot configure the video codec", objArr);
            return $assertionsDisabled;
        }
    }

    @CalledByNative
    private static MediaFormat createAudioFormat(String mime, int sampleRate, int channelCount) {
        return MediaFormat.createAudioFormat(mime, sampleRate, channelCount);
    }

    @CalledByNative
    private static MediaFormat createVideoDecoderFormat(String mime, int width, int height) {
        return MediaFormat.createVideoFormat(mime, width, height);
    }

    @CalledByNative
    private static MediaFormat createVideoEncoderFormat(String mime, int width, int height, int bitRate, int frameRate, int iFrameInterval, int colorFormat) {
        MediaFormat format = MediaFormat.createVideoFormat(mime, width, height);
        format.setInteger("bitrate", bitRate);
        format.setInteger("frame-rate", frameRate);
        format.setInteger("i-frame-interval", iFrameInterval);
        format.setInteger("color-format", colorFormat);
        return format;
    }

    @CalledByNative
    private boolean isAdaptivePlaybackSupported(int width, int height) {
        if (this.mAdaptivePlaybackSupported && width <= MAX_ADAPTIVE_PLAYBACK_WIDTH && height <= MAX_ADAPTIVE_PLAYBACK_HEIGHT) {
            return true;
        }
        return $assertionsDisabled;
    }

    @TargetApi(19)
    private static boolean codecSupportsAdaptivePlayback(MediaCodec mediaCodec, String mime) {
        boolean z = true;
        if (VERSION.SDK_INT < 19 || mediaCodec == null) {
            return $assertionsDisabled;
        }
        try {
            MediaCodecInfo info = mediaCodec.getCodecInfo();
            if (info.isEncoder()) {
                return $assertionsDisabled;
            }
            CodecCapabilities capabilities = info.getCapabilitiesForType(mime);
            if (capabilities == null || !capabilities.isFeatureSupported("adaptive-playback")) {
                z = MEDIA_CODEC_OK;
            }
            return z;
        } catch (IllegalArgumentException e) {
            Object[] objArr = new Object[MEDIA_CODEC_ENCODER];
            objArr[MEDIA_CODEC_OK] = e;
            Log.m32e(TAG, "Cannot retrieve codec information", objArr);
            return $assertionsDisabled;
        }
    }

    @CalledByNative
    private static void setCodecSpecificData(MediaFormat format, int index, byte[] bytes) {
        String name;
        switch (index) {
            case MEDIA_CODEC_OK /*0*/:
                name = "csd-0";
                break;
            case MEDIA_CODEC_ENCODER /*1*/:
                name = "csd-1";
                break;
            case MEDIA_CODEC_DEQUEUE_OUTPUT_AGAIN_LATER /*2*/:
                name = "csd-2";
                break;
            default:
                name = null;
                break;
        }
        if (name != null) {
            format.setByteBuffer(name, ByteBuffer.wrap(bytes));
        }
    }

    @CalledByNative
    private static void setFrameHasADTSHeader(MediaFormat format) {
        format.setInteger("is-adts", MEDIA_CODEC_ENCODER);
    }

    @CalledByNative
    private boolean configureAudio(MediaFormat format, MediaCrypto crypto, int flags, boolean playAudio) {
        try {
            this.mMediaCodec.configure(format, null, crypto, flags);
            if (playAudio) {
                int sampleRate = format.getInteger("sample-rate");
                int channelConfig = getAudioFormat(format.getInteger("channel-count"));
                this.mAudioTrack = new AudioTrack(MEDIA_CODEC_OUTPUT_BUFFERS_CHANGED, sampleRate, channelConfig, MEDIA_CODEC_DEQUEUE_OUTPUT_AGAIN_LATER, AudioTrack.getMinBufferSize(sampleRate, channelConfig, MEDIA_CODEC_DEQUEUE_OUTPUT_AGAIN_LATER), MEDIA_CODEC_ENCODER);
                if (this.mAudioTrack.getState() == 0) {
                    this.mAudioTrack = null;
                    return $assertionsDisabled;
                }
            }
            return true;
        } catch (IllegalStateException e) {
            Object[] objArr = new Object[MEDIA_CODEC_ENCODER];
            objArr[MEDIA_CODEC_OK] = e;
            Log.m32e(TAG, "Cannot configure the audio codec", objArr);
            return $assertionsDisabled;
        }
    }

    @CalledByNative
    private long playOutputBuffer(byte[] buf) {
        if (this.mAudioTrack == null) {
            return 0;
        }
        if (MEDIA_CODEC_OUTPUT_BUFFERS_CHANGED != this.mAudioTrack.getPlayState()) {
            this.mAudioTrack.play();
        }
        int size = this.mAudioTrack.write(buf, MEDIA_CODEC_OK, buf.length);
        if (buf.length != size) {
            Log.m33i(TAG, "Failed to send all data to audio output, expected size: " + buf.length + ", actual size: " + size, new Object[MEDIA_CODEC_OK]);
        }
        return (long) this.mAudioTrack.getPlaybackHeadPosition();
    }

    @CalledByNative
    private void setVolume(double volume) {
        if (this.mAudioTrack != null) {
            this.mAudioTrack.setStereoVolume((float) volume, (float) volume);
        }
    }

    private void resetLastPresentationTimeIfNeeded(long presentationTimeUs) {
        if (this.mFlushed) {
            this.mLastPresentationTimeUs = Math.max(presentationTimeUs - MAX_PRESENTATION_TIMESTAMP_SHIFT_US, 0);
            this.mFlushed = $assertionsDisabled;
        }
    }

    private int getAudioFormat(int channelCount) {
        switch (channelCount) {
            case MEDIA_CODEC_ENCODER /*1*/:
                return MEDIA_CODEC_OUTPUT_FORMAT_CHANGED;
            case MEDIA_CODEC_DEQUEUE_OUTPUT_AGAIN_LATER /*2*/:
                return 12;
            case MEDIA_CODEC_OUTPUT_FORMAT_CHANGED /*4*/:
                return 204;
            case MEDIA_CODEC_OUTPUT_END_OF_STREAM /*6*/:
                return 252;
            case MEDIA_CODEC_ABORT /*8*/:
                return PointerIconCompat.STYLE_GRAB;
            default:
                return MEDIA_CODEC_ENCODER;
        }
    }
}
