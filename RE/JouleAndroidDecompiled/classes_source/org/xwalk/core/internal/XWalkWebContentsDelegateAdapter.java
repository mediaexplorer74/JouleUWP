package org.xwalk.core.internal;

import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ConsoleMessage;
import android.webkit.ConsoleMessage.MessageLevel;
import com.google.android.gms.common.ConnectionResult;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.content.browser.ContentVideoView;
import org.chromium.content.browser.ContentViewCore;

class XWalkWebContentsDelegateAdapter extends XWalkWebContentsDelegate {
    private static final String TAG;
    private XWalkContentsClient mXWalkContentsClient;

    static {
        TAG = XWalkWebContentsDelegateAdapter.class.getName();
    }

    public XWalkWebContentsDelegateAdapter(XWalkContentsClient client) {
        this.mXWalkContentsClient = client;
    }

    public boolean shouldCreateWebContents(String contentUrl) {
        if (this.mXWalkContentsClient != null) {
            return this.mXWalkContentsClient.shouldCreateWebContents(contentUrl);
        }
        return super.shouldCreateWebContents(contentUrl);
    }

    public void onLoadProgressChanged(int progress) {
        if (this.mXWalkContentsClient != null) {
            this.mXWalkContentsClient.onProgressChanged(progress);
        }
    }

    public boolean addNewContents(boolean isDialog, boolean isUserGesture) {
        return this.mXWalkContentsClient.onCreateWindow(isDialog, isUserGesture);
    }

    public void closeContents() {
        if (this.mXWalkContentsClient != null) {
            this.mXWalkContentsClient.onCloseWindow();
        }
    }

    public void activateContents() {
        if (this.mXWalkContentsClient != null) {
            this.mXWalkContentsClient.onRequestFocus();
        }
    }

    public void rendererUnresponsive() {
        if (this.mXWalkContentsClient != null) {
            this.mXWalkContentsClient.onRendererUnresponsive();
        }
    }

    public void rendererResponsive() {
        if (this.mXWalkContentsClient != null) {
            this.mXWalkContentsClient.onRendererResponsive();
        }
    }

    public void handleKeyboardEvent(KeyEvent event) {
        if (this.mXWalkContentsClient != null) {
            this.mXWalkContentsClient.onUnhandledKeyEvent(event);
        }
    }

    public boolean addMessageToConsole(int level, String message, int lineNumber, String sourceId) {
        if (this.mXWalkContentsClient == null) {
            return false;
        }
        MessageLevel messageLevel = MessageLevel.DEBUG;
        switch (level) {
            case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                messageLevel = MessageLevel.TIP;
                break;
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                messageLevel = MessageLevel.LOG;
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                messageLevel = MessageLevel.WARNING;
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                messageLevel = MessageLevel.ERROR;
                break;
            default:
                Log.w(TAG, "Unknown message level, defaulting to DEBUG");
                break;
        }
        return this.mXWalkContentsClient.onConsoleMessage(new ConsoleMessage(message, sourceId, lineNumber, messageLevel));
    }

    public void toggleFullscreen(boolean enterFullscreen) {
        if (!enterFullscreen) {
            ContentVideoView videoView = ContentVideoView.getContentVideoView();
            if (videoView != null) {
                videoView.exitFullscreen(false);
            }
        }
        if (this.mXWalkContentsClient != null) {
            this.mXWalkContentsClient.onToggleFullscreen(enterFullscreen);
        }
    }

    public boolean isFullscreen() {
        if (this.mXWalkContentsClient != null) {
            return this.mXWalkContentsClient.hasEnteredFullscreen();
        }
        return false;
    }

    public boolean shouldOverrideRunFileChooser(int processId, int renderId, int mode, String acceptTypes, boolean capture) {
        if (this.mXWalkContentsClient != null) {
            return this.mXWalkContentsClient.shouldOverrideRunFileChooser(processId, renderId, mode, acceptTypes, capture);
        }
        return false;
    }
}
