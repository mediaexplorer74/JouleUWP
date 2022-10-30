package com.chefsteps;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher;
import com.dd.crop.TextureVideoView;
import com.dd.crop.TextureVideoView.MediaPlayerListener;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.chromium.blink_public.web.WebInputEventModifier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CSTJoulePlugin extends CordovaPlugin {
    private static final String TAG = "CSTJoulePlugin";
    private boolean autoplayGalleryOnTransition;
    private CallbackContext cbContext;
    private Activity ctx;
    private ImageView currentGalleryPreview;
    private TextureVideoView currentVideoGallery;
    private FrameLayout frameLayout;
    private boolean loopGallery;
    private boolean shouldBePlaying;
    private ViewSwitcher switcher;
    private Direction transitionDirection;
    private int viewWidth;
    private FrameLayout webParent;

    /* renamed from: com.chefsteps.CSTJoulePlugin.1 */
    class C01271 implements Runnable {
        final /* synthetic */ LayoutParams val$params;
        final /* synthetic */ ViewGroup val$root;
        final /* synthetic */ View val$v;

        C01271(ViewGroup viewGroup, View view, LayoutParams layoutParams) {
            this.val$root = viewGroup;
            this.val$v = view;
            this.val$params = layoutParams;
        }

        public void run() {
            CSTJoulePlugin.this.switcher = new ViewSwitcher(CSTJoulePlugin.this.ctx);
            CSTJoulePlugin.this.switcher.setLayoutParams(new LayoutParams(CSTJoulePlugin.this.viewWidth, -1));
            CSTJoulePlugin.this.currentGalleryPreview = new ImageView(CSTJoulePlugin.this.ctx);
            CSTJoulePlugin.this.currentGalleryPreview.setScaleType(ScaleType.CENTER_CROP);
            CSTJoulePlugin.this.currentVideoGallery = new TextureVideoView(CSTJoulePlugin.this.ctx);
            CSTJoulePlugin.this.currentVideoGallery.setListener(new VideoListener(null));
            CSTJoulePlugin.this.currentVideoGallery.setScaleType(TextureVideoView.ScaleType.CENTER_CROP);
            CSTJoulePlugin.this.switcher.addView(CSTJoulePlugin.this.currentGalleryPreview);
            CSTJoulePlugin.this.switcher.addView(CSTJoulePlugin.this.currentVideoGallery);
            CSTJoulePlugin.this.switcher.reset();
            this.val$root.removeView(this.val$v);
            CSTJoulePlugin.this.webParent = new FrameLayout(CSTJoulePlugin.this.ctx);
            CSTJoulePlugin.this.webParent.addView(this.val$v);
            this.val$root.setSystemUiVisibility(WebInputEventModifier.NumLockOn);
            CSTJoulePlugin.this.frameLayout = new FrameLayout(CSTJoulePlugin.this.ctx);
            CSTJoulePlugin.this.frameLayout.setLayoutParams(this.val$params);
            CSTJoulePlugin.this.frameLayout.addView(CSTJoulePlugin.this.switcher);
            CSTJoulePlugin.this.frameLayout.addView(CSTJoulePlugin.this.webParent);
            this.val$root.addView(CSTJoulePlugin.this.frameLayout);
            this.val$v.requestFocus();
        }
    }

    /* renamed from: com.chefsteps.CSTJoulePlugin.2 */
    class C01282 implements Runnable {
        final /* synthetic */ CallbackContext val$callbackContext;
        final /* synthetic */ JSONObject val$options;

        C01282(JSONObject jSONObject, CallbackContext callbackContext) {
            this.val$options = jSONObject;
            this.val$callbackContext = callbackContext;
        }

        public void run() {
            CSTJoulePlugin.this.playBackgroundVideo(this.val$options, this.val$callbackContext);
        }
    }

    /* renamed from: com.chefsteps.CSTJoulePlugin.3 */
    class C01293 implements Runnable {
        C01293() {
        }

        public void run() {
            Log.v(CSTJoulePlugin.TAG, "Setting background to transparent");
            CSTJoulePlugin.this.webView.getView().setBackgroundColor(0);
            CSTJoulePlugin.this.webView.getView().setLayerType(1, null);
        }
    }

    /* renamed from: com.chefsteps.CSTJoulePlugin.4 */
    class C01304 implements Runnable {
        final /* synthetic */ CallbackContext val$callbackContext;
        final /* synthetic */ Uri val$thumbnailUri;
        final /* synthetic */ String val$videoUri;

        C01304(Uri uri, String str, CallbackContext callbackContext) {
            this.val$thumbnailUri = uri;
            this.val$videoUri = str;
            this.val$callbackContext = callbackContext;
        }

        public void run() {
            CSTJoulePlugin.this.setImage(this.val$thumbnailUri);
            CSTJoulePlugin.this.setUpVideo(this.val$videoUri, CSTJoulePlugin.this.loopGallery, CSTJoulePlugin.this.autoplayGalleryOnTransition, this.val$callbackContext);
        }
    }

    private enum Direction {
        FORWARD,
        BACK
    }

    private class VideoListener implements MediaPlayerListener {
        private static final String TAG = "VideoListener";

        private VideoListener() {
        }

        public void onVideoPrepared() {
            Log.v(TAG, "Video is prepared");
            if (CSTJoulePlugin.this.shouldBePlaying) {
                Log.v(TAG, "Showing the video");
                CSTJoulePlugin.this.switcher.setDisplayedChild(1);
                CSTJoulePlugin.this.currentVideoGallery.play();
                CSTJoulePlugin.this.shouldBePlaying = false;
                CSTJoulePlugin.this.cbContext.success("Playing video");
            }
        }

        public void onVideoEnd() {
        }
    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        Log.v(TAG, "initialization");
        super.initialize(cordova, webView);
        this.ctx = cordova.getActivity();
        ViewGroup root = (ViewGroup) webView.getView().getParent();
        View v = webView.getView();
        LayoutParams params = new LayoutParams(-1, -1, 8);
        this.viewWidth = v.getWidth();
        this.ctx.runOnUiThread(new C01271(root, v, params));
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.v(TAG, "Executing action: " + action + " with " + args);
        if (action.equals("initializeWebView")) {
            return initializeWebView(callbackContext);
        }
        if (!action.equals("playBackgroundVideo")) {
            return false;
        }
        if (args == null || args.length() < 1) {
            callbackContext.error("Must provide options string!");
            return false;
        }
        this.cordova.getThreadPool().execute(new C01282(args.getJSONObject(0), callbackContext));
        return true;
    }

    private boolean initializeWebView(CallbackContext callbackContext) {
        if (this.webView == null || this.webView.getView() == null) {
            callbackContext.error("Failed to set the background to fully transparent");
            return false;
        }
        this.ctx.runOnUiThread(new C01293());
        callbackContext.success("Set the background to fully transparent");
        return true;
    }

    private boolean playBackgroundVideo(JSONObject options, CallbackContext callbackContext) {
        this.autoplayGalleryOnTransition = false;
        this.loopGallery = false;
        if (options != null) {
            try {
                if (options.has("autoplay")) {
                    this.autoplayGalleryOnTransition = options.getBoolean("autoplay");
                    Log.v(TAG, "Read option to set autoplay to " + this.autoplayGalleryOnTransition);
                }
            } catch (JSONException e) {
                callbackContext.error("Error unwrapping transition options: " + e.getMessage());
                return false;
            }
        }
        if (options != null && options.has("loop")) {
            this.loopGallery = options.getBoolean("loop");
            Log.v(TAG, "Read option to set loop behavior to " + this.loopGallery);
        }
        if (options != null && options.has("gallery")) {
            JSONObject videoObj = options.getJSONObject("gallery");
            if (videoObj != null) {
                this.ctx.runOnUiThread(new C01304(Uri.parse(videoObj.getString("thumbnail")), videoObj.getString("video"), callbackContext));
            } else {
                Log.e(TAG, "Transitioning to a view with gallery but no videos");
                callbackContext.error("Transitioning to a view with a gallery but no videos");
                return false;
            }
        }
        return true;
    }

    private void setImage(Uri uri) {
        Log.v(TAG, "Setting image with " + uri);
        this.currentGalleryPreview.setImageURI(uri);
        this.currentVideoGallery.pause();
        Log.v(TAG, "Showing the image");
        this.switcher.setDisplayedChild(0);
    }

    private void setUpVideo(String videoUri, boolean loopGallery, boolean autoPlayGalleryOnTransition, CallbackContext callbackContext) {
        Log.v(TAG, "Setting up video with " + videoUri);
        this.currentVideoGallery.setDataSource(videoUri);
        this.currentVideoGallery.setLooping(loopGallery);
        this.shouldBePlaying = autoPlayGalleryOnTransition;
        this.cbContext = callbackContext;
    }

    private Direction translateDirection(String arg) {
        if (arg != null) {
            if (arg.equals("forward")) {
                return Direction.FORWARD;
            }
            if (arg.equals("back")) {
                return Direction.BACK;
            }
        }
        return null;
    }
}
