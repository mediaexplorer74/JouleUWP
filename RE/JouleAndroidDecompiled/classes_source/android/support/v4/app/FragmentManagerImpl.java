package android.support.v4.app;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment.SavedState;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.media.TransportMediator;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.LogWriter;
import android.support.v4.view.InputDeviceCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* compiled from: FragmentManager */
final class FragmentManagerImpl extends FragmentManager implements LayoutInflaterFactory {
    static final Interpolator ACCELERATE_CUBIC;
    static final Interpolator ACCELERATE_QUINT;
    static final int ANIM_DUR = 220;
    public static final int ANIM_STYLE_CLOSE_ENTER = 3;
    public static final int ANIM_STYLE_CLOSE_EXIT = 4;
    public static final int ANIM_STYLE_FADE_ENTER = 5;
    public static final int ANIM_STYLE_FADE_EXIT = 6;
    public static final int ANIM_STYLE_OPEN_ENTER = 1;
    public static final int ANIM_STYLE_OPEN_EXIT = 2;
    static boolean DEBUG = false;
    static final Interpolator DECELERATE_CUBIC;
    static final Interpolator DECELERATE_QUINT;
    static final boolean HONEYCOMB;
    static final String TAG = "FragmentManager";
    static final String TARGET_REQUEST_CODE_STATE_TAG = "android:target_req_state";
    static final String TARGET_STATE_TAG = "android:target_state";
    static final String USER_VISIBLE_HINT_TAG = "android:user_visible_hint";
    static final String VIEW_STATE_TAG = "android:view_state";
    static Field sAnimationListenerField;
    ArrayList<Fragment> mActive;
    ArrayList<Fragment> mAdded;
    ArrayList<Integer> mAvailBackStackIndices;
    ArrayList<Integer> mAvailIndices;
    ArrayList<BackStackRecord> mBackStack;
    ArrayList<OnBackStackChangedListener> mBackStackChangeListeners;
    ArrayList<BackStackRecord> mBackStackIndices;
    FragmentContainer mContainer;
    FragmentController mController;
    ArrayList<Fragment> mCreatedMenus;
    int mCurState;
    boolean mDestroyed;
    Runnable mExecCommit;
    boolean mExecutingActions;
    boolean mHavePendingDeferredStart;
    FragmentHostCallback mHost;
    boolean mNeedMenuInvalidate;
    String mNoTransactionsBecause;
    Fragment mParent;
    ArrayList<Runnable> mPendingActions;
    SparseArray<Parcelable> mStateArray;
    Bundle mStateBundle;
    boolean mStateSaved;
    Runnable[] mTmpActions;

    /* renamed from: android.support.v4.app.FragmentManagerImpl.1 */
    class FragmentManager implements Runnable {
        FragmentManager() {
        }

        public void run() {
            FragmentManagerImpl.this.execPendingActions();
        }
    }

    /* renamed from: android.support.v4.app.FragmentManagerImpl.2 */
    class FragmentManager implements Runnable {
        FragmentManager() {
        }

