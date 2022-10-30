package org.chromium.content.browser;

public interface ContentViewDownloadDelegate {
    void onDangerousDownload(String str, int i);

    void onDownloadStarted(String str, String str2);

    void requestHttpGetDownload(DownloadInfo downloadInfo);
}
