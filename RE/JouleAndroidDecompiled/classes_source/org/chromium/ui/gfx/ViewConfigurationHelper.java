package org.chromium.ui.gfx;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.TypedValue;
import android.view.ViewConfiguration;
import com.adobe.phonegap.push.PushConstants;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.ui.C0408R;

@JNINamespace("gfx")
public class ViewConfigurationHelper {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final float MIN_SCALING_SPAN_MM = 27.0f;
    private static final float MIN_SCALING_TOUCH_MAJOR_DIP = 48.0f;
    private final Context mAppContext;
    private float mDensity;
    private ViewConfiguration mViewConfiguration;

    /* renamed from: org.chromium.ui.gfx.ViewConfigurationHelper.1 */
    class C04181 implements ComponentCallbacks {
        C04181() {
        }

        public void onConfigurationChanged(Configuration configuration) {
            ViewConfigurationHelper.this.updateNativeViewConfigurationIfNecessary();
        }

        public void onLowMemory() {
        }
    }

    private native void nativeUpdateSharedViewConfiguration(float f, float f2, float f3, float f4, float f5, float f6);

    static {
        $assertionsDisabled = !ViewConfigurationHelper.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    private ViewConfigurationHelper(Context context) {
        this.mAppContext = context.getApplicationContext();
        this.mViewConfiguration = ViewConfiguration.get(this.mAppContext);
        this.mDensity = this.mAppContext.getResources().getDisplayMetrics().density;
        if (!$assertionsDisabled && this.mDensity <= 0.0f) {
            throw new AssertionError();
        }
    }

    private void registerListener() {
        this.mAppContext.registerComponentCallbacks(new C04181());
    }

    private void updateNativeViewConfigurationIfNecessary() {
        ViewConfiguration configuration = ViewConfiguration.get(this.mAppContext);
        if (this.mViewConfiguration != configuration) {
            this.mViewConfiguration = configuration;
            this.mDensity = this.mAppContext.getResources().getDisplayMetrics().density;
            if ($assertionsDisabled || this.mDensity > 0.0f) {
                nativeUpdateSharedViewConfiguration(getMaximumFlingVelocity(), getMinimumFlingVelocity(), getTouchSlop(), getDoubleTapSlop(), getMinScalingSpan(), getMinScalingTouchMajor());
                return;
            }
            throw new AssertionError();
        } else if (!$assertionsDisabled && this.mDensity != this.mAppContext.getResources().getDisplayMetrics().density) {
            throw new AssertionError();
        }
    }

    @CalledByNative
    private static int getDoubleTapTimeout() {
        return ViewConfiguration.getDoubleTapTimeout();
    }

    @CalledByNative
    private static int getLongPressTimeout() {
        return ViewConfiguration.getLongPressTimeout();
    }

    @CalledByNative
    private static int getTapTimeout() {
        return ViewConfiguration.getTapTimeout();
    }

    @CalledByNative
    private static float getScrollFriction() {
        return ViewConfiguration.getScrollFriction();
    }

    @CalledByNative
    private float getMaximumFlingVelocity() {
        return toDips(this.mViewConfiguration.getScaledMaximumFlingVelocity());
    }

    @CalledByNative
    private float getMinimumFlingVelocity() {
        return toDips(this.mViewConfiguration.getScaledMinimumFlingVelocity());
    }

    @CalledByNative
    private float getTouchSlop() {
        return toDips(this.mViewConfiguration.getScaledTouchSlop());
    }

    @CalledByNative
    private float getDoubleTapSlop() {
        return toDips(this.mViewConfiguration.getScaledDoubleTapSlop());
    }

    @CalledByNative
    private float getMinScalingSpan() {
        return toDips(getScaledMinScalingSpan());
    }

    @CalledByNative
    private float getMinScalingTouchMajor() {
        return toDips(getScaledMinScalingTouchMajor());
    }

    private int getScaledMinScalingSpan() {
        Resources res = this.mAppContext.getResources();
        int id = res.getIdentifier("config_minScalingSpan", "dimen", PushConstants.ANDROID);
        if (id == 0) {
            id = C0408R.dimen.config_min_scaling_span;
        }
        try {
            return res.getDimensionPixelSize(id);
        } catch (NotFoundException e) {
            if ($assertionsDisabled) {
                return (int) TypedValue.applyDimension(5, MIN_SCALING_SPAN_MM, res.getDisplayMetrics());
            }
            throw new AssertionError("MinScalingSpan resource lookup failed.");
        }
    }

    private int getScaledMinScalingTouchMajor() {
        Resources res = this.mAppContext.getResources();
        int id = res.getIdentifier("config_minScalingTouchMajor", "dimen", PushConstants.ANDROID);
        if (id == 0) {
            id = C0408R.dimen.config_min_scaling_touch_major;
        }
        try {
            return res.getDimensionPixelSize(id);
        } catch (NotFoundException e) {
            if ($assertionsDisabled) {
                return (int) TypedValue.applyDimension(1, MIN_SCALING_TOUCH_MAJOR_DIP, res.getDisplayMetrics());
            }
            throw new AssertionError("MinScalingTouchMajor resource lookup failed.");
        }
    }

    private float toDips(int pixels) {
        return ((float) pixels) / this.mDensity;
    }

    @CalledByNative
    private static ViewConfigurationHelper createWithListener(Context context) {
        ViewConfigurationHelper viewConfigurationHelper = new ViewConfigurationHelper(context);
        viewConfigurationHelper.registerListener();
        return viewConfigurationHelper;
    }
}
