package org.chromium.base;

import java.util.Locale;
import org.chromium.base.annotations.RemovableInRelease;

public class Log {
    public static final int ASSERT = 7;
    public static final int DEBUG = 3;
    public static final int ERROR = 6;
    public static final int INFO = 4;
    public static final int VERBOSE = 2;
    public static final int WARN = 5;

    private Log() {
    }

    private static String formatLog(String messageTemplate, Object... params) {
        if (params == null || params.length == 0) {
            return messageTemplate;
        }
        return String.format(Locale.US, messageTemplate, params);
    }

    private static String formatLogWithStack(String messageTemplate, Object... params) {
        return "[" + getCallOrigin() + "] " + formatLog(messageTemplate, params);
    }

    public static boolean isLoggable(String tag, int level) {
        return android.util.Log.isLoggable(tag, level);
    }

    private static void verbose(String tag, String messageTemplate, Object... args) {
        if (isLoggable(tag, VERBOSE)) {
            String message = formatLogWithStack(messageTemplate, args);
            Throwable tr = getThrowableToLog(args);
            if (tr != null) {
                android.util.Log.v(tag, message, tr);
            } else {
                android.util.Log.v(tag, message);
            }
        }
    }

    @RemovableInRelease
    @VisibleForTesting
    public static void m34v(String tag, String message) {
        verbose(tag, message, new Object[0]);
    }

    @RemovableInRelease
    @VisibleForTesting
    public static void m35v(String tag, String messageTemplate, Object arg1) {
        verbose(tag, messageTemplate, arg1);
    }

    @RemovableInRelease
    @VisibleForTesting
    public static void m36v(String tag, String messageTemplate, Object arg1, Object arg2) {
        Object[] objArr = new Object[VERBOSE];
        objArr[0] = arg1;
        objArr[1] = arg2;
        verbose(tag, messageTemplate, objArr);
    }

    @RemovableInRelease
    @VisibleForTesting
    public static void m37v(String tag, String messageTemplate, Object arg1, Object arg2, Object arg3) {
        Object[] objArr = new Object[DEBUG];
        objArr[0] = arg1;
        objArr[1] = arg2;
        objArr[VERBOSE] = arg3;
        verbose(tag, messageTemplate, objArr);
    }

    @RemovableInRelease
    @VisibleForTesting
    public static void m38v(String tag, String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4) {
        Object[] objArr = new Object[INFO];
        objArr[0] = arg1;
        objArr[1] = arg2;
        objArr[VERBOSE] = arg3;
        objArr[DEBUG] = arg4;
        verbose(tag, messageTemplate, objArr);
    }

    @RemovableInRelease
    @VisibleForTesting
    public static void m39v(String tag, String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        Object[] objArr = new Object[WARN];
        objArr[0] = arg1;
        objArr[1] = arg2;
        objArr[VERBOSE] = arg3;
        objArr[DEBUG] = arg4;
        objArr[INFO] = arg5;
        verbose(tag, messageTemplate, objArr);
    }

    @RemovableInRelease
    @VisibleForTesting
    public static void m40v(String tag, String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
        Object[] objArr = new Object[ERROR];
        objArr[0] = arg1;
        objArr[1] = arg2;
        objArr[VERBOSE] = arg3;
        objArr[DEBUG] = arg4;
        objArr[INFO] = arg5;
        objArr[WARN] = arg6;
        verbose(tag, messageTemplate, objArr);
    }

    @RemovableInRelease
    @VisibleForTesting
    public static void m41v(String tag, String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7) {
        Object[] objArr = new Object[ASSERT];
        objArr[0] = arg1;
        objArr[1] = arg2;
        objArr[VERBOSE] = arg3;
        objArr[DEBUG] = arg4;
        objArr[INFO] = arg5;
        objArr[WARN] = arg6;
        objArr[ERROR] = arg7;
        verbose(tag, messageTemplate, objArr);
    }

    private static void debug(String tag, String messageTemplate, Object... args) {
        if (isLoggable(tag, DEBUG)) {
            String message = formatLogWithStack(messageTemplate, args);
            Throwable tr = getThrowableToLog(args);
            if (tr != null) {
                android.util.Log.d(tag, message, tr);
            } else {
                android.util.Log.d(tag, message);
            }
        }
    }

    @RemovableInRelease
    @VisibleForTesting
    public static void m24d(String tag, String message) {
        debug(tag, message, new Object[0]);
    }

    @RemovableInRelease
    @VisibleForTesting
    public static void m25d(String tag, String messageTemplate, Object arg1) {
        debug(tag, messageTemplate, arg1);
    }

