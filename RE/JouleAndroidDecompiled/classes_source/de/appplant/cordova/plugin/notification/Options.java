package de.appplant.cordova.plugin.notification;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import com.adobe.phonegap.push.PushConstants;
import java.util.Date;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.chromium.ui.base.PageTransition;
import org.json.JSONException;
import org.json.JSONObject;

public class Options {
    static final String EXTRA = "NOTIFICATION_OPTIONS";
    private final AssetUtil assets;
    private final Context context;
    private long interval;
    private JSONObject options;

    public Options(Context context) {
        this.options = new JSONObject();
        this.interval = 0;
        this.context = context;
        this.assets = AssetUtil.getInstance(context);
    }

    public Options parse(JSONObject options) {
        this.options = options;
        parseInterval();
        parseAssets();
        return this;
    }

    private void parseInterval() {
        String every = this.options.optString("every").toLowerCase();
        if (every.isEmpty()) {
            this.interval = 0;
        } else if (every.equals("second")) {
            this.interval = 1000;
        } else if (every.equals("minute")) {
            this.interval = 60000;
        } else if (every.equals("hour")) {
            this.interval = 3600000;
        } else if (every.equals("day")) {
            this.interval = 86400000;
        } else if (every.equals("week")) {
            this.interval = 604800000;
        } else if (every.equals("month")) {
            this.interval = 2678400000L;
        } else if (every.equals("year")) {
            this.interval = 31536000000L;
        } else {
            try {
                this.interval = (long) (Integer.parseInt(every) * 60000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void parseAssets() {
        if (!this.options.has("iconUri")) {
            Uri iconUri = this.assets.parse(this.options.optString(PushConstants.ICON, PushConstants.ICON));
            Uri soundUri = this.assets.parseSound(this.options.optString(PushConstants.SOUND, null));
            try {
                this.options.put("iconUri", iconUri.toString());
                this.options.put("soundUri", soundUri.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public Context getContext() {
        return this.context;
    }

    JSONObject getDict() {
        return this.options;
    }

    public String getText() {
        return this.options.optString(PushConstants.STYLE_TEXT, CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
    }

    public long getRepeatInterval() {
        return this.interval;
    }

    public int getBadgeNumber() {
        return this.options.optInt(PushConstants.BADGE, 0);
    }

    public Boolean isOngoing() {
        return Boolean.valueOf(this.options.optBoolean("ongoing", false));
    }

    public Boolean isAutoClear() {
        return Boolean.valueOf(this.options.optBoolean("autoClear", false));
    }

    public Integer getId() {
        return Integer.valueOf(this.options.optInt("id", 0));
    }

    public String getIdStr() {
        return getId().toString();
    }

    public Date getTriggerDate() {
        return new Date(getTriggerTime());
    }

    public long getTriggerTime() {
        return this.options.optLong("at", 0) * 1000;
    }

    public String getTitle() {
        String title = this.options.optString(PushConstants.TITLE, CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
        if (title.isEmpty()) {
            return this.context.getApplicationInfo().loadLabel(this.context.getPackageManager()).toString();
        }
        return title;
    }

    public int getLedColor() {
        return Integer.parseInt(this.options.optString("led", "000000"), 16) - PageTransition.FORWARD_BACK;
    }

    public Uri getSoundUri() {
        Uri uri = null;
        try {
            uri = Uri.parse(this.options.optString("soundUri"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    public Bitmap getIconBitmap() {
        try {
            return this.assets.getIconFromUri(Uri.parse(this.options.optString("iconUri")));
        } catch (Exception e) {
            return this.assets.getIconFromDrawable(this.options.optString(PushConstants.ICON, PushConstants.ICON));
        }
    }

    public int getSmallIcon() {
        int resId = this.assets.getResIdForDrawable(this.options.optString("smallIcon", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE));
        if (resId == 0) {
            return 17301656;
        }
        return resId;
    }

    public String toString() {
        return this.options.toString();
    }
}