        public void run() {
            FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mHost.getHandler(), null, -1, 0);
        }
    }

    /* renamed from: android.support.v4.app.FragmentManagerImpl.3 */
    class FragmentManager implements Runnable {
        final /* synthetic */ int val$flags;
        final /* synthetic */ String val$name;

        FragmentManager(String str, int i) {
            this.val$name = str;
            this.val$flags = i;
        }

        public void run() {
            FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mHost.getHandler(), this.val$name, -1, this.val$flags);
        }
    }

    /* renamed from: android.support.v4.app.FragmentManagerImpl.4 */
    class FragmentManager implements Runnable {
        final /* synthetic */ int val$flags;
        final /* synthetic */ int val$id;

        FragmentManager(int i, int i2) {
            this.val$id = i;
            this.val$flags = i2;
        }

        public void run() {
            FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mHost.getHandler(), null, this.val$id, this.val$flags);
        }
    }

    /* compiled from: FragmentManager */
    static class AnimateOnHWLayerIfNeededListener implements AnimationListener {
        private AnimationListener mOrignalListener;
        private boolean mShouldRunOnHWLayer;
        private View mView;

        /* renamed from: android.support.v4.app.FragmentManagerImpl.AnimateOnHWLayerIfNeededListener.1 */
        class FragmentManager implements Runnable {
            FragmentManager() {
            }

            public void run() {
                ViewCompat.setLayerType(AnimateOnHWLayerIfNeededListener.this.mView, FragmentManagerImpl.ANIM_STYLE_OPEN_EXIT, null);
            }
        }

        /* renamed from: android.support.v4.app.FragmentManagerImpl.AnimateOnHWLayerIfNeededListener.2 */
        class FragmentManager implements Runnable {
            FragmentManager() {
            }

            public void run() {
                ViewCompat.setLayerType(AnimateOnHWLayerIfNeededListener.this.mView, 0, null);
            }
        }

        public AnimateOnHWLayerIfNeededListener(View v, Animation anim) {
            this.mOrignalListener = null;
            this.mShouldRunOnHWLayer = FragmentManagerImpl.HONEYCOMB;
            this.mView = null;
            if (v != null && anim != null) {
                this.mView = v;
            }
        }

        public AnimateOnHWLayerIfNeededListener(View v, Animation anim, AnimationListener listener) {
            this.mOrignalListener = null;
            this.mShouldRunOnHWLayer = FragmentManagerImpl.HONEYCOMB;
            this.mView = null;
            if (v != null && anim != null) {
                this.mOrignalListener = listener;
                this.mView = v;
            }
        }

        @CallSuper
        public void onAnimationStart(Animation animation) {
            if (this.mView != null) {
                this.mShouldRunOnHWLayer = FragmentManagerImpl.shouldRunOnHWLayer(this.mView, animation);
                if (this.mShouldRunOnHWLayer) {
                    this.mView.post(new FragmentManager());
                }
            }
            if (this.mOrignalListener != null) {
                this.mOrignalListener.onAnimationStart(animation);
            }
        }

        @CallSuper
        public void onAnimationEnd(Animation animation) {
            if (this.mView != null && this.mShouldRunOnHWLayer) {
                this.mView.post(new FragmentManager());
            }
            if (this.mOrignalListener != null) {
                this.mOrignalListener.onAnimationEnd(animation);
            }
        }

        public void onAnimationRepeat(Animation animation) {
            if (this.mOrignalListener != null) {
                this.mOrignalListener.onAnimationRepeat(animation);
            }
        }
    }

    /* compiled from: FragmentManager */
    static class FragmentTag {
        public static final int[] Fragment;
        public static final int Fragment_id = 1;
        public static final int Fragment_name = 0;
        public static final int Fragment_tag = 2;

        FragmentTag() {
        }

        static {
            Fragment = new int[]{16842755, 16842960, 16842961};
        }
    }

    /* renamed from: android.support.v4.app.FragmentManagerImpl.5 */
    class FragmentManager extends AnimateOnHWLayerIfNeededListener {
        final /* synthetic */ Fragment val$fragment;

        FragmentManager(View v, Animation anim, Fragment fragment) {
            this.val$fragment = fragment;
            super(v, anim);
        }

        public void onAnimationEnd(Animation animation) {
            super.onAnimationEnd(animation);
            if (this.val$fragment.mAnimatingAway != null) {
                this.val$fragment.mAnimatingAway = null;
                FragmentManagerImpl.this.moveToState(this.val$fragment, this.val$fragment.mStateAfterAnimating, 0, 0, FragmentManagerImpl.HONEYCOMB);
            }
        }
    }

    FragmentManagerImpl() {
        this.mCurState = 0;
        this.mStateBundle = null;
        this.mStateArray = null;
        this.mExecCommit = new FragmentManager();
    }

    static {
        boolean z = HONEYCOMB;
        DEBUG = HONEYCOMB;
        if (VERSION.SDK_INT >= 11) {
            z = true;
        }
        HONEYCOMB = z;
        sAnimationListenerField = null;
        DECELERATE_QUINT = new DecelerateInterpolator(2.5f);
        DECELERATE_CUBIC = new DecelerateInterpolator(1.5f);
        ACCELERATE_QUINT = new AccelerateInterpolator(2.5f);
        ACCELERATE_CUBIC = new AccelerateInterpolator(1.5f);
    }

    static boolean modifiesAlpha(Animation anim) {
        if (anim instanceof AlphaAnimation) {
            return true;
        }
        if (anim instanceof AnimationSet) {
            List<Animation> anims = ((AnimationSet) anim).getAnimations();
            for (int i = 0; i < anims.size(); i += ANIM_STYLE_OPEN_ENTER) {
                if (anims.get(i) instanceof AlphaAnimation) {
                    return true;
                }
            }
        }
        return HONEYCOMB;
    }

    static boolean shouldRunOnHWLayer(View v, Animation anim) {
        return (VERSION.SDK_INT >= 19 && ViewCompat.getLayerType(v) == 0 && ViewCompat.hasOverlappingRendering(v) && modifiesAlpha(anim)) ? true : HONEYCOMB;
    }

    private void throwException(RuntimeException ex) {
        Log.e(TAG, ex.getMessage());
        Log.e(TAG, "Activity state:");
        PrintWriter pw = new PrintWriter(new LogWriter(TAG));
        if (this.mHost != null) {
            try {
                this.mHost.onDump("  ", null, pw, new String[0]);
            } catch (Exception e) {
                Log.e(TAG, "Failed dumping state", e);
            }
        } else {
            try {
                dump("  ", null, pw, new String[0]);
            } catch (Exception e2) {
                Log.e(TAG, "Failed dumping state", e2);
            }
        }
        throw ex;
    }

    public FragmentTransaction beginTransaction() {
        return new BackStackRecord(this);
    }

    public boolean executePendingTransactions() {
        return execPendingActions();
    }

    public void popBackStack() {
        enqueueAction(new FragmentManager(), HONEYCOMB);
    }

    public boolean popBackStackImmediate() {
        checkStateLoss();
        executePendingTransactions();
        return popBackStackState(this.mHost.getHandler(), null, -1, 0);
    }

    public void popBackStack(String name, int flags) {
        enqueueAction(new FragmentManager(name, flags), HONEYCOMB);
    }

    public boolean popBackStackImmediate(String name, int flags) {
        checkStateLoss();
        executePendingTransactions();
        return popBackStackState(this.mHost.getHandler(), name, -1, flags);
    }

    public void popBackStack(int id, int flags) {
        if (id < 0) {
            throw new IllegalArgumentException("Bad id: " + id);
        }
        enqueueAction(new FragmentManager(id, flags), HONEYCOMB);
    }

    public boolean popBackStackImmediate(int id, int flags) {
        checkStateLoss();
        executePendingTransactions();
        if (id >= 0) {
            return popBackStackState(this.mHost.getHandler(), null, id, flags);
        }
        throw new IllegalArgumentException("Bad id: " + id);
    }

    public int getBackStackEntryCount() {
        return this.mBackStack != null ? this.mBackStack.size() : 0;
    }

    public BackStackEntry getBackStackEntryAt(int index) {
        return (BackStackEntry) this.mBackStack.get(index);
    }

    public void addOnBackStackChangedListener(OnBackStackChangedListener listener) {
        if (this.mBackStackChangeListeners == null) {
            this.mBackStackChangeListeners = new ArrayList();
        }
        this.mBackStackChangeListeners.add(listener);
    }

    public void removeOnBackStackChangedListener(OnBackStackChangedListener listener) {
        if (this.mBackStackChangeListeners != null) {
            this.mBackStackChangeListeners.remove(listener);
        }
    }

    public void putFragment(Bundle bundle, String key, Fragment fragment) {
        if (fragment.mIndex < 0) {
            throwException(new IllegalStateException("Fragment " + fragment + " is not currently in the FragmentManager"));
        }
        bundle.putInt(key, fragment.mIndex);
    }

    public Fragment getFragment(Bundle bundle, String key) {
        int index = bundle.getInt(key, -1);
        if (index == -1) {
            return null;
        }
        if (index >= this.mActive.size()) {
            throwException(new IllegalStateException("Fragment no longer exists for key " + key + ": index " + index));
        }
        Fragment f = (Fragment) this.mActive.get(index);
        if (f != null) {
            return f;
        }
        throwException(new IllegalStateException("Fragment no longer exists for key " + key + ": index " + index));
        return f;
    }

    public List<Fragment> getFragments() {
        return this.mActive;
    }

    public SavedState saveFragmentInstanceState(Fragment fragment) {
        if (fragment.mIndex < 0) {
            throwException(new IllegalStateException("Fragment " + fragment + " is not currently in the FragmentManager"));
        }
        if (fragment.mState <= 0) {
            return null;
        }
        Bundle result = saveFragmentBasicState(fragment);
        if (result != null) {
            return new SavedState(result);
        }
        return null;
    }

    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        sb.append("FragmentManager{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" in ");
        if (this.mParent != null) {
            DebugUtils.buildShortClassTag(this.mParent, sb);
        } else {
            DebugUtils.buildShortClassTag(this.mHost, sb);
        }
        sb.append("}}");
        return sb.toString();
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        int N;
        int i;
        Fragment f;
        String innerPrefix = prefix + "    ";
        if (this.mActive != null) {
            N = this.mActive.size();
            if (N > 0) {
                writer.print(prefix);
                writer.print("Active Fragments in ");
                writer.print(Integer.toHexString(System.identityHashCode(this)));
                writer.println(":");
                for (i = 0; i < N; i += ANIM_STYLE_OPEN_ENTER) {
                    f = (Fragment) this.mActive.get(i);
                    writer.print(prefix);
                    writer.print("  #");
                    writer.print(i);
                    writer.print(": ");
                    writer.println(f);
                    if (f != null) {
                        f.dump(innerPrefix, fd, writer, args);
                    }
                }
            }
        }
        if (this.mAdded != null) {
            N = this.mAdded.size();
            if (N > 0) {
                writer.print(prefix);
                writer.println("Added Fragments:");
                for (i = 0; i < N; i += ANIM_STYLE_OPEN_ENTER) {
                    f = (Fragment) this.mAdded.get(i);
                    writer.print(prefix);
                    writer.print("  #");
                    writer.print(i);
                    writer.print(": ");
                    writer.println(f.toString());
                }
            }
        }
        if (this.mCreatedMenus != null) {
            N = this.mCreatedMenus.size();
            if (N > 0) {
                writer.print(prefix);
                writer.println("Fragments Created Menus:");
                for (i = 0; i < N; i += ANIM_STYLE_OPEN_ENTER) {
                    f = (Fragment) this.mCreatedMenus.get(i);
                    writer.print(prefix);
                    writer.print("  #");
                    writer.print(i);
                    writer.print(": ");
                    writer.println(f.toString());
                }
            }
        }
        if (this.mBackStack != null) {
            N = this.mBackStack.size();
            if (N > 0) {
                writer.print(prefix);
                writer.println("Back Stack:");
                for (i = 0; i < N; i += ANIM_STYLE_OPEN_ENTER) {
                    BackStackRecord bs = (BackStackRecord) this.mBackStack.get(i);
                    writer.print(prefix);
                    writer.print("  #");
                    writer.print(i);
                    writer.print(": ");
                    writer.println(bs.toString());
                    bs.dump(innerPrefix, fd, writer, args);
                }
            }
        }
        synchronized (this) {
            if (this.mBackStackIndices != null) {
                N = this.mBackStackIndices.size();
                if (N > 0) {
                    writer.print(prefix);
                    writer.println("Back Stack Indices:");
                    for (i = 0; i < N; i += ANIM_STYLE_OPEN_ENTER) {
                        bs = (BackStackRecord) this.mBackStackIndices.get(i);
                        writer.print(prefix);
                        writer.print("  #");
                        writer.print(i);
                        writer.print(": ");
                        writer.println(bs);
                    }
                }
            }
            if (this.mAvailBackStackIndices != null && this.mAvailBackStackIndices.size() > 0) {
                writer.print(prefix);
                writer.print("mAvailBackStackIndices: ");
                writer.println(Arrays.toString(this.mAvailBackStackIndices.toArray()));
            }
        }
        if (this.mPendingActions != null) {
            N = this.mPendingActions.size();
            if (N > 0) {
                writer.print(prefix);
                writer.println("Pending Actions:");
                for (i = 0; i < N; i += ANIM_STYLE_OPEN_ENTER) {
                    Runnable r = (Runnable) this.mPendingActions.get(i);
                    writer.print(prefix);
                    writer.print("  #");
                    writer.print(i);
                    writer.print(": ");
                    writer.println(r);
                }
            }
        }
        writer.print(prefix);
        writer.println("FragmentManager misc state:");
        writer.print(prefix);
        writer.print("  mHost=");
        writer.println(this.mHost);
        writer.print(prefix);
        writer.print("  mContainer=");
        writer.println(this.mContainer);
        if (this.mParent != null) {
            writer.print(prefix);
            writer.print("  mParent=");
            writer.println(this.mParent);
        }
        writer.print(prefix);
        writer.print("  mCurState=");
        writer.print(this.mCurState);
        writer.print(" mStateSaved=");
        writer.print(this.mStateSaved);
        writer.print(" mDestroyed=");
        writer.println(this.mDestroyed);
        if (this.mNeedMenuInvalidate) {
            writer.print(prefix);
            writer.print("  mNeedMenuInvalidate=");
            writer.println(this.mNeedMenuInvalidate);
        }
        if (this.mNoTransactionsBecause != null) {
            writer.print(prefix);
            writer.print("  mNoTransactionsBecause=");
            writer.println(this.mNoTransactionsBecause);
        }
        if (this.mAvailIndices != null && this.mAvailIndices.size() > 0) {
            writer.print(prefix);
            writer.print("  mAvailIndices: ");
            writer.println(Arrays.toString(this.mAvailIndices.toArray()));
        }
    }

    static Animation makeOpenCloseAnimation(Context context, float startScale, float endScale, float startAlpha, float endAlpha) {
        AnimationSet set = new AnimationSet(HONEYCOMB);
        ScaleAnimation scale = new ScaleAnimation(startScale, endScale, startScale, endScale, ANIM_STYLE_OPEN_ENTER, 0.5f, ANIM_STYLE_OPEN_ENTER, 0.5f);
        scale.setInterpolator(DECELERATE_QUINT);
        scale.setDuration(220);
        set.addAnimation(scale);
        AlphaAnimation alpha = new AlphaAnimation(startAlpha, endAlpha);
        alpha.setInterpolator(DECELERATE_CUBIC);
        alpha.setDuration(220);
        set.addAnimation(alpha);
        return set;
    }

    static Animation makeFadeAnimation(Context context, float start, float end) {
        AlphaAnimation anim = new AlphaAnimation(start, end);
        anim.setInterpolator(DECELERATE_CUBIC);
        anim.setDuration(220);
        return anim;
    }

    Animation loadAnimation(Fragment fragment, int transit, boolean enter, int transitionStyle) {
        Animation animObj = fragment.onCreateAnimation(transit, enter, fragment.mNextAnim);
        if (animObj != null) {
            return animObj;
        }
        if (fragment.mNextAnim != 0) {
            Animation anim = AnimationUtils.loadAnimation(this.mHost.getContext(), fragment.mNextAnim);
            if (anim != null) {
                return anim;
            }
        }
        if (transit == 0) {
            return null;
        }
        int styleIndex = transitToStyleIndex(transit, enter);
        if (styleIndex < 0) {
            return null;
        }
        switch (styleIndex) {
            case ANIM_STYLE_OPEN_ENTER /*1*/:
                return makeOpenCloseAnimation(this.mHost.getContext(), 1.125f, 1.0f, 0.0f, 1.0f);
            case ANIM_STYLE_OPEN_EXIT /*2*/:
                return makeOpenCloseAnimation(this.mHost.getContext(), 1.0f, 0.975f, 1.0f, 0.0f);
            case ANIM_STYLE_CLOSE_ENTER /*3*/:
                return makeOpenCloseAnimation(this.mHost.getContext(), 0.975f, 1.0f, 0.0f, 1.0f);
            case ANIM_STYLE_CLOSE_EXIT /*4*/:
                return makeOpenCloseAnimation(this.mHost.getContext(), 1.0f, 1.075f, 1.0f, 0.0f);
            case ANIM_STYLE_FADE_ENTER /*5*/:
                return makeFadeAnimation(this.mHost.getContext(), 0.0f, 1.0f);
            case ANIM_STYLE_FADE_EXIT /*6*/:
                return makeFadeAnimation(this.mHost.getContext(), 1.0f, 0.0f);
            default:
                if (transitionStyle == 0 && this.mHost.onHasWindowAnimations()) {
                    transitionStyle = this.mHost.onGetWindowAnimations();
                }
                if (transitionStyle == 0) {
                    return null;
                }
                return null;
        }
    }

    public void performPendingDeferredStart(Fragment f) {
        if (!f.mDeferStart) {
            return;
        }
        if (this.mExecutingActions) {
            this.mHavePendingDeferredStart = true;
            return;
        }
        f.mDeferStart = HONEYCOMB;
        moveToState(f, this.mCurState, 0, 0, HONEYCOMB);
    }

    private void setHWLayerAnimListenerIfAlpha(View v, Animation anim) {
        if (v != null && anim != null && shouldRunOnHWLayer(v, anim)) {
            AnimationListener originalListener = null;
            try {
                if (sAnimationListenerField == null) {
                    sAnimationListenerField = Animation.class.getDeclaredField("mListener");
                    sAnimationListenerField.setAccessible(true);
                }
                originalListener = (AnimationListener) sAnimationListenerField.get(anim);
            } catch (NoSuchFieldException e) {
                Log.e(TAG, "No field with the name mListener is found in Animation class", e);
            } catch (IllegalAccessException e2) {
                Log.e(TAG, "Cannot access Animation's mListener field", e2);
            }
            anim.setAnimationListener(new AnimateOnHWLayerIfNeededListener(v, anim, originalListener));
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void moveToState(android.support.v4.app.Fragment r18, int r19, int r20, int r21, boolean r22) {
        /*
        r17 = this;
        r0 = r18;
        r4 = r0.mAdded;
        if (r4 == 0) goto L_0x000c;
    L_0x0006:
        r0 = r18;
        r4 = r0.mDetached;
        if (r4 == 0) goto L_0x0013;
    L_0x000c:
        r4 = 1;
        r0 = r19;
        if (r0 <= r4) goto L_0x0013;
    L_0x0011:
        r19 = 1;
    L_0x0013:
        r0 = r18;
        r4 = r0.mRemoving;
        if (r4 == 0) goto L_0x0027;
    L_0x0019:
        r0 = r18;
        r4 = r0.mState;
        r0 = r19;
        if (r0 <= r4) goto L_0x0027;
    L_0x0021:
        r0 = r18;
        r0 = r0.mState;
        r19 = r0;
    L_0x0027:
        r0 = r18;
        r4 = r0.mDeferStart;
        if (r4 == 0) goto L_0x003b;
    L_0x002d:
        r0 = r18;
        r4 = r0.mState;
        r5 = 4;
        if (r4 >= r5) goto L_0x003b;
    L_0x0034:
        r4 = 3;
        r0 = r19;
        if (r0 <= r4) goto L_0x003b;
    L_0x0039:
        r19 = 3;
    L_0x003b:
        r0 = r18;
        r4 = r0.mState;
        r0 = r19;
        if (r4 >= r0) goto L_0x0413;
    L_0x0043:
        r0 = r18;
        r4 = r0.mFromLayout;
        if (r4 == 0) goto L_0x0050;
    L_0x0049:
        r0 = r18;
        r4 = r0.mInLayout;
        if (r4 != 0) goto L_0x0050;
    L_0x004f:
        return;
    L_0x0050:
        r0 = r18;
        r4 = r0.mAnimatingAway;
        if (r4 == 0) goto L_0x0069;
    L_0x0056:
        r4 = 0;
        r0 = r18;
        r0.mAnimatingAway = r4;
        r0 = r18;
        r6 = r0.mStateAfterAnimating;
        r7 = 0;
        r8 = 0;
        r9 = 1;
        r4 = r17;
        r5 = r18;
        r4.moveToState(r5, r6, r7, r8, r9);
    L_0x0069:
        r0 = r18;
        r4 = r0.mState;
        switch(r4) {
            case 0: goto L_0x00b9;
            case 1: goto L_0x0220;
            case 2: goto L_0x0372;
            case 3: goto L_0x037c;
            case 4: goto L_0x03a2;
            default: goto L_0x0070;
        };
    L_0x0070:
        r0 = r18;
        r4 = r0.mState;
        r0 = r19;
        if (r4 == r0) goto L_0x004f;
    L_0x0078:
        r4 = "FragmentManager";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "moveToState: Fragment state for ";
        r5 = r5.append(r6);
        r0 = r18;
        r5 = r5.append(r0);
        r6 = " not updated inline; ";
        r5 = r5.append(r6);
        r6 = "expected state ";
        r5 = r5.append(r6);
        r0 = r19;
        r5 = r5.append(r0);
        r6 = " found ";
        r5 = r5.append(r6);
        r0 = r18;
        r6 = r0.mState;
        r5 = r5.append(r6);
        r5 = r5.toString();
        android.util.Log.w(r4, r5);
        r0 = r19;
        r1 = r18;
        r1.mState = r0;
        goto L_0x004f;
    L_0x00b9:
        r4 = DEBUG;
        if (r4 == 0) goto L_0x00d7;
    L_0x00bd:
        r4 = "FragmentManager";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "moveto CREATED: ";
        r5 = r5.append(r6);
        r0 = r18;
        r5 = r5.append(r0);
        r5 = r5.toString();
        android.util.Log.v(r4, r5);
    L_0x00d7:
        r0 = r18;
        r4 = r0.mSavedFragmentState;
        if (r4 == 0) goto L_0x0144;
    L_0x00dd:
        r0 = r18;
        r4 = r0.mSavedFragmentState;
        r0 = r17;
        r5 = r0.mHost;
        r5 = r5.getContext();
        r5 = r5.getClassLoader();
        r4.setClassLoader(r5);
        r0 = r18;
        r4 = r0.mSavedFragmentState;
        r5 = "android:view_state";
        r4 = r4.getSparseParcelableArray(r5);
        r0 = r18;
        r0.mSavedViewState = r4;
        r0 = r18;
        r4 = r0.mSavedFragmentState;
        r5 = "android:target_state";
        r0 = r17;
        r4 = r0.getFragment(r4, r5);
        r0 = r18;
        r0.mTarget = r4;
        r0 = r18;
        r4 = r0.mTarget;
        if (r4 == 0) goto L_0x0123;
    L_0x0114:
        r0 = r18;
        r4 = r0.mSavedFragmentState;
        r5 = "android:target_req_state";
        r6 = 0;
        r4 = r4.getInt(r5, r6);
        r0 = r18;
        r0.mTargetRequestCode = r4;
    L_0x0123:
        r0 = r18;
        r4 = r0.mSavedFragmentState;
        r5 = "android:user_visible_hint";
        r6 = 1;
        r4 = r4.getBoolean(r5, r6);
        r0 = r18;
        r0.mUserVisibleHint = r4;
        r0 = r18;
        r4 = r0.mUserVisibleHint;
        if (r4 != 0) goto L_0x0144;
    L_0x0138:
        r4 = 1;
        r0 = r18;
        r0.mDeferStart = r4;
        r4 = 3;
        r0 = r19;
        if (r0 <= r4) goto L_0x0144;
    L_0x0142:
        r19 = 3;
    L_0x0144:
        r0 = r17;
        r4 = r0.mHost;
        r0 = r18;
        r0.mHost = r4;
        r0 = r17;
        r4 = r0.mParent;
        r0 = r18;
        r0.mParentFragment = r4;
        r0 = r17;
        r4 = r0.mParent;
        if (r4 == 0) goto L_0x019d;
    L_0x015a:
        r0 = r17;
        r4 = r0.mParent;
        r4 = r4.mChildFragmentManager;
    L_0x0160:
        r0 = r18;
        r0.mFragmentManager = r4;
        r4 = 0;
        r0 = r18;
        r0.mCalled = r4;
        r0 = r17;
        r4 = r0.mHost;
        r4 = r4.getContext();
        r0 = r18;
        r0.onAttach(r4);
        r0 = r18;
        r4 = r0.mCalled;
        if (r4 != 0) goto L_0x01a6;
    L_0x017c:
        r4 = new android.support.v4.app.SuperNotCalledException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Fragment ";
        r5 = r5.append(r6);
        r0 = r18;
        r5 = r5.append(r0);
        r6 = " did not call through to super.onAttach()";
        r5 = r5.append(r6);
        r5 = r5.toString();
        r4.<init>(r5);
        throw r4;
    L_0x019d:
        r0 = r17;
        r4 = r0.mHost;
        r4 = r4.getFragmentManagerImpl();
        goto L_0x0160;
    L_0x01a6:
        r0 = r18;
        r4 = r0.mParentFragment;
        if (r4 != 0) goto L_0x01b5;
    L_0x01ac:
        r0 = r17;
        r4 = r0.mHost;
        r0 = r18;
        r4.onAttachFragment(r0);
    L_0x01b5:
        r0 = r18;
        r4 = r0.mRetaining;
        if (r4 != 0) goto L_0x03d4;
    L_0x01bb:
        r0 = r18;
        r4 = r0.mSavedFragmentState;
        r0 = r18;
        r0.performCreate(r4);
    L_0x01c4:
        r4 = 0;
        r0 = r18;
        r0.mRetaining = r4;
        r0 = r18;
        r4 = r0.mFromLayout;
        if (r4 == 0) goto L_0x0220;
    L_0x01cf:
        r0 = r18;
        r4 = r0.mSavedFragmentState;
        r0 = r18;
        r4 = r0.getLayoutInflater(r4);
        r5 = 0;
        r0 = r18;
        r6 = r0.mSavedFragmentState;
        r0 = r18;
        r4 = r0.performCreateView(r4, r5, r6);
        r0 = r18;
        r0.mView = r4;
        r0 = r18;
        r4 = r0.mView;
        if (r4 == 0) goto L_0x03f2;
    L_0x01ee:
        r0 = r18;
        r4 = r0.mView;
        r0 = r18;
        r0.mInnerView = r4;
        r4 = android.os.Build.VERSION.SDK_INT;
        r5 = 11;
        if (r4 < r5) goto L_0x03e4;
    L_0x01fc:
        r0 = r18;
        r4 = r0.mView;
        r5 = 0;
        android.support.v4.view.ViewCompat.setSaveFromParentEnabled(r4, r5);
    L_0x0204:
        r0 = r18;
        r4 = r0.mHidden;
        if (r4 == 0) goto L_0x0213;
    L_0x020a:
        r0 = r18;
        r4 = r0.mView;
        r5 = 8;
        r4.setVisibility(r5);
    L_0x0213:
        r0 = r18;
        r4 = r0.mView;
        r0 = r18;
        r5 = r0.mSavedFragmentState;
        r0 = r18;
        r0.onViewCreated(r4, r5);
    L_0x0220:
        r4 = 1;
        r0 = r19;
        if (r0 <= r4) goto L_0x0372;
    L_0x0225:
        r4 = DEBUG;
        if (r4 == 0) goto L_0x0243;
    L_0x0229:
        r4 = "FragmentManager";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "moveto ACTIVITY_CREATED: ";
        r5 = r5.append(r6);
        r0 = r18;
        r5 = r5.append(r0);
        r5 = r5.toString();
        android.util.Log.v(r4, r5);
    L_0x0243:
        r0 = r18;
        r4 = r0.mFromLayout;
        if (r4 != 0) goto L_0x0355;
    L_0x0249:
        r11 = 0;
        r0 = r18;
        r4 = r0.mContainerId;
        if (r4 == 0) goto L_0x02d9;
    L_0x0250:
        r0 = r18;
        r4 = r0.mContainerId;
        r5 = -1;
        if (r4 != r5) goto L_0x027c;
    L_0x0257:
        r4 = new java.lang.IllegalArgumentException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Cannot create fragment ";
        r5 = r5.append(r6);
        r0 = r18;
        r5 = r5.append(r0);
        r6 = " for a container view with no id";
        r5 = r5.append(r6);
        r5 = r5.toString();
        r4.<init>(r5);
        r0 = r17;
        r0.throwException(r4);
    L_0x027c:
        r0 = r17;
        r4 = r0.mContainer;
        r0 = r18;
        r5 = r0.mContainerId;
        r11 = r4.onFindViewById(r5);
        r11 = (android.view.ViewGroup) r11;
        if (r11 != 0) goto L_0x02d9;
    L_0x028c:
        r0 = r18;
        r4 = r0.mRestored;
        if (r4 != 0) goto L_0x02d9;
    L_0x0292:
        r4 = r18.getResources();	 Catch:{ NotFoundException -> 0x03f9 }
        r0 = r18;
        r5 = r0.mContainerId;	 Catch:{ NotFoundException -> 0x03f9 }
        r14 = r4.getResourceName(r5);	 Catch:{ NotFoundException -> 0x03f9 }
    L_0x029e:
        r4 = new java.lang.IllegalArgumentException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "No view found for id 0x";
        r5 = r5.append(r6);
        r0 = r18;
        r6 = r0.mContainerId;
        r6 = java.lang.Integer.toHexString(r6);
        r5 = r5.append(r6);
        r6 = " (";
        r5 = r5.append(r6);
        r5 = r5.append(r14);
        r6 = ") for fragment ";
        r5 = r5.append(r6);
        r0 = r18;
        r5 = r5.append(r0);
        r5 = r5.toString();
        r4.<init>(r5);
        r0 = r17;
        r0.throwException(r4);
    L_0x02d9:
        r0 = r18;
        r0.mContainer = r11;
        r0 = r18;
        r4 = r0.mSavedFragmentState;
        r0 = r18;
        r4 = r0.getLayoutInflater(r4);
        r0 = r18;
        r5 = r0.mSavedFragmentState;
        r0 = r18;
        r4 = r0.performCreateView(r4, r11, r5);
        r0 = r18;
        r0.mView = r4;
        r0 = r18;
        r4 = r0.mView;
        if (r4 == 0) goto L_0x040c;
    L_0x02fb:
        r0 = r18;
        r4 = r0.mView;
        r0 = r18;
        r0.mInnerView = r4;
        r4 = android.os.Build.VERSION.SDK_INT;
        r5 = 11;
        if (r4 < r5) goto L_0x03fe;
    L_0x0309:
        r0 = r18;
        r4 = r0.mView;
        r5 = 0;
        android.support.v4.view.ViewCompat.setSaveFromParentEnabled(r4, r5);
    L_0x0311:
        if (r11 == 0) goto L_0x0339;
    L_0x0313:
        r4 = 1;
        r0 = r17;
        r1 = r18;
        r2 = r20;
        r3 = r21;
        r10 = r0.loadAnimation(r1, r2, r4, r3);
        if (r10 == 0) goto L_0x0332;
    L_0x0322:
        r0 = r18;
        r4 = r0.mView;
        r0 = r17;
        r0.setHWLayerAnimListenerIfAlpha(r4, r10);
        r0 = r18;
        r4 = r0.mView;
        r4.startAnimation(r10);
    L_0x0332:
        r0 = r18;
        r4 = r0.mView;
        r11.addView(r4);
    L_0x0339:
        r0 = r18;
        r4 = r0.mHidden;
        if (r4 == 0) goto L_0x0348;
    L_0x033f:
        r0 = r18;
        r4 = r0.mView;
        r5 = 8;
        r4.setVisibility(r5);
    L_0x0348:
        r0 = r18;
        r4 = r0.mView;
        r0 = r18;
        r5 = r0.mSavedFragmentState;
        r0 = r18;
        r0.onViewCreated(r4, r5);
    L_0x0355:
        r0 = r18;
        r4 = r0.mSavedFragmentState;
        r0 = r18;
        r0.performActivityCreated(r4);
        r0 = r18;
        r4 = r0.mView;
        if (r4 == 0) goto L_0x036d;
    L_0x0364:
        r0 = r18;
        r4 = r0.mSavedFragmentState;
        r0 = r18;
        r0.restoreViewState(r4);
    L_0x036d:
        r4 = 0;
        r0 = r18;
        r0.mSavedFragmentState = r4;
    L_0x0372:
        r4 = 2;
        r0 = r19;
        if (r0 <= r4) goto L_0x037c;
    L_0x0377:
        r4 = 3;
        r0 = r18;
        r0.mState = r4;
    L_0x037c:
        r4 = 3;
        r0 = r19;
        if (r0 <= r4) goto L_0x03a2;
    L_0x0381:
        r4 = DEBUG;
        if (r4 == 0) goto L_0x039f;
    L_0x0385:
        r4 = "FragmentManager";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "moveto STARTED: ";
        r5 = r5.append(r6);
        r0 = r18;
        r5 = r5.append(r0);
        r5 = r5.toString();
        android.util.Log.v(r4, r5);
    L_0x039f:
        r18.performStart();
    L_0x03a2:
        r4 = 4;
        r0 = r19;
        if (r0 <= r4) goto L_0x0070;
    L_0x03a7:
        r4 = DEBUG;
        if (r4 == 0) goto L_0x03c5;
    L_0x03ab:
        r4 = "FragmentManager";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "moveto RESUMED: ";
        r5 = r5.append(r6);
        r0 = r18;
        r5 = r5.append(r0);
        r5 = r5.toString();
        android.util.Log.v(r4, r5);
    L_0x03c5:
        r18.performResume();
        r4 = 0;
        r0 = r18;
        r0.mSavedFragmentState = r4;
        r4 = 0;
        r0 = r18;
        r0.mSavedViewState = r4;
        goto L_0x0070;
    L_0x03d4:
        r0 = r18;
        r4 = r0.mSavedFragmentState;
        r0 = r18;
        r0.restoreChildFragmentState(r4);
        r4 = 1;
        r0 = r18;
        r0.mState = r4;
        goto L_0x01c4;
    L_0x03e4:
        r0 = r18;
        r4 = r0.mView;
        r4 = android.support.v4.app.NoSaveStateFrameLayout.wrap(r4);
        r0 = r18;
        r0.mView = r4;
        goto L_0x0204;
    L_0x03f2:
        r4 = 0;
        r0 = r18;
        r0.mInnerView = r4;
        goto L_0x0220;
    L_0x03f9:
        r12 = move-exception;
        r14 = "unknown";
        goto L_0x029e;
    L_0x03fe:
        r0 = r18;
        r4 = r0.mView;
        r4 = android.support.v4.app.NoSaveStateFrameLayout.wrap(r4);
        r0 = r18;
        r0.mView = r4;
        goto L_0x0311;
    L_0x040c:
        r4 = 0;
        r0 = r18;
        r0.mInnerView = r4;
        goto L_0x0355;
    L_0x0413:
        r0 = r18;
        r4 = r0.mState;
        r0 = r19;
        if (r4 <= r0) goto L_0x0070;
    L_0x041b:
        r0 = r18;
        r4 = r0.mState;
        switch(r4) {
            case 1: goto L_0x0424;
            case 2: goto L_0x04c3;
            case 3: goto L_0x049d;
            case 4: goto L_0x0477;
            case 5: goto L_0x0451;
            default: goto L_0x0422;
        };
    L_0x0422:
        goto L_0x0070;
    L_0x0424:
        r4 = 1;
        r0 = r19;
        if (r0 >= r4) goto L_0x0070;
    L_0x0429:
        r0 = r17;
        r4 = r0.mDestroyed;
        if (r4 == 0) goto L_0x0441;
    L_0x042f:
        r0 = r18;
        r4 = r0.mAnimatingAway;
        if (r4 == 0) goto L_0x0441;
    L_0x0435:
        r0 = r18;
        r15 = r0.mAnimatingAway;
        r4 = 0;
        r0 = r18;
        r0.mAnimatingAway = r4;
        r15.clearAnimation();
    L_0x0441:
        r0 = r18;
        r4 = r0.mAnimatingAway;
        if (r4 == 0) goto L_0x0571;
    L_0x0447:
        r0 = r19;
        r1 = r18;
        r1.mStateAfterAnimating = r0;
        r19 = 1;
        goto L_0x0070;
    L_0x0451:
        r4 = 5;
        r0 = r19;
        if (r0 >= r4) goto L_0x0477;
    L_0x0456:
        r4 = DEBUG;
        if (r4 == 0) goto L_0x0474;
    L_0x045a:
        r4 = "FragmentManager";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "movefrom RESUMED: ";
        r5 = r5.append(r6);
        r0 = r18;
        r5 = r5.append(r0);
        r5 = r5.toString();
        android.util.Log.v(r4, r5);
    L_0x0474:
        r18.performPause();
    L_0x0477:
        r4 = 4;
        r0 = r19;
        if (r0 >= r4) goto L_0x049d;
    L_0x047c:
        r4 = DEBUG;
        if (r4 == 0) goto L_0x049a;
    L_0x0480:
        r4 = "FragmentManager";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "movefrom STARTED: ";
        r5 = r5.append(r6);
        r0 = r18;
        r5 = r5.append(r0);
        r5 = r5.toString();
        android.util.Log.v(r4, r5);
    L_0x049a:
        r18.performStop();
    L_0x049d:
        r4 = 3;
        r0 = r19;
        if (r0 >= r4) goto L_0x04c3;
    L_0x04a2:
        r4 = DEBUG;
        if (r4 == 0) goto L_0x04c0;
    L_0x04a6:
        r4 = "FragmentManager";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "movefrom STOPPED: ";
        r5 = r5.append(r6);
        r0 = r18;
        r5 = r5.append(r0);
        r5 = r5.toString();
        android.util.Log.v(r4, r5);
    L_0x04c0:
        r18.performReallyStop();
    L_0x04c3:
        r4 = 2;
        r0 = r19;
        if (r0 >= r4) goto L_0x0424;
    L_0x04c8:
        r4 = DEBUG;
        if (r4 == 0) goto L_0x04e6;
    L_0x04cc:
        r4 = "FragmentManager";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "movefrom ACTIVITY_CREATED: ";
        r5 = r5.append(r6);
        r0 = r18;
        r5 = r5.append(r0);
        r5 = r5.toString();
        android.util.Log.v(r4, r5);
    L_0x04e6:
        r0 = r18;
        r4 = r0.mView;
        if (r4 == 0) goto L_0x0501;
    L_0x04ec:
        r0 = r17;
        r4 = r0.mHost;
        r0 = r18;
        r4 = r4.onShouldSaveFragmentState(r0);
        if (r4 == 0) goto L_0x0501;
    L_0x04f8:
        r0 = r18;
        r4 = r0.mSavedViewState;
        if (r4 != 0) goto L_0x0501;
    L_0x04fe:
        r17.saveFragmentViewState(r18);
    L_0x0501:
        r18.performDestroyView();
        r0 = r18;
        r4 = r0.mView;
        if (r4 == 0) goto L_0x0560;
    L_0x050a:
        r0 = r18;
        r4 = r0.mContainer;
        if (r4 == 0) goto L_0x0560;
    L_0x0510:
        r10 = 0;
        r0 = r17;
        r4 = r0.mCurState;
        if (r4 <= 0) goto L_0x052a;
    L_0x0517:
        r0 = r17;
        r4 = r0.mDestroyed;
        if (r4 != 0) goto L_0x052a;
    L_0x051d:
        r4 = 0;
        r0 = r17;
        r1 = r18;
        r2 = r20;
        r3 = r21;
        r10 = r0.loadAnimation(r1, r2, r4, r3);
    L_0x052a:
        if (r10 == 0) goto L_0x0555;
    L_0x052c:
        r13 = r18;
        r0 = r18;
        r4 = r0.mView;
        r0 = r18;
        r0.mAnimatingAway = r4;
        r0 = r19;
        r1 = r18;
        r1.mStateAfterAnimating = r0;
        r0 = r18;
        r0 = r0.mView;
        r16 = r0;
        r4 = new android.support.v4.app.FragmentManagerImpl$5;
        r0 = r17;
        r1 = r16;
        r4.<init>(r1, r10, r13);
        r10.setAnimationListener(r4);
        r0 = r18;
        r4 = r0.mView;
        r4.startAnimation(r10);
    L_0x0555:
        r0 = r18;
        r4 = r0.mContainer;
        r0 = r18;
        r5 = r0.mView;
        r4.removeView(r5);
    L_0x0560:
        r4 = 0;
        r0 = r18;
        r0.mContainer = r4;
        r4 = 0;
        r0 = r18;
        r0.mView = r4;
        r4 = 0;
        r0 = r18;
        r0.mInnerView = r4;
        goto L_0x0424;
    L_0x0571:
        r4 = DEBUG;
        if (r4 == 0) goto L_0x058f;
    L_0x0575:
        r4 = "FragmentManager";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "movefrom CREATED: ";
        r5 = r5.append(r6);
        r0 = r18;
        r5 = r5.append(r0);
        r5 = r5.toString();
        android.util.Log.v(r4, r5);
    L_0x058f:
        r0 = r18;
        r4 = r0.mRetaining;
        if (r4 != 0) goto L_0x05c7;
    L_0x0595:
        r18.performDestroy();
    L_0x0598:
        r4 = 0;
        r0 = r18;
        r0.mCalled = r4;
        r18.onDetach();
        r0 = r18;
        r4 = r0.mCalled;
        if (r4 != 0) goto L_0x05cd;
    L_0x05a6:
        r4 = new android.support.v4.app.SuperNotCalledException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Fragment ";
        r5 = r5.append(r6);
        r0 = r18;
        r5 = r5.append(r0);
        r6 = " did not call through to super.onDetach()";
        r5 = r5.append(r6);
        r5 = r5.toString();
        r4.<init>(r5);
        throw r4;
    L_0x05c7:
        r4 = 1;
        r0 = r18;
        r0.mState = r4;
        goto L_0x0598;
    L_0x05cd:
        if (r22 != 0) goto L_0x0070;
    L_0x05cf:
        r0 = r18;
        r4 = r0.mRetaining;
        if (r4 != 0) goto L_0x05da;
    L_0x05d5:
        r17.makeInactive(r18);
        goto L_0x0070;
    L_0x05da:
        r4 = 0;
        r0 = r18;
        r0.mHost = r4;
        r4 = 0;
        r0 = r18;
        r0.mParentFragment = r4;
        r4 = 0;
        r0 = r18;
        r0.mFragmentManager = r4;
        goto L_0x0070;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.FragmentManagerImpl.moveToState(android.support.v4.app.Fragment, int, int, int, boolean):void");
    }

    void moveToState(Fragment f) {
        moveToState(f, this.mCurState, 0, 0, HONEYCOMB);
    }

    void moveToState(int newState, boolean always) {
        moveToState(newState, 0, 0, always);
    }

    void moveToState(int newState, int transit, int transitStyle, boolean always) {
        if (this.mHost == null && newState != 0) {
            throw new IllegalStateException("No host");
        } else if (always || this.mCurState != newState) {
            this.mCurState = newState;
            if (this.mActive != null) {
                boolean loadersRunning = HONEYCOMB;
                for (int i = 0; i < this.mActive.size(); i += ANIM_STYLE_OPEN_ENTER) {
                    Fragment f = (Fragment) this.mActive.get(i);
                    if (f != null) {
                        moveToState(f, newState, transit, transitStyle, HONEYCOMB);
                        if (f.mLoaderManager != null) {
                            loadersRunning |= f.mLoaderManager.hasRunningLoaders();
                        }
                    }
                }
                if (!loadersRunning) {
                    startPendingDeferredFragments();
                }
                if (this.mNeedMenuInvalidate && this.mHost != null && this.mCurState == ANIM_STYLE_FADE_ENTER) {
                    this.mHost.onSupportInvalidateOptionsMenu();
                    this.mNeedMenuInvalidate = HONEYCOMB;
                }
            }
        }
    }

    void startPendingDeferredFragments() {
        if (this.mActive != null) {
            for (int i = 0; i < this.mActive.size(); i += ANIM_STYLE_OPEN_ENTER) {
                Fragment f = (Fragment) this.mActive.get(i);
                if (f != null) {
                    performPendingDeferredStart(f);
                }
            }
        }
    }

    void makeActive(Fragment f) {
        if (f.mIndex < 0) {
            if (this.mAvailIndices == null || this.mAvailIndices.size() <= 0) {
                if (this.mActive == null) {
                    this.mActive = new ArrayList();
                }
                f.setIndex(this.mActive.size(), this.mParent);
                this.mActive.add(f);
            } else {
                f.setIndex(((Integer) this.mAvailIndices.remove(this.mAvailIndices.size() - 1)).intValue(), this.mParent);
                this.mActive.set(f.mIndex, f);
            }
            if (DEBUG) {
                Log.v(TAG, "Allocated fragment index " + f);
            }
        }
    }

    void makeInactive(Fragment f) {
        if (f.mIndex >= 0) {
            if (DEBUG) {
                Log.v(TAG, "Freeing fragment index " + f);
            }
            this.mActive.set(f.mIndex, null);
            if (this.mAvailIndices == null) {
                this.mAvailIndices = new ArrayList();
            }
            this.mAvailIndices.add(Integer.valueOf(f.mIndex));
            this.mHost.inactivateFragment(f.mWho);
            f.initState();
        }
    }

    public void addFragment(Fragment fragment, boolean moveToStateNow) {
        if (this.mAdded == null) {
            this.mAdded = new ArrayList();
        }
        if (DEBUG) {
            Log.v(TAG, "add: " + fragment);
        }
        makeActive(fragment);
        if (!fragment.mDetached) {
            if (this.mAdded.contains(fragment)) {
                throw new IllegalStateException("Fragment already added: " + fragment);
            }
            this.mAdded.add(fragment);
            fragment.mAdded = true;
            fragment.mRemoving = HONEYCOMB;
            if (fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            if (moveToStateNow) {
                moveToState(fragment);
            }
        }
    }

    public void removeFragment(Fragment fragment, int transition, int transitionStyle) {
        boolean inactive;
        if (DEBUG) {
            Log.v(TAG, "remove: " + fragment + " nesting=" + fragment.mBackStackNesting);
        }
        if (fragment.isInBackStack()) {
            inactive = HONEYCOMB;
        } else {
            inactive = true;
        }
        if (!fragment.mDetached || inactive) {
            int i;
            if (this.mAdded != null) {
                this.mAdded.remove(fragment);
            }
            if (fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            fragment.mAdded = HONEYCOMB;
            fragment.mRemoving = true;
            if (inactive) {
                i = 0;
            } else {
                i = ANIM_STYLE_OPEN_ENTER;
            }
            moveToState(fragment, i, transition, transitionStyle, HONEYCOMB);
        }
    }

    public void hideFragment(Fragment fragment, int transition, int transitionStyle) {
        if (DEBUG) {
            Log.v(TAG, "hide: " + fragment);
        }
        if (!fragment.mHidden) {
            fragment.mHidden = true;
            if (fragment.mView != null) {
                Animation anim = loadAnimation(fragment, transition, HONEYCOMB, transitionStyle);
                if (anim != null) {
                    setHWLayerAnimListenerIfAlpha(fragment.mView, anim);
                    fragment.mView.startAnimation(anim);
                }
                fragment.mView.setVisibility(8);
            }
            if (fragment.mAdded && fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            fragment.onHiddenChanged(true);
        }
    }

    public void showFragment(Fragment fragment, int transition, int transitionStyle) {
        if (DEBUG) {
            Log.v(TAG, "show: " + fragment);
        }
        if (fragment.mHidden) {
            fragment.mHidden = HONEYCOMB;
            if (fragment.mView != null) {
                Animation anim = loadAnimation(fragment, transition, true, transitionStyle);
                if (anim != null) {
                    setHWLayerAnimListenerIfAlpha(fragment.mView, anim);
                    fragment.mView.startAnimation(anim);
                }
                fragment.mView.setVisibility(0);
            }
            if (fragment.mAdded && fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            fragment.onHiddenChanged(HONEYCOMB);
        }
    }

    public void detachFragment(Fragment fragment, int transition, int transitionStyle) {
        if (DEBUG) {
            Log.v(TAG, "detach: " + fragment);
        }
        if (!fragment.mDetached) {
            fragment.mDetached = true;
            if (fragment.mAdded) {
                if (this.mAdded != null) {
                    if (DEBUG) {
                        Log.v(TAG, "remove from detach: " + fragment);
                    }
                    this.mAdded.remove(fragment);
                }
                if (fragment.mHasMenu && fragment.mMenuVisible) {
                    this.mNeedMenuInvalidate = true;
                }
                fragment.mAdded = HONEYCOMB;
                moveToState(fragment, ANIM_STYLE_OPEN_ENTER, transition, transitionStyle, HONEYCOMB);
            }
        }
    }

    public void attachFragment(Fragment fragment, int transition, int transitionStyle) {
        if (DEBUG) {
            Log.v(TAG, "attach: " + fragment);
        }
        if (fragment.mDetached) {
            fragment.mDetached = HONEYCOMB;
            if (!fragment.mAdded) {
                if (this.mAdded == null) {
                    this.mAdded = new ArrayList();
                }
                if (this.mAdded.contains(fragment)) {
                    throw new IllegalStateException("Fragment already added: " + fragment);
                }
                if (DEBUG) {
                    Log.v(TAG, "add from attach: " + fragment);
                }
                this.mAdded.add(fragment);
                fragment.mAdded = true;
                if (fragment.mHasMenu && fragment.mMenuVisible) {
                    this.mNeedMenuInvalidate = true;
                }
                moveToState(fragment, this.mCurState, transition, transitionStyle, HONEYCOMB);
            }
        }
    }

    public Fragment findFragmentById(int id) {
        int i;
        Fragment f;
        if (this.mAdded != null) {
            for (i = this.mAdded.size() - 1; i >= 0; i--) {
                f = (Fragment) this.mAdded.get(i);
                if (f != null && f.mFragmentId == id) {
                    return f;
                }
            }
        }
        if (this.mActive != null) {
            for (i = this.mActive.size() - 1; i >= 0; i--) {
                f = (Fragment) this.mActive.get(i);
                if (f != null && f.mFragmentId == id) {
                    return f;
                }
            }
        }
        return null;
    }

    public Fragment findFragmentByTag(String tag) {
        int i;
        Fragment f;
        if (!(this.mAdded == null || tag == null)) {
            for (i = this.mAdded.size() - 1; i >= 0; i--) {
                f = (Fragment) this.mAdded.get(i);
                if (f != null && tag.equals(f.mTag)) {
                    return f;
                }
            }
        }
        if (!(this.mActive == null || tag == null)) {
            for (i = this.mActive.size() - 1; i >= 0; i--) {
                f = (Fragment) this.mActive.get(i);
                if (f != null && tag.equals(f.mTag)) {
                    return f;
                }
            }
        }
        return null;
    }

    public Fragment findFragmentByWho(String who) {
        if (!(this.mActive == null || who == null)) {
            for (int i = this.mActive.size() - 1; i >= 0; i--) {
                Fragment f = (Fragment) this.mActive.get(i);
                if (f != null) {
                    f = f.findFragmentByWho(who);
                    if (f != null) {
                        return f;
                    }
                }
            }
        }
        return null;
    }

    private void checkStateLoss() {
        if (this.mStateSaved) {
            throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
        } else if (this.mNoTransactionsBecause != null) {
            throw new IllegalStateException("Can not perform this action inside of " + this.mNoTransactionsBecause);
        }
    }

    public void enqueueAction(Runnable action, boolean allowStateLoss) {
        if (!allowStateLoss) {
            checkStateLoss();
        }
        synchronized (this) {
            if (this.mDestroyed || this.mHost == null) {
                throw new IllegalStateException("Activity has been destroyed");
            }
            if (this.mPendingActions == null) {
                this.mPendingActions = new ArrayList();
            }
            this.mPendingActions.add(action);
            if (this.mPendingActions.size() == ANIM_STYLE_OPEN_ENTER) {
                this.mHost.getHandler().removeCallbacks(this.mExecCommit);
                this.mHost.getHandler().post(this.mExecCommit);
            }
        }
    }

    public int allocBackStackIndex(BackStackRecord bse) {
        synchronized (this) {
            int index;
            if (this.mAvailBackStackIndices == null || this.mAvailBackStackIndices.size() <= 0) {
                if (this.mBackStackIndices == null) {
                    this.mBackStackIndices = new ArrayList();
                }
                index = this.mBackStackIndices.size();
                if (DEBUG) {
                    Log.v(TAG, "Setting back stack index " + index + " to " + bse);
                }
                this.mBackStackIndices.add(bse);
                return index;
            }
            index = ((Integer) this.mAvailBackStackIndices.remove(this.mAvailBackStackIndices.size() - 1)).intValue();
            if (DEBUG) {
                Log.v(TAG, "Adding back stack index " + index + " with " + bse);
            }
            this.mBackStackIndices.set(index, bse);
            return index;
        }
    }

    public void setBackStackIndex(int index, BackStackRecord bse) {
        synchronized (this) {
            if (this.mBackStackIndices == null) {
                this.mBackStackIndices = new ArrayList();
            }
            int N = this.mBackStackIndices.size();
            if (index < N) {
                if (DEBUG) {
                    Log.v(TAG, "Setting back stack index " + index + " to " + bse);
                }
                this.mBackStackIndices.set(index, bse);
            } else {
                while (N < index) {
                    this.mBackStackIndices.add(null);
                    if (this.mAvailBackStackIndices == null) {
                        this.mAvailBackStackIndices = new ArrayList();
                    }
                    if (DEBUG) {
                        Log.v(TAG, "Adding available back stack index " + N);
                    }
                    this.mAvailBackStackIndices.add(Integer.valueOf(N));
                    N += ANIM_STYLE_OPEN_ENTER;
                }
                if (DEBUG) {
                    Log.v(TAG, "Adding back stack index " + index + " with " + bse);
                }
                this.mBackStackIndices.add(bse);
            }
        }
    }

    public void freeBackStackIndex(int index) {
        synchronized (this) {
            this.mBackStackIndices.set(index, null);
            if (this.mAvailBackStackIndices == null) {
                this.mAvailBackStackIndices = new ArrayList();
            }
            if (DEBUG) {
                Log.v(TAG, "Freeing back stack index " + index);
            }
            this.mAvailBackStackIndices.add(Integer.valueOf(index));
        }
    }

    public void execSingleAction(Runnable action, boolean allowStateLoss) {
        if (this.mExecutingActions) {
            throw new IllegalStateException("FragmentManager is already executing transactions");
        } else if (Looper.myLooper() != this.mHost.getHandler().getLooper()) {
            throw new IllegalStateException("Must be called from main thread of fragment host");
        } else {
            if (!allowStateLoss) {
                checkStateLoss();
            }
            this.mExecutingActions = true;
            action.run();
            this.mExecutingActions = HONEYCOMB;
            doPendingDeferredStart();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean execPendingActions() {
        /*
        r5 = this;
        r3 = r5.mExecutingActions;
        if (r3 == 0) goto L_0x000c;
    L_0x0004:
        r3 = new java.lang.IllegalStateException;
        r4 = "FragmentManager is already executing transactions";
        r3.<init>(r4);
        throw r3;
    L_0x000c:
        r3 = android.os.Looper.myLooper();
        r4 = r5.mHost;
        r4 = r4.getHandler();
        r4 = r4.getLooper();
        if (r3 == r4) goto L_0x0024;
    L_0x001c:
        r3 = new java.lang.IllegalStateException;
        r4 = "Must be called from main thread of fragment host";
        r3.<init>(r4);
        throw r3;
    L_0x0024:
        r0 = 0;
    L_0x0025:
        monitor-enter(r5);
        r3 = r5.mPendingActions;	 Catch:{ all -> 0x0077 }
        if (r3 == 0) goto L_0x0032;
    L_0x002a:
        r3 = r5.mPendingActions;	 Catch:{ all -> 0x0077 }
        r3 = r3.size();	 Catch:{ all -> 0x0077 }
        if (r3 != 0) goto L_0x0037;
    L_0x0032:
        monitor-exit(r5);	 Catch:{ all -> 0x0077 }
        r5.doPendingDeferredStart();
        return r0;
    L_0x0037:
        r3 = r5.mPendingActions;	 Catch:{ all -> 0x0077 }
        r2 = r3.size();	 Catch:{ all -> 0x0077 }
        r3 = r5.mTmpActions;	 Catch:{ all -> 0x0077 }
        if (r3 == 0) goto L_0x0046;
    L_0x0041:
        r3 = r5.mTmpActions;	 Catch:{ all -> 0x0077 }
        r3 = r3.length;	 Catch:{ all -> 0x0077 }
        if (r3 >= r2) goto L_0x004a;
    L_0x0046:
        r3 = new java.lang.Runnable[r2];	 Catch:{ all -> 0x0077 }
        r5.mTmpActions = r3;	 Catch:{ all -> 0x0077 }
    L_0x004a:
        r3 = r5.mPendingActions;	 Catch:{ all -> 0x0077 }
        r4 = r5.mTmpActions;	 Catch:{ all -> 0x0077 }
        r3.toArray(r4);	 Catch:{ all -> 0x0077 }
        r3 = r5.mPendingActions;	 Catch:{ all -> 0x0077 }
        r3.clear();	 Catch:{ all -> 0x0077 }
        r3 = r5.mHost;	 Catch:{ all -> 0x0077 }
        r3 = r3.getHandler();	 Catch:{ all -> 0x0077 }
        r4 = r5.mExecCommit;	 Catch:{ all -> 0x0077 }
        r3.removeCallbacks(r4);	 Catch:{ all -> 0x0077 }
        monitor-exit(r5);	 Catch:{ all -> 0x0077 }
        r3 = 1;
        r5.mExecutingActions = r3;
        r1 = 0;
    L_0x0066:
        if (r1 >= r2) goto L_0x007a;
    L_0x0068:
        r3 = r5.mTmpActions;
        r3 = r3[r1];
        r3.run();
        r3 = r5.mTmpActions;
        r4 = 0;
        r3[r1] = r4;
        r1 = r1 + 1;
        goto L_0x0066;
    L_0x0077:
        r3 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x0077 }
        throw r3;
    L_0x007a:
        r3 = 0;
        r5.mExecutingActions = r3;
        r0 = 1;
        goto L_0x0025;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.FragmentManagerImpl.execPendingActions():boolean");
    }

    void doPendingDeferredStart() {
        if (this.mHavePendingDeferredStart) {
            boolean loadersRunning = HONEYCOMB;
            for (int i = 0; i < this.mActive.size(); i += ANIM_STYLE_OPEN_ENTER) {
                Fragment f = (Fragment) this.mActive.get(i);
                if (!(f == null || f.mLoaderManager == null)) {
                    loadersRunning |= f.mLoaderManager.hasRunningLoaders();
                }
            }
            if (!loadersRunning) {
                this.mHavePendingDeferredStart = HONEYCOMB;
                startPendingDeferredFragments();
            }
        }
    }

    void reportBackStackChanged() {
        if (this.mBackStackChangeListeners != null) {
            for (int i = 0; i < this.mBackStackChangeListeners.size(); i += ANIM_STYLE_OPEN_ENTER) {
                ((OnBackStackChangedListener) this.mBackStackChangeListeners.get(i)).onBackStackChanged();
            }
        }
    }

    void addBackStackState(BackStackRecord state) {
        if (this.mBackStack == null) {
            this.mBackStack = new ArrayList();
        }
        this.mBackStack.add(state);
        reportBackStackChanged();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    boolean popBackStackState(android.os.Handler r13, java.lang.String r14, int r15, int r16) {
        /*
        r12 = this;
        r9 = r12.mBackStack;
        if (r9 != 0) goto L_0x0006;
    L_0x0004:
        r9 = 0;
    L_0x0005:
        return r9;
    L_0x0006:
        if (r14 != 0) goto L_0x003e;
    L_0x0008:
        if (r15 >= 0) goto L_0x003e;
    L_0x000a:
        r9 = r16 & 1;
        if (r9 != 0) goto L_0x003e;
    L_0x000e:
        r9 = r12.mBackStack;
        r9 = r9.size();
        r5 = r9 + -1;
        if (r5 >= 0) goto L_0x001a;
    L_0x0018:
        r9 = 0;
        goto L_0x0005;
    L_0x001a:
        r9 = r12.mBackStack;
        r1 = r9.remove(r5);
        r1 = (android.support.v4.app.BackStackRecord) r1;
        r2 = new android.util.SparseArray;
        r2.<init>();
        r6 = new android.util.SparseArray;
        r6.<init>();
        r9 = r12.mCurState;
        r10 = 1;
        if (r9 < r10) goto L_0x0034;
    L_0x0031:
        r1.calculateBackFragments(r2, r6);
    L_0x0034:
        r9 = 1;
        r10 = 0;
        r1.popFromBackStack(r9, r10, r2, r6);
        r12.reportBackStackChanged();
    L_0x003c:
        r9 = 1;
        goto L_0x0005;
    L_0x003e:
        r4 = -1;
        if (r14 != 0) goto L_0x0043;
    L_0x0041:
        if (r15 < 0) goto L_0x0093;
    L_0x0043:
        r9 = r12.mBackStack;
        r9 = r9.size();
        r4 = r9 + -1;
    L_0x004b:
        if (r4 < 0) goto L_0x0061;
    L_0x004d:
        r9 = r12.mBackStack;
        r1 = r9.get(r4);
        r1 = (android.support.v4.app.BackStackRecord) r1;
        if (r14 == 0) goto L_0x0065;
    L_0x0057:
        r9 = r1.getName();
        r9 = r14.equals(r9);
        if (r9 == 0) goto L_0x0065;
    L_0x0061:
        if (r4 >= 0) goto L_0x006e;
    L_0x0063:
        r9 = 0;
        goto L_0x0005;
    L_0x0065:
        if (r15 < 0) goto L_0x006b;
    L_0x0067:
        r9 = r1.mIndex;
        if (r15 == r9) goto L_0x0061;
    L_0x006b:
        r4 = r4 + -1;
        goto L_0x004b;
    L_0x006e:
        r9 = r16 & 1;
        if (r9 == 0) goto L_0x0093;
    L_0x0072:
        r4 = r4 + -1;
    L_0x0074:
        if (r4 < 0) goto L_0x0093;
    L_0x0076:
        r9 = r12.mBackStack;
        r1 = r9.get(r4);
        r1 = (android.support.v4.app.BackStackRecord) r1;
        if (r14 == 0) goto L_0x008a;
    L_0x0080:
        r9 = r1.getName();
        r9 = r14.equals(r9);
        if (r9 != 0) goto L_0x0090;
    L_0x008a:
        if (r15 < 0) goto L_0x0093;
    L_0x008c:
        r9 = r1.mIndex;
        if (r15 != r9) goto L_0x0093;
    L_0x0090:
        r4 = r4 + -1;
        goto L_0x0074;
    L_0x0093:
        r9 = r12.mBackStack;
        r9 = r9.size();
        r9 = r9 + -1;
        if (r4 != r9) goto L_0x00a0;
    L_0x009d:
        r9 = 0;
        goto L_0x0005;
    L_0x00a0:
        r8 = new java.util.ArrayList;
        r8.<init>();
        r9 = r12.mBackStack;
        r9 = r9.size();
        r3 = r9 + -1;
    L_0x00ad:
        if (r3 <= r4) goto L_0x00bb;
    L_0x00af:
        r9 = r12.mBackStack;
        r9 = r9.remove(r3);
        r8.add(r9);
        r3 = r3 + -1;
        goto L_0x00ad;
    L_0x00bb:
        r9 = r8.size();
        r0 = r9 + -1;
        r2 = new android.util.SparseArray;
        r2.<init>();
        r6 = new android.util.SparseArray;
        r6.<init>();
        r9 = r12.mCurState;
        r10 = 1;
        if (r9 < r10) goto L_0x00df;
    L_0x00d0:
        r3 = 0;
    L_0x00d1:
        if (r3 > r0) goto L_0x00df;
    L_0x00d3:
        r9 = r8.get(r3);
        r9 = (android.support.v4.app.BackStackRecord) r9;
        r9.calculateBackFragments(r2, r6);
        r3 = r3 + 1;
        goto L_0x00d1;
    L_0x00df:
        r7 = 0;
        r3 = 0;
    L_0x00e1:
        if (r3 > r0) goto L_0x0115;
    L_0x00e3:
        r9 = DEBUG;
        if (r9 == 0) goto L_0x0103;
    L_0x00e7:
        r9 = "FragmentManager";
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "Popping back stack state: ";
        r10 = r10.append(r11);
        r11 = r8.get(r3);
        r10 = r10.append(r11);
        r10 = r10.toString();
        android.util.Log.v(r9, r10);
    L_0x0103:
        r9 = r8.get(r3);
        r9 = (android.support.v4.app.BackStackRecord) r9;
        if (r3 != r0) goto L_0x0113;
    L_0x010b:
        r10 = 1;
    L_0x010c:
        r7 = r9.popFromBackStack(r10, r7, r2, r6);
        r3 = r3 + 1;
        goto L_0x00e1;
    L_0x0113:
        r10 = 0;
        goto L_0x010c;
    L_0x0115:
        r12.reportBackStackChanged();
        goto L_0x003c;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.FragmentManagerImpl.popBackStackState(android.os.Handler, java.lang.String, int, int):boolean");
    }

    FragmentManagerNonConfig retainNonConfig() {
        ArrayList<Fragment> fragments = null;
        ArrayList<FragmentManagerNonConfig> childFragments = null;
        if (this.mActive != null) {
            for (int i = 0; i < this.mActive.size(); i += ANIM_STYLE_OPEN_ENTER) {
                Fragment f = (Fragment) this.mActive.get(i);
                if (f != null) {
                    if (f.mRetainInstance) {
                        if (fragments == null) {
                            fragments = new ArrayList();
                        }
                        fragments.add(f);
                        f.mRetaining = true;
                        f.mTargetIndex = f.mTarget != null ? f.mTarget.mIndex : -1;
                        if (DEBUG) {
                            Log.v(TAG, "retainNonConfig: keeping retained " + f);
                        }
                    }
                    boolean addedChild = HONEYCOMB;
                    if (f.mChildFragmentManager != null) {
                        FragmentManagerNonConfig child = f.mChildFragmentManager.retainNonConfig();
                        if (child != null) {
                            if (childFragments == null) {
                                childFragments = new ArrayList();
                                for (int j = 0; j < i; j += ANIM_STYLE_OPEN_ENTER) {
                                    childFragments.add(null);
                                }
                            }
                            childFragments.add(child);
                            addedChild = true;
                        }
                    }
                    if (!(childFragments == null || addedChild)) {
                        childFragments.add(null);
                    }
                }
            }
        }
        if (fragments == null && childFragments == null) {
            return null;
        }
        return new FragmentManagerNonConfig(fragments, childFragments);
    }

    void saveFragmentViewState(Fragment f) {
        if (f.mInnerView != null) {
            if (this.mStateArray == null) {
                this.mStateArray = new SparseArray();
            } else {
                this.mStateArray.clear();
            }
            f.mInnerView.saveHierarchyState(this.mStateArray);
            if (this.mStateArray.size() > 0) {
                f.mSavedViewState = this.mStateArray;
                this.mStateArray = null;
            }
        }
    }

    Bundle saveFragmentBasicState(Fragment f) {
        Bundle result = null;
        if (this.mStateBundle == null) {
            this.mStateBundle = new Bundle();
        }
        f.performSaveInstanceState(this.mStateBundle);
        if (!this.mStateBundle.isEmpty()) {
            result = this.mStateBundle;
            this.mStateBundle = null;
        }
        if (f.mView != null) {
            saveFragmentViewState(f);
        }
        if (f.mSavedViewState != null) {
            if (result == null) {
                result = new Bundle();
            }
            result.putSparseParcelableArray(VIEW_STATE_TAG, f.mSavedViewState);
        }
        if (!f.mUserVisibleHint) {
            if (result == null) {
                result = new Bundle();
            }
            result.putBoolean(USER_VISIBLE_HINT_TAG, f.mUserVisibleHint);
        }
        return result;
    }

    Parcelable saveAllState() {
        execPendingActions();
        if (HONEYCOMB) {
            this.mStateSaved = true;
        }
        if (this.mActive == null || this.mActive.size() <= 0) {
            return null;
        }
        int i;
        int N = this.mActive.size();
        FragmentState[] active = new FragmentState[N];
        boolean haveFragments = HONEYCOMB;
        for (i = 0; i < N; i += ANIM_STYLE_OPEN_ENTER) {
            Fragment f = (Fragment) this.mActive.get(i);
            if (f != null) {
                if (f.mIndex < 0) {
                    throwException(new IllegalStateException("Failure saving state: active " + f + " has cleared index: " + f.mIndex));
                }
                haveFragments = true;
                FragmentState fs = new FragmentState(f);
                active[i] = fs;
                if (f.mState <= 0 || fs.mSavedFragmentState != null) {
                    fs.mSavedFragmentState = f.mSavedFragmentState;
                } else {
                    fs.mSavedFragmentState = saveFragmentBasicState(f);
                    if (f.mTarget != null) {
                        if (f.mTarget.mIndex < 0) {
                            throwException(new IllegalStateException("Failure saving state: " + f + " has target not in fragment manager: " + f.mTarget));
                        }
                        if (fs.mSavedFragmentState == null) {
                            fs.mSavedFragmentState = new Bundle();
                        }
                        putFragment(fs.mSavedFragmentState, TARGET_STATE_TAG, f.mTarget);
                        if (f.mTargetRequestCode != 0) {
                            fs.mSavedFragmentState.putInt(TARGET_REQUEST_CODE_STATE_TAG, f.mTargetRequestCode);
                        }
                    }
                }
                if (DEBUG) {
                    Log.v(TAG, "Saved state of " + f + ": " + fs.mSavedFragmentState);
                }
            }
        }
        if (haveFragments) {
            int[] added = null;
            BackStackState[] backStack = null;
            if (this.mAdded != null) {
                N = this.mAdded.size();
                if (N > 0) {
                    added = new int[N];
                    for (i = 0; i < N; i += ANIM_STYLE_OPEN_ENTER) {
                        added[i] = ((Fragment) this.mAdded.get(i)).mIndex;
                        if (added[i] < 0) {
                            throwException(new IllegalStateException("Failure saving state: active " + this.mAdded.get(i) + " has cleared index: " + added[i]));
                        }
                        if (DEBUG) {
                            Log.v(TAG, "saveAllState: adding fragment #" + i + ": " + this.mAdded.get(i));
                        }
                    }
                }
            }
            if (this.mBackStack != null) {
                N = this.mBackStack.size();
                if (N > 0) {
                    backStack = new BackStackState[N];
                    for (i = 0; i < N; i += ANIM_STYLE_OPEN_ENTER) {
                        backStack[i] = new BackStackState((BackStackRecord) this.mBackStack.get(i));
                        if (DEBUG) {
                            Log.v(TAG, "saveAllState: adding back stack #" + i + ": " + this.mBackStack.get(i));
                        }
                    }
                }
            }
            Parcelable fms = new FragmentManagerState();
            fms.mActive = active;
            fms.mAdded = added;
            fms.mBackStack = backStack;
            return fms;
        } else if (!DEBUG) {
            return null;
        } else {
            Log.v(TAG, "saveAllState: no fragments!");
            return null;
        }
    }

    void restoreAllState(Parcelable state, FragmentManagerNonConfig nonConfig) {
        if (state != null) {
            FragmentManagerState fms = (FragmentManagerState) state;
            if (fms.mActive != null) {
                List<Fragment> nonConfigFragments;
                int count;
                int i;
                Fragment f;
                FragmentState fs;
                List<FragmentManagerNonConfig> childNonConfigs = null;
                if (nonConfig != null) {
                    nonConfigFragments = nonConfig.getFragments();
                    childNonConfigs = nonConfig.getChildNonConfigs();
                    count = nonConfigFragments != null ? nonConfigFragments.size() : 0;
                    for (i = 0; i < count; i += ANIM_STYLE_OPEN_ENTER) {
                        f = (Fragment) nonConfigFragments.get(i);
                        if (DEBUG) {
                            Log.v(TAG, "restoreAllState: re-attaching retained " + f);
                        }
                        fs = fms.mActive[f.mIndex];
                        fs.mInstance = f;
                        f.mSavedViewState = null;
                        f.mBackStackNesting = 0;
                        f.mInLayout = HONEYCOMB;
                        f.mAdded = HONEYCOMB;
                        f.mTarget = null;
                        if (fs.mSavedFragmentState != null) {
                            fs.mSavedFragmentState.setClassLoader(this.mHost.getContext().getClassLoader());
                            f.mSavedViewState = fs.mSavedFragmentState.getSparseParcelableArray(VIEW_STATE_TAG);
                            f.mSavedFragmentState = fs.mSavedFragmentState;
                        }
                    }
                }
                this.mActive = new ArrayList(fms.mActive.length);
                if (this.mAvailIndices != null) {
                    this.mAvailIndices.clear();
                }
                i = 0;
                while (i < fms.mActive.length) {
                    fs = fms.mActive[i];
                    if (fs != null) {
                        FragmentManagerNonConfig childNonConfig = null;
                        if (childNonConfigs != null && i < childNonConfigs.size()) {
                            childNonConfig = (FragmentManagerNonConfig) childNonConfigs.get(i);
                        }
                        f = fs.instantiate(this.mHost, this.mParent, childNonConfig);
                        if (DEBUG) {
                            Log.v(TAG, "restoreAllState: active #" + i + ": " + f);
                        }
                        this.mActive.add(f);
                        fs.mInstance = null;
                    } else {
                        this.mActive.add(null);
                        if (this.mAvailIndices == null) {
                            this.mAvailIndices = new ArrayList();
                        }
                        if (DEBUG) {
                            Log.v(TAG, "restoreAllState: avail #" + i);
                        }
                        this.mAvailIndices.add(Integer.valueOf(i));
                    }
                    i += ANIM_STYLE_OPEN_ENTER;
                }
                if (nonConfig != null) {
                    nonConfigFragments = nonConfig.getFragments();
                    count = nonConfigFragments != null ? nonConfigFragments.size() : 0;
                    for (i = 0; i < count; i += ANIM_STYLE_OPEN_ENTER) {
                        f = (Fragment) nonConfigFragments.get(i);
                        if (f.mTargetIndex >= 0) {
                            if (f.mTargetIndex < this.mActive.size()) {
                                f.mTarget = (Fragment) this.mActive.get(f.mTargetIndex);
                            } else {
                                Log.w(TAG, "Re-attaching retained fragment " + f + " target no longer exists: " + f.mTargetIndex);
                                f.mTarget = null;
                            }
                        }
                    }
                }
                if (fms.mAdded != null) {
                    this.mAdded = new ArrayList(fms.mAdded.length);
                    for (i = 0; i < fms.mAdded.length; i += ANIM_STYLE_OPEN_ENTER) {
                        f = (Fragment) this.mActive.get(fms.mAdded[i]);
                        if (f == null) {
                            throwException(new IllegalStateException("No instantiated fragment for index #" + fms.mAdded[i]));
                        }
                        f.mAdded = true;
                        if (DEBUG) {
                            Log.v(TAG, "restoreAllState: added #" + i + ": " + f);
                        }
                        if (this.mAdded.contains(f)) {
                            throw new IllegalStateException("Already added!");
                        }
                        this.mAdded.add(f);
                    }
                } else {
                    this.mAdded = null;
                }
                if (fms.mBackStack != null) {
                    this.mBackStack = new ArrayList(fms.mBackStack.length);
                    for (i = 0; i < fms.mBackStack.length; i += ANIM_STYLE_OPEN_ENTER) {
                        BackStackRecord bse = fms.mBackStack[i].instantiate(this);
                        if (DEBUG) {
                            Log.v(TAG, "restoreAllState: back stack #" + i + " (index " + bse.mIndex + "): " + bse);
                            bse.dump("  ", new PrintWriter(new LogWriter(TAG)), HONEYCOMB);
                        }
                        this.mBackStack.add(bse);
                        if (bse.mIndex >= 0) {
                            setBackStackIndex(bse.mIndex, bse);
                        }
                    }
                    return;
                }
                this.mBackStack = null;
            }
        }
    }

    public void attachController(FragmentHostCallback host, FragmentContainer container, Fragment parent) {
        if (this.mHost != null) {
            throw new IllegalStateException("Already attached");
        }
        this.mHost = host;
        this.mContainer = container;
        this.mParent = parent;
    }

    public void noteStateNotSaved() {
        this.mStateSaved = HONEYCOMB;
    }

    public void dispatchCreate() {
        this.mStateSaved = HONEYCOMB;
        moveToState(ANIM_STYLE_OPEN_ENTER, HONEYCOMB);
    }

    public void dispatchActivityCreated() {
        this.mStateSaved = HONEYCOMB;
        moveToState(ANIM_STYLE_OPEN_EXIT, HONEYCOMB);
    }

    public void dispatchStart() {
        this.mStateSaved = HONEYCOMB;
        moveToState(ANIM_STYLE_CLOSE_EXIT, HONEYCOMB);
    }

    public void dispatchResume() {
        this.mStateSaved = HONEYCOMB;
        moveToState(ANIM_STYLE_FADE_ENTER, HONEYCOMB);
    }

    public void dispatchPause() {
        moveToState(ANIM_STYLE_CLOSE_EXIT, HONEYCOMB);
    }

    public void dispatchStop() {
        this.mStateSaved = true;
        moveToState(ANIM_STYLE_CLOSE_ENTER, HONEYCOMB);
    }

    public void dispatchReallyStop() {
        moveToState(ANIM_STYLE_OPEN_EXIT, HONEYCOMB);
    }

    public void dispatchDestroyView() {
        moveToState(ANIM_STYLE_OPEN_ENTER, HONEYCOMB);
    }

    public void dispatchDestroy() {
        this.mDestroyed = true;
        execPendingActions();
        moveToState(0, HONEYCOMB);
        this.mHost = null;
        this.mContainer = null;
        this.mParent = null;
    }

    public void dispatchMultiWindowModeChanged(boolean isInMultiWindowMode) {
        if (this.mAdded != null) {
            for (int i = this.mAdded.size() - 1; i >= 0; i--) {
                Fragment f = (Fragment) this.mAdded.get(i);
                if (f != null) {
                    f.performMultiWindowModeChanged(isInMultiWindowMode);
                }
            }
        }
    }

    public void dispatchPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        if (this.mAdded != null) {
            for (int i = this.mAdded.size() - 1; i >= 0; i--) {
                Fragment f = (Fragment) this.mAdded.get(i);
                if (f != null) {
                    f.performPictureInPictureModeChanged(isInPictureInPictureMode);
                }
            }
        }
    }

    public void dispatchConfigurationChanged(Configuration newConfig) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i += ANIM_STYLE_OPEN_ENTER) {
                Fragment f = (Fragment) this.mAdded.get(i);
                if (f != null) {
                    f.performConfigurationChanged(newConfig);
                }
            }
        }
    }

    public void dispatchLowMemory() {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i += ANIM_STYLE_OPEN_ENTER) {
                Fragment f = (Fragment) this.mAdded.get(i);
                if (f != null) {
                    f.performLowMemory();
                }
            }
        }
    }

    public boolean dispatchCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int i;
        Fragment f;
        boolean show = HONEYCOMB;
        ArrayList<Fragment> newMenus = null;
        if (this.mAdded != null) {
            for (i = 0; i < this.mAdded.size(); i += ANIM_STYLE_OPEN_ENTER) {
                f = (Fragment) this.mAdded.get(i);
                if (f != null && f.performCreateOptionsMenu(menu, inflater)) {
                    show = true;
                    if (newMenus == null) {
                        newMenus = new ArrayList();
                    }
                    newMenus.add(f);
                }
            }
        }
        if (this.mCreatedMenus != null) {
            for (i = 0; i < this.mCreatedMenus.size(); i += ANIM_STYLE_OPEN_ENTER) {
                f = (Fragment) this.mCreatedMenus.get(i);
                if (newMenus == null || !newMenus.contains(f)) {
                    f.onDestroyOptionsMenu();
                }
            }
        }
        this.mCreatedMenus = newMenus;
        return show;
    }

    public boolean dispatchPrepareOptionsMenu(Menu menu) {
        boolean show = HONEYCOMB;
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i += ANIM_STYLE_OPEN_ENTER) {
                Fragment f = (Fragment) this.mAdded.get(i);
                if (f != null && f.performPrepareOptionsMenu(menu)) {
                    show = true;
                }
            }
        }
        return show;
    }

    public boolean dispatchOptionsItemSelected(MenuItem item) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i += ANIM_STYLE_OPEN_ENTER) {
                Fragment f = (Fragment) this.mAdded.get(i);
                if (f != null && f.performOptionsItemSelected(item)) {
                    return true;
                }
            }
        }
        return HONEYCOMB;
    }

    public boolean dispatchContextItemSelected(MenuItem item) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i += ANIM_STYLE_OPEN_ENTER) {
                Fragment f = (Fragment) this.mAdded.get(i);
                if (f != null && f.performContextItemSelected(item)) {
                    return true;
                }
            }
        }
        return HONEYCOMB;
    }

    public void dispatchOptionsMenuClosed(Menu menu) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i += ANIM_STYLE_OPEN_ENTER) {
                Fragment f = (Fragment) this.mAdded.get(i);
                if (f != null) {
                    f.performOptionsMenuClosed(menu);
                }
            }
        }
    }

    public static int reverseTransit(int transit) {
        switch (transit) {
            case FragmentTransaction.TRANSIT_FRAGMENT_OPEN /*4097*/:
                return InputDeviceCompat.SOURCE_MOUSE;
            case FragmentTransaction.TRANSIT_FRAGMENT_FADE /*4099*/:
                return FragmentTransaction.TRANSIT_FRAGMENT_FADE;
            case InputDeviceCompat.SOURCE_MOUSE /*8194*/:
                return FragmentTransaction.TRANSIT_FRAGMENT_OPEN;
            default:
                return 0;
        }
    }

    public static int transitToStyleIndex(int transit, boolean enter) {
        int animAttr = -1;
        switch (transit) {
            case FragmentTransaction.TRANSIT_FRAGMENT_OPEN /*4097*/:
                animAttr = enter ? ANIM_STYLE_OPEN_ENTER : ANIM_STYLE_OPEN_EXIT;
                break;
            case FragmentTransaction.TRANSIT_FRAGMENT_FADE /*4099*/:
                animAttr = enter ? ANIM_STYLE_FADE_ENTER : ANIM_STYLE_FADE_EXIT;
                break;
            case InputDeviceCompat.SOURCE_MOUSE /*8194*/:
                animAttr = enter ? ANIM_STYLE_CLOSE_ENTER : ANIM_STYLE_CLOSE_EXIT;
                break;
        }
        return animAttr;
    }

    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        if (!"fragment".equals(name)) {
            return null;
        }
        String fname = attrs.getAttributeValue(null, "class");
        TypedArray a = context.obtainStyledAttributes(attrs, FragmentTag.Fragment);
        if (fname == null) {
            fname = a.getString(0);
        }
        int id = a.getResourceId(ANIM_STYLE_OPEN_ENTER, -1);
        String tag = a.getString(ANIM_STYLE_OPEN_EXIT);
        a.recycle();
        if (!Fragment.isSupportFragmentClass(this.mHost.getContext(), fname)) {
            return null;
        }
        int containerId;
        if (parent != null) {
            containerId = parent.getId();
        } else {
            containerId = 0;
        }
        if (containerId == -1 && id == -1 && tag == null) {
            throw new IllegalArgumentException(attrs.getPositionDescription() + ": Must specify unique android:id, android:tag, or have a parent with an id for " + fname);
        }
        Fragment fragment;
        if (id != -1) {
            fragment = findFragmentById(id);
        } else {
            fragment = null;
        }
        if (fragment == null && tag != null) {
            fragment = findFragmentByTag(tag);
        }
        if (fragment == null && containerId != -1) {
            fragment = findFragmentById(containerId);
        }
        if (DEBUG) {
            Log.v(TAG, "onCreateView: id=0x" + Integer.toHexString(id) + " fname=" + fname + " existing=" + fragment);
        }
        if (fragment == null) {
            int i;
            fragment = Fragment.instantiate(context, fname);
            fragment.mFromLayout = true;
            if (id != 0) {
                i = id;
            } else {
                i = containerId;
            }
            fragment.mFragmentId = i;
            fragment.mContainerId = containerId;
            fragment.mTag = tag;
            fragment.mInLayout = true;
            fragment.mFragmentManager = this;
            fragment.mHost = this.mHost;
            fragment.onInflate(this.mHost.getContext(), attrs, fragment.mSavedFragmentState);
            addFragment(fragment, true);
        } else if (fragment.mInLayout) {
            throw new IllegalArgumentException(attrs.getPositionDescription() + ": Duplicate id 0x" + Integer.toHexString(id) + ", tag " + tag + ", or parent id 0x" + Integer.toHexString(containerId) + " with another fragment for " + fname);
        } else {
            fragment.mInLayout = true;
            fragment.mHost = this.mHost;
            if (!fragment.mRetaining) {
                fragment.onInflate(this.mHost.getContext(), attrs, fragment.mSavedFragmentState);
            }
        }
        if (this.mCurState >= ANIM_STYLE_OPEN_ENTER || !fragment.mFromLayout) {
            moveToState(fragment);
        } else {
            moveToState(fragment, ANIM_STYLE_OPEN_ENTER, 0, 0, HONEYCOMB);
        }
        if (fragment.mView == null) {
            throw new IllegalStateException("Fragment " + fname + " did not create a view.");
        }
        if (id != 0) {
            fragment.mView.setId(id);
        }
        if (fragment.mView.getTag() == null) {
            fragment.mView.setTag(tag);
        }
        return fragment.mView;
    }

    LayoutInflaterFactory getLayoutInflaterFactory() {
        return this;
    }
}
