package org.chromium.base.library_loader;

public class ProcessInitException extends Exception {
    private int mErrorCode;

    public ProcessInitException(int errorCode) {
        this.mErrorCode = 0;
        this.mErrorCode = errorCode;
    }

    public ProcessInitException(int errorCode, Throwable throwable) {
        super(null, throwable);
        this.mErrorCode = 0;
        this.mErrorCode = errorCode;
    }

    public int getErrorCode() {
        return this.mErrorCode;
    }
}
