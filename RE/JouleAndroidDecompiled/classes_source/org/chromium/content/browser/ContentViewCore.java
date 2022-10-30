package org.chromium.content.browser;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.Editable;
import android.text.Editable.Factory;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.google.android.gms.actions.SearchIntents;
import com.google.android.gms.common.ConnectionResult;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.base.CalledByNative;
import org.chromium.base.CommandLine;
import org.chromium.base.JNINamespace;
import org.chromium.base.Log;
import org.chromium.base.ObserverList;
import org.chromium.base.ObserverList.RewindableIterator;
import org.chromium.base.TraceEvent;
import org.chromium.base.VisibleForTesting;
import org.chromium.content.C0317R;
import org.chromium.content.browser.PopupZoomer.OnTapListener;
import org.chromium.content.browser.PopupZoomer.OnVisibilityChangedListener;
import org.chromium.content.browser.ScreenOrientationListener.ScreenOrientationObserver;
import org.chromium.content.browser.SelectActionModeCallback.ActionHandler;
import org.chromium.content.browser.accessibility.AccessibilityInjector;
import org.chromium.content.browser.accessibility.BrowserAccessibilityManager;
import org.chromium.content.browser.accessibility.captioning.CaptioningBridgeFactory;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.chromium.content.browser.accessibility.captioning.SystemCaptioningBridge;
import org.chromium.content.browser.accessibility.captioning.SystemCaptioningBridge.SystemCaptioningBridgeListener;
import org.chromium.content.browser.accessibility.captioning.TextTrackSettings;
import org.chromium.content.browser.input.AdapterInputConnection;
import org.chromium.content.browser.input.GamepadList;
import org.chromium.content.browser.input.ImeAdapter;
import org.chromium.content.browser.input.ImeAdapter.AdapterInputConnectionFactory;
import org.chromium.content.browser.input.ImeAdapter.ImeAdapterDelegate;
import org.chromium.content.browser.input.InputMethodManagerWrapper;
import org.chromium.content.browser.input.PastePopupMenu;
import org.chromium.content.browser.input.PastePopupMenu.PastePopupMenuDelegate;
import org.chromium.content.browser.input.PopupTouchHandleDrawable;
import org.chromium.content.browser.input.PopupTouchHandleDrawable.PopupTouchHandleDrawableDelegate;
import org.chromium.content.browser.input.SelectPopup;
import org.chromium.content.browser.input.SelectPopupDialog;
import org.chromium.content.browser.input.SelectPopupDropdown;
import org.chromium.content.browser.input.SelectPopupItem;
import org.chromium.content.common.ContentSwitches;
import org.chromium.content_public.browser.AccessibilitySnapshotCallback;
import org.chromium.content_public.browser.AccessibilitySnapshotNode;
import org.chromium.content_public.browser.GestureStateListener;
import org.chromium.content_public.browser.WebContents;
import org.chromium.content_public.browser.WebContentsObserver;
import org.chromium.ui.base.DeviceFormFactor;
import org.chromium.ui.base.PageTransition;
import org.chromium.ui.base.ViewAndroidDelegate;
import org.chromium.ui.base.WindowAndroid;
import org.chromium.ui.base.ime.TextInputType;
import org.chromium.ui.gfx.DeviceDisplayInfo;

@JNINamespace("content")
public class ContentViewCore implements AccessibilityStateChangeListener, ScreenOrientationObserver, SystemCaptioningBridgeListener {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final int INVALID_RENDER_PROCESS_PID = 0;
    private static final int IS_LONG_PRESS = 1;
    private static final int IS_LONG_TAP = 2;
    private static final ZoomControlsDelegate NO_OP_ZOOM_CONTROLS_DELEGATE;
    private static final String TAG = "cr.ContentViewCore";
    private static final int TEXT_STYLE_BOLD = 1;
    private static final int TEXT_STYLE_ITALIC = 2;
    private static final int TEXT_STYLE_STRIKE_THRU = 8;
    private static final int TEXT_STYLE_UNDERLINE = 4;
    private static final float ZOOM_CONTROLS_EPSILON = 0.007f;
    private AccessibilityInjector mAccessibilityInjector;
    private final AccessibilityManager mAccessibilityManager;
    private ContentObserver mAccessibilityScriptInjectionObserver;
    private ActionHandler mActionHandler;
    private SelectActionMode mActionMode;
    private AdapterInputConnectionFactory mAdapterInputConnectionFactory;
    private BrowserAccessibilityManager mBrowserAccessibilityManager;
    private ViewGroup mContainerView;
    private InternalAccessDelegate mContainerViewInternals;
    private ObserverList<ContainerViewObserver> mContainerViewObservers;
    private ContentViewClient mContentViewClient;
    private final Context mContext;
    private ContextualSearchClient mContextualSearchClient;
    private float mCurrentTouchOffsetX;
    private float mCurrentTouchOffsetY;
    private ContentViewDownloadDelegate mDownloadDelegate;
    private final Editable mEditable;
    private Boolean mEnableTouchHover;
    private Runnable mFakeMouseMoveRunnable;
    private boolean mFloatingActionModeCreationFailed;
    private final Rect mFocusPreOSKViewportRect;
    private boolean mFocusedNodeEditable;
    private boolean mFullscreenRequiredForOrientationLock;
    private final ObserverList<GestureStateListener> mGestureStateListeners;
    private final RewindableIterator<GestureStateListener> mGestureStateListenersIterator;
    private boolean mHasInsertion;
    private boolean mHasSelection;
    private ImeAdapter mImeAdapter;
    private AdapterInputConnection mInputConnection;
    private InputMethodManagerWrapper mInputMethodManagerWrapper;
    private boolean mIsMobileOptimizedHint;
    private final Map<String, Pair<Object, Class>> mJavaScriptInterfaces;
    private String mLastSelectedText;
    private int mLastTapX;
    private int mLastTapY;
    private boolean mNativeAccessibilityAllowed;
    private boolean mNativeAccessibilityEnabled;
    private long mNativeContentViewCore;
    private long mNativeSelectPopupSourceFrame;
    private OverscrollRefreshHandler mOverscrollRefreshHandler;
    private PastePopupMenu mPastePopupMenu;
    private int mPhysicalBackingHeightPix;
    private int mPhysicalBackingWidthPix;
    private PopupZoomer mPopupZoomer;
    private PositionObserver mPositionObserver;
    private int mPotentiallyActiveFlingCount;
    private boolean mPreserveSelectionOnNextLossOfFocus;
    private final RenderCoordinates mRenderCoordinates;
    private final HashSet<Object> mRetainedJavaScriptObjects;
    private SelectPopup mSelectPopup;
    private final Rect mSelectionRect;
    private boolean mShouldSetAccessibilityFocusOnPageLoad;
    private SmartClipDataListener mSmartClipDataListener;
    private int mSmartClipOffsetX;
    private int mSmartClipOffsetY;
    private final SystemCaptioningBridge mSystemCaptioningBridge;
    private int mTopControlsHeightPix;
    private boolean mTopControlsShrinkBlinkSize;
    private boolean mTouchExplorationEnabled;
    private PopupTouchHandleDrawableDelegate mTouchHandleDelegate;
    private boolean mTouchScrollInProgress;
    private boolean mUnselectAllOnActionModeDismiss;
    private ContentViewAndroidDelegate mViewAndroidDelegate;
    private int mViewportHeightPix;
    private int mViewportWidthPix;
    private boolean mWasPastePopupShowingOnInsertionDragStart;
    private WebContents mWebContents;
    private WebContentsObserver mWebContentsObserver;
    private ZoomControlsDelegate mZoomControlsDelegate;

    /* renamed from: org.chromium.content.browser.ContentViewCore.10 */
    class AnonymousClass10 extends ContentObserver {
        AnonymousClass10(Handler x0) {
            super(x0);
        }

        public void onChange(boolean selfChange, Uri uri) {
            ContentViewCore.this.setAccessibilityState(ContentViewCore.this.mAccessibilityManager.isEnabled());
        }
    }

    /* renamed from: org.chromium.content.browser.ContentViewCore.5 */
    class C03375 implements Runnable {
        final /* synthetic */ MotionEvent val$eventFakeMouseMove;

        C03375(MotionEvent motionEvent) {
            this.val$eventFakeMouseMove = motionEvent;
        }

        public void run() {
            ContentViewCore.this.onHoverEvent(this.val$eventFakeMouseMove);
            this.val$eventFakeMouseMove.recycle();
        }
    }

    public interface InternalAccessDelegate {
        boolean awakenScrollBars();

        boolean drawChild(Canvas canvas, View view, long j);

        void onScrollChanged(int i, int i2, int i3, int i4);

        boolean super_awakenScrollBars(int i, boolean z);

        boolean super_dispatchKeyEvent(KeyEvent keyEvent);

        boolean super_dispatchKeyEventPreIme(KeyEvent keyEvent);

        void super_onConfigurationChanged(Configuration configuration);

        boolean super_onGenericMotionEvent(MotionEvent motionEvent);

        boolean super_onKeyUp(int i, KeyEvent keyEvent);
    }

    public interface SmartClipDataListener {
        void onSmartClipDataExtracted(String str, String str2, Rect rect);
    }

    public interface ZoomControlsDelegate {
        void dismissZoomPicker();

        void invokeZoomPicker();

        void updateZoomControls();
    }

    /* renamed from: org.chromium.content.browser.ContentViewCore.1 */
    static class C06001 implements ZoomControlsDelegate {
        C06001() {
        }

        public void invokeZoomPicker() {
        }

        public void dismissZoomPicker() {
        }

        public void updateZoomControls() {
        }
    }

    /* renamed from: org.chromium.content.browser.ContentViewCore.2 */
    class C06012 implements ImeAdapterDelegate {
        static final /* synthetic */ boolean $assertionsDisabled;

        /* renamed from: org.chromium.content.browser.ContentViewCore.2.1 */
        class C03341 extends ResultReceiver {
            static final /* synthetic */ boolean $assertionsDisabled;

            static {
                $assertionsDisabled = !ContentViewCore.class.desiredAssertionStatus() ? true : ContentViewCore.$assertionsDisabled;
            }

