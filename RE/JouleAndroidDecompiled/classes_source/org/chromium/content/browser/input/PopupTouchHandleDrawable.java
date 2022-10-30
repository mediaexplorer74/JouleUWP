package org.chromium.content.browser.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import com.google.android.gms.common.ConnectionResult;
import java.lang.ref.WeakReference;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.content.browser.PositionObserver;
import org.chromium.content.browser.PositionObserver.Listener;

@JNINamespace("content")
public class PopupTouchHandleDrawable extends View {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final int FADE_IN_DELAY_MS = 300;
    private static final int FADE_IN_DURATION_MS = 200;
    private float mAlpha;
    private final PopupWindow mContainer;
    private final Context mContext;
    private Runnable mDeferredHandleFadeInRunnable;
    private boolean mDelayVisibilityUpdateWAR;
    private final WeakReference<PopupTouchHandleDrawableDelegate> mDelegate;
    private Drawable mDrawable;
    private long mFadeStartTime;
    private boolean mHasPendingInvalidate;
    private float mHotspotX;
    private float mHotspotY;
    private Runnable mInvalidationRunnable;
    private int mOrientation;
    private final Listener mParentPositionListener;
    private PositionObserver mParentPositionObserver;
    private int mParentPositionX;
    private int mParentPositionY;
    private int mPositionX;
    private int mPositionY;
    private final int[] mTempScreenCoords;
    private boolean mTemporarilyHidden;
    private boolean mVisible;

    /* renamed from: org.chromium.content.browser.input.PopupTouchHandleDrawable.2 */
    class C03622 implements Runnable {
        C03622() {
        }

        public void run() {
            PopupTouchHandleDrawable.this.mHasPendingInvalidate = PopupTouchHandleDrawable.$assertionsDisabled;
            PopupTouchHandleDrawable.this.doInvalidate();
        }
    }

    /* renamed from: org.chromium.content.browser.input.PopupTouchHandleDrawable.3 */
    class C03633 implements Runnable {
        C03633() {
        }

        public void run() {
            if (PopupTouchHandleDrawable.this.isScrollInProgress()) {
                PopupTouchHandleDrawable.this.rescheduleFadeIn();
                return;
            }
            PopupTouchHandleDrawable.this.mTemporarilyHidden = PopupTouchHandleDrawable.$assertionsDisabled;
            PopupTouchHandleDrawable.this.beginFadeIn();
        }
    }

    public interface PopupTouchHandleDrawableDelegate {
        View getParent();

        PositionObserver getParentPositionObserver();

        boolean isScrollInProgress();

        boolean onTouchHandleEvent(MotionEvent motionEvent);
    }

    /* renamed from: org.chromium.content.browser.input.PopupTouchHandleDrawable.1 */
    class C06111 implements Listener {
        C06111() {
        }

        public void onPositionChanged(int x, int y) {
            PopupTouchHandleDrawable.this.updateParentPosition(x, y);
        }
    }

