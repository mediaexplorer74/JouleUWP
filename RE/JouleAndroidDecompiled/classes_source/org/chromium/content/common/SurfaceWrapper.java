package org.chromium.content.common;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.view.Surface;

public class SurfaceWrapper implements Parcelable {
    public static final Creator<SurfaceWrapper> CREATOR;
    private final Surface mSurface;

    /* renamed from: org.chromium.content.common.SurfaceWrapper.1 */
    static class C03731 implements Creator<SurfaceWrapper> {
        C03731() {
        }

        public SurfaceWrapper createFromParcel(Parcel in) {
            return new SurfaceWrapper((Surface) Surface.CREATOR.createFromParcel(in));
        }

        public SurfaceWrapper[] newArray(int size) {
            return new SurfaceWrapper[size];
        }
    }

    public SurfaceWrapper(Surface surface) {
        this.mSurface = surface;
    }

    public Surface getSurface() {
        return this.mSurface;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        this.mSurface.writeToParcel(out, 0);
    }

    static {
        CREATOR = new C03731();
    }
}
