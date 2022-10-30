package org.xwalk.core.internal.extension.api.device_capabilities;

import org.json.JSONObject;

class DeviceCapabilitiesCodecs {
    private XWalkMediaCodec mediaCodec;

    public DeviceCapabilitiesCodecs(DeviceCapabilities instance) {
        this.mediaCodec = XWalkMediaCodec.Create(instance);
    }

    public JSONObject getInfo() {
        return this.mediaCodec.getCodecsInfo();
    }
}
