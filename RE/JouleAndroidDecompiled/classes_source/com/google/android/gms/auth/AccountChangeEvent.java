package com.google.android.gms.auth;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.common.internal.zzw;
import com.google.android.gms.common.internal.zzx;
import org.apache.cordova.camera.CameraLauncher;

public class AccountChangeEvent implements SafeParcelable {
    public static final Creator<AccountChangeEvent> CREATOR;
    final int mVersion;
    final long zzUZ;
    final String zzVa;
    final int zzVb;
    final int zzVc;
    final String zzVd;

    static {
        CREATOR = new zza();
    }

    AccountChangeEvent(int version, long id, String accountName, int changeType, int eventIndex, String changeData) {
        this.mVersion = version;
        this.zzUZ = id;
        this.zzVa = (String) zzx.zzz(accountName);
        this.zzVb = changeType;
        this.zzVc = eventIndex;
        this.zzVd = changeData;
    }

    public AccountChangeEvent(long id, String accountName, int changeType, int eventIndex, String changeData) {
        this.mVersion = 1;
        this.zzUZ = id;
        this.zzVa = (String) zzx.zzz(accountName);
        this.zzVb = changeType;
        this.zzVc = eventIndex;
        this.zzVd = changeData;
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        if (that == this) {
            return true;
        }
        if (!(that instanceof AccountChangeEvent)) {
            return false;
        }
        AccountChangeEvent accountChangeEvent = (AccountChangeEvent) that;
        return this.mVersion == accountChangeEvent.mVersion && this.zzUZ == accountChangeEvent.zzUZ && zzw.equal(this.zzVa, accountChangeEvent.zzVa) && this.zzVb == accountChangeEvent.zzVb && this.zzVc == accountChangeEvent.zzVc && zzw.equal(this.zzVd, accountChangeEvent.zzVd);
    }

    public String getAccountName() {
        return this.zzVa;
    }

    public String getChangeData() {
        return this.zzVd;
    }

    public int getChangeType() {
        return this.zzVb;
    }

    public int getEventIndex() {
        return this.zzVc;
    }

    public int hashCode() {
        return zzw.hashCode(Integer.valueOf(this.mVersion), Long.valueOf(this.zzUZ), this.zzVa, Integer.valueOf(this.zzVb), Integer.valueOf(this.zzVc), this.zzVd);
    }

    public String toString() {
        String str = "UNKNOWN";
        switch (this.zzVb) {
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                str = "ADDED";
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                str = "REMOVED";
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                str = "RENAMED_FROM";
                break;
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                str = "RENAMED_TO";
                break;
        }
        return "AccountChangeEvent {accountName = " + this.zzVa + ", changeType = " + str + ", changeData = " + this.zzVd + ", eventIndex = " + this.zzVc + "}";
    }

    public void writeToParcel(Parcel dest, int flags) {
        zza.zza(this, dest, flags);
    }
}
