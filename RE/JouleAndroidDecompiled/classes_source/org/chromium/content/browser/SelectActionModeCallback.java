package org.chromium.content.browser;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Rect;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import org.chromium.content.C0317R;

public class SelectActionModeCallback implements Callback {
    protected final ActionHandler mActionHandler;
    private final Context mContext;
    private boolean mEditable;
    private boolean mIsInsertion;
    private boolean mIsPasswordType;

    public interface ActionHandler {
        void copy();

        void cut();

        boolean isIncognito();

        boolean isInsertion();

        boolean isSelectionEditable();

        boolean isSelectionPassword();

        boolean isShareAvailable();

        boolean isWebSearchAvailable();

        void onDestroyActionMode();

        void onGetContentRect(Rect rect);

        void paste();

        void search();

        void selectAll();

        void share();
    }

    public SelectActionModeCallback(Context context, ActionHandler actionHandler) {
        this.mContext = context;
        this.mActionHandler = actionHandler;
    }

    protected Context getContext() {
        return this.mContext;
    }

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.setTitle(null);
        mode.setSubtitle(null);
        this.mEditable = this.mActionHandler.isSelectionEditable();
        this.mIsPasswordType = this.mActionHandler.isSelectionPassword();
        this.mIsInsertion = this.mActionHandler.isInsertion();
        createActionMenu(mode, menu);
        return true;
    }

    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        boolean isEditableNow = this.mActionHandler.isSelectionEditable();
        boolean isPasswordNow = this.mActionHandler.isSelectionPassword();
        boolean isInsertionNow = this.mActionHandler.isInsertion();
        if (this.mEditable == isEditableNow && this.mIsPasswordType == isPasswordNow && this.mIsInsertion == isInsertionNow) {
            return false;
        }
        this.mEditable = isEditableNow;
        this.mIsPasswordType = isPasswordNow;
        this.mIsInsertion = isInsertionNow;
        menu.clear();
        createActionMenu(mode, menu);
        return true;
    }

    private void createActionMenu(ActionMode mode, Menu menu) {
        try {
            mode.getMenuInflater().inflate(C0317R.menu.select_action_menu, menu);
        } catch (NotFoundException e) {
            new MenuInflater(getContext()).inflate(C0317R.menu.select_action_menu, menu);
        }
        if (this.mIsInsertion) {
            menu.removeItem(C0317R.id.select_action_menu_select_all);
            menu.removeItem(C0317R.id.select_action_menu_cut);
            menu.removeItem(C0317R.id.select_action_menu_copy);
            menu.removeItem(C0317R.id.select_action_menu_share);
            menu.removeItem(C0317R.id.select_action_menu_web_search);
            return;
        }
        if (!(this.mEditable && canPaste())) {
            menu.removeItem(C0317R.id.select_action_menu_paste);
        }
        if (!this.mEditable) {
            menu.removeItem(C0317R.id.select_action_menu_cut);
        }
        if (this.mEditable || !this.mActionHandler.isShareAvailable()) {
            menu.removeItem(C0317R.id.select_action_menu_share);
        }
        if (this.mEditable || this.mActionHandler.isIncognito() || !this.mActionHandler.isWebSearchAvailable()) {
            menu.removeItem(C0317R.id.select_action_menu_web_search);
        }
        if (this.mIsPasswordType) {
            menu.removeItem(C0317R.id.select_action_menu_copy);
            menu.removeItem(C0317R.id.select_action_menu_cut);
        }
    }

    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int id = item.getItemId();
        if (id == C0317R.id.select_action_menu_select_all) {
            this.mActionHandler.selectAll();
        } else if (id == C0317R.id.select_action_menu_cut) {
            this.mActionHandler.cut();
            mode.finish();
        } else if (id == C0317R.id.select_action_menu_copy) {
            this.mActionHandler.copy();
            mode.finish();
        } else if (id == C0317R.id.select_action_menu_paste) {
            this.mActionHandler.paste();
            mode.finish();
        } else if (id == C0317R.id.select_action_menu_share) {
            this.mActionHandler.share();
            mode.finish();
        } else if (id != C0317R.id.select_action_menu_web_search) {
            return false;
        } else {
            this.mActionHandler.search();
            mode.finish();
        }
        return true;
    }

    public void onDestroyActionMode(ActionMode mode) {
        this.mActionHandler.onDestroyActionMode();
    }

    public void onGetContentRect(ActionMode mode, View view, Rect outRect) {
        this.mActionHandler.onGetContentRect(outRect);
    }

    private boolean canPaste() {
        return ((ClipboardManager) getContext().getSystemService("clipboard")).hasPrimaryClip();
    }
}
