package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzx;
import com.google.android.gms.internal.zzmq;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;
import org.chromium.blink_public.web.WebInputEventModifier;

public class zzq extends zzz {

    @WorkerThread
    interface zza {
        void zza(String str, int i, Throwable th, byte[] bArr);
    }

    @WorkerThread
    private static class zzb implements Runnable {
        private final int zzBc;
        private final String zzTJ;
        private final zza zzaWP;
        private final Throwable zzaWQ;
        private final byte[] zzaWR;

        private zzb(String str, zza com_google_android_gms_measurement_internal_zzq_zza, int i, Throwable th, byte[] bArr) {
            zzx.zzz(com_google_android_gms_measurement_internal_zzq_zza);
            this.zzaWP = com_google_android_gms_measurement_internal_zzq_zza;
            this.zzBc = i;
            this.zzaWQ = th;
            this.zzaWR = bArr;
            this.zzTJ = str;
        }

        public void run() {
            this.zzaWP.zza(this.zzTJ, this.zzBc, this.zzaWQ, this.zzaWR);
        }
    }

    @WorkerThread
    private class zzc implements Runnable {
        private final String zzTJ;
        private final byte[] zzaWS;
        private final zza zzaWT;
        private final Map<String, String> zzaWU;
        final /* synthetic */ zzq zzaWV;
        private final URL zzzq;

        public zzc(zzq com_google_android_gms_measurement_internal_zzq, String str, URL url, byte[] bArr, Map<String, String> map, zza com_google_android_gms_measurement_internal_zzq_zza) {
            this.zzaWV = com_google_android_gms_measurement_internal_zzq;
            zzx.zzcM(str);
            zzx.zzz(url);
            zzx.zzz(com_google_android_gms_measurement_internal_zzq_zza);
            this.zzzq = url;
            this.zzaWS = bArr;
            this.zzaWT = com_google_android_gms_measurement_internal_zzq_zza;
            this.zzTJ = str;
            this.zzaWU = map;
        }

