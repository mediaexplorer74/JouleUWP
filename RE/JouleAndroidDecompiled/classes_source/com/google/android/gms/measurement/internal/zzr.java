package com.google.android.gms.measurement.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzx;

class zzr extends BroadcastReceiver {
    static final String zzSZ;
    private boolean zzTa;
    private boolean zzTb;
    private final zzw zzaTV;

    /* renamed from: com.google.android.gms.measurement.internal.zzr.1 */
    class C01961 implements Runnable {
        final /* synthetic */ boolean zzaWW;
        final /* synthetic */ zzr zzaWX;

        C01961(zzr com_google_android_gms_measurement_internal_zzr, boolean z) {
            this.zzaWX = com_google_android_gms_measurement_internal_zzr;
            this.zzaWW = z;
        }

        public void run() {
            this.zzaWX.zzaTV.zzJ(this.zzaWW);
        }
    }

    static {
        zzSZ = zzr.class.getName();
    }

    zzr(zzw com_google_android_gms_measurement_internal_zzw) {
        zzx.zzz(com_google_android_gms_measurement_internal_zzw);
        this.zzaTV = com_google_android_gms_measurement_internal_zzw;
    }

    private Context getContext() {
        return this.zzaTV.getContext();
    }

    private zzp zzAo() {
        return this.zzaTV.zzAo();
    }

    @WorkerThread
    public boolean isRegistered() {
        this.zzaTV.zzjk();
        return this.zzTa;
    }

    @MainThread
    public void onReceive(Context context, Intent intent) {
        this.zzaTV.zzjv();
        String action = intent.getAction();
        zzAo().zzCK().zzj("NetworkBroadcastReceiver received action", action);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            boolean zzlB = this.zzaTV.zzCW().zzlB();
            if (this.zzTb != zzlB) {
                this.zzTb = zzlB;
                this.zzaTV.zzCn().zzg(new C01961(this, zzlB));
                return;
            }
            return;
        }
        zzAo().zzCF().zzj("NetworkBroadcastReceiver received unknown action", action);
    }

    @WorkerThread
    public void unregister() {
        this.zzaTV.zzjv();
        this.zzaTV.zzjk();
        if (isRegistered()) {
            zzAo().zzCK().zzfg("Unregistering connectivity change receiver");
            this.zzTa = false;
            this.zzTb = false;
            try {
                getContext().unregisterReceiver(this);
            } catch (IllegalArgumentException e) {
                zzAo().zzCE().zzj("Failed to unregister the network broadcast receiver", e);
            }
        }
    }

    @WorkerThread
    public void zzly() {
        this.zzaTV.zzjv();
        this.zzaTV.zzjk();
        if (!this.zzTa) {
            getContext().registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            this.zzTb = this.zzaTV.zzCW().zzlB();
            zzAo().zzCK().zzj("Registering connectivity change receiver. Network connected", Boolean.valueOf(this.zzTb));
            this.zzTa = true;
        }
    }
}
