package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.gms.internal.zzpl;
import java.util.concurrent.Callable;

public class zzb {
    private static SharedPreferences zzaBZ;

    /* renamed from: com.google.android.gms.flags.impl.zzb.1 */
    static class C01591 implements Callable<SharedPreferences> {
        final /* synthetic */ Context zzxh;

        C01591(Context context) {
            this.zzxh = context;
        }

        public /* synthetic */ Object call() throws Exception {
            return zzvw();
        }

        public SharedPreferences zzvw() {
            return this.zzxh.getSharedPreferences("google_sdk_flags", 1);
        }
    }

    static {
        zzaBZ = null;
    }

    public static SharedPreferences zzw(Context context) {
        SharedPreferences sharedPreferences;
        synchronized (SharedPreferences.class) {
            if (zzaBZ == null) {
                zzaBZ = (SharedPreferences) zzpl.zzb(new C01591(context));
            }
            sharedPreferences = zzaBZ;
        }
        return sharedPreferences;
    }
}
