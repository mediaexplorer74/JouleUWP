package org.chromium.content.browser;

import android.content.Context;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.content.browser.DownloadInfo.Builder;
import org.chromium.ui.base.WindowAndroid.FileAccessCallback;

@JNINamespace("content")
public class DownloadController {
    private static final String LOGTAG = "DownloadController";
    private static DownloadNotificationService sDownloadNotificationService;
    private static final DownloadController sInstance;

    public interface DownloadNotificationService {
        void onDownloadCompleted(DownloadInfo downloadInfo);

        void onDownloadUpdated(DownloadInfo downloadInfo);
    }

    /* renamed from: org.chromium.content.browser.DownloadController.1 */
    class C06091 implements FileAccessCallback {
        final /* synthetic */ long val$callbackId;

        C06091(long j) {
            this.val$callbackId = j;
        }

        public void onFileAccessResult(boolean granted) {
            DownloadController.this.nativeOnRequestFileAccessResult(this.val$callbackId, granted);
        }
    }

    private native void nativeInit();

    private native void nativeOnRequestFileAccessResult(long j, boolean z);

    static {
        sInstance = new DownloadController();
    }

    @CalledByNative
    public static DownloadController getInstance() {
        return sInstance;
    }

    private DownloadController() {
        nativeInit();
    }

    private static ContentViewDownloadDelegate downloadDelegateFromView(ContentViewCore view) {
        return view.getDownloadDelegate();
    }

    public static void setDownloadNotificationService(DownloadNotificationService service) {
        sDownloadNotificationService = service;
    }

    @CalledByNative
    public void newHttpGetDownload(ContentViewCore view, String url, String userAgent, String contentDisposition, String mimeType, String cookie, String referer, boolean hasUserGesture, String filename, long contentLength) {
        ContentViewDownloadDelegate downloadDelegate = downloadDelegateFromView(view);
        if (downloadDelegate != null) {
            downloadDelegate.requestHttpGetDownload(new Builder().setUrl(url).setUserAgent(userAgent).setContentDisposition(contentDisposition).setMimeType(mimeType).setCookie(cookie).setReferer(referer).setHasUserGesture(hasUserGesture).setFileName(filename).setContentLength(contentLength).setIsGETRequest(true).build());
        }
    }

    @CalledByNative
    public void onDownloadStarted(ContentViewCore view, String filename, String mimeType) {
        ContentViewDownloadDelegate downloadDelegate = downloadDelegateFromView(view);
        if (downloadDelegate != null) {
            downloadDelegate.onDownloadStarted(filename, mimeType);
        }
    }

    @CalledByNative
    public void onDownloadCompleted(Context context, String url, String mimeType, String filename, String path, long contentLength, boolean successful, int downloadId, boolean hasUserGesture) {
        if (sDownloadNotificationService != null) {
            sDownloadNotificationService.onDownloadCompleted(new Builder().setUrl(url).setMimeType(mimeType).setFileName(filename).setFilePath(path).setContentLength(contentLength).setIsSuccessful(successful).setDescription(filename).setDownloadId(downloadId).setHasDownloadId(true).setHasUserGesture(hasUserGesture).build());
        }
    }

    @CalledByNative
    public void onDownloadUpdated(Context context, String url, String mimeType, String filename, String path, long contentLength, boolean successful, int downloadId, int percentCompleted, long timeRemainingInMs, boolean hasUserGesture) {
        if (sDownloadNotificationService != null) {
            sDownloadNotificationService.onDownloadUpdated(new Builder().setUrl(url).setMimeType(mimeType).setFileName(filename).setFilePath(path).setContentLength(contentLength).setIsSuccessful(successful).setDescription(filename).setDownloadId(downloadId).setHasDownloadId(true).setPercentCompleted(percentCompleted).setTimeRemainingInMillis(timeRemainingInMs).setHasUserGesture(hasUserGesture).build());
        }
    }

    @CalledByNative
    public void onDangerousDownload(ContentViewCore view, String filename, int downloadId) {
        ContentViewDownloadDelegate downloadDelegate = downloadDelegateFromView(view);
        if (downloadDelegate != null) {
            downloadDelegate.onDangerousDownload(filename, downloadId);
        }
    }

    @CalledByNative
    private boolean hasFileAccess(ContentViewCore view) {
        return view.getWindowAndroid().hasFileAccess();
    }

    @CalledByNative
    private void requestFileAccess(ContentViewCore view, long callbackId) {
        view.getWindowAndroid().requestFileAccess(new C06091(callbackId));
    }
}
