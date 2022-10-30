package org.xwalk.core.internal.extension.api.messaging;

import android.app.Activity;
import java.util.HashMap;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.json.JSONObject;
import org.xwalk.core.internal.extension.XWalkExtensionWithActivityStateListener;

public class Messaging extends XWalkExtensionWithActivityStateListener {
    public static final String JS_API_PATH = "jsapi/messaging_api.js";
    private static final String NAME = "xwalk.experimental.messaging";
    private static HashMap<String, Command> sMethodMap;
    private boolean isIntentFiltersRegistered;
    private MessagingManager mMessagingManager;
    private MessagingSmsManager mSmsManager;

    /* renamed from: org.xwalk.core.internal.extension.api.messaging.Messaging.1 */
    class C06551 implements Command {
        C06551() {
        }

        public void runCommand(int instanceID, JSONObject jsonMsg) {
            Messaging.this.mSmsManager.onSmsSend(instanceID, jsonMsg);
        }
    }

    /* renamed from: org.xwalk.core.internal.extension.api.messaging.Messaging.2 */
    class C06562 implements Command {
        C06562() {
        }

        public void runCommand(int instanceID, JSONObject jsonMsg) {
            Messaging.this.mSmsManager.onSmsClear(instanceID, jsonMsg);
        }
    }

    /* renamed from: org.xwalk.core.internal.extension.api.messaging.Messaging.3 */
    class C06573 implements Command {
        C06573() {
        }

        public void runCommand(int instanceID, JSONObject jsonMsg) {
            Messaging.this.mSmsManager.onSmsSegmentInfo(instanceID, jsonMsg);
        }
    }

    /* renamed from: org.xwalk.core.internal.extension.api.messaging.Messaging.4 */
    class C06584 implements Command {
        C06584() {
        }

        public void runCommand(int instanceID, JSONObject jsonMsg) {
            Messaging.this.mMessagingManager.onMsgFindMessages(instanceID, jsonMsg);
        }
    }

    /* renamed from: org.xwalk.core.internal.extension.api.messaging.Messaging.5 */
    class C06595 implements Command {
        C06595() {
        }

        public void runCommand(int instanceID, JSONObject jsonMsg) {
            Messaging.this.mMessagingManager.onMsgGetMessage(instanceID, jsonMsg);
        }
    }

    /* renamed from: org.xwalk.core.internal.extension.api.messaging.Messaging.6 */
    class C06606 implements Command {
        C06606() {
        }

        public void runCommand(int instanceID, JSONObject jsonMsg) {
            Messaging.this.mMessagingManager.onMsgDeleteMessage(instanceID, jsonMsg);
        }
    }

    /* renamed from: org.xwalk.core.internal.extension.api.messaging.Messaging.7 */
    class C06617 implements Command {
        C06617() {
        }

        public void runCommand(int instanceID, JSONObject jsonMsg) {
            Messaging.this.mMessagingManager.onMsgDeleteConversation(instanceID, jsonMsg);
        }
    }

    /* renamed from: org.xwalk.core.internal.extension.api.messaging.Messaging.8 */
    class C06628 implements Command {
        C06628() {
        }

        public void runCommand(int instanceID, JSONObject jsonMsg) {
            Messaging.this.mMessagingManager.onMsgMarkMessageRead(instanceID, jsonMsg);
        }
    }

    /* renamed from: org.xwalk.core.internal.extension.api.messaging.Messaging.9 */
    class C06639 implements Command {
        C06639() {
        }

        public void runCommand(int instanceID, JSONObject jsonMsg) {
            Messaging.this.mMessagingManager.onMsgMarkConversationRead(instanceID, jsonMsg);
        }
    }

    static {
        sMethodMap = new HashMap();
    }

    private void initMethodMap() {
        sMethodMap.put("msg_smsSend", new C06551());
        sMethodMap.put("msg_smsClear", new C06562());
        sMethodMap.put("msg_smsSegmentInfo", new C06573());
        sMethodMap.put("msg_findMessages", new C06584());
        sMethodMap.put("msg_getMessage", new C06595());
        sMethodMap.put("msg_deleteMessage", new C06606());
        sMethodMap.put("msg_deleteConversation", new C06617());
        sMethodMap.put("msg_markMessageRead", new C06628());
        sMethodMap.put("msg_markConversationRead", new C06639());
    }

    private String getCommandString(String message) {
        if (message.isEmpty()) {
            return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        }
        try {
            return new JSONObject(message).getString("cmd");
        } catch (Exception e) {
            e.printStackTrace();
            return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        }
    }

    public Messaging(String jsApiContent, Activity activity) {
        super(NAME, jsApiContent, activity);
        this.isIntentFiltersRegistered = false;
        this.mSmsManager = new MessagingSmsManager(activity, this);
        this.mMessagingManager = new MessagingManager(activity, this);
        if (!this.isIntentFiltersRegistered) {
            this.mSmsManager.registerIntentFilters();
            this.isIntentFiltersRegistered = true;
        }
        initMethodMap();
    }

    public void onMessage(int instanceID, String message) {
        Command command = (Command) sMethodMap.get(getCommandString(message));
        if (command != null) {
            try {
                command.runCommand(instanceID, new JSONObject(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String onSyncMessage(int instanceID, String message) {
        if (getCommandString(message).equals("msg_smsServiceId")) {
            return this.mSmsManager.getServiceIds();
        }
        return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
    }

    public void onActivityStateChange(Activity activity, int newState) {
        if (newState == 5 && this.isIntentFiltersRegistered) {
            this.mSmsManager.unregisterIntentFilters();
            this.isIntentFiltersRegistered = false;
        } else if (newState == 2 && !this.isIntentFiltersRegistered) {
            this.mSmsManager.registerIntentFilters();
            this.isIntentFiltersRegistered = true;
        }
    }
}
