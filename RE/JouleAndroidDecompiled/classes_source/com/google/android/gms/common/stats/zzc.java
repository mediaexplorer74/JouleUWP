package com.google.android.gms.common.stats;

import com.google.android.gms.internal.zzlz;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;

public final class zzc {
    public static zzlz<Integer> zzanx;
    public static zzlz<Integer> zzany;

    public static final class zza {
        public static zzlz<String> zzanA;
        public static zzlz<String> zzanB;
        public static zzlz<String> zzanC;
        public static zzlz<String> zzanD;
        public static zzlz<Long> zzanE;
        public static zzlz<Integer> zzanz;

        static {
            zzanz = zzlz.zza("gms:common:stats:connections:level", Integer.valueOf(zzd.LOG_LEVEL_OFF));
            zzanA = zzlz.zzv("gms:common:stats:connections:ignored_calling_processes", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
            zzanB = zzlz.zzv("gms:common:stats:connections:ignored_calling_services", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
            zzanC = zzlz.zzv("gms:common:stats:connections:ignored_target_processes", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
            zzanD = zzlz.zzv("gms:common:stats:connections:ignored_target_services", "com.google.android.gms.auth.GetToken");
            zzanE = zzlz.zza("gms:common:stats:connections:time_out_duration", Long.valueOf(600000));
        }
    }

    public static final class zzb {
        public static zzlz<Long> zzanE;
        public static zzlz<Integer> zzanz;

        static {
            zzanz = zzlz.zza("gms:common:stats:wakeLocks:level", Integer.valueOf(zzd.LOG_LEVEL_OFF));
            zzanE = zzlz.zza("gms:common:stats:wakelocks:time_out_duration", Long.valueOf(600000));
        }
    }

    static {
        zzanx = zzlz.zza("gms:common:stats:max_num_of_events", Integer.valueOf(100));
        zzany = zzlz.zza("gms:common:stats:max_chunk_size", Integer.valueOf(100));
    }
}
