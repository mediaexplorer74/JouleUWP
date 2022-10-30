package org.chromium.content.browser;

public final class DownloadInfo {
    private final String mContentDisposition;
    private final long mContentLength;
    private final String mCookie;
    private final String mDescription;
    private final int mDownloadId;
    private final String mFileName;
    private final String mFilePath;
    private final boolean mHasDownloadId;
    private final boolean mHasUserGesture;
    private final boolean mIsGETRequest;
    private final boolean mIsSuccessful;
    private final String mMimeType;
    private final int mPercentCompleted;
    private final String mReferer;
    private final long mTimeRemainingInMillis;
    private final String mUrl;
    private final String mUserAgent;

    public static class Builder {
        static final /* synthetic */ boolean $assertionsDisabled;
        private String mContentDisposition;
        private long mContentLength;
        private String mCookie;
        private String mDescription;
        private int mDownloadId;
        private String mFileName;
        private String mFilePath;
        private boolean mHasDownloadId;
        private boolean mHasUserGesture;
        private boolean mIsGETRequest;
        private boolean mIsSuccessful;
        private String mMimeType;
        private int mPercentCompleted;
        private String mReferer;
        private long mTimeRemainingInMillis;
        private String mUrl;
        private String mUserAgent;

        static {
            $assertionsDisabled = !DownloadInfo.class.desiredAssertionStatus();
        }

        public Builder() {
            this.mPercentCompleted = -1;
        }

        public Builder setUrl(String url) {
            this.mUrl = url;
            return this;
        }

        public Builder setUserAgent(String userAgent) {
            this.mUserAgent = userAgent;
            return this;
        }

        public Builder setMimeType(String mimeType) {
            this.mMimeType = mimeType;
            return this;
        }

        public Builder setCookie(String cookie) {
            this.mCookie = cookie;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.mFileName = fileName;
            return this;
        }

        public Builder setDescription(String description) {
            this.mDescription = description;
            return this;
        }

        public Builder setFilePath(String filePath) {
            this.mFilePath = filePath;
            return this;
        }

        public Builder setReferer(String referer) {
            this.mReferer = referer;
            return this;
        }

        public Builder setContentLength(long contentLength) {
            this.mContentLength = contentLength;
            return this;
        }

        public Builder setIsGETRequest(boolean isGETRequest) {
            this.mIsGETRequest = isGETRequest;
            return this;
        }

        public Builder setHasDownloadId(boolean hasDownloadId) {
            this.mHasDownloadId = hasDownloadId;
            return this;
        }

        public Builder setDownloadId(int downloadId) {
            this.mDownloadId = downloadId;
            return this;
        }

        public Builder setHasUserGesture(boolean hasUserGesture) {
            this.mHasUserGesture = hasUserGesture;
            return this;
        }

        public Builder setIsSuccessful(boolean isSuccessful) {
            this.mIsSuccessful = isSuccessful;
            return this;
        }

        public Builder setContentDisposition(String contentDisposition) {
            this.mContentDisposition = contentDisposition;
            return this;
        }

        public Builder setPercentCompleted(int percentCompleted) {
            if ($assertionsDisabled || percentCompleted <= 100) {
                this.mPercentCompleted = percentCompleted;
                return this;
            }
            throw new AssertionError();
        }

        public Builder setTimeRemainingInMillis(long timeRemainingInMillis) {
            this.mTimeRemainingInMillis = timeRemainingInMillis;
            return this;
        }

        public DownloadInfo build() {
            return new DownloadInfo();
        }

        public static Builder fromDownloadInfo(DownloadInfo downloadInfo) {
            Builder builder = new Builder();
            builder.setUrl(downloadInfo.getUrl()).setUserAgent(downloadInfo.getUserAgent()).setMimeType(downloadInfo.getMimeType()).setCookie(downloadInfo.getCookie()).setFileName(downloadInfo.getFileName()).setDescription(downloadInfo.getDescription()).setFilePath(downloadInfo.getFilePath()).setReferer(downloadInfo.getReferer()).setContentLength(downloadInfo.getContentLength()).setHasDownloadId(downloadInfo.hasDownloadId()).setDownloadId(downloadInfo.getDownloadId()).setHasUserGesture(downloadInfo.hasUserGesture()).setContentDisposition(downloadInfo.getContentDisposition()).setIsGETRequest(downloadInfo.isGETRequest()).setIsSuccessful(downloadInfo.isSuccessful()).setPercentCompleted(downloadInfo.getPercentCompleted()).setTimeRemainingInMillis(downloadInfo.getTimeRemainingInMillis());
            return builder;
        }
    }

    private DownloadInfo(Builder builder) {
        this.mUrl = builder.mUrl;
        this.mUserAgent = builder.mUserAgent;
        this.mMimeType = builder.mMimeType;
        this.mCookie = builder.mCookie;
        this.mFileName = builder.mFileName;
        this.mDescription = builder.mDescription;
        this.mFilePath = builder.mFilePath;
        this.mReferer = builder.mReferer;
        this.mContentLength = builder.mContentLength;
        this.mHasDownloadId = builder.mHasDownloadId;
        this.mDownloadId = builder.mDownloadId;
        this.mHasUserGesture = builder.mHasUserGesture;
        this.mIsSuccessful = builder.mIsSuccessful;
        this.mIsGETRequest = builder.mIsGETRequest;
        this.mContentDisposition = builder.mContentDisposition;
        this.mPercentCompleted = builder.mPercentCompleted;
        this.mTimeRemainingInMillis = builder.mTimeRemainingInMillis;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public String getUserAgent() {
        return this.mUserAgent;
    }

    public String getMimeType() {
        return this.mMimeType;
    }

    public String getCookie() {
        return this.mCookie;
    }

    public String getFileName() {
        return this.mFileName;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public String getFilePath() {
        return this.mFilePath;
    }

    public String getReferer() {
        return this.mReferer;
    }

    public long getContentLength() {
        return this.mContentLength;
    }

    public boolean isGETRequest() {
        return this.mIsGETRequest;
    }

    public boolean hasDownloadId() {
        return this.mHasDownloadId;
    }

    public int getDownloadId() {
        return this.mDownloadId;
    }

    public boolean hasUserGesture() {
        return this.mHasUserGesture;
    }

    public boolean isSuccessful() {
        return this.mIsSuccessful;
    }

    public String getContentDisposition() {
        return this.mContentDisposition;
    }

    public int getPercentCompleted() {
        return this.mPercentCompleted;
    }

    public long getTimeRemainingInMillis() {
        return this.mTimeRemainingInMillis;
    }
}
