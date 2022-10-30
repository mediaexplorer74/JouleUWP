package org.crosswalk.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v4.media.TransportMediator;
import android.util.AttributeSet;
import android.view.KeyEvent;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine.EngineView;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.internal.XWalkPreferencesInternal;

public class XWalkCordovaView extends XWalkView implements EngineView {
    private static boolean hasSetStaticPref;
    protected XWalkWebViewEngine parentEngine;
    protected XWalkCordovaResourceClient resourceClient;
    protected XWalkCordovaUiClient uiClient;

    private static Context setGlobalPrefs(Context context, CordovaPreferences preferences) {
        boolean z = false;
        if (!hasSetStaticPref) {
            hasSetStaticPref = true;
            try {
                ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getApplicationContext().getPackageName(), TransportMediator.FLAG_KEY_MEDIA_NEXT);
                boolean prefAnimatable = preferences == null ? false : preferences.getBoolean("CrosswalkAnimatable", false);
                boolean manifestAnimatable = ai.metaData == null ? false : ai.metaData.getBoolean("CrosswalkAnimatable");
                String str = XWalkPreferencesInternal.ANIMATABLE_XWALK_VIEW;
                if (prefAnimatable || manifestAnimatable) {
                    z = true;
                }
                XWalkPreferences.setValue(str, z);
                if ((ai.flags & 2) != 0) {
                    XWalkPreferences.setValue(XWalkPreferencesInternal.REMOTE_DEBUGGING, true);
                }
                XWalkPreferences.setValue(XWalkPreferencesInternal.JAVASCRIPT_CAN_OPEN_WINDOW, true);
                XWalkPreferences.setValue(XWalkPreferencesInternal.ALLOW_UNIVERSAL_ACCESS_FROM_FILE, true);
            } catch (NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return context;
    }

    public XWalkCordovaView(Context context, CordovaPreferences preferences) {
        super(setGlobalPrefs(context, preferences), (AttributeSet) null);
    }

    public XWalkCordovaView(Context context, AttributeSet attrs) {
        super(setGlobalPrefs(context, null), attrs);
    }

    void init(XWalkWebViewEngine parentEngine) {
        this.parentEngine = parentEngine;
        if (this.resourceClient == null) {
            setResourceClient(new XWalkCordovaResourceClient(parentEngine));
        }
        if (this.uiClient == null) {
            setUIClient(new XWalkCordovaUiClient(parentEngine));
        }
    }

    public void setResourceClient(XWalkResourceClient client) {
        if (client instanceof XWalkCordovaResourceClient) {
            this.resourceClient = (XWalkCordovaResourceClient) client;
        }
        super.setResourceClient(client);
    }

    public void setUIClient(XWalkUIClient client) {
        if (client instanceof XWalkCordovaUiClient) {
            this.uiClient = (XWalkCordovaUiClient) client;
        }
        super.setUIClient(client);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        Boolean ret = this.parentEngine.client.onDispatchKeyEvent(event);
        if (ret != null) {
            return ret.booleanValue();
        }
        return super.dispatchKeyEvent(event);
    }

    public void pauseTimers() {
    }

    public void pauseTimersForReal() {
        super.pauseTimers();
    }

    public CordovaWebView getCordovaWebView() {
        return this.parentEngine == null ? null : this.parentEngine.getCordovaWebView();
    }

    public void setBackgroundColor(int color) {
        if (this.parentEngine != null && this.parentEngine.isXWalkReady()) {
            super.setBackgroundColor(color);
        }
    }
}
