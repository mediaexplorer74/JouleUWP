package android.support.v4.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v4.os.BuildCompat;

public final class PointerIconCompat {
    static final PointerIconCompatImpl IMPL;
    public static final int STYLE_ALIAS = 1010;
    public static final int STYLE_ALL_SCROLL = 1013;
    public static final int STYLE_ARROW = 1000;
    public static final int STYLE_CELL = 1006;
    public static final int STYLE_CONTEXT_MENU = 1001;
    public static final int STYLE_COPY = 1011;
    public static final int STYLE_CROSSHAIR = 1007;
    public static final int STYLE_DEFAULT = 1000;
    public static final int STYLE_GRAB = 1020;
    public static final int STYLE_GRABBING = 1021;
    public static final int STYLE_HAND = 1002;
    public static final int STYLE_HELP = 1003;
    public static final int STYLE_HORIZONTAL_DOUBLE_ARROW = 1014;
    public static final int STYLE_NO_DROP = 1012;
    public static final int STYLE_NULL = 0;
    public static final int STYLE_TEXT = 1008;
    public static final int STYLE_TOP_LEFT_DIAGONAL_DOUBLE_ARROW = 1017;
    public static final int STYLE_TOP_RIGHT_DIAGONAL_DOUBLE_ARROW = 1016;
    public static final int STYLE_VERTICAL_DOUBLE_ARROW = 1015;
    public static final int STYLE_VERTICAL_TEXT = 1009;
    public static final int STYLE_WAIT = 1004;
    public static final int STYLE_ZOOM_IN = 1018;
    public static final int STYLE_ZOOM_OUT = 1019;
    private Object mPointerIcon;

    interface PointerIconCompatImpl {
        Object createCustomIcon(Bitmap bitmap, float f, float f2);

        Object getSystemIcon(Context context, int i);

        Object loadCustomIcon(Resources resources, int i);
    }

    static class BasePointerIconCompatImpl implements PointerIconCompatImpl {
        BasePointerIconCompatImpl() {
        }

        public Object getSystemIcon(Context context, int style) {
            return null;
        }

        public Object createCustomIcon(Bitmap bitmap, float hotSpotX, float hotSpotY) {
            return null;
        }

        public Object loadCustomIcon(Resources resources, int resourceId) {
            return null;
        }
    }

    static class Api24PointerIconCompatImpl extends BasePointerIconCompatImpl {
        Api24PointerIconCompatImpl() {
        }

        public Object getSystemIcon(Context context, int style) {
            return PointerIconCompatApi24.getSystemIcon(context, style);
        }

        public Object createCustomIcon(Bitmap bitmap, float hotSpotX, float hotSpotY) {
            return PointerIconCompatApi24.createCustomIcon(bitmap, hotSpotX, hotSpotY);
        }

        public Object loadCustomIcon(Resources resources, int resourceId) {
            return PointerIconCompatApi24.loadCustomIcon(resources, resourceId);
        }
    }

    private PointerIconCompat(Object pointerIcon) {
        this.mPointerIcon = pointerIcon;
    }

    private static PointerIconCompat create(Object pointerIcon) {
        return new PointerIconCompat(pointerIcon);
    }

    public Object getPointerIcon() {
        return this.mPointerIcon;
    }

    static {
        if (BuildCompat.isAtLeastN()) {
            IMPL = new Api24PointerIconCompatImpl();
        } else {
            IMPL = new BasePointerIconCompatImpl();
        }
    }

    public static PointerIconCompat getSystemIcon(Context context, int style) {
        return create(IMPL.getSystemIcon(context, style));
    }

    public static PointerIconCompat createCustomIcon(Bitmap bitmap, float hotSpotX, float hotSpotY) {
        return create(IMPL.createCustomIcon(bitmap, hotSpotX, hotSpotY));
    }

    public static PointerIconCompat loadCustomIcon(Resources resources, int resourceId) {
        return create(IMPL.loadCustomIcon(resources, resourceId));
    }
}
