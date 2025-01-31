package com.google.android.gms.internal;

import android.os.Binder;

public abstract class zzlz<T> {
    private static zza zzaiV;
    private static int zzaiW;
    private static String zzaiX;
    private static final Object zzqy;
    private T zzSC;
    protected final String zzvs;
    protected final T zzvt;

    private interface zza {
        Long getLong(String str, Long l);

        String getString(String str, String str2);

        Boolean zza(String str, Boolean bool);

        Float zzb(String str, Float f);

        Integer zzb(String str, Integer num);
    }

    /* renamed from: com.google.android.gms.internal.zzlz.1 */
    static class C05601 extends zzlz<Boolean> {
        C05601(String str, Boolean bool) {
            super(str, bool);
        }

        protected /* synthetic */ Object zzct(String str) {
            return zzcu(str);
        }

        protected Boolean zzcu(String str) {
            return zzlz.zzaiV.zza(this.zzvs, (Boolean) this.zzvt);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzlz.2 */
    static class C05612 extends zzlz<Long> {
        C05612(String str, Long l) {
            super(str, l);
        }

        protected /* synthetic */ Object zzct(String str) {
            return zzcv(str);
        }

        protected Long zzcv(String str) {
            return zzlz.zzaiV.getLong(this.zzvs, (Long) this.zzvt);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzlz.3 */
    static class C05623 extends zzlz<Integer> {
        C05623(String str, Integer num) {
            super(str, num);
        }

        protected /* synthetic */ Object zzct(String str) {
            return zzcw(str);
        }

        protected Integer zzcw(String str) {
            return zzlz.zzaiV.zzb(this.zzvs, (Integer) this.zzvt);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzlz.4 */
    static class C05634 extends zzlz<Float> {
        C05634(String str, Float f) {
            super(str, f);
        }

        protected /* synthetic */ Object zzct(String str) {
            return zzcx(str);
        }

        protected Float zzcx(String str) {
            return zzlz.zzaiV.zzb(this.zzvs, (Float) this.zzvt);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzlz.5 */
    static class C05645 extends zzlz<String> {
        C05645(String str, String str2) {
            super(str, str2);
        }

        protected /* synthetic */ Object zzct(String str) {
            return zzcy(str);
        }

        protected String zzcy(String str) {
            return zzlz.zzaiV.getString(this.zzvs, (String) this.zzvt);
        }
    }

    static {
        zzqy = new Object();
        zzaiV = null;
        zzaiW = 0;
        zzaiX = "com.google.android.providers.gsf.permission.READ_GSERVICES";
    }

    protected zzlz(String str, T t) {
        this.zzSC = null;
        this.zzvs = str;
        this.zzvt = t;
    }

    public static boolean isInitialized() {
        return zzaiV != null;
    }

    public static zzlz<Float> zza(String str, Float f) {
        return new C05634(str, f);
    }

    public static zzlz<Integer> zza(String str, Integer num) {
        return new C05623(str, num);
    }

    public static zzlz<Long> zza(String str, Long l) {
        return new C05612(str, l);
    }

    public static zzlz<Boolean> zzk(String str, boolean z) {
        return new C05601(str, Boolean.valueOf(z));
    }

    public static int zzpW() {
        return zzaiW;
    }

    public static zzlz<String> zzv(String str, String str2) {
        return new C05645(str, str2);
    }

    public final T get() {
        return this.zzSC != null ? this.zzSC : zzct(this.zzvs);
    }

    protected abstract T zzct(String str);

    public final T zzpX() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            T t = get();
            return t;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }
}
