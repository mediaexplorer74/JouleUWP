package org.chromium.content.browser.input;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.res.TypedArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import java.util.List;
import org.chromium.content.C0317R;
import org.chromium.content.browser.ContentViewCore;

public class SelectPopupDialog implements SelectPopup {
    private static final int[] SELECT_DIALOG_ATTRS;
    private final ContentViewCore mContentViewCore;
    private final Context mContext;
    private final AlertDialog mListBoxPopup;
    private boolean mSelectionNotified;

    /* renamed from: org.chromium.content.browser.input.SelectPopupDialog.1 */
    class C03641 implements OnClickListener {
        final /* synthetic */ ListView val$listView;

        C03641(ListView listView) {
            this.val$listView = listView;
        }

        public void onClick(DialogInterface dialog, int which) {
            SelectPopupDialog.this.notifySelection(SelectPopupDialog.getSelectedIndices(this.val$listView));
        }
    }

    /* renamed from: org.chromium.content.browser.input.SelectPopupDialog.2 */
    class C03652 implements OnClickListener {
        C03652() {
        }

        public void onClick(DialogInterface dialog, int which) {
            SelectPopupDialog.this.notifySelection(null);
        }
    }

    /* renamed from: org.chromium.content.browser.input.SelectPopupDialog.3 */
    class C03663 implements OnItemClickListener {
        final /* synthetic */ ListView val$listView;

        C03663(ListView listView) {
            this.val$listView = listView;
        }

        public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
            SelectPopupDialog.this.notifySelection(SelectPopupDialog.getSelectedIndices(this.val$listView));
            SelectPopupDialog.this.mListBoxPopup.dismiss();
        }
    }

    /* renamed from: org.chromium.content.browser.input.SelectPopupDialog.4 */
    class C03674 implements OnCancelListener {
        C03674() {
        }

        public void onCancel(DialogInterface dialog) {
            SelectPopupDialog.this.notifySelection(null);
        }
    }

    static {
        SELECT_DIALOG_ATTRS = new int[]{C0317R.attr.select_dialog_multichoice, C0317R.attr.select_dialog_singlechoice};
    }

    public SelectPopupDialog(ContentViewCore contentViewCore, List<SelectPopupItem> items, boolean multiple, int[] selected) {
        this.mContentViewCore = contentViewCore;
        this.mContext = this.mContentViewCore.getContext();
        ListView listView = new ListView(this.mContext);
        listView.setCacheColorHint(0);
        Builder b = new Builder(this.mContext).setView(listView).setCancelable(true).setInverseBackgroundForced(true);
        if (multiple) {
            b.setPositiveButton(17039370, new C03641(listView));
            b.setNegativeButton(17039360, new C03652());
        }
        this.mListBoxPopup = b.create();
        listView.setAdapter(new SelectPopupAdapter(this.mContext, getSelectDialogLayout(multiple), items));
        listView.setFocusableInTouchMode(true);
        if (multiple) {
            listView.setChoiceMode(2);
            for (int itemChecked : selected) {
                listView.setItemChecked(itemChecked, true);
            }
        } else {
            listView.setChoiceMode(1);
            listView.setOnItemClickListener(new C03663(listView));
            if (selected.length > 0) {
                listView.setSelection(selected[0]);
                listView.setItemChecked(selected[0], true);
            }
        }
        this.mListBoxPopup.setOnCancelListener(new C03674());
    }

    private int getSelectDialogLayout(boolean isMultiChoice) {
        TypedArray styledAttributes = this.mContext.obtainStyledAttributes(C0317R.style.SelectPopupDialog, SELECT_DIALOG_ATTRS);
        int resourceId = styledAttributes.getResourceId(isMultiChoice ? 0 : 1, 0);
        styledAttributes.recycle();
        return resourceId;
    }

    private static int[] getSelectedIndices(ListView listView) {
        int i;
        SparseBooleanArray sparseArray = listView.getCheckedItemPositions();
        int selectedCount = 0;
        for (i = 0; i < sparseArray.size(); i++) {
            if (sparseArray.valueAt(i)) {
                selectedCount++;
            }
        }
        int[] indices = new int[selectedCount];
        int j = 0;
        for (i = 0; i < sparseArray.size(); i++) {
            if (sparseArray.valueAt(i)) {
                int j2 = j + 1;
                indices[j] = sparseArray.keyAt(i);
                j = j2;
            }
        }
        return indices;
    }

    private void notifySelection(int[] indicies) {
        if (!this.mSelectionNotified) {
            this.mContentViewCore.selectPopupMenuItems(indicies);
            this.mSelectionNotified = true;
        }
    }

    public void show() {
        this.mListBoxPopup.show();
    }

    public void hide() {
        this.mListBoxPopup.cancel();
        notifySelection(null);
    }
}
