package org.xwalk.core.internal.extension.api.device_capabilities;

import org.json.JSONObject;

class MediaCodecNull extends XWalkMediaCodec {
    public MediaCodecNull(DeviceCapabilities instance) {
    }

    public JSONObject getCodecsInfo() {
        return new JSONObject();
    }
}
