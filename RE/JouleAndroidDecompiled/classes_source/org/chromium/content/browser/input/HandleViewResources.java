package org.chromium.content.browser.input;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

@JNINamespace("content")
public class HandleViewResources {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final int[] CENTER_HANDLE_ATTRS;
    private static final int[] LEFT_HANDLE_ATTRS;
    private static final int[] RIGHT_HANDLE_ATTRS;

    static {
        $assertionsDisabled = !HandleViewResources.class.desiredAssertionStatus();
        LEFT_HANDLE_ATTRS = new int[]{16843461};
        CENTER_HANDLE_ATTRS = new int[]{16843463};
        RIGHT_HANDLE_ATTRS = new int[]{16843462};
    }

    public static Drawable getLeftHandleDrawable(Context context) {
        return getHandleDrawable(context, LEFT_HANDLE_ATTRS);
    }

    public static Drawable getCenterHandleDrawable(Context context) {
        return getHandleDrawable(context, CENTER_HANDLE_ATTRS);
    }

    public static Drawable getRightHandleDrawable(Context context) {
        return getHandleDrawable(context, RIGHT_HANDLE_ATTRS);
    }

    private static Drawable getHandleDrawable(Context context, int[] attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs);
        Drawable drawable = a.getDrawable(0);
        if (drawable == null) {
            try {
                drawable = ApiCompatibilityUtils.getDrawable(context.getResources(), a.getResourceId(0, 0));
            } catch (NotFoundException e) {
            }
        }
        a.recycle();
        return drawable;
    }

    private static Bitmap getHandleBitmap(Context context, int[] attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs);
        int resId = a.getResourceId(a.getIndex(0), 0);
        Resources res = a.getResources();
        a.recycle();
        Config config = Config.ARGB_8888;
        Options options = new Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = config;
        Bitmap bitmap = BitmapFactory.decodeResource(res, resId, options);
        if (bitmap != null) {
            return bitmap;
        }
        if (res != context.getResources()) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
            if (bitmap != null) {
                return bitmap;
            }
        }
        Drawable drawable = getHandleDrawable(context, attrs);
        if ($assertionsDisabled || drawable != null) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap canvasBitmap = Bitmap.createBitmap(width, height, config);
            Canvas canvas = new Canvas(canvasBitmap);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
            return canvasBitmap;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private static Bitmap getLeftHandleBitmap(Context context) {
        return getHandleBitmap(context, LEFT_HANDLE_ATTRS);
    }

    @CalledByNative
    private static Bitmap getCenterHandleBitmap(Context context) {
        return getHandleBitmap(context, CENTER_HANDLE_ATTRS);
    }

    @CalledByNative
    private static Bitmap getRightHandleBitmap(Context context) {
        return getHandleBitmap(context, RIGHT_HANDLE_ATTRS);
    }
}
