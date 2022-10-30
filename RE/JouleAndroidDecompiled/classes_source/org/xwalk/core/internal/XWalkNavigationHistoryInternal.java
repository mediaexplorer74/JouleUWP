package org.xwalk.core.internal;

import com.google.android.gms.common.ConnectionResult;
import java.io.Serializable;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.content_public.browser.NavigationHistory;

@XWalkAPI(createInternally = true)
public class XWalkNavigationHistoryInternal implements Cloneable, Serializable {
    private NavigationHistory mHistory;
    private XWalkViewInternal mXWalkView;

    /* renamed from: org.xwalk.core.internal.XWalkNavigationHistoryInternal.1 */
    static /* synthetic */ class C04621 {
        static final /* synthetic */ int[] f7xf8f64db9;

        static {
            f7xf8f64db9 = new int[DirectionInternal.values().length];
            try {
                f7xf8f64db9[DirectionInternal.FORWARD.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f7xf8f64db9[DirectionInternal.BACKWARD.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    @XWalkAPI
    public enum DirectionInternal {
        BACKWARD,
        FORWARD
    }

    XWalkNavigationHistoryInternal() {
        this.mXWalkView = null;
        this.mHistory = null;
    }

    XWalkNavigationHistoryInternal(XWalkViewInternal view, NavigationHistory history) {
        this.mXWalkView = view;
        this.mHistory = history;
    }

    XWalkNavigationHistoryInternal(XWalkNavigationHistoryInternal history) {
        this.mXWalkView = history.mXWalkView;
        this.mHistory = history.mHistory;
    }

    @XWalkAPI
    public int size() {
        return this.mHistory.getEntryCount();
    }

    @XWalkAPI
    public boolean hasItemAt(int index) {
        return index >= 0 && index <= size() - 1;
    }

    @XWalkAPI
    public XWalkNavigationItemInternal getItemAt(int index) {
        if (index < 0 || index >= size()) {
            return null;
        }
        return new XWalkNavigationItemInternal(this.mHistory.getEntryAtIndex(index));
    }

    @XWalkAPI
    public XWalkNavigationItemInternal getCurrentItem() {
        return getItemAt(getCurrentIndex());
    }

    @XWalkAPI
    public boolean canGoBack() {
        return this.mXWalkView.canGoBack();
    }

    @XWalkAPI
    public boolean canGoForward() {
        return this.mXWalkView.canGoForward();
    }

    @XWalkAPI
    public void navigate(DirectionInternal direction, int steps) {
        switch (C04621.f7xf8f64db9[direction.ordinal()]) {
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                this.mXWalkView.navigateTo(steps);
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                this.mXWalkView.navigateTo(-steps);
            default:
        }
    }

    @XWalkAPI
    public int getCurrentIndex() {
        return this.mHistory.getCurrentEntryIndex();
    }

    @XWalkAPI
    public void clear() {
        this.mXWalkView.clearHistory();
    }

    protected synchronized XWalkNavigationHistoryInternal clone() {
        return new XWalkNavigationHistoryInternal(this);
    }
}
