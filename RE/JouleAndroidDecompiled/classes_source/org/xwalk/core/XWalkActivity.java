package org.xwalk.core;

import android.app.Activity;
import android.os.Bundle;

public abstract class XWalkActivity extends Activity {
    private XWalkActivityDelegate mActivityDelegate;

    /* renamed from: org.xwalk.core.XWalkActivity.1 */
    class C04311 implements Runnable {
        C04311() {
        }

        public void run() {
            XWalkActivity.this.finish();
        }
    }

    /* renamed from: org.xwalk.core.XWalkActivity.2 */
    class C04322 implements Runnable {
        C04322() {
        }

        public void run() {
            XWalkActivity.this.onXWalkReady();
        }
    }

    protected abstract void onXWalkReady();

    public boolean isXWalkReady() {
        return this.mActivityDelegate.isXWalkReady();
    }

    public boolean isSharedMode() {
        return this.mActivityDelegate.isSharedMode();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mActivityDelegate = new XWalkActivityDelegate(this, new C04311(), new C04322());
    }

    protected void onResume() {
        super.onResume();
        this.mActivityDelegate.onResume();
    }
}
