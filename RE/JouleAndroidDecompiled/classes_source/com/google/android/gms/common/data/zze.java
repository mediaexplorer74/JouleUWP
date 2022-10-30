package com.google.android.gms.common.data;

import android.database.CursorWindow;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.view.PointerIconCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import org.apache.cordova.camera.CameraLauncher;

public class zze implements Creator<DataHolder> {
    static void zza(DataHolder dataHolder, Parcel parcel, int i) {
        int zzav = zzb.zzav(parcel);
        zzb.zza(parcel, 1, dataHolder.zzqe(), false);
        zzb.zzc(parcel, PointerIconCompat.STYLE_DEFAULT, dataHolder.getVersionCode());
        zzb.zza(parcel, 2, dataHolder.zzqf(), i, false);
        zzb.zzc(parcel, 3, dataHolder.getStatusCode());
        zzb.zza(parcel, 4, dataHolder.zzpZ(), false);
        zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzak(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzbJ(i);
    }

    public DataHolder zzak(Parcel parcel) {
        int i = 0;
        Bundle bundle = null;
        int zzau = zza.zzau(parcel);
        CursorWindow[] cursorWindowArr = null;
        String[] strArr = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzau) {
            int zzat = zza.zzat(parcel);
            switch (zza.zzca(zzat)) {
                case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                    strArr = zza.zzB(parcel, zzat);
                    break;
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                    cursorWindowArr = (CursorWindow[]) zza.zzb(parcel, zzat, CursorWindow.CREATOR);
                    break;
                case ConnectionResult.SERVICE_DISABLED /*3*/:
                    i = zza.zzg(parcel, zzat);
                    break;
                case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                    bundle = zza.zzr(parcel, zzat);
                    break;
                case PointerIconCompat.STYLE_DEFAULT /*1000*/:
                    i2 = zza.zzg(parcel, zzat);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() != zzau) {
            throw new zza.zza("Overread allowed size end=" + zzau, parcel);
        }
        DataHolder dataHolder = new DataHolder(i2, strArr, cursorWindowArr, i, bundle);
        dataHolder.zzqd();
        return dataHolder;
    }

    public DataHolder[] zzbJ(int i) {
        return new DataHolder[i];
    }
}