        public void run() {
            HttpURLConnection zzc;
            Throwable e;
            int i;
            HttpURLConnection httpURLConnection;
            Throwable th;
            this.zzaWV.zzCd();
            int i2 = 0;
            OutputStream outputStream;
            try {
                zzc = this.zzaWV.zzc(this.zzzq);
                try {
                    if (this.zzaWU != null) {
                        for (Entry entry : this.zzaWU.entrySet()) {
                            zzc.addRequestProperty((String) entry.getKey(), (String) entry.getValue());
                        }
                    }
                    if (this.zzaWS != null) {
                        byte[] zzg = this.zzaWV.zzCk().zzg(this.zzaWS);
                        this.zzaWV.zzAo().zzCK().zzj("Uploading data. size", Integer.valueOf(zzg.length));
                        zzc.setDoOutput(true);
                        zzc.addRequestProperty("Content-Encoding", "gzip");
                        zzc.setFixedLengthStreamingMode(zzg.length);
                        zzc.connect();
                        outputStream = zzc.getOutputStream();
                        try {
                            outputStream.write(zzg);
                            outputStream.close();
                            outputStream = null;
                        } catch (IOException e2) {
                            e = e2;
                            i = 0;
                            httpURLConnection = zzc;
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e3) {
                                    this.zzaWV.zzAo().zzCE().zzj("Error closing HTTP compressed POST connection output stream", e3);
                                }
                            }
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                            }
                            this.zzaWV.zzCn().zzg(new zzb(this.zzaWT, i, e, null, null));
                        } catch (Throwable th2) {
                            th = th2;
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e32) {
                                    this.zzaWV.zzAo().zzCE().zzj("Error closing HTTP compressed POST connection output stream", e32);
                                }
                            }
                            if (zzc != null) {
                                zzc.disconnect();
                            }
                            this.zzaWV.zzCn().zzg(new zzb(this.zzaWT, i2, null, null, null));
                            throw th;
                        }
                    }
                    outputStream = null;
                    i2 = zzc.getResponseCode();
                    byte[] zza = this.zzaWV.zzc(zzc);
                    if (null != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e322) {
                            this.zzaWV.zzAo().zzCE().zzj("Error closing HTTP compressed POST connection output stream", e322);
                        }
                    }
                    if (zzc != null) {
                        zzc.disconnect();
                    }
                    this.zzaWV.zzCn().zzg(new zzb(this.zzaWT, i2, null, zza, null));
                } catch (IOException e4) {
                    e = e4;
                    i = i2;
                    outputStream = null;
                    httpURLConnection = zzc;
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    this.zzaWV.zzCn().zzg(new zzb(this.zzaWT, i, e, null, null));
                } catch (Throwable th3) {
                    th = th3;
                    outputStream = null;
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (zzc != null) {
                        zzc.disconnect();
                    }
                    this.zzaWV.zzCn().zzg(new zzb(this.zzaWT, i2, null, null, null));
                    throw th;
                }
            } catch (IOException e5) {
                e = e5;
                i = 0;
                outputStream = null;
                httpURLConnection = null;
                if (outputStream != null) {
                    outputStream.close();
                }
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                this.zzaWV.zzCn().zzg(new zzb(this.zzaWT, i, e, null, null));
            } catch (Throwable th32) {
                th = th32;
                zzc = null;
                outputStream = null;
                if (outputStream != null) {
                    outputStream.close();
                }
                if (zzc != null) {
                    zzc.disconnect();
                }
                this.zzaWV.zzCn().zzg(new zzb(this.zzaWT, i2, null, null, null));
                throw th;
            }
        }
    }

    public zzq(zzw com_google_android_gms_measurement_internal_zzw) {
        super(com_google_android_gms_measurement_internal_zzw);
    }

    @WorkerThread
    private byte[] zzc(HttpURLConnection httpURLConnection) throws IOException {
        InputStream inputStream = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            inputStream = httpURLConnection.getInputStream();
            byte[] bArr = new byte[WebInputEventModifier.NumLockOn];
            while (true) {
                int read = inputStream.read(bArr);
                if (read <= 0) {
                    break;
                }
                byteArrayOutputStream.write(bArr, 0, read);
            }
            byte[] toByteArray = byteArrayOutputStream.toByteArray();
            return toByteArray;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ zzp zzAo() {
        return super.zzAo();
    }

    public /* bridge */ /* synthetic */ void zzCd() {
        super.zzCd();
    }

    public /* bridge */ /* synthetic */ zzc zzCe() {
        return super.zzCe();
    }

    public /* bridge */ /* synthetic */ zzab zzCf() {
        return super.zzCf();
    }

    public /* bridge */ /* synthetic */ zzn zzCg() {
        return super.zzCg();
    }

    public /* bridge */ /* synthetic */ zzg zzCh() {
        return super.zzCh();
    }

    public /* bridge */ /* synthetic */ zzac zzCi() {
        return super.zzCi();
    }

    public /* bridge */ /* synthetic */ zze zzCj() {
        return super.zzCj();
    }

    public /* bridge */ /* synthetic */ zzaj zzCk() {
        return super.zzCk();
    }

    public /* bridge */ /* synthetic */ zzu zzCl() {
        return super.zzCl();
    }

    public /* bridge */ /* synthetic */ zzad zzCm() {
        return super.zzCm();
    }

    public /* bridge */ /* synthetic */ zzv zzCn() {
        return super.zzCn();
    }

    public /* bridge */ /* synthetic */ zzt zzCo() {
        return super.zzCo();
    }

    public /* bridge */ /* synthetic */ zzd zzCp() {
        return super.zzCp();
    }

    @WorkerThread
    public void zza(String str, URL url, Map<String, String> map, zza com_google_android_gms_measurement_internal_zzq_zza) {
        zzjk();
        zzjv();
        zzx.zzz(url);
        zzx.zzz(com_google_android_gms_measurement_internal_zzq_zza);
        zzCn().zzh(new zzc(this, str, url, null, map, com_google_android_gms_measurement_internal_zzq_zza));
    }

    @WorkerThread
    public void zza(String str, URL url, byte[] bArr, Map<String, String> map, zza com_google_android_gms_measurement_internal_zzq_zza) {
        zzjk();
        zzjv();
        zzx.zzz(url);
        zzx.zzz(bArr);
        zzx.zzz(com_google_android_gms_measurement_internal_zzq_zza);
        zzCn().zzh(new zzc(this, str, url, bArr, map, com_google_android_gms_measurement_internal_zzq_zza));
    }

    @WorkerThread
    protected HttpURLConnection zzc(URL url) throws IOException {
        URLConnection openConnection = url.openConnection();
        if (openConnection instanceof HttpURLConnection) {
            HttpURLConnection httpURLConnection = (HttpURLConnection) openConnection;
            httpURLConnection.setDefaultUseCaches(false);
            httpURLConnection.setConnectTimeout((int) zzCp().zzBO());
            httpURLConnection.setReadTimeout((int) zzCp().zzBP());
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.setDoInput(true);
            return httpURLConnection;
        }
        throw new IOException("Failed to obtain HTTP connection");
    }

    protected void zziJ() {
    }

    public /* bridge */ /* synthetic */ void zzjj() {
        super.zzjj();
    }

    public /* bridge */ /* synthetic */ void zzjk() {
        super.zzjk();
    }

    public /* bridge */ /* synthetic */ zzmq zzjl() {
        return super.zzjl();
    }

    public boolean zzlB() {
        NetworkInfo activeNetworkInfo;
        zzjv();
        try {
            activeNetworkInfo = ((ConnectivityManager) getContext().getSystemService("connectivity")).getActiveNetworkInfo();
        } catch (SecurityException e) {
            activeNetworkInfo = null;
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
