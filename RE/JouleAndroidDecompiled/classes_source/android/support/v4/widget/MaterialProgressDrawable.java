package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import org.chromium.ui.base.PageTransition;

class MaterialProgressDrawable extends Drawable implements Animatable {
    private static final int ANIMATION_DURATION = 1332;
    private static final int ARROW_HEIGHT = 5;
    private static final int ARROW_HEIGHT_LARGE = 6;
    private static final float ARROW_OFFSET_ANGLE = 5.0f;
    private static final int ARROW_WIDTH = 10;
    private static final int ARROW_WIDTH_LARGE = 12;
    private static final float CENTER_RADIUS = 8.75f;
    private static final float CENTER_RADIUS_LARGE = 12.5f;
    private static final int CIRCLE_DIAMETER = 40;
    private static final int CIRCLE_DIAMETER_LARGE = 56;
    private static final float COLOR_START_DELAY_OFFSET = 0.75f;
    static final int DEFAULT = 1;
    private static final float END_TRIM_START_DELAY_OFFSET = 0.5f;
    private static final float FULL_ROTATION = 1080.0f;
    static final int LARGE = 0;
    private static final Interpolator LINEAR_INTERPOLATOR;
    private static final Interpolator MATERIAL_INTERPOLATOR;
    private static final float MAX_PROGRESS_ARC = 0.8f;
    private static final float NUM_POINTS = 5.0f;
    private static final float START_TRIM_DURATION_OFFSET = 0.5f;
    private static final float STROKE_WIDTH = 2.5f;
    private static final float STROKE_WIDTH_LARGE = 3.0f;
    private final int[] COLORS;
    private Animation mAnimation;
    private final ArrayList<Animation> mAnimators;
    private final Callback mCallback;
    boolean mFinishing;
    private double mHeight;
    private View mParent;
    private Resources mResources;
    private final Ring mRing;
    private float mRotation;
    private float mRotationCount;
    private double mWidth;

    /* renamed from: android.support.v4.widget.MaterialProgressDrawable.1 */
    class C01061 extends Animation {
        final /* synthetic */ Ring val$ring;

        C01061(Ring ring) {
            this.val$ring = ring;
        }

        public void applyTransformation(float interpolatedTime, Transformation t) {
            if (MaterialProgressDrawable.this.mFinishing) {
                MaterialProgressDrawable.this.applyFinishTranslation(interpolatedTime, this.val$ring);
                return;
            }
            float minProgressArc = MaterialProgressDrawable.this.getMinProgressArc(this.val$ring);
            float startingEndTrim = this.val$ring.getStartingEndTrim();
            float startingTrim = this.val$ring.getStartingStartTrim();
            float startingRotation = this.val$ring.getStartingRotation();
            MaterialProgressDrawable.this.updateRingColor(interpolatedTime, this.val$ring);
            if (interpolatedTime <= MaterialProgressDrawable.START_TRIM_DURATION_OFFSET) {
                this.val$ring.setStartTrim(startingTrim + ((MaterialProgressDrawable.MAX_PROGRESS_ARC - minProgressArc) * MaterialProgressDrawable.MATERIAL_INTERPOLATOR.getInterpolation(interpolatedTime / MaterialProgressDrawable.START_TRIM_DURATION_OFFSET)));
            }
            if (interpolatedTime > MaterialProgressDrawable.START_TRIM_DURATION_OFFSET) {
                this.val$ring.setEndTrim(startingEndTrim + (MaterialProgressDrawable.MATERIAL_INTERPOLATOR.getInterpolation((interpolatedTime - MaterialProgressDrawable.START_TRIM_DURATION_OFFSET) / MaterialProgressDrawable.START_TRIM_DURATION_OFFSET) * (MaterialProgressDrawable.MAX_PROGRESS_ARC - minProgressArc)));
            }
            this.val$ring.setRotation(startingRotation + (0.25f * interpolatedTime));
            MaterialProgressDrawable.this.setRotation((216.0f * interpolatedTime) + (MaterialProgressDrawable.FULL_ROTATION * (MaterialProgressDrawable.this.mRotationCount / MaterialProgressDrawable.NUM_POINTS)));
        }
    }

