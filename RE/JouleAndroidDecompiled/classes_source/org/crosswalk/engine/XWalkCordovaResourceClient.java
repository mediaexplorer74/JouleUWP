package org.crosswalk.engine;

import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.http.SslError;
import android.support.v4.media.TransportMediator;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import java.io.FileNotFoundException;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.CordovaResourceApi.OpenForReadResult;
import org.apache.cordova.LOG;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;

public class XWalkCordovaResourceClient extends XWalkResourceClient {
    private static final String TAG = "XWalkCordovaResourceClient";
    protected XWalkWebViewEngine parentEngine;

    public XWalkCordovaResourceClient(XWalkWebViewEngine parentEngine) {
        super(parentEngine.webView);
        this.parentEngine = parentEngine;
    }

    public void onReceivedLoadError(XWalkView view, int errorCode, String description, String failingUrl) {
        LOG.m11d(TAG, "CordovaWebViewClient.onReceivedError: Error code=%s Description=%s URL=%s", Integer.valueOf(errorCode), description, failingUrl);
        this.parentEngine.client.onReceivedError(errorCode, description, failingUrl);
    }

    public WebResourceResponse shouldInterceptLoadRequest(XWalkView view, String url) {
        try {
            if (this.parentEngine.pluginManager.shouldAllowRequest(url)) {
                CordovaResourceApi resourceApi = this.parentEngine.resourceApi;
                Uri origUri = Uri.parse(url);
                Uri remappedUri = resourceApi.remapUri(origUri);
                if (origUri.equals(remappedUri)) {
                    return null;
                }
                OpenForReadResult result = resourceApi.openForRead(remappedUri, true);
                return new WebResourceResponse(result.mimeType, "UTF-8", result.inputStream);
            }
            LOG.m21w(TAG, "URL blocked by whitelist: " + url);
            return new WebResourceResponse("text/plain", "UTF-8", null);
        } catch (Throwable e) {
            if (!(e instanceof FileNotFoundException)) {
                LOG.m13e(TAG, "Error occurred while loading a file (returning a 404).", e);
            }
            return new WebResourceResponse("text/plain", "UTF-8", null);
        }
    }

    public boolean shouldOverrideUrlLoading(XWalkView view, String url) {
        return this.parentEngine.client.onNavigationAttempt(url);
    }

    public void onReceivedSslError(XWalkView view, ValueCallback<Boolean> callback, SslError error) {
        try {
            if ((this.parentEngine.cordova.getActivity().getPackageManager().getApplicationInfo(this.parentEngine.cordova.getActivity().getPackageName(), TransportMediator.FLAG_KEY_MEDIA_NEXT).flags & 2) != 0) {
                callback.onReceiveValue(Boolean.valueOf(true));
            } else {
                callback.onReceiveValue(Boolean.valueOf(false));
            }
        } catch (NameNotFoundException e) {
            callback.onReceiveValue(Boolean.valueOf(false));
        }
    }
}
