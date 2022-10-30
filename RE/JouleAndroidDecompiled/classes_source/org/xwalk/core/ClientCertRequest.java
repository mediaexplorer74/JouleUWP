package org.xwalk.core;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

public interface ClientCertRequest {
    void cancel();

    String getHost();

    int getPort();

    void ignore();

    void proceed(PrivateKey privateKey, List<X509Certificate> list);
}