    /* renamed from: android.support.v4.widget.MaterialProgressDrawable.2 */
    class C01072 implements AnimationListener {
        final /* synthetic */ Ring val$ring;

        C01072(Ring ring) {
            this.val$ring = ring;
        }

        public void onAnimationStart(Animation animation) {
            MaterialProgressDrawable.this.mRotationCount = 0.0f;
        }

        public void onAnimationEnd(Animation animation) {
        }

        public void onAnimationRepeat(Animation animation) {
            this.val$ring.storeOriginals();
            this.val$ring.goToNextColor();
            this.val$ring.setStartTrim(this.val$ring.getEndTrim());
            if (MaterialProgressDrawable.this.mFinishing) {
                MaterialProgressDrawable.this.mFinishing = false;
                animation.setDuration(1332);
                this.val$ring.setShowArrow(false);
                return;
            }
            MaterialProgressDrawable.this.mRotationCount = (MaterialProgressDrawable.this.mRotationCount + 1.0f) % MaterialProgressDrawable.NUM_POINTS;
        }
    }

    /* renamed from: android.support.v4.widget.MaterialProgressDrawable.3 */
    class C01083 implements Callback {
        C01083() {
        }

        public void invalidateDrawable(Drawable d) {
            MaterialProgressDrawable.this.invalidateSelf();
        }

        public void scheduleDrawable(Drawable d, Runnable what, long when) {
            MaterialProgressDrawable.this.scheduleSelf(what, when);
        }

        public void unscheduleDrawable(Drawable d, Runnable what) {
            MaterialProgressDrawable.this.unscheduleSelf(what);
        }
    }

    @Retention(RetentionPolicy.CLASS)
    public @interface ProgressDrawableSize {
    }

    private static class Ring {
        private int mAlpha;
        private Path mArrow;
        private int mArrowHeight;
        private final Paint mArrowPaint;
        private float mArrowScale;
        private int mArrowWidth;
        private int mBackgroundColor;
        private final Callback mCallback;
        private final Paint mCirclePaint;
        private int mColorIndex;
        private int[] mColors;
        private int mCurrentColor;
        private float mEndTrim;
        private final Paint mPaint;
        private double mRingCenterRadius;
        private float mRotation;
        private boolean mShowArrow;
        private float mStartTrim;
        private float mStartingEndTrim;
        private float mStartingRotation;
        private float mStartingStartTrim;
        private float mStrokeInset;
        private float mStrokeWidth;
        private final RectF mTempBounds;

        public Ring(Callback callback) {
            this.mTempBounds = new RectF();
            this.mPaint = new Paint();
            this.mArrowPaint = new Paint();
            this.mStartTrim = 0.0f;
            this.mEndTrim = 0.0f;
            this.mRotation = 0.0f;
            this.mStrokeWidth = MaterialProgressDrawable.NUM_POINTS;
            this.mStrokeInset = MaterialProgressDrawable.STROKE_WIDTH;
            this.mCirclePaint = new Paint(MaterialProgressDrawable.DEFAULT);
            this.mCallback = callback;
            this.mPaint.setStrokeCap(Cap.SQUARE);
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStyle(Style.STROKE);
            this.mArrowPaint.setStyle(Style.FILL);
            this.mArrowPaint.setAntiAlias(true);
        }

        public void setBackgroundColor(int color) {
            this.mBackgroundColor = color;
        }

        public void setArrowDimensions(float width, float height) {
            this.mArrowWidth = (int) width;
            this.mArrowHeight = (int) height;
        }

        public void draw(Canvas c, Rect bounds) {
            RectF arcBounds = this.mTempBounds;
            arcBounds.set(bounds);
            arcBounds.inset(this.mStrokeInset, this.mStrokeInset);
            float startAngle = (this.mStartTrim + this.mRotation) * 360.0f;
            float sweepAngle = ((this.mEndTrim + this.mRotation) * 360.0f) - startAngle;
            this.mPaint.setColor(this.mCurrentColor);
            c.drawArc(arcBounds, startAngle, sweepAngle, false, this.mPaint);
            drawTriangle(c, startAngle, sweepAngle, bounds);
            if (this.mAlpha < PageTransition.CORE_MASK) {
                this.mCirclePaint.setColor(this.mBackgroundColor);
                this.mCirclePaint.setAlpha(255 - this.mAlpha);
                c.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(), (float) (bounds.width() / 2), this.mCirclePaint);
            }
        }

