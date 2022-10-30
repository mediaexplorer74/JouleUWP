package org.chromium.base;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager.TaskDescription;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Process;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

@TargetApi(21)
public class ApiCompatibilityUtils {

    private static class FinishAndRemoveTaskWithRetry implements Runnable {
        private static final long MAX_TRY_COUNT = 3;
        private static final long RETRY_DELAY_MS = 500;
        private final Activity mActivity;
        private int mTryCount;

        FinishAndRemoveTaskWithRetry(Activity activity) {
            this.mActivity = activity;
        }

        public void run() {
            this.mActivity.finishAndRemoveTask();
            this.mTryCount++;
            if (!this.mActivity.isFinishing()) {
                if (((long) this.mTryCount) < MAX_TRY_COUNT) {
                    ThreadUtils.postOnUiThreadDelayed(this, RETRY_DELAY_MS);
                } else {
                    this.mActivity.finish();
                }
            }
        }
    }

    private ApiCompatibilityUtils() {
    }

    public static boolean isLayoutRtl(View view) {
        if (VERSION.SDK_INT < 17) {
            return false;
        }
        if (view.getLayoutDirection() == 1) {
            return true;
        }
        return false;
    }

    public static int getLayoutDirection(Configuration configuration) {
        if (VERSION.SDK_INT >= 17) {
            return configuration.getLayoutDirection();
        }
        return 0;
    }

    public static boolean isPrintingSupported() {
        return VERSION.SDK_INT >= 19;
    }

    public static void setLayoutDirection(View view, int layoutDirection) {
        if (VERSION.SDK_INT >= 17) {
            view.setLayoutDirection(layoutDirection);
        }
    }

    public static void setTextAlignment(View view, int textAlignment) {
        if (VERSION.SDK_INT >= 17) {
            view.setTextAlignment(textAlignment);
        }
    }

    public static void setTextDirection(View view, int textDirection) {
        if (VERSION.SDK_INT >= 17) {
            view.setTextDirection(textDirection);
        }
    }

    public static void setMarginEnd(MarginLayoutParams layoutParams, int end) {
        if (VERSION.SDK_INT >= 17) {
            layoutParams.setMarginEnd(end);
        } else {
            layoutParams.rightMargin = end;
        }
    }

    public static int getMarginEnd(MarginLayoutParams layoutParams) {
        if (VERSION.SDK_INT >= 17) {
            return layoutParams.getMarginEnd();
        }
        return layoutParams.rightMargin;
    }

    public static void setMarginStart(MarginLayoutParams layoutParams, int start) {
        if (VERSION.SDK_INT >= 17) {
            layoutParams.setMarginStart(start);
        } else {
            layoutParams.leftMargin = start;
        }
    }

    public static int getMarginStart(MarginLayoutParams layoutParams) {
        if (VERSION.SDK_INT >= 17) {
            return layoutParams.getMarginStart();
        }
        return layoutParams.leftMargin;
    }

    public static void setPaddingRelative(View view, int start, int top, int end, int bottom) {
        if (VERSION.SDK_INT >= 17) {
            view.setPaddingRelative(start, top, end, bottom);
        } else {
            view.setPadding(start, top, end, bottom);
        }
    }

    public static int getPaddingStart(View view) {
        if (VERSION.SDK_INT >= 17) {
            return view.getPaddingStart();
        }
        return view.getPaddingLeft();
    }

    public static int getPaddingEnd(View view) {
        if (VERSION.SDK_INT >= 17) {
            return view.getPaddingEnd();
        }
        return view.getPaddingRight();
    }

    public static void setCompoundDrawablesRelative(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
        if (VERSION.SDK_INT == 17) {
            Drawable drawable;
            boolean isRtl = isLayoutRtl(textView);
            if (isRtl) {
                drawable = end;
            } else {
                drawable = start;
            }
            if (!isRtl) {
                start = end;
            }
            textView.setCompoundDrawables(drawable, top, start, bottom);
        } else if (VERSION.SDK_INT > 17) {
            textView.setCompoundDrawablesRelative(start, top, end, bottom);
        } else {
            textView.setCompoundDrawables(start, top, end, bottom);
        }
    }

