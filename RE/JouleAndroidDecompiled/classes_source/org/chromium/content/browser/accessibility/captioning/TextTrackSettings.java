package org.chromium.content.browser.accessibility.captioning;

import android.annotation.TargetApi;
import java.util.Objects;

@TargetApi(19)
public final class TextTrackSettings {
    private static final String DEFAULT_VALUE = "";
    private String mTextTrackBackgroundColor;
    private String mTextTrackFontFamily;
    private String mTextTrackFontStyle;
    private String mTextTrackFontVariant;
    private String mTextTrackTextColor;
    private String mTextTrackTextShadow;
    private String mTextTrackTextSize;
    private boolean mTextTracksEnabled;

    public TextTrackSettings(boolean textTracksEnabled, String textTrackBackgroundColor, String textTrackFontFamily, String textTrackFontStyle, String textTrackFontVariant, String textTrackTextColor, String textTrackTextShadow, String textTrackTextSize) {
        this.mTextTracksEnabled = textTracksEnabled;
        this.mTextTrackBackgroundColor = textTrackBackgroundColor;
        this.mTextTrackFontFamily = textTrackFontFamily;
        this.mTextTrackFontStyle = textTrackFontStyle;
        this.mTextTrackFontVariant = textTrackFontVariant;
        this.mTextTrackTextColor = textTrackTextColor;
        this.mTextTrackTextShadow = textTrackTextShadow;
        this.mTextTrackTextSize = textTrackTextSize;
    }

    public boolean getTextTracksEnabled() {
        return this.mTextTracksEnabled;
    }

    public String getTextTrackBackgroundColor() {
        return Objects.toString(this.mTextTrackBackgroundColor, DEFAULT_VALUE);
    }

    public String getTextTrackFontFamily() {
        return Objects.toString(this.mTextTrackFontFamily, DEFAULT_VALUE);
    }

    public String getTextTrackFontStyle() {
        return Objects.toString(this.mTextTrackFontStyle, DEFAULT_VALUE);
    }

    public String getTextTrackFontVariant() {
        return Objects.toString(this.mTextTrackFontVariant, DEFAULT_VALUE);
    }

    public String getTextTrackTextColor() {
        return Objects.toString(this.mTextTrackTextColor, DEFAULT_VALUE);
    }

    public String getTextTrackTextShadow() {
        return Objects.toString(this.mTextTrackTextShadow, DEFAULT_VALUE);
    }

    public String getTextTrackTextSize() {
        return Objects.toString(this.mTextTrackTextSize, DEFAULT_VALUE);
    }
}
