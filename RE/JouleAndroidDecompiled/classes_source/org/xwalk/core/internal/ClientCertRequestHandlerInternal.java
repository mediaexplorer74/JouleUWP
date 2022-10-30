package org.xwalk.core.internal;

import android.util.Log;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;
import org.chromium.base.ThreadUtils;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.chromium.net.AndroidPrivateKey;

@XWalkAPI(createInternally = true, impl = ClientCertRequestInternal.class)
public class ClientCertRequestHandlerInternal implements ClientCertRequestInternal {
    private static final String TAG = "ClientCertRequestHandlerInternal";
    private XWalkContentsClientBridge mContentsClient;
    private String mHost;
    private int mId;
    private boolean mIsCalled;
    private int mPort;

    /* renamed from: org.xwalk.core.internal.ClientCertRequestHandlerInternal.1 */
    class C04401 implements Runnable {
        final /* synthetic */ List val$chain;
        final /* synthetic */ PrivateKey val$privateKey;

        C04401(List list, PrivateKey privateKey) {
            this.val$chain = list;
            this.val$privateKey = privateKey;
        }

        public void run() {
            X509Certificate[] chains = null;
            if (this.val$chain != null) {
                chains = (X509Certificate[]) this.val$chain.toArray(new X509Certificate[this.val$chain.size()]);
            }
            ClientCertRequestHandlerInternal.this.proceedOnUiThread(this.val$privateKey, chains);
        }
    }

    /* renamed from: org.xwalk.core.internal.ClientCertRequestHandlerInternal.2 */
    class C04412 implements Runnable {
        C04412() {
        }

        public void run() {
            ClientCertRequestHandlerInternal.this.ignoreOnUiThread();
        }
    }

    /* renamed from: org.xwalk.core.internal.ClientCertRequestHandlerInternal.3 */
    class C04423 implements Runnable {
        C04423() {
        }

        public void run() {
            ClientCertRequestHandlerInternal.this.cancelOnUiThread();
        }
    }

    ClientCertRequestHandlerInternal(XWalkContentsClientBridge contentsClient, int id, String host, int port) {
        this.mId = id;
        this.mHost = host;
        this.mPort = port;
        this.mContentsClient = contentsClient;
    }

    ClientCertRequestHandlerInternal() {
        this.mId = -1;
        this.mHost = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        this.mPort = -1;
        this.mContentsClient = null;
    }

    @XWalkAPI
    public void proceed(PrivateKey privateKey, List<X509Certificate> chain) {
        ThreadUtils.runOnUiThread(new C04401(chain, privateKey));
    }

    @XWalkAPI
    public void ignore() {
        ThreadUtils.runOnUiThread(new C04412());
    }

    @XWalkAPI
    public void cancel() {
        ThreadUtils.runOnUiThread(new C04423());
    }

    @XWalkAPI
    public String getHost() {
        return this.mHost;
    }

    @XWalkAPI
    public int getPort() {
        return this.mPort;
    }

    private void proceedOnUiThread(PrivateKey privateKey, X509Certificate[] chain) {
        checkIfCalled();
        AndroidPrivateKey key = this.mContentsClient.mLocalKeyStore.createKey(privateKey);
        if (key == null || chain == null || chain.length == 0) {
            Log.w(TAG, "Empty client certificate chain?");
            provideResponse(null, (byte[][]) null);
            return;
        }
        byte[][] encodedChain = new byte[chain.length][];
        int i = 0;
        while (i < chain.length) {
            try {
                encodedChain[i] = chain[i].getEncoded();
                i++;
            } catch (CertificateEncodingException e) {
                Log.w(TAG, "Could not retrieve encoded certificate chain: " + e);
                provideResponse(null, (byte[][]) null);
                return;
            }
        }
        this.mContentsClient.mLookupTable.allow(this.mHost, this.mPort, key, encodedChain);
        provideResponse(key, encodedChain);
    }

    private void ignoreOnUiThread() {
        checkIfCalled();
        provideResponse(null, (byte[][]) null);
    }

    private void cancelOnUiThread() {
        checkIfCalled();
        this.mContentsClient.mLookupTable.deny(this.mHost, this.mPort);
        provideResponse(null, (byte[][]) null);
    }

    private void checkIfCalled() {
        if (this.mIsCalled) {
            throw new IllegalStateException("The callback was already called.");
        }
        this.mIsCalled = true;
    }

    private void provideResponse(AndroidPrivateKey androidKey, byte[][] certChain) {
        this.mContentsClient.provideClientCertificateResponse(this.mId, certChain, androidKey);
    }
}
