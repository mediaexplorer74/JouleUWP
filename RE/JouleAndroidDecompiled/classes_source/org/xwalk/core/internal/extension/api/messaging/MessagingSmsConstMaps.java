package org.xwalk.core.internal.extension.api.messaging;

import android.support.v4.media.TransportMediator;
import com.adobe.phonegap.push.PushConstants;
import java.util.HashMap;

public class MessagingSmsConstMaps {
    public static final HashMap<String, Integer> smsDeliveryStatusDictS2I;
    public static final HashMap<Integer, String> smsDiliveryStatusDictI2S;
    public static final HashMap<Integer, String> smsStateDictI2S;
    public static final HashMap<String, Integer> smsStateDictS2I;
    public static final HashMap<String, String> smsTableColumnDict;
    public static final HashMap<String, String> sortOrderDict;

    static {
        smsTableColumnDict = new HashMap();
        smsDeliveryStatusDictS2I = new HashMap();
        smsDiliveryStatusDictI2S = new HashMap();
        smsStateDictI2S = new HashMap();
        smsStateDictS2I = new HashMap();
        sortOrderDict = new HashMap();
        smsTableColumnDict.put("id", MessagingSmsConsts.ID);
        smsTableColumnDict.put(MessagingSmsConsts.DATE, MessagingSmsConsts.DATE);
        smsTableColumnDict.put(PushConstants.FROM, MessagingSmsConsts.ADDRESS);
        smsTableColumnDict.put("state", MessagingSmsConsts.STATUS);
        smsTableColumnDict.put("error", MessagingSmsConsts.READ);
        smsDeliveryStatusDictS2I.put("success", Integer.valueOf(-1));
        smsDeliveryStatusDictS2I.put("pending", Integer.valueOf(64));
        smsDeliveryStatusDictS2I.put("success", Integer.valueOf(0));
        smsDeliveryStatusDictS2I.put("error", Integer.valueOf(TransportMediator.FLAG_KEY_MEDIA_NEXT));
        smsDiliveryStatusDictI2S.put(Integer.valueOf(-1), "success");
        smsDiliveryStatusDictI2S.put(Integer.valueOf(64), "pending");
        smsDiliveryStatusDictI2S.put(Integer.valueOf(0), "success");
        smsDiliveryStatusDictI2S.put(Integer.valueOf(TransportMediator.FLAG_KEY_MEDIA_NEXT), "error");
        smsStateDictI2S.put(Integer.valueOf(1), "received");
        smsStateDictI2S.put(Integer.valueOf(3), "draft");
        smsStateDictI2S.put(Integer.valueOf(4), "sending");
        smsStateDictI2S.put(Integer.valueOf(6), "sending");
        smsStateDictI2S.put(Integer.valueOf(2), "sent");
        smsStateDictI2S.put(Integer.valueOf(5), "failed");
        smsStateDictS2I.put("received", Integer.valueOf(1));
        smsStateDictS2I.put("draft", Integer.valueOf(3));
        smsStateDictS2I.put("sending", Integer.valueOf(4));
        smsStateDictS2I.put("sent", Integer.valueOf(2));
        smsStateDictS2I.put("failed", Integer.valueOf(5));
        sortOrderDict.put("ascending", "ASC");
        sortOrderDict.put("descending", "DESC");
    }
}
