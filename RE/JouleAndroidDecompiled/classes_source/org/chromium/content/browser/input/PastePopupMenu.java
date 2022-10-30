package org.chromium.content.browser.input;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import com.adobe.phonegap.push.PushConstants;

public class PastePopupMenu implements OnClickListener {
    private final PopupWindow mContainer;
    private final Context mContext;
    private final PastePopupMenuDelegate mDelegate;
    private final int mLineOffsetY;
    private final View mParent;
    private View mPasteView;
    private final int mPasteViewLayout;
    private int mPositionX;
    private int mPositionY;
    private int mRawPositionX;
    private int mRawPositionY;
    private int mStatusBarHeight;
    private final int mWidthOffsetX;

    public interface PastePopupMenuDelegate {
        void paste();
    }

    public PastePopupMenu(View parent, PastePopupMenuDelegate delegate) {
        this.mParent = parent;
        this.mDelegate = delegate;
        this.mContext = parent.getContext();
        this.mContainer = new PopupWindow(this.mContext, null, 16843464);
        this.mContainer.setSplitTouchEnabled(true);
        this.mContainer.setClippingEnabled(false);
        this.mContainer.setAnimationStyle(0);
        this.mContainer.setWidth(-2);
        this.mContainer.setHeight(-2);
        int[] popupLayoutAttrs = new int[]{16843540};
        this.mPasteView = null;
        TypedArray attrs = this.mContext.getTheme().obtainStyledAttributes(popupLayoutAttrs);
        this.mPasteViewLayout = attrs.getResourceId(attrs.getIndex(0), 0);
        attrs.recycle();
        this.mLineOffsetY = (int) TypedValue.applyDimension(1, 5.0f, this.mContext.getResources().getDisplayMetrics());
        this.mWidthOffsetX = (int) TypedValue.applyDimension(1, 30.0f, this.mContext.getResources().getDisplayMetrics());
        int statusBarHeightResourceId = this.mContext.getResources().getIdentifier("status_bar_height", "dimen", PushConstants.ANDROID);
        if (statusBarHeightResourceId > 0) {
            this.mStatusBarHeight = this.mContext.getResources().getDimensionPixelSize(statusBarHeightResourceId);
        }
    }

    public void showAt(int x, int y) {
        updateContent();
        positionAt(x, y);
    }

    public void hide() {
        this.mContainer.dismiss();
    }

    public boolean isShowing() {
        return this.mContainer.isShowing();
    }

    public void onClick(View v) {
        paste();
        hide();
    }

    private void positionAt(int x, int y) {
        if (this.mRawPositionX != x || this.mRawPositionY != y || !isShowing()) {
            this.mRawPositionX = x;
            this.mRawPositionY = y;
            View contentView = this.mContainer.getContentView();
            int width = contentView.getMeasuredWidth();
            int height = contentView.getMeasuredHeight();
            this.mPositionX = (int) (((float) x) - (((float) width) / 2.0f));
            this.mPositionY = (y - height) - this.mLineOffsetY;
            coords = new int[2];
            this.mParent.getLocationInWindow(coords);
            coords[0] = coords[0] + this.mPositionX;
            coords[1] = coords[1] + this.mPositionY;
            int minOffsetY = 0;
            if (this.mParent.getSystemUiVisibility() == 0) {
                minOffsetY = this.mStatusBarHeight;
            }
            int screenWidth = this.mContext.getResources().getDisplayMetrics().widthPixels;
            if (coords[1] < minOffsetY) {
                coords[1] = coords[1] + height;
                coords[1] = coords[1] + this.mLineOffsetY;
                int handleHalfWidth = this.mWidthOffsetX / 2;
                if (x + width < screenWidth) {
                    coords[0] = coords[0] + ((width / 2) + handleHalfWidth);
                } else {
                    coords[0] = coords[0] - ((width / 2) + handleHalfWidth);
                }
            } else {
                coords[0] = Math.max(0, coords[0]);
                coords[0] = Math.min(screenWidth - width, coords[0]);
            }
            if (isShowing()) {
                this.mContainer.update(coords[0], coords[1], -1, -1);
            } else {
                this.mContainer.showAtLocation(this.mParent, 0, coords[0], coords[1]);
            }
        }
    }

    private void updateContent() {
        if (this.mPasteView == null) {
            int layout = this.mPasteViewLayout;
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
            if (inflater != null) {
                this.mPasteView = inflater.inflate(layout, null);
            }
            if (this.mPasteView == null) {
                throw new IllegalArgumentException("Unable to inflate TextEdit paste window");
            }
            int size = MeasureSpec.makeMeasureSpec(0, 0);
            this.mPasteView.setLayoutParams(new LayoutParams(-2, -2));
            this.mPasteView.measure(size, size);
            this.mPasteView.setOnClickListener(this);
        }
        this.mContainer.setContentView(this.mPasteView);
    }

    private void paste() {
        this.mDelegate.paste();
    }
}
