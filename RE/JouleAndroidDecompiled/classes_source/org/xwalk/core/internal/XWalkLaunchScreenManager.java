package org.xwalk.core.internal;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.adobe.phonegap.push.PushConstants;
import java.util.ArrayList;
import org.chromium.blink_public.web.WebInputEventModifier;
import org.chromium.content.browser.ContentViewRenderView.FirstRenderedFrameListener;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;

public class XWalkLaunchScreenManager implements FirstRenderedFrameListener, OnShowListener, OnDismissListener, PageLoadListener {
    private static final String BORDER_MODE_REPEAT = "repeat";
    private static final String BORDER_MODE_ROUND = "round";
    private static final String BORDER_MODE_STRETCH = "stretch";
    private static String mIntentFilterStr;
    private Activity mActivity;
    private int mCurrentOrientation;
    private boolean mCustomHideLaunchScreen;
    private boolean mFirstFrameReceived;
    private Dialog mLaunchScreenDialog;
    private BroadcastReceiver mLaunchScreenReadyWhenReceiver;
    private Context mLibContext;
    private OrientationEventListener mOrientationListener;
    private boolean mPageLoadFinished;
    private ReadyWhenType mReadyWhen;
    private XWalkViewInternal mXWalkView;

    /* renamed from: org.xwalk.core.internal.XWalkLaunchScreenManager.1 */
    class C04601 implements Runnable {
        final /* synthetic */ String val$imageBorderList;