            C03341(Handler x0) {
                super(x0);
            }

            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == ContentViewCore.TEXT_STYLE_ITALIC) {
                    ContentViewCore.this.getContainerView().getWindowVisibleDisplayFrame(ContentViewCore.this.mFocusPreOSKViewportRect);
                } else if (!ContentViewCore.this.hasFocus() || resultCode != 0) {
                } else {
                    if ($assertionsDisabled || ContentViewCore.this.mWebContents != null) {
                        ContentViewCore.this.mWebContents.scrollFocusedEditableNodeIntoView();
                        return;
                    }
                    throw new AssertionError();
                }
            }
        }

        static {
            $assertionsDisabled = !ContentViewCore.class.desiredAssertionStatus() ? true : ContentViewCore.$assertionsDisabled;
        }

        C06012() {
        }

        public void onImeEvent() {
            ContentViewCore.this.mPopupZoomer.hide(true);
            ContentViewCore.this.getContentViewClient().onImeEvent();
            if (ContentViewCore.this.mFocusedNodeEditable) {
                ContentViewCore.this.dismissTextHandles();
            }
        }

        public void onKeyboardBoundsUnchanged() {
            if ($assertionsDisabled || ContentViewCore.this.mWebContents != null) {
                ContentViewCore.this.mWebContents.scrollFocusedEditableNodeIntoView();
                return;
            }
            throw new AssertionError();
        }

        public boolean performContextMenuAction(int id) {
            if ($assertionsDisabled || ContentViewCore.this.mWebContents != null) {
                switch (id) {
                    case 16908319:
                        ContentViewCore.this.mWebContents.selectAll();
                        return true;
                    case 16908320:
                        ContentViewCore.this.mWebContents.cut();
                        return true;
                    case 16908321:
                        ContentViewCore.this.mWebContents.copy();
                        return true;
                    case 16908322:
                        ContentViewCore.this.mWebContents.paste();
                        return true;
                    default:
                        return ContentViewCore.$assertionsDisabled;
                }
            }
            throw new AssertionError();
        }

        public View getAttachedView() {
            return ContentViewCore.this.mContainerView;
        }

        public ResultReceiver getNewShowKeyboardReceiver() {
            return new C03341(new Handler());
        }
    }

    /* renamed from: org.chromium.content.browser.ContentViewCore.3 */
    class C06023 implements OnVisibilityChangedListener {
        private final ViewGroup mContainerViewAtCreation;

        /* renamed from: org.chromium.content.browser.ContentViewCore.3.1 */
        class C03351 implements Runnable {
            final /* synthetic */ PopupZoomer val$zoomer;

            C03351(PopupZoomer popupZoomer) {
                this.val$zoomer = popupZoomer;
            }

            public void run() {
                if (C06023.this.mContainerViewAtCreation.indexOfChild(this.val$zoomer) == -1) {
                    C06023.this.mContainerViewAtCreation.addView(this.val$zoomer);
                }
            }
        }

        /* renamed from: org.chromium.content.browser.ContentViewCore.3.2 */
        class C03362 implements Runnable {
            final /* synthetic */ PopupZoomer val$zoomer;

            C03362(PopupZoomer popupZoomer) {
                this.val$zoomer = popupZoomer;
            }

            public void run() {
                if (C06023.this.mContainerViewAtCreation.indexOfChild(this.val$zoomer) != -1) {
                    C06023.this.mContainerViewAtCreation.removeView(this.val$zoomer);
                    C06023.this.mContainerViewAtCreation.invalidate();
                }
            }
        }

        C06023() {
            this.mContainerViewAtCreation = ContentViewCore.this.mContainerView;
        }

        public void onPopupZoomerShown(PopupZoomer zoomer) {
            this.mContainerViewAtCreation.post(new C03351(zoomer));
        }

        public void onPopupZoomerHidden(PopupZoomer zoomer) {
            this.mContainerViewAtCreation.post(new C03362(zoomer));
        }
    }

    /* renamed from: org.chromium.content.browser.ContentViewCore.4 */
    class C06034 implements OnTapListener {
        private final ViewGroup mContainerViewAtCreation;

        C06034() {
            this.mContainerViewAtCreation = ContentViewCore.this.mContainerView;
        }

        public boolean onSingleTap(View v, MotionEvent e) {
            this.mContainerViewAtCreation.requestFocus();
            if (ContentViewCore.this.mNativeContentViewCore != 0) {
                ContentViewCore.this.nativeSingleTap(ContentViewCore.this.mNativeContentViewCore, e.getEventTime(), e.getX(), e.getY());
            }
            return true;
        }

        public boolean onLongPress(View v, MotionEvent e) {
            if (ContentViewCore.this.mNativeContentViewCore != 0) {
                ContentViewCore.this.nativeLongPress(ContentViewCore.this.mNativeContentViewCore, e.getEventTime(), e.getX(), e.getY());
            }
            return true;
        }
    }

    /* renamed from: org.chromium.content.browser.ContentViewCore.6 */
    class C06046 implements ActionHandler {
        C06046() {
        }

        public void selectAll() {
            ContentViewCore.this.mWebContents.selectAll();
        }

        public void cut() {
            ContentViewCore.this.mWebContents.cut();
        }

        public void copy() {
            ContentViewCore.this.mWebContents.copy();
        }

        public void paste() {
            ContentViewCore.this.mWebContents.paste();
        }

        public void share() {
            String query = ContentViewCore.this.getSelectedText();
            if (!TextUtils.isEmpty(query)) {
                Intent send = new Intent("android.intent.action.SEND");
                send.setType("text/plain");
                send.putExtra("android.intent.extra.TEXT", query);
                try {
                    Intent i = Intent.createChooser(send, ContentViewCore.this.getContext().getString(C0317R.string.actionbar_share));
                    i.setFlags(PageTransition.CHAIN_START);
                    ContentViewCore.this.getContext().startActivity(i);
                } catch (ActivityNotFoundException e) {
                }
            }
        }

        public void search() {
            String query = ContentViewCore.this.getSelectedText();
            if (!TextUtils.isEmpty(query)) {
                if (ContentViewCore.this.getContentViewClient().doesPerformWebSearch()) {
                    ContentViewCore.this.getContentViewClient().performWebSearch(query);
                    return;
                }
                Intent i = new Intent("android.intent.action.WEB_SEARCH");
                i.putExtra("new_search", true);
                i.putExtra(SearchIntents.EXTRA_QUERY, query);
                i.putExtra("com.android.browser.application_id", ContentViewCore.this.getContext().getPackageName());
                i.addFlags(PageTransition.CHAIN_START);
                try {
                    ContentViewCore.this.getContext().startActivity(i);
                } catch (ActivityNotFoundException e) {
                }
            }
        }

        public boolean isSelectionPassword() {
            return ContentViewCore.this.mImeAdapter.isSelectionPassword();
        }

        public boolean isSelectionEditable() {
            return ContentViewCore.this.mFocusedNodeEditable;
        }

        public boolean isInsertion() {
            return ContentViewCore.this.mHasInsertion;
        }

        public void onDestroyActionMode() {
            ContentViewCore.this.mActionMode = null;
            if (ContentViewCore.this.mUnselectAllOnActionModeDismiss) {
                ContentViewCore.this.dismissTextHandles();
                ContentViewCore.this.clearUserSelection();
            }
            ContentViewCore.this.getContentViewClient().onContextualActionBarHidden();
        }

        public void onGetContentRect(Rect outRect) {
            outRect.set(ContentViewCore.this.mSelectionRect);
        }

        public boolean isShareAvailable() {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            return ContentViewCore.this.getContext().getPackageManager().queryIntentActivities(intent, AccessibilityNodeInfoCompat.ACTION_CUT).size() > 0 ? true : ContentViewCore.$assertionsDisabled;
        }

        public boolean isWebSearchAvailable() {
            if (ContentViewCore.this.getContentViewClient().doesPerformWebSearch()) {
                return true;
            }
            Intent intent = new Intent("android.intent.action.WEB_SEARCH");
            intent.putExtra("new_search", true);
            if (ContentViewCore.this.getContext().getPackageManager().queryIntentActivities(intent, AccessibilityNodeInfoCompat.ACTION_CUT).size() <= 0) {
                return ContentViewCore.$assertionsDisabled;
            }
            return true;
        }

        public boolean isIncognito() {
            return ContentViewCore.this.mWebContents.isIncognito();
        }
    }

    /* renamed from: org.chromium.content.browser.ContentViewCore.7 */
    class C06057 implements PopupTouchHandleDrawableDelegate {
        C06057() {
        }

        public View getParent() {
            return ContentViewCore.this.getContainerView();
        }

        public PositionObserver getParentPositionObserver() {
            return ContentViewCore.this.mPositionObserver;
        }

        public boolean onTouchHandleEvent(MotionEvent event) {
            return ContentViewCore.this.onTouchEventImpl(event, true);
        }

        public boolean isScrollInProgress() {
            return ContentViewCore.this.isScrollInProgress();
        }
    }

    /* renamed from: org.chromium.content.browser.ContentViewCore.8 */
    class C06068 implements PastePopupMenuDelegate {
        C06068() {
        }

        public void paste() {
            ContentViewCore.this.mWebContents.paste();
            ContentViewCore.this.dismissTextHandles();
        }
    }

    /* renamed from: org.chromium.content.browser.ContentViewCore.9 */
    class C06079 extends AccessibilitySnapshotCallback {
        final /* synthetic */ ViewStructure val$viewRoot;

        C06079(ViewStructure viewStructure) {
            this.val$viewRoot = viewStructure;
        }

        public void onAccessibilitySnapshot(AccessibilitySnapshotNode root) {
            this.val$viewRoot.setClassName(CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
            if (root == null) {
                this.val$viewRoot.asyncCommit();
            } else {
                ContentViewCore.this.createVirtualStructure(this.val$viewRoot, root, ContentViewCore.INVALID_RENDER_PROCESS_PID, ContentViewCore.INVALID_RENDER_PROCESS_PID);
            }
        }
    }

    private static class ContentViewAndroidDelegate implements ViewAndroidDelegate {
        static final /* synthetic */ boolean $assertionsDisabled;
        private Map<View, Position> mAnchorViews;
        private WeakReference<ViewGroup> mCurrentContainerView;
        private final RenderCoordinates mRenderCoordinates;

        @VisibleForTesting
        private static class Position {
            private final float mHeight;
            private final float mWidth;
            private final float mX;
            private final float mY;

            public Position(float x, float y, float width, float height) {
                this.mX = x;
                this.mY = y;
                this.mWidth = width;
                this.mHeight = height;
            }
        }

        static {
            $assertionsDisabled = !ContentViewCore.class.desiredAssertionStatus() ? true : ContentViewCore.$assertionsDisabled;
        }

        ContentViewAndroidDelegate(ViewGroup containerView, RenderCoordinates renderCoordinates) {
            this.mAnchorViews = new LinkedHashMap();
            this.mRenderCoordinates = renderCoordinates;
            this.mCurrentContainerView = new WeakReference(containerView);
        }

        public View acquireAnchorView() {
            ViewGroup containerView = (ViewGroup) this.mCurrentContainerView.get();
            if (containerView == null) {
                return null;
            }
            View anchorView = new View(containerView.getContext());
            this.mAnchorViews.put(anchorView, null);
            containerView.addView(anchorView);
            return anchorView;
        }

        public void setAnchorViewPosition(View view, float x, float y, float width, float height) {
            this.mAnchorViews.put(view, new Position(x, y, width, height));
            doSetAnchorViewPosition(view, x, y, width, height);
        }

        private void doSetAnchorViewPosition(View view, float x, float y, float width, float height) {
            if (view.getParent() != null) {
                ViewParent containerView = (ViewGroup) this.mCurrentContainerView.get();
                if (containerView == null) {
                    return;
                }
                if ($assertionsDisabled || view.getParent() == containerView) {
                    float scale = (float) DeviceDisplayInfo.create(containerView.getContext()).getDIPScale();
                    int leftMargin = Math.round(x * scale);
                    int topMargin = Math.round(this.mRenderCoordinates.getContentOffsetYPix() + (y * scale));
                    int scaledWidth = Math.round(width * scale);
                    if (containerView instanceof FrameLayout) {
                        int startMargin;
                        if (ApiCompatibilityUtils.isLayoutRtl(containerView)) {
                            startMargin = containerView.getMeasuredWidth() - Math.round((width + x) * scale);
                        } else {
                            startMargin = leftMargin;
                        }
                        if (scaledWidth + startMargin > containerView.getWidth()) {
                            scaledWidth = containerView.getWidth() - startMargin;
                        }
                        LayoutParams lp = new LayoutParams(scaledWidth, Math.round(height * scale));
                        ApiCompatibilityUtils.setMarginStart(lp, startMargin);
                        lp.topMargin = topMargin;
                        view.setLayoutParams(lp);
                        return;
                    } else if (containerView instanceof AbsoluteLayout) {
                        view.setLayoutParams(new AbsoluteLayout.LayoutParams(scaledWidth, (int) (height * scale), leftMargin + this.mRenderCoordinates.getScrollXPixInt(), topMargin + this.mRenderCoordinates.getScrollYPixInt()));
                        return;
                    } else {
                        Object[] objArr = new Object[ContentViewCore.TEXT_STYLE_BOLD];
                        objArr[ContentViewCore.INVALID_RENDER_PROCESS_PID] = containerView.getClass().getName();
                        Log.m32e(ContentViewCore.TAG, "Unknown layout %s", objArr);
                        return;
                    }
                }
                throw new AssertionError();
            }
        }

        public void releaseAnchorView(View anchorView) {
            this.mAnchorViews.remove(anchorView);
            ViewGroup containerView = (ViewGroup) this.mCurrentContainerView.get();
            if (containerView != null) {
                containerView.removeView(anchorView);
            }
        }

        void updateCurrentContainerView(ViewGroup containerView) {
            ViewGroup oldContainerView = (ViewGroup) this.mCurrentContainerView.get();
            this.mCurrentContainerView = new WeakReference(containerView);
            for (Entry<View, Position> entry : this.mAnchorViews.entrySet()) {
                View anchorView = (View) entry.getKey();
                Position position = (Position) entry.getValue();
                if (oldContainerView != null) {
                    oldContainerView.removeView(anchorView);
                }
                containerView.addView(anchorView);
                if (position != null) {
                    doSetAnchorViewPosition(anchorView, position.mX, position.mY, position.mWidth, position.mHeight);
                }
            }
        }
    }

    private static class ContentViewWebContentsObserver extends WebContentsObserver {
        private final WeakReference<ContentViewCore> mWeakContentViewCore;

        ContentViewWebContentsObserver(ContentViewCore contentViewCore) {
            super(contentViewCore.getWebContents());
            this.mWeakContentViewCore = new WeakReference(contentViewCore);
        }

        public void didStartLoading(String url) {
            ContentViewCore contentViewCore = (ContentViewCore) this.mWeakContentViewCore.get();
            if (contentViewCore != null) {
                contentViewCore.mAccessibilityInjector.onPageLoadStarted();
            }
        }

        public void didStopLoading(String url) {
            ContentViewCore contentViewCore = (ContentViewCore) this.mWeakContentViewCore.get();
            if (contentViewCore != null) {
                contentViewCore.mAccessibilityInjector.onPageLoadStopped();
            }
        }

        public void didFailLoad(boolean isProvisionalLoad, boolean isMainFrame, int errorCode, String description, String failingUrl, boolean wasIgnoredByHandler) {
            if (isProvisionalLoad) {
                determinedProcessVisibility();
            }
        }

        public void didNavigateMainFrame(String url, String baseUrl, boolean isNavigationToDifferentPage, boolean isFragmentNavigation, int statusCode) {
            if (isNavigationToDifferentPage) {
                resetPopupsAndInput();
            }
        }

        public void renderProcessGone(boolean wasOomProtected) {
            resetPopupsAndInput();
        }

        public void navigationEntryCommitted() {
            determinedProcessVisibility();
        }

        private void resetPopupsAndInput() {
            ContentViewCore contentViewCore = (ContentViewCore) this.mWeakContentViewCore.get();
            if (contentViewCore != null) {
                contentViewCore.mIsMobileOptimizedHint = ContentViewCore.$assertionsDisabled;
                contentViewCore.hidePopupsAndClearSelection();
                contentViewCore.resetScrollInProgress();
            }
        }

        private void determinedProcessVisibility() {
            ContentViewCore contentViewCore = (ContentViewCore) this.mWeakContentViewCore.get();
            if (contentViewCore != null) {
                ChildProcessLauncher.determinedVisibility(contentViewCore.getCurrentRenderProcessId());
            }
        }
    }

    private native void nativeAddJavascriptInterface(long j, Object obj, String str, Class cls);

    private native void nativeDismissTextHandles(long j);

    private native void nativeDoubleTap(long j, long j2, float f, float f2);

    private native void nativeExtractSmartClipData(long j, int i, int i2, int i3, int i4);

    private native void nativeFlingCancel(long j, long j2);

    private native void nativeFlingStart(long j, long j2, float f, float f2, float f3, float f4, boolean z);

    private static native ContentViewCore nativeFromWebContentsAndroid(WebContents webContents);

    private native int nativeGetCurrentRenderProcessId(long j);

    private native WindowAndroid nativeGetJavaWindowAndroid(long j);

    private native long nativeGetNativeImeAdapter(long j);

    private native WebContents nativeGetWebContentsAndroid(long j);

    private native long nativeInit(WebContents webContents, ViewAndroidDelegate viewAndroidDelegate, long j, HashSet<Object> hashSet);

    private native void nativeLongPress(long j, long j2, float f, float f2);

    private native void nativeMoveCaret(long j, float f, float f2);

    private native void nativeOnJavaContentViewCoreDestroyed(long j);

    private native boolean nativeOnTouchEvent(long j, MotionEvent motionEvent, long j2, int i, int i2, int i3, int i4, float f, float f2, float f3, float f4, int i5, int i6, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, int i7, int i8, int i9, int i10, boolean z);

    private native void nativePinchBegin(long j, long j2, float f, float f2);

    private native void nativePinchBy(long j, long j2, float f, float f2, float f3);

    private native void nativePinchEnd(long j, long j2);

    private native void nativeRemoveJavascriptInterface(long j, String str);

    private native void nativeResetGestureDetection(long j);

    private native void nativeScrollBegin(long j, long j2, float f, float f2, float f3, float f4, boolean z);

    private native void nativeScrollBy(long j, long j2, float f, float f2, float f3, float f4);

    private native void nativeScrollEnd(long j, long j2);

    private native void nativeSelectBetweenCoordinates(long j, float f, float f2, float f3, float f4);

    private native void nativeSelectPopupMenuItems(long j, long j2, int[] iArr);

    private native int nativeSendMouseMoveEvent(long j, long j2, float f, float f2);

    private native int nativeSendMouseWheelEvent(long j, long j2, float f, float f2, float f3, float f4);

    private native void nativeSendOrientationChangeEvent(long j, int i);

    private native void nativeSetAccessibilityEnabled(long j, boolean z);

    private native void nativeSetAllowJavascriptInterfacesInspection(long j, boolean z);

    private native void nativeSetBackgroundOpaque(long j, boolean z);

    private native void nativeSetDoubleTapSupportEnabled(long j, boolean z);

    private native void nativeSetDrawsContent(long j, boolean z);

    private native void nativeSetFocus(long j, boolean z);

    private native void nativeSetMultiTouchZoomSupportEnabled(long j, boolean z);

    private native void nativeSetTextHandlesTemporarilyHidden(long j, boolean z);

    private native void nativeSetTextTrackSettings(long j, boolean z, String str, String str2, String str3, String str4, String str5, String str6, String str7);

    private native void nativeSingleTap(long j, long j2, float f, float f2);

    private native void nativeWasResized(long j);

    static {
        boolean z;
        if (ContentViewCore.class.desiredAssertionStatus()) {
            z = $assertionsDisabled;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
        NO_OP_ZOOM_CONTROLS_DELEGATE = new C06001();
    }

    public static Activity activityFromContext(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            return activityFromContext(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    public static ContentViewCore fromWebContents(WebContents webContents) {
        return nativeFromWebContentsAndroid(webContents);
    }

    public ContentViewCore(Context context) {
        this.mJavaScriptInterfaces = new HashMap();
        this.mRetainedJavaScriptObjects = new HashSet();
        this.mNativeContentViewCore = 0;
        this.mNativeSelectPopupSourceFrame = 0;
        this.mFakeMouseMoveRunnable = null;
        this.mSelectionRect = new Rect();
        this.mFocusPreOSKViewportRect = new Rect();
        this.mSmartClipDataListener = null;
        this.mFullscreenRequiredForOrientationLock = true;
        this.mContext = context;
        this.mAdapterInputConnectionFactory = new AdapterInputConnectionFactory();
        this.mInputMethodManagerWrapper = new InputMethodManagerWrapper(this.mContext);
        this.mRenderCoordinates = new RenderCoordinates();
        float deviceScaleFactor = getContext().getResources().getDisplayMetrics().density;
        String forceScaleFactor = CommandLine.getInstance().getSwitchValue(ContentSwitches.FORCE_DEVICE_SCALE_FACTOR);
        if (forceScaleFactor != null) {
            deviceScaleFactor = Float.valueOf(forceScaleFactor).floatValue();
        }
        this.mRenderCoordinates.setDeviceScaleFactor(deviceScaleFactor);
        this.mAccessibilityManager = (AccessibilityManager) getContext().getSystemService("accessibility");
        this.mSystemCaptioningBridge = CaptioningBridgeFactory.getSystemCaptioningBridge(this.mContext);
        this.mGestureStateListeners = new ObserverList();
        this.mGestureStateListenersIterator = this.mGestureStateListeners.rewindableIterator();
        this.mEditable = Factory.getInstance().newEditable(CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
        Selection.setSelection(this.mEditable, INVALID_RENDER_PROCESS_PID);
        this.mContainerViewObservers = new ObserverList();
    }

    @CalledByNative
    public Context getContext() {
        return this.mContext;
    }

    public ViewGroup getContainerView() {
        return this.mContainerView;
    }

    public WebContents getWebContents() {
        return this.mWebContents;
    }

    public WindowAndroid getWindowAndroid() {
        if (this.mNativeContentViewCore == 0) {
            return null;
        }
        return nativeGetJavaWindowAndroid(this.mNativeContentViewCore);
    }

    public void setTopControlsHeight(int topControlsHeightPix, boolean topControlsShrinkBlinkSize) {
        if (topControlsHeightPix != this.mTopControlsHeightPix || topControlsShrinkBlinkSize != this.mTopControlsShrinkBlinkSize) {
            this.mTopControlsHeightPix = topControlsHeightPix;
            this.mTopControlsShrinkBlinkSize = topControlsShrinkBlinkSize;
            if (this.mNativeContentViewCore != 0) {
                nativeWasResized(this.mNativeContentViewCore);
            }
        }
    }

    public ViewAndroidDelegate getViewAndroidDelegate() {
        return this.mViewAndroidDelegate;
    }

    @VisibleForTesting
    public void setImeAdapterForTest(ImeAdapter imeAdapter) {
        this.mImeAdapter = imeAdapter;
    }

    @VisibleForTesting
    public ImeAdapter getImeAdapterForTest() {
        return this.mImeAdapter;
    }

    @VisibleForTesting
    public void setAdapterInputConnectionFactory(AdapterInputConnectionFactory factory) {
        this.mAdapterInputConnectionFactory = factory;
    }

    @VisibleForTesting
    public void setInputMethodManagerWrapperForTest(InputMethodManagerWrapper immw) {
        this.mInputMethodManagerWrapper = immw;
    }

    @VisibleForTesting
    public AdapterInputConnection getInputConnectionForTest() {
        return this.mInputConnection;
    }

    private ImeAdapter createImeAdapter() {
        return new ImeAdapter(this.mInputMethodManagerWrapper, new C06012());
    }

    public void initialize(ViewGroup containerView, InternalAccessDelegate internalDispatcher, WebContents webContents, WindowAndroid windowAndroid) {
        createContentViewAndroidDelegate();
        setContainerView(containerView);
        long windowNativePointer = windowAndroid.getNativePointer();
        if ($assertionsDisabled || windowNativePointer != 0) {
            this.mZoomControlsDelegate = NO_OP_ZOOM_CONTROLS_DELEGATE;
            this.mNativeContentViewCore = nativeInit(webContents, this.mViewAndroidDelegate, windowNativePointer, this.mRetainedJavaScriptObjects);
            this.mWebContents = nativeGetWebContentsAndroid(this.mNativeContentViewCore);
            setContainerViewInternals(internalDispatcher);
            this.mRenderCoordinates.reset();
            initPopupZoomer(this.mContext);
            this.mImeAdapter = createImeAdapter();
            attachImeAdapter();
            this.mAccessibilityInjector = AccessibilityInjector.newInstance(this);
            this.mWebContentsObserver = new ContentViewWebContentsObserver(this);
            return;
        }
        throw new AssertionError();
    }

    @VisibleForTesting
    void createContentViewAndroidDelegate() {
        this.mViewAndroidDelegate = new ContentViewAndroidDelegate(this.mContainerView, this.mRenderCoordinates);
    }

    public void setContainerView(ViewGroup containerView) {
        try {
            TraceEvent.begin("ContentViewCore.setContainerView");
            if (this.mContainerView != null) {
                if ($assertionsDisabled || this.mOverscrollRefreshHandler == null) {
                    this.mPastePopupMenu = null;
                    this.mInputConnection = null;
                    hidePopupsAndClearSelection();
                } else {
                    throw new AssertionError();
                }
            }
            this.mContainerView = containerView;
            this.mPositionObserver = new ViewPositionObserver(this.mContainerView);
            this.mContainerView.setClickable(true);
            this.mViewAndroidDelegate.updateCurrentContainerView(this.mContainerView);
            Iterator i$ = this.mContainerViewObservers.iterator();
            while (i$.hasNext()) {
                ((ContainerViewObserver) i$.next()).onContainerViewChanged(this.mContainerView);
            }
        } finally {
            TraceEvent.end("ContentViewCore.setContainerView");
        }
    }

    public void addContainerViewObserver(ContainerViewObserver observer) {
        this.mContainerViewObservers.addObserver(observer);
    }

    public void removeContainerViewObserver(ContainerViewObserver observer) {
        this.mContainerViewObservers.removeObserver(observer);
    }

    @CalledByNative
    void onNativeContentViewCoreDestroyed(long nativeContentViewCore) {
        if ($assertionsDisabled || nativeContentViewCore == this.mNativeContentViewCore) {
            this.mNativeContentViewCore = 0;
            return;
        }
        throw new AssertionError();
    }

    public void setContainerViewInternals(InternalAccessDelegate internalDispatcher) {
        this.mContainerViewInternals = internalDispatcher;
    }

    @VisibleForTesting
    void initPopupZoomer(Context context) {
        this.mPopupZoomer = new PopupZoomer(context);
        this.mPopupZoomer.setOnVisibilityChangedListener(new C06023());
        this.mPopupZoomer.setOnTapListener(new C06034());
    }

    @VisibleForTesting
    public void setPopupZoomerForTest(PopupZoomer popupZoomer) {
        this.mPopupZoomer = popupZoomer;
    }

    public void destroy() {
        if (this.mNativeContentViewCore != 0) {
            nativeOnJavaContentViewCoreDestroyed(this.mNativeContentViewCore);
        }
        this.mWebContentsObserver.destroy();
        this.mWebContentsObserver = null;
        setSmartClipDataListener(null);
        setZoomControlsDelegate(null);
        this.mContentViewClient = new ContentViewClient();
        this.mWebContents = null;
        this.mOverscrollRefreshHandler = null;
        this.mNativeContentViewCore = 0;
        this.mJavaScriptInterfaces.clear();
        this.mRetainedJavaScriptObjects.clear();
        unregisterAccessibilityContentObserver();
        this.mGestureStateListeners.clear();
        ScreenOrientationListener.getInstance().removeObserver(this);
        this.mPositionObserver.clearListener();
        this.mContainerViewObservers.clear();
    }

    private void unregisterAccessibilityContentObserver() {
        if (this.mAccessibilityScriptInjectionObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(this.mAccessibilityScriptInjectionObserver);
            this.mAccessibilityScriptInjectionObserver = null;
        }
    }

    public boolean isAlive() {
        return this.mNativeContentViewCore != 0 ? true : $assertionsDisabled;
    }

    @CalledByNative
    public long getNativeContentViewCore() {
        return this.mNativeContentViewCore;
    }

    public void setContentViewClient(ContentViewClient client) {
        if (client == null) {
            throw new IllegalArgumentException("The client can't be null.");
        }
        this.mContentViewClient = client;
    }

    @VisibleForTesting
    public ContentViewClient getContentViewClient() {
        if (this.mContentViewClient == null) {
            this.mContentViewClient = new ContentViewClient();
        }
        return this.mContentViewClient;
    }

    @CalledByNative
    private void onBackgroundColorChanged(int color) {
        getContentViewClient().onBackgroundColorChanged(color);
    }

    @CalledByNative
    public int getViewportWidthPix() {
        return this.mViewportWidthPix;
    }

    @CalledByNative
    public int getViewportHeightPix() {
        return this.mViewportHeightPix;
    }

    @CalledByNative
    public int getPhysicalBackingWidthPix() {
        return this.mPhysicalBackingWidthPix;
    }

    @CalledByNative
    public int getPhysicalBackingHeightPix() {
        return this.mPhysicalBackingHeightPix;
    }

    @VisibleForTesting
    public int getViewportSizeOffsetWidthPix() {
        return INVALID_RENDER_PROCESS_PID;
    }

    @VisibleForTesting
    public int getViewportSizeOffsetHeightPix() {
        return this.mTopControlsShrinkBlinkSize ? this.mTopControlsHeightPix : INVALID_RENDER_PROCESS_PID;
    }

    @CalledByNative
    public boolean doTopControlsShrinkBlinkSize() {
        return this.mTopControlsShrinkBlinkSize;
    }

    @CalledByNative
    public int getTopControlsHeightPix() {
        return this.mTopControlsHeightPix;
    }

    public float getContentHeightCss() {
        return this.mRenderCoordinates.getContentHeightCss();
    }

    public float getContentWidthCss() {
        return this.mRenderCoordinates.getContentWidthCss();
    }

    public String getSelectedText() {
        return this.mHasSelection ? this.mLastSelectedText : CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
    }

    public boolean isSelectionEditable() {
        return this.mHasSelection ? this.mFocusedNodeEditable : $assertionsDisabled;
    }

    public boolean isFocusedNodeEditable() {
        return this.mFocusedNodeEditable;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return onTouchEventImpl(event, $assertionsDisabled);
    }

    private boolean onTouchEventImpl(MotionEvent event, boolean isTouchHandleEvent) {
        TraceEvent.begin("onTouchEvent");
        try {
            int eventAction = event.getActionMasked();
            if (eventAction == 0) {
                cancelRequestToScrollFocusedEditableNodeIntoView();
            }
            if (SPenSupport.isSPenSupported(this.mContext)) {
                eventAction = SPenSupport.convertSPenEventAction(eventAction);
            }
            if (!isValidTouchEventActionForNative(eventAction)) {
                return $assertionsDisabled;
            }
            if (this.mNativeContentViewCore == 0) {
                TraceEvent.end("onTouchEvent");
                return $assertionsDisabled;
            }
            float x;
            float y;
            int pointerId;
            float touchMajor;
            float touchMinor;
            float orientation;
            int toolType;
            MotionEvent offset = null;
            if (!(this.mCurrentTouchOffsetX == 0.0f && this.mCurrentTouchOffsetY == 0.0f)) {
                offset = createOffsetMotionEvent(event);
                event = offset;
            }
            int pointerCount = event.getPointerCount();
            long j = this.mNativeContentViewCore;
            long eventTime = event.getEventTime();
            int historySize = event.getHistorySize();
            int actionIndex = event.getActionIndex();
            float x2 = event.getX();
            float y2 = event.getY();
            if (pointerCount > TEXT_STYLE_BOLD) {
                x = event.getX(TEXT_STYLE_BOLD);
            } else {
                x = 0.0f;
            }
            if (pointerCount > TEXT_STYLE_BOLD) {
                y = event.getY(TEXT_STYLE_BOLD);
            } else {
                y = 0.0f;
            }
            int pointerId2 = event.getPointerId(INVALID_RENDER_PROCESS_PID);
            if (pointerCount > TEXT_STYLE_BOLD) {
                pointerId = event.getPointerId(TEXT_STYLE_BOLD);
            } else {
                pointerId = -1;
            }
            float touchMajor2 = event.getTouchMajor();
            if (pointerCount > TEXT_STYLE_BOLD) {
                touchMajor = event.getTouchMajor(TEXT_STYLE_BOLD);
            } else {
                touchMajor = 0.0f;
            }
            float touchMinor2 = event.getTouchMinor();
            if (pointerCount > TEXT_STYLE_BOLD) {
                touchMinor = event.getTouchMinor(TEXT_STYLE_BOLD);
            } else {
                touchMinor = 0.0f;
            }
            float orientation2 = event.getOrientation();
            if (pointerCount > TEXT_STYLE_BOLD) {
                orientation = event.getOrientation(TEXT_STYLE_BOLD);
            } else {
                orientation = 0.0f;
            }
            float rawX = event.getRawX();
            float rawY = event.getRawY();
            int toolType2 = event.getToolType(INVALID_RENDER_PROCESS_PID);
            if (pointerCount > TEXT_STYLE_BOLD) {
                toolType = event.getToolType(TEXT_STYLE_BOLD);
            } else {
                toolType = INVALID_RENDER_PROCESS_PID;
            }
            boolean consumed = nativeOnTouchEvent(j, event, eventTime, eventAction, pointerCount, historySize, actionIndex, x2, y2, x, y, pointerId2, pointerId, touchMajor2, touchMajor, touchMinor2, touchMinor, orientation2, orientation, rawX, rawY, toolType2, toolType, event.getButtonState(), event.getMetaState(), isTouchHandleEvent);
            if (offset != null) {
                offset.recycle();
            }
            TraceEvent.end("onTouchEvent");
            return consumed;
        } finally {
            TraceEvent.end("onTouchEvent");
        }
    }

    @CalledByNative
    private void requestDisallowInterceptTouchEvent() {
        this.mContainerView.requestDisallowInterceptTouchEvent(true);
    }

    private static boolean isValidTouchEventActionForNative(int eventAction) {
        return (eventAction == 0 || eventAction == TEXT_STYLE_BOLD || eventAction == 3 || eventAction == TEXT_STYLE_ITALIC || eventAction == 5 || eventAction == 6) ? true : $assertionsDisabled;
    }

    public boolean isScrollInProgress() {
        return (this.mTouchScrollInProgress || this.mPotentiallyActiveFlingCount > 0 || getContentViewClient().isExternalScrollActive()) ? true : $assertionsDisabled;
    }

    @CalledByNative
    private void onFlingStartEventConsumed(int vx, int vy) {
        this.mTouchScrollInProgress = $assertionsDisabled;
        this.mPotentiallyActiveFlingCount += TEXT_STYLE_BOLD;
        this.mGestureStateListenersIterator.rewind();
        while (this.mGestureStateListenersIterator.hasNext()) {
            ((GestureStateListener) this.mGestureStateListenersIterator.next()).onFlingStartGesture(vx, vy, computeVerticalScrollOffset(), computeVerticalScrollExtent());
        }
    }

    @CalledByNative
    private void onFlingStartEventHadNoConsumer(int vx, int vy) {
        this.mTouchScrollInProgress = $assertionsDisabled;
    }

    @CalledByNative
    private void onFlingCancelEventAck() {
        updateGestureStateListener(10);
    }

    @CalledByNative
    private void onScrollBeginEventAck() {
        this.mTouchScrollInProgress = true;
        hidePastePopup();
        this.mZoomControlsDelegate.invokeZoomPicker();
        updateGestureStateListener(6);
    }

    @CalledByNative
    private void onScrollUpdateGestureConsumed() {
        this.mZoomControlsDelegate.invokeZoomPicker();
        this.mGestureStateListenersIterator.rewind();
        while (this.mGestureStateListenersIterator.hasNext()) {
            ((GestureStateListener) this.mGestureStateListenersIterator.next()).onScrollUpdateGestureConsumed();
        }
    }

    @CalledByNative
    private void onScrollEndEventAck() {
        if (this.mTouchScrollInProgress) {
            this.mTouchScrollInProgress = $assertionsDisabled;
            updateGestureStateListener(TEXT_STYLE_STRIKE_THRU);
        }
    }

    @CalledByNative
    private void onPinchBeginEventAck() {
        updateGestureStateListener(12);
    }

    @CalledByNative
    private void onPinchEndEventAck() {
        updateGestureStateListener(14);
    }

    @CalledByNative
    private void onSingleTapEventAck(boolean consumed, int x, int y) {
        this.mGestureStateListenersIterator.rewind();
        while (this.mGestureStateListenersIterator.hasNext()) {
            ((GestureStateListener) this.mGestureStateListenersIterator.next()).onSingleTap(consumed, x, y);
        }
    }

    @CalledByNative
    private void onShowUnhandledTapUIIfNeeded(int x, int y) {
        if (this.mContextualSearchClient != null) {
            this.mContextualSearchClient.showUnhandledTapUIIfNeeded(x, y);
        }
    }

    @CalledByNative
    private boolean filterTapOrPressEvent(int type, int x, int y) {
        if (type == 5 && offerLongPressToEmbedder()) {
            return true;
        }
        updateForTapOrPress(type, (float) x, (float) y);
        return $assertionsDisabled;
    }

    @VisibleForTesting
    public void sendDoubleTapForTest(long timeMs, int x, int y) {
        if (this.mNativeContentViewCore != 0) {
            nativeDoubleTap(this.mNativeContentViewCore, timeMs, (float) x, (float) y);
        }
    }

    public void flingViewport(long timeMs, int velocityX, int velocityY) {
        if (this.mNativeContentViewCore != 0) {
            nativeFlingCancel(this.mNativeContentViewCore, timeMs);
            nativeScrollBegin(this.mNativeContentViewCore, timeMs, 0.0f, 0.0f, (float) velocityX, (float) velocityY, true);
            nativeFlingStart(this.mNativeContentViewCore, timeMs, 0.0f, 0.0f, (float) velocityX, (float) velocityY, true);
        }
    }

    public void cancelFling(long timeMs) {
        if (this.mNativeContentViewCore != 0) {
            nativeFlingCancel(this.mNativeContentViewCore, timeMs);
        }
    }

    public void addGestureStateListener(GestureStateListener listener) {
        this.mGestureStateListeners.addObserver(listener);
    }

    public void removeGestureStateListener(GestureStateListener listener) {
        this.mGestureStateListeners.removeObserver(listener);
    }

    void updateGestureStateListener(int gestureType) {
        this.mGestureStateListenersIterator.rewind();
        while (this.mGestureStateListenersIterator.hasNext()) {
            GestureStateListener listener = (GestureStateListener) this.mGestureStateListenersIterator.next();
            switch (gestureType) {
                case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                    listener.onScrollStarted(computeVerticalScrollOffset(), computeVerticalScrollExtent());
                    break;
                case TEXT_STYLE_STRIKE_THRU /*8*/:
                    listener.onScrollEnded(computeVerticalScrollOffset(), computeVerticalScrollExtent());
                    break;
                case ConnectionResult.DEVELOPER_ERROR /*10*/:
                    listener.onFlingCancelGesture();
                    break;
                case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                    listener.onFlingEndGesture(computeVerticalScrollOffset(), computeVerticalScrollExtent());
                    break;
                case TextInputType.TIME /*12*/:
                    listener.onPinchStarted();
                    break;
                case ConnectionResult.TIMEOUT /*14*/:
                    listener.onPinchEnded();
                    break;
                default:
                    break;
            }
        }
    }

    public void setDrawsContent(boolean draws) {
        if (this.mNativeContentViewCore != 0) {
            nativeSetDrawsContent(this.mNativeContentViewCore, draws);
        }
    }

    public void onShow() {
        if ($assertionsDisabled || this.mWebContents != null) {
            this.mWebContents.onShow();
            setAccessibilityState(this.mAccessibilityManager.isEnabled());
            restoreSelectionPopupsIfNecessary();
            return;
        }
        throw new AssertionError();
    }

    public int getCurrentRenderProcessId() {
        return nativeGetCurrentRenderProcessId(this.mNativeContentViewCore);
    }

    public void onHide() {
        if ($assertionsDisabled || this.mWebContents != null) {
            hidePopupsAndPreserveSelection();
            setInjectedAccessibility($assertionsDisabled);
            this.mWebContents.onHide();
            return;
        }
        throw new AssertionError();
    }

    private void hidePopupsAndClearSelection() {
        this.mUnselectAllOnActionModeDismiss = true;
        hidePopups();
    }

    private void hidePopupsAndPreserveSelection() {
        this.mUnselectAllOnActionModeDismiss = $assertionsDisabled;
        hidePopups();
    }

    private void clearUserSelection() {
        if (this.mFocusedNodeEditable) {
            if (this.mInputConnection != null) {
                int selectionEnd = Selection.getSelectionEnd(this.mEditable);
                this.mInputConnection.setSelection(selectionEnd, selectionEnd);
            }
        } else if (this.mWebContents != null) {
            this.mWebContents.unselect();
        }
    }

    private void hidePopups() {
        hideSelectActionMode();
        hidePastePopup();
        hideSelectPopup();
        this.mPopupZoomer.hide($assertionsDisabled);
        if (this.mUnselectAllOnActionModeDismiss) {
            dismissTextHandles();
        }
    }

    private void restoreSelectionPopupsIfNecessary() {
        if (this.mHasSelection && this.mActionMode == null) {
            showSelectActionMode(true);
        }
    }

    public void hideSelectActionMode() {
        if (this.mActionMode != null) {
            this.mActionMode.finish();
            this.mActionMode = null;
        }
    }

    public boolean isSelectActionBarShowing() {
        return this.mActionMode != null ? true : $assertionsDisabled;
    }

    private void resetGestureDetection() {
        if (this.mNativeContentViewCore != 0) {
            nativeResetGestureDetection(this.mNativeContentViewCore);
        }
    }

    public void onAttachedToWindow() {
        setAccessibilityState(this.mAccessibilityManager.isEnabled());
        setTextHandlesTemporarilyHidden($assertionsDisabled);
        restoreSelectionPopupsIfNecessary();
        ScreenOrientationListener.getInstance().addObserver(this, this.mContext);
        GamepadList.onAttachedToWindow(this.mContext);
        this.mAccessibilityManager.addAccessibilityStateChangeListener(this);
        this.mSystemCaptioningBridge.addListener(this);
    }

    @SuppressLint({"MissingSuperCall"})
    public void onDetachedFromWindow() {
        setInjectedAccessibility($assertionsDisabled);
        this.mZoomControlsDelegate.dismissZoomPicker();
        unregisterAccessibilityContentObserver();
        ScreenOrientationListener.getInstance().removeObserver(this);
        GamepadList.onDetachedFromWindow();
        this.mAccessibilityManager.removeAccessibilityStateChangeListener(this);
        setTextHandlesTemporarilyHidden(true);
        hidePopupsAndPreserveSelection();
        this.mSystemCaptioningBridge.removeListener(this);
    }

    public void onVisibilityChanged(View changedView, int visibility) {
        if (visibility != 0) {
            this.mZoomControlsDelegate.dismissZoomPicker();
        }
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        if (!this.mImeAdapter.hasTextInputType()) {
            outAttrs.imeOptions = PageTransition.FROM_ADDRESS_BAR;
        }
        this.mInputConnection = this.mAdapterInputConnectionFactory.get(this.mContainerView, this.mImeAdapter, this.mEditable, outAttrs);
        return this.mInputConnection;
    }

    @VisibleForTesting
    public AdapterInputConnection getAdapterInputConnectionForTest() {
        return this.mInputConnection;
    }

    @VisibleForTesting
    public Editable getEditableForTest() {
        return this.mEditable;
    }

    public boolean onCheckIsTextEditor() {
        return this.mImeAdapter.hasTextInputType();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        try {
            TraceEvent.begin("ContentViewCore.onConfigurationChanged");
            if (newConfig.keyboard != TEXT_STYLE_BOLD) {
                if (this.mNativeContentViewCore != 0) {
                    this.mImeAdapter.attach(nativeGetNativeImeAdapter(this.mNativeContentViewCore));
                }
                this.mInputMethodManagerWrapper.restartInput(this.mContainerView);
            }
            this.mContainerViewInternals.super_onConfigurationChanged(newConfig);
            this.mContainerView.requestLayout();
        } finally {
            TraceEvent.end("ContentViewCore.onConfigurationChanged");
        }
    }

    public void onSizeChanged(int wPix, int hPix, int owPix, int ohPix) {
        if (getViewportWidthPix() != wPix || getViewportHeightPix() != hPix) {
            this.mViewportWidthPix = wPix;
            this.mViewportHeightPix = hPix;
            if (this.mNativeContentViewCore != 0) {
                nativeWasResized(this.mNativeContentViewCore);
            }
            updateAfterSizeChanged();
        }
    }

    public void onPhysicalBackingSizeChanged(int wPix, int hPix) {
        if (this.mPhysicalBackingWidthPix != wPix || this.mPhysicalBackingHeightPix != hPix) {
            this.mPhysicalBackingWidthPix = wPix;
            this.mPhysicalBackingHeightPix = hPix;
            if (this.mNativeContentViewCore != 0) {
                nativeWasResized(this.mNativeContentViewCore);
            }
        }
    }

    public void onOverdrawBottomHeightChanged(int overdrawHeightPix) {
    }

    private void updateAfterSizeChanged() {
        this.mPopupZoomer.hide($assertionsDisabled);
        if (!this.mFocusPreOSKViewportRect.isEmpty()) {
            Rect rect = new Rect();
            getContainerView().getWindowVisibleDisplayFrame(rect);
            if (!rect.equals(this.mFocusPreOSKViewportRect)) {
                if (rect.width() == this.mFocusPreOSKViewportRect.width()) {
                    if ($assertionsDisabled || this.mWebContents != null) {
                        this.mWebContents.scrollFocusedEditableNodeIntoView();
                    } else {
                        throw new AssertionError();
                    }
                }
                cancelRequestToScrollFocusedEditableNodeIntoView();
            }
        }
    }

    private void cancelRequestToScrollFocusedEditableNodeIntoView() {
        this.mFocusPreOSKViewportRect.setEmpty();
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) {
            resetGestureDetection();
        }
    }

    public void onFocusChanged(boolean gainFocus) {
        if (gainFocus) {
            restoreSelectionPopupsIfNecessary();
        } else {
            hideImeIfNeeded();
            cancelRequestToScrollFocusedEditableNodeIntoView();
            if (this.mPreserveSelectionOnNextLossOfFocus) {
                this.mPreserveSelectionOnNextLossOfFocus = $assertionsDisabled;
                hidePopupsAndPreserveSelection();
            } else {
                hidePopupsAndClearSelection();
                clearUserSelection();
            }
        }
        if (this.mNativeContentViewCore != 0) {
            nativeSetFocus(this.mNativeContentViewCore, gainFocus);
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!this.mPopupZoomer.isShowing() || keyCode != TEXT_STYLE_UNDERLINE) {
            return this.mContainerViewInternals.super_onKeyUp(keyCode, event);
        }
        this.mPopupZoomer.hide(true);
        return true;
    }

    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        try {
            TraceEvent.begin("ContentViewCore.dispatchKeyEventPreIme");
            boolean super_dispatchKeyEventPreIme = this.mContainerViewInternals.super_dispatchKeyEventPreIme(event);
            return super_dispatchKeyEventPreIme;
        } finally {
            TraceEvent.end("ContentViewCore.dispatchKeyEventPreIme");
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (GamepadList.dispatchKeyEvent(event)) {
            return true;
        }
        if (getContentViewClient().shouldOverrideKeyEvent(event)) {
            return this.mContainerViewInternals.super_dispatchKeyEvent(event);
        }
        if (this.mImeAdapter.dispatchKeyEvent(event)) {
            return true;
        }
        return this.mContainerViewInternals.super_dispatchKeyEvent(event);
    }

    public boolean onHoverEvent(MotionEvent event) {
        TraceEvent.begin("onHoverEvent");
        MotionEvent offset = createOffsetMotionEvent(event);
        try {
            if (this.mBrowserAccessibilityManager != null) {
                boolean onHoverEvent = this.mBrowserAccessibilityManager.onHoverEvent(offset);
                return onHoverEvent;
            } else if (this.mTouchExplorationEnabled && offset.getAction() == 10) {
                offset.recycle();
                TraceEvent.end("onHoverEvent");
                return true;
            } else {
                if (event.getToolType(INVALID_RENDER_PROCESS_PID) == TEXT_STYLE_BOLD) {
                    if (this.mEnableTouchHover == null) {
                        this.mEnableTouchHover = Boolean.valueOf(CommandLine.getInstance().hasSwitch(ContentSwitches.ENABLE_TOUCH_HOVER));
                    }
                    if (!this.mEnableTouchHover.booleanValue()) {
                        offset.recycle();
                        TraceEvent.end("onHoverEvent");
                        return $assertionsDisabled;
                    }
                }
                this.mContainerView.removeCallbacks(this.mFakeMouseMoveRunnable);
                if (this.mNativeContentViewCore != 0) {
                    nativeSendMouseMoveEvent(this.mNativeContentViewCore, offset.getEventTime(), offset.getX(), offset.getY());
                }
                offset.recycle();
                TraceEvent.end("onHoverEvent");
                return true;
            }
        } finally {
            offset.recycle();
            TraceEvent.end("onHoverEvent");
        }
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        if (GamepadList.onGenericMotionEvent(event)) {
            return true;
        }
        if ((event.getSource() & TEXT_STYLE_ITALIC) != 0) {
            switch (event.getAction()) {
                case TEXT_STYLE_STRIKE_THRU /*8*/:
                    if (this.mNativeContentViewCore == 0) {
                        return $assertionsDisabled;
                    }
                    nativeSendMouseWheelEvent(this.mNativeContentViewCore, event.getEventTime(), event.getX(), event.getY(), event.getAxisValue(9), event.getAxisValue(10));
                    this.mContainerView.removeCallbacks(this.mFakeMouseMoveRunnable);
                    this.mFakeMouseMoveRunnable = new C03375(MotionEvent.obtain(event));
                    this.mContainerView.postDelayed(this.mFakeMouseMoveRunnable, 250);
                    return true;
            }
        }
        return this.mContainerViewInternals.super_onGenericMotionEvent(event);
    }

    public void setCurrentMotionEventOffsets(float dx, float dy) {
        this.mCurrentTouchOffsetX = dx;
        this.mCurrentTouchOffsetY = dy;
    }

    private MotionEvent createOffsetMotionEvent(MotionEvent src) {
        MotionEvent dst = MotionEvent.obtain(src);
        dst.offsetLocation(this.mCurrentTouchOffsetX, this.mCurrentTouchOffsetY);
        return dst;
    }

    public void scrollBy(float dxPix, float dyPix) {
        if (this.mNativeContentViewCore != 0) {
            if (dxPix != 0.0f || dyPix != 0.0f) {
                long time = SystemClock.uptimeMillis();
                if (this.mPotentiallyActiveFlingCount > 0) {
                    nativeFlingCancel(this.mNativeContentViewCore, time);
                }
                nativeScrollBegin(this.mNativeContentViewCore, time, 0.0f, 0.0f, -dxPix, -dyPix, $assertionsDisabled);
                nativeScrollBy(this.mNativeContentViewCore, time, 0.0f, 0.0f, dxPix, dyPix);
                nativeScrollEnd(this.mNativeContentViewCore, time);
            }
        }
    }

    public void scrollTo(float xPix, float yPix) {
        if (this.mNativeContentViewCore != 0) {
            scrollBy(xPix - this.mRenderCoordinates.getScrollXPix(), yPix - this.mRenderCoordinates.getScrollYPix());
        }
    }

    public int getNativeScrollXForTest() {
        return this.mRenderCoordinates.getScrollXPixInt();
    }

    public int getNativeScrollYForTest() {
        return this.mRenderCoordinates.getScrollYPixInt();
    }

    public int computeHorizontalScrollExtent() {
        return this.mRenderCoordinates.getLastFrameViewportWidthPixInt();
    }

    public int computeHorizontalScrollOffset() {
        return this.mRenderCoordinates.getScrollXPixInt();
    }

    public int computeHorizontalScrollRange() {
        return this.mRenderCoordinates.getContentWidthPixInt();
    }

    public int computeVerticalScrollExtent() {
        return this.mRenderCoordinates.getLastFrameViewportHeightPixInt();
    }

    public int computeVerticalScrollOffset() {
        return this.mRenderCoordinates.getScrollYPixInt();
    }

    public int computeVerticalScrollRange() {
        return this.mRenderCoordinates.getContentHeightPixInt();
    }

    public boolean awakenScrollBars(int startDelay, boolean invalidate) {
        if (this.mContainerView.getScrollBarStyle() == 0) {
            return $assertionsDisabled;
        }
        return this.mContainerViewInternals.super_awakenScrollBars(startDelay, invalidate);
    }

    private void updateForTapOrPress(int type, float xPix, float yPix) {
        if (type == 3 || type == TEXT_STYLE_ITALIC || type == 5 || type == 16) {
            if (this.mContainerView.isFocusable() && this.mContainerView.isFocusableInTouchMode() && !this.mContainerView.isFocused()) {
                this.mContainerView.requestFocus();
            }
            if (!this.mPopupZoomer.isShowing()) {
                this.mPopupZoomer.setLastTouch(xPix, yPix);
            }
            this.mLastTapX = (int) xPix;
            this.mLastTapY = (int) yPix;
        }
    }

    public int getLastTapX() {
        return this.mLastTapX;
    }

    public int getLastTapY() {
        return this.mLastTapY;
    }

    public void setZoomControlsDelegate(ZoomControlsDelegate zoomControlsDelegate) {
        if (zoomControlsDelegate == null) {
            this.mZoomControlsDelegate = NO_OP_ZOOM_CONTROLS_DELEGATE;
        } else {
            this.mZoomControlsDelegate = zoomControlsDelegate;
        }
    }

    public void updateMultiTouchZoomSupport(boolean supportsMultiTouchZoom) {
        if (this.mNativeContentViewCore != 0) {
            nativeSetMultiTouchZoomSupportEnabled(this.mNativeContentViewCore, supportsMultiTouchZoom);
        }
    }

    public void updateDoubleTapSupport(boolean supportsDoubleTap) {
        if (this.mNativeContentViewCore != 0) {
            nativeSetDoubleTapSupportEnabled(this.mNativeContentViewCore, supportsDoubleTap);
        }
    }

    public void selectPopupMenuItems(int[] indices) {
        if (this.mNativeContentViewCore != 0) {
            nativeSelectPopupMenuItems(this.mNativeContentViewCore, this.mNativeSelectPopupSourceFrame, indices);
        }
        this.mNativeSelectPopupSourceFrame = 0;
        this.mSelectPopup = null;
    }

    @VisibleForTesting
    void sendOrientationChangeEvent(int orientation) {
        if (this.mNativeContentViewCore != 0) {
            nativeSendOrientationChangeEvent(this.mNativeContentViewCore, orientation);
        }
    }

    public void setDownloadDelegate(ContentViewDownloadDelegate delegate) {
        this.mDownloadDelegate = delegate;
    }

    ContentViewDownloadDelegate getDownloadDelegate() {
        return this.mDownloadDelegate;
    }

    @VisibleForTesting
    public ActionHandler getSelectActionHandler() {
        return this.mActionHandler;
    }

    private void showSelectActionMode(boolean allowFallbackIfFloatingActionModeCreationFails) {
        if (this.mActionMode != null) {
            this.mActionMode.invalidate();
            return;
        }
        if (this.mActionHandler == null) {
            this.mActionHandler = new C06046();
        }
        this.mActionMode = null;
        if (this.mContainerView.getParent() != null) {
            if ($assertionsDisabled || this.mWebContents != null) {
                boolean tryCreateFloatingActionMode = supportsFloatingActionMode();
                this.mActionMode = getContentViewClient().startActionMode(this.mContainerView, this.mActionHandler, tryCreateFloatingActionMode);
                if (tryCreateFloatingActionMode && this.mActionMode == null) {
                    this.mFloatingActionModeCreationFailed = true;
                    if (allowFallbackIfFloatingActionModeCreationFails) {
                        this.mActionMode = getContentViewClient().startActionMode(this.mContainerView, this.mActionHandler, $assertionsDisabled);
                    } else {
                        return;
                    }
                }
            }
            throw new AssertionError();
        }
        this.mUnselectAllOnActionModeDismiss = true;
        if (this.mActionMode == null) {
            clearSelection();
        } else {
            getContentViewClient().onContextualActionBarShown();
        }
    }

    private boolean supportsFloatingActionMode() {
        return (this.mFloatingActionModeCreationFailed || !getContentViewClient().supportsFloatingActionMode()) ? $assertionsDisabled : true;
    }

    private void invalidateActionModeContentRect() {
        if (this.mActionMode != null) {
            this.mActionMode.invalidateContentRect();
        }
    }

    public void clearSelection() {
        if (this.mWebContents != null) {
            this.mWebContents.unselect();
        }
    }

    public void preserveSelectionOnNextLossOfFocus() {
        this.mPreserveSelectionOnNextLossOfFocus = true;
    }

    @VisibleForTesting
    public boolean hasSelection() {
        return this.mHasSelection;
    }

    @VisibleForTesting
    protected boolean hasInsertion() {
        return this.mHasInsertion;
    }

    @CalledByNative
    private void onSelectionEvent(int eventType, int xAnchor, int yAnchor, int left, int top, int right, int bottom) {
        if (left == right) {
            right += TEXT_STYLE_BOLD;
        }
        if (top == bottom) {
            bottom += TEXT_STYLE_BOLD;
        }
        switch (eventType) {
            case INVALID_RENDER_PROCESS_PID /*0*/:
                this.mSelectionRect.set(left, top, right, bottom);
                this.mHasSelection = true;
                this.mUnselectAllOnActionModeDismiss = true;
                this.mContainerView.performHapticFeedback(INVALID_RENDER_PROCESS_PID);
                showSelectActionMode(true);
                break;
            case TEXT_STYLE_BOLD /*1*/:
                this.mSelectionRect.set(left, top, right, bottom);
                invalidateActionModeContentRect();
                break;
            case TEXT_STYLE_ITALIC /*2*/:
                this.mHasSelection = $assertionsDisabled;
                this.mUnselectAllOnActionModeDismiss = $assertionsDisabled;
                hideSelectActionMode();
                this.mSelectionRect.setEmpty();
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
            case TEXT_STYLE_UNDERLINE /*4*/:
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                break;
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                this.mSelectionRect.set(left, top, right, bottom);
                this.mHasInsertion = true;
                break;
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                this.mSelectionRect.set(left, top, right, bottom);
                if (!isScrollInProgress() && isPastePopupShowing()) {
                    showPastePopup(xAnchor, yAnchor);
                    break;
                } else {
                    hidePastePopup();
                    break;
                }
            case ConnectionResult.NETWORK_ERROR /*7*/:
                if (!this.mWasPastePopupShowingOnInsertionDragStart) {
                    showPastePopup(xAnchor, yAnchor);
                    break;
                } else {
                    hidePastePopup();
                    break;
                }
            case TEXT_STYLE_STRIKE_THRU /*8*/:
                hidePastePopup();
                this.mHasInsertion = $assertionsDisabled;
                this.mSelectionRect.setEmpty();
                break;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                this.mWasPastePopupShowingOnInsertionDragStart = isPastePopupShowing();
                hidePastePopup();
                break;
            default:
                if (!$assertionsDisabled) {
                    throw new AssertionError("Invalid selection event type.");
                }
                break;
        }
        if (this.mContextualSearchClient != null) {
            this.mContextualSearchClient.onSelectionEvent(eventType, (float) xAnchor, (float) yAnchor);
        }
    }

    private void dismissTextHandles() {
        this.mHasSelection = $assertionsDisabled;
        this.mHasInsertion = $assertionsDisabled;
        if (this.mNativeContentViewCore != 0) {
            nativeDismissTextHandles(this.mNativeContentViewCore);
        }
    }

    private void setTextHandlesTemporarilyHidden(boolean hide) {
        if (this.mNativeContentViewCore != 0) {
            nativeSetTextHandlesTemporarilyHidden(this.mNativeContentViewCore, hide);
        }
    }

    public void hideImeIfNeeded() {
        if (this.mInputMethodManagerWrapper.isActive(this.mContainerView)) {
            this.mInputMethodManagerWrapper.hideSoftInputFromWindow(this.mContainerView.getWindowToken(), INVALID_RENDER_PROCESS_PID, null);
        }
    }

    @CalledByNative
    private void updateFrameInfo(float scrollOffsetX, float scrollOffsetY, float pageScaleFactor, float minPageScaleFactor, float maxPageScaleFactor, float contentWidth, float contentHeight, float viewportWidth, float viewportHeight, float controlsOffsetYCss, float contentOffsetYCss, boolean isMobileOptimizedHint) {
        TraceEvent.begin("ContentViewCore:updateFrameInfo");
        this.mIsMobileOptimizedHint = isMobileOptimizedHint;
        float deviceScale = this.mRenderCoordinates.getDeviceScaleFactor();
        contentWidth = Math.max(contentWidth, ((float) this.mViewportWidthPix) / (deviceScale * pageScaleFactor));
        contentHeight = Math.max(contentHeight, ((float) this.mViewportHeightPix) / (deviceScale * pageScaleFactor));
        float contentOffsetYPix = this.mRenderCoordinates.fromDipToPix(contentOffsetYCss);
        boolean contentSizeChanged = (contentWidth == this.mRenderCoordinates.getContentWidthCss() && contentHeight == this.mRenderCoordinates.getContentHeightCss()) ? $assertionsDisabled : true;
        boolean scaleLimitsChanged = (minPageScaleFactor == this.mRenderCoordinates.getMinPageScaleFactor() && maxPageScaleFactor == this.mRenderCoordinates.getMaxPageScaleFactor()) ? $assertionsDisabled : true;
        boolean scrollChanged = (!((pageScaleFactor > this.mRenderCoordinates.getPageScaleFactor() ? 1 : (pageScaleFactor == this.mRenderCoordinates.getPageScaleFactor() ? 0 : -1)) != 0 ? true : $assertionsDisabled) && scrollOffsetX == this.mRenderCoordinates.getScrollX() && scrollOffsetY == this.mRenderCoordinates.getScrollY()) ? $assertionsDisabled : true;
        boolean contentOffsetChanged = contentOffsetYPix != this.mRenderCoordinates.getContentOffsetYPix() ? true : $assertionsDisabled;
        boolean needHidePopupZoomer = (contentSizeChanged || scrollChanged) ? true : $assertionsDisabled;
        boolean needUpdateZoomControls = (scaleLimitsChanged || scrollChanged) ? true : $assertionsDisabled;
        if (needHidePopupZoomer) {
            this.mPopupZoomer.hide(true);
        }
        if (scrollChanged) {
            this.mContainerViewInternals.onScrollChanged((int) this.mRenderCoordinates.fromLocalCssToPix(scrollOffsetX), (int) this.mRenderCoordinates.fromLocalCssToPix(scrollOffsetY), (int) this.mRenderCoordinates.getScrollXPix(), (int) this.mRenderCoordinates.getScrollYPix());
        }
        this.mRenderCoordinates.updateFrameInfo(scrollOffsetX, scrollOffsetY, contentWidth, contentHeight, viewportWidth, viewportHeight, pageScaleFactor, minPageScaleFactor, maxPageScaleFactor, contentOffsetYPix);
        if (scrollChanged || contentOffsetChanged) {
            this.mGestureStateListenersIterator.rewind();
            while (this.mGestureStateListenersIterator.hasNext()) {
                ((GestureStateListener) this.mGestureStateListenersIterator.next()).onScrollOffsetOrExtentChanged(computeVerticalScrollOffset(), computeVerticalScrollExtent());
            }
        }
        if (needUpdateZoomControls) {
            this.mZoomControlsDelegate.updateZoomControls();
        }
        getContentViewClient().onOffsetsForFullscreenChanged(controlsOffsetYCss * deviceScale, contentOffsetYPix, 0.0f);
        if (this.mBrowserAccessibilityManager != null) {
            this.mBrowserAccessibilityManager.notifyFrameInfoInitialized();
        }
        TraceEvent.end("ContentViewCore:updateFrameInfo");
    }

    @CalledByNative
    private void updateImeAdapter(long nativeImeAdapterAndroid, int textInputType, int textInputFlags, String text, int selectionStart, int selectionEnd, int compositionStart, int compositionEnd, boolean showImeIfNeeded, boolean isNonImeChange) {
        try {
            TraceEvent.begin("ContentViewCore.updateImeAdapter");
            boolean focusedNodeEditable = textInputType != 0 ? true : $assertionsDisabled;
            if (!focusedNodeEditable) {
                hidePastePopup();
            }
            this.mImeAdapter.updateKeyboardVisibility(nativeImeAdapterAndroid, textInputType, textInputFlags, showImeIfNeeded);
            if (this.mInputConnection != null) {
                this.mInputConnection.updateState(text, selectionStart, selectionEnd, compositionStart, compositionEnd, isNonImeChange);
            }
            if (this.mActionMode != null) {
                this.mActionMode.invalidate();
            }
            if (focusedNodeEditable != this.mFocusedNodeEditable) {
                this.mFocusedNodeEditable = focusedNodeEditable;
                getContentViewClient().onFocusedNodeEditabilityChanged(this.mFocusedNodeEditable);
            }
            TraceEvent.end("ContentViewCore.updateImeAdapter");
        } catch (Throwable th) {
            TraceEvent.end("ContentViewCore.updateImeAdapter");
        }
    }

    @CalledByNative
    private void forceUpdateImeAdapter(long nativeImeAdapterAndroid) {
        this.mImeAdapter.attach(nativeImeAdapterAndroid);
    }

    @CalledByNative
    private void setTitle(String title) {
        getContentViewClient().onUpdateTitle(title);
    }

    @CalledByNative
    private void showSelectPopup(long nativeSelectPopupSourceFrame, Rect bounds, String[] items, int[] enabled, boolean multiple, int[] selectedIndices) {
        if (this.mContainerView.getParent() == null || this.mContainerView.getVisibility() != 0) {
            this.mNativeSelectPopupSourceFrame = nativeSelectPopupSourceFrame;
            selectPopupMenuItems(null);
            return;
        }
        hidePopupsAndClearSelection();
        if (!$assertionsDisabled && this.mNativeSelectPopupSourceFrame != 0) {
            throw new AssertionError("Zombie popup did not clear the frame source");
        } else if ($assertionsDisabled || items.length == enabled.length) {
            List<SelectPopupItem> popupItems = new ArrayList();
            for (int i = INVALID_RENDER_PROCESS_PID; i < items.length; i += TEXT_STYLE_BOLD) {
                popupItems.add(new SelectPopupItem(items[i], enabled[i]));
            }
            if (!DeviceFormFactor.isTablet(this.mContext) || multiple || isTouchExplorationEnabled()) {
                this.mSelectPopup = new SelectPopupDialog(this, popupItems, multiple, selectedIndices);
            } else {
                this.mSelectPopup = new SelectPopupDropdown(this, popupItems, bounds, selectedIndices);
            }
            this.mNativeSelectPopupSourceFrame = nativeSelectPopupSourceFrame;
            this.mSelectPopup.show();
        } else {
            throw new AssertionError();
        }
    }

    @CalledByNative
    private void hideSelectPopup() {
        if (this.mSelectPopup != null) {
            this.mSelectPopup.hide();
        }
    }

    public SelectPopup getSelectPopupForTest() {
        return this.mSelectPopup;
    }

    @CalledByNative
    private void showDisambiguationPopup(Rect targetRect, Bitmap zoomedBitmap) {
        this.mPopupZoomer.setBitmap(zoomedBitmap);
        this.mPopupZoomer.show(targetRect);
    }

    @CalledByNative
    private MotionEventSynthesizer createMotionEventSynthesizer() {
        return new MotionEventSynthesizer(this);
    }

    @CalledByNative
    private PopupTouchHandleDrawable createPopupTouchHandleDrawable() {
        if (this.mTouchHandleDelegate == null) {
            this.mTouchHandleDelegate = new C06057();
        }
        return new PopupTouchHandleDrawable(this.mTouchHandleDelegate);
    }

    public void setOverscrollRefreshHandler(OverscrollRefreshHandler handler) {
        if ($assertionsDisabled || this.mOverscrollRefreshHandler == null || handler == null) {
            this.mOverscrollRefreshHandler = handler;
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private boolean onOverscrollRefreshStart() {
        if (this.mOverscrollRefreshHandler == null) {
            return $assertionsDisabled;
        }
        return this.mOverscrollRefreshHandler.start();
    }

    @CalledByNative
    private void onOverscrollRefreshUpdate(float delta) {
        if (this.mOverscrollRefreshHandler != null) {
            this.mOverscrollRefreshHandler.pull(delta);
        }
    }

    @CalledByNative
    private void onOverscrollRefreshRelease(boolean allowRefresh) {
        if (this.mOverscrollRefreshHandler != null) {
            this.mOverscrollRefreshHandler.release(allowRefresh);
        }
    }

    @CalledByNative
    private void onOverscrollRefreshReset() {
        if (this.mOverscrollRefreshHandler != null) {
            this.mOverscrollRefreshHandler.reset();
        }
    }

    @CalledByNative
    private void onSelectionChanged(String text) {
        this.mLastSelectedText = text;
        if (this.mContextualSearchClient != null) {
            this.mContextualSearchClient.onSelectionChanged(text);
        }
    }

    @CalledByNative
    private void showPastePopupWithFeedback(int x, int y) {
        if (showPastePopup(x, y)) {
            this.mContainerView.performHapticFeedback(INVALID_RENDER_PROCESS_PID);
        }
    }

    private boolean isPastePopupShowing() {
        if (supportsFloatingActionMode()) {
            if (this.mActionMode != null) {
                return true;
            }
            return $assertionsDisabled;
        } else if (this.mPastePopupMenu == null || !this.mPastePopupMenu.isShowing()) {
            return $assertionsDisabled;
        } else {
            return true;
        }
    }

    private boolean showPastePopup(int x, int y) {
        if (!this.mHasInsertion || !canPaste()) {
            return $assertionsDisabled;
        }
        if (supportsFloatingActionMode()) {
            if (this.mActionMode == null) {
                showSelectActionMode($assertionsDisabled);
            } else {
                invalidateActionModeContentRect();
            }
        }
        if (!supportsFloatingActionMode()) {
            if ($assertionsDisabled || this.mActionMode == null) {
                getPastePopup().showAt(x, (int) (((float) y) + this.mRenderCoordinates.getContentOffsetYPix()));
            } else {
                throw new AssertionError();
            }
        }
        return true;
    }

    private void hidePastePopup() {
        if (!this.mHasInsertion) {
            return;
        }
        if (supportsFloatingActionMode()) {
            this.mUnselectAllOnActionModeDismiss = $assertionsDisabled;
            hideSelectActionMode();
        } else if (this.mPastePopupMenu != null) {
            this.mPastePopupMenu.hide();
        }
    }

    private PastePopupMenu getPastePopup() {
        if ($assertionsDisabled || !supportsFloatingActionMode()) {
            if (this.mPastePopupMenu == null) {
                this.mPastePopupMenu = new PastePopupMenu(getContainerView(), new C06068());
            }
            return this.mPastePopupMenu;
        }
        throw new AssertionError();
    }

    @VisibleForTesting
    public PastePopupMenu getPastePopupForTest() {
        return getPastePopup();
    }

    private boolean canPaste() {
        if (this.mFocusedNodeEditable) {
            return ((ClipboardManager) this.mContext.getSystemService("clipboard")).hasPrimaryClip();
        }
        return $assertionsDisabled;
    }

    @CalledByNative
    private void onRenderProcessChange() {
        attachImeAdapter();
        this.mSystemCaptioningBridge.syncToListener(this);
    }

    public void attachImeAdapter() {
        if (this.mImeAdapter != null && this.mNativeContentViewCore != 0) {
            this.mImeAdapter.attach(nativeGetNativeImeAdapter(this.mNativeContentViewCore));
        }
    }

    @CalledByNative
    private boolean hasFocus() {
        if (this.mContainerView.isFocusable()) {
            return this.mContainerView.hasFocus();
        }
        return true;
    }

    public boolean canZoomIn() {
        return this.mRenderCoordinates.getMaxPageScaleFactor() - this.mRenderCoordinates.getPageScaleFactor() > ZOOM_CONTROLS_EPSILON ? true : $assertionsDisabled;
    }

    public boolean canZoomOut() {
        return this.mRenderCoordinates.getPageScaleFactor() - this.mRenderCoordinates.getMinPageScaleFactor() > ZOOM_CONTROLS_EPSILON ? true : $assertionsDisabled;
    }

    public boolean zoomIn() {
        if (canZoomIn()) {
            return pinchByDelta(1.25f);
        }
        return $assertionsDisabled;
    }

    public boolean zoomOut() {
        if (canZoomOut()) {
            return pinchByDelta(0.8f);
        }
        return $assertionsDisabled;
    }

    public boolean zoomReset() {
        if (canZoomOut()) {
            return pinchByDelta(this.mRenderCoordinates.getMinPageScaleFactor() / this.mRenderCoordinates.getPageScaleFactor());
        }
        return $assertionsDisabled;
    }

    public boolean pinchByDelta(float delta) {
        if (this.mNativeContentViewCore == 0) {
            return $assertionsDisabled;
        }
        long timeMs = SystemClock.uptimeMillis();
        int xPix = getViewportWidthPix() / TEXT_STYLE_ITALIC;
        int yPix = getViewportHeightPix() / TEXT_STYLE_ITALIC;
        nativePinchBegin(this.mNativeContentViewCore, timeMs, (float) xPix, (float) yPix);
        nativePinchBy(this.mNativeContentViewCore, timeMs, (float) xPix, (float) yPix, delta);
        nativePinchEnd(this.mNativeContentViewCore, timeMs);
        return true;
    }

    public void invokeZoomPicker() {
        this.mZoomControlsDelegate.invokeZoomPicker();
    }

    public void setAllowJavascriptInterfacesInspection(boolean allow) {
        nativeSetAllowJavascriptInterfacesInspection(this.mNativeContentViewCore, allow);
    }

    public Map<String, Pair<Object, Class>> getJavascriptInterfaces() {
        return this.mJavaScriptInterfaces;
    }

    public void addJavascriptInterface(Object object, String name) {
        addPossiblyUnsafeJavascriptInterface(object, name, JavascriptInterface.class);
    }

    public void addPossiblyUnsafeJavascriptInterface(Object object, String name, Class<? extends Annotation> requiredAnnotation) {
        if (this.mNativeContentViewCore != 0 && object != null) {
            this.mJavaScriptInterfaces.put(name, new Pair(object, requiredAnnotation));
            nativeAddJavascriptInterface(this.mNativeContentViewCore, object, name, requiredAnnotation);
        }
    }

    public void removeJavascriptInterface(String name) {
        this.mJavaScriptInterfaces.remove(name);
        if (this.mNativeContentViewCore != 0) {
            nativeRemoveJavascriptInterface(this.mNativeContentViewCore, name);
        }
    }

    @VisibleForTesting
    public float getScale() {
        return this.mRenderCoordinates.getPageScaleFactor();
    }

    @CalledByNative
    private void startContentIntent(String contentUrl) {
        getContentViewClient().onStartContentIntent(getContext(), contentUrl);
    }

    public void onAccessibilityStateChanged(boolean enabled) {
        setAccessibilityState(enabled);
    }

    public boolean supportsAccessibilityAction(int action) {
        return this.mAccessibilityInjector.supportsAccessibilityAction(action);
    }

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (this.mAccessibilityInjector.supportsAccessibilityAction(action)) {
            return this.mAccessibilityInjector.performAccessibilityAction(action, arguments);
        }
        return $assertionsDisabled;
    }

    public void setBrowserAccessibilityManager(BrowserAccessibilityManager manager) {
        this.mBrowserAccessibilityManager = manager;
        if (this.mBrowserAccessibilityManager != null && this.mRenderCoordinates.hasFrameInfo()) {
            this.mBrowserAccessibilityManager.notifyFrameInfoInitialized();
        }
    }

    public BrowserAccessibilityManager getBrowserAccessibilityManager() {
        return this.mBrowserAccessibilityManager;
    }

    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        if (this.mBrowserAccessibilityManager != null) {
            return this.mBrowserAccessibilityManager.getAccessibilityNodeProvider();
        }
        if (this.mNativeAccessibilityAllowed && !this.mNativeAccessibilityEnabled && this.mNativeContentViewCore != 0 && VERSION.SDK_INT >= 16) {
            this.mNativeAccessibilityEnabled = true;
            nativeSetAccessibilityEnabled(this.mNativeContentViewCore, true);
        }
        return null;
    }

    public void onProvideVirtualStructure(ViewStructure structure) {
        if (getWebContents().isIncognito()) {
            structure.setChildCount(INVALID_RENDER_PROCESS_PID);
            return;
        }
        structure.setChildCount(TEXT_STYLE_BOLD);
        getWebContents().requestAccessibilitySnapshot(new C06079(structure.asyncNewChild(INVALID_RENDER_PROCESS_PID)), this.mRenderCoordinates.getContentOffsetYPix(), this.mRenderCoordinates.getScrollXPix());
    }

    private void createVirtualStructure(ViewStructure viewNode, AccessibilitySnapshotNode node, int parentX, int parentY) {
        int i = INVALID_RENDER_PROCESS_PID;
        viewNode.setClassName(node.className);
        viewNode.setText(node.text);
        viewNode.setDimens((node.f0x - parentX) - node.scrollX, node.f1y - parentY, INVALID_RENDER_PROCESS_PID, node.scrollY, node.width, node.height);
        viewNode.setChildCount(node.children.size());
        if (node.hasStyle) {
            int i2;
            if (node.bold) {
                i2 = TEXT_STYLE_BOLD;
            } else {
                i2 = INVALID_RENDER_PROCESS_PID;
            }
            i2 = (node.underline ? TEXT_STYLE_UNDERLINE : INVALID_RENDER_PROCESS_PID) | ((node.italic ? TEXT_STYLE_ITALIC : INVALID_RENDER_PROCESS_PID) | i2);
            if (node.lineThrough) {
                i = TEXT_STYLE_STRIKE_THRU;
            }
            viewNode.setTextStyle(node.textSize, node.color, node.bgcolor, i2 | i);
        }
        for (int i3 = INVALID_RENDER_PROCESS_PID; i3 < node.children.size(); i3 += TEXT_STYLE_BOLD) {
            createVirtualStructure(viewNode.asyncNewChild(i3), (AccessibilitySnapshotNode) node.children.get(i3), node.f0x, node.f1y);
        }
        viewNode.asyncCommit();
    }

    @TargetApi(19)
    public void onSystemCaptioningChanged(TextTrackSettings settings) {
        if (this.mNativeContentViewCore != 0) {
            nativeSetTextTrackSettings(this.mNativeContentViewCore, settings.getTextTracksEnabled(), settings.getTextTrackBackgroundColor(), settings.getTextTrackFontFamily(), settings.getTextTrackFontStyle(), settings.getTextTrackFontVariant(), settings.getTextTrackTextColor(), settings.getTextTrackTextShadow(), settings.getTextTrackTextSize());
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        this.mAccessibilityInjector.onInitializeAccessibilityNodeInfo(info);
    }

    @TargetApi(15)
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        boolean z = $assertionsDisabled;
        event.setClassName(getClass().getName());
        event.setScrollX(this.mRenderCoordinates.getScrollXPixInt());
        event.setScrollY(this.mRenderCoordinates.getScrollYPixInt());
        int maxScrollXPix = Math.max(INVALID_RENDER_PROCESS_PID, this.mRenderCoordinates.getMaxHorizontalScrollPixInt());
        int maxScrollYPix = Math.max(INVALID_RENDER_PROCESS_PID, this.mRenderCoordinates.getMaxVerticalScrollPixInt());
        if (maxScrollXPix > 0 || maxScrollYPix > 0) {
            z = true;
        }
        event.setScrollable(z);
        if (VERSION.SDK_INT >= 15) {
            event.setMaxScrollX(maxScrollXPix);
            event.setMaxScrollY(maxScrollYPix);
        }
    }

    public boolean isDeviceAccessibilityScriptInjectionEnabled() {
        boolean z = true;
        try {
            if ((VERSION.SDK_INT >= 16 && !CommandLine.getInstance().hasSwitch(ContentSwitches.ENABLE_ACCESSIBILITY_SCRIPT_INJECTION)) || !this.mContentViewClient.isJavascriptEnabled() || getContext().checkCallingOrSelfPermission("android.permission.INTERNET") != 0) {
                return $assertionsDisabled;
            }
            Field field = Secure.class.getField("ACCESSIBILITY_SCRIPT_INJECTION");
            field.setAccessible(true);
            String accessibilityScriptInjection = (String) field.get(null);
            ContentResolver contentResolver = getContext().getContentResolver();
            if (this.mAccessibilityScriptInjectionObserver == null) {
                ContentObserver contentObserver = new AnonymousClass10(new Handler());
                contentResolver.registerContentObserver(Secure.getUriFor(accessibilityScriptInjection), $assertionsDisabled, contentObserver);
                this.mAccessibilityScriptInjectionObserver = contentObserver;
            }
            if (Secure.getInt(contentResolver, accessibilityScriptInjection, INVALID_RENDER_PROCESS_PID) != TEXT_STYLE_BOLD) {
                z = $assertionsDisabled;
            }
            return z;
        } catch (NoSuchFieldException e) {
            return $assertionsDisabled;
        } catch (IllegalAccessException e2) {
            return $assertionsDisabled;
        }
    }

    public boolean isInjectingAccessibilityScript() {
        return this.mAccessibilityInjector.accessibilityIsAvailable();
    }

    public boolean isTouchExplorationEnabled() {
        return this.mTouchExplorationEnabled;
    }

    public void setAccessibilityState(boolean state) {
        boolean z = $assertionsDisabled;
        if (state) {
            boolean useScriptInjection = isDeviceAccessibilityScriptInjectionEnabled();
            setInjectedAccessibility(useScriptInjection);
            if (!useScriptInjection) {
                z = true;
            }
            this.mNativeAccessibilityAllowed = z;
            this.mTouchExplorationEnabled = this.mAccessibilityManager.isTouchExplorationEnabled();
            return;
        }
        setInjectedAccessibility($assertionsDisabled);
        this.mNativeAccessibilityAllowed = $assertionsDisabled;
        this.mTouchExplorationEnabled = $assertionsDisabled;
    }

    public void setInjectedAccessibility(boolean enabled) {
        this.mAccessibilityInjector.addOrRemoveAccessibilityApisIfNecessary();
        this.mAccessibilityInjector.setScriptEnabled(enabled);
    }

    public void stopCurrentAccessibilityNotifications() {
        this.mAccessibilityInjector.onPageLostFocus();
    }

    public boolean shouldSetAccessibilityFocusOnPageLoad() {
        return this.mShouldSetAccessibilityFocusOnPageLoad;
    }

    public void setShouldSetAccessibilityFocusOnPageLoad(boolean on) {
        this.mShouldSetAccessibilityFocusOnPageLoad = on;
    }

    public RenderCoordinates getRenderCoordinates() {
        return this.mRenderCoordinates;
    }

    public boolean getIsMobileOptimizedHint() {
        return this.mIsMobileOptimizedHint;
    }

    @CalledByNative
    private static Rect createRect(int x, int y, int right, int bottom) {
        return new Rect(x, y, right, bottom);
    }

    public void extractSmartClipData(int x, int y, int width, int height) {
        if (this.mNativeContentViewCore != 0) {
            nativeExtractSmartClipData(this.mNativeContentViewCore, x + this.mSmartClipOffsetX, y + this.mSmartClipOffsetY, width, height);
        }
    }

    public void setSmartClipOffsets(int offsetX, int offsetY) {
        this.mSmartClipOffsetX = offsetX;
        this.mSmartClipOffsetY = offsetY;
    }

    @CalledByNative
    private void onSmartClipDataExtracted(String text, String html, Rect clipRect) {
        float deviceScale = this.mRenderCoordinates.getDeviceScaleFactor();
        clipRect.offset(-((int) (((float) this.mSmartClipOffsetX) / deviceScale)), -((int) (((float) this.mSmartClipOffsetY) / deviceScale)));
        if (this.mSmartClipDataListener != null) {
            this.mSmartClipDataListener.onSmartClipDataExtracted(text, html, clipRect);
        }
    }

    public void setSmartClipDataListener(SmartClipDataListener listener) {
        this.mSmartClipDataListener = listener;
    }

    public void setBackgroundOpaque(boolean opaque) {
        if (this.mNativeContentViewCore != 0) {
            nativeSetBackgroundOpaque(this.mNativeContentViewCore, opaque);
        }
    }

    private boolean offerLongPressToEmbedder() {
        return this.mContainerView.performLongClick();
    }

    private void resetScrollInProgress() {
        if (isScrollInProgress()) {
            boolean touchScrollInProgress = this.mTouchScrollInProgress;
            int potentiallyActiveFlingCount = this.mPotentiallyActiveFlingCount;
            this.mTouchScrollInProgress = $assertionsDisabled;
            this.mPotentiallyActiveFlingCount = INVALID_RENDER_PROCESS_PID;
            if (touchScrollInProgress) {
                updateGestureStateListener(TEXT_STYLE_STRIKE_THRU);
            }
            if (potentiallyActiveFlingCount > 0) {
                updateGestureStateListener(11);
            }
        }
    }

    ContentVideoViewClient getContentVideoViewClient() {
        return getContentViewClient().getContentVideoViewClient();
    }

    @CalledByNative
    private boolean shouldBlockMediaRequest(String url) {
        return getContentViewClient().shouldBlockMediaRequest(url);
    }

    @CalledByNative
    private void onNativeFlingStopped() {
        this.mTouchScrollInProgress = $assertionsDisabled;
        if (this.mPotentiallyActiveFlingCount > 0) {
            this.mPotentiallyActiveFlingCount--;
            updateGestureStateListener(11);
        }
    }

    public void onScreenOrientationChanged(int orientation) {
        sendOrientationChangeEvent(orientation);
    }

    public void setFullscreenRequiredForOrientationLock(boolean value) {
        this.mFullscreenRequiredForOrientationLock = value;
    }

    @CalledByNative
    private boolean isFullscreenRequiredForOrientationLock() {
        return this.mFullscreenRequiredForOrientationLock;
    }

    public void setContextualSearchClient(ContextualSearchClient contextualSearchClient) {
        this.mContextualSearchClient = contextualSearchClient;
    }
}