    static {
        $assertionsDisabled = !PopupTouchHandleDrawable.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public PopupTouchHandleDrawable(PopupTouchHandleDrawableDelegate delegate) {
        boolean z = true;
        super(delegate.getParent().getContext());
        this.mTempScreenCoords = new int[2];
        this.mOrientation = 3;
        this.mContext = delegate.getParent().getContext();
        this.mDelegate = new WeakReference(delegate);
        this.mContainer = new PopupWindow(this.mContext, null, 16843464);
        this.mContainer.setSplitTouchEnabled(true);
        this.mContainer.setClippingEnabled($assertionsDisabled);
        this.mContainer.setAnimationStyle(0);
        this.mAlpha = 1.0f;
        if (getVisibility() != 0) {
            z = $assertionsDisabled;
        }
        this.mVisible = z;
        this.mParentPositionListener = new C06111();
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        PopupTouchHandleDrawableDelegate delegate = getDelegateAndHideIfNull();
        if (delegate == null) {
            return $assertionsDisabled;
        }
        delegate.getParent().getLocationOnScreen(this.mTempScreenCoords);
        float offsetX = (event.getRawX() - event.getX()) - ((float) this.mTempScreenCoords[0]);
        float offsetY = (event.getRawY() - event.getY()) - ((float) this.mTempScreenCoords[1]);
        MotionEvent offsetEvent = MotionEvent.obtainNoHistory(event);
        offsetEvent.offsetLocation(offsetX, offsetY);
        boolean handled = delegate.onTouchHandleEvent(offsetEvent);
        offsetEvent.recycle();
        return handled;
    }

    @CalledByNative
    private void setOrientation(int orientation) {
        if (!$assertionsDisabled && (orientation < 0 || orientation > 3)) {
            throw new AssertionError();
        } else if (this.mOrientation != orientation) {
            boolean hadValidOrientation = this.mOrientation != 3 ? true : $assertionsDisabled;
            this.mOrientation = orientation;
            int oldAdjustedPositionX = getAdjustedPositionX();
            int oldAdjustedPositionY = getAdjustedPositionY();
            switch (orientation) {
                case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                    this.mDrawable = HandleViewResources.getLeftHandleDrawable(this.mContext);
                    this.mHotspotX = ((float) (this.mDrawable.getIntrinsicWidth() * 3)) / 4.0f;
                    break;
                case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                    this.mDrawable = HandleViewResources.getCenterHandleDrawable(this.mContext);
                    this.mHotspotX = ((float) this.mDrawable.getIntrinsicWidth()) / 2.0f;
                    break;
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                    this.mDrawable = HandleViewResources.getRightHandleDrawable(this.mContext);
                    this.mHotspotX = ((float) this.mDrawable.getIntrinsicWidth()) / 4.0f;
                    break;
            }
            this.mHotspotY = 0.0f;
            if (hadValidOrientation) {
                setFocus((float) oldAdjustedPositionX, (float) oldAdjustedPositionY);
            }
            this.mDrawable.setAlpha((int) (255.0f * this.mAlpha));
            scheduleInvalidate();
        }
    }

    private void updateParentPosition(int parentPositionX, int parentPositionY) {
        if (this.mParentPositionX != parentPositionX || this.mParentPositionY != parentPositionY) {
            this.mParentPositionX = parentPositionX;
            this.mParentPositionY = parentPositionY;
            temporarilyHide();
        }
    }

    private int getContainerPositionX() {
        return this.mParentPositionX + this.mPositionX;
    }

    private int getContainerPositionY() {
        return this.mParentPositionY + this.mPositionY;
    }

    private void updatePosition() {
        this.mContainer.update(getContainerPositionX(), getContainerPositionY(), getRight() - getLeft(), getBottom() - getTop());
    }

    private void updateVisibility() {
        int newVisibility = (!this.mVisible || this.mTemporarilyHidden) ? 4 : 0;
        if (newVisibility != 0 || getVisibility() == 0 || this.mDelayVisibilityUpdateWAR) {
            this.mDelayVisibilityUpdateWAR = $assertionsDisabled;
            setVisibility(newVisibility);
            return;
        }
        this.mDelayVisibilityUpdateWAR = true;
        scheduleInvalidate();
    }

    private void updateAlpha() {
        if (this.mAlpha != 1.0f) {
            this.mAlpha = Math.min(1.0f, ((float) (AnimationUtils.currentAnimationTimeMillis() - this.mFadeStartTime)) / 200.0f);
            this.mDrawable.setAlpha((int) (255.0f * this.mAlpha));
            scheduleInvalidate();
        }
    }

    private void temporarilyHide() {
        this.mTemporarilyHidden = true;
        updateVisibility();
        rescheduleFadeIn();
    }

    private void doInvalidate() {
        if (this.mContainer.isShowing()) {
            updateVisibility();
            updatePosition();
            invalidate();
        }
    }

    private void scheduleInvalidate() {
        if (this.mInvalidationRunnable == null) {
            this.mInvalidationRunnable = new C03622();
        }
        if (!this.mHasPendingInvalidate) {
            this.mHasPendingInvalidate = true;
            ApiCompatibilityUtils.postOnAnimation(this, this.mInvalidationRunnable);
        }
    }

    private void rescheduleFadeIn() {
        if (this.mDeferredHandleFadeInRunnable == null) {
            this.mDeferredHandleFadeInRunnable = new C03633();
        }
        removeCallbacks(this.mDeferredHandleFadeInRunnable);
        ApiCompatibilityUtils.postOnAnimationDelayed(this, this.mDeferredHandleFadeInRunnable, 300);
    }

    private void beginFadeIn() {
        if (getVisibility() != 0) {
            this.mAlpha = 0.0f;
            this.mFadeStartTime = AnimationUtils.currentAnimationTimeMillis();
            doInvalidate();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mDrawable == null) {
            setMeasuredDimension(0, 0);
        } else {
            setMeasuredDimension(this.mDrawable.getIntrinsicWidth(), this.mDrawable.getIntrinsicHeight());
        }
    }

    protected void onDraw(Canvas c) {
        if (this.mDrawable != null) {
            updateAlpha();
            this.mDrawable.setBounds(0, 0, getRight() - getLeft(), getBottom() - getTop());
            this.mDrawable.draw(c);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        hide();
    }

    private int getAdjustedPositionX() {
        return this.mPositionX + Math.round(this.mHotspotX);
    }

    private int getAdjustedPositionY() {
        return this.mPositionY + Math.round(this.mHotspotY);
    }

    private PopupTouchHandleDrawableDelegate getDelegateAndHideIfNull() {
        PopupTouchHandleDrawableDelegate delegate = (PopupTouchHandleDrawableDelegate) this.mDelegate.get();
        if (delegate == null) {
            hide();
        }
        return delegate;
    }

    private boolean isScrollInProgress() {
        PopupTouchHandleDrawableDelegate delegate = getDelegateAndHideIfNull();
        if (delegate == null) {
            return $assertionsDisabled;
        }
        return delegate.isScrollInProgress();
    }

    @CalledByNative
    private void show() {
        if (!this.mContainer.isShowing()) {
            PopupTouchHandleDrawableDelegate delegate = getDelegateAndHideIfNull();
            if (delegate != null) {
                this.mParentPositionObserver = delegate.getParentPositionObserver();
                if ($assertionsDisabled || this.mParentPositionObserver != null) {
                    updateParentPosition(this.mParentPositionObserver.getPositionX(), this.mParentPositionObserver.getPositionY());
                    this.mParentPositionObserver.addListener(this.mParentPositionListener);
                    this.mContainer.setContentView(this);
                    this.mContainer.showAtLocation(delegate.getParent(), 0, getContainerPositionX(), getContainerPositionY());
                    return;
                }
                throw new AssertionError();
            }
        }
    }

    @CalledByNative
    private void hide() {
        this.mTemporarilyHidden = $assertionsDisabled;
        this.mAlpha = 1.0f;
        if (this.mDeferredHandleFadeInRunnable != null) {
            removeCallbacks(this.mDeferredHandleFadeInRunnable);
        }
        if (this.mContainer.isShowing()) {
            this.mContainer.dismiss();
        }
        if (this.mParentPositionObserver != null) {
            this.mParentPositionObserver.removeListener(this.mParentPositionListener);
            this.mParentPositionObserver = null;
        }
    }

    @CalledByNative
    private void setFocus(float focusX, float focusY) {
        int x = ((int) focusX) - Math.round(this.mHotspotX);
        int y = ((int) focusY) - Math.round(this.mHotspotY);
        if (this.mPositionX != x || this.mPositionY != y) {
            this.mPositionX = x;
            this.mPositionY = y;
            if (isScrollInProgress()) {
                temporarilyHide();
            } else {
                scheduleInvalidate();
            }
        }
    }

    @CalledByNative
    private void setVisible(boolean visible) {
        this.mVisible = visible;
        if (getVisibility() != (visible ? 0 : 4)) {
            scheduleInvalidate();
        }
    }

    @CalledByNative
    private int getPositionX() {
        return this.mPositionX;
    }

    @CalledByNative
    private int getPositionY() {
        return this.mPositionY;
    }

    @CalledByNative
    private int getVisibleWidth() {
        if (this.mDrawable == null) {
            return 0;
        }
        return this.mDrawable.getIntrinsicWidth();
    }

    @CalledByNative
    private int getVisibleHeight() {
        if (this.mDrawable == null) {
            return 0;
        }
        return this.mDrawable.getIntrinsicHeight();
    }
}
