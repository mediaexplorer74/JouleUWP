package org.xwalk.core.internal.extension.api.device_capabilities;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class MediaCodec extends XWalkMediaCodec {
    public MediaCodec(DeviceCapabilities instance) {
        this.mDeviceCapabilities = instance;
        this.mAudioCodecsSet = new HashSet();
        this.mVideoCodecsSet = new HashSet();
        getCodecsList();
    }

    public JSONObject getCodecsInfo() {
        JSONObject outputObject = new JSONObject();
        JSONArray audioCodecsArray = new JSONArray();
        JSONArray videoCodecsArray = new JSONArray();
        try {
            JSONObject codecsObject;
            for (AudioCodecElement codecToAdd : this.mAudioCodecsSet) {
                codecsObject = new JSONObject();
                codecsObject.put("format", codecToAdd.codecName);
                audioCodecsArray.put(codecsObject);
            }
            for (VideoCodecElement codecToAdd2 : this.mVideoCodecsSet) {
                codecsObject = new JSONObject();
                codecsObject.put("format", codecToAdd2.codecName);
                codecsObject.put("encode", codecToAdd2.isEncoder);
                codecsObject.put("hwAccel", codecToAdd2.hwAccel);
                videoCodecsArray.put(codecsObject);
            }
            outputObject.put("audioCodecs", audioCodecsArray);
            outputObject.put("videoCodecs", videoCodecsArray);
            return outputObject;
        } catch (JSONException e) {
            return this.mDeviceCapabilities.setErrorMessage(e.toString());
        }
    }

    public void getCodecsList() {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            String name = codecInfo.getName().toUpperCase();
            boolean hasAdded = false;
            for (String nameListElement : AUDIO_CODEC_NAMES) {
                if (name.contains(nameListElement)) {
                    this.mAudioCodecsSet.add(new AudioCodecElement(nameListElement));
                    hasAdded = true;
                    break;
                }
            }
            if (!hasAdded) {
                for (String nameListElement2 : VIDEO_CODEC_NAMES) {
                    if (name.contains(nameListElement2)) {
                        this.mVideoCodecsSet.add(new VideoCodecElement(nameListElement2, codecInfo.isEncoder(), false));
                        break;
                    }
                }
            }
        }
    }
}