        private void drawTriangle(Canvas c, float startAngle, float sweepAngle, Rect bounds) {
            if (this.mShowArrow) {
                if (this.mArrow == null) {
                    this.mArrow = new Path();
                    this.mArrow.setFillType(FillType.EVEN_ODD);
                } else {
                    this.mArrow.reset();
                }
                float inset = ((float) (((int) this.mStrokeInset) / 2)) * this.mArrowScale;
                float x = (float) ((this.mRingCenterRadius * Math.cos(0.0d)) + ((double) bounds.exactCenterX()));
                float y = (float) ((this.mRingCenterRadius * Math.sin(0.0d)) + ((double) bounds.exactCenterY()));
                this.mArrow.moveTo(0.0f, 0.0f);
                this.mArrow.lineTo(((float) this.mArrowWidth) * this.mArrowScale, 0.0f);
                this.mArrow.lineTo((((float) this.mArrowWidth) * this.mArrowScale) / 2.0f, ((float) this.mArrowHeight) * this.mArrowScale);
                this.mArrow.offset(x - inset, y);
                this.mArrow.close();
                this.mArrowPaint.setColor(this.mCurrentColor);
                c.rotate((startAngle + sweepAngle) - MaterialProgressDrawable.NUM_POINTS, bounds.exactCenterX(), bounds.exactCenterY());
                c.drawPath(this.mArrow, this.mArrowPaint);
            }
        }

        public void setColors(@NonNull int[] colors) {
            this.mColors = colors;
            setColorIndex(MaterialProgressDrawable.LARGE);
        }

        public void setColor(int color) {
            this.mCurrentColor = color;
        }

        public void setColorIndex(int index) {
            this.mColorIndex = index;
            this.mCurrentColor = this.mColors[this.mColorIndex];
        }

        public int getNextColor() {
            return this.mColors[getNextColorIndex()];
        }

        private int getNextColorIndex() {
            return (this.mColorIndex + MaterialProgressDrawable.DEFAULT) % this.mColors.length;
        }

        public void goToNextColor() {
            setColorIndex(getNextColorIndex());
        }

        public void setColorFilter(ColorFilter filter) {
            this.mPaint.setColorFilter(filter);
            invalidateSelf();
        }

        public void setAlpha(int alpha) {
            this.mAlpha = alpha;
        }

        public int getAlpha() {
            return this.mAlpha;
        }

        public void setStrokeWidth(float strokeWidth) {
            this.mStrokeWidth = strokeWidth;
            this.mPaint.setStrokeWidth(strokeWidth);
            invalidateSelf();
        }

        public float getStrokeWidth() {
            return this.mStrokeWidth;
        }

        public void setStartTrim(float startTrim) {
            this.mStartTrim = startTrim;
            invalidateSelf();
        }

        public float getStartTrim() {
            return this.mStartTrim;
        }

        public float getStartingStartTrim() {
            return this.mStartingStartTrim;
        }

        public float getStartingEndTrim() {
            return this.mStartingEndTrim;
        }

        public int getStartingColor() {
            return this.mColors[this.mColorIndex];
        }

        public void setEndTrim(float endTrim) {
            this.mEndTrim = endTrim;
            invalidateSelf();
        }

        public float getEndTrim() {
            return this.mEndTrim;
        }

        public void setRotation(float rotation) {
            this.mRotation = rotation;
            invalidateSelf();
        }

        public float getRotation() {
            return this.mRotation;
        }

        public void setInsets(int width, int height) {
            float insets;
            float minEdge = (float) Math.min(width, height);
            if (this.mRingCenterRadius <= 0.0d || minEdge < 0.0f) {
                insets = (float) Math.ceil((double) (this.mStrokeWidth / 2.0f));
            } else {
                insets = (float) (((double) (minEdge / 2.0f)) - this.mRingCenterRadius);
            }
            this.mStrokeInset = insets;
        }

