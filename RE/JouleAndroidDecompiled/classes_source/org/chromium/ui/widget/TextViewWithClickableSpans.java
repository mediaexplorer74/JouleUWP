package org.chromium.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.accessibility.AccessibilityManager;
import android.widget.PopupMenu;
import android.widget.TextView;

public class TextViewWithClickableSpans extends TextView {
    private AccessibilityManager mAccessibilityManager;

    /* renamed from: org.chromium.ui.widget.TextViewWithClickableSpans.1 */
    class C04251 implements OnLongClickListener {
        C04251() {
        }

        public boolean onLongClick(View v) {
            if (!TextViewWithClickableSpans.this.mAccessibilityManager.isTouchExplorationEnabled()) {
                return false;
            }
            TextViewWithClickableSpans.this.openDisambiguationMenu();
            return true;
        }
    }

    /* renamed from: org.chromium.ui.widget.TextViewWithClickableSpans.2 */
    class C04262 implements OnMenuItemClickListener {
        final /* synthetic */ ClickableSpan val$clickableSpan;

        C04262(ClickableSpan clickableSpan) {
            this.val$clickableSpan = clickableSpan;
        }

        public boolean onMenuItemClick(MenuItem menuItem) {
            this.val$clickableSpan.onClick(TextViewWithClickableSpans.this);
            return true;
        }
    }

    public TextViewWithClickableSpans(Context context) {
        super(context);
        init();
    }

    public TextViewWithClickableSpans(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewWithClickableSpans(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.mAccessibilityManager = (AccessibilityManager) getContext().getSystemService("accessibility");
        setOnLongClickListener(new C04251());
    }

    @TargetApi(16)
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (action != 16) {
            return super.performAccessibilityAction(action, arguments);
        }
        handleAccessibilityClick();
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean superResult = super.onTouchEvent(event);
        if (event.getAction() == 1 || !this.mAccessibilityManager.isTouchExplorationEnabled() || touchIntersectsAnyClickableSpans(event)) {
            return superResult;
        }
        handleAccessibilityClick();
        return true;
    }

    private boolean touchIntersectsAnyClickableSpans(MotionEvent event) {
        CharSequence text = getText();
        if (!(text instanceof SpannableString)) {
            return false;
        }
        SpannableString spannable = (SpannableString) text;
        int x = (((int) event.getX()) - getTotalPaddingLeft()) + getScrollX();
        int y = (((int) event.getY()) - getTotalPaddingTop()) + getScrollY();
        Layout layout = getLayout();
        int off = layout.getOffsetForHorizontal(layout.getLineForVertical(y), (float) x);
        if (((ClickableSpan[]) spannable.getSpans(off, off, ClickableSpan.class)).length > 0) {
            return true;
        }
        return false;
    }

    private ClickableSpan[] getClickableSpans() {
        CharSequence text = getText();
        if (!(text instanceof SpannableString)) {
            return null;
        }
        SpannableString spannable = (SpannableString) text;
        return (ClickableSpan[]) spannable.getSpans(0, spannable.length(), ClickableSpan.class);
    }

    private void handleAccessibilityClick() {
        ClickableSpan[] clickableSpans = getClickableSpans();
        if (clickableSpans != null && clickableSpans.length != 0) {
            if (clickableSpans.length == 1) {
                clickableSpans[0].onClick(this);
            } else {
                openDisambiguationMenu();
            }
        }
    }

    private void openDisambiguationMenu() {
        ClickableSpan[] clickableSpans = getClickableSpans();
        if (clickableSpans != null && clickableSpans.length != 0) {
            SpannableString spannable = (SpannableString) getText();
            PopupMenu popup = new PopupMenu(getContext(), this);
            Menu menu = popup.getMenu();
            for (ClickableSpan clickableSpan : clickableSpans) {
                menu.add(spannable.subSequence(spannable.getSpanStart(clickableSpan), spannable.getSpanEnd(clickableSpan))).setOnMenuItemClickListener(new C04262(clickableSpan));
            }
            popup.show();
        }
    }
}
