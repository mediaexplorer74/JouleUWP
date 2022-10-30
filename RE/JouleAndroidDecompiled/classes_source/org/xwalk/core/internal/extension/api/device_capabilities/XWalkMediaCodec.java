package org.xwalk.core.internal.extension.api.device_capabilities;

import android.os.Build.VERSION;
import java.util.Set;
import org.json.JSONObject;

abstract class XWalkMediaCodec {
    protected static final String[] AUDIO_CODEC_NAMES;
    private static final String TAG = "XWalkMediaCodec";
    protected static final String[] VIDEO_CODEC_NAMES;
    protected Set<AudioCodecElement> mAudioCodecsSet;
    protected DeviceCapabilities mDeviceCapabilities;
    protected Set<VideoCodecElement> mVideoCodecsSet;

    protected class AudioCodecElement {
        public String codecName;

        public AudioCodecElement(String name) {
            this.codecName = name;
        }

        public boolean equals(Object obj) {
            return this.codecName.equals(((AudioCodecElement) obj).codecName);
        }

        public int hashCode() {
            return this.codecName.hashCode();
        }
    }

    protected class VideoCodecElement {
        public String codecName;
        public boolean hwAccel;
        public boolean isEncoder;

        public VideoCodecElement(String name, boolean encoder, boolean hardware) {
            this.codecName = name;
            this.isEncoder = encoder;
            this.hwAccel = hardware;
        }

        public boolean equals(Object obj) {
            return this.codecName.equals(((VideoCodecElement) obj).codecName) && this.isEncoder == ((VideoCodecElement) obj).isEncoder && this.hwAccel == ((VideoCodecElement) obj).hwAccel;
        }

        public int hashCode() {
            return this.codecName.hashCode();
        }
    }

    public abstract JSONObject getCodecsInfo();

    XWalkMediaCodec() {
    }

    static {
        AUDIO_CODEC_NAMES = new String[]{"ALAC", "MP3", "AMRNB", "AMRWB", "AAC", "G711", "VORBIS", "WMA", "FLAC", "OPUS"};
        VIDEO_CODEC_NAMES = new String[]{"H263", "H264", "MPEG4", "AVC", "WMV", "VP8", "Theora"};
    }

    public static XWalkMediaCodec Create(DeviceCapabilities instance) {
        if (VERSION.SDK_INT >= 16) {
            return new MediaCodec(instance);
        }
        return new MediaCodecNull(instance);
    }
}
