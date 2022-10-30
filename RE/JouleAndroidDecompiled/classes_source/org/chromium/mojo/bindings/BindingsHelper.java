package org.chromium.mojo.bindings;

import android.support.v4.internal.view.SupportMenu;
import android.support.v4.media.TransportMediator;
import org.chromium.mojo.system.AsyncWaiter;
import org.chromium.mojo.system.Handle;

public class BindingsHelper {
    public static final int ALIGNMENT = 8;
    public static final int ARRAY_NULLABLE = 1;
    public static final int ELEMENT_NULLABLE = 2;
    public static final DataHeader MAP_STRUCT_HEADER;
    public static final int NOTHING_NULLABLE = 0;
    public static final int POINTER_SIZE = 8;
    public static final int SERIALIZED_HANDLE_SIZE = 4;
    public static final int SERIALIZED_INTERFACE_SIZE = 8;
    public static final int UNION_SIZE = 16;
    public static final int UNSPECIFIED_ARRAY_LENGTH = -1;

    static {
        MAP_STRUCT_HEADER = new DataHeader(24, NOTHING_NULLABLE);
    }

    public static boolean isArrayNullable(int arrayNullability) {
        return (arrayNullability & ARRAY_NULLABLE) > 0;
    }

    public static boolean isElementNullable(int arrayNullability) {
        return (arrayNullability & ELEMENT_NULLABLE) > 0;
    }

    public static int align(int size) {
        return ((size + SERIALIZED_INTERFACE_SIZE) + UNSPECIFIED_ARRAY_LENGTH) & -8;
    }

    public static long align(long size) {
        return ((8 + size) - 1) & -8;
    }

    public static int utf8StringSizeInBytes(String s) {
        int res = NOTHING_NULLABLE;
        int i = NOTHING_NULLABLE;
        while (i < s.length()) {
            char c = s.charAt(i);
            int codepoint = c;
            if (isSurrogate(c)) {
                i += ARRAY_NULLABLE;
                codepoint = Character.toCodePoint(c, s.charAt(i));
            }
            res += ARRAY_NULLABLE;
            if (codepoint > TransportMediator.KEYCODE_MEDIA_PAUSE) {
                res += ARRAY_NULLABLE;
                if (codepoint > 2047) {
                    res += ARRAY_NULLABLE;
                    if (codepoint > SupportMenu.USER_MASK) {
                        res += ARRAY_NULLABLE;
                        if (codepoint > 2097151) {
                            res += ARRAY_NULLABLE;
                            if (codepoint > 67108863) {
                                res += ARRAY_NULLABLE;
                            }
                        }
                    }
                }
            }
            i += ARRAY_NULLABLE;
        }
        return res;
    }

    public static boolean equals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    public static int hashCode(Object o) {
        if (o == null) {
            return NOTHING_NULLABLE;
        }
        return o.hashCode();
    }

    public static int hashCode(boolean o) {
        return o ? 1231 : 1237;
    }

    public static int hashCode(long o) {
        return (int) ((o >>> 32) ^ o);
    }

    public static int hashCode(float o) {
        return Float.floatToIntBits(o);
    }

    public static int hashCode(double o) {
        return hashCode(Double.doubleToLongBits(o));
    }

    public static int hashCode(int o) {
        return o;
    }

    private static boolean isSurrogate(char c) {
        return c >= '\ud800' && c < '\ue000';
    }

    static AsyncWaiter getDefaultAsyncWaiterForHandle(Handle handle) {
        if (handle.getCore() != null) {
            return handle.getCore().getDefaultAsyncWaiter();
        }
        return null;
    }
}
