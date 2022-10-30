package org.xwalk.core;

interface XWalkLibraryInterface {
    public static final String PRIVATE_DATA_DIRECTORY_SUFFIX = "xwalkcore";
    public static final int STATUS_ARCHITECTURE_MISMATCH = 6;
    public static final int STATUS_INCOMPLETE_LIBRARY = 5;
    public static final int STATUS_MATCH = 1;
    public static final int STATUS_NEWER_VERSION = 4;
    public static final int STATUS_NOT_FOUND = 2;
    public static final int STATUS_OLDER_VERSION = 3;
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_SIGNATURE_CHECK_ERROR = 7;
}
