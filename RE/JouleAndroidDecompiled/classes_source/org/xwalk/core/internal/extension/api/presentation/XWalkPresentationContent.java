package org.xwalk.core.internal.extension.api.presentation;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import java.lang.ref.WeakReference;
import org.xwalk.core.internal.XWalkUIClientInternal;
import org.xwalk.core.internal.XWalkUIClientInternal.LoadStatusInternal;
import org.xwalk.core.internal.XWalkViewInternal;

public class XWalkPresentationContent {
    public final int INVALID_PRESENTATION_ID;
    private WeakReference<Activity> mActivity;
    private XWalkViewInternal mContentView;
    private Context mContext;
    private PresentationDelegate mDelegate;
    private int mPresentationId;

    public interface PresentationDelegate {
        void onContentClosed(XWalkPresentationContent xWalkPresentationContent);

        void onContentLoaded(XWalkPresentationContent xWalkPresentationContent);
    }

    /* renamed from: org.xwalk.core.internal.extension.api.presentation.XWalkPresentationContent.1 */
    class C06711 extends XWalkUIClientInternal {
        C06711(XWalkViewInternal x0) {
            super(x0);
        }

        public void onJavascriptCloseWindow(XWalkViewInternal view) {
            XWalkPresentationContent.this.mPresentationId = -1;
            XWalkPresentationContent.this.onContentClosed();
        }

        public void onPageLoadStarted(XWalkViewInternal view, String url) {
            XWalkPresentationContent.this.mPresentationId = XWalkPresentationContent.this.mContentView.getContentID();
            view.evaluateJavascript(("navigator.presentation.session = new navigator.presentation.PresentationSession(" + XWalkPresentationContent.this.mPresentationId) + ");", null);
        }

        public void onPageLoadStopped(XWalkViewInternal view, String url, LoadStatusInternal status) {
            if (status == LoadStatusInternal.FINISHED) {
                XWalkPresentationContent.this.onContentLoaded();
            }
        }
    }

    public XWalkPresentationContent(Context context, WeakReference<Activity> activity, PresentationDelegate delegate) {
        this.INVALID_PRESENTATION_ID = -1;
        this.mPresentationId = -1;
        this.mContext = context;
        this.mActivity = activity;
        this.mDelegate = delegate;
    }

    public void load(String url) {
        Activity activity = (Activity) this.mActivity.get();
        if (activity != null) {
            if (this.mContentView == null) {
                this.mContentView = new XWalkViewInternal(this.mContext, activity);
                this.mContentView.setUIClient(new C06711(this.mContentView));
            }
            this.mContentView.load(url, null);
        }
    }

    public int getPresentationId() {
        return this.mPresentationId;
    }

    public View getContentView() {
        return this.mContentView;
    }

    public void close() {
        this.mContentView.onDestroy();
        this.mPresentationId = -1;
        this.mContentView = null;
    }

    public void onPause() {
        this.mContentView.pauseTimers();
        this.mContentView.onHide();
    }

    public void onResume() {
        this.mContentView.resumeTimers();
        this.mContentView.onShow();
    }

    private void onContentLoaded() {
        if (this.mDelegate != null) {
            this.mDelegate.onContentLoaded(this);
        }
    }

    private void onContentClosed() {
        if (this.mDelegate != null) {
            this.mDelegate.onContentClosed(this);
        }
    }
}