        /* renamed from: org.xwalk.core.internal.XWalkLaunchScreenManager.1.1 */
        class C04581 implements OnKeyListener {
            C04581() {
            }

            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == 4) {
                    XWalkLaunchScreenManager.this.performHideLaunchScreen();
                    XWalkLaunchScreenManager.this.mActivity.onBackPressed();
                }
                return true;
            }
        }

        /* renamed from: org.xwalk.core.internal.XWalkLaunchScreenManager.1.2 */
        class C04592 extends OrientationEventListener {
            C04592(Context x0, int x1) {
                super(x0, x1);
            }

            public void onOrientationChanged(int ori) {
                if (XWalkLaunchScreenManager.this.mLaunchScreenDialog != null && XWalkLaunchScreenManager.this.mLaunchScreenDialog.isShowing() && XWalkLaunchScreenManager.this.getScreenOrientation() != XWalkLaunchScreenManager.this.mCurrentOrientation) {
                    RelativeLayout root = XWalkLaunchScreenManager.this.getLaunchScreenLayout(C04601.this.val$imageBorderList);
                    if (root != null) {
                        XWalkLaunchScreenManager.this.mLaunchScreenDialog.setContentView(root);
                    }
                }
            }
        }

        C04601(String str) {
            this.val$imageBorderList = str;
        }

        public void run() {
            int bgResId = XWalkLaunchScreenManager.this.mActivity.getResources().getIdentifier("launchscreen_bg", PushConstants.DRAWABLE, XWalkLaunchScreenManager.this.mActivity.getPackageName());
            if (bgResId != 0) {
                Drawable bgDrawable = null;
                try {
                    bgDrawable = XWalkLaunchScreenManager.this.mActivity.getResources().getDrawable(bgResId);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
                if (bgDrawable != null) {
                    XWalkLaunchScreenManager.this.mLaunchScreenDialog = new Dialog(XWalkLaunchScreenManager.this.mLibContext, 16974064);
                    XWalkLaunchScreenManager.this.mLaunchScreenDialog.setOnKeyListener(new C04581());
                    XWalkLaunchScreenManager.this.mLaunchScreenDialog.setOnShowListener(XWalkLaunchScreenManager.this);
                    XWalkLaunchScreenManager.this.mLaunchScreenDialog.setOnDismissListener(XWalkLaunchScreenManager.this);
                    XWalkLaunchScreenManager.this.mLaunchScreenDialog.getWindow().setBackgroundDrawable(bgDrawable);
                    RelativeLayout root = XWalkLaunchScreenManager.this.getLaunchScreenLayout(this.val$imageBorderList);
                    if (root != null) {
                        XWalkLaunchScreenManager.this.mLaunchScreenDialog.setContentView(root);
                    }
                    XWalkLaunchScreenManager.this.mLaunchScreenDialog.show();
                    XWalkLaunchScreenManager.this.mOrientationListener = new C04592(XWalkLaunchScreenManager.this.mActivity, 3);
                    XWalkLaunchScreenManager.this.mOrientationListener.enable();
                    if (XWalkLaunchScreenManager.this.mReadyWhen == ReadyWhenType.CUSTOM) {
                        XWalkLaunchScreenManager.this.registerBroadcastReceiver();
                    }
                }
            }
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkLaunchScreenManager.2 */
    class C04612 extends BroadcastReceiver {
        C04612() {
        }

        public void onReceive(Context context, Intent intent) {
            XWalkLaunchScreenManager.this.mCustomHideLaunchScreen = true;
            XWalkLaunchScreenManager.this.hideLaunchScreenWhenReady();
        }
    }

    private enum BorderModeType {
        REPEAT,
        STRETCH,
        ROUND,
        NONE
    }

    private enum ReadyWhenType {
        FIRST_PAINT,
        USER_INTERACTIVE,
        COMPLETE,
        CUSTOM
    }

    public XWalkLaunchScreenManager(Context context, XWalkViewInternal xwView) {
        this.mXWalkView = xwView;
        this.mLibContext = context;
        this.mActivity = this.mXWalkView.getActivity();
        mIntentFilterStr = this.mActivity.getPackageName() + ".hideLaunchScreen";
    }

    public void displayLaunchScreen(String readyWhen, String imageBorderList) {
        if (this.mXWalkView != null) {
            setReadyWhen(readyWhen);
            this.mActivity.runOnUiThread(new C04601(imageBorderList));
        }
    }

    public void onFirstFrameReceived() {
        this.mFirstFrameReceived = true;
        hideLaunchScreenWhenReady();
    }

    public void onShow(DialogInterface dialog) {
        this.mActivity.getWindow().setBackgroundDrawable(null);
        if (this.mFirstFrameReceived) {
            hideLaunchScreenWhenReady();
        }
    }

    public void onDismiss(DialogInterface dialog) {
        this.mOrientationListener.disable();
        this.mOrientationListener = null;
    }

    public void onPageFinished(String url) {
        this.mPageLoadFinished = true;
        hideLaunchScreenWhenReady();
    }

    public static String getHideLaunchScreenFilterStr() {
        return mIntentFilterStr;
    }

    public int getScreenOrientation() {
        Display display = this.mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if (size.x < size.y) {
            return 1;
        }
        return 2;
    }

    private RelativeLayout getLaunchScreenLayout(String imageBorderList) {
        String[] borders = imageBorderList.split(";");
        if (borders.length < 1) {
            return parseImageBorder(CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
        }
        int orientation = getScreenOrientation();
        this.mCurrentOrientation = orientation;
        if (borders.length < 2 || orientation != 2) {
            if (borders.length != 3 || orientation != 1) {
                return parseImageBorder(borders[0]);
            }
            if (borders[2].equals("empty")) {
                return parseImageBorder(CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
            }
            if (borders[2].isEmpty()) {
                return parseImageBorder(borders[0]);
            }
            return parseImageBorder(borders[2]);
        } else if (borders[1].equals("empty")) {
            return parseImageBorder(CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
        } else {
            if (borders[1].isEmpty()) {
                return parseImageBorder(borders[0]);
            }
            return parseImageBorder(borders[1]);
        }
    }

    private int getSuitableSize(int maxSize, int divider) {
        int finalSize = divider;
        float minMod = (float) divider;
        while (divider > 1) {
            int mod = maxSize % divider;
            if (mod == 0) {
                return divider;
            }
            if (((float) mod) < minMod) {
                minMod = (float) mod;
                finalSize = divider;
            }
            divider--;
        }
        return finalSize;
    }

    private ImageView getSubImageView(Bitmap img, int x, int y, int width, int height, BorderModeType mode, int maxWidth, int maxHeight) {
        if (img == null) {
            return null;
        }
        if (width <= 0 || height <= 0) {
            return null;
        }
        if (!new Rect(0, 0, img.getWidth(), img.getHeight()).contains(new Rect(x, y, x + width, y + height))) {
            return null;
        }
        Bitmap subImage = Bitmap.createBitmap(img, x, y, width, height);
        ImageView subImageView = new ImageView(this.mActivity);
        if (mode == BorderModeType.ROUND) {
            int originW = subImage.getWidth();
            int originH = subImage.getHeight();
            int newW = originW;
            int newH = originH;
            if (maxWidth > 0) {
                newW = getSuitableSize(maxWidth, originW);
            }
            if (maxHeight > 0) {
                newH = getSuitableSize(maxHeight, originH);
            }
            subImage = Bitmap.createScaledBitmap(subImage, newW, newH, true);
            mode = BorderModeType.REPEAT;
        }
        if (mode == BorderModeType.REPEAT) {
            BitmapDrawable drawable = new BitmapDrawable(this.mActivity.getResources(), subImage);
            drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            subImageView.setImageDrawable(drawable);
            subImageView.setScaleType(ScaleType.FIT_XY);
            return subImageView;
        } else if (mode == BorderModeType.STRETCH) {
            subImageView.setImageBitmap(subImage);
            subImageView.setScaleType(ScaleType.FIT_XY);
            return subImageView;
        } else {
            subImageView.setImageBitmap(subImage);
            return subImageView;
        }
    }

    private int getStatusBarHeight() {
        int resourceId = this.mActivity.getResources().getIdentifier("status_bar_height", "dimen", PushConstants.ANDROID);
        if (resourceId > 0) {
            return this.mActivity.getResources().getDimensionPixelSize(resourceId);
        }
        return 25;
    }

    private RelativeLayout parseImageBorder(String imageBorder) {
        int topBorder = 0;
        int rightBorder = 0;
        int leftBorder = 0;
        int bottomBorder = 0;
        BorderModeType horizontalMode = BorderModeType.STRETCH;
        BorderModeType verticalMode = BorderModeType.STRETCH;
        if (imageBorder.equals("empty")) {
            imageBorder = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        }
        String[] items = imageBorder.split(" ");
        ArrayList<String> borders = new ArrayList();
        ArrayList<BorderModeType> modes = new ArrayList();
        for (String item : items) {
            if (item.endsWith("px")) {
                borders.add(item.replaceAll("px", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE));
            } else {
                if (item.equals(BORDER_MODE_REPEAT)) {
                    modes.add(BorderModeType.REPEAT);
                } else {
                    if (item.equals(BORDER_MODE_STRETCH)) {
                        modes.add(BorderModeType.STRETCH);
                    } else {
                        if (item.equals(BORDER_MODE_ROUND)) {
                            modes.add(BorderModeType.ROUND);
                        }
                    }
                }
            }
        }
        try {
            if (borders.size() == 1) {
                bottomBorder = Integer.valueOf((String) borders.get(0)).intValue();
                leftBorder = bottomBorder;
                rightBorder = bottomBorder;
                topBorder = bottomBorder;
            } else if (borders.size() == 2) {
                bottomBorder = Integer.valueOf((String) borders.get(0)).intValue();
                topBorder = bottomBorder;
                leftBorder = Integer.valueOf((String) borders.get(1)).intValue();
                rightBorder = leftBorder;
            } else if (borders.size() == 3) {
                leftBorder = Integer.valueOf((String) borders.get(1)).intValue();
                rightBorder = leftBorder;
                topBorder = Integer.valueOf((String) borders.get(0)).intValue();
                bottomBorder = Integer.valueOf((String) borders.get(2)).intValue();
            } else if (borders.size() == 4) {
                topBorder = Integer.valueOf((String) borders.get(0)).intValue();
                rightBorder = Integer.valueOf((String) borders.get(1)).intValue();
                leftBorder = Integer.valueOf((String) borders.get(2)).intValue();
                bottomBorder = Integer.valueOf((String) borders.get(3)).intValue();
            }
        } catch (NumberFormatException e) {
            bottomBorder = 0;
            leftBorder = 0;
            rightBorder = 0;
            topBorder = 0;
        }
        DisplayMetrics matrix = this.mActivity.getResources().getDisplayMetrics();
        topBorder = (int) TypedValue.applyDimension(1, (float) topBorder, matrix);
        rightBorder = (int) TypedValue.applyDimension(1, (float) rightBorder, matrix);
        leftBorder = (int) TypedValue.applyDimension(1, (float) leftBorder, matrix);
        bottomBorder = (int) TypedValue.applyDimension(1, (float) bottomBorder, matrix);
        if (modes.size() == 1) {
            verticalMode = (BorderModeType) modes.get(0);
            horizontalMode = verticalMode;
        } else if (modes.size() == 2) {
            horizontalMode = (BorderModeType) modes.get(0);
            verticalMode = (BorderModeType) modes.get(1);
        }
        int imgResId = this.mActivity.getResources().getIdentifier("launchscreen_img", PushConstants.DRAWABLE, this.mActivity.getPackageName());
        if (imgResId == 0) {
            return null;
        }
        Bitmap img = BitmapFactory.decodeResource(this.mActivity.getResources(), imgResId);
        if (img == null) {
            return null;
        }
        RelativeLayout relativeLayout = new RelativeLayout(this.mActivity);
        relativeLayout.setLayoutParams(new LayoutParams(-1, -1));
        if (borders.size() == 0) {
            View imageView = new ImageView(this.mActivity);
            imageView.setImageBitmap(img);
            ViewGroup.LayoutParams layoutParams = new LayoutParams(-2, -2);
            layoutParams.addRule(13, -1);
            relativeLayout.addView(imageView, layoutParams);
            return relativeLayout;
        }
        Display display = this.mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if ((this.mActivity.getWindow().getAttributes().flags & WebInputEventModifier.NumLockOn) == 0) {
            size.y -= getStatusBarHeight();
        }
        View subImageView = getSubImageView(img, 0, 0, leftBorder, topBorder, BorderModeType.NONE, 0, 0);
        if (subImageView != null) {
            layoutParams = new LayoutParams(-2, -2);
            layoutParams.addRule(9, -1);
            layoutParams.addRule(10, -1);
            relativeLayout.addView(subImageView, layoutParams);
        }
        subImageView = getSubImageView(img, leftBorder, 0, (img.getWidth() - leftBorder) - rightBorder, topBorder, horizontalMode, (size.x - leftBorder) - rightBorder, 0);
        if (subImageView != null) {
            layoutParams = new LayoutParams(-1, -2);
            layoutParams.addRule(10, -1);
            layoutParams.addRule(14, -1);
            layoutParams.leftMargin = leftBorder;
            layoutParams.rightMargin = rightBorder;
            relativeLayout.addView(subImageView, layoutParams);
        }
        subImageView = getSubImageView(img, img.getWidth() - rightBorder, 0, rightBorder, topBorder, BorderModeType.NONE, 0, 0);
        if (subImageView != null) {
            layoutParams = new LayoutParams(-2, -2);
            layoutParams.addRule(11, -1);
            layoutParams.addRule(10, -1);
            relativeLayout.addView(subImageView, layoutParams);
        }
        subImageView = getSubImageView(img, 0, topBorder, leftBorder, (img.getHeight() - topBorder) - bottomBorder, verticalMode, 0, (size.y - topBorder) - bottomBorder);
        if (subImageView != null) {
            layoutParams = new LayoutParams(-2, -1);
            layoutParams.addRule(9, -1);
            layoutParams.addRule(13, -1);
            layoutParams.topMargin = topBorder;
            layoutParams.bottomMargin = bottomBorder;
            relativeLayout.addView(subImageView, layoutParams);
        }
        subImageView = getSubImageView(img, leftBorder, topBorder, (img.getWidth() - leftBorder) - rightBorder, (img.getHeight() - topBorder) - bottomBorder, BorderModeType.NONE, 0, 0);
        if (subImageView != null) {
            subImageView.setScaleType(ScaleType.FIT_XY);
            layoutParams = new LayoutParams(-1, -1);
            layoutParams.leftMargin = leftBorder;
            layoutParams.topMargin = topBorder;
            layoutParams.rightMargin = rightBorder;
            layoutParams.bottomMargin = bottomBorder;
            relativeLayout.addView(subImageView, layoutParams);
        }
        subImageView = getSubImageView(img, img.getWidth() - rightBorder, topBorder, rightBorder, (img.getHeight() - topBorder) - bottomBorder, verticalMode, 0, (size.y - topBorder) - bottomBorder);
        if (subImageView != null) {
            layoutParams = new LayoutParams(-2, -1);
            layoutParams.addRule(13, -1);
            layoutParams.addRule(11, -1);
            layoutParams.topMargin = topBorder;
            layoutParams.bottomMargin = bottomBorder;
            relativeLayout.addView(subImageView, layoutParams);
        }
        subImageView = getSubImageView(img, 0, img.getHeight() - bottomBorder, leftBorder, bottomBorder, BorderModeType.NONE, 0, 0);
        if (subImageView != null) {
            layoutParams = new LayoutParams(-2, -2);
            layoutParams.addRule(9, -1);
            layoutParams.addRule(12, -1);
            relativeLayout.addView(subImageView, layoutParams);
        }
        subImageView = getSubImageView(img, leftBorder, img.getHeight() - bottomBorder, (img.getWidth() - leftBorder) - rightBorder, bottomBorder, horizontalMode, (size.x - leftBorder) - rightBorder, 0);
        if (subImageView != null) {
            layoutParams = new LayoutParams(-1, -2);
            layoutParams.addRule(14, -1);
            layoutParams.addRule(12, -1);
            layoutParams.leftMargin = leftBorder;
            layoutParams.rightMargin = rightBorder;
            relativeLayout.addView(subImageView, layoutParams);
        }
        subImageView = getSubImageView(img, img.getWidth() - rightBorder, img.getHeight() - bottomBorder, rightBorder, bottomBorder, BorderModeType.NONE, 0, 0);
        if (subImageView == null) {
            return relativeLayout;
        }
        layoutParams = new LayoutParams(-2, -2);
        layoutParams.addRule(11, -1);
        layoutParams.addRule(12, -1);
        relativeLayout.addView(subImageView, layoutParams);
        return relativeLayout;
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(mIntentFilterStr);
        this.mLaunchScreenReadyWhenReceiver = new C04612();
        this.mActivity.registerReceiver(this.mLaunchScreenReadyWhenReceiver, intentFilter);
    }

    private void hideLaunchScreenWhenReady() {
        if (this.mLaunchScreenDialog != null && this.mFirstFrameReceived) {
            if (this.mReadyWhen == ReadyWhenType.FIRST_PAINT) {
                performHideLaunchScreen();
            } else if (this.mReadyWhen == ReadyWhenType.USER_INTERACTIVE) {
                performHideLaunchScreen();
            } else if (this.mReadyWhen == ReadyWhenType.COMPLETE) {
                if (this.mPageLoadFinished) {
                    performHideLaunchScreen();
                }
            } else if (this.mReadyWhen == ReadyWhenType.CUSTOM && this.mCustomHideLaunchScreen) {
                performHideLaunchScreen();
            }
        }
    }

    private void performHideLaunchScreen() {
        if (this.mLaunchScreenDialog != null) {
            this.mLaunchScreenDialog.dismiss();
            this.mLaunchScreenDialog = null;
        }
        if (this.mReadyWhen == ReadyWhenType.CUSTOM) {
            this.mActivity.unregisterReceiver(this.mLaunchScreenReadyWhenReceiver);
        }
    }

    private void setReadyWhen(String readyWhen) {
        if (readyWhen.equals("first-paint")) {
            this.mReadyWhen = ReadyWhenType.FIRST_PAINT;
        } else if (readyWhen.equals("user-interactive")) {
            this.mReadyWhen = ReadyWhenType.USER_INTERACTIVE;
        } else if (readyWhen.equals("complete")) {
            this.mReadyWhen = ReadyWhenType.COMPLETE;
        } else if (readyWhen.equals("custom")) {
            this.mReadyWhen = ReadyWhenType.CUSTOM;
        } else {
            this.mReadyWhen = ReadyWhenType.FIRST_PAINT;
        }
    }
}
