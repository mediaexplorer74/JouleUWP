package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import org.apache.cordova.camera.CameraLauncher;

public class zzk implements Creator<EventParcel> {
    static void zza(EventParcel eventParcel, Parcel parcel, int i) {
        int zzav = zzb.zzav(parcel);
        zzb.zzc(parcel, 1, eventParcel.versionCode);
        zzb.zza(parcel, 2, eventParcel.name, false);
        zzb.zza(parcel, 3, eventParcel.zzaVV, i, false);
        zzb.zza(parcel, 4, eventParcel.zzaVW, false);
        zzb.zza(parcel, 5, eventParcel.zzaVX);
        zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzfN(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zziJ(i);
    }

    public EventParcel zzfN(Parcel parcel) {
        String str = null;
        int zzau = zza.zzau(parcel);
        int i = 0;
        long j = 0;
        EventParams eventParams = null;
        String str2 = null;
        while (parcel.dataPosition() < zzau) {
            int zzat = zza.zzat(parcel);
            switch (zza.zzca(zzat)) {
                case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                    i = zza.zzg(parcel, zzat);
                    break;
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                    str2 = zza.zzp(parcel, zzat);
                    break;
                case ConnectionResult.SERVICE_DISABLED /*3*/:
                    eventParams = (EventParams) zza.zza(parcel, zzat, EventParams.CREATOR);
                    break;
                case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                    str = zza.zzp(parcel, zzat);
                    break;
                case ConnectionResult.INVALID_ACCOUNT /*5*/:
                    j = zza.zzi(parcel, zzat);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() == zzau) {
            return new EventParcel(i, str2, eventParams, str, j);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }

    public EventParcel[] zziJ(int i) {
        return new EventParcel[i];
    }
}