        public float getInsets() {
            return this.mStrokeInset;
        }

        public void setCenterRadius(double centerRadius) {
            this.mRingCenterRadius = centerRadius;
        }

        public double getCenterRadius() {
            return this.mRingCenterRadius;
        }

        public void setShowArrow(boolean show) {
            if (this.mShowArrow != show) {
                this.mShowArrow = show;
                invalidateSelf();
            }
        }

        public void setArrowScale(float scale) {
            if (scale != this.mArrowScale) {
                this.mArrowScale = scale;
                invalidateSelf();
            }
        }

        public float getStartingRotation() {
            return this.mStartingRotation;
        }

        public void storeOriginals() {
            this.mStartingStartTrim = this.mStartTrim;
            this.mStartingEndTrim = this.mEndTrim;
            this.mStartingRotation = this.mRotation;
        }

        public void resetOriginals() {
            this.mStartingStartTrim = 0.0f;
            this.mStartingEndTrim = 0.0f;
            this.mStartingRotation = 0.0f;
            setStartTrim(0.0f);
            setEndTrim(0.0f);
            setRotation(0.0f);
        }

        private void invalidateSelf() {
            this.mCallback.invalidateDrawable(null);
        }
    }

    static {
        LINEAR_INTERPOLATOR = new LinearInterpolator();
        MATERIAL_INTERPOLATOR = new FastOutSlowInInterpolator();
    }

    public MaterialProgressDrawable(Context context, View parent) {
        int[] iArr = new int[DEFAULT];
        iArr[LARGE] = ViewCompat.MEASURED_STATE_MASK;
        this.COLORS = iArr;
        this.mAnimators = new ArrayList();
        this.mCallback = new C01083();
        this.mParent = parent;
        this.mResources = context.getResources();
        this.mRing = new Ring(this.mCallback);
        this.mRing.setColors(this.COLORS);
        updateSizes(DEFAULT);
        setupAnimators();
    }

    private void setSizeParameters(double progressCircleWidth, double progressCircleHeight, double centerRadius, double strokeWidth, float arrowWidth, float arrowHeight) {
        Ring ring = this.mRing;
        float screenDensity = this.mResources.getDisplayMetrics().density;
        this.mWidth = ((double) screenDensity) * progressCircleWidth;
        this.mHeight = ((double) screenDensity) * progressCircleHeight;
        ring.setStrokeWidth(((float) strokeWidth) * screenDensity);
        ring.setCenterRadius(((double) screenDensity) * centerRadius);
        ring.setColorIndex(LARGE);
        ring.setArrowDimensions(arrowWidth * screenDensity, arrowHeight * screenDensity);
        ring.setInsets((int) this.mWidth, (int) this.mHeight);
    }

    public void updateSizes(@ProgressDrawableSize int size) {
        if (size == 0) {
            setSizeParameters(56.0d, 56.0d, 12.5d, 3.0d, 12.0f, 6.0f);
        } else {
            setSizeParameters(40.0d, 40.0d, 8.75d, 2.5d, 10.0f, NUM_POINTS);
        }
    }

    public void showArrow(boolean show) {
        this.mRing.setShowArrow(show);
    }

    public void setArrowScale(float scale) {
        this.mRing.setArrowScale(scale);
    }

    public void setStartEndTrim(float startAngle, float endAngle) {
        this.mRing.setStartTrim(startAngle);
        this.mRing.setEndTrim(endAngle);
    }

    public void setProgressRotation(float rotation) {
        this.mRing.setRotation(rotation);
    }

    public void setBackgroundColor(int color) {
        this.mRing.setBackgroundColor(color);
    }

    public void setColorSchemeColors(int... colors) {
        this.mRing.setColors(colors);
        this.mRing.setColorIndex(LARGE);
    }

    public int getIntrinsicHeight() {
        return (int) this.mHeight;
    }

    public int getIntrinsicWidth() {
        return (int) this.mWidth;
    }

    public void draw(Canvas c) {
        Rect bounds = getBounds();
        int saveCount = c.save();
        c.rotate(this.mRotation, bounds.exactCenterX(), bounds.exactCenterY());
        this.mRing.draw(c, bounds);
        c.restoreToCount(saveCount);
    }

    public void setAlpha(int alpha) {
        this.mRing.setAlpha(alpha);
    }

    public int getAlpha() {
        return this.mRing.getAlpha();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mRing.setColorFilter(colorFilter);
    }

    void setRotation(float rotation) {
        this.mRotation = rotation;
        invalidateSelf();
    }

    private float getRotation() {
        return this.mRotation;
    }

    public int getOpacity() {
        return -3;
    }

    public boolean isRunning() {
        ArrayList<Animation> animators = this.mAnimators;
        int N = animators.size();
        for (int i = LARGE; i < N; i += DEFAULT) {
            Animation animator = (Animation) animators.get(i);
            if (animator.hasStarted() && !animator.hasEnded()) {
                return true;
            }
        }
        return false;
    }

    public void start() {
        this.mAnimation.reset();
        this.mRing.storeOriginals();
        if (this.mRing.getEndTrim() != this.mRing.getStartTrim()) {
            this.mFinishing = true;
            this.mAnimation.setDuration(666);
            this.mParent.startAnimation(this.mAnimation);
            return;
        }
        this.mRing.setColorIndex(LARGE);
        this.mRing.resetOriginals();
        this.mAnimation.setDuration(1332);
        this.mParent.startAnimation(this.mAnimation);
    }

    public void stop() {
        this.mParent.clearAnimation();
        setRotation(0.0f);
        this.mRing.setShowArrow(false);
        this.mRing.setColorIndex(LARGE);
        this.mRing.resetOriginals();
    }

    private float getMinProgressArc(Ring ring) {
        return (float) Math.toRadians(((double) ring.getStrokeWidth()) / (6.283185307179586d * ring.getCenterRadius()));
    }

    private int evaluateColorChange(float fraction, int startValue, int endValue) {
        int startInt = Integer.valueOf(startValue).intValue();
        int startA = (startInt >> 24) & PageTransition.CORE_MASK;
        int startR = (startInt >> 16) & PageTransition.CORE_MASK;
        int startG = (startInt >> 8) & PageTransition.CORE_MASK;
        int startB = startInt & PageTransition.CORE_MASK;
        int endInt = Integer.valueOf(endValue).intValue();
        return ((((((int) (((float) (((endInt >> 24) & PageTransition.CORE_MASK) - startA)) * fraction)) + startA) << 24) | ((((int) (((float) (((endInt >> 16) & PageTransition.CORE_MASK) - startR)) * fraction)) + startR) << 16)) | ((((int) (((float) (((endInt >> 8) & PageTransition.CORE_MASK) - startG)) * fraction)) + startG) << 8)) | (((int) (((float) ((endInt & PageTransition.CORE_MASK) - startB)) * fraction)) + startB);
    }

    private void updateRingColor(float interpolatedTime, Ring ring) {
        if (interpolatedTime > COLOR_START_DELAY_OFFSET) {
            ring.setColor(evaluateColorChange((interpolatedTime - COLOR_START_DELAY_OFFSET) / 0.25f, ring.getStartingColor(), ring.getNextColor()));
        }
    }

    private void applyFinishTranslation(float interpolatedTime, Ring ring) {
        updateRingColor(interpolatedTime, ring);
        float targetRotation = (float) (Math.floor((double) (ring.getStartingRotation() / MAX_PROGRESS_ARC)) + 1.0d);
        ring.setStartTrim(ring.getStartingStartTrim() + (((ring.getStartingEndTrim() - getMinProgressArc(ring)) - ring.getStartingStartTrim()) * interpolatedTime));
        ring.setEndTrim(ring.getStartingEndTrim());
        ring.setRotation(ring.getStartingRotation() + ((targetRotation - ring.getStartingRotation()) * interpolatedTime));
    }

    private void setupAnimators() {
        Ring ring = this.mRing;
        Animation animation = new C01061(ring);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(DEFAULT);
        animation.setInterpolator(LINEAR_INTERPOLATOR);
        animation.setAnimationListener(new C01072(ring));
        this.mAnimation = animation;
    }
}
