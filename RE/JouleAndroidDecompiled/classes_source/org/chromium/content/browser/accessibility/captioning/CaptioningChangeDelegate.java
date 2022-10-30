package org.chromium.content.browser.accessibility.captioning;

import android.graphics.Color;
import android.graphics.Typeface;
import com.google.android.gms.common.ConnectionResult;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.base.VisibleForTesting;
import org.chromium.content.browser.accessibility.captioning.SystemCaptioningBridge.SystemCaptioningBridgeListener;

public class CaptioningChangeDelegate {
    @VisibleForTesting
    public static final String DEFAULT_CAPTIONING_PREF_VALUE = "";
    private static final String FONT_STYLE_ITALIC = "italic";
    private final Map<SystemCaptioningBridgeListener, Boolean> mListeners;
    private String mTextTrackBackgroundColor;
    private String mTextTrackFontFamily;
    private String mTextTrackFontStyle;
    private String mTextTrackFontVariant;
    private String mTextTrackTextColor;
    private String mTextTrackTextShadow;
    private String mTextTrackTextSize;
    private boolean mTextTracksEnabled;

    public enum ClosedCaptionEdgeAttribute {
        NONE(CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE),
        OUTLINE("%2$s %2$s 0 %1$s, -%2$s -%2$s 0 %1$s, %2$s -%2$s 0 %1$s, -%2$s %2$s 0 %1$s"),
        DROP_SHADOW("%1$s %2$s %2$s 0.1em"),
        RAISED("-%2$s -%2$s 0 %1$s"),
        DEPRESSED("%2$s %2$s 0 %1$s");
        
        private static String sDefaultEdgeColor;
        private static String sEdgeColor;
        private static String sShadowOffset;
        private final String mTextShadow;

        static {
            sDefaultEdgeColor = "silver";
            sShadowOffset = "0.05em";
        }

        private ClosedCaptionEdgeAttribute(String textShadow) {
            this.mTextShadow = textShadow;
        }

        public static ClosedCaptionEdgeAttribute fromSystemEdgeAttribute(Integer type, String color) {
            if (type == null) {
                return NONE;
            }
            if (color == null || color.isEmpty()) {
                sEdgeColor = sDefaultEdgeColor;
            } else {
                sEdgeColor = color;
            }
            switch (type.intValue()) {
                case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                    return OUTLINE;
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                    return DROP_SHADOW;
                case ConnectionResult.SERVICE_DISABLED /*3*/:
                    return RAISED;
                case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                    return DEPRESSED;
                default:
                    return NONE;
            }
        }

        public static void setShadowOffset(String shadowOffset) {
            sShadowOffset = shadowOffset;
        }

        public static void setDefaultEdgeColor(String color) {
            sDefaultEdgeColor = color;
        }

        public String getTextShadow() {
            return String.format(this.mTextShadow, new Object[]{sEdgeColor, sShadowOffset});
        }
    }

    public enum ClosedCaptionFont {
        DEFAULT(CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE),
        SANS_SERIF("sans-serif"),
        SANS_SERIF_CONDENSED("sans-serif-condensed"),
        SANS_SERIF_MONOSPACE("sans-serif-monospace"),
        SERIF("serif"),
        SERIF_MONOSPACE("serif-monospace"),
        CASUAL("casual"),
        CURSIVE("cursive"),
        SANS_SERIF_SMALLCAPS("sans-serif-smallcaps"),
        MONOSPACE("monospace");
        
        private final String mFontFamily;

        private ClosedCaptionFont(String fontFamily) {
            this.mFontFamily = fontFamily;
        }

        public static ClosedCaptionFont fromSystemFont(Typeface typeFace) {
            if (typeFace == null) {
                return DEFAULT;
            }
            for (ClosedCaptionFont font : values()) {
                if (belongsToFontFamily(typeFace, font)) {
                    return font;
                }
            }
            return DEFAULT;
        }

        private static boolean belongsToFontFamily(Typeface typeFace, ClosedCaptionFont font) {
            return Typeface.create(font.getFontFamily(), typeFace.getStyle()).equals(typeFace);
        }

        public String getFontFamily() {
            return this.mFontFamily;
        }
    }

    public void onEnabledChanged(boolean enabled) {
        this.mTextTracksEnabled = enabled;
        notifySettingsChanged();
    }

    public void onFontScaleChanged(float fontScale) {
        this.mTextTrackTextSize = androidFontScaleToPercentage(fontScale);
        notifySettingsChanged();
    }

    public void onLocaleChanged(Locale locale) {
    }

    public void onUserStyleChanged(CaptioningStyle userStyle) {
        this.mTextTrackTextColor = androidColorToCssColor(userStyle.getForegroundColor());
        this.mTextTrackBackgroundColor = androidColorToCssColor(userStyle.getBackgroundColor());
        this.mTextTrackTextShadow = ClosedCaptionEdgeAttribute.fromSystemEdgeAttribute(userStyle.getEdgeType(), androidColorToCssColor(userStyle.getEdgeColor())).getTextShadow();
        Typeface typeFace = userStyle.getTypeface();
        this.mTextTrackFontFamily = ClosedCaptionFont.fromSystemFont(typeFace).getFontFamily();
        if (typeFace == null || !typeFace.isItalic()) {
            this.mTextTrackFontStyle = DEFAULT_CAPTIONING_PREF_VALUE;
        } else {
            this.mTextTrackFontStyle = FONT_STYLE_ITALIC;
        }
        this.mTextTrackFontVariant = DEFAULT_CAPTIONING_PREF_VALUE;
        notifySettingsChanged();
    }

    public CaptioningChangeDelegate() {
        this.mListeners = new WeakHashMap();
    }

    public static String androidColorToCssColor(Integer color) {
        if (color == null) {
            return DEFAULT_CAPTIONING_PREF_VALUE;
        }
        String alpha = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US)).format(((double) Color.alpha(color.intValue())) / 255.0d);
        return String.format("rgba(%s, %s, %s, %s)", new Object[]{Integer.valueOf(Color.red(color.intValue())), Integer.valueOf(Color.green(color.intValue())), Integer.valueOf(Color.blue(color.intValue())), alpha});
    }

    public static String androidFontScaleToPercentage(float fontScale) {
        return new DecimalFormat("#%", new DecimalFormatSymbols(Locale.US)).format((double) fontScale);
    }

    private void notifySettingsChanged() {
        for (SystemCaptioningBridgeListener listener : this.mListeners.keySet()) {
            notifyListener(listener);
        }
    }

    public void notifyListener(SystemCaptioningBridgeListener listener) {
        if (this.mTextTracksEnabled) {
            listener.onSystemCaptioningChanged(new TextTrackSettings(this.mTextTracksEnabled, this.mTextTrackBackgroundColor, this.mTextTrackFontFamily, this.mTextTrackFontStyle, this.mTextTrackFontVariant, this.mTextTrackTextColor, this.mTextTrackTextShadow, this.mTextTrackTextSize));
        } else {
            listener.onSystemCaptioningChanged(new TextTrackSettings());
        }
    }

    public void addListener(SystemCaptioningBridgeListener listener) {
        this.mListeners.put(listener, null);
    }

    public void removeListener(SystemCaptioningBridgeListener listener) {
        this.mListeners.remove(listener);
    }

    public boolean hasActiveListener() {
        return !this.mListeners.isEmpty();
    }
}
