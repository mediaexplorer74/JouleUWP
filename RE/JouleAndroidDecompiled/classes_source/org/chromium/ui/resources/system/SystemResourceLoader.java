package org.chromium.ui.resources.system;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.support.v4.media.TransportMediator;
import com.google.android.gms.common.ConnectionResult;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.ui.gfx.DeviceDisplayInfo;
import org.chromium.ui.resources.Resource;
import org.chromium.ui.resources.ResourceLoader.ResourceLoaderCallback;
import org.chromium.ui.resources.async.AsyncPreloadResourceLoader;
import org.chromium.ui.resources.async.AsyncPreloadResourceLoader.ResourceCreator;
import org.chromium.ui.resources.statics.StaticResource;

public class SystemResourceLoader extends AsyncPreloadResourceLoader {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final float COS_PI_OVER_6 = 0.866f;
    private static final float SIN_PI_OVER_6 = 0.5f;

    /* renamed from: org.chromium.ui.resources.system.SystemResourceLoader.1 */
    class C06421 implements ResourceCreator {
        final /* synthetic */ Context val$context;

        C06421(Context context) {
            this.val$context = context;
        }

        public Resource create(int resId) {
            return SystemResourceLoader.createResource(this.val$context, resId);
        }
    }

    static {
        $assertionsDisabled = !SystemResourceLoader.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public SystemResourceLoader(int resourceType, ResourceLoaderCallback callback, Context context) {
        super(resourceType, callback, new C06421(context));
    }

    private static Resource createResource(Context context, int resId) {
        switch (resId) {
            case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                return StaticResource.create(Resources.getSystem(), getResourceId("android:drawable/overscroll_edge"), TransportMediator.FLAG_KEY_MEDIA_NEXT, 12);
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                return StaticResource.create(Resources.getSystem(), getResourceId("android:drawable/overscroll_glow"), TransportMediator.FLAG_KEY_MEDIA_NEXT, 64);
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                return createOverscrollGlowLBitmap(context);
            default:
                if ($assertionsDisabled) {
                    return null;
                }
                throw new AssertionError();
        }
    }

    private static Resource createOverscrollGlowLBitmap(Context context) {
        DeviceDisplayInfo displayInfo = DeviceDisplayInfo.create(context);
        float arcWidth = (((float) Math.min(displayInfo.getPhysicalDisplayWidth() != 0 ? displayInfo.getPhysicalDisplayWidth() : displayInfo.getDisplayWidth(), displayInfo.getPhysicalDisplayHeight() != 0 ? displayInfo.getPhysicalDisplayHeight() : displayInfo.getDisplayHeight())) * SIN_PI_OVER_6) / SIN_PI_OVER_6;
        float y = COS_PI_OVER_6 * arcWidth;
        float height = arcWidth - y;
        float arcRectX = (-arcWidth) / 2.0f;
        float arcRectY = (-arcWidth) - y;
        RectF arcRect = new RectF(arcRectX, arcRectY, arcRectX + (arcWidth * 2.0f), arcRectY + (arcWidth * 2.0f));
        Paint arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setAlpha(187);
        arcPaint.setStyle(Style.FILL);
        Bitmap bitmap = Bitmap.createBitmap((int) arcWidth, (int) height, Config.ALPHA_8);
        new Canvas(bitmap).drawArc(arcRect, 45.0f, 90.0f, true, arcPaint);
        return new StaticResource(bitmap);
    }

    private static int getResourceId(String name) {
        return Resources.getSystem().getIdentifier(name, null, null);
    }
}