    public static void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
        if (VERSION.SDK_INT == 17) {
            Drawable drawable;
            boolean isRtl = isLayoutRtl(textView);
            if (isRtl) {
                drawable = end;
            } else {
                drawable = start;
            }
            if (!isRtl) {
                start = end;
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable, top, start, bottom);
        } else if (VERSION.SDK_INT > 17) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom);
        }
    }

    public static void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, int start, int top, int end, int bottom) {
        if (VERSION.SDK_INT == 17) {
            int i;
            boolean isRtl = isLayoutRtl(textView);
            if (isRtl) {
                i = end;
            } else {
                i = start;
            }
            if (!isRtl) {
                start = end;
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(i, top, start, bottom);
        } else if (VERSION.SDK_INT > 17) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom);
        }
    }

    public static void postInvalidateOnAnimation(View view) {
        if (VERSION.SDK_INT >= 16) {
            view.postInvalidateOnAnimation();
        } else {
            view.postInvalidate();
        }
    }

    public static void postOnAnimation(View view, Runnable action) {
        if (VERSION.SDK_INT >= 16) {
            view.postOnAnimation(action);
        } else {
            view.postDelayed(action, getFrameTime());
        }
    }

    public static void postOnAnimationDelayed(View view, Runnable action, long delayMillis) {
        if (VERSION.SDK_INT >= 16) {
            view.postOnAnimationDelayed(action, delayMillis);
        } else {
            view.postDelayed(action, getFrameTime() + delayMillis);
        }
    }

    private static long getFrameTime() {
        if (VERSION.SDK_INT >= 11) {
            return ValueAnimator.getFrameDelay();
        }
        return 10;
    }

    public static void setContentDescriptionForRemoteView(RemoteViews remoteViews, int viewId, CharSequence contentDescription) {
        if (VERSION.SDK_INT >= 15) {
            remoteViews.setContentDescription(viewId, contentDescription);
        }
    }

    public static void startActivity(Context context, Intent intent, Bundle options) {
        if (VERSION.SDK_INT >= 16) {
            context.startActivity(intent, options);
        } else {
            context.startActivity(intent);
        }
    }

    public static Bundle toBundle(ActivityOptions options) {
        if (VERSION.SDK_INT >= 16) {
            return options.toBundle();
        }
        return null;
    }

    public static void setBackgroundForView(View view, Drawable drawable) {
        if (VERSION.SDK_INT >= 16) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static void removeOnGlobalLayoutListener(View view, OnGlobalLayoutListener listener) {
        if (VERSION.SDK_INT >= 16) {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        } else {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
    }

    public static void setImageAlpha(ImageView iv, int alpha) {
        if (VERSION.SDK_INT >= 16) {
            iv.setImageAlpha(alpha);
        } else {
            iv.setAlpha(alpha);
        }
    }

    public static String getCreatorPackage(PendingIntent intent) {
        if (VERSION.SDK_INT >= 17) {
            return intent.getCreatorPackage();
        }
        return intent.getTargetPackage();
    }

    @TargetApi(17)
    public static boolean isDeviceProvisioned(Context context) {
        if (VERSION.SDK_INT >= 17 && context != null && context.getContentResolver() != null && Global.getInt(context.getContentResolver(), "device_provisioned", 0) == 0) {
            return false;
        }
        return true;
    }

    public static void finishAndRemoveTask(Activity activity) {
        if (VERSION.SDK_INT > 21) {
            activity.finishAndRemoveTask();
        } else if (VERSION.SDK_INT == 21) {
            new FinishAndRemoveTaskWithRetry(activity).run();
        } else {
            activity.finish();
        }
    }

    public static boolean isInteractive(Context context) {
        PowerManager manager = (PowerManager) context.getSystemService("power");
        if (VERSION.SDK_INT >= 20) {
            return manager.isInteractive();
        }
        return manager.isScreenOn();
    }

    public static int getActivityNewDocumentFlag() {
        return VERSION.SDK_INT >= 21 ? AccessibilityNodeInfoCompat.ACTION_COLLAPSE : AccessibilityNodeInfoCompat.ACTION_COLLAPSE;
    }

    public static boolean shouldSkipFirstUseHints(ContentResolver contentResolver) {
        if (VERSION.SDK_INT < 21 || Secure.getInt(contentResolver, "skip_first_use_hints", 0) == 0) {
            return false;
        }
        return true;
    }

    public static void setTaskDescription(Activity activity, String title, Bitmap icon, int color) {
        if (VERSION.SDK_INT >= 21) {
            activity.setTaskDescription(new TaskDescription(title, icon, color));
        }
    }

    public static void setStatusBarColor(Activity activity, int statusBarColor) {
        if (VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            if (statusBarColor == ViewCompat.MEASURED_STATE_MASK && window.getNavigationBarColor() == ViewCompat.MEASURED_STATE_MASK) {
                window.clearFlags(ExploreByTouchHelper.INVALID_ID);
            } else {
                window.addFlags(ExploreByTouchHelper.INVALID_ID);
            }
            window.setStatusBarColor(statusBarColor);
        }
    }

    public static void setStatusBarColor(Window window, int statusBarColor) {
        if (VERSION.SDK_INT >= 21) {
            if (statusBarColor == ViewCompat.MEASURED_STATE_MASK && window.getNavigationBarColor() == ViewCompat.MEASURED_STATE_MASK) {
                window.clearFlags(ExploreByTouchHelper.INVALID_ID);
            } else {
                window.addFlags(ExploreByTouchHelper.INVALID_ID);
            }
            window.setStatusBarColor(statusBarColor);
        }
    }

    public static Drawable getDrawable(Resources res, int id) throws NotFoundException {
        if (VERSION.SDK_INT >= 21) {
            return res.getDrawable(id, null);
        }
        return res.getDrawable(id);
    }

    public static Drawable getDrawableForDensity(Resources res, int id, int density) {
        if (VERSION.SDK_INT >= 21) {
            return res.getDrawableForDensity(id, density, null);
        }
        return res.getDrawableForDensity(id, density);
    }

    public static void finishAfterTransition(Activity activity) {
        if (VERSION.SDK_INT >= 21) {
            activity.finishAfterTransition();
        } else {
            activity.finish();
        }
    }

    public static void announceForAccessibility(View view, CharSequence text) {
        if (VERSION.SDK_INT >= 16) {
            view.announceForAccessibility(text);
        }
    }

    public static Drawable getUserBadgedIcon(Context context, int id) {
        Drawable drawable = getDrawable(context.getResources(), id);
        if (VERSION.SDK_INT >= 21) {
            return context.getPackageManager().getUserBadgedIcon(drawable, Process.myUserHandle());
        }
        return drawable;
    }
}
