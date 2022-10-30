package org.xwalk.core.internal;

import org.chromium.base.ThreadUtils;

@XWalkAPI(createInternally = true, impl = XWalkJavascriptResultInternal.class)
public class XWalkJavascriptResultHandlerInternal implements XWalkJavascriptResultInternal {
    private XWalkContentsClientBridge mBridge;
    private final int mId;

    /* renamed from: org.xwalk.core.internal.XWalkJavascriptResultHandlerInternal.1 */
    class C04561 implements Runnable {
        final /* synthetic */ String val$promptResult;

        C04561(String str) {
            this.val$promptResult = str;
        }

        public void run() {
            if (XWalkJavascriptResultHandlerInternal.this.mBridge != null) {
                XWalkJavascriptResultHandlerInternal.this.mBridge.confirmJsResult(XWalkJavascriptResultHandlerInternal.this.mId, this.val$promptResult);
            }
            XWalkJavascriptResultHandlerInternal.this.mBridge = null;
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkJavascriptResultHandlerInternal.2 */
    class C04572 implements Runnable {
        C04572() {
        }

        public void run() {
            if (XWalkJavascriptResultHandlerInternal.this.mBridge != null) {
                XWalkJavascriptResultHandlerInternal.this.mBridge.cancelJsResult(XWalkJavascriptResultHandlerInternal.this.mId);
            }
            XWalkJavascriptResultHandlerInternal.this.mBridge = null;
        }
    }

    XWalkJavascriptResultHandlerInternal(XWalkContentsClientBridge bridge, int id) {
        this.mBridge = bridge;
        this.mId = id;
    }

    XWalkJavascriptResultHandlerInternal() {
        this.mBridge = null;
        this.mId = -1;
    }

    @XWalkAPI
    public void confirm() {
        confirmWithResult(null);
    }

    @XWalkAPI
    public void confirmWithResult(String promptResult) {
        ThreadUtils.runOnUiThread(new C04561(promptResult));
    }

    @XWalkAPI
    public void cancel() {
        ThreadUtils.runOnUiThread(new C04572());
    }
}
