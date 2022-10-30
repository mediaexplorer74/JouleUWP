package org.xwalk.core.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

class XWalkContentsClientCallbackHelper {
    private static final int MSG_ON_DOWNLOAD_START = 3;
    private static final int MSG_ON_LOAD_RESOURCE = 1;
    private static final int MSG_ON_PAGE_FINISHED = 7;
    private static final int MSG_ON_PAGE_STARTED = 2;
    private static final int MSG_ON_RECEIVED_ERROR = 5;
    private static final int MSG_ON_RECEIVED_LOGIN_REQUEST = 4;
    private static final int MSG_ON_RESOURCE_LOAD_STARTED = 6;
    private final XWalkContentsClient mContentsClient;
    private final Handler mHandler;

    /* renamed from: org.xwalk.core.internal.XWalkContentsClientCallbackHelper.1 */
    class C04531 extends Handler {
        C04531(Looper x0) {
            super(x0);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case XWalkContentsClientCallbackHelper.MSG_ON_LOAD_RESOURCE /*1*/:
                    XWalkContentsClientCallbackHelper.this.mContentsClient.onLoadResource(msg.obj);
                case XWalkContentsClientCallbackHelper.MSG_ON_PAGE_STARTED /*2*/:
                    XWalkContentsClientCallbackHelper.this.mContentsClient.onPageStarted((String) msg.obj);
                case XWalkContentsClientCallbackHelper.MSG_ON_DOWNLOAD_START /*3*/:
                    DownloadInfo info = msg.obj;
                    XWalkContentsClientCallbackHelper.this.mContentsClient.onDownloadStart(info.mUrl, info.mUserAgent, info.mContentDisposition, info.mMimeType, info.mContentLength);
                case XWalkContentsClientCallbackHelper.MSG_ON_RECEIVED_LOGIN_REQUEST /*4*/:
                    LoginRequestInfo info2 = msg.obj;
                    XWalkContentsClientCallbackHelper.this.mContentsClient.onReceivedLoginRequest(info2.mRealm, info2.mAccount, info2.mArgs);
                case XWalkContentsClientCallbackHelper.MSG_ON_RECEIVED_ERROR /*5*/:
                    OnReceivedErrorInfo info3 = msg.obj;
                    XWalkContentsClientCallbackHelper.this.mContentsClient.onReceivedError(info3.mErrorCode, info3.mDescription, info3.mFailingUrl);
                case XWalkContentsClientCallbackHelper.MSG_ON_RESOURCE_LOAD_STARTED /*6*/:
                    XWalkContentsClientCallbackHelper.this.mContentsClient.onResourceLoadStarted((String) msg.obj);
                case XWalkContentsClientCallbackHelper.MSG_ON_PAGE_FINISHED /*7*/:
                    XWalkContentsClientCallbackHelper.this.mContentsClient.onPageFinished((String) msg.obj);
                default:
                    throw new IllegalStateException("XWalkContentsClientCallbackHelper: unhandled message " + msg.what);
            }
        }
    }

    private static class DownloadInfo {
        final String mContentDisposition;
        final long mContentLength;
        final String mMimeType;
        final String mUrl;
        final String mUserAgent;

        DownloadInfo(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
            this.mUrl = url;
            this.mUserAgent = userAgent;
            this.mContentDisposition = contentDisposition;
            this.mMimeType = mimeType;
            this.mContentLength = contentLength;
        }
    }

    private static class LoginRequestInfo {
        final String mAccount;
        final String mArgs;
        final String mRealm;

        LoginRequestInfo(String realm, String account, String args) {
            this.mRealm = realm;
            this.mAccount = account;
            this.mArgs = args;
        }
    }

    private static class OnReceivedErrorInfo {
        final String mDescription;
        final int mErrorCode;
        final String mFailingUrl;

        OnReceivedErrorInfo(int errorCode, String description, String failingUrl) {
            this.mErrorCode = errorCode;
            this.mDescription = description;
            this.mFailingUrl = failingUrl;
        }
    }

    public XWalkContentsClientCallbackHelper(XWalkContentsClient contentsClient) {
        this.mHandler = new C04531(Looper.getMainLooper());
        this.mContentsClient = contentsClient;
    }

    public void postOnLoadResource(String url) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(MSG_ON_LOAD_RESOURCE, url));
    }

    public void postOnPageStarted(String url) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(MSG_ON_PAGE_STARTED, url));
    }

    public void postOnDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(MSG_ON_DOWNLOAD_START, new DownloadInfo(url, userAgent, contentDisposition, mimeType, contentLength)));
    }

    public void postOnReceivedLoginRequest(String realm, String account, String args) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(MSG_ON_RECEIVED_LOGIN_REQUEST, new LoginRequestInfo(realm, account, args)));
    }

    public void postOnReceivedError(int errorCode, String description, String failingUrl) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(MSG_ON_RECEIVED_ERROR, new OnReceivedErrorInfo(errorCode, description, failingUrl)));
    }

    public void postOnResourceLoadStarted(String url) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(MSG_ON_RESOURCE_LOAD_STARTED, url));
    }

    public void postOnPageFinished(String url) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(MSG_ON_PAGE_FINISHED, url));
    }
}
