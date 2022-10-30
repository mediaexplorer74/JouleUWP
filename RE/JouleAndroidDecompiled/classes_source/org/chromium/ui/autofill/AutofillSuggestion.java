package org.chromium.ui.autofill;

import org.chromium.ui.DropdownItem;

public class AutofillSuggestion implements DropdownItem {
    private final boolean mDeletable;
    private final int mIconId;
    private final String mLabel;
    private final String mSublabel;
    private final int mSuggestionId;

    public AutofillSuggestion(String name, String label, int iconId, int suggestionId, boolean deletable) {
        this.mLabel = name;
        this.mSublabel = label;
        this.mIconId = iconId;
        this.mSuggestionId = suggestionId;
        this.mDeletable = deletable;
    }

    public String getLabel() {
        return this.mLabel;
    }

    public String getSublabel() {
        return this.mSublabel;
    }

    public int getIconId() {
        return this.mIconId;
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean isGroupHeader() {
        return false;
    }

    public int getSuggestionId() {
        return this.mSuggestionId;
    }

    public boolean isDeletable() {
        return this.mDeletable;
    }
}
