package org.chromium.net;

import android.util.Log;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import org.chromium.ui.base.PageTransition;

public class DefaultAndroidKeyStore implements AndroidKeyStore {
    private static final String TAG = "AndroidKeyStoreInProcessImpl";

    private static class DefaultAndroidPrivateKey implements AndroidPrivateKey {
        final PrivateKey mKey;
        final DefaultAndroidKeyStore mStore;

        DefaultAndroidPrivateKey(PrivateKey key, DefaultAndroidKeyStore store) {
            this.mKey = key;
            this.mStore = store;
        }

        PrivateKey getJavaKey() {
            return this.mKey;
        }

        public AndroidKeyStore getKeyStore() {
            return this.mStore;
        }
    }

    public AndroidPrivateKey createKey(PrivateKey javaKey) {
        return new DefaultAndroidPrivateKey(javaKey, this);
    }

    public byte[] getRSAKeyModulus(AndroidPrivateKey key) {
        PrivateKey javaKey = ((DefaultAndroidPrivateKey) key).getJavaKey();
        if (javaKey instanceof RSAKey) {
            return ((RSAKey) javaKey).getModulus().toByteArray();
        }
        Log.w(TAG, "Not a RSAKey instance!");
        return null;
    }

    public byte[] getECKeyOrder(AndroidPrivateKey key) {
        PrivateKey javaKey = ((DefaultAndroidPrivateKey) key).getJavaKey();
        if (javaKey instanceof ECKey) {
            return ((ECKey) javaKey).getParams().getOrder().toByteArray();
        }
        Log.w(TAG, "Not an ECKey instance!");
        return null;
    }

    public byte[] rawSignDigestWithPrivateKey(AndroidPrivateKey key, byte[] message) {
        byte[] bArr = null;
        PrivateKey javaKey = ((DefaultAndroidPrivateKey) key).getJavaKey();
        Signature signature = null;
        try {
            String keyAlgorithm = javaKey.getAlgorithm();
            if ("RSA".equalsIgnoreCase(keyAlgorithm)) {
                signature = Signature.getInstance("NONEwithRSA");
            } else if ("EC".equalsIgnoreCase(keyAlgorithm)) {
                signature = Signature.getInstance("NONEwithECDSA");
            }
        } catch (NoSuchAlgorithmException e) {
        }
        if (signature == null) {
            Log.e(TAG, "Unsupported private key algorithm: " + javaKey.getAlgorithm());
        } else {
            try {
                signature.initSign(javaKey);
                signature.update(message);
                bArr = signature.sign();
            } catch (Exception e2) {
                Log.e(TAG, "Exception while signing message with " + javaKey.getAlgorithm() + " private key: " + e2);
            }
        }
        return bArr;
    }

    public int getPrivateKeyType(AndroidPrivateKey key) {
        String keyAlgorithm = ((DefaultAndroidPrivateKey) key).getJavaKey().getAlgorithm();
        if ("RSA".equalsIgnoreCase(keyAlgorithm)) {
            return 0;
        }
        if ("EC".equalsIgnoreCase(keyAlgorithm)) {
            return 2;
        }
        return PageTransition.CORE_MASK;
    }

    private Object getOpenSSLKeyForPrivateKey(AndroidPrivateKey key) {
        PrivateKey javaKey = ((DefaultAndroidPrivateKey) key).getJavaKey();
        if (javaKey == null) {
            Log.e(TAG, "key == null");
            return null;
        } else if (javaKey instanceof RSAPrivateKey) {
            try {
                Class<?> superClass = Class.forName("org.apache.harmony.xnet.provider.jsse.OpenSSLRSAPrivateKey");
                if (superClass.isInstance(javaKey)) {
                    Method getKey;
                    try {
                        getKey = superClass.getDeclaredMethod("getOpenSSLKey", new Class[0]);
                        getKey.setAccessible(true);
                        Object opensslKey = getKey.invoke(javaKey, new Object[0]);
                        getKey.setAccessible(false);
                        if (opensslKey != null) {
                            return opensslKey;
                        }
                        Log.e(TAG, "getOpenSSLKey() returned null");
                        return null;
                    } catch (Exception e) {
                        Log.e(TAG, "Exception while trying to retrieve system EVP_PKEY handle: " + e);
                        return null;
                    } catch (Throwable th) {
                        getKey.setAccessible(false);
                    }
                }
                Log.e(TAG, "Private key is not an OpenSSLRSAPrivateKey instance, its class name is:" + javaKey.getClass().getCanonicalName());
                return null;
            } catch (Exception e2) {
                Log.e(TAG, "Cannot find system OpenSSLRSAPrivateKey class: " + e2);
                return null;
            }
        } else {
            Log.e(TAG, "does not implement RSAPrivateKey");
            return null;
        }
    }

    public long getOpenSSLHandleForPrivateKey(AndroidPrivateKey key) {
        Object opensslKey = getOpenSSLKeyForPrivateKey(key);
        if (opensslKey == null) {
            return 0;
        }
        try {
            Method getPkeyContext = opensslKey.getClass().getDeclaredMethod("getPkeyContext", new Class[0]);
            try {
                getPkeyContext.setAccessible(true);
                long evp_pkey = ((Number) getPkeyContext.invoke(opensslKey, new Object[0])).longValue();
                getPkeyContext.setAccessible(false);
                if (evp_pkey != 0) {
                    return evp_pkey;
                }
                Log.e(TAG, "getPkeyContext() returned null");
                return evp_pkey;
            } catch (Exception e) {
                Log.e(TAG, "Exception while trying to retrieve system EVP_PKEY handle: " + e);
                return 0;
            } catch (Throwable th) {
                getPkeyContext.setAccessible(false);
            }
        } catch (Exception e2) {
            Log.e(TAG, "No getPkeyContext() method on OpenSSLKey member:" + e2);
            return 0;
        }
    }

    public Object getOpenSSLEngineForPrivateKey(AndroidPrivateKey key) {
        try {
            Class<?> engineClass = Class.forName("org.apache.harmony.xnet.provider.jsse.OpenSSLEngine");
            Object opensslKey = getOpenSSLKeyForPrivateKey(key);
            if (opensslKey == null) {
                return null;
            }
            try {
                Method getEngine = opensslKey.getClass().getDeclaredMethod("getEngine", new Class[0]);
                try {
                    getEngine.setAccessible(true);
                    Object engine = getEngine.invoke(opensslKey, new Object[0]);
                    getEngine.setAccessible(false);
                    if (engine == null) {
                        Log.e(TAG, "getEngine() returned null");
                    }
                    if (engineClass.isInstance(engine)) {
                        return engine;
                    }
                    Log.e(TAG, "Engine is not an OpenSSLEngine instance, its class name is:" + engine.getClass().getCanonicalName());
                    return null;
                } catch (Exception e) {
                    Log.e(TAG, "Exception while trying to retrieve OpenSSLEngine object: " + e);
                    return null;
                } catch (Throwable th) {
                    getEngine.setAccessible(false);
                }
            } catch (Exception e2) {
                Log.e(TAG, "No getEngine() method on OpenSSLKey member:" + e2);
                return null;
            }
        } catch (Exception e22) {
            Log.e(TAG, "Cannot find system OpenSSLEngine class: " + e22);
            return null;
        }
    }

    public void releaseKey(AndroidPrivateKey key) {
    }
}