    @RemovableInRelease
    @VisibleForTesting
    public static void m26d(String tag, String messageTemplate, Object arg1, Object arg2) {
        Object[] objArr = new Object[VERBOSE];
        objArr[0] = arg1;
        objArr[1] = arg2;
        debug(tag, messageTemplate, objArr);
    }

    @RemovableInRelease
    @VisibleForTesting
    public static void m27d(String tag, String messageTemplate, Object arg1, Object arg2, Object arg3) {
        Object[] objArr = new Object[DEBUG];
        objArr[0] = arg1;
        objArr[1] = arg2;
        objArr[VERBOSE] = arg3;
        debug(tag, messageTemplate, objArr);
    }

    @RemovableInRelease
    @VisibleForTesting
    public static void m28d(String tag, String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4) {
        Object[] objArr = new Object[INFO];
        objArr[0] = arg1;
        objArr[1] = arg2;
        objArr[VERBOSE] = arg3;
        objArr[DEBUG] = arg4;
        debug(tag, messageTemplate, objArr);
    }

    @RemovableInRelease
    @VisibleForTesting
    public static void m29d(String tag, String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        Object[] objArr = new Object[WARN];
        objArr[0] = arg1;
        objArr[1] = arg2;
        objArr[VERBOSE] = arg3;
        objArr[DEBUG] = arg4;
        objArr[INFO] = arg5;
        debug(tag, messageTemplate, objArr);
    }

    @RemovableInRelease
    @VisibleForTesting
    public static void m30d(String tag, String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
        Object[] objArr = new Object[ERROR];
        objArr[0] = arg1;
        objArr[1] = arg2;
        objArr[VERBOSE] = arg3;
        objArr[DEBUG] = arg4;
        objArr[INFO] = arg5;
        objArr[WARN] = arg6;
        debug(tag, messageTemplate, objArr);
    }

    @RemovableInRelease
    @VisibleForTesting
    public static void m31d(String tag, String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7) {
        Object[] objArr = new Object[ASSERT];
        objArr[0] = arg1;
        objArr[1] = arg2;
        objArr[VERBOSE] = arg3;
        objArr[DEBUG] = arg4;
        objArr[INFO] = arg5;
        objArr[WARN] = arg6;
        objArr[ERROR] = arg7;
        debug(tag, messageTemplate, objArr);
    }

    @VisibleForTesting
    public static void m33i(String tag, String messageTemplate, Object... args) {
        if (isLoggable(tag, INFO)) {
            String message = formatLog(messageTemplate, args);
            Throwable tr = getThrowableToLog(args);
            if (tr != null) {
                android.util.Log.i(tag, message, tr);
            } else {
                android.util.Log.i(tag, message);
            }
        }
    }

    @VisibleForTesting
    public static void m42w(String tag, String messageTemplate, Object... args) {
        if (isLoggable(tag, WARN)) {
            String message = formatLog(messageTemplate, args);
            Throwable tr = getThrowableToLog(args);
            if (tr != null) {
                android.util.Log.w(tag, message, tr);
            } else {
                android.util.Log.w(tag, message);
            }
        }
    }

    @VisibleForTesting
    public static void m32e(String tag, String messageTemplate, Object... args) {
        if (isLoggable(tag, ERROR)) {
            String message = formatLog(messageTemplate, args);
            Throwable tr = getThrowableToLog(args);
            if (tr != null) {
                android.util.Log.e(tag, message, tr);
            } else {
                android.util.Log.e(tag, message);
            }
        }
    }

    @VisibleForTesting
    public static void wtf(String tag, String messageTemplate, Object... args) {
        if (isLoggable(tag, ASSERT)) {
            String message = formatLog(messageTemplate, args);
            Throwable tr = getThrowableToLog(args);
            if (tr != null) {
                android.util.Log.wtf(tag, message, tr);
            } else {
                android.util.Log.wtf(tag, message);
            }
        }
    }

    private static Throwable getThrowableToLog(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        Object lastArg = args[args.length - 1];
        if (lastArg instanceof Throwable) {
            return (Throwable) lastArg;
        }
        return null;
    }

    private static String getCallOrigin() {
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        String logClassName = Log.class.getName();
        int callerStackIndex = 0;
        while (callerStackIndex < st.length) {
            if (st[callerStackIndex].getClassName().equals(logClassName)) {
                callerStackIndex += INFO;
                break;
            }
            callerStackIndex++;
        }
        return st[callerStackIndex].getFileName() + ":" + st[callerStackIndex].getLineNumber();
    }
}
