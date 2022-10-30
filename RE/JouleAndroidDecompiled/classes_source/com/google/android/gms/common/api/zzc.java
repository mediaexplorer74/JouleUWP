package com.google.android.gms.common.api;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.view.PointerIconCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import org.apache.cordova.camera.CameraLauncher;

public class zzc implements Creator<Status> {
    static void zza(Status status, Parcel parcel, int i) {
        int zzav = zzb.zzav(parcel);
        zzb.zzc(parcel, 1, status.getStatusCode());
        zzb.zzc(parcel, PointerIconCompat.STYLE_DEFAULT, status.getVersionCode());
        zzb.zza(parcel, 2, status.getStatusMessage(), false);
        zzb.zza(parcel, 3, status.zzpc(), i, false);
        zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzai(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzby(i);
    }

    public Status zzai(Parcel parcel) {
        PendingIntent pendingIntent = null;
        int i = 0;
        int zzau = zza.zzau(parcel);
        String str = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzau) {
            int zzat = zza.zzat(parcel);
            switch (zza.zzca(zzat)) {
                case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                    i = zza.zzg(parcel, zzat);
                    break;
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                    str = zza.zzp(parcel, zzat);
                    break;
                case ConnectionResult.SERVICE_DISABLED /*3*/:
                    pendingIntent = (PendingIntent) zza.zza(parcel, zzat, PendingIntent.CREATOR);
                    break;
                case PointerIconCompat.STYLE_DEFAULT /*1000*/:
                    i2 = zza.zzg(parcel, zzat);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() == zzau) {
            return new Status(i2, i, str, pendingIntent);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }

    public Status[] zzby(int i) {
        return new Status[i];
    }
}
